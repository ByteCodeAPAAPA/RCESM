<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>СГИ</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/base.css">
    <link rel="stylesheet" href="/css/sgi.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="/js/base/dialog.js"></script>
    <script src="/js/base/cache.js"></script>
    <script src="/js/base/base.js"></script>
    <script src="/js/sgi.js" defer></script>
</head>
<body>
<main>
    <div class="table-wrapper">
        <div class="table-header">
            <div class="table-header-item no" style="width: var(--no);">
                № п/п
            </div>
            <div class="table-header-item" style="width: var(--workcenter);">
                № цеха
            </div>
            <div class="table-header-item" style="width: var(--event);">
                Мероприятие
            </div>
            <div class="table-header-item" style="width: var(--action);">
                Сопутствующие действия
            </div>
            <div class="table-header-item" style="width: var(--department);">
                Ответственный отдел
            </div>
            <div class="table-header-item" style="width: var(--employee);">
                Ответственное лицо
            </div>
            <div class="table-header-item" style="width: var(--desiredDate);">
                Желаемый срок
            </div>
            <div class="table-header-item" style="width: var(--note);">
                Примечание
            </div>
            <div class="table-header-item" style="width: var(--planDate);">
                Планируемый срок
            </div>
            <div class="table-header-item" style="width: var(--comment);">
                Комментарий
            </div>
            <div class="table-header-item" style="width: var(--editing);">

            </div>
            <div class="table-header-item" style="width: var(--executions);">
                Факт выполнения
            </div>
            <div class="table-header-item" style="width: var(--status);">
                Статус
            </div>
        </div>
        <div class="table-content">
            <div class="table-content-rows">
            </div>
        </div>
    </div>
</main>
<footer>
    <div class="footer-wrapper">
        <div class="footer-content">
            <div class="pagination" id="paginationContainer">
            </div>
            <div class="buttons">
                <button class="btn btn-primary btn-action" id="create-button">
                    <i class="bi bi-plus-circle"></i>
                    <span class="btn-text">Добавить</span>
                </button>
                <button class="btn btn-outline-primary btn-action btn-filters" id="filter-button">
                    <i class="bi bi-arrow-counterclockwise"></i>
                    <span class="btn-text">Фильтры</span>
                </button>
                <button class="btn btn-outline-secondary btn-action">
                    <i class="bi bi-list"></i>
                    <a class="btn-text" id="menuButton" href="/menu" style="color: black">Меню</a>
                </button>
                <div class="print-container" id="print-container-button">
                    <button class="btn btn-outline-secondary btn-action print-button">
                        <i class="bi bi-three-dots-vertical"></i>
                        <span class="btn-text">Печать отчета</span>
                    </button>
                    <div class="print-menu">
                        <div class="print-menu-item" data-department="ОГМ">
                            <i class="bi bi-printer"></i> ОГМ
                        </div>
                        <div class="print-menu-item" data-department="ОРС">
                            <i class="bi bi-printer"></i> ОРС
                        </div>
                        <div class="print-menu-item" data-department="ОТиПК">
                            <i class="bi bi-printer"></i> ОТиПК
                        </div>
                        <div class="print-menu-item" data-department="ОГЭ">
                            <i class="bi bi-printer"></i> ОГЭ
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</footer>
<dialog id="create-dialog">
    <div class="modal-header">
        <h2 class="modal-title">Создание мероприятия</h2>
    </div>

    <form id="sgiForm" enctype="multipart/form-data">
        <input type="hidden" name="parentId" id="parentIdInput">
        <div class="modal-body">
            <div class="form-group">
                <label class="form-label">№ цеха</label>
                <input type="text" class="form-control" name="workcenter" required>
            </div>

            <div class="form-group">
                <label class="form-label">Мероприятие</label>
                <textarea class="form-control" name="event" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Сопутствующие действия</label>
                <textarea class="form-control" name="actions" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Отдел</label>
                <select class="form-control" name="department" required>
                    <option value="">Выберите отдел</option>
                    <option value="mechanic">ОГМ</option>
                    <option value="builder">ОРС</option>
                    <option value="protection">ОТиПК</option>
                    <option value="energy">ОГЭ</option>
                </select>
            </div>

            <div class="form-group">
                <label class="form-label">Ответственный</label>
                <textarea class="area-modal form-control tooltip-trigger" data-description="Открыть модальное окно" data-field="employee" name="employee" readonly  required></textarea>
                <input type="hidden" name="hiddenEmployee">
            </div>

            <div class="form-group">
                <label class="form-label">Желаемый срок</label>
                <input type="date" class="form-control" name="desiredDate">
            </div>

            <div class="form-group">
                <label class="form-label">Примечание</label>
                <textarea class="form-control" name="note"></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Прикрепленные фото</label>
                <div class="file-upload tooltip-trigger" data-description="Добавить фото">
                    <div class="file-list">

                    </div>
                </div>
                <input type="file" multiple accept="image/*" style="display: none;" name="additionalFiles">
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" id="cancelButton">Отмена</button>
            <button type="submit" class="btn btn-primary" id="createBtn">Создать</button>
        </div>
    </form>

