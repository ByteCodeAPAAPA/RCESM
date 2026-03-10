<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Форма станка</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/choices.js/public/assets/styles/choices.min.css"/>
    <link rel="stylesheet" href="/css/modern.css">
</head>
<body>

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4 fade-in-up">
        <h1 id="form-title" class="page-title mb-0">Создание нового станка</h1>
        <a href="/machines" class="btn btn-secondary icon-text">
            <i class="bi bi-arrow-left"></i>
            <span>Назад к списку</span>
        </a>
    </div>

    <div class="card fade-in-up" style="animation-delay: 100ms;">
        <div class="card-body p-4">
            <form id="machine-form" enctype="multipart/form-data">
                <input type="hidden" id="machine-id" name="machineId">

                <div class="mb-3">
                    <label for="name" class="form-label">Имя станка</label>
                    <input type="text" class="form-control" id="name" name="name" required>
                </div>

                <div class="mb-3">
                    <label for="number" class="form-label">Серийный номер</label>
                    <input type="number" class="form-control" id="number" name="number" required>
                </div>

                <div class="mb-3">
                    <label for="description" class="form-label">Описание</label>
                    <textarea class="form-control" id="description" name="description" rows="3"></textarea>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="responsibleEmployees" class="form-label">Ответственные сотрудники</label>
                        <select id="responsibleEmployees" name="responsibleEmployeesList" class="form-select" multiple>

                        </select>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="admittedEmployees" class="form-label">Допущенные сотрудники</label>
                        <select id="admittedEmployees" name="admittedEmployeesList" class="form-select" multiple>

                        </select>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="documentFiles" class="form-label">Документы (PDF)</label>
                    <input class="form-control" type="file" id="documentFiles" name="documentFiles" multiple accept="application/pdf">
                </div>

                <div class="mb-3">
                    <label for="additionalFiles" class="form-label">Фотографии</label>
                    <input class="form-control" type="file" id="additionalFiles" name="additionalFiles" multiple accept="image/*">
                </div>

                <div class="mt-4 d-flex justify-content-end gap-2">
                    <a href="/machines" class="btn btn-secondary icon-text">
                        <i class="bi bi-x-circle"></i>
                        <span>Отмена</span>
                    </a>
                    <button type="submit" class="btn btn-primary icon-text" id="save-button">
                        <i class="bi bi-check-circle"></i>
                        <span>Сохранить</span>
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="/js/machines.js"></script>
<script src="https://cdn.jsdelivr.net/npm/choices.js/public/assets/scripts/choices.min.js"></script>
</body>
</html>
