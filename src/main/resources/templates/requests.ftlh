<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Заявка на вызов</title>
    <link rel="stylesheet" href="/css/notification.css">
    <link rel="stylesheet" href="/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/stylesBid.css">
    <link rel="stylesheet" href="/css/sweetalert2.min.css">
</head>
<body>
<#if createForm!false>
    <div class="container mt-5 mb-5">
        <form id="createRequestForm" action="/create" method="POST" enctype="multipart/form-data"
              class="p-4 border rounded bg-light shadow-sm" novalidate>
            <h2 class="mb-4">Создание заявки</h2>

            <input type="hidden" name="type" value="${type}">

            <!-- Сотрудник -->
            <#if type == 'otk'>
                <div class="mb-3">
                    <label class="form-label">Сотрудник</label>
                    <div class="input-group has-validation">
                        <input type="text" id="employeeName" class="form-control" placeholder="Имя сотрудника" required>
                        <input type="hidden" id="employeeJson" name="employeeJson" required>
                        <button type="button" class="btn btn-outline-primary openModal"
                                data-endpoint="employees"
                                data-input-id="employeeName"
                                data-hidden-entity="employeeJson"
                                data-modal-id="employeesModal"
                                data-param="${type}">
                            Выбрать
                        </button>
                    </div>
                </div>
            </#if>


            <!-- Цех -->
            <div class="mb-3" id="mlmNodeCreateField">
                <label class="form-label">Цех</label>
                <div class="input-group">
                    <input type="text" id="mlmNodeName" class="form-control" placeholder="Цех"
                           value="${mlmNodeEmployee.getName()}" required>
                    <input type="hidden" id="mlmNodeJson" name="mlmNodeJson" value="${mlmNodeEmployee.getName()}">
                </div>
            </div>

            <!-- ПОЛЕ ОБОЗНАЧЕНИЕ/НАИМЕНОВАНИЕ-->
            <div class="mb-3" id="titleJsonId">
                <label for="titleJson" class="form-label">Обозначение/Наименование</label>
                <input type="text" id="titleJson" name="titleJson" class="form-control"
                       placeholder="Обозначение/Наименование" required>
            </div>

            <!-- ТМЦ -->
            <#if type != 'technologist'>
                <div class="mb-3">
                    <label class="form-label">Тип ТМЦ</label>
                    <div class="input-group">
                        <input type="text" id="itemName" class="form-control" placeholder="ТМЦ">
                        <input type="hidden" id="itemNameJson" name="itemNameJson">
                        <button type="button" class="btn btn-outline-primary openModal"
                                data-endpoint="item"
                                data-input-id="itemName"
                                data-hidden-entity="itemNameJson"
                                data-modal-id="itemModal">
                            Выбрать
                        </button>
                    </div>
                </div>
            </#if>

            <!-- Кол-во деталей -->
            <#if type == 'otk'>
                <div class="mb-3" id="qtyCreateField">
                    <label for="qty" class="form-label">Количество деталей к контролю</label>
                    <#if mlmNodeEmployee.getName() == 'Цех №1'>
                        <input type="text" id="qty" name="qty" class="form-control">
                    <#else >
                        <input type="number" id="qty" name="qty" class="form-control" required
                               min="0" oninput="this.value = Math.abs(this.value)">
                    </#if>
                </div>
            </#if>

            <!-- Тип контроля -->
            <#if type == 'otk'>
                <div class="mb-3" id="divControlId">
                    <label for="control" class="form-label">Тип контроля</label>
                    <#if mlmNodeEmployee.getName() == 'Цех №1'>
                        <select id="control" name="control" class="form-select">
                            <option value="">Выберите тип контроля</option>
                            <option value="ВИК" selected>ВИК</option>
                            <option value="ЛНК">ЛНК</option>
                        </select>
                    <#else >
                        <select id="control" name="control" class="form-select" required>
                            <option value="ВИК">ВИК</option>
                            <option value="ЛНК">ЛНК</option>
                        </select>
                    </#if>
                    <input type="hidden" id="hiddenСontrol" class="form-control">
                </div>
            </#if>

            <!-- Заказ клиента -->
            <div class="mb-3" id="divCustomerId">
                <label class="form-label">Заказ клиента</label>
                <div class="input-group">
                    <#if mlmNodeEmployee.getName() == 'Цех №1'>
                        <input type="text" id="customerOrderString" name="customerOrderString" class="form-control"
                               placeholder="Номер заказа">
                    <#else >
                        <input type="text"
                               id="customerOrderString"
                               name="customerOrderString"
                               class="form-control"
                               placeholder="Заказ должен начинаться с 20..."
                               value=""
                               oninput="validateOrderNumber(this)"
                               pattern="20.*"
                               title="Заказ должен начинаться с 20">

                        <div class="invalid-feedback" id="orderError">Заказ должен начинаться с 20</div>
                    </#if>

                    <input type="hidden" id="customerOrderJson" name="customerOrderJson">
                    <button type="button" class="btn btn-outline-primary openModal"
                            data-endpoint="customer-orders"
                            data-input-id="customerOrderString"
                            data-hidden-entity="customerOrderJson"
                            data-modal-id="customerOrdersModal">
                        Выбрать
                    </button>
                </div>
            </div>

            <!-- Причина -->
            <#if type == 'constructor' || type == 'technologist'>
                <div class="mb-3" id="reasonCreateField">
                    <label class="form-label">Причина</label>
                    <div class="input-group">
                        <input type="text" id="reasonsName" class="form-control" placeholder="Причина" readonly>
                        <input type="hidden" id="reasonsJson" name="reasonsJson">
                        <button type="button" class="btn btn-outline-primary openModal"
                                data-endpoint="reasons"
                                data-input-id="reasonsName"
                                data-hidden-entity="reasonsJson"
                                data-modal-id="reasonsModal"
                                data-param="${type}">
                            Выбрать
                        </button>
                    </div>
                </div>
            <#else>
                <div class="mb-3" id="divReasonsId">
                    <label for="reasonsJson" class="form-label">Причина</label>
                    <input type="text" id="reasonsJson" name="reasonsJson" class="form-control"
                           placeholder="Введите причину" list="reasonsList">
                    <datalist id="reasonsList">
                    </datalist>
                </div>
            </#if>

            <!-- Комментарий -->
            <div class="mb-3" id="divCommentId">
                <label for="comment" class="form-label">Комментарий</label>
                <textarea class="form-control" id="comment" name="comment" rows="4"
                          placeholder="Введите комментарий"></textarea>
            </div>

            <!-- Фото -->
            <div class="mb-3">
                <label for="additionalFiles" class="form-label">Загрузить фотографии или сделать фото</label>
                <input type="file" id="additionalFiles" name="additionalFiles" accept="image/*" multiple
                       capture="environment"
                       class="form-control">
            </div>

            <!-- Кнопка отправки -->
            <div class="d-grid" id="createId">
                <button type="submit" class="btn btn-success" id="submitBtn">
                    <span id="btnText">Создать заявку</span>
                    <div id="btnSpinner" class="spinner-border spinner-border-sm d-none" role="status">
                        <span class="visually-hidden">Загрузка...</span>
                    </div>
                </button>
            </div>
        </form>
    </div>