</dialog>
<dialog id="editing-dialog">
    <div class="modal-header">
        <h2 class="modal-title">Редактирование мероприятия</h2>
        <button class="close-btn tooltip-trigger" data-description="Закрыть" id="cancelButton">&times;</button>
    </div>

    <div class="modal-body">
        <div class="form-group">
            <label class="form-label">№ цеха</label>
            <input type="text" class="form-control" data-field="workcenter">
        </div>

        <div class="form-group">
            <label class="form-label">Мероприятие</label>
            <textarea class="form-control" data-field="event"></textarea>
        </div>

        <div class="form-group">
            <label class="form-label">Сопутствующие действия</label>
            <textarea class="form-control" data-field="actions"></textarea>
        </div>

        <div class="form-group">
            <label class="form-label">Отдел</label>
            <select class="form-control" data-field="department">
                <option value="mechanic">ОГМ</option>
                <option value="builder">ОРС</option>
                <option value="protection">ОТиПК</option>
                <option value="energy">ОГЭ</option>
            </select>
        </div>

        <div class="form-group">
            <label class="form-label">Ответственный</label>
            <textarea class="area-modal form-control tooltip-trigger" data-description="Открыть Модальное окно" data-field="employee" readonly  required></textarea>
        </div>

        <div class="form-group">
            <label class="form-label">Желаемый срок</label>
            <input type="date" class="form-control" data-field="desiredDate">
        </div>

        <div class="form-group">
            <label class="form-label">Планируемый срок</label>
            <input type="date" class="form-control" data-field="planDate">
        </div>

        <div class="form-group">
            <label class="form-label">Примечание</label>
            <textarea class="form-control" data-field="note">Примечание</textarea>
        </div>

        <div class="form-group">
            <label class="form-label">Прикрепленные фото</label>
            <div class="file-upload tooltip-trigger" data-description="Добавить фото">
                <div class="file-list">
                </div>
            </div>
            <input type="file" multiple accept="image/*" style="display: none;" name="additionalFiles"
                   data-field="imagesSGI">
        </div>

        <div class="form-group">
            <label style="text-align: center" class="form-label">Документ</label>
            <div data-field="document" contenteditable="false">
                <div class="frame">
                    <i class="document fa-solid fa-file tooltip-trigger" data-description="Открыть модально окно"></i>
                </div>
            </div>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" id="saveEditBtn">Сохранить</button>
    </div>
