<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Список заявок</title>
    <link rel="stylesheet" href="/css/notification.css">
    <link rel="stylesheet" href="/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/list.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${springMacroRequestContext.contextPath}/webjars/bootstrap-table/1.24.1/src/bootstrap-table.css">
    <link rel="stylesheet" href="/css/stylesMain.css">
    <link rel="stylesheet" href="/css/requestlistmobile.css">
    <script src="/js/sockjs.min.js"></script>
    <script src="/js/stomp.min.js"></script>
    <script src="/js/notifications.js"></script>
</head>
<body>
<div id="toolbar" class="mb-2">
    <#switch typeRequest>
        <#case 'otk'>
            <#assign titleText = "Список заявок для ОТК">
            <#break>
        <#case 'technologist'>
            <#assign titleText = "Список заявок для ОГТ">
            <#break>
        <#default>
            <#assign titleText = "Список заявок для ОГК">
    </#switch>
    <span class="custom-title ml-3">
  ${titleText}
  <a class="btn btn-info ml-2" href="/menu" aria-label="Home">
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-house-fill"
         viewBox="0 0 16 16">
  <path d="M8.707 1.5a1 1 0 0 0-1.414 0L.646 8.146a.5.5 0 0 0 .708.708L8 2.207l6.646 6.647a.5.5 0 0 0 .708-.708L13 5.793V2.5a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1.293z"/>
  <path d="m8 3.293 6 6V13.5a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13.5V9.293z"/>
    </svg>
  </a>
</span>
</div>

<div id="table-container">
    <table id="table"
           data-toolbar="#toolbar"
           data-search="true"
           data-show-export="true"
           data-show-columns="true"
           data-pagination="true"
           data-advanced-search="true"
           data-id-table="advancedTable"
           data-show-toggle="true"
           data-locale="ru-RU"
           data-buttons-class="info"
           class="table">
        <thead>
        <tr>
            <th data-field="requestNumber" data-sortable="true">№</th>
            <th data-field="createdBy" data-sortable="true">Создал</th>
            <th data-field="employee" data-sortable="true">Ответственный</th>
            <th data-field="customerOrder" data-sortable="true">Заказа клиента</th>
            <th data-field="mlmNode" data-sortable="true">Цех</th>
            <th data-field="reason" data-sortable="true">Причина</th>
            <th data-field="item" data-sortable="true">Тип детали</th>
            <th data-field="status" data-sortable="true">Статус</th>
            <th data-field="createdDate" data-sortable="true">Дата создания</th>
            <th data-field="updatedDate" data-sortable="true">Дата изменения</th>
            <th data-field="updateUser" data-sortable="true">Изменил</th>
            <#if typeRequest == 'otk'>
                <th data-field="typeControl" data-sortable="true">Тип контроля</th>
                <th data-field="qtyRejected" data-sortable="true">Кол-во брака</th>
            </#if>
            <th data-field="log" data-searchable="false">Функции</th>
        </tr>
        </thead>
        <tbody>
        <#list requestsList as req>
            <#assign reqIndex = req_index>
            <#if req.getStatus().getName() == "Выполнен" || req.getStatus().getName() == "Закрыт">
            <tr class="table-success" data-request-number="${req.requestNumber}">
            <#elseif req.getStatus().getName() == "В работе">
                <tr class="table-warning" data-request-number="${req.requestNumber}">
            <#elseif req.getStatus().getName() == "Отменен" || req.getStatus().getName() == "Забракована">
                <tr class="table-danger" data-request-number="${req.requestNumber}">
            </#if>
            <td>${req.getRequestNumber()}</td>
            <td>${req.getCreatedBy().getName()}</td>
            <td>${(req.getEmployee().getName())!"Не назначен"}</td>
            <td>${(req.getCustomerOrder().getName())!"Нет"}</td>
            <td>${req.getSubDivision().getName()}</td>
            <#if typeRequest == 'otk'>
                <td>${(req.getReason_wr())!"-"}</td>
            <#else>
                <td>${(req.getReason().getName())!"-"}</td>
            </#if>
            <td>${(req.getItem().getName())!"-"}</td>
            <td>${req.getStatus().getName()}</td>
            <td>${formattedBidList[reqIndex]!"-"}</td>
            <td>${updateDateList[reqIndex]!"-"}</td>
            <td>${(req.getUpdatedBy().getName())!"-"}</td>
            <#if typeRequest == 'otk'>
                <td>${(req.getControl())!"Нет"}</td>
                <td>${(req.getQtyRejected()!"0")}</td>
            </#if>
            <td>
                <i class="history-icon bi bi-clock-history text-primary fs-4" data-id="${req.id}"></i>
                <#if req.getRequestNumber()??>
                    <a class="btn btn-info ml-3" href="/view/${req.getRequestNumber()?c}">Редактировать</a>
                </#if>
            </td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>