</#if>
<#if viewForm!false>
    <form id="viewRequestForm" class="container mt-4">
        <input type="hidden" id="id" value="${bid.id}"/>

        <!-- Информация -->
        <div class="card mb-4">
            <#switch bid.getStatus().getName()>
                <#case "Новый">
                    <div class="card-header bg-secondary text-white">
                        Информация
                    </div>
                    <#break>
                <#case "В работе">
                    <div class="card-header bg-warning text-white">
                        Информация
                    </div>
                    <#break>
                <#case "Забракована">
                    <div class="card-header bg-danger text-white">
                        Информация
                    </div>
                    <#break>
                <#case "Закрыт">
                    <div class="card-header btn-success text-white">
                        Информация
                    </div>
                    <#break>
                <#case "На доработке">
                    <div class="card-header bg-warning text-white">
                        Информация
                    </div>
                    <#break>
            </#switch>
            <div class="card-body">
                <#switch bid.getStatus().getName()>
                    <#case "Новый">
                        <p class="card-header fs-5 fw-bold bg-secondary text-center text-white">Заявка
                            №${bid.requestNumber}</p>
                        <#break>
                    <#case "В работе">
                        <p class="card-header fs-5 fw-bold bg-warning text-center text-white">Заявка
                            №${bid.requestNumber}</p>
                        <#break>
                    <#case "Забракована">
                        <p class="card-header fs-5 fw-bold bg-danger text-center text-white">Заявка
                            №${bid.requestNumber}</p>
                        <#break>
                    <#case "Закрыт">
                        <p class="card-header fs-5 fw-bold btn-success text-center text-white">Заявка
                            №${bid.requestNumber}</p>
                        <#break>
                    <#case "На доработке">
                        <p class="card-header fs-5 fw-bold bg-warning text-center text-white">Заявка
                            №${bid.requestNumber}</p>
                        <#break>
                </#switch>
                <#if bid.status.getName() != "Новый">
                    <p class="mb-2 m-lg-2"><strong>Ответственный:</strong> ${(bid.getEmployee().getName())!""}</p>
                </#if>
                <p class="mt-2 mb-2 m-lg-2"><strong>Дата создания:</strong> ${(date)!""}</p>
                <p class="mb-2 m-lg-2"><strong>Цех:</strong> ${(bid.getSubDivision().getName())!""}</p>
                <#if bid.typeRequest.name() != 'technologist'>
                    <p class="mb-2 m-lg-2"><strong>Тип ТМЦ:</strong> ${(bid.item.getName())!"Не указан"}</p>
                </#if>
                <p class="mb-2 m-lg-2"><strong>Заказ клиента:</strong> ${(bid.getCustomerOrder().getName())!"Не указан"}
                </p>
                <p class="mb-2 m-lg-2"><strong>Обозначение/Наименование:</strong> ${(bid.getTitle())!"Не указан"}</p>
                <#if bid.typeRequest == 'otk'>
                    <p class="mb-2 m-lg-2"><strong>Тип контроля:</strong> ${(bid.getControl())!"ВИК"}</p>
                    <#if bid.typeRequest == 'otk' && (bid.status.getName() == "Новый" || bid.status.getName() == "В работе")>
                        <p class="mb-2 m-lg-2"><strong>Количество деталей к контролю:</strong> ${(bid.qty)!"Не указано"}
                        </p>
                    <#elseif bid.typeRequest == 'otk' && bid.status.getName() == "Закрыт">
                        <p class="mb-2 m-lg-2"><strong>Количество деталей принято:</strong> ${(bid.qty)!0}</p>
                    <#elseif bid.typeRequest == 'otk' && bid.status.getName() == "Забракована">
                        <p class="mb-2 m-lg-2"><strong>Количество деталей отклонено:</strong> ${(bid.qty)!0}</p>
                    </#if>
                    <p class="mb-2 m-lg-2"><strong>Причина:</strong> ${(bid.getReason_wr())!"Не указана"}</p>
                <#else >
                    <p class="mb-2 m-lg-2"><strong>Причина:</strong> ${(bid.reason.getName())!"Не указана"}</p>
                </#if>
                <#if bid.status.getName() == "В работе">
                <p class="mb-2 m-lg-2"><strong>Комментарий:</strong>
                    <textarea class="form-control"
                              id="comment"
                              name="comment"
                              rows="5"
                              style="resize: both;"
                              readonly>${bid.comment!''}</textarea>
                    </#if>
            </div>
        </div>

        <div class="d-flex justify-content-between">
            <a href="/requestslist/${type}" class="btn btn-outline-secondary d-none d-md-block mb-3">Перейти в
                список</a>
            <#if bid.typeRequest == "otk">
                <#if bid.getQtyRejected() = 0>
                    <a class="btn btn-outline-secondary mb-3">Количество брака заявки: ${(bid.getQtyRejected())!0}</a>
                <#else>
                    <a class="btn btn-danger mb-3 history-icon" data-id="${bid.id}" data-bs-toggle="tooltip"
                       title="Нажмите для просмотра истории изменений">
                        <i class="bi bi-clock-history me-2"></i>
                        Количество брака заявки: ${(bid.getQtyRejected())!0}
                    </a>
                </#if>
            </#if>

        </div>


        <!-- Редактирование -->
        <div class="card mb-4">
            <#switch bid.getStatus().getName()>
                <#case "Новый">
                    <div class="card-header bg-secondary text-white">
                        Редактирование
                    </div>
                    <#break >
                <#case "В работе">
                    <div class="card-header bg-warning text-white">
                        Редактирование
                    </div>
                    <#break>
                <#case "Забракована">
                    <div class="card-header bg-danger text-white">
                        Редактирование
                    </div>
                    <#break>
                <#case "Закрыт">
                    <div class="card-header btn-success text-white">
                        Редактирование
                    </div>
                    <#break>
                <#case "На доработке">
                    <div class="card-header bg-warning text-white">
                        Редактирование
                    </div>
                    <#break>
            </#switch>
            <div class="card-body">

                <#if bid.status.getName() == "На доработке">
                    <div class="mt-3">
                        <label for="closed" class="form-label">Несоответствия</label>
                        <input type="text" class="form-control mb-2"
                               value="${(bid.inconsistencies??)?then(bid.inconsistencies?map(i -> i.getName())?join(', '), '')}"
                               readonly>
                        <label for="closed" class="form-label">Описание несоответствия</label>
                        <textarea class="form-control mb-3"
                                  name="description"
                                  id="description"
                                  rows="3"
                                  readonly>${bid.getDescription()!""}
                            </textarea>
                        <button class="btn btn-success work"
                                id="successRework"
                                data-param="${bid.getId()}"
                                data-status="underRework"
                        >
                            Принять в доработку
                        </button>
                        <button class="btn btn-danger" id="cancelRework">Отклонить</button>
                    </div>
                </#if>

                <#if bid.status.getName() == "Новый">
                    <!-- Ответственный -->
                    <div class="mb-3">
                        <label for="employeeName" class="form-label">Ответственный:</label>
                        <div class="input-group">
                            <input type="text" id="employeeName"
                                   value="<#if bid.employee??>${bid.getEmployee().getName()?trim}<#else>Не назначен</#if>"
                                   readonly
                                   class="form-control">
                            <#if bid.employee??>
                                <#assign employeeJson = '{"id":"${bid.getEmployee().getId()?c}", "name":"${bid.getEmployee().getName()?js_string}"}'>
                            <#else>
                                <#assign employeeJson = '{"id":"", "name":""}'>
                            </#if>
                            <input type="hidden" id="employeeJson"
                                   value='${employeeJson}'
                                   name="employee">
                            <#--                            canChangeEmployee ||-->
                            <#if bid.typeRequest.name() == 'otk'>
                                <button type="button"
                                        class="btn btn-outline-secondary openModal"
                                        data-endpoint="employees"
                                        data-input-id="employeeName"
                                        data-hidden-entity="employeeJson"
                                        data-modal-id="employeesModal"
                                        data-param="${bid.getTypeRequest().name()}">
                                    Изменить
                                </button>
                            </#if>

                        </div>
                        <#if role != 'MASTER'>
                        <p class="mb-2"><strong>Комментарий:</strong>
                            <textarea class="form-control"
                                      id="comment"
                                      name="comment"
                                      rows="5"
                                      style="resize: both;"
                                      readonly>${bid.comment!''}</textarea>
                            </#if>
                    </div>
                    <!-- Кнопка "Взять в работу" -->
                    <#if role != 'MASTER'>
                        <div>
                            <button type="button"
                                    class="btn btn-success mb-3 work"
                                    id="workId"
                                    data-param="${bid.getId()}">
                                Взять в работу
                            </button>
                        </div>
                    </#if>
                </#if>

                <#if bid.getStatus().getName() == 'В работе' && role == 'MASTER'>
                <p class="text-success fs-3 fw-bold">Заявка была принята в работу</p>
            <#elseif bid.getStatus().getName() == 'В работе' && role != 'MASTER' && type != 'otk'>
                <button type="button"
                        class="btn btn-success mb-3 work"
                        id="executeBtn"
                        data-bs-toggle="modal"
                        data-bs-target="#completedTechnologistModal"
                >
                    Выполнено
                </button>
            <#elseif bid.getStatus().getName() == 'В работе' && role != 'MASTER' && type == 'otk'>
                <div>
                    <button type="button"
                            class="btn btn-success mb-3 work"
                            id="workId"
                            data-bs-toggle="modal"
                            data-bs-target="#exampleModal"
                    >
                        Выполнено
                    </button>
                    <button type="button"
                            class="btn btn-danger mb-3"
                            id="executeOtkBtn"
                            data-bs-toggle="modal"
                            data-bs-target="#exampleModalOtk">
                        Забракован
                    </button>
                </div>
                <p class="mb-2 m-lg-2"><strong>Комментарий от ОТК:</strong>
                    <textarea class="form-control"
                              id="descriptionCompletedId"
