package com.example.rces.service.impl;

import com.example.rces.dto.CreateRequestDto;
import com.example.rces.dto.RequestContext;
import com.example.rces.dto.RequestDto;
import com.example.rces.dto.RequestParamsDto;
import com.example.rces.mapper.RequestMapper;
import com.example.rces.models.*;
import com.example.rces.models.enums.GeneralReason;
import com.example.rces.models.enums.Item;
import com.example.rces.models.enums.Status;
import com.example.rces.repository.RequestsRepository;
import com.example.rces.service.*;
import com.example.rces.utils.telegram.MessageType;
import com.example.rces.utils.telegram.event.TelegramRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.ws.rs.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.rces.service.impl.CustomUserDetailsServiceImpl.currentUser;
import static com.example.rces.utils.FilesUtil.handleImageCollection;
import static com.example.rces.utils.FilesUtil.isJson;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class RequestServiceImpl implements RequestsService {

    private final RequestsRepository repository;
    private final ObjectMapper objectMapper;
    private final TelegramService telegramService;
    private final CustomerOrderService customerOrderService;
    private final ImageService imageService;
    private final EmployeeService employeeService;
    private final InconsistenciesService inconsistenciesService;
    private final RequestMapper requestMapper;
    private final SubDivisionService subDivisionService;
    private final ReasonService reasonService;
    private final JdbcTemplate jdbcTemplate;
    private final NotificationService notificationService;

    @Autowired
    public RequestServiceImpl(RequestsRepository repository, ObjectMapper objectMapper,
                              TelegramService telegramService, CustomerOrderService customerOrderService,
                              ImageService imageService, EmployeeService employeeService,
                              InconsistenciesService inconsistenciesService, RequestMapper requestMapper,
                              SubDivisionService subDivisionService, ReasonService reasonService,
                              JdbcTemplate jdbcTemplate, NotificationService notificationService) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.telegramService = telegramService;
        this.customerOrderService = customerOrderService;
        this.imageService = imageService;
        this.employeeService = employeeService;
        this.inconsistenciesService = inconsistenciesService;
        this.requestMapper = requestMapper;
        this.subDivisionService = subDivisionService;
        this.reasonService = reasonService;
        this.jdbcTemplate = jdbcTemplate;
        this.notificationService = notificationService;
    }

    @Override
    public RequestDto createRequest(Employee createdEmployee, CreateRequestDto createRequestDto, MultipartFile[] additionalFiles) throws JsonProcessingException {

        Employee employee = null;

        if (createRequestDto.getEmployeeJson() != null) {
            employee = objectMapper.readValue(createRequestDto.getEmployeeJson(), Employee.class);
            employee = employeeService.loadUserByUsername(employee.getUsername());
        }

        CustomerOrder customerOrder = customerOrderService.createOrGetCustomerOrder(createdEmployee,
                createRequestDto.getCustomerOrderString(), createRequestDto.getCustomerOrderJson());
        GeneralReason reason = null;
        String reasonText = null;
        if (Objects.equals(createRequestDto.getType(), "otk")) {
            reasonText = String.valueOf(createRequestDto.getReasonsJson());
        } else {
            if (!createRequestDto.getReasonsJson().isBlank()) {
                reason = objectMapper.readValue(createRequestDto.getReasonsJson(), GeneralReason.class);
            }
        }
        Item item = null;
        if (createRequestDto.getItemNameJson() != null && !createRequestDto.getItemNameJson().isBlank()) {
            item = objectMapper.readValue(createRequestDto.getItemNameJson(), Item.class);
        }
        SubDivision mlmNode = null;
        if (!createRequestDto.getMlmNodeJson().isBlank()) {
            mlmNode = subDivisionService.getByName(createRequestDto.getMlmNodeJson());
        }


        createRequestDto.setRequestNumber(repository.findNextRequestNumber());
        Requests requests = repository.save(requestMapper.createFullRequest(
                createRequestDto,
                item,
                reason,
                mlmNode,
                employee,
                customerOrder,
                createdEmployee
        ));

        if (additionalFiles != null && additionalFiles.length > 0) {
            imageService.createImages(additionalFiles, requests, true);
        }

        if (reasonText != null) {
            reasonService.createOrUpdateReason(reasonText, createRequestDto.getType());
        }

        telegramService.sendMessageForRequest(new TelegramRequestEvent(this, requests, requests.getEmployee(), MessageType.CREATE));
        notificationService.sendPrivateNotification(
                requests.getEmployee().getUsername(),
                "Заявка с номером - " + requests.getRequestNumber() + " была успешно создана для вас!",
                "http://localhost:2520/view/" + requests.getRequestNumber(),
                requests.getStatus().getName()
        );

        return requestMapper.toDTO((requests));
    }

    @Override
    public void save(Requests requests) {
        repository.save(requests);
    }

    @Override
    public void save(RequestParamsDto requestParamsDto, Set<Inconsistency> inconsistencyData) {
        Requests requests = repository.findById(requestParamsDto.getRequestId())
                .orElseThrow(() -> new ApplicationContextException("Не существует заявки с id: " + requestParamsDto.getRequestId()));
        Employee updatedEmployee = currentUser().orElseThrow();

        RequestContext context = new RequestContext(requests, requestParamsDto.getDescription(),
                requestParamsDto.getStatus(), requestParamsDto.getQtyCompleted(),
                inconsistencyData, updatedEmployee, requestParamsDto.getDescriptionsCompleted());

        if (requestParamsDto.getStatus() == null) {
            handleStatusNull(context);
        } else {
            handleStatusUpdate(context);
        }

        requests.setDateWork(LocalDateTime.now());
        requests.setVersion(requests.getVersion() + 1);
        repository.save(requests);
    }

    @Override
    public void update(UUID id, Boolean sendMessage, Map<String, Object> updatedFields) {
        Requests request = repository.findById(id).orElseThrow(() -> new ApplicationContextException("Не существует заявки с id: " + id));
        Requests oldRequest;
        try {
            oldRequest = (Requests) request.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        Employee updaterEmployee = currentUser().orElseThrow();

        List<Field> fields = List.of(request.getClass().getDeclaredFields());
        updatedFields.forEach((key, value) -> {
            if (!"id".equals(key)) {
                try {
                    String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
                    Method method = request.getClass().getMethod(methodName, fields.stream()
                            .filter(f -> f.getName().equals(key))
                            .findFirst()
                            .orElseThrow(() -> new NoSuchFieldException("Поле " + key + " не найдено"))
                            .getType());

                    if (key.equals("customerOrder") && !isJson(value)) {
                        CustomerOrder customerOrder = customerOrderService.createOrGetCustomerOrder(updaterEmployee, (String) value, null);
                        method.invoke(request, customerOrder);
                        return;
                    } else if (method.getParameterTypes()[0].isEnum() && value != null) {
                        Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) method.getParameterTypes()[0];
                        value = enumClass.getMethod("fromField", Object.class).invoke(null, value.toString());
                    } else if (method.getParameterTypes()[0].isAnnotationPresent(Entity.class) && value != null) {
                        value = objectMapper.readValue((String) value, method.getParameterTypes()[0]);
                    } else if (key.equals("images") && value != null) {
                        if (!((ArrayList<?>) value).isEmpty()) {
                            List<UUID> imageIds = ((ArrayList<?>) value).stream()
                                    .filter(LinkedHashMap.class::isInstance)
                                    .map(img -> UUID.fromString((String) ((LinkedHashMap<?, ?>) img).get("id")))
                                    .toList();
                            List<Images> images = new ArrayList<>(imageService.findAllByIds(imageIds));
                            ((ArrayList<?>) value).stream()
                                    .filter(String.class::isInstance)
                                    .map(String.class::cast)
                                    .forEach(imgStr -> {
                                        Images newImage = new Images(imgStr, request);
                                        images.add(newImage);
                                        imageService.save(newImage);
                                    });
                            handleImageCollection(request, images);
                            return;
                        }
                        handleImageCollection(request, (List<?>) value);
                        return;
                    } else if (key.equals("inconsistencies")) {
                        value = Inconsistency.fromField(value, new HashSet<>(inconsistenciesService.findAll()));
                    }

                    method.invoke(request, value);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Метод " + key + " не найден", e);
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка при обновлении поля " + key, e);
                }
            }
        });
        request.setDateWork(LocalDateTime.now());
        request.setVersion(request.getVersion() + 1);
        repository.save(request);
        if (!request.getEmployee().equals(oldRequest.getEmployee())) {
            if (!updaterEmployee.getId().equals(request.getEmployee().getId())) {
                telegramService.sendMessageForRequest(new TelegramRequestEvent(this, request, request.getEmployee(), MessageType.REDIRECT));
            }
        }
    }

    @Override
    public void createComment(UUID id, String comment) {
        Requests requests = repository.findById(id).orElseThrow(() -> new ApplicationContextException("Не существует заявки с id: " + id));
        requests.setCommentAgreed(comment);
        repository.save(requests);
    }

    @Override
    public Requests findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ApplicationContextException("Не существует заявки с id: " + id));
    }

    @Override
    public Requests findByRequestNumber(Integer requestNumber) {
        return repository.findByRequestNumber(requestNumber);
    }

    @Override
    public List<Requests> findAllByTypeRequest(Requests.Type type) {
        return repository.findAllByTypeRequest(type);
    }

    @Override
    public List<Requests> findAll() {
        return repository.findAll();
    }

    @Override
    public String getTypeRequest(UUID id) {
        Requests requests = repository.findById(id).orElseThrow(() -> new ApplicationContextException("Не существует заявки с id: " + id));
        return requests.getTypeRequest().name();
    }

    @Override
    public void updateCreateBy(UUID requestId, String user) {
        if (requestId != null && user != null) {
            Employee employee = employeeService.loadUserByUsername(user);
            Requests requests = findById(requestId);
            updateRequestCreatedBy(requestId, employee.getId());
            notificationService.sendPrivateNotification(
                    employee.getUsername(),
                    "Вам переадресовали забракованную заявку с номером " + requests.getRequestNumber() + " для исправления.",
                    "http://192.168.30.80:2005/view/" + requests.getRequestNumber(),
                    Status.Closed.getName()
            );
        }
    }

    private void updateRequestCreatedBy(UUID requestId, Long userId) {
        try {
            String sql = "UPDATE requests SET created_by = ?, update_at = NOW() " +
                    "WHERE id = UNHEX(REPLACE(?, '-', ''))";

            String uuidWithoutDashes = requestId.toString().replace("-", "");

            jdbcTemplate.update(sql, userId, uuidWithoutDashes);

        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Обрабатывает заявку при первоначальном назначении ответственного сотрудника.
     * Выполняет проверки и устанавливает соответствующий статус в зависимости от условий:
     * <ul>
     *   <li><b>Назначение на нового сотрудника</b> - проверяет, что заявка не назначена на другого сотрудника,
     *        затем устанавливает статус {@link Status#InWork} и отправляет уведомление о взятии в работу</li>
     *   <li><b>Повторное отклонение</b> - если заявка имеет несоответствия и повторно назначается на того же сотрудника,
     *        устанавливает статус {@link Status#Rejected}, увеличивает счетчик отклонений и отправляет уведомление об отмене</li>
     * </ul>
     *
     * @param context контекст запроса, содержащий заявку и данные обновления
     * @throws ForbiddenException если:
     *         <ul>
     *           <li>заявка уже назначена на другого сотрудника</li>
     *           <li>не указан ответственный сотрудник</li>
     *         </ul>
     * @throws NullPointerException если context или обязательные поля context равны null
     */
    private void handleStatusNull(RequestContext context) {
        Requests requests = context.getRequest();
        Employee updateEmployee = context.getEmployee();
        if (requests.getEmployee() != null) {
            if (requests.getEmployee().equals(updateEmployee)) {
                if (requests.getStatus().equals(Status.New)) {
                    requests.setStatus(Status.InWork);
                    telegramService.sendMessageForRequest(new TelegramRequestEvent(this, requests, requests.getCreatedBy(), MessageType.WORK));
                    notificationService.sendPrivateNotification(
                            requests.getCreatedBy().getUsername(),
                            "Ваша заявка с номером - " + requests.getRequestNumber() + " успешно принята в работу!",
                            "http://192.168.30.80:2005/view/" + requests.getRequestNumber(),
                            requests.getStatus().getName()
                    );
//                } else if (!requests.getInconsistencies().isEmpty() && (context.getNoticeOgc() || context.getNoticeOgt())) {
//                    requests.setStatus(Status.UnderRework);
//                    requests.setDescription(context.getDescription());
//                    requests.setQtyRejected(requests.getQtyRejected() + 1);
//                    System.out.println("Отправляем сообщение технологам или конструкторам");
//                    telegramService.sendMessageForRequest(new TelegramRequestEvent(this, requests, requests.getCreatedBy(), MessageType.CANCEL));
                } else if (!requests.getInconsistencies().isEmpty()) {
                    requests.setStatus(Status.Rejected);
                    requests.setDescription(context.getDescription());
                    requests.setQtyRejected(requests.getQtyRejected() + 1);
                    telegramService.sendMessageForRequest(new TelegramRequestEvent(this, requests, requests.getCreatedBy(), MessageType.CANCEL));
                    notificationService.sendPrivateNotification(
                            requests.getCreatedBy().getUsername(),
                            String.format("Заявка %d забракована по причине: (%s)", requests.getRequestNumber(), requests.getInconsistencies()
                                    .stream()
                                    .map(Inconsistency::getName)
                                    .collect(Collectors.joining(", "))),
                            "http://192.168.30.80:2005/view/" + requests.getRequestNumber(),
                            requests.getStatus().getName()
                    );
                }
            } else {
                throw new ForbiddenException("Эта заявка уже назначена на другого сотрудника. Вы можете изменить ответственного, выбрав нового исполнителя в поле «Ответственный».");
            }
        } else {
            throw new ForbiddenException("Для начала работы с заявкой требуется назначить ответственного!");
        }
    }

    /**
     * Обрабатывает изменение статуса заявки.
     * В зависимости от целевого статуса выполняет дополнительные действия:
     * <ul>
     *   <li><b>closed</b> - делегирует обработку методу {@link #handleClosedStatus(RequestContext)}</li>
     *   <li><b>update</b> - сбрасывает статус на {@link Status#New}, очищает список несоответствий
     *        и отправляет уведомление об обновлении через Telegram</li>
     *   <li><b>refresh</b> - сбрасывает статус на {@link Status#New} без дополнительных действий</li>
     *   <li><b>completed</b> - устанавливает статус {@link Status#Closed}, фиксирует дату закрытия,
     *        обновляет описание и отправляет уведомление о закрытии инициатору заявки</li>
     * </ul>
     *
     * @param context контекст запроса, содержащий обрабатываемую заявку
     * @throws IllegalArgumentException если передан неизвестный статус
     * @throws NullPointerException если context или requests в context равны null
     */

    private void handleStatusUpdate(RequestContext context) {
        Requests requests = context.getRequest();
        String description = context.getDescription();
        String status = context.getStatus();

        switch (status) {
            case "closed":
                handleClosedStatus(context);
                break;
            case "update":
                requests.setStatus(Status.New);
                requests.getInconsistencies().clear();
                telegramService.sendMessageForRequest(new TelegramRequestEvent(this, requests, requests.getEmployee(), MessageType.UPDATE));
                notificationService.sendPrivateNotification(
                        requests.getEmployee().getUsername(),
                        "Заявка с номером " + requests.getRequestNumber() + " была отправлена на повторное предъявление!",
                        "http://192.168.30.80:2005/view/" + requests.getRequestNumber(),
                        requests.getStatus().getName()
                );
                break;
            case "refresh":
                requests.setStatus(Status.New);
                break;
            case "completed":
                requests.setStatus(Status.Closed);
                requests.setCloseDate(LocalDateTime.now());
                requests.setDescription(description);
                telegramService.sendMessageForRequest(new TelegramRequestEvent(this, requests, requests.getCreatedBy(), MessageType.CLOSE));
                break;
            case "underRework":
                requests.setStatus(Status.UnderRework);
                System.out.println("Отправляем сообщения в телеграмм");
                break;
            default:
                throw new IllegalArgumentException("Неизвестный статус: " + status);
        }
    }

    private void handleClosedStatus(RequestContext context) {
        Requests requests = context.getRequest();
        String description = context.getDescription();
        Integer qty = context.getQty();
        Set<Inconsistency> inconsistencyData = context.getInconsistencies();
        Employee updaterEmployee = context.getEmployee();

        if (Objects.equals(requests.getQty(), qty)) {
            requests.setStatus(Status.Closed);
            requests.setCloseDate(LocalDateTime.now());
            requests.setDescription(context.getDescriptionsCompleted());
            requests.setClosedEmployee(updaterEmployee);
            Message message = telegramService.sendMessageForRequest(new TelegramRequestEvent(this, requests, requests.getCreatedBy(), MessageType.CLOSE));
            notificationService.sendPrivateNotification(
                    requests.getCreatedBy().getUsername(),
                    "Ваша заявка с № " + requests.getRequestNumber() + " была выполнена!",
                    "http://192.168.30.80:2005/view/" + requests.getRequestNumber(),
                    requests.getStatus().getName()
            );
            requests.setChatId(message != null ? message.getChatId() : -1);
            requests.setMessageId(message != null ? message.getMessageId() : -1);
        } else if (qty == 0) {
            requests.setStatus(Status.Rejected);
            requests.setDescription(description);
            requests.setInconsistencies(inconsistencyData);
            requests.setQtyRejected(requests.getQtyRejected() + 1);
            telegramService.sendMessageForRequest(new TelegramRequestEvent(this, requests, requests.getCreatedBy(), MessageType.CANCEL));
            notificationService.sendPrivateNotification(
                    requests.getCreatedBy().getUsername(),
                    "Ваша заявка с № " + requests.getRequestNumber() + " была переведена в статус - 'Забракована'!",
                    "http://192.168.30.80:2005/view/" + requests.getRequestNumber(),
                    requests.getStatus().getName()
            );
        } else if (requests.getQty() > qty) {
            handlePartialRejection(context);
        }
    }

    private void handlePartialRejection(RequestContext context) {
        Requests requests = context.getRequest();
        Integer qty = context.getQty();
        String description = context.getDescription();
        Set<Inconsistency> inconsistencyData = context.getInconsistencies();

        Requests rejected = createChildRejectedRequest(requests, qty, description, inconsistencyData);

        telegramService.sendMessageForRequest(new TelegramRequestEvent(this, rejected, rejected.getCreatedBy(), MessageType.REJECTED));
        notificationService.sendPrivateNotification(
                rejected.getCreatedBy().getUsername(), """
                        Ваша заявка с № %d была частично забракована, \
                        забракованные детали перенесены в новую заявку с № %d! \
                        Несоответствия по которым частично отклонена заявка: %s\
                        """.formatted(
                        requests.getRequestNumber(),
                        rejected.getRequestNumber(),
                        rejected.getInconsistencies().stream()
                                .map(Inconsistency::getName)
                                .collect(Collectors.joining(", "))
                ),
                "http://192.168.30.80:2005/view/" + rejected.getRequestNumber(),
                rejected.getStatus().getName()
        );

        requests.setStatus(Status.Closed);
        requests.setQty(qty);

        telegramService.sendMessageForRequest(new TelegramRequestEvent(this, requests, requests.getCreatedBy(), MessageType.CLOSE));
        notificationService.sendPrivateNotification(
                rejected.getCreatedBy().getUsername(),
                "Ваша заявка с № " + requests.getRequestNumber() + " была частично выполнена.",
                "http://192.168.30.80:2005/view/" + requests.getRequestNumber(),
                requests.getStatus().getName()
        );

        requests.setCloseDate(LocalDateTime.now());
    }

    private Requests createChildRejectedRequest(Requests parent, Integer qty, String description, Set<Inconsistency> inconsistencyData) {
        UUID newId = UUID.randomUUID();

        try {
            jdbcTemplate.update("""
                                INSERT INTO rces.requests (
                                    id, type_request, work_date, request_number, employee_id, 
                                    customer_order_id, reason, qty, item, status_id, reason_wr, 
                                    description, title, control, subdivision_id,
                                    created_by, created_date, updated_by, updated_date, version,
                                    qty_rejected,
                                    chat_id, comment, comment_agreed, frozen
                                ) VALUES (
                                    UUID_TO_BIN(?), ?, ?, ?, ?, 
                                    UUID_TO_BIN(?), ?, ?, ?, ?, 
                                    ?, ?, ?, ?, ?,
                                    ?, ?, ?, ?, 0, ?,
                                    ?, ?, ?, ?
                                )
                            """,
                    newId.toString(),
                    parent.getTypeRequest() != null ? parent.getTypeRequest().name() : null,
                    parent.getDateWork(),
                    repository.findNextRequestNumber(),
                    parent.getEmployee() != null ? parent.getEmployee().getId() : null,
                    parent.getCustomerOrder() != null ? parent.getCustomerOrder().getId().toString() : null,
                    parent.getReason() != null ? parent.getReason().name() : null,
                    parent.getQty() - qty,
                    parent.getItem() != null ? parent.getItem().name() : null,
                    "Rejected",
                    parent.getReason_wr(),
                    description,
                    parent.getTitle(),
                    parent.getControl(),
                    parent.getSubDivision() != null ? parent.getSubDivision().getId() : null,
                    parent.getCreatedBy().getId(),
                    parent.getCreatedDate() != null ? parent.getCreatedDate() : Instant.now(),
                    parent.getUpdatedBy() != null ? parent.getUpdatedBy().getId() : parent.getCreatedBy().getId(),
                    Instant.now(),
                    1,
                    parent.getChatId(),
                    parent.getComment(),
                    parent.getCommentAgreed(),
                    parent.isFrozen()
            );

            Requests savedRequest = repository.findById(newId)
                    .orElseThrow(() -> new RuntimeException("Не удалось создать заявку на брак с id: " + newId));

            savedRequest.setInconsistencies(new HashSet<>(inconsistencyData));
            copyImagesRequest(parent, savedRequest);

            return repository.save(savedRequest);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании заявки на брак: " + e.getMessage(), e);
        }
    }

    private void copyImagesRequest(Requests parent, Requests child) {
        if (parent.getImages() == null || parent.getImages().isEmpty()) {
            return;
        }

        if (child.getImages() == null) {
            child.setImages(new ArrayList<>());
        }

        for (Images original : parent.getImages()) {
            Images newImage = new Images();
            newImage.setData(original.getData());
            newImage.setName(original.getName());
            newImage.setBase64Data(original.getBase64Data());
            newImage.setRequest(child);
            child.getImages().add(newImage);
        }
    }
}
