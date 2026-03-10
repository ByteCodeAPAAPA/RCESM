var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
class Inspection extends Base {
    constructor(itemsPerPage = Infinity, visibleRow = Infinity) {
        super($(`.inspection-list`), itemsPerPage, visibleRow, () => {
            this.displayPage('/api/inspection/get-page-inspection', undefined).catch(console.error);
        });
        this.createInspection = (event) => __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            const button = $(event.target);
            const form = button.closest('form').get(0);
            const dialog = $('#create-dialog');
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }
            button.prop('disabled', true);
            const formData = {
                subDivision: dialog.find('select[name="subDivision"]').val()
            };
            try {
                const newInspection = yield this.createEntity('/api/inspection/create-inspection', formData);
                this.saveMassive = {};
                this.localCache.set(newInspection.id, newInspection);
                this.dialog.close("create-dialog");
                const newRow = this.createRow(newInspection);
                this.addInspectionToGroup(newInspection); // вместо прямого добавления
                button.prop('disabled', false);
                this.createNotification('Инспекция успешно создана', NotificationType.SUCCESS);
            }
            catch (error) {
                this.saveMassive = {};
                form.reset();
                button.prop('disabled', false);
            }
        });
        this.viewInspection = (event) => __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            const dialog = $('#viewInspectionDialog');
            const currentCard = $(event.currentTarget).closest('.table-card');
            const currentInspectionId = currentCard.attr('id');
            const inspection = this.localCache.get(Number(currentInspectionId));
            if (!inspection.violation || inspection.violation.length === 0) {
                inspection.violation = yield this.requestToApi(`/api/inspection/get-violation/${currentInspectionId}`, "GET");
                this.localCache.set(Number(currentInspectionId), inspection);
            }
            const violationsContainer = dialog.find('.violations-container');
            violationsContainer.empty();
            dialog.find('#closeBtn').off('click').on('click', () => {
                this.dialog.close('viewInspectionDialog');
            });
            dialog.find('#addViolationBtn').off('click').on('click', () => {
                this.openAddViolationDialog(currentInspectionId);
            });
            if (!inspection.violation || inspection.violation.length === 0) {
                violationsContainer.append(`
                <div class="no-violations">
                    Нарушений не найдено
                </div>
            `);
            }
            else {
                inspection.violation.forEach((violation) => {
                    var _a, _b;
                    let maxScore = 5;
                    switch (violation.criteria) {
                        case 'Технологическая дисциплина':
                            maxScore = 3;
                            break;
                        case 'Организация рабочих мест':
                            maxScore = 2;
                            break;
                        case 'Документация':
                            maxScore = 3;
                            break;
                        case 'Безопасность и охрана труда':
                            maxScore = 5;
                            break;
                    }
                    const violationCard = `
                    <div class="violation-card" id="${currentInspectionId}" data-violation-id="${violation.id}">
                        <div class="violation-card-header">
                            <div class="score">${violation.score}/${maxScore}</div>
                            <div class="criteria">${violation.criteria}</div>
                        </div>
                        <div class="violation-card-body">
                            <div class="field-row">
                                <div class="label">Ответственный:</div>
                                <div class="value">${(_a = violation.subDivision) === null || _a === void 0 ? void 0 : _a.name}</div>
                            </div>
                            <div class="field-row">
                                <div class="label">Дата обнаружения:</div>
                                <div class="value">${this.formatDateTime(violation.createdDate)}</div>
                            </div>
                            <div class="field-row">
                                <div class="label">Статус:</div>
                                <div class="value" name="status">${violation.status}</div>
                            </div>
                            <div class="field-row">
                                <div class="label">Описание:</div>
                                <div class="value">${violation.description}</div>
                            </div>
                            <div class="field-row">
                                <div class="label">Создал:</div>
                                <div class="value">${(_b = violation.createdBy) === null || _b === void 0 ? void 0 : _b.name}</div>
                            </div>
                            <div class="field-row">
                                <div class="label">Фото:</div>
                                <button class="dialog-btn print photo-icon">Просмотр</button>
                            </div>
                        </div>
                        <div class="violation-card-footer">
                            <button class="btn btn-delete" data-violation-id="${violation.id}">
                                Удалить
                            </button>
                            <button class="btn btn-fixed" data-violation-id="${violation.id}">
                                Отметить как исправленное
                            </button>
                        </div>
                    </div>
                `;
                    violationsContainer.append(violationCard);
                });
            }
            dialog.on('click', '.btn-delete', (event) => {
                const button = $(event.currentTarget);
                if (inspection.haveSecondInspection) {
                    this.createNotification("Нельзя удалять нарушение у инспекции, если есть вторичная инспекция", NotificationType.WARNING);
                    return;
                }
                const violationId = button.attr('data-violation-id');
                this.createConfirmationDialog("Подтвердите удаление нарушения").then((confirmed) => {
                    // @ts-ignore
                    if (confirmed) {
                        this.deleteEntity(`/api/inspection/delete-violation/${violationId}`).then(() => {
                            button.closest('.violation-card').remove();
                            inspection.violation = inspection.violation.filter((v) => v.id !== violationId);
                            this.localCache.set(inspection.id, inspection);
                            if (!inspection.violation || inspection.violation.length === 0) {
                                violationsContainer.append(`
                                <div class="no-violations">
                                    Нарушений не найдено
                                </div>
                            `);
                            }
                            this.createNotification('Нарушение успешно удалено', NotificationType.SUCCESS);
                        });
                    }
                });
            });
            dialog.on('click', '.btn-fixed', (event) => {
                const button = $(event.currentTarget);
                if (inspection.haveSecondInspection) {
                    this.createNotification("У инспекции есть вторичная инспекция", NotificationType.WARNING);
                    return;
                }
                const violationId = button.attr('data-violation-id');
                const violation = inspection.violation.find((v) => v.id === violationId);
                this.requestToApi(`/api/inspection/change-status-violation/${violationId}`, "PATCH").then(() => {
                    violation.status = violation.status === 'Не исправлено' ? 'Исправлено' : 'Не исправлено';
                    inspection.violation = inspection.violation.map((v) => v.id === violationId ? violation : v);
                    this.localCache.set(inspection.id, inspection);
                    button.closest('.violation-card').find('[name="status"]').text(violation.status);
                    this.createNotification('Статус изменен', NotificationType.SUCCESS);
                });
            });
            dialog.on('click', '.photo-icon', this.openImagesDialog.bind(this));
            this.dialog.open('viewInspectionDialog');
        });
        this.openImagesDialog = (event) => __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            const button = $(event.currentTarget);
            const violationId = button.closest('.violation-card').data('violation-id');
            const unlock = this.lockScreen();
            try {
                const images = yield this.requestToApi(`/api/inspection/get-images-inspection/${violationId}`, "GET");
                if (!images || images.length === 0) {
                    this.createNotification('Фотографии не прикреплены', NotificationType.INFO);
                    return;
                }
                const dialog = $('#photosDialog');
                const gallery = dialog.find('.photos-gallery');
                gallery.empty();
                images.forEach((image, index) => {
                    gallery.append(`
                    <div class="photo-item ${index === 0 ? 'active' : ''}">
                        <img src="${image.data}" id="${image.id}" 
                             alt="${image.name || 'Фото нарушения'} ${index + 1}"
                             loading="lazy">
                    </div>
                `);
                });
                dialog.find('.current-photo').text('1');
                dialog.find('.total-photos').text(images.length);
                this.setupPhotoNavigation(dialog, images.length);
                this.dialog.open('photosDialog');
            }
            catch (error) {
                this.createNotification('Ошибка загрузки фотографий', NotificationType.ERROR);
            }
            finally {
                unlock();
            }
        });
        this.createHandler('click', '#create-button', () => this.dialog.open('create-dialog'), true);
        this.createHandler('click', '#createBtn', this.createInspection.bind(this), true);
        this.createHandler('click', '#createSecondaryBtn', this.createSecondaryInspection.bind(this), true);
        this.createHandler('click', '.view-btn', this.viewInspection.bind(this), true);
        this.createHandler('click', '#createViolation', this.createViolation.bind(this), true);
        this.createHandler('click', '.modal-input-change', this.changeSubDivision.bind(this), true);
        this.createHandler('click', '.delete-inspection', (event) => {
            const id = $(event.target).closest('.table-card').attr('id');
            const inspection = this.localCache.get(Number(id));
            if (inspection.haveSecondInspection) {
                this.createNotification("Нельзя удалять инспекцию с вторичной инспекцией", NotificationType.WARNING);
                return;
            }
            this.createConfirmationDialog('Подтвердите удаление инспекции').then((confirmed) => {
                // @ts-ignore
                if (confirmed) {
                    this.deleteEntity(`/api/inspection/delete-inspection/${id}`).then(() => {
                        this.deleteRow(id);
                        this.createNotification('Инспекция успешно удалена', NotificationType.SUCCESS);
                        if (inspection.primaryInspectionId != null) {
                            const primaryInspection = this.localCache.get(inspection.primaryInspectionId);
                            primaryInspection.haveSecondInspection = false;
                        }
                    });
                }
            });
        });
        this.createHandler('click', '#report-btn', this.makeReport.bind(this), true);
        this.createHandler('click', '#print-button', (event) => this.print(event), true);
    }
    createRow(inspection) {
        var _a;
        const card = `
            <div class="table-card" id="${inspection.id}" data-index="${inspection.id}">
               <div class="card-body">
                   <div class="card-title">
                       <h5>Инспекция №${inspection.id}</h5>
                   </div>
                       <p class="card-text">
                           Дата: ${this.formatDateTime(inspection.dateInspection)}<br>
                           Тип: ${inspection.type}<br>
                           Цех: <span data-inspection-id="${inspection.id}">${(_a = inspection.subDivision) === null || _a === void 0 ? void 0 : _a.name}</span>
                           ${inspection.primaryInspectionId != null ?
            `<br> Первичная инспекция: <span data-inspection-id="${inspection.primaryInspectionId}">№${inspection.primaryInspectionId}</span>`
            : ''}       
                       </p>
                   <div class="buttons">
                       <button class="btn btn-outline-primary view-btn">Подробнее</button>
                       ${inspection.primaryInspectionId != null ? '' : '<button class="btn btn-warning" id="createSecondaryBtn">Создать повторную проверку</button>'}
                       <button class="btn btn-success" data-inspectionId="${inspection.id}" id="report-btn">Отчеты</button>
                       <button class="btn btn-danger delete-inspection">Удалить</button>
                   </div>
               </div>     
            </div>`;
        return $(card);
    }
    onScroll() {
    }
    createSecondaryInspection(event) {
        return __awaiter(this, void 0, void 0, function* () {
            const inspectionId = $(event.target).closest('.table-card').attr('id');
            const primaryInspection = this.localCache.get(Number(inspectionId));
            const newInspection = yield this.createEntity(`/api/inspection/create-secondary-inspection/${inspectionId}`);
            this.localCache.set(newInspection.id, newInspection);
            const newRow = this.createRow(newInspection);
            this.addInspectionToGroup(newInspection); // вместо прямого добавле
            primaryInspection.haveSecondInspection = true;
            this.localCache.set(primaryInspection.id, primaryInspection);
            this.createNotification('Вторичная инспекция успешно создана', NotificationType.SUCCESS);
        });
    }
    openAddViolationDialog(inspectionId) {
        const addDialog = $('#addViolationDialog');
        const form = addDialog.find('#addViolationForm')[0];
        addDialog.data('inspection-id', inspectionId);
        form.reset();
        const criteriaSelect = addDialog.find('#criteriaSelect');
        const scoreSelect = addDialog.find('#scoreSelect');
        const updateScoreOptions = (maxScore) => {
            scoreSelect.empty();
            for (let i = 1; i <= maxScore; i++) {
                const option = $('<option>', {
                    value: i,
                    text: this.getScoreText(i)
                });
                if (i === 1) {
                    option.prop('selected', true);
                }
                scoreSelect.append(option);
            }
        };
        updateScoreOptions(5);
        criteriaSelect.off('change').on('change', function () {
            const criteria = $(this).val();
            let maxScore = 5;
            switch (criteria) {
                case 'Технологическая дисциплина':
                    maxScore = 3;
                    break;
                case 'Организация рабочих мест':
                    maxScore = 2;
                    break;
                case 'Документация':
                    maxScore = 3;
                    break;
                case 'Безопасность и охрана труда':
                    maxScore = 5;
                    break;
                default:
                    maxScore = 5; // По умолчанию, если критерий не выбран
                    break;
            }
            updateScoreOptions(maxScore);
        });
        addDialog.find('#cancelAddBtn').off('click').on('click', () => {
            this.dialog.close('addViolationDialog');
        });
        form.onsubmit = (e) => {
            e.preventDefault();
        };
        this.dialog.open('addViolationDialog');
    }
    getScoreText(score) {
        const lastDigit = score % 10;
        const lastTwoDigits = score % 100;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) {
            return `${score} баллов`;
        }
        if (lastDigit === 1) {
            return `${score} балл`;
        }
        if (lastDigit >= 2 && lastDigit <= 4) {
            return `${score} балла`;
        }
        return `${score} баллов`;
    }
    createViolation(event) {
        return __awaiter(this, void 0, void 0, function* () {
            var _a, _b;
            event.preventDefault();
            const button = $(event.target);
            const form = button.closest('form').get(0);
            const dialog = $('#addViolationDialog');
            const currentInspectionId = dialog.data('inspection-id');
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }
            const inspection = this.localCache.get(Number(currentInspectionId));
            if (inspection.haveSecondInspection) {
                this.createNotification("У инспекции есть вторичная инспекция", NotificationType.WARNING);
                return;
            }
            button.prop('disabled', true);
            const formData = new FormData();
            const jsonData = {
                inspectionId: currentInspectionId,
                description: dialog.find('textarea[name="description"]').val(),
                criteria: dialog.find('select[name="criteria"]').val(),
                score: dialog.find('select[name="score"]').val(),
                subDivision: dialog.find('input[name="subDivision"]').val(),
            };
            const jsonBlob = new Blob([JSON.stringify(jsonData)], { type: 'application/json' });
            formData.append('data', jsonBlob, 'data.json');
            const fileInput = dialog.find('input[name="additionalFiles"]')[0];
            if (fileInput === null || fileInput === void 0 ? void 0 : fileInput.files) {
                for (let i = 0; i < fileInput.files.length; i++) {
                    formData.append('additionalFiles', fileInput.files[i]);
                }
            }
            try {
                const newViolation = (yield this.createEntity('/api/inspection/create-violation', formData));
                this.saveMassive = {};
                const inspection = this.localCache.get(newViolation.inspectionId);
                inspection.violation.push(newViolation);
                this.localCache.set(inspection.id, inspection);
                this.dialog.close("addViolationDialog");
                const violationContainer = $("#viewInspectionDialog").find('.violations-container');
                violationContainer.find('.no-violations').remove();
                const violationCard = `
                <div class="violation-card" id="${inspection.id}" data-violation-id="${newViolation.id}">
                    <div class="violation-card-header">
                        <div class="score">${newViolation.score}/5</div>
                        <div class="criteria">${newViolation.criteria}</div>
                    </div>
                    <div class="violation-card-body">
                        <div class="field-row">
                            <div class="label">Ответственный:</div>
                            <div class="value">${(_a = newViolation.subDivision) === null || _a === void 0 ? void 0 : _a.name}</div>
                        </div>
                        <div class="field-row">
                            <div class="label">Дата обнаружения:</div>
                            <div class="value">${this.formatDateTime(newViolation.createdDate)}</div>
                        </div>
                        <div class="field-row">
                            <div class="label">Статус:</div>
                            <div class="value">${newViolation.status}</div>
                        </div>
                        <div class="field-row">
                            <div class="label">Описание:</div>
                            <div class="value">${newViolation.description}</div>
                        </div>
                        <div class="field-row">
                            <div class="label">Создал:</div>
                            <div class="value">${(_b = newViolation.createdBy) === null || _b === void 0 ? void 0 : _b.name}</div>
                        </div>
                        <div class="field-row">
                            <div class="label">Фото:</div>
                            <button class="dialog-btn print photo-icon">Просмотр</button>
                        </div>
                    </div>
                    <div class="violation-card-footer">
                        <button class="btn btn-delete" data-violation-id="${newViolation.id}">
                            Удалить
                        </button>
                        <button class="btn btn-fixed" data-violation-id="${newViolation.id}">
                            Отметить как исправленное
                        </button>
                    </div>
                </div>
                `;
                violationContainer.append(violationCard);
                button.prop('disabled', false);
                this.createNotification('Нарушение успешно создано', NotificationType.SUCCESS);
            }
            catch (error) {
                this.saveMassive = {};
                form.reset();
                button.prop('disabled', false);
            }
        });
    }
    changeSubDivision(event) {
        return __awaiter(this, void 0, void 0, function* () {
            var _a;
            event.preventDefault();
            const modalDiv = $(event.currentTarget);
            const dialog = $('#subDivisionDialog');
            const rowContainer = dialog.find('.dialog-content-rows');
            const searchInput = dialog.find('.choice-field input');
            const changeButton = $('#changeSubDivision');
            const currentId = modalDiv.closest('#addViolationDialog').data('inspection-id');
            let selected;
            const allSubDivisions = (yield this.cache.get('subDivision'));
            let currentInspection = null;
            if (currentId) {
                currentInspection = this.localCache.get(Number(currentId));
            }
            // Фильтруем только нужные подразделения
            const allowedNames = ['ОГТ', 'ОГМ', 'ОТиТБ', 'ПДО'];
            const currentSubDivisionName = (_a = currentInspection === null || currentInspection === void 0 ? void 0 : currentInspection.subDivision) === null || _a === void 0 ? void 0 : _a.name;
            const filteredData = allSubDivisions.filter((item) => {
                const itemName = item.name;
                // Включаем текущее подразделение инспекции
                if (currentSubDivisionName && itemName === currentSubDivisionName) {
                    return true;
                }
                // Включаем только разрешенные имена
                for (const allowedName of allowedNames) {
                    if (itemName.includes(allowedName)) {
                        return true;
                    }
                }
                return false;
            });
            // Если текущее подразделение уже есть в списке разрешенных, убедимся, что оно не дублируется
            const uniqueData = [];
            const seenNames = new Set();
            for (const item of filteredData) {
                if (!seenNames.has(item.name)) {
                    seenNames.add(item.name);
                    uniqueData.push(item);
                }
            }
            const data = uniqueData;
            const renderRows = (items) => {
                rowContainer.empty();
                items.forEach(item => {
                    rowContainer.append(`
                <div class="dialog-content-rows-row" data-id="${item.id}">
                    <div class="content-row-column col-250">${item.name}</div>
                </div>`);
                });
            };
            renderRows(data);
            searchInput.off('input').on('input', function () {
                const searchText = $(this).val().toString().toLowerCase().trim();
                const filtered = data.filter((e) => e.name.toLowerCase().includes(searchText));
                renderRows(filtered);
            });
            this.dialog.open("subDivisionDialog");
            rowContainer.off('click').on('click', '.dialog-content-rows-row', function () {
                const id = $(this).data('id');
                selected = data.find((e) => e.id === id);
                $('.dialog-content-rows-row').removeClass('selected');
                $(this).addClass('selected');
            });
            changeButton.off('click').on('click', () => {
                if (!selected) {
                    this.createNotification(`Выберите подразделение из списка`, NotificationType.WARNING);
                    return;
                }
                modalDiv.text(selected.name);
                modalDiv.val(selected.name);
                if (currentId) {
                    this.saveMassive[currentId] = Object.assign(Object.assign({}, this.saveMassive[currentId]), { ['subDivision']: selected });
                }
                else {
                    this.saveMassive['subDivision'] = selected;
                }
                modalDiv.addClass('change-textarea');
                this.dialog.close('subDivisionDialog');
            });
            dialog.off('click', '.close').on('click', '.close', (event) => {
                this.dialog.close('subDivisionDialog');
            });
            modalDiv.addClass('change');
        });
    }
    setupPhotoNavigation(dialog, totalPhotos) {
        const gallery = dialog.find('.photos-gallery');
        const prevBtn = dialog.find('#prevPhotoBtn');
        const nextBtn = dialog.find('#nextPhotoBtn');
        const currentPhotoSpan = dialog.find('.current-photo');
        let currentIndex = 0;
        const updateNavigation = () => {
            gallery.find('.photo-item').removeClass('active').hide();
            gallery.find(`.photo-item:eq(${currentIndex})`).addClass('active').show();
            currentPhotoSpan.text(currentIndex + 1);
            prevBtn.prop('disabled', currentIndex === 0);
            nextBtn.prop('disabled', currentIndex === totalPhotos - 1);
        };
        prevBtn.off('click').on('click', () => {
            if (currentIndex > 0) {
                currentIndex--;
                updateNavigation();
            }
        });
        nextBtn.off('click').on('click', () => {
            if (currentIndex < totalPhotos - 1) {
                currentIndex++;
                updateNavigation();
            }
        });
        dialog.find('#closePhotosBtn').off('click').on('click', () => {
            this.dialog.close('photosDialog');
        });
        updateNavigation();
        dialog.on('dialog:open', () => {
            dialog.find('#closePhotosBtn').focus();
        });
    }
    makeReport(event) {
        return __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            const currentInspectionId = $(event.target).closest('.table-card').attr('id');
            const inspection = this.localCache.get(Number(currentInspectionId));
            if (!inspection.violation || inspection.violation.length === 0) {
                inspection.violation = yield this.requestToApi(`/api/inspection/get-violation/${currentInspectionId}`, "GET");
                this.localCache.set(Number(currentInspectionId), inspection);
                if (!inspection.violation || inspection.violation.length === 0) {
                    this.createNotification('Нарушений не найдено', NotificationType.INFO);
                    return;
                }
            }
            const dialog = $('#reportDialog');
            const tabsHtml = `
        <div class="report-tabs">
            <button class="report-tab active" data-tab="workshop">Отчет по цеху</button>
            <button class="report-tab" data-tab="special">Отчет по службам</button>
        </div>
    `;
            if (!dialog.find('.report-tabs').length) {
                dialog.find('.dialog-container-header').append(tabsHtml);
            }
            const contentHtml = `
        <div class="report-tab-content active" id="workshopReport">
            <div class="dialog-content-header">
                <div class="dialog-content-header-column">Цех/Подразделение</div>
                <div class="dialog-content-header-column">Критерий</div>
                <div class="dialog-content-header-column">Сумма баллов</div>
            </div>
            <div class="dialog-content-rows workshop-rows"></div>
        </div>
        <div class="report-tab-content" id="specialReport">
            <div class="dialog-content-header">
                <div class="dialog-content-header-column">Цех/Подразделение</div>
                <div class="dialog-content-header-column">Критерий</div>
                <div class="dialog-content-header-column">Сумма баллов</div>
            </div>
            <div class="dialog-content-rows special-rows"></div>
        </div>
    `;
            if (!dialog.find('.report-tab-content').length) {
                dialog.find('.dialog-container-content').html(contentHtml);
            }
            $('#print-button').data('inspectionId', inspection.id);
            yield this.fillWorkshopReport(inspection);
            yield this.fillSpecialReport();
            dialog.find('.report-tab').off('click').on('click', function () {
                var _a;
                const tabId = $(this).data('tab');
                dialog.find('.report-tab').removeClass('active');
                $(this).addClass('active');
                dialog.find('.report-tab-content').removeClass('active');
                dialog.find(`#${tabId}Report`).addClass('active');
                // Обновляем заголовок при переключении вкладок
                if (tabId === 'workshop') {
                    const inspectionDate = new Date(inspection.dateInspection);
                    const monthNames = [
                        'январь', 'февраль', 'март', 'апрель', 'май', 'июнь',
                        'июль', 'август', 'сентябрь', 'октябрь', 'ноябрь', 'декабрь'
                    ];
                    const monthName = monthNames[inspectionDate.getMonth()];
                    const year = inspectionDate.getFullYear();
                    $('#reportDialog .dialog-name').text(`Отчет по цеху "${(_a = inspection.subDivision) === null || _a === void 0 ? void 0 : _a.name}" за ${monthName} ${year} года`);
                }
                else {
                    $('#reportDialog .dialog-name').text('Отчет по ПДО/ОГМ/ОТиТБ/ОГТ за текущий месяц');
                }
            });
            dialog.find('.dialog-btn.close').off('click').on('click', () => {
                this.dialog.close('reportDialog');
            });
            this.dialog.open('reportDialog');
        });
    }
    fillWorkshopReport(inspection) {
        return __awaiter(this, void 0, void 0, function* () {
            var _a, _b, _c, _d, _e, _f, _g;
            const rowsContainer = $('#reportDialog .workshop-rows');
            rowsContainer.empty();
            const violationsByCriteria = {};
            inspection.violation.forEach((violation) => {
                if (violation.subDivision && violation.subDivision.name === inspection.subDivision.name) {
                    const criteria = violation.criteria;
                    if (!violationsByCriteria[criteria]) {
                        violationsByCriteria[criteria] = {
                            subDivisionName: inspection.subDivision.name,
                            totalScore: 0,
                            description: criteria
                        };
                    }
                    violationsByCriteria[criteria].totalScore += violation.score;
                }
            });
            let index = 0;
            for (const key in violationsByCriteria) {
                if (violationsByCriteria.hasOwnProperty(key)) {
                    const violation = violationsByCriteria[key];
                    const row = `
                    <div class="report-row" data-index="${index}">
                        <div class="report-column">${violation.subDivisionName}</div>
                        <div class="report-column">${violation.description}</div>
                        <div class="report-column score-column">${violation.totalScore}</div>
                    </div>
                `;
                    rowsContainer.append(row);
                    index++;
                }
            }
            if (index === 0) {
                rowsContainer.append(`
                <div class="no-data">
                    Нет нарушений для подразделения "${inspection.subDivision.name}"
                </div>
            `);
            }
            rowsContainer.append('<div class="report-divider"></div>');
            const baseBonus = 5.0;
            let totalPenalty = 0;
            const appliedPenalties = [];
            const subDivisionName = ((_a = inspection.subDivision) === null || _a === void 0 ? void 0 : _a.name) || '';
            const isSpecialDivision = ['ПДО', 'ОГМ', 'ОТиТБ'].some(name => subDivisionName.toUpperCase().indexOf(name) !== -1);
            if (isSpecialDivision) {
                const safetyScore = ((_b = violationsByCriteria['Безопасность и охрана труда']) === null || _b === void 0 ? void 0 : _b.totalScore) || 0;
                const docsScore = ((_c = violationsByCriteria['Документация']) === null || _c === void 0 ? void 0 : _c.totalScore) || 0;
                if (safetyScore >= 6) {
                    totalPenalty += 2.5;
                    appliedPenalties.push('Безопасность и охрана труда: -2.5%');
                }
                if (docsScore >= 6) {
                    totalPenalty += 2.5;
                    appliedPenalties.push('Документация: -2.5%');
                }
            }
            else {
                const safetyScore = ((_d = violationsByCriteria['Безопасность и охрана труда']) === null || _d === void 0 ? void 0 : _d.totalScore) || 0;
                const techScore = ((_e = violationsByCriteria['Технологическая дисциплина']) === null || _e === void 0 ? void 0 : _e.totalScore) || 0;
                const orgScore = ((_f = violationsByCriteria['Организация рабочих мест']) === null || _f === void 0 ? void 0 : _f.totalScore) || 0;
                const docsScore = ((_g = violationsByCriteria['Документация']) === null || _g === void 0 ? void 0 : _g.totalScore) || 0;
                if (safetyScore >= 9) {
                    totalPenalty += 1.25;
                    appliedPenalties.push('Безопасность и охрана труда: -1.25%');
                }
                if (techScore >= 9) {
                    totalPenalty += 1.25;
                    appliedPenalties.push('Технологическая дисциплина: -1.25%');
                }
                if (orgScore >= 9) {
                    totalPenalty += 1.25;
                    appliedPenalties.push('Организация рабочих мест: -1.25%');
                }
                if (docsScore >= 9) {
                    totalPenalty += 1.25;
                    appliedPenalties.push('Документация: -1.25%');
                }
            }
            const finalBonus = Math.max(0, baseBonus - totalPenalty);
            let penaltiesHtml = '';
            if (appliedPenalties.length > 0) {
                appliedPenalties.forEach(penalty => {
                    penaltiesHtml += `
                    <div class="penalty-row">
                        <span class="penalty-label">${penalty}</span>
                    </div>
                `;
                });
            }
            const bonusRow = `
            <div class="bonus-calculation">
                <h4>Расчет премии для ${inspection.subDivision.name}</h4>
                <div class="bonus-row">
                    <span class="bonus-label">Базовая премия:</span>
                    <span class="bonus-value">${baseBonus.toFixed(2)} %</span>
                </div>
                ${appliedPenalties.length > 0 ? `
                    <div class="bonus-section">
                        <div class="bonus-section-title">Примененные штрафы:</div>
                        ${penaltiesHtml}
                    </div>
                ` : `
                    <div class="bonus-row">
                        <span class="bonus-label">Штрафы не применены</span>
                        <span class="bonus-value">-</span>
                    </div>
                `}
                <div class="bonus-row final-bonus">
                    <span class="bonus-label">Финальная премия:</span>
                    <span class="bonus-value">${finalBonus.toFixed(2)} %</span>
                </div>
            </div>
        `;
            rowsContainer.append(bonusRow);
        });
    }
    fillSpecialReport() {
        return __awaiter(this, void 0, void 0, function* () {
            const rowsContainer = $('#reportDialog .special-rows');
            rowsContainer.empty();
            try {
                const allInspectionsViolation = (yield this.requestToApi('/api/inspection/get-all-services-violation', 'GET'));
                if (allInspectionsViolation.length === 0) {
                    rowsContainer.append(`
                    <div class="no-data">
                        Нет данных по инспекциям
                    </div>
                `);
                    return;
                }
                const violationsBySubDivision = {};
                const specialDivisions = ['ПДО', 'ОГМ', 'ОТиТБ', 'ОГТ'];
                for (const violation of allInspectionsViolation) {
                    if (!violation || !violation.subDivision || !violation.criteria)
                        continue;
                    const subDivName = violation.subDivision.name;
                    const criteria = violation.criteria;
                    const isSpecial = specialDivisions.some(div => subDivName && subDivName.toUpperCase() === div.toUpperCase());
                    if (!isSpecial)
                        continue;
                    if (!violationsBySubDivision[subDivName]) {
                        violationsBySubDivision[subDivName] = {};
                    }
                    if (!violationsBySubDivision[subDivName][criteria]) {
                        violationsBySubDivision[subDivName][criteria] = 0;
                    }
                    violationsBySubDivision[subDivName][criteria] += violation.score || 0;
                }
                if (Object.keys(violationsBySubDivision).length === 0) {
                    rowsContainer.append(`
                    <div class="no-data">
                        Нет нарушений по специальным подразделениям (ПДО/ОГМ/ОТиТБ/ОГТ)
                    </div>
                `);
                    return;
                }
                let totalIndex = 0;
                for (const subDivName in violationsBySubDivision) {
                    if (violationsBySubDivision.hasOwnProperty(subDivName)) {
                        const criteriaScores = violationsBySubDivision[subDivName];
                        rowsContainer.append(`
                        <div class="subdivision-header" data-subdivision="${subDivName}">
                            <strong>${subDivName}</strong>
                        </div>
                    `);
                        for (const criteria in criteriaScores) {
                            if (criteriaScores.hasOwnProperty(criteria)) {
                                const totalScore = criteriaScores[criteria];
                                const row = `
                                <div class="report-row" data-index="${totalIndex}">
                                    <div class="report-column">${subDivName}</div>
                                    <div class="report-column">${criteria}</div>
                                    <div class="report-column score-column">${totalScore}</div>
                                </div>
                            `;
                                rowsContainer.append(row);
                                totalIndex++;
                            }
                        }
                        rowsContainer.append('<div class="subdivision-divider"></div>');
                    }
                }
                rowsContainer.find('.subdivision-divider').last().remove();
                rowsContainer.append('<div class="report-divider"></div>');
                for (const subDivName in violationsBySubDivision) {
                    if (violationsBySubDivision.hasOwnProperty(subDivName)) {
                        const criteriaScores = violationsBySubDivision[subDivName];
                        const baseBonus = 5.0;
                        let totalPenalty = 0;
                        const appliedPenalties = [];
                        const safetyScore = criteriaScores['Безопасность и охрана труда'] || 0;
                        const docsScore = criteriaScores['Документация'] || 0;
                        if (safetyScore >= 6) {
                            totalPenalty += 2.5;
                            appliedPenalties.push('Безопасность и охрана труда: -2.5%');
                        }
                        if (docsScore >= 6) {
                            totalPenalty += 2.5;
                            appliedPenalties.push('Документация: -2.5%');
                        }
                        const finalBonus = Math.max(0, baseBonus - totalPenalty);
                        let penaltiesHtml = '';
                        if (appliedPenalties.length > 0) {
                            appliedPenalties.forEach(penalty => {
                                penaltiesHtml += `
                            <div class="penalty-row">
                                <span class="penalty-label">${penalty}</span>
                            </div>
                        `;
                            });
                        }
                        const bonusRow = `
                        <div class="bonus-calculation">
                            <h4>Расчет премии для ${subDivName}</h4>
                            <div class="bonus-row">
                                <span class="bonus-label">Базовая премия:</span>
                                <span class="bonus-value">${baseBonus.toFixed(2)} %</span>
                            </div>
                            ${appliedPenalties.length > 0 ? `
                                <div class="bonus-section">
                                    <div class="bonus-section-title">Примененные штрафы:</div>
                                    ${penaltiesHtml}
                                </div>
                            ` : `
                                <div class="bonus-row">
                                    <span class="bonus-label">Штрафы не применены</span>
                                    <span class="bonus-value">-</span>
                                </div>
                            `}
                            <div class="bonus-row final-bonus">
                                <span class="bonus-label">Финальная премия:</span>
                                <span class="bonus-value">${finalBonus.toFixed(2)} %</span>
                            </div>
                        </div>
                        <div style="height: 20px;"></div>
                    `;
                        rowsContainer.append(bonusRow);
                    }
                }
            }
            catch (error) {
                console.error('Ошибка при загрузке данных для специального отчета:', error);
                rowsContainer.append(`
                <div class="no-data error">
                    Ошибка при загрузке данных: ${error.message || 'Неизвестная ошибка'}
                </div>
            `);
            }
        });
    }
    print(event) {
        const _super = Object.create(null, {
            print: { get: () => super.print }
        });
        return __awaiter(this, void 0, void 0, function* () {
            const inspectionId = $(event.currentTarget).data('inspectionId');
            this.reports = [
                {
                    name: 'Отчет по участку',
                    api: '/api/report/print/inspection-workshop',
                    params: {
                        'id': inspectionId
                    }
                },
                {
                    name: 'Отчет по службам',
                    api: '/api/report/print/inspection-services',
                    params: {}
                }
            ];
            yield _super.print.call(this, event);
        });
    }
    displayPage(url, param, ...callbacks) {
        return __awaiter(this, void 0, void 0, function* () {
            const request = yield this.requestToApi(url, 'GET', param);
            this.renderInspections(request.data);
            callbacks.forEach(callback => callback === null || callback === void 0 ? void 0 : callback(request.data, request.count));
        });
    }
    // Группировка инспекций по месяцам
    groupByMonth(inspections) {
        const groups = {};
        inspections.forEach(insp => {
            const date = new Date(insp.dateInspection);
            const monthKey = date.toLocaleString('ru-RU', { month: 'long', year: 'numeric' });
            const formattedKey = monthKey.charAt(0).toUpperCase() + monthKey.slice(1);
            if (!groups[formattedKey])
                groups[formattedKey] = [];
            groups[formattedKey].push(insp);
        });
        return groups;
    }
    parseMonthString(monthKey) {
        const [monthName, year] = monthKey.split(' ');
        const monthIndex = this.getMonthIndex(monthName);
        return new Date(parseInt(year), monthIndex, 1);
    }
    getMonthIndex(monthName) {
        const months = ['январь', 'февраль', 'март', 'апрель', 'май', 'июнь',
            'июль', 'август', 'сентябрь', 'октябрь', 'ноябрь', 'декабрь'];
        return months.findIndex(m => m.toLowerCase() === monthName.toLowerCase());
    }
    // Основной метод отрисовки
    renderInspections(inspections) {
        const grouped = this.groupByMonth(inspections);
        const container = this.rowContainer;
        container.empty();
        const sortedMonths = Object.keys(grouped).sort((a, b) => {
            return this.parseMonthString(a).getTime() - this.parseMonthString(b).getTime();
        });
        for (const monthKey of sortedMonths) {
            const monthInspections = grouped[monthKey];
            const monthSection = $(`
                <div class="month-section" data-month="${monthKey}">
                    <div class="month-header">
                        <span class="month-name">${monthKey}</span>
                        <span class="month-toggle">▼</span>
                    </div>
                    <div class="month-cards"></div>
                </div>
            `);
            const cardsContainer = monthSection.find('.month-cards');
            monthInspections.forEach(inspection => {
                const card = this.createRow(inspection);
                cardsContainer.append(card);
                this.localCache.set(inspection.id, inspection);
            });
            // По умолчанию скрываем карточки
            cardsContainer.hide();
            // Обработчик клика на заголовок
            monthSection.find('.month-header').on('click', () => {
                cardsContainer.slideToggle(200);
                monthSection.find('.month-toggle').text(cardsContainer.is(':visible') ? '▼' : '▶');
            });
            container.append(monthSection);
        }
    }
    // Вставка новой секции месяца в правильном порядке
    insertMonthSection($newSection) {
        const container = this.rowContainer;
        const newMonthKey = $newSection.data('month');
        const newDate = this.parseMonthString(newMonthKey);
        let inserted = false;
        container.children('.month-section').each((_, el) => {
            const $el = $(el);
            const existingDate = this.parseMonthString($el.data('month'));
            if (newDate < existingDate) {
                $newSection.insertBefore($el);
                inserted = true;
                return false;
            }
        });
        if (!inserted) {
            container.append($newSection);
        }
        // Добавляем обработчик клика
        $newSection.find('.month-header').on('click', () => {
            const $cards = $newSection.find('.month-cards');
            $cards.slideToggle(200);
            $newSection.find('.month-toggle').text($cards.is(':visible') ? '▼' : '▶');
        });
    }
    // Добавление одной инспекции в соответствующую группу
    addInspectionToGroup(inspection) {
        const date = new Date(inspection.dateInspection);
        const monthKey = date.toLocaleString('ru-RU', { month: 'long', year: 'numeric' });
        const formattedKey = monthKey.charAt(0).toUpperCase() + monthKey.slice(1);
        let monthSection = $(`.month-section[data-month="${formattedKey}"]`);
        if (monthSection.length === 0) {
            monthSection = $(`
                <div class="month-section" data-month="${formattedKey}">
                    <div class="month-header">
                        <span class="month-name">${formattedKey}</span>
                        <span class="month-toggle">▼</span>
                    </div>
                    <div class="month-cards"></div>
                </div>
            `);
            this.insertMonthSection(monthSection);
        }
        const cardsContainer = monthSection.find('.month-cards');
        const card = this.createRow(inspection);
        cardsContainer.append(card);
        this.localCache.set(inspection.id, inspection);
    }
    deleteRow(rowIndex) {
        return new Promise((resolve) => {
            const $row = $(`#${rowIndex}`);
            const $monthSection = $row.closest('.month-section');
            $row.fadeOut(300, () => {
                $row.remove();
                this.localCache.delete(rowIndex);
                if ($monthSection.find('.table-card').length === 0) {
                    $monthSection.remove();
                }
                resolve();
            });
        });
    }
}
$(document).ready(() => {
    new Inspection();
});
