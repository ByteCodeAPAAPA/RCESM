<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Справка - ${appInfo.title}</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #4361ee;
            --secondary-color: #3a0ca3;
            --success-color: #4cc9f0;
            --warning-color: #f72585;
            --light-bg: #f8f9fa;
            --dark-text: #2b2d42;
            --light-text: #6c757d;
            --card-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            --transition: all 0.3s ease;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: var(--dark-text);
        }

        .help-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 40px 20px;
        }

        .header {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            padding: 40px;
            border-radius: 20px;
            margin-bottom: 40px;
            text-align: center;
            box-shadow: var(--card-shadow);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .header h1 {
            font-size: 2.5rem;
            color: var(--primary-color);
            margin-bottom: 10px;
            font-weight: 700;
        }

        .version-badge {
            display: inline-block;
            background: var(--success-color);
            color: white;
            padding: 8px 16px;
            border-radius: 25px;
            font-size: 0.9rem;
            margin: 10px 0;
            font-weight: 600;
        }

        /* Стили для табов */
        .tabs-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            margin-bottom: 30px;
            overflow: hidden;
            box-shadow: var(--card-shadow);
        }

        .tabs-header {
            display: flex;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            padding: 0;
            overflow-x: auto;
            scrollbar-width: none;
        }

        .tabs-header::-webkit-scrollbar {
            display: none;
        }

        .tab-btn {
            padding: 20px 30px;
            background: none;
            border: none;
            color: white;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition);
            white-space: nowrap;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .tab-btn:hover {
            background: rgba(255, 255, 255, 0.1);
        }

        .tab-btn.active {
            background: rgba(255, 255, 255, 0.2);
            border-bottom: 3px solid white;
        }

        .tab-content {
            display: none;
            padding: 30px;
            animation: fadeIn 0.5s ease;
        }

        .tab-content.active {
            display: block;
        }

        .section {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: var(--card-shadow);
        }

        .section h2 {
            color: var(--secondary-color);
            margin-bottom: 25px;
            font-size: 1.8rem;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .section h2 i {
            color: var(--primary-color);
        }

        .feature-list {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            list-style: none;
        }

        .feature-item {
            background: white;
            border-radius: 15px;
            padding: 25px;
            transition: var(--transition);
            border: 2px solid transparent;
            position: relative;
            overflow: hidden;
        }

        .feature-item:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            border-color: var(--primary-color);
        }

        .feature-item::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 4px;
            height: 100%;
            background: var(--primary-color);
        }

        .feature-enabled::before {
            background: var(--success-color);
        }

        .feature-disabled::before {
            background: var(--light-text);
        }

        .feature-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }

        .feature-header h4 {
            font-size: 1.2rem;
            color: var(--dark-text);
            font-weight: 600;
        }

        .status-badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
            text-transform: uppercase;
        }

        .status-active {
            background: #d1fae5;
            color: #065f46;
        }

        .status-inactive {
            background: #f3f4f6;
            color: #6b7280;
        }

        .feature-description {
            color: var(--light-text);
            line-height: 1.6;
        }

        /* Увеличенный текст для инструкций */
        .instructions {
            display: grid;
            gap: 30px;
        }

        .instruction-group {
            background: white;
            padding: 30px;
            border-radius: 15px;
            border-left: 4px solid var(--primary-color);
        }

        .instruction-group h5 {
            color: var(--secondary-color);
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 1.4rem;
        }

        .instruction-group ol {
            padding-left: 25px;
            font-size: 1.1rem;
            line-height: 1.8;
        }

        .instruction-group li {
            margin-bottom: 15px;
            color: var(--dark-text);
        }

        .instruction-group strong {
            color: var(--primary-color);
            font-weight: 600;
        }

        /* Стили для изображений в инструкциях */
        .instruction-images {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin: 25px 0;
        }

        .instruction-image {
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            transition: var(--transition);
        }

        .instruction-image:hover {
            transform: scale(1.02);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }

        .instruction-image img {
            width: 100%;
            height: auto;
            display: block;
        }

        .image-caption {
            padding: 15px;
            background: var(--light-bg);
            text-align: center;
            font-size: 0.95rem;
            color: var(--light-text);
        }

        /* Кнопки для изображений */
        .image-modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.9);
            align-items: center;
            justify-content: center;
        }

        .modal-content {
            max-width: 90%;
            max-height: 90%;
        }

        .modal-content img {
            width: 100%;
            height: auto;
            border-radius: 8px;
        }

        .close-modal {
            position: absolute;
            top: 20px;
            right: 30px;
            color: white;
            font-size: 40px;
            font-weight: bold;
            cursor: pointer;
        }

        .search-box {
            position: sticky;
            top: 20px;
            z-index: 100;
            margin-bottom: 30px;
        }

        .search-input {
            width: 100%;
            padding: 15px 20px;
            border: none;
            border-radius: 50px;
            background: rgba(255, 255, 255, 0.95);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            font-size: 1.1rem;
            outline: none;
        }

        .search-input:focus {
            box-shadow: 0 4px 20px rgba(67, 97, 238, 0.3);
        }

        @media (max-width: 768px) {
            .help-container {
                padding: 20px 15px;
            }

            .header {
                padding: 25px;
            }

            .header h1 {
                font-size: 2rem;
            }

            .tabs-header {
                flex-direction: column;
            }

            .tab-btn {
                padding: 15px 20px;
                justify-content: center;
            }

            .section {
                padding: 20px;
            }

            .feature-list {
                grid-template-columns: 1fr;
            }

            .instruction-group {
                padding: 20px;
            }

            .instruction-group h5 {
                font-size: 1.2rem;
            }

            .instruction-group ol {
                font-size: 1rem;
            }

            .instruction-images {
                grid-template-columns: 1fr;
            }
        }

        .fade-in {
            animation: fadeIn 0.6s ease-in;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    </style>
</head>
<body>
<div class="help-container">
    <!-- Поиск -->
    <div class="search-box">
        <input type="text" class="search-input" placeholder="🔍 Поиск по справке..." id="searchInput">
    </div>

    <!-- Заголовок -->
    <div class="header fade-in">
        <h1><i class="fas fa-info-circle"></i> ${appInfo.title}</h1>
        <div class="version-badge">Версия 1.14</div>
    </div>

    <!-- Табы для разных функционалов -->
    <div class="tabs-container">
        <div class="tabs-header">
            <button class="tab-btn active" data-tab="main">
                <i class="fas fa-cogs"></i>Основной функционал
            </button>
            <button class="tab-btn" data-tab="instructions">
                <i class="fas fa-graduation-cap"></i>Инструкции
            </button>
        </div>

        <!-- Основной функционал -->
        <div class="tab-content active" id="tab-main">
            <div class="section">
                <h2><i class="fas fa-cogs"></i> Основной функционал</h2>
                <ul class="feature-list">
                    <#list appInfo.features?filter(f -> f.category == 'main') as feature>
                        <li class="feature-item ${feature.enabled?then('feature-enabled', 'feature-disabled')}">
                            <div class="feature-header">
                                <h4>${feature.name}</h4>
                                <span class="status-badge ${feature.enabled?then('status-active', 'status-inactive')}">
                                ${feature.enabled?then('Активно', 'Неактивно')}
                            </span>
                            </div>
                            <p class="feature-description">${feature.description}</p>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>

        <!-- Инструкции -->
        <div class="tab-content" id="tab-instructions">
            <div class="section">
                <h2><i class="fas fa-graduation-cap"></i> Инструкции по использованию</h2>
                <div class="instructions">

                    <!-- Создание заявки -->
                    <div class="instruction-group">
                        <h5><i class="fas fa-plus-circle"></i> Создание заявки</h5>

                        <div class="instruction-images">
                            <div class="instruction-image">
                                <img src="/img/2025-09-18_08-19-29.png" alt="Кнопка создания заявки"
                                     onclick="openModal(this.src, 'Кнопка создания заявки')">
                                <div class="image-caption">Рис. 1: Кнопка "Создать заявку" в верхнем меню</div>
                            </div>
                            <div class="instruction-image">
                                <img src="/img/2025-09-18_08-25-14.png" alt="Форма создания заявки"
                                     onclick="openModal(this.src, 'Форма создания заявки')">
                                <div class="image-caption">Рис. 2: Форма заполнения заявки</div>
                            </div>
                        </div>

                        <ol>
                            <li><strong>В меню находится кнопка "Создать заявку"</strong> (Она находится посередине в верхней части экрана).</li>
                            <li><strong>Нажмите кнопку "Создать заявку"</strong> для перехода к форме создания.</li>
                            <li><strong>Заполните все обязательные поля:</strong>
                                <ul style="margin: 10px 0 10px 20px; color: var(--light-text);">
                                    <li>"Сотрудник"</li>
                                    <li>"Обозначение/Наименование"</li>
                                    <li>"Количество деталей к контролю"</li>
                                    <li>"Тип контроля"</li>
                                    <li>"Заказ клиента"</li>
                                </ul>
                            </li>
                            <li><strong>При необходимости можете прикрепить фото</strong> - используйте кнопку "Выбор файлов".</li>
                            <li><strong>Нажмите кнопку "Создать заявку"</strong> для сохранения.</li>
                            <li><strong>Если все выполнили правильно</strong> - заявка будет успешно создана и появится уведомление.</li>
                        </ol>
                    </div>

                    <!-- Просмотр заявок -->
                    <div class="instruction-group">
                        <h5><i class="fas fa-search"></i> Просмотр заявок</h5>
                        <ol>
                            <li><strong>Переход в список заявок</strong>
                                <p>В меню есть информирующий блок "Список заявок для ОТК" - тут находится кнопка "Просмотр заявок".</p>
                                <div class="instruction-image">
                                    <img src="/img/2025-09-17_16-52-17.png" alt="Кнопка просмотра заявок"
                                         onclick="openModal(this.src, 'Кнопка просмотра заявок')">
                                    <div class="image-caption">Рис. 3: Блок "Список заявок для ОТК"</div>
                                </div>
                                <p>Нажмите "Просмотр заявок" - вы перейдете на форму "Список заявок".</p>
                                <div class="instruction-image">
                                    <img src="/img/2025-09-17_16-53-42.png" alt="Список заявок"
                                         onclick="openModal(this.src, 'Список заявок')">
                                    <div class="image-caption">Рис. 4: Таблица со списком всех заявок</div>
                                </div>
                            </li>
                            <li><strong>На форме присутствует сортировка и поиск</strong> по всем столбцам таблицы.</li>
                            <li><strong>Для детального просмотра</strong> в нужной заявке нажмите кнопку "Редактировать".</li>
                            <li><strong>Используйте поиск по ключевым словам</strong> или номеру заявки.</li>
                            <li><strong>Для просмотра хронологии</strong> в столбце "Функции" нажмите на иконку часов - отобразятся все действия с заявкой.
                                <div class="instruction-image">
                                    <img src="/img/2025-09-17_16-57-54.png" alt="Кнопка хронологии заявки"
                                         onclick="openModal(this.src, 'Хронология заявки')">
                                    <div class="image-caption">Рис. 5: Кнопка хронологии</div>
                                </div>
                                <div class="instruction-image">
                                    <img src="/img/2025-09-18_08-49-36.png" alt="Модальное окно хронологии заявки"
                                         onclick="openModal(this.src, 'Хронология заявки')">
                                    <div class="image-caption">Рис. 6: Просмотр хронологии заявок</div>
                                </div>
                            </li>
                        </ol>
                    </div>

                    <div class="instruction-group">
                        <h5><i class="fas fa-tasks"></i> Управление заявками</h5>

                        <ol>
                            <li>
                                <strong>Редактирование заявки</strong>
                                <p>Нажмите кнопку "Редактировать" в списке заявок или перейдите по ссылке из уведомления в Telegram.</p>
                                <div class="instruction-images">
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-51-46.png" alt="Редактирование заявки через список"
                                             onclick="openModal(this.src, 'Редактирование заявки через список')">
                                        <div class="image-caption">Рис. 7: Редактирование заявки через список заявок.</div>
                                    </div>
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-51-01.png" alt="Редактирование заявки через Telegram"
                                             onclick="openModal(this.src, 'Редактирование заявки через Telegram')">
                                        <div class="image-caption">Рис. 8: Редактирование заявки через уведомление в Telegram.</div>
                                    </div>
                                </div>
                            </li>

                            <li>
                                <strong>Принятие заявки в работу</strong>
                                <p>Нажмите на кнопку "Взять в работу", чтобы перевести заявку в статус "В работе".</p>
                                <div class="instruction-images">
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-11-46.png" alt="Перевод заявки в статус В работе"
                                             onclick="openModal(this.src, 'Перевод заявки в статус В работе')">
                                        <div class="image-caption">Рис. 9: Перевод заявки в статус "В работе".</div>
                                    </div>
                                </div>
                            </li>

                            <li>
                                <strong>Перевод заявки в статус "Забракована"</strong>
                                <p>Нажмите на кнопку "Забраковать". В открывшемся окне, под полем "Несоответствия", нажмите кнопку "Изменить". В появившемся окне выберите ключевые несоответствия для типа контроля заявки. При необходимости дополните или уточните причину брака в поле "Описание несоответствия". Нажмите кнопку "Сохранить" – заявка переведется в статус "Забракована". Мастер получит уведомление в Telegram о браковке заявки с указанием её номера.</p>
                                <div class="instruction-images">
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-15-53.png" alt="Браковка заявки - шаг 1"
                                             onclick="openModal(this.src, 'Браковка заявки - шаг 1')">
                                        <div class="image-caption">Рис. 10: Перевод заявки в статус "Забракована" (шаг 1).</div>
                                    </div>
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-26-41.png" alt="Браковка заявки - шаг 2"
                                             onclick="openModal(this.src, 'Браковка заявки - шаг 2')">
                                        <div class="image-caption">Рис. 11: Перевод заявки в статус "Забракована" (шаг 2).</div>
                                    </div>
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-20-24.png" alt="Браковка заявки - шаг 3"
                                             onclick="openModal(this.src, 'Браковка заявки - шаг 3')">
                                        <div class="image-caption">Рис. 12: Перевод заявки в статус "Забракована" (шаг 3).</div>
                                    </div>
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-21-31.png" alt="Браковка заявки - шаг 4"
                                             onclick="openModal(this.src, 'Браковка заявки - шаг 4')">
                                        <div class="image-caption">Рис. 13: Перевод заявки в статус "Забракована" (шаг 4).</div>
                                    </div>
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-22-35.png" alt="Браковка заявки - шаг 5"
                                             onclick="openModal(this.src, 'Браковка заявки - шаг 5')">
                                        <div class="image-caption">Рис. 14: Перевод заявки в статус "Забракована" (шаг 5).</div>
                                    </div>
                                </div>
                            </li>

                            <li>
                                <strong>Повторное предъявление заявки</strong>
                                <p>Перейдите в забракованную заявку из меню, где есть уведомления о том что заявка забракована или из списка заявок по фильтрам своего цеха и статусу "Забракована" в форме редактирования заявки вам будет доступна кнопка "Отправить повторно" при нажатии на нее заявка поменяет статус на "Новая" и ответственному за заявку отправится сообщение в Telegram и так пока заявку не закроет сотрудник ОТК.</p>
                                <div class="instruction-images">
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_14-28-16.png" alt="Браковка заявки - шаг 1"
                                             onclick="openModal(this.src, 'Браковка заявки - шаг 1')">
                                        <div class="image-caption">Рис. 10: Перевод заявки в статус "Забракована" (шаг 1).</div>
                                    </div>
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_14-29-30.png" alt="Браковка заявки - шаг 2"
                                             onclick="openModal(this.src, 'Браковка заявки - шаг 2')">
                                        <div class="image-caption">Рис. 11: Перевод заявки в статус "Забракована" (шаг 2).</div>
                                    </div>
                                </div>
                            </li>

                            <li>
                                <strong>Закрытие заявки</strong>
                                <p>Нажмите на кнопку "Выполнено". В открывшемся окне введите количество принятых деталей. Например, если в заявке указано 3 детали к контролю, а принято 2, появятся дополнительные поля "Несоответствия" и "Описание несоответствия" для непринятой детали. После их заполнения две детали получат статус "Закрыта", а одна – "Забракована". Мастер получит соответствующие уведомления в Telegram. Если все детали приняты, укажите их общее количество и закройте заявку.</p>
                                <div class="instruction-images">
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-29-35.png" alt="Закрытие заявки - шаг 1"
                                             onclick="openModal(this.src, 'Закрытие заявки - шаг 1')">
                                        <div class="image-caption">Рис. 15: Перевод заявки в статус "Закрыта" (шаг 1).</div>
                                    </div>
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-32-08.png" alt="Закрытие заявки - шаг 2"
                                             onclick="openModal(this.src, 'Закрытие заявки - шаг 2')">
                                        <div class="image-caption">Рис. 16: Перевод заявки в статус "Закрыта" (шаг 2).</div>
                                    </div>
                                    <div class="instruction-image">
                                        <img src="/img/2025-09-18_09-33-11.png" alt="Закрытие заявки - шаг 3"
                                             onclick="openModal(this.src, 'Закрытие заявки - шаг 3')">
                                        <div class="image-caption">Рис. 17: Перевод заявки в статус "Закрыта" (шаг 3).</div>
                                    </div>
                                </div>
                            </li>

                            <li>
                                <strong>Смена ответственного</strong>
                                <p>Мастер или сотрудник ОТК может изменить ответственного за заявку на этапах "В работе" или "Забракована" через поле "Ответственный", если текущий ответственный отсутствует или работает в другую смену.</p>
                            </li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Модальное окно для изображений -->
<div id="imageModal" class="image-modal">
    <span class="close-modal" onclick="closeModal()">&times;</span>
    <img class="modal-content" id="modalImage">
    <div id="modalCaption" style="color: white; text-align: center; margin-top: 15px; font-size: 1.1rem;"></div>
</div>

<script>
    // Управление табами
    document.querySelectorAll('.tab-btn').forEach(button => {
        button.addEventListener('click', () => {
            document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

            button.classList.add('active');
            const tabId = button.getAttribute('data-tab');
            document.getElementById(`tab-` + tabId).classList.add('active');
        });
    });

    // Модальное окно для изображений
    function openModal(src, caption) {
        const modal = document.getElementById('imageModal');
        const modalImg = document.getElementById('modalImage');
        const captionText = document.getElementById('modalCaption');

        modal.style.display = 'flex';
        modalImg.src = src;
        captionText.innerHTML = caption;
    }

    function closeModal() {
        document.getElementById('imageModal').style.display = 'none';
    }

    // Закрытие модального окна по клику вне изображения
    document.getElementById('imageModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeModal();
        }
    });

    // Закрытие модального окна по клавише ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeModal();
        }
    });

    // Плавная прокрутка
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Поиск
    const searchInput = document.getElementById('searchInput');
    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();

        const features = document.querySelectorAll('.feature-item');
        features.forEach(feature => {
            const text = feature.textContent.toLowerCase();
            feature.style.display = text.includes(searchTerm) ? 'block' : 'none';
        });

        const instructions = document.querySelectorAll('.instruction-group');
        instructions.forEach(instruction => {
            const text = instruction.textContent.toLowerCase();
            instruction.style.display = text.includes(searchTerm) ? 'block' : 'none';
        });
    });

    // Анимация при скролле
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('fade-in');
            }
        });
    }, { threshold: 0.1 });

    document.querySelectorAll('.section').forEach(section => {
        observer.observe(section);
    });
</script>
</body>
</html>