</dialog>
<dialog id="execution-dialog">
    <div class="modal-header">
        <h2 class="modal-title">Факт выполнения</h2>
        <button class="close-btn" id="cancelButton">&times;</button>
    </div>
    <div class="modal-body">
        <div class="form-group">
            <label class="form-label">Дата выполнения</label>
            <input type="date" class="form-control" data-field="executionDate">
        </div>
        <div class="form-group">
            <label class="form-label">Отчет</label>
            <textarea class="form-control" data-field="report"></textarea>
        </div>
        <div class="form-group">
            <label class="form-label">Прикрепленные фото</label>
            <div class="file-upload">
                <div class="file-list">
                </div>
            </div>
            <input type="file" multiple accept="image/*" style="display: none;" name="additionalFiles"
                   data-field="imagesFactSGI">
        </div>
    </div>
    <div class="modal-footer">
        <button class="btn btn-primary" id="cancelButton">Отмена</button>
        <button class="btn btn-primary" id="saveBtn">Сохранить</button>
    </div>
</dialog>
<dialog id="filter-dialog">
    <div class="modal-header">
        <h2 class="modal-title">Фильтры</h2>
        <button class="close-btn" id="cancelButton">&times;</button>
    </div>

    <div class="modal-body">
        <div class="form-group">
            <label class="form-label">№ п/п</label>
            <input type="text" class="form-control" data-field="number">
        </div>

        <div class="form-group">
            <label class="form-label">№ цеха</label>
            <input type="text" class="form-control" data-field="workcenter">
        </div>

        <div class="form-group">
            <label class="form-label">Мероприятие</label>
            <textarea class="form-control" data-field="event"></textarea>
        </div>

        <div class="form-group">
            <label class="form-label">Сопутствующие действия</label>
            <textarea class="form-control" data-field="actions"></textarea>
        </div>

        <div class="form-group">
            <label class="form-label">Отдел</label>
            <select class="form-control" data-field="departament">
                <option value="">Выберите отдел</option>
                <option value="mechanic">ОГМ</option>
                <option value="builder">ОРС</option>
                <option value="protection">ОТиПК</option>
                <option value="energy">ОГЭ</option>
            </select>
        </div>

        <div class="form-group">
            <label class="form-label">Ответственный</label>
            <select class="form-control" data-field="employee">
            </select>
        </div>

        <div class="form-group">
            <label class="form-label">Желаемый срок</label>
            <input type="date" class="form-control" data-field="desiredDate">
        </div>

        <div class="form-group">
            <label class="form-label">Планируемый срок</label>
            <input type="date" class="form-control" data-field="planDate">
        </div>

        <div class="form-group">
            <label class="form-label">Примечание</label>
            <textarea class="form-control" data-field="note"></textarea>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" id="default-filter">Сбросить фильтры</button>
        <button class="btn btn-primary" id="filtered">Применить</button>
    </div>
</dialog>
<dialog id="employeeDialog">
    <div class="dialog-container">
        <div class="dialog-container-header">
            <div class="dialog-name">Окно выбора сотрудника</div>
            <div class="choice-field">
                <input type="text" placeholder="Введите фамилию сотрудника">
            </div>
        </div>
        <div class="dialog-container-content">
            <div class="dialog-content-header">
                <div class="dialog-content-header-column col-250">Имя</div>
                <div class="dialog-content-header-column col-250">Подразделение</div>
            </div>
            <div class="dialog-content-rows">
            </div>
        </div>
        <div class="dialog-container-footer">
            <button class="dialog-btn" onclick="document.getElementById('employeeDialog').close()">
                Отмена
            </button>
            <button class="dialog-btn primary" id="changeEmployee">
                Выбрать
            </button>
        </div>
    </div>
</dialog>
<dialog id="documentDialog">
    <div class="dialog-container">
        <div class="dialog-container-header">
            <div class="dialog-name">Окно работы с документом</div>
        </div>
        <div class="dialog-container-content">
            <div class="dialog-content-header">
                <div class="dialog-content-header-column col-450">Наименование файла</div>
                <div class="dialog-content-header-column col-100">Тип</div>
                <div style="text-align: end; font-size: 1rem" class="dialog-content-header-column col-100">Файл</div>
            </div>
            <div class="dialog-content-rows">
            </div>
            <div class="organization-row"></div>
        </div>
        <div class="dialog-container-footer">
            <button class="dialog-btn" id="cancelButton">
                Отмена
            </button>
        </div>
    </div>
</dialog>
</body>
</html>