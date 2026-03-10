<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Реестр средств измерений</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <link rel="stylesheet" href="/css/base.css">
    <link rel="stylesheet" href="/css/spe.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="/js/base/dialog.js"></script>
    <script src="/js/base/cache.js"></script>
    <script src="/js/base/base.js"></script>
    <script src="/js/spe.js"></script>
</head>

<body>
<div class="container">
    <header>
        <div class="title">
            <h1>Перечень УМИ ООО «БорМаш»</h1>
            <p class="subtitle">Актуальная информация о состоянии и поверке оборудования</p>
        </div>
        <div class="toolbar">
            <div class="tool edit tooltip-trigger" id="edit-button" data-description="Переключить режим редактирования">
                <button>
                    <i class="fas fa-pencil-alt"></i>
                </button>
            </div>
            <div class="tool save tooltip-trigger" id="save-button" data-description="Сохранить записи">
                <button>
                    <i class="fas fa-floppy-disk"></i>
                </button>
            </div>
            <div class="tool create tooltip-trigger" id="create-fgis-button" data-description="Добавить инструмент (ФГИС)">
                <button>
                    <i class="fas fa-square-plus text-success fs-4" style="text-shadow: 0 0 2px white;"></i>
                </button>
            </div>
            <div class="tool create tooltip-trigger" id="create-button" data-description="Добавить инструмент">
                <button>
                    <i class="fas fa-plus"></i>
                </button>
            </div>
            <div class="tool print tooltip-trigger" id="print-button" data-description="Печать отчета">
                <button>
                    <i class="fas fa-print"></i>
                </button>
            </div>
        </div>
    </header>

    <div class="controls">
        <div class="search-box">
            <i class="fas fa-search"></i>
            <input type="text" id="searchInput" placeholder="Поиск оборудования...">
        </div>
        <div class="filters">
            <button class="subdivision-button filter-btn tooltip-trigger" data-description="Фильтр по подразделению">
                <i class="fas fa-sitemap"></i>Подразделения
            </button>
<#--            <button class="employee-button filter-btn">-->
<#--                <i class="fas fa-user"></i>Ответственный-->
<#--            </button>-->
            <button class="filter-btn active filter-status" data-status="NONE">
                <i class="fas fa-list"></i>Все
            </button>
            <button class="filter-btn filter-status" data-status="WRITE_OFF">
                <i class="fas fa-check-circle"></i>Списаны
            </button>
            <button class="filter-btn filter-status" data-status="VERIFICATION_REQUIRED">
                <i class="fas fa-exclamation-triangle"></i>Требуется поверка
            </button>
            <button class="filter-btn filter-status" data-status="EXPIRED">
                <i class="fas fa-exclamation-triangle"></i>Истек срок поверки
            </button>
            <button class="filter-btn filter-status" data-status="AT_INSPECTION">
                <i class="fas fa-exclamation-triangle"></i>На поверке
            </button>
            <button class="filter-btn filter-status" data-status="REPAIR">
                <i class="fas fa-exclamation-triangle"></i>На ремонте
            </button>
            <button class="filter-btn filter-status" data-status="NO_DOCUMENT">
                <i class="fas fa-file-excel"></i>Без документа
            </button>
        </div>
    </div>

    <div class="data-container">
        <div class="sidebar">
            <div class="stats">
                <div class="stat-item">
                    <div class="stat-label">Всего единиц</div>
                    <div class="stat-value" id="total-units"></div>
                </div>
                <div class="stat-item">
                    <div class="stat-label">Списано</div>
                    <div class="stat-value" id="written-off"></div>
                </div>
                <div class="stat-item">
                    <div class="stat-label">Требуется поверка</div>
                    <div class="stat-value" id="verification-required"></div>
                </div>
                <div class="stat-item">
                    <div class="stat-label">Истек срок поверки</div>
                    <div class="stat-value" id="verification-period-has-expired"></div>
                </div>
                <div class="stat-item">
                    <div class="stat-label">На поверке</div>
                    <div class="stat-value" id="at-inspection"></div>
                </div>
                <div class="stat-item">
                    <div class="stat-label">На ремонте</div>
                    <div class="stat-value" id="at-repair"></div>
                </div>
                <div class="stat-item">
                    <div class="stat-label">Без документа</div>
                    <div class="stat-value" id="no-document"></div>
                </div>
            </div>

        </div>

        <div class="table-container">
            <div class="table-header">
                <div class="table-header-cell" style="width: var(--equipment);">
                    <div class="circle circle-header tooltip-trigger" data-description="Выделить все строки"></div>
                    Оборудование
                </div>
                <div class="table-header-cell" style="width: var(--characteristics);">Характеристики</div>
                <div class="table-header-cell" style="width: var(--subdivision);">Подразделение</div>
                <div class="table-header-cell" style="width: var(--responsible);">Ответственный</div>
                <div class="table-header-cell" style="width: var(--mark);">Отметка</div>
                <div class="table-header-cell" style="width: var(--preparationDate);">Дата подготовки поверки</div>
                <div class="table-header-cell" style="width: var(--verificationDate);">Дата поверки</div>
                <div class="table-header-cell" style="width: var(--certificate);">Свидетельство</div>
                <div class="table-header-cell" style="width: var(--periodicity);">Периодичность</div>
                <div class="table-header-cell" style="width: var(--file);">Документ</div>
                <div class="table-header-cell last-child" style="width: var(--status);">Статус</div>
            </div>

            <div class="table-body">

            </div>

        </div>
    </div>
