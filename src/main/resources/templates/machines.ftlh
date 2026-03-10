<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Управление станками</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/modern.css">
</head>
<body>

<div class="container mt-4">
    <h1 class="page-title fade-in-up">Управление станками</h1>

    <div class="card fade-in-up" style="animation-delay: 100ms;">
        <div class="card-header d-flex justify-content-between align-items-center">
            <span>Список станков</span>
            <a href="/machines/new" class="btn btn-primary icon-text">
                <i class="bi bi-plus-circle"></i>
                <span>Создать новый станок</span>
            </a>
            <a href="/menu" class="btn btn-primary icon-text">
                <i class="bi bi-menu-button-wide-fill"></i>
                <span>Перейти в меню</span>
            </a>
        </div>
        <div class="card-body">
            <div class="table-responsive d-none d-lg-block">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th scope="col">Имя</th>
                            <th scope="col">Серийный номер</th>
                            <th scope="col">Описание</th>
                            <th scope="col" class="text-end">Действия</th>
                        </tr>
                    </thead>
                    <tbody id="machines-table-body">

                    </tbody>
                </table>
            </div>
            <div id="machines-card-view" class="d-lg-none">

            </div>
            <div id="loading-indicator" class="text-center" style="display: none;">
                <div class="spinner-border" role="status">
                    <span class="visually-hidden">Загрузка...</span>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="/js/machines.js"></script>
</body>
</html>
