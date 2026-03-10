<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Система проверки цехов</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <link rel="stylesheet" href="/css/base.css">
    <link rel="stylesheet" href="/css/inspection.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="/js/base/dialog.js"></script>
    <script src="/js/base/cache.js"></script>
    <script src="/js/base/base.js"></script>
    <script src="/js/inspection.js"></script>
</head>
<body>
<div class="container">
    <div class="inspection">
        <div class="title">
            <h2>Инспекции</h2>
            <button class="btn btn-primary" id="create-button">Новая инспекция</button>
        </div>
        <hr>
        <div class="inspection-list">
        </div>
    </div>
</div>
<dialog id="create-dialog">
    <div class="modal-header">
        <h2 class="modal-title">Создание инспекции</h2>
    </div>
    <form id="inspectionForm" enctype="multipart/form-data">
        <div class="modal-body">
            <div class="form-group">
                <label class="form-label">Выберите Цех/Подразделение:</label>
                <select class="form-control" name="subDivision" required>
                    <option value="">Выберите Цех/Подразделение</option>
                    <option value="WorkShop1">Цех №1</option>
                    <option value="WorkShop3">Цех №3</option>
                    <option value="WorkShop4">Цех №4</option>
                    <option value="WorkShop5">Цех №5</option>
                    <option value="WorkShop6">Цех №6</option>
                    <option value="WorkShop8">Цех №8</option>
                    <option value="YTO">УТО</option>
                    <option value="OPIO">ОПИО</option>
                </select>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" id="cancelButton"
                    onclick="document.getElementById('create-dialog').close()">Отмена</button>
            <button type="submit" class="btn btn-primary" id="createBtn">Создать</button>
        </div>
    </form>
</dialog>
<dialog id="viewInspectionDialog">
    <div class="dialog-container">
        <div class="dialog-container-header">
            <div class="dialog-name">Окно нарушений</div>
        </div>
        <div class="dialog-container-content">
            <div class="violations-container">
            </div>
        </div>
        <div class="dialog-container-footer">
            <button class="dialog-btn" id="closeBtn">
                Выйти
            </button>
            <button class="dialog-btn btn-primary" id="addViolationBtn">
                Добавить нарушение
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
            <button class="dialog-btn close">
                Отмена
            </button>
            <button class="dialog-btn primary" id="changeSubDivision">
                Выбрать
            </button>
        </div>
    </div>
</dialog>
<dialog id="photosDialog">
    <div class="dialog-container">
        <div class="dialog-container-header">
            <div class="dialog-name">Фотографии нарушения</div>
            <button class="dialog-close-btn" id="closePhotosBtn">&times;</button>
        </div>
        <div class="dialog-container-content">
            <div class="photos-gallery"></div>
        </div>
        <div class="dialog-container-footer">
            <div class="photo-counter">
                <span class="current-photo">0</span> / <span class="total-photos">0</span>
            </div>
            <div class="photo-navigation">
                <button class="dialog-btn" id="prevPhotoBtn" disabled>
                    <i class="fas fa-chevron-left"></i>
                </button>
                <button class="dialog-btn" id="nextPhotoBtn" disabled>
                    <i class="fas fa-chevron-right"></i>
                </button>
            </div>
        </div>
    </div>
</dialog>
<dialog id="addViolationDialog">
    <div class="dialog-container">
        <div class="dialog-container-header">
            <div class="dialog-name">Добавление нарушения</div>
        </div>
        <form id="addViolationForm" enctype="multipart/form-data">
            <div class="dialog-container-content">
                <div class="form-group mb-3">
                    <label class="form-label">Описание нарушения:</label>
                    <textarea class="form-control" name="description" rows="3" required></textarea>
                </div>
                <div class="form-group mb-3">
                    <label class="form-label">Критерии:</label>
                    <select class="form-control" name="criteria" id="criteriaSelect" required>
                        <option value="">Выберите критерий</option>
                        <option value="Безопасность и охрана труда">Безопасность и охрана труда</option>
                        <option value="Технологическая дисциплина">Технологическая дисциплина</option>
                        <option value="Организация рабочих мест">Организация рабочих мест</option>
                        <option value="Документация">Документация</option>
                    </select>
                </div>
                <div class="form-group mb-3">
                    <label class="form-label">Оценка:</label>
                    <select class="form-control" name="score" id="scoreSelect" required>
                        <option value="1">1 балл</option>
                        <option value="2">2 балла</option>
                        <option value="3">3 балла</option>
                        <option value="4">4 балла</option>
                        <option value="5">5 баллов</option>
                    </select>
                </div>
                <div class="form-group mb-3">
                    <label class="form-label">Ответственное подразделение:</label>
                    <input type="text" class="form-control modal-input-change" name="subDivision" readonly required>
                </div>
                <div class="form-group mb-3">
                    <label class="form-label">Фотографии нарушений:</label>
                    <input type="file" class="form-control" name="additionalFiles" multiple accept="image/*">
                </div>
            </div>
            <div class="dialog-container-footer">
                <button type="button" class="dialog-btn" id="cancelAddBtn">Отмена</button>
                <button type="submit" class="dialog-btn btn-primary" id="createViolation">Создать</button>
            </div>
        </form>
    </div>
</dialog>
<dialog id="reportDialog">
    <div class="dialog-container">
        <div class="dialog-container-header">
            <div class="dialog-name">Окно отчета</div>
        </div>
        <div class="dialog-container-content">
            <div class="dialog-content-header">
                <div class="dialog-content-header-column col-250">Цех/Подразделение</div>
                <div class="dialog-content-header-column col-250">Замечания</div>
                <div class="dialog-content-header-column col-250">Оценка</div>
            </div>
            <div class="dialog-content-rows">
            </div>
        </div>
        <div class="dialog-container-footer">
            <button class="dialog-btn close">
                <i class="fas fa-times"></i> Закрыть
            </button>
            <div class="tool print tooltip-trigger" id="print-button" data-description="Печать отчета">
                <button class="dialog-btn print">
                    <i class="fas fa-print"></i>
                </button>
            </div>
        </div>
    </div>
</dialog>
</body>
</html>