<!DOCTYPE html>
<html lang="ru" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ой! Что-то пошло не так</title>
    <style>
        :root {
            --primary: #6366f1;
            --error: #ef4444;
            --dark: #1e293b;
            --light: #f8fafc;
        }

        body {
            font-family: 'Inter', system-ui, -apple-system, sans-serif;
            background-color: var(--light);
            color: var(--dark);
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            line-height: 1.5;
        }

        .error-container {
            text-align: center;
            max-width: 480px;
            padding: 2rem;
        }

        .error-animation {
            position: relative;
            width: 200px;
            height: 200px;
            margin: 0 auto 2rem;
        }

        .error-circle {
            position: absolute;
            width: 100%;
            height: 100%;
            border-radius: 50%;
            border: 8px solid transparent;
            border-top-color: var(--error);
            animation: spin 1.5s linear infinite;
        }

        .error-icon {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            font-size: 4rem;
            color: var(--error);
        }

        h1 {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 1rem;
            color: var(--error);
        }

        p {
            font-size: 1.125rem;
            margin-bottom: 2rem;
            opacity: 0.8;
        }

        .error-code {
            background-color: rgba(239, 68, 68, 0.1);
            padding: 0.5rem 1rem;
            border-radius: 0.5rem;
            font-family: monospace;
            display: inline-block;
            margin-top: 1rem;
        }

        .home-link {
            display: inline-block;
            margin-top: 2rem;
            padding: 0.75rem 1.5rem;
            background-color: var(--primary);
            color: white;
            text-decoration: none;
            border-radius: 0.5rem;
            font-weight: 500;
            transition: all 0.2s;
        }

        .home-link:hover {
            background-color: #4f46e5;
            transform: translateY(-2px);
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-10px); }
        }

        .emoji {
            animation: bounce 1.5s infinite;
            display: inline-block;
        }
    </style>
</head>
<body>
<div class="error-container">
    <div class="error-animation">
        <div class="error-circle"></div>
        <div class="error-circle" style="border-top-color: rgba(239, 68, 68, 0.5); animation-delay: -0.5s;"></div>
        <div class="error-icon">🚨</div>
    </div>

    <h1>Упс! <span class="emoji">😵</span></h1>
    <p>Кажется, вы пытались сделать что-то, что мы не предусмотрели.</p>
    <p>Попробуйте вернуться на главную или использовать правильный тип запроса.</p>

    <#if type??>
        <div class="error-code">
            Неподдерживаемый тип заявки: ${type!}
        </div>
    </#if>

    <#if RequestParameters.message??>
        <div class="error-code">
            ${errorMessage}
        </div>
    </#if>

    <a href="/" class="home-link">Вернуться на главную</a>
</div>
</body>
</html>