<div class="mobile-cards-container" id="mobileCards">
    <div class="mobile-header">
        <div class="mobile-title">
            <#switch typeRequest>
                <#case 'otk'>
                    <#assign titleText = "Список заявок для ОТК">
                    <#break>
                <#case 'technologist'>
                    <#assign titleText = "Список заявок для ОГТ">
                    <#break>
                <#default>
                    <#assign titleText = "Список заявок для ОГК">
            </#switch>
            ${titleText}
            <a class="btn btn-info home-btn" href="/menu" aria-label="Home">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor"
                     class="bi bi-house-fill"
                     viewBox="0 0 16 16">
                    <path d="M8.707 1.5a1 1 0 0 0-1.414 0L.646 8.146a.5.5 0 0 0 .708.708L8 2.207l6.646 6.647a.5.5 0 0 0 .708-.708L13 5.793V2.5a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1.293z"/>
                    <path d="m8 3.293 6 6V13.5a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13.5V9.293z"/>
                </svg>
            </a>
        </div>
        <div class="mobile-search">
            <i class="bi bi-search"></i>
            <input type="text" id="mobileSearchInput" placeholder="Поиск по номеру и ответственному">
        </div>
    </div>

    <#list requestsList as req>
        <#assign reqIndex = req_index>
        <#if req.getStatus().getName() == "Выполнен" || req.getStatus().getName() == "Закрыт">
            <#assign cardClass = "card-success">
            <#assign badgeClass = "success">
        <#elseif req.getStatus().getName() == "В работе">
            <#assign cardClass = "card-warning">
            <#assign badgeClass = "warning">
        <#elseif req.getStatus().getName() == "Новый">
            <#assign cardClass = "card-secondary">
            <#assign badgeClass = "secondary">
        <#else>
            <#assign cardClass = "card-danger">
            <#assign badgeClass = "danger">
        </#if>

        <div class="request-card ${cardClass}" data-request-number="${req.requestNumber}">
            <div class="card-header">
                <span class="request-number">№ ${req.getRequestNumber()?c}</span>
                <span class="status-badge ${badgeClass}">${req.getStatus().getName()}</span>
            </div>
            <div class="card-body">
                <div class="card-row">
                    <span class="card-label">Создал:</span>
                    <span class="card-value">${req.getCreatedBy().getName()}</span>
                </div>
                <div class="card-row">
                    <span class="card-label">Ответственный:</span>
                    <span class="card-value">${(req.getEmployee().getName())!"Не назначен"}</span>
                </div>
                <div class="card-row">
                    <span class="card-label">Цех:</span>
                    <span class="card-value">${req.getSubDivision().getName()}</span>
                </div>
                <div class="card-row">
                    <span class="card-label">Наименование:</span>
                    <span class="card-value">${(req.getTitle())!"Не назначен"}</span>
                </div>
                <div class="card-row">
                    <span class="card-label">Дата создания:</span>
                    <span class="card-value">${formattedBidList[reqIndex]!"-"}</span>
                </div>
            </div>
            <div class="card-details" id="details-${req.id}">
                <div class="card-row">
                    <span class="card-label">Заказ клиента:</span>
                    <span class="card-value">${(req.getCustomerOrder().getName())!"Нет"}</span>
                </div>
                <#if typeRequest == 'otk'>
                    <div class="card-row">
                        <span class="card-label">Причина:</span>
                        <span class="card-value">${(req.getReason_wr())!"-"}</span>
                    </div>
                    <div class="card-row">
                        <span class="card-label">Тип контроля:</span>
                        <span class="card-value">${(req.getControl())!"Нет"}</span>
                    </div>
                <#else>
                    <div class="card-row">
                        <span class="card-label">Причина:</span>
                        <span class="card-value">${(req.getReason().getName())!"-"}</span>
                    </div>
                </#if>
                <div class="card-row">
                    <span class="card-label">Тип детали:</span>
                    <span class="card-value">${(req.getItem().getName())!"-"}</span>
                </div>
                <div class="card-row">
                    <span class="card-label">Дата изменения:</span>
                    <span class="card-value">${updateDateList[reqIndex]!"-"}</span>
                </div>
                <div class="card-row">
                    <span class="card-label">Изменил:</span>
                    <span class="card-value">${(req.getUpdatedBy().getName())!"-"}</span>
                </div>
            </div>
            <div class="card-footer">
                <i class="history-icon bi bi-clock-history" data-id="${req.id}"></i>
                <#if req.getRequestNumber()??>
                    <a class="btn btn-info btn-edit" href="/view/${req.getRequestNumber()?c}">
                        <i class="bi bi-pencil"></i>
                    </a>
                </#if>
            </div>
        </div>
    </#list>

    <div class="mobile-pagination">
        <button class="btn btn-secondary" id="prevPage" disabled>
            <i class="bi bi-chevron-left"></i> Назад
        </button>
        <span class="page-info" id="pageInfo">Страница 1 из 1</span>
        <button class="btn btn-secondary" id="nextPage" disabled>
            Далее <i class="bi bi-chevron-right"></i>
        </button>
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

<div id="notification-toast-container"></div>
<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Popper.js -->
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>
<!-- Bootstrap Table JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>
<!-- Bootstrap Table JS Locale - RU-->
<script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/locale/bootstrap-table-ru-RU.min.js"></script>
<!-- Auto-refresh extension -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/extensions/auto-refresh/bootstrap-table-auto-refresh.min.js"></script>
<!-- Печать в JSON и MS-Exel -->
<script src="https://cdn.jsdelivr.net/npm/tableexport.jquery.plugin@1.29.0/tableExport.min.js"></script>
<!-- Так же для поддержки печати -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/extensions/export/bootstrap-table-export.min.js"></script>
<!-- Фильтр через модальное окно -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/extensions/toolbar/bootstrap-table-toolbar.min.js"></script>
<!-- Настройка Bootstrap Table JS, получение истории изменения и мобильная адаптация -->
<script src="/js/list.js"></script>


</body>
</html>
