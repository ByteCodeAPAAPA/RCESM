<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/stylesMain.css">
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/js/admin.js" defer></script>
    <title>Страница администрирования</title>
</head>
<body>
<div class="offcanvas offcanvas-start" data-bs-scroll="true" tabindex="-1" id="offcanvasWithBothOptions" aria-labelledby="offcanvasWithBothOptionsLabel">
    <div class="offcanvas-header">
        <h5 class="offcanvas-title" id="offcanvasWithBothOptionsLabel">Функции</h5>
        <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
    <div class="offcanvas-body">
        <div class="list-group" style="width: 200px;">
            <a href="/menu" class="list-group-item list-group-item-action btn btn-primary mb-2">Меню</a>
            <#if user.getRole() == 'ADMIN' || user.getRole() == 'CONTROL' || user.getRole() == 'EVENT'>
                <a href="/sgi" class="list-group-item list-group-item-action btn btn-primary">СГИ</a>
            </#if>
            <#if user.getRole() != 'CONTROL' && user.getRole() != 'EVENT'>
                <div class="dropdown mt-3">
                    <button class="btn btn-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                        Формы заявок
                    </button>
                    <ul class="dropdown-menu">
                        <#if user.getRole() == 'ADMIN' || user.getRole() == 'MASTER'>
                            <li><a class="dropdown-item" href="/requestslist/technologist">Заявки для ОГТ </a></li>
                            <li><a class="dropdown-item" href="/requestslist/constructor">Заявки для ОГК</a></li>
                            <li><a class="dropdown-item" href="/requestslist/otk">Заявки для ОТК</a></li>
                        <#elseif user.getRole() == 'TECHNOLOGIST'>
                            <li><a class="dropdown-item" href="requestslist/technologist">Заявки для ОГТ </a></li>
                        <#elseif user.getRole() == 'CONSTRUCTOR'>
                            <li><a class="dropdown-item" href="requestslist/constructor">Заявки для ОГК</a></li>
                        <#elseif user.getRole() == 'OTK'>
                            <li><a class="dropdown-item" href="requestslist/otk">Заявки для ОТК</a></li>
                        </#if>
                    </ul>
                </div>
            </#if>
        </div>
    </div>
</div>
<h1 class="text-center">
    Список пользователей
    <button class="btn" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasWithBothOptions" aria-controls="offcanvasWithBothOptions" style="background: none; border: none;">
        <i class="bi bi-list" style="color: black"></i>
    </button>
</h1>

<div class="container">
    <table class="table table-bordered" id="requestTable">
        <thead>
        <tr>
            <th>
                Сотрудник
                <button type="button" class="toggleInput">
                    <svg viewBox="0 0 24 24" width=16>
                        <path d="M10 18h4v-2h-4zM3 6v2h18V6zm3 7h12v-2H6z"/>
                    </svg>
                </button>
                <div class="inputContainer">
                    <input type="text" class="form-control" id="employee">
                </div>
            </th>
            <th>
                <button type="button" class="toggleInput">
                    <svg viewBox="0 0 24 24" width=16>
                        <path d="M10 18h4v-2h-4zM3 6v2h18V6zm3 7h12v-2H6z"/>
                    </svg>
                </button>
                <div class="inputContainer">
                    <input type="text" class="form-control" id="mlmNode">
                </div>
            </th>
            <th>
                Роль
                <button type="button" class="toggleInput">
                    <svg viewBox="0 0 24 24" width=16>
                        <path d="M10 18h4v-2h-4zM3 6v2h18V6zm3 7h12v-2H6z"/>
                    </svg>
                </button>
                <div class="inputContainer">
                    <input type="text" class="form-control" id="role">
                </div>
            </th>
            <th>
                ChatID
                <button type="button" class="toggleInput">
                    <svg viewBox="0 0 24 24" width=16>
                        <path d="M10 18h4v-2h-4zM3 6v2h18V6zm3 7h12v-2H6z"/>
                    </svg>
                </button>
                <div class="inputContainer">
                    <input type="text" class="form-control" id="chatId">
                </div>
            </th>
            <th> Действия </th>
        </tr>
        </thead>
        <tbody>
        <#list users as req>
            <tr id="req-${req.id}">
                <td>${req.name}</td>
                <td>${(req.mlmNode)!""}</td>
                <td>${req.role!""}</td>
                <td>${req.chatId!""}</td>
                <td>
                    <button type="button"
                            class="btn btn-primary d-none d-md-inline-block create-btn"
                            data-bs-toggle="modal"
                            data-bs-target="#createModal"
                            data-id="${req.id}"
                            data-user="${req.name}"
                    >
                        Редактировать
                    </button>
                    <button
                            type="button"
                            class="btn btn-danger delete-btn"
                            data-id="${req.id}"
                    >
                        Удалить
                    </button>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
    <div id="pagination" class="pagination mt-4"></div>
    <div class="text-center mt-3">
        <a class="btn btn-primary" href="/registration">Добавить нового пользователя</a>
    </div>
    <div class="text-center mt-3">
        <button class="btn btn-primary clear" id="clearFilter">Очистить фильтр</button>
    </div>
</div>

<div class="modal fade" id="createModal" tabindex="-1" aria-labelledby="createModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content ">
            <div class="modal-header">
                <h1 class="modal-title fs-5" id="createModalLabel">Редактирование пользователя</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Закрыть"></button>
            </div>
            <div class="modal-body">
                <div class="container">
                    <form name="editUser" action="/update" method="POST">
                        <input hidden="" type="text" class="form-control" id="userId" value="" name="id">
                        <div class="mb-3">
                            <label for="shop" class="d-flex justify-content-center form-label">Имя пользователя</label>
                            <input type="text" class="form-control" id="userNameId" name="userName" value="">
                        </div>
                        <div class="mb-3">
                            <label for="shop" class="d-flex justify-content-center form-label">Отдел</label>
                            <select id="mlmNodeId" name="mlmNodeName" class="d-flex justify-content-center form-control" required>
                                <#list mlmNodes as mlmNode>
                                    <option value="${mlmNode}">${mlmNode.getName()}</option>
                                </#list>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="shop" class="d-flex justify-content-center form-label">Статус</label>
                            <select id="activeId" name="active" class="form-control">
                                <option value="true">Активный</option>
                                <option value="false">Не активный</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="shop" class="d-flex justify-content-center form-label">Роль</label>
                            <select id="roleId" name="roleName" class="d-flex justify-content-center form-control" required>
                                <#list roles as role>
                                    <option value="${role}">${role}</option>
                                </#list>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="shop" class="d-flex justify-content-center form-label">ChatId</label>
                            <input type="number" class="form-control" id="employChatId" name="chatName" value="">
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary" id="saveId">Сохранить изменения</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>