<#--                              name="descriptionCompleted"-->
                              rows="5"
                              style="resize: both;"
                    ></textarea>
                    </#if>

                    <#if role == ('MASTER') && bid.getStatus().getName() == 'Выполнен'>
                    <#if type != 'otk'>
                        <label for="closed" class="form-label">Описание несоответствия</label>
                        <textarea class="form-control"
                                  name="description"
                                  id="description"
                                  rows="5"
                                  readonly>${bid.getDescription()!""}
                    </textarea>
                    <#else >
                        <input type="text" readonly class="form-control" value="${bid.status.getName()}">
                    </#if>

                    <#elseif role != ('MASTER') && bid.getStatus().getName() == 'Выполнен'>
                <p class="text-success fs-3 fw-bold">Решение отправлено</p>
                </#if>

                <#if bid.getStatus().getName() == 'Закрыт'>
                    <p class="text-success fs-3 fw-bold">Заявка закрыта!</p>
                    <#if bid.typeRequest.name() == 'technologist'>
                        <p class="text fw-bold">Описание решения поставленной
                            задачи:${(bid.getDescription())!"Описание решения заявки отсутствует!"}</p>
                    <#elseif bid.typeRequest.name() == 'otk'>
                        <#if bid.getDescription()?? && bid.getDescription()?has_content>
                            <div class="comment-minimalist mt-4" data-theme="light">
                                <div class="comment-header d-flex align-items-center gap-2 mb-3">
            <span class="comment-badge d-inline-flex align-items-center gap-1 px-3 py-1 rounded-pill"
                  style="background: rgba(13, 110, 253, 0.1); color: #0d6efd;">
                <i class="bi bi-pencil-square"></i>
                <span class="fw-semibold">Комментарий</span>
            </span>
                                    <span class="text-muted small">•</span>
                                    <span class="text-muted small">Дополнительная информация</span>
                                </div>
                                <div class="comment-text p-4 rounded-3"
                                     style="background: #fff;
                    border-left: 4px solid #0d6efd;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.04);">
                                    <p class="mb-0"
                                       style="color: #374151; line-height: 1.7;">${bid.getDescription()}</p>
                                </div>
                            </div>
                        <#else>
                            <div class="empty-comment-minimalist mt-4 p-4 rounded-3 text-center"
                                 style="background: #f8fafc; border: 1px solid #e2e8f0;">
                                <div class="empty-icon-wrapper mb-3">
                                    <div class="d-inline-flex p-3 rounded-circle" style="background: #e2e8f0;">
                                        <i class="bi bi-chat-dots fs-4" style="color: #94a3b8;"></i>
                                    </div>
                                </div>
                                <p class="fw-medium mb-1" style="color: #64748b;">Нет комментариев</p>
                                <p class="small text-muted mb-0">Комментарий не был добавлен к выполнению</p>
                            </div>
                        </#if>
                    </#if>
                </#if>

                <#if bid.getStatus().getName() == 'Забракована'>
                    <p class="text-danger fs-3 fw-bold">Заявка забракована!</p>
                    <label for="employeeName" class="form-label">Ответственный:</label>
                    <div class="input-group">
                        <input type="text" id="employeeName"
                               value="${bid.getEmployee().getName()?trim}"
                               readonly
                               class="form-control">
                        <#assign employeeJson = '{"id":"${bid.getEmployee().getId()?c}", "name":"${bid.getEmployee().getName()?js_string}"}'>
                        <input type="hidden" id="employeeJson"
                               value='${employeeJson}'
                               name="employee">
                        <button type="button"
                                class="btn btn-outline-secondary openModal"
                                data-endpoint="employees"
                                data-input-id="employeeName"
                                data-hidden-entity="employeeJson"
                                data-modal-id="employeesModal"
                                data-param="${bid.getTypeRequest().name()}">
                            Изменить
                        </button>
                    </div>
                    <label for="closed" class="form-label">Несоответствия</label>
                    <input type="text" class="form-control"
                           value="${(bid.inconsistencies??)?then(bid.inconsistencies?map(i -> i.getName())?join(', '), '')}"
                           readonly>
                    <label for="closed" class="form-label">Описание несоответствия</label>
                    <textarea class="form-control"
                              name="description"
                              id="description"
                              rows="3"
                              readonly>${bid.getDescription()!""}
                    </textarea>

                    <#if role == 'MASTER'>
                        <div class="d-flex justify-content-between gap-3 mt-3">
                            <button type="button"
                                    class="btn btn-success work"
                                    id="canceledBtn"
                                    data-param="${bid.getId()}"
                                    data-status="update">
                                Отправить повторно
                            </button>
                            <button type="button" class="btn btn-secondary"
                                    id="updateUserBtn"
                                    data-bs-toggle="modal"
                                    data-bs-target="#updateEmployeeModalId">
                                Перенаправить на исправление
                            </button>
                        </div>
                    </#if>

                </#if>

                <#if bid.typeRequest == "otk">
                    <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel"
                         aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="exampleModalLabel">Выполнение заявки</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Закрыть"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="mb-3 d-flex justify-content-between">
                                        <label for="qtyCompleted" class="form-label mt-2">Количество принятых
                                            деталей: </label>
                                        <div style="width: 450px;">
                                            <input type="number" id="qtyCompleted" name="qtyCompleted"
                                                   class="form-control"
                                                   min="0"
                                                   max="${(bid.getQty())!0}">
                                            <div class="invalid-feedback">
                                                Введите корректное количество (Количество деталей к
                                                контролю: ${(bid.getQty())!0})
                                            </div>
                                        </div>
                                    </div>
                                    <div id="rejectedBidOtk" class="d-none">
                                        <div class="mb-2">
                                            <div class="mb-2">
                                                <p class="text-danger">Количество, которое будет перемещено в заявку с
                                                    браком:
                                                    <span id="movedQuantity">0</span>
                                                </p>
                                            </div>
                                        </div>
                                        <div class="mb-2">
                                            <label for="inconsistencyName1" class="mb-3">Несоответствия:</label>
                                            <input type="text" id="inconsistencyName1" readonly
                                                   class="form-control readonly-scroll"
                                                   value="${(bid.inconsistencies??)?then(bid.inconsistencies?map(i -> i.getName())?join(', '), '')}">
                                            <input type="hidden" id="inconsistencyJson1" name="inconsistencies"
                                                   value='[<#list bid.inconsistencies as i>{"name": "${i.getName()?json_string}"}<#sep>, </#sep></#list>]'>
                                            <button type="button" class="btn btn-primary mt-2 openModal"
                                                    data-endpoint="inconsistency"
                                                    data-input-id="inconsistencyName1"
                                                    data-hidden-entity="inconsistencyJson1"
                                                    data-modal-id="inconsistencyModal"
                                                    data-multiple="true"
                                                    data-param="${bid.getControl()!""}">
                                                Изменить
                                            </button>
                                        </div>
                                        <div class="mb-3">
                                            <label for="description" class="form-label">Описание несоответствия:</label>
                                            <textarea class="form-control"
                                                      id="description1"
                                                      name="description"
                                                      rows="4"
                                                      placeholder="Введите описание несоответствия">${bid.description!''}</textarea>
                                        </div>
                                        <div>
                                            <!-- ДОБАВЛЕНИЕ ФОТО -->
                                            <button class="btn btn-outline-secondary"
                                                    type="button"
                                                    id="openPhotoModal1"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#photoModal"
                                            >Прикрепленные фото
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена
                                    </button>
                                    <button type="button"
                                            id="successId"
                                            class="btn btn-success work"
                                            data-endpoint="request/in-work"
                                            data-param="${bid.getId()}"
                                            data-status="closed">
                                        Сохранить
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </#if>

                <div class="modal fade" id="exampleFullPhotoModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-body text-center">
                                <img id="exampleFullPhoto" src="" style="max-width:100%; max-height:80vh;">
                                <button type="button" class="btn btn-secondary mt-2 close">Закрыть</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="modal fade" id="exampleModalOtk" tabindex="-1" aria-labelledby="exampleModalOtkLabel"
                     aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="exampleModalOtkLabel">Выполнение заявки</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Закрыть"></button>
                            </div>
                            <div class="modal-body">

                                <div class="mb-2">
                                    <label for="inconsistencyName2" class="mb-3">Несоответствия:</label>
                                    <input type="text" id="inconsistencyName2" readonly
                                           class="form-control readonly-scroll"
                                           value="${(bid.inconsistencies??)?then(bid.inconsistencies?map(i -> i.getName())?join(', '), '')}">
                                    <input type="hidden" id="inconsistencyJson2" name="inconsistencies"
                                           value='[<#list bid.inconsistencies as i>{"name": "${i.getName()?json_string}"}<#sep>, </#sep></#list>]'>
                                    <button type="button" class="btn btn-primary mt-2 openModal"
                                            data-endpoint="inconsistency"
                                            data-input-id="inconsistencyName2"
                                            data-hidden-entity="inconsistencyJson2"
                                            data-modal-id="inconsistencyModal"
                                            data-multiple="true"
                                            data-param="${bid.getControl()!""}">
                                        Изменить
                                    </button>
                                </div>
                                <div class="mb-2">
                                    <p>Добавить несоответствие:
                                        <button type="button" class="btn btn-primary m-lg-2" id="openModalBtn"
                                                data-bs-toggle="modal"
                                                data-bs-target="#addInconsistenciesModal">✅
                                        </button>
                                    </p>
                                    <#--                                    <div class="d-flex flex-column flex-md-row justify-content-evenly align-items-center gap-3 mt-2">-->
                                    <#--                                        <div class="text-center">-->
                                    <#--                                            <h5>Уведомление НП</h5>-->
                                    <#--                                            <button type="button" class="btn btn-primary" id="noticeId"-->
                                    <#--                                                    onclick="toggleValue(this)" value="false">Уведомить-->
                                    <#--                                            </button>-->
                                    <#--                                        </div>-->

                                    <#--                                        <div class="text-center">-->
                                    <#--                                            <h5>Уведомление для ОГТ</h5>-->
                                    <#--                                            <button type="button" class="btn btn-primary" id="noticeOgtId"-->
                                    <#--                                                    onclick="toggleValue(this)" value="false">Уведомить-->
                                    <#--                                            </button>-->
                                    <#--                                        </div>-->

                                    <#--                                        <div class="text-center">-->
                                    <#--                                            <h5>Уведомления для ОГК</h5>-->
                                    <#--                                            <button type="button" class="btn btn-primary" id="noticeOgcId"-->
                                    <#--                                                    onclick="toggleValue(this)" value="false">Уведомить-->
                                    <#--                                            </button>-->
                                    <#--                                        </div>-->
                                    <#--                                    </div>-->
                                    <#--                                </div>-->
                                    <div class="mb-3 mt-4">
                                        <label for="description" class="form-label">Описание несоответствия:</label>
                                        <textarea class="form-control"
                                                  id="description2"
                                                  name="description"
                                                  rows="4"
                                                  placeholder="Введите описание несоответствия">${bid.description!''}</textarea>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена
                                    </button>
                                    <button type="button"
                                            id="successId"
                                            class="btn btn-success work"
                                            data-endpoint="request/in-work"
                                            data-param="${bid.getId()}"
                                            data-description="${bid.getDescription()!""}">
                                        Сохранить
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <#if ((bid.status.getName() == "Новый" || bid.status.getName() == "Выполнен") && role == "MASTER")>
                        <div class="mb-1 mt-2">
                            <label for="comment" class="form-label">Комментарий:</label>
                            <textarea class="form-control work"
                                      id="comment"
                                      name="comment"
                                      rows="5"
                                      style="resize: both;">${bid.comment!''}</textarea>
                        </div>
                    </#if>
                    <#if bid.status.getName() == "В работе" && role == "OTK">
                        <div class="mb-1 mt-2">
                            <button class="btn btn-outline-secondary" id="openOtkId" type="button">Комментарий для
                                детали
                                которая пропущена с дефектами ↓
                            </button>
                            <textarea class="form-control mt-2"
                                      id="commentOtk"
                                      name="commentAgreed"
                                      rows="5"
                                      style="resize: both; display: none">${bid.commentAgreed!''}</textarea>
                        </div>
                        <button class="btn btn-success" id="commentOtkId"
                                style="display: none"
                                data-param="${bid.getId()}"
                                data-comment="${bid.getCommentAgreed()!""}">
                            Сохранить комментарий
                        </button>
                    </#if>
                </div>
            </div>

            <#if bid.getStatus().getName() == "Забракована" || (bid.getStatus().getName() == "Закрыт" && bid.qtyRejected > 0)>
                <div class="card mb-4">
                    <div class="card-header bg-danger text-white">Забракованные версии</div>
                    <div class="card-body">

                        <!-- Десктопная версия -->
                        <div class="d-none d-md-block">
                            <table class="table">
                                <thead>
                                <tr>
                                    <td>Дата</td>
                                    <td>Несоответствие</td>
                                    <td>Описание</td>
                                    <td>Фото</td>
                                </tr>
                                </thead>
                                <tbody>
                                <#list requestHistoryDTOList as history>
                                    <tr>
                                        <td>${history.changeRequestDate()}</td>
                                        <td>
                                            <#if history.requestData.inconsistencies?? && history.requestData.inconsistencies?size gt 0>
                                                ${history.requestData.inconsistencies?join(", ")}
                                            <#else>
                                                Не указано
                                            </#if>
                                        </td>
                                        <td>${(history.getRequestData().getDescription())!"-"}</td>
                                        <td>
                                            <#if history.requestData.images?? && history.requestData.images?size gt 0>
                                                <button type="button"
                                                        class="btn btn-outline-primary btn-sm"
                                                        data-bs-toggle="modal"
                                                        data-bs-target="#photoModal${history?index}">
                                                    <i class="bi bi-image"></i> Фото (${history.requestData.images?size}
                                                    )
                                                </button>
                                            <#else>
                                                <span class="text-muted">Нет фото</span>
                                            </#if>
                                        </td>
                                    </tr>
                                </#list>
                                </tbody>
                            </table>
                        </div>

                        <!-- Мобильная версия -->
                        <div class="d-md-none">
                            <div class="request-history-mobile">
                                <#list requestHistoryDTOList as history>
                                    <div class="card history-card mb-3">
                                        <div class="card-body">
                                            <!-- Верхняя строка с датой и фото -->
                                            <div class="d-flex justify-content-between align-items-start mb-2">
                                                <div class="d-flex align-items-center">
                                                    <i class="bi bi-calendar text-primary me-2"></i>
                                                    <strong class="text-dark">${history.changeRequestDate()}</strong>
                                                </div>
                                                <#if history.requestData.images?? && history.requestData.images?size gt 0>
                                                    <button type="button"
                                                            class="btn btn-outline-primary btn-sm"
                                                            data-bs-toggle="modal"
                                                            data-bs-target="#photoModal${history?index}">
                                                        <i class="bi bi-images me-1"></i>
                                                        <span class="badge bg-primary">${history.requestData.images?size}</span>
                                                    </button>
                                                <#else>
                                                    <span class="badge bg-secondary">Нет фото</span>
                                                </#if>
                                            </div>

                                            <!-- Несоответствие -->
                                            <div class="mb-2">
                                                <small class="text-muted d-block">Несоответствие:</small>
                                                <div class="fw-medium">
                                                    <#if history.requestData.inconsistencies?? && history.requestData.inconsistencies?size gt 0>
                                                        ${history.requestData.inconsistencies?join(", ")}
                                                    <#else>
                                                        <span class="text-muted">Не указано</span>
                                                    </#if>
                                                </div>
                                            </div>

                                            <!-- Описание -->
                                            <div>
                                                <small class="text-muted d-block">Описание:</small>
                                                <div class="text-break">
                                                    ${(history.getRequestData().getDescription())!"-"}
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </#list>
                            </div>
                        </div>
                    </div>
                </div>
            </#if>

            <#list requestHistoryDTOList as history>
                <#if history.requestData.images?? && history.requestData.images?size gt 0>
                    <div class="modal fade" id="photoModal${history?index}" tabindex="-1"
                         aria-labelledby="photoModalLabel${history?index}" aria-hidden="true">
                        <div class="modal-dialog modal-lg modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="photoModalLabel${history?index}">
                                        Фото версии от ${history.changeRequestDate()}
                                    </h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Закрыть"></button>
                                </div>
                                <div class="modal-body">
                                    <div id="carousel${history?index}" class="carousel slide" data-bs-ride="carousel">
                                        <#if history.requestData.images?size gt 1>
                                            <div class="carousel-indicators">
                                                <#list history.requestData.images as image>
                                                    <button type="button"
                                                            data-bs-target="#carousel${history?index}"
                                                            data-bs-slide-to="${image?index}"
                                                            <#if image?index == 0>class="active"
                                                            aria-current="true"</#if>
                                                            aria-label="Фото ${image?index + 1}">
                                                    </button>
                                                </#list>
                                            </div>
                                        </#if>

                                        <div class="carousel-inner">
                                            <#list history.requestData.images as image>
                                                <div class="carousel-item <#if image?index == 0>active</#if>">
                                                    <#if image.data?? && image.data?has_content>
                                                        <#assign imageData = image.data>
                                                        <#if !image.data?starts_with("data:")>
                                                            <#assign imageData = "data:image/jpeg;base64," + image.data>
                                                        </#if>

                                                        <img src="${imageData}"
                                                             class="d-block w-100"
                                                             alt="Фото ${image?index + 1}"
                                                             style="max-height: 70vh; object-fit: contain;"
                                                             onerror="console.error('Image load error'); this.style.display='none'; this.nextElementSibling.style.display='block';">
                                                    <#else>
                                                        <div class="d-block w-100 bg-light d-flex align-items-center justify-content-center"
                                                             style="height: 400px;">
                                                            <div class="text-center text-muted">
                                                                <i class="bi bi-image" style="font-size: 3rem;"></i>
                                                                <p class="mt-2">Нет данных изображения</p>
                                                                <small>Data: ${(image.data??)?then('exists','null')}</small>
                                                            </div>
                                                        </div>
                                                    </#if>

                                                    <div class="d-none bg-light d-flex align-items-center justify-content-center"
                                                         style="height: 400px;">
                                                        <div class="text-center text-danger">
                                                            <i class="bi bi-exclamation-triangle"
                                                               style="font-size: 3rem;"></i>
                                                            <p class="mt-2">Ошибка загрузки изображения</p>
                                                        </div>
                                                    </div>

                                                    <div class="carousel-caption d-none d-md-block bg-dark bg-opacity-50 rounded p-2">
                                                        <p class="mb-0">
                                                            <#if image.name??>
                                                                ${image.name?replace("T", " ")}
                                                            <#else>
                                                                Фото ${image?index + 1}
                                                            </#if>
                                                        </p>
                                                    </div>
                                                </div>
                                            </#list>
                                        </div>

                                        <#if history.requestData.images?size gt 1>
                                            <button class="carousel-control-prev" type="button"
                                                    data-bs-target="#carousel${history?index}" data-bs-slide="prev">
                                                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                                <span class="visually-hidden">Предыдущее</span>
                                            </button>
                                            <button class="carousel-control-next" type="button"
                                                    data-bs-target="#carousel${history?index}" data-bs-slide="next">
                                                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                                <span class="visually-hidden">Следующее</span>
                                            </button>
                                        </#if>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </#if>
            </#list>

            <!-- Telegram уведомление -->
            <div class="form-check form-switch mb-4 d-none">
                <input type="hidden" name="sendToTelegram" value="true">
                <input class="form-check-input" type="checkbox" id="telegramCheckbox" name="sendToTelegram"
                       value="false">
                <label class="form-check-label" for="telegramCheckbox">
                    Не отправлять уведомление в Telegram
                </label>
            </div>

            <!-- Кнопки действий -->
            <div class="d-flex flex-wrap gap-2 mb-4">
                <button class="btn btn-outline-secondary"
                        type="button"
                        id="openPhotoModal"
                        data-bs-toggle="modal"
                        data-bs-target="#photoModal"
                >Прикрепленные фото
                </button>
                <a href="/menu" class="btn btn-outline-secondary">Меню</a>
            </div>
            <div class="modal fade" id="completedTechnologistModal" tabindex="-1"
                 aria-labelledby="completedTechnologistModalLabel"
                 aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="completedTechnologistModalLabel">Выполнение заявки</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Закрыть"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label for="description" class="form-label">Описание выполненной работы:</label>
                                <textarea class="form-control"
                                          id="descriptionTechnologyId"
                                          rows="4"
                                          placeholder="Введите описание выполненной работы"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                            <button type="button"
                                    id="successId"
                                    class="btn btn-success work"
                                    data-endpoint="request/in-work"
                                    data-param="${bid.getId()}"
                                    data-status="completed">
                                Сохранить
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
    <div class="modal fade" id="invokeServiceModalId" tabindex="-1"
         aria-labelledby="invokeServiceModalLabel"
         aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="invokeServiceModalLabel">Вызов отдела на исправление</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                            aria-label="Закрыть"></button>
                </div>
                <div class="modal-body">
                    <form id="invokeServiceForm" action="/api/request/create-invoke" method="POST">
                        <input type="hidden" name="bidId" value="${bid.id}">

                        <label for="typeServiceId" class="mt-2 mb-2">Выберите отдел для вызова сотрудника: </label>
                        <select id="typeServiceId" name="typeService" class="form-select">
                            <#--                            <#list bid.typeRequest.values() as typeService>-->
                            <#--                                <option value="${typeService}">${typeService.getName()}</option>-->
                            <#--                            </#list>-->
                        </select>

                        <label for="descriptionInvokeServiceId" class="mt-2 mb-2">Причина вызова: </label>
                        <textarea class="form-control"
                                  id="descriptionInvokeServiceId"
                                  name="description"
                                  rows="4"
                                  placeholder="Введите причину вызова">${(bid.getDescription())!""}</textarea>

                        <label for="titleInvokeServiceId" class="mt-2 mb-2">Обозначение/Наименование: </label>
                        <input type="text" id="titleInvokeServiceId" name="title"
                               class="form-control mt-2 mb-2"
                               placeholder="Введите Обозначение/Наименование"
                               value="${(bid.getTitle())!""}">

                        <div class="d-flex justify-content-between mt-5 mb-2">
                            <button type="submit" class="btn btn-success">
                                Сохранить
                            </button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade " id="updateEmployeeModalId" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Список мастеров</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Закрыть"></button>
                </div>
                <div class="modal-body">
                    <select id="masterSelect" class="form-select">
                        <option value="">-- Выберите --</option>
                        <#list employeeMaster as emp>
                            <option value="${emp.getName()}">${emp.getName()}</option>
                        </#list>
                    </select>
                    <div class="mt-3 text-end">
                        <button class="btn btn-success" id="updateUserModalSaveBtn"
                                data-req="${bid.getId()}"
                        >Сохранить
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

