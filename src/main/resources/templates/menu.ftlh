<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Меню пользователя</title>
    <link rel="stylesheet" href="css/menu.css">
    <link rel="stylesheet" href="css/notification.css">
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <script>
        const dailyCounts = [<#list dailyCounts as item>${item}<#if item_has_next>, </#if></#list>];

        const dailyCountConstructor = [<#list dailyCountsConstructor as item>${item}<#if item_has_next>, </#if></#list>];

        const dailyCountOtk = [<#list dailyCountOtk as item>${item}<#if item_has_next>, </#if></#list>];

        const dailyCountTechnologist = [<#list dailyCountTechnologist as item>${item}<#if item_has_next>, </#if></#list>];

        const chartDates = [
            <#list chartDates as date>"${date}"<#if date_has_next>, </#if></#list>
        ];

        const dailyApplicationsConstructor = [
            <#list dailyApplicationsConstructor as dayApps>
            [
                <#list dayApps as app>
                {
                    number: "${app.number}",
                    id: "${app.id}",
                    title: "${app.title?js_string}",
                    status: "${(app.status)!""?js_string}"
                }<#if app_has_next>, </#if>
                </#list>
            ]<#if dayApps_has_next>, </#if>
            </#list>
        ];

        const dailyApplicationsOtk = [
            <#list dailyApplicationsOtk as dayApps>
            [
                <#list dayApps as app>
                {
                    number: "${app.number}",
                    id: "${app.id}",
                    title: "${app.title?js_string}",
                    status: "${(app.status)!""?js_string}"
                }<#if app_has_next>, </#if>
                </#list>
            ]<#if dayApps_has_next>, </#if>
            </#list>
        ];

        const dailyApplicationsTechnologist = [
            <#list dailyApplicationsTechnologist as dayApps>
            [
                <#list dayApps as app>
                {
                    number: "${app.number}",
                    id: "${app.id}",
                    title: "${app.title?js_string}",
                    status: "${(app.status)!""?js_string}"
                }<#if app_has_next>, </#if>
                </#list>
            ]<#if dayApps_has_next>, </#if>
            </#list>
        ];
    </script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <script src="/js/menu.js"></script>
    <script src="/js/sockjs.min.js"></script>
    <script src="/js/stomp.min.js"></script>
    <script src="/js/notifications.js"></script>
</head>
<body>
<div class="container mb-1" id="menuContainer">
    <div class="row">
        <h1 class="text-center mb-4">Меню пользователя</h1>
        <div class="card">
            <div class="text-center card-body">
                <div class="user-info mb-4">
                    <label class="fw-bold">Имя пользователя:</label>
                    <span>${user.getName()}</span><br>
                </div>
                <nav class="mb-4">
                    <ul class="nav justify-content-center">
                        <li class="nav-item">
                            <#if user.getRole() == 'ADMIN'>
                                <a class="btn btn-primary bottom-margin" id="sgiPageButton" href="/sgi">СГИ</a>
                                <a class="btn btn-primary bottom-margin" id="usersPageButton"
                                   href="/admin">Пользователи</a>
                                <a class="btn btn-primary bottom-margin" id="sTTPageButton"
                                   href="/shift-task/task">ССЗ</a>
                                <a class="btn btn-primary bottom-margin" id="reportPageButton" href="report">Дерево
                                    ЗК</a>
                                <a class="btn btn-primary bottom-margin" id="CreatebidPageButton" href="/createBid">Создать
                                    заявку</a>
                                <a class="btn btn-primary bottom-margin" id="equipmentPageButton"
                                   href="http://192.168.30.80:2001/">Оснастка</a>
                                <a class="btn btn-primary bottom-margin" id="inspectionPageButton" href="/inspection">Чек-листы</a>
                                <a class="btn btn-primary bottom-margin" href="/machines">Станки</a>
                            <#elseif user.getRole() == 'MASTER'>
                                <a class="btn btn-primary" id="BidPageButton" href="/createBid">Создать заявку</a>
                            <#elseif user.getRole() == 'TECHNOLOGIST'>
                                <a class="btn btn-primary bottom-margin" id="equipmentPageButton"
                                   href="http://192.168.30.80:2001/">Оснастка</a>
                            </#if>
                            <#if user.getRole() == 'CONTROL' || user.getRole() == 'EVENT'>
                                <a class="btn btn-primary bottom-margin" id="equipmentPageButton"
                                   href="http://192.168.30.80:2001/">Оснастка</a>
                                <a class="btn btn-primary bottom-margin" id="inspectionPageButton" href="/inspection">Чек-листы</a>
                                <a class="btn btn-primary" id="sgiPageButton" href="/sgi">СГИ</a>
                                <a class="btn btn-primary" id="spePageButton" href="/spe">СПЕ</a>
                            </#if>
                            <a class="btn btn-primary " id="logoutButton" href="/logout">Выйти</a>
                        </li>
                    </ul>
                </nav>
                <#if user.getRole() == 'MASTER'>
                <#--                    <button type="button" class="btn btn-primary bottom-margin" data-bs-toggle="modal"-->
                <#--                            data-bs-target="#exampleModal1">-->
                <#--                        Созданные заявки для ОГТ-->
                <#--                    </button>-->
                <#--                    <button type="button" class="btn btn-primary bottom-margin" data-bs-toggle="modal"-->
                <#--                            data-bs-target="#exampleModal2">-->
                <#--                        Созданные заявки для ОГК-->
                <#--                    </button>-->
                    <button type="button" class="btn btn-primary bottom-margin" data-bs-toggle="modal"
                            data-bs-target="#exampleModal3">
                        Созданные заявки для ОТК
                    </button>
                </#if>
                <#if user.getRole() == 'ADMIN' || user.getRole() == 'CONTROL'>
                    <button id="graphButton" class="btn btn-primary mb-4">Показать график</button>
                    <a class="btn btn-primary mb-4" id="loggingButton" href="/logs">Логирование ошибок</a>
                </#if>
            </div>
        </div>
    </div>
</div>
<div class="container mb-4 d-flex justify-content-center">
    <#--    <div>-->
    <#--        <#if user.getRole() == 'ADMIN' || user.getRole() == 'MASTER' || user.getRole() == 'TECHNOLOGIST' || user.getRole() == 'CONTROL'>-->
    <#--            <div class="md-4">-->
    <#--                <div class="card text-center">-->
    <#--                    <div class="card-body">-->
    <#--                        <h5 class="card-title">Список заявок для ОГТ</h5>-->
    <#--                        <p class="card-text">-->
    <#--                            Количество заявок:${qtuRequestTechnologist}<br>-->
    <#--                        </p>-->
    <#--                        <a href="requestslist/technologist" id="requestListOtk" class="btn btn-primary">Просмотр-->
    <#--                            заявок</a>-->
    <#--                    </div>-->
    <#--                </div>-->
    <#--            </div>-->
    <#--        </#if>-->
    <#--    </div>-->
    <div>
        <#if user.getRole() == 'ADMIN' || user.getRole() == 'MASTER' || user.getRole() == 'OTK' || user.getRole() == 'CONTROL'>
            <div class="md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">Список заявок для ОТК</h5>
                        <p class="card-text">
                            Количество заявок:${qtuRequestsOtk}<br>
                        </p>
                        <a href="requestslist/otk" class="btn btn-primary">Просмотр заявок</a>
                    </div>
                </div>
            </div>
        </#if>
    </div>
</div>
<div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Заявки</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="modalDataValue" name="type"/>
                <table class="table table-hover vertical-align: middle">
                    <thead>
                    <tr class="taq">
                        <th>#</th>
                        <th>ЗК</th>
                        <th>Статус</th>
                        <th>Просмотр задания</th>
                    </tr>
                    </thead>
                    <tbody id="requestsTableBody">
                    <#list requests as request>
                        <tr>
                            <td>${request.requestNumber}</td>
                            <td>${(request.customerOrder.getName())!"Не указано"}</td>
                            <td>${request.status.getName()}</td>
                            <td>
                                <#if request.getRequestNumber()??>
                                    <a href="view/${request.getRequestNumber()?c}">Просмотр</a>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="exampleModal1" tabindex="-1" aria-labelledby="exampleModal1Label" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModal1Label">Заявки</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="modalDataValue" name="type"/>
                <table class="table table-hover vertical-align: middle">
                    <thead>
                    <tr class="taq">
                        <th>#</th>
                        <th>ЗК</th>
                        <th>Статус</th>
                        <th>Просмотр задания</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list requestsMaster['technologist'] as request>
                        <tr data-type="technologist">
                            <td>${request.requestNumber}</td>
                            <td>${(request.customerOrder.getName())!"Не указано"}</td>
                            <td>${request.status.getName()}</td>
                            <td>
                                <#if request.getRequestNumber()??>
                                    <a href="view/${request.getRequestNumber()?c}">Просмотр</a>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="exampleModal2" tabindex="-1" aria-labelledby="exampleModal2Label" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModal2Label">Заявки</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="modalDataValue" name="type"/>
                <table class="table table-hover vertical-align: middle">
                    <thead>
                    <tr class="taq">
                        <th>#</th>
                        <th>ЗК</th>
                        <th>Статус</th>
                        <th>Просмотр задания</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list requestsMaster['constructor'] as request>
                        <tr data-type="constructor">
                            <td>${request.requestNumber}</td>
                            <td>${(request.customerOrder.getName())!"Не указано"}</td>
                            <td>${request.status.getName()}</td>
                            <td>
                                <#if request.getRequestNumber()??>
                                    <a href="view/${request.getRequestNumber()?c}">Просмотр</a>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="exampleModal3" tabindex="-1" aria-labelledby="exampleModal3Label" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModal3Label">Заявки</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="modalDataValue" name="type"/>
                <table class="table table-hover vertical-align: middle">
                    <thead>
                    <tr class="taq">
                        <th>#</th>
                        <th>ЗК</th>
                        <th>Статус</th>
                        <th>Просмотр задания</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list requestsMaster['otk'] as request>
                        <tr data-type="otk">
                            <td>${request.requestNumber}</td>
                            <td>${(request.customerOrder.getName())!"Не указано"}</td>
                            <td>${request.status.getName()}</td>
                            <td>  <#if request.getRequestNumber()??>
                                    <a href="view/${request.getRequestNumber()?c}">Просмотр</a>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<#if rejectedBid?size != 0>
    <#if user.getRole() == 'MASTER'>
        <div class="btn-counter-container pulse-div">
            <button class="btn btn-danger mt-1 mb-1 pulse-counter" type="button" data-bs-toggle="collapse"
                    data-bs-target="#tableCollapse" aria-expanded="false" aria-controls="tableCollapse">
                Просмотр забракованных заявок
            </button>
            <div class="pulse-counter mobile-counter" title="Количество забракованной продукции">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16" fill="currentColor"
                     aria-hidden="true">
                    <path d="M8 1a7 7 0 1 0 0 14A7 7 0 0 0 8 1zM7.002 5a1 1 0 1 1 2 0v4a1 1 0 1 1-2 0V5zm.998 7a1.25 1.25 0 1 1 0-2.5 1.25 1.25 0 0 1 0 2.5z"/>
                </svg>
                <span class="counter-text">Кол-во: ${rejectedBid?size}</span>
            </div>
        </div>
    </#if>
</#if>
<#if requests?size != 0>
    <#if user.getRole() == 'OTK'>
        <div class="card">
            <div class="btn-counter-container">
                <button class="btn btn-primary btn-sm-mobile" type="button" data-bs-toggle="collapse"
                        data-bs-target="#tableCollapseOtk" aria-expanded="false" aria-controls="tableCollapseOtk">
                    Просмотр заявок
                </button>
                <div class="pulse-counter-otk mobile-counter" title="Количество заявок для пользователя">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16"
                         fill="currentColor" aria-hidden="true">
                        <path d="M8 1a7 7 0 1 0 0 14A7 7 0 0 0 8 1zM7.002 5a1 1 0 1 1 2 0v4a1 1 0 1 1-2 0V5zm.998 7a1.25 1.25 0 1 1 0-2.5 1.25 1.25 0 0 1 0 2.5z"/>
                    </svg>
                    <span class="counter-text">Кол-во: ${requests?size}</span>
                </div>
            </div>
        </div>
    <#elseif user.getRole() == 'TECHNOLOGIST'>
        <div class="card">
            <div class="btn-counter-container">
                <button class="btn btn-primary btn-sm-mobile" type="button" data-bs-toggle="collapse"
                        data-bs-target="#tableCollapseOtk" aria-expanded="false" aria-controls="tableCollapseOtk">
                    Просмотр заявок
                </button>
                <div class="pulse-counter-otk mobile-counter" title="Количество заявок для пользователя">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16"
                         fill="currentColor" aria-hidden="true">
                        <path d="M8 1a7 7 0 1 0 0 14A7 7 0 0 0 8 1zM7.002 5a1 1 0 1 1 2 0v4a1 1 0 1 1-2 0V5zm.998 7a1.25 1.25 0 1 1 0-2.5 1.25 1.25 0 0 1 0 2.5z"/>
                    </svg>
                    <span class="counter-text">Кол-во: ${requests?size}</span>
                </div>
            </div>
        </div>
    </#if>
</#if>
<div class="collapse" id="tableCollapse">
    <div class="card">
        <h5 class="card-title text-center mt-3">Забракованная продукция: ${user.getSubDivision().getName()}</h5>
        <div class="table-responsive">
            <table class="table table-striped table-bordered mt-3 mb-3 d-none d-md-table">
                <thead>
                <tr>
                    <th class="text-center">№</th>
                    <th class="text-center">Обозначение/Наименование</th>
                    <th class="text-center">ЗК</th>
                    <th class="text-center">Дата</th>
                    <th class="text-center">Действие</th>
                </tr>
                </thead>
                <tbody>
                <#list rejectedBid as reject>
                    <#assign rejIn = reject_index>
                    <tr>
                        <td class="text-center">${reject.getRequestNumber()}</td>
                        <td class="text-center">${(reject.getTitle()!"")}</td>
                        <td class="text-center">${(reject.getCustomerOrder().getName())!"Не указано"}</td>
                        <td class="text-center">${(rejectedDate[rejIn])!""}</td>
                        <td class="text-center">  <#if reject.getRequestNumber()??>
                                <a href="view/${reject.getRequestNumber()?c}" class="btn btn-info btn-sm">Перейти</a>
                            </#if>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>

            <div class="d-md-none mobile-table-container">
                <#list rejectedBid as reject>
                    <div class="mobile-table-item card mb-2">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-6"><strong>№:</strong></div>
                                <div class="col-6">${reject.getRequestNumber()}</div>
                            </div>
                            <div class="row">
                                <div class="col-12"><strong>Наименование:</strong></div>
                                <div class="col-12">${reject.getTitle()}</div>
                            </div>
                            <div class="row">
                                <div class="col-6"><strong>ЗК:</strong></div>
                                <div class="col-6">${(reject.getCustomerOrder().getName())!"Не указано"}</div>
                            </div>
                            <div class="row">
                                <div class="col-6"><strong>Дата:</strong></div>
                                <div class="col-6">${(rejectedDate[rejIn])!""}</div>
                            </div>
                            <div class="row mt-2">
                                <div class="col-12">

                                </div>
                                <#if reject.getRequestNumber()??>
                                    <a href="view/${reject.getRequestNumber()?c}" class="btn btn-info btn-sm w-100">Перейти</a>
                                </#if>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>
        </div>
        <a href="/report/print/bid" class="btn btn-primary">Печать отчета: ${user.getSubDivision().getName()}</a>
    </div>
</div>
<div class="collapse" id="tableCollapseOtk">
    <div class="card">
        <h5 class="card-title text-center mt-3">Заявки ожидающие принятия в работу</h5>
        <div class="table-responsive">
            <table class="table table-striped table-bordered mt-3 mb-3 d-none d-md-table">
                <thead>
                <tr>
                    <th class="text-center">№</th>
                    <th class="text-center">Обозначение/Наименование</th>
                    <th class="text-center">ЗК</th>
                    <th class="text-center">Дата</th>
                    <th class="text-center">Причина</th>
                    <th class="text-center">Действие</th>
                </tr>
                </thead>
                <tbody>
                <#list requests as bid>
                    <#assign req = bid_index>
                    <tr>
                        <td class="text-center">${bid.getRequestNumber()}</td>
                        <td class="text-center">${(bid.getTitle()!"Не указано")}</td>
                        <td class="text-center">${(bid.getCustomerOrder().getName())!"Не указано"}</td>
                        <td class="text-center">${(requestsDate[req])!""}</td>
                        <#if bid.getTypeRequest().name() == 'otk'>
                            <td class="text-center">${(bid.getReason_wr())!"Не указана"}</td>
                        <#else >
                            <td class="text-center">${(bid.getReason().getName())!"Не указана"}</td>
                        </#if>
                        <#if bid.getRequestNumber()??>
                            <td class="text-center"><a href="view/${bid.getRequestNumber()?c}"
                                                       class="btn btn-info btn-sm">Перейти</a></td>
                        </#if>

                    </tr>
                </#list>
                </tbody>
            </table>

            <div class="d-md-none mobile-table-container">
                <#list requests as bid>
                    <div class="mobile-table-item card mb-2">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-6"><strong>№:</strong></div>
                                <div class="col-6">${bid.getRequestNumber()}</div>
                            </div>
                            <div class="row">
                                <div class="col-12"><strong>Наименование:</strong></div>
                                <div class="col-12">${(bid.getTitle()!"Не указано")}</div>
                            </div>
                            <div class="row">
                                <div class="col-6"><strong>ЗК:</strong></div>
                                <div class="col-6">${(bid.getCustomerOrder().getName())!"Не указано"}</div>
                            </div>
                            <div class="row">
                                <div class="col-6"><strong>Дата:</strong></div>
                                <div class="col-6">${(requestsDate[req])!""}</div>
                            </div>
                            <div class="row">
                                <div class="col-12"><strong>Причина:</strong></div>
                                <div class="col-12">${(bid.getReason_wr()!"Не указана")}</div>
                            </div>
                            <div class="row mt-2">
                                <div class="col-12">
                                    <#if bid.getRequestNumber()??>
                                        <a class="btn btn-info ml-3" href="/view/${bid.getRequestNumber()?c}">Редактировать</a>
                                    </#if>
                                </div>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>
        </div>
    </div>
</div>
<div class="container mt-5" id="chartContainer">
    <h1 class="text-center" id="chartTitle" style="display: none">График заявок</h1>
    <canvas id="myChart" width="400" height="200" style="display: none;"></canvas>
</div>
<div id="notification-toast-container"></div>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="/js/bootstrap.bundle.js"></script>
</body>
</html>