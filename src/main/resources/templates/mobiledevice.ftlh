<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Страница недоступна</title>
    <style>
        /* Основные стили */
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background-color: #f0f0f0;
            font-family: Arial, sans-serif;
            text-align: center;
        }

        .container {
            max-width: 400px;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .phone-icon {
            font-size: 64px;
            color: #ff4d4d;
            margin-bottom: 20px;
        }

        .message {
            font-size: 18px;
            color: #333;
        }
        /* Базовые стили для ссылок */
        a {
            color: #007bff; /* Основной цвет ссылки */
            text-decoration: none; /* Убираем подчеркивание */
            font-weight: 500; /* Полужирный шрифт */
            transition: all 0.3s ease; /* Плавный переход для всех свойств */
            position: relative; /* Для псевдоэлементов */
        }

        /* Стили для hover-эффекта */
        a:hover {
            color: #0056b3; /* Цвет при наведении */
        }

        /* Подчеркивание при наведении */
        a::after {
            content: '';
            position: absolute;
            left: 0;
            bottom: -2px;
            width: 100%;
            height: 2px;
            background-color: #007bff; /* Цвет подчеркивания */
            transform: scaleX(0); /* Начальное состояние: подчеркивание скрыто */
            transform-origin: right;
            transition: transform 0.3s ease; /* Плавное появление */
        }

        a:hover::after {
            transform: scaleX(1); /* Подчеркивание появляется */
            transform-origin: left;
        }

        /* Стили для активной ссылки */
        a:active {
            color: #004080; /* Цвет при нажатии */
        }
    </style>
</head>
<body>
<div class="container">
    <div class="phone-icon">
        📱
    </div>
    <!-- Сообщение -->
    <div class="message">
        Эта страница не доступна на мобильных устройствах.
        <br>
        <a href="/menu" style="flex: auto">Меню</a>
    </div>
</div>
</body>
</html>