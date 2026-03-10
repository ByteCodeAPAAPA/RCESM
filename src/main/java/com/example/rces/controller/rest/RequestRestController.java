package com.example.rces.controller.rest;

import com.example.rces.dto.CreateByRequest;
import com.example.rces.dto.ImagesDTO;
import com.example.rces.dto.RequestHistoryDTO;
import com.example.rces.dto.RequestParamsDto;
import com.example.rces.models.Employee;
import com.example.rces.models.Inconsistency;
import com.example.rces.models.Requests;
import com.example.rces.service.*;
import jakarta.ws.rs.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.example.rces.service.impl.CustomUserDetailsServiceImpl.currentUser;

@RestController
@RequestMapping("/api/request")
public class RequestRestController {

    private final RequestsService requestsService;
    private final ImageService imageService;
    private final InconsistenciesService inconsistenciesService;
    private final RequestHistoryService requestHistoryService;
    private final RequestLogService requestLogService;

    @Autowired
    public RequestRestController(RequestsService requestsService, ImageService imageService, InconsistenciesService inconsistenciesService, RequestHistoryService requestHistoryService, RequestLogService requestLogService) {
        this.requestsService = requestsService;
        this.imageService = imageService;
        this.inconsistenciesService = inconsistenciesService;
        this.requestHistoryService = requestHistoryService;
        this.requestLogService = requestLogService;
    }

    @PostMapping("/in-work")
    public ResponseEntity<?> inWork(@RequestBody RequestParamsDto requestParams) {
        Requests requests = requestsService.findById(requestParams.getRequestId());
        Set<Inconsistency> inconsistencies = Collections.emptySet();
        if (requestParams.getInconsistencyData() != null && !requestParams.getInconsistencyData().isEmpty()) {
            try {
                inconsistencies = Inconsistency.fromField(requestParams.getInconsistencyData(), new HashSet<>(inconsistenciesService.findAll()));
            } catch (Exception e) {
                throw new RuntimeException("Ошибка парсинга inconsistencyData", e);
            }
        }
        if (requestParams.getStatus() != null && requestParams.getStatus().equals("closed")) {
            if (requests.getTypeRequest().name().equals("otk")) {
                if (requestParams.getQtyCompleted() == null || requestParams.getQtyCompleted() < 0 || requestParams.getQtyCompleted() > requests.getQty()) {
                    throw new RuntimeException("Передано некорректное число!");
                }
            }
        }
        try {
            requestsService.save(requestParams, inconsistencies);
            Map<String, String> successMap = new HashMap<>();
            successMap.put("message", String.format("Заявка: %s успешно принята в работу!", requests.getRequestNumber()));
            return ResponseEntity.ok(successMap);
        } catch (ForbiddenException forbiddenException) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", forbiddenException.getMessage());
            return ResponseEntity.badRequest().body(errorMap);
        }
    }

    @GetMapping("/type-request")
    @ResponseBody
    public String getTypeRequest(@RequestParam UUID param) {
        return "\"" + requestsService.getTypeRequest(param) + "\"";
    }

    @PostMapping("/update")
    public void updateData(@RequestParam String bidType,// Имя класса bid
                           @RequestParam UUID id, // id класса bid
                           @RequestParam(required = false) Boolean sendMessage, // отправлять сообщение в ТГ
                           @RequestBody Map<String, Object> updatedFields) // ключ - название поля в классе bid, значение - значение поля в bid
    {
        updatedFields.remove("qtyCompleted");
        requestsService.update(id, sendMessage, updatedFields);
    }

    @PostMapping("/comment-bid")
    public void createCommentBid(@RequestParam UUID id, @RequestParam String comment) {
        requestsService.createComment(id, comment);
    }

    @GetMapping("/images")
    public List<ImagesDTO> getImages(@RequestParam UUID param) {
        return imageService.getImagesByRequestId(param);
    }

    @DeleteMapping("/delete-images")
    public ResponseEntity<?> deleteImages(@RequestBody Map<String, String> payload) {
        UUID imageId = UUID.fromString(payload.get("id"));
        UUID requestId = UUID.fromString(payload.get("reqId"));
        Requests requests = requestsService.findById(requestId);
        Employee currentUser = currentUser().orElseThrow();
        if (requests.getEmployee() == null || !requests.getEmployee().getName().equals(currentUser.getName())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Пользователь не может удалять фото в заявке!");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        imageService.deleteById(imageId);
        Map<String, String> messageResponse = new HashMap<>();
        messageResponse.put("message", "Фото успешно удалено!");
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/{requestId}/history")
    public ResponseEntity<List<?>> getHistory(@PathVariable UUID requestId) {
        try {
            List<RequestHistoryDTO> history = requestHistoryService.getDetailedRequestHistory(requestId);
            if (history.isEmpty()) {
                return ResponseEntity.ok(requestLogService.getAllByRequestId(requestId));
            }
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create-by")
    public ResponseEntity<?> updateCreateBy(@RequestBody CreateByRequest request) {
        try {
            requestsService.updateCreateBy(request.getRequestId(), request.getUser());
            return ResponseEntity.ok("Заявка успешно переадресована!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