</#if>

<div class="modal fade" id="photoModal" tabindex="-1" aria-labelledby="photoModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg custom-modal-width">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="photoModalLabel">Прикрепленные фото</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Закрыть"></button>
            </div>
            <div class="modal-body">
                <div id="photoContainer" class="d-flex flex-wrap gap-3"></div>
                <#if role?? && role == "OTK">
                    <input type="file" id="photoInput" accept="image/*" multiple style="display: none;">
                <#else>
                    <input type="file" id="photoInput" accept="image/*" multiple capture="environment"
                           style="display: none;">
                </#if>
                <img id="fullPhoto" src="" style="max-width:100%; max-height:80vh; margin-top:10px; display: none">
            </div>
            <div class="modal-footer">
                <#if createForm!false >
                    <button type="button" id="addPhotoBtn" class="btn btn-primary">Добавить фото</button>
                <#else>
                    <#if bid.getStatus().getName() != "Забракована">
                        <button type="button" id="addPhotoBtn" class="btn btn-primary">Добавить фото</button>
                    </#if>
                </#if>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="addInconsistenciesModal" tabindex="-1" aria-labelledby="addInconsistenciesModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg custom-modal-width">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addInconsistenciesModalLabel">Добавление несоответствия</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Закрыть"></button>
            </div>
            <form id="inconsistenciesForm" action="/api/inconsistencies/create" method="post">
                <div class="modal-body">
                    <div class="text-center">
                        <label for="addInconsistencies">Название
                            <input type="text" class="form-control" name="name" id="inconsistenciesNameId" required>
                        </label>
                    </div>
                    <div class="mt-2 text-center">
                        <label for="addInconsistencies">Тип контроля
                            <input type="text" class="form-control text-center" name="controlType"
                                   id="inconsistenciesControlTypeId" value="ВИК">
                        </label>
                    </div>
                    <div class="mt-3 text-center">
                        <button type="submit" id="addInconsistenciesBtn" class="btn btn-success">Сохранить</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade " id="universalModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Выберите элемент</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Закрыть"></button>
            </div>
            <div class="modal-body">
                <input type="text" class="form-control mb-2" id="modalSearchInput" placeholder="Поиск...">
                <ul class="list-group modal-list" id="modalItemList">

                </ul>
                <div id="modalLoading" class="text-center my-3 d-none">
                    <div class="spinner-border text-primary" role="status"></div>
                </div>
                <div id="modalError" class="text-danger text-center d-none">Ошибка загрузки данных</div>
            </div>
            <div class="modal-footer d-none" id="modalFooter">
                <button type="button" class="btn btn-primary" id="modalSaveBtn">Сохранить</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="viewRequestLogs" tabindex="-1" aria-labelledby="viewRequestLogsLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable modal-xl h-auto" style="max-height: 95vh;">
        <div class="modal-content rounded-4 h-100">
            <div class="modal-header bg-light rounded-top-4">
                <h5 class="modal-title fs-5 fw-semibold w-100 text-center">История изменений</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" id="closeId"
                        aria-label="Закрыть"></button>
            </div>
            <div class="modal-body p-0 d-flex flex-column">
                <div class="container-fluid flex-shrink-0">
                    <div class="row gx-3 py-2 bg-light fw-semibold small border-bottom">
                        <div class="col-2 text-center">Дата</div>
                        <div class="col-1 text-center">Действие</div>
                        <div class="col-2 text-center">Пользователь</div>
                        <div class="col-1 text-center">Версия</div>
                        <div class="col-6 text-center">Изменения</div>
                    </div>
                </div>
                <div id="logsBody" class="flex-grow-1 overflow-auto" style="max-height: 70vh;">
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Уведомления -->
<div id="notification-toast-container"></div>

