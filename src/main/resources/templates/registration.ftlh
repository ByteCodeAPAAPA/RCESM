<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <title>Добавления пользователя</title>
</head>
<body>
<div class="container">
    <div class="row justify-content-center" style="height: 100vh;">
        <div class="col-md-6">
            <h2 class="text-center">Добавление нового пользователя</h2>
            <form action="/registration" method="post" class="mt-4">
                <div class="form-group">
                    <label for="name">Имя пользователя:</label>
                    <input type="text" class="form-control" id="name" name="username" required>
                </div>
                <div class="form-group">
                    <label for="password">Пароль:</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                <div class="form-group">
                    <label for="role">Роль:</label>
                    <select id="role" name="role" class="form-control" required>
                        <#list userRole as role>
                            <option value="${role}">${role}</option>
                        </#list>
                    </select>
                </div>
                <div class="form-group">
                    <label for="mlmnode">Цех:</label>
                    <select id="mlmnode" name="mlmNode" class="form-control" required>
                        <#list mlmNode as node>
                            <option value="${node.getName()}">${node.getName()}</option>
                        </#list>
                    </select>
                </div>
                <div class="form-group">
                    <label for="chatId">TelegramID:</label>
                    <input type="number" class="form-control" id="chatId" name="chatId">
                </div>
                <div class="d-flex justify-content-center">
                    <button type="submit" class="btn btn-primary">Добавить пользователя</button>
                </div>
            </form>
            <div class="text-center mt-3">
                <a class="btn btn-primary" href="/menu">Меню</a>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>