</div>

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
<dialog id="subDivisionDialog">
    <div class="dialog-container">
        <div class="dialog-container-header">
            <div class="dialog-name">Окно выбора подразделения</div>
            <div class="choice-field">
                <input type="text" placeholder="Введите подразделение">
            </div>
        </div>
        <div class="dialog-container-content">
            <div class="dialog-content-header">
                <div class="dialog-content-header-column col-250">Наименование</div>
            </div>
            <div class="dialog-content-rows">
            </div>
        </div>
        <div class="dialog-container-footer">
            <button class="dialog-btn close" onclick="document.getElementById('subDivisionDialog').close()">
                Отмена
            </button>
            <button class="dialog-btn primary" id="changeSubDivision">
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
                <div style="text-align: end;" class="dialog-content-header-column col-100">Файл</div>
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
<dialog id="create-dialog">
    <div class="modal-header">
        <h2 class="modal-title">Создание оборудования</h2>
    </div>

    <form id="speForm" enctype="multipart/form-data">
        <div class="modal-body">
            <div class="form-group">
                <label class="form-label">Наименование</label>
                <input type="text" class="form-control" name="name" required>
            </div>

            <div class="form-group">
                <label class="form-label">Тип</label>
                <textarea class="form-control" name="type" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Заводской номер</label>
                <textarea class="form-control" name="outNumber" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Класс точности</label>
                <textarea class="form-control" name="accuracyClass" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Предел измерения</label>
                <textarea class="form-control" name="limitMeasurement" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Подразделение</label>
                <textarea class="area-modal form-control" data-name="subDivision" name="subDivision" readonly required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Сотрудник</label>
                <textarea class="area-modal form-control" data-name="employee" name="employee" readonly  required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Периодичность поверки</label>
                <textarea class="form-control" name="periodicity" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Дата подготовки</label>
                <input type="date" class="form-control" name="datePreparation">
            </div>

            <div class="form-group">
                <label class="form-label">Дата поверки</label>
                <input type="date" class="form-control" name="dateVerification">
            </div>

            <div class="form-group">
                <label class="form-label">Сертификат</label>
                <textarea class="form-control" name="certificateNumber"></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Организация-поверитель</label>
                <select class="form-control" name="organization" required>
                    <option value="">Выберите организацию</option>
                    <option value="organization1">Борисоглебский филиал ФБУ</option>
                    <option value="organization2">ФБУ "Воронежский ЦСМ"</option>
                    <option value="organization3">ООО "СТАНДАРТ"</option>
                </select>
            </div>

        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" id="cancelButton" onclick="document.getElementById('create-dialog').close()">Отмена</button>
            <button type="submit" class="btn btn-primary" id="createBtn">Создать</button>
        </div>
    </form>

</dialog>
<dialog id="create-fgis-dialog">
    <div class="modal-header">
        <h2 class="modal-title">Создание оборудования</h2>
    </div>

    <form id="speForm" enctype="multipart/form-data">
        <div class="modal-body">

            <div class="form-group">
                <label class="form-label">Заводской номер</label>
                <textarea class="form-control" name="outNumber" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Класс точности</label>
                <textarea class="form-control" name="accuracyClass" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Предел измерения</label>
                <textarea class="form-control" name="limitMeasurement" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Подразделение</label>
                <textarea class="area-modal form-control" data-name="subDivision" name="subDivision" readonly required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Сотрудник</label>
                <textarea class="area-modal form-control" data-name="employee" name="employee" readonly  required></textarea>
            </div>

        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" id="cancelButton" onclick="document.getElementById('create-fgis-dialog').close()">Отмена</button>
            <button type="submit" class="btn btn-primary" id="createFgisBtn">Создать</button>
        </div>
    </form>

</dialog>
</body>

</html>