<script>
    function validateOrderNumber(input) {
        const value = input.value;
        const errorElement = document.getElementById('orderError');

        if (value && !value.startsWith('20')) {
            input.classList.add('is-invalid');
            errorElement.style.display = 'block';
            return false;
        } else {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');
            errorElement.style.display = 'none';
            return true;
        }
    }
</script>
<script>
    let isSubmitting = false;

    const createRequestForm = document.getElementById('createRequestForm');
    if (createRequestForm) {
        createRequestForm.addEventListener('submit', function (e) {

            if (isSubmitting) {
                e.preventDefault();
                e.stopPropagation();
                return;
            }

            if (!this.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
                this.classList.add('was-validated');
                return;
            }

            isSubmitting = true;
            setButtonState(true);

        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        resetSubmitButton();
    });

    window.addEventListener('pageshow', function (event) {
        if (event.persisted) {
            resetSubmitButton();
        }
    });

    function setButtonState(submitting) {
        const submitBtn = document.getElementById('submitBtn');
        if (!submitBtn) {
            return;
        }
        const btnText = document.getElementById('btnText');
        const btnSpinner = document.getElementById('btnSpinner');

        if (submitting) {
            submitBtn.disabled = true;
            btnText.textContent = 'Создание заявки...';
            btnSpinner.classList.remove('d-none');
            submitBtn.classList.add('btn-processing');
        } else {
            submitBtn.disabled = false;
            btnText.textContent = 'Создать заявку';
            btnSpinner.classList.add('d-none');
            submitBtn.classList.remove('btn-processing');
        }
    }

    function resetSubmitButton() {
        isSubmitting = false;
        setButtonState(false);
    }

    const submitBtn = document.getElementById('submitBtn');
    if (submitBtn) {
        submitBtn.addEventListener('click', function (e) {
            if (isSubmitting) {
                e.preventDefault();
                e.stopPropagation();
            }
        });
    }

    window.addEventListener('load', function () {
        const errorMessages = document.querySelector('.alert-danger, .error-message');
        if (errorMessages) {
            resetSubmitButton();
        }
    });
</script>


<script>
    function toggleValue(button) {
        button.value = button.value === 'true' ? 'false' : 'true';

        if (button.value === 'true') {
            button.classList.replace('btn-primary', 'btn-success');
            button.textContent = '✅ Уведомлено';
        } else {
            button.classList.replace('btn-success', 'btn-primary');
            button.textContent = 'Уведомить';
        }
        return false;
    }
</script>

<script src="/js/jquery.min.js"></script>
<script src="/js/bootstrap.bundle.js"></script>
<script src="/js/bootstrap-table.min.js"></script>
<script src="/js/sweetalert2.min.js"></script>
<script src="/js/sockjs.min.js"></script>
<script src="/js/stomp.min.js"></script>

<script>
    const bidType = "${type}";
    const viewForm = <#if viewForm??>${viewForm?c}<#else>false</#if>;
    <#if viewForm?? && viewForm && bid.typeRequest == "otk" && bid.getSubDivision().getName() != 'workShop1'>
    <#if bid.getQty()??>
    const bidQty = ${bid.getQty()?c};
    </#if>
    </#if>
</script>

<script src="/js/notifications.js"></script>
<script src="/js/script.js"></script>
<script src="/js/list.js"></script>

<script>
    $(document).ready(function () {
        var timeoutId;
        var lastSearchTerm = '';

        function loadReasons(searchTerm) {
            if (!searchTerm || searchTerm === lastSearchTerm) {
                return;
            }
            lastSearchTerm = searchTerm;
            $.ajax({
                url: '/api/reasons-wr',
                data: {search: searchTerm},
                dataType: 'json',
                success: function (data) {
                    var datalist = $('#reasonsList');
                    datalist.empty();
                    data.forEach(function (reason) {
                        $('<option>').val(reason.text).appendTo(datalist);
                    });
                }
            });
        }

        $('#reasonsJson').on('input', function () {
            var searchTerm = $(this).val().trim();
            if (searchTerm.length < 5) {
                $('#reasonsList').empty();
                lastSearchTerm = '';
                return;
            }
            clearTimeout(timeoutId);
            timeoutId = setTimeout(function () {
                loadReasons(searchTerm);
            }, 700);
        });
    });
</script>

</body>
</html>