<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Успех</title>
    <style>
        /* Общие стили */
        body, html {
            margin: 0;
            padding: 0;
            height: 100%;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #f5f7fa, #c3cfe2);
        }

        .container {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            height: 100%;
            text-align: center;
        }

        /* Стиль для зеленой галочки */
        .checkmark {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background: linear-gradient(135deg, #4CAF50, #45a049);
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
            animation: pop 0.6s ease-in-out;
        }
        @keyframes pop {
            0% {
                transform: scale(0);
            }
            50% {
                transform: scale(1.2);
            }
            100% {
                transform: scale(1);
            }
        }
        .checkmark::after {
            content: "✓";
            font-size: 70px;
            color: white;
            font-weight: bold;
        }

        /* Стиль для текста сообщения */
        .message {
            font-size: 26px;
            color: #333;
            background: white;
            padding: 20px 40px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            max-width: 90%;
            margin: 0 auto;
        }

        /* Стиль для номера заявки */
        .message span {
            font-weight: bold;
            color: #4CAF50;
        }

        /* Обертка для ссылок */
        .links-container {
            display: flex;
            gap: 20px; /* расстояние между кнопками */
            margin-top: 20px;
            flex-wrap: wrap; /* перенос при необходимости */
            justify-content: center;
        }

        /* Стиль для ссылок */
        .links-container a {
            display: inline-block;
            font-size: 18px;
            padding: 10px 20px;
            background: linear-gradient(135deg, #4CAF50, #45a049);
            color: white;
            text-decoration: none;
            border-radius: 25px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        /* Эффект при наведении */
        .links-container a:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
        }

        /* Адаптация для мобильных устройств */
        @media (max-width: 600px) {
            .checkmark {
                width: 90px;
                height: 90px;
            }
            .checkmark::after {
                font-size: 50px;
            }
            .message {
                font-size: 20px;
                padding: 15px 20px;
            }
            /* Увеличиваем размер и делаем кнопки более удобными для мобильных */
            .links-container {
                flex-direction: column;
                gap: 15px;
                margin-top: 20px;
                align-items: center;
                width: 100%;
            }
            .links-container a {
                font-size: 20px;
                padding: 12px 24px;
                width: 80%;
                text-align: center;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="checkmark"></div>
    <div class="message">
        Заявка под номером <span>${requestNumber}</span>
        <#if create>
            успешно создана.
        <#else>
            успешно обновлена.
        </#if>
        <div class="links-container">
            <#if requestNumber??>
                <a href="/view/${requestNumber?c}">Редактировать</a>
            </#if>
            <a href="/">Меню</a>
        </div>
    </div>
</div>
</body>
</html>