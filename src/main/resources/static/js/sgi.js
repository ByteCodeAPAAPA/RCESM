var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
class Sgi extends Base {
    constructor(itemsPerPage = 16, visibleRow = Infinity) {
        super($(`.table-content-rows`), itemsPerPage, visibleRow, () => {
            this.displayPage('/api/sgi/get-page-sgi', undefined, (data, count) => this.buildPagination(data, count)).catch(console.error);
        });
        this.filters = {};
        this.openSubSgi = (event) => {
            if ($(event.target).is('input')) {
                return;
            }
            const $currentRow = $(event.currentTarget).closest('.row-items-row');
            const $innerRows = $currentRow.siblings('.row-items-inner-row');
            $innerRows.slideToggle(400);
        };
        this.createSgi = (event) => __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            const button = $(event.target);
            const form = button.closest('form').get(0);
            const dialog = $('#create-dialog');
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }
            button.prop('disabled', true);
            const formData = new FormData();
            const employeeInput = dialog.find('input[name="hiddenEmployee"]').val();
            const employee = JSON.parse(employeeInput);
            const jsonData = {
                workcenter: dialog.find('input[name="workcenter"]').val(),
                event: dialog.find('textarea[name="event"]').val(),
                actions: dialog.find('textarea[name="actions"]').val(),
                department: dialog.find('select[name="department"]').val(),
                employee: employee,
                desiredDate: dialog.find('input[name="desiredDate"]').val(),
                note: dialog.find('textarea[name="note"]').val(),
                parentId: dialog.find('input[name="parentId"]').val()
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
                const newSgi = yield this.createEntity('/api/sgi/create-sgi', formData);
                this.dialog.close("create-dialog");
                const newRow = this.createRow(newSgi);
                if ($(`.table-content-rows`).find('.row-items-row').length === this.itemsPerPage &&
                    !dialog.find('[name="parentId"]').val().length) {
                    yield $('#last-page').click();
                    $(`.table-content-rows`).append(newRow);
                    this.createNotification('Создано новое мероприятие под номером ' + newSgi.number, NotificationType.SUCCESS);
                }
                else if (newSgi.parent == null) {
                    $(`.table-content-rows`).append(newRow);
                    this.createNotification('Создано новое мероприятие под номером ' + newSgi.number, NotificationType.SUCCESS);
                }
                else {
                    const parentSgi = this.localCache.get(newSgi.parent);
                    parentSgi.subSGI.push(newSgi);
                    this.localCache.set(parentSgi.id, parentSgi);
                    yield this.updateRow(parentSgi, parentSgi.id);
                    this.createNotification('Создана новая подзадача для мероприятия под номером ' + parentSgi.number, NotificationType.SUCCESS);
                }
                this.localCache.set(newSgi.id, newSgi);
                button.prop('disabled', false);
            }
            catch (error) {
                this.createNotification('Ошибка при создании SGI', NotificationType.ERROR);
                button.prop('disabled', false);
            }
        });
        this.uploadImages = function (event) {
            return __awaiter(this, void 0, void 0, function* () {
                const $this = $(event.target);
                $this.prop('disabled', true);
                const currentDialog = $this.closest('dialog');
                const inputFiles = currentDialog.find('[name="additionalFiles"]');
                const imageContainer = currentDialog.find('.file-list');
                // Сохраняем ссылку на класс или контекст
                // Предположим, что localCache - это свойство класса
                const that = this; // или const localCache = this.localCache;
                inputFiles.off('change').on('change', function (e) {
                    return __awaiter(this, void 0, void 0, function* () {
                        e.preventDefault();
                        const input = e.target;
                        const files = input.files;
                        if (!files)
                            return;
                        input.files = new DataTransfer().files;
                        // Используем сохраненную ссылку
                        if (!that.localCache.has('imagesMap')) {
                            that.localCache.set('imagesMap', new Map());
                        }
                        const imagesMap = that.localCache.get('imagesMap');
                        for (let i = 0; i < files.length; i++) {
                            const file = files[i];
                            if (!imagesMap.has(file.name)) {
                                imagesMap.set(file.name, file);
                            }
                        }
                        const dataTransfer = new DataTransfer();
                        for (const [fileName, file] of imagesMap) {
                            if (file instanceof File) {
                                dataTransfer.items.add(file);
                                const imageUrl = URL.createObjectURL(file);
                                const fileItem = `
                <div class="file-item">
                    <img src="${imageUrl}" alt="${file.name}">
                </div>`;
                                imageContainer.append(fileItem);
                                imagesMap.set(file.name, null);
                            }
                        }
                        const validFileMap = that.localCache.has('validFileMap')
                            ? that.localCache.get('validFileMap')
                            : new Map();
                        for (let i = 0; i < dataTransfer.files.length; i++) {
                            const file = dataTransfer.files[i];
                            validFileMap.set(file.name, file);
                        }
                        that.localCache.set('validFileMap', validFileMap);
                        const newDataTransfer = new DataTransfer();
                        validFileMap.forEach(file => newDataTransfer.items.add(file));
                        input.files = newDataTransfer.files;
                    });
                });
                inputFiles.trigger('click');
                $(document).on('click', () => $('.context-menu').remove());
                $this.prop('disabled', false);
            });
        };
        this.edit = function (event) {
            return __awaiter(this, void 0, void 0, function* () {
                const currentRow = $(event.target).closest('.row-items-row');
                const currentId = $(currentRow).attr('id');
                const currentSGI = this.localCache.get(currentId);
                const dialog = $('#editing-dialog');
                const isParentSGI = currentSGI.parent === null;
                if (isParentSGI && !dialog.find('#createSubSGI').length) {
                    dialog.find('.modal-footer').prepend(`<button class="btn btn-primary" id="createSubSGI">Создать подзадачу</button>`);
                }
                else if (!isParentSGI && dialog.find('#createSubSGI').length) {
                    dialog.find('#createSubSGI').remove();
                }
                for (const key in currentSGI) {
                    if (!currentSGI.hasOwnProperty(key))
                        continue;
                    const value = currentSGI[key];
                    const field = dialog.find(`[data-field="${key}"]`);
                    if (!field.length)
                        continue;
                    if (key === 'imagesSGI')
                        continue;
                    if (key === 'employee') {
                        field.val(value.name);
                        continue;
                    }
                    field.val(value || '');
                }
                dialog.find('.document').attr('id', currentSGI.id);
                this.dialog.open('editing-dialog');
                yield this.renderImages('#editing-dialog', 'edit', currentSGI, currentSGI.imagesSGI);
                $('#editing-dialog #saveEditBtn').off('click').on('click', (e) => __awaiter(this, void 0, void 0, function* () {
                    if (currentSGI.agree) {
                        this.ccreateNotification('Нельзя редактировать выполненное мероприятие', NotificationType.ERROR);
                        return;
                    }
                    e.preventDefault;
                    const formData = new FormData();
                    formData.append('id', currentId);
                    formData.append('factExecutionSGIBool', 'false');
                    $(dialog).find('[data-field]').each((_, el) => {
                        if (el.type !== 'file') {
                            formData.append(el.dataset.field, el.value);
                        }
                        else {
                            for (let file of el.files) {
                                formData.append(el.dataset.field, file);
                            }
                        }
                    });
                    const updateSGI = yield $.ajax({
                        url: '/api/sgi/update',
                        type: 'PATCH',
                        data: formData,
                        processData: false,
                        contentType: false,
                        dataType: 'json',
                        error: () => {
                            this.dialog.close('editing-dialog');
                            dialog.find('#createSubSGI').remove();
                            this.createNotification('Редактировать может только создатель задачи', NotificationType.ERROR);
                        }
                    });
                    this.localCache.set(currentId, updateSGI);
                    this.localCache.delete('validFileMap');
                    if (updateSGI.parent != null) {
                        const parentSGI = this.localCache.get(updateSGI.parent);
                        parentSGI.subSGI = parentSGI.subSGI.filter((sub) => sub.id !== updateSGI.id);
                        parentSGI.subSGI.push(updateSGI);
                        yield this.updateRow(parentSGI, parentSGI.id);
                        const parentRow = $(`.row-items-row[id="${parentSGI.id}"]`);
                        const hamburger = parentRow.find('.hamburger');
                        if (hamburger.length > 0) {
                            const fakeEvent = {
                                currentTarget: hamburger[0],
                                target: hamburger[0],
                                preventDefault: () => { },
                                stopPropagation: () => { }
                            };
                            yield this.openSubSgi(fakeEvent);
                        }
                    }
                    else {
                        yield this.updateRow(updateSGI, currentId);
                    }
                    this.dialog.close('editing-dialog');
                    dialog.find('#createSubSGI').remove();
                    this.createNotification('Мероприятие успешно отредактировано', NotificationType.SUCCESS);
                }));
                $('#createSubSGI').off('click').on('click', (e) => __awaiter(this, void 0, void 0, function* () {
                    if (currentSGI.agree) {
                        this.createNotification('Нельзя редактировать выполненное мероприятие', NotificationType.ERROR);
                    }
                    this.dialog.close('editing-dialog');
                    dialog.find('#createSubSGI').remove();
                    const createDialog = $('#create-dialog');
                    this.dialog.open('create-dialog');
                    createDialog.find('[name="parentId"]').val(currentId);
                }));
                dialog.find('#cancelButton').off('click').on('click', () => {
                    this.localCache.delete('validFileMap');
                    this.localCache.delete('imagesMap');
                    this.dialog.close('editing-dialog');
                    dialog.find('#createSubSGI').remove();
                });
            });
        };
        this.renderImages = (currentDialog, type, currentSGI, images) => __awaiter(this, void 0, void 0, function* () {
            const base64ToFile = (base64, name) => {
                const arr = base64.split(','), mime = arr[0].match(/:(.*?);/)[1], bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
                for (let i = 0; i < n; i++)
                    u8arr[i] = bstr.charCodeAt(i);
                return new File([u8arr], name, { type: mime });
            };
            const dialog = $(currentDialog);
            const imageContainer = dialog.find('.file-list');
            imageContainer.empty();
            const validFileMap = new Map();
            if (!images || images === null) {
                const url = `/api/sgi/get-images-${type === 'fact' ? 'fact-sgi' : 'sgi'}`;
                try {
                    images = yield $.ajax({
                        url: url,
                        type: 'GET',
                        data: { id: type === 'fact' ? currentSGI.factExecution.id : currentSGI.id }
                    });
                }
                catch (error) {
                    images = [];
                }
                const processedImages = Array.isArray(images) ? images : [];
                if (type === 'fact') {
                    currentSGI.factExecution.imagesFactSGI = processedImages;
                }
                else {
                    currentSGI.imagesSGI = processedImages;
                }
            }
            for (const image of images || []) {
                const imgHtml = `
            <div class="file-item" id="${image.id}">
                <img src="${image.data}" alt="${image.name}">
            </div>`;
                imageContainer.append(imgHtml);
                this.localCache.set(image.name, null);
                validFileMap.set(image.name, base64ToFile(image.data, image.name));
            }
            this.localCache.set('validFileMap', validFileMap);
            let input = dialog.find('input[type="file"]').clone()[0];
            const dataTransfer = new DataTransfer();
            validFileMap.forEach(file => dataTransfer.items.add(file));
            input.files = dataTransfer.files;
            dialog.find('input[type="file"]').replaceWith(input);
        });
        this.zoomImageOnClick = (event) => __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            event.stopPropagation();
            const target = $(event.target);
            const imgSrc = target.attr('src');
            const imgAlt = target.attr('alt');
            const currentDialog = target.closest('dialog');
            if (!$('#imagePreviewModal').length) {
                currentDialog.append(`
                <div id="imagePreviewModal" style="display: none; position: fixed; z-index: 10000; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.9);">
                    <span class="close" style="position: absolute; top: 15px; right: 35px; color: #f1f1f1; font-size: 40px; font-weight: bold; cursor: pointer;">&times;</span>
                    <img class="modal-content" id="previewImage" style="margin: auto; display: block; width: 80%; max-width: 700px; margin-top: 40px;">
                </div>
            `);
                // Обработчики для закрытия модального окна
                $(document).on('click', '#imagePreviewModal .close, #imagePreviewModal', function (e) {
                    if (e.target.id === 'imagePreviewModal' || e.target.className === 'close') {
                        $('#imagePreviewModal').hide();
                    }
                });
                // Закрытие по ESC
                $(document).on('keydown', function (e) {
                    if (e.key === 'Escape' && $('#imagePreviewModal').is(':visible')) {
                        $('#imagePreviewModal').hide();
                    }
                });
            }
            $('#previewImage').attr('src', imgSrc).attr('alt', imgAlt);
            $('#imagePreviewModal').show();
        });
        this.imageContextMenu = (event) => __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            const mouseEvent = event;
            const target = event.target;
            const currentDialog = $(target).closest('dialog');
            $('.context-menu').remove();
            const menu = $(`
        <div class="context-menu">
            <button class="context-btn">Удалить</button>
        </div>
    `);
            $(currentDialog).append(menu);
            // Получаем позицию диалога
            const dialogOffset = $(currentDialog).offset();
            // Позиционируем меню
            menu.css({
                'position': 'absolute',
                'top': `${mouseEvent.pageY - dialogOffset.top}px`,
                'left': `${mouseEvent.pageX - dialogOffset.left}px`,
                'background': '#f8f9fa',
                'border': '1px solid #dee2e6',
                'padding': '8px',
                'border-radius': '4px',
                'box-shadow': '0 4px 12px rgba(0,0,0,0.15)',
                'z-index': '1000'
            });
            menu.find('.context-btn').css({
                'background': '#dc3545',
                'color': 'white',
                'border': 'none',
                'padding': '6px 12px',
                'cursor': 'pointer',
                'border-radius': '3px',
                'font-size': '0.875rem'
            });
            menu.find('.context-btn').click(() => {
                const imgElement = target;
                const imgName = imgElement.alt;
                if (this.localCache.has('imagesMap')) {
                    const imagesMap = this.localCache.get('imagesMap');
                    imagesMap.delete(imgName);
                }
                const validFileMap = this.localCache.get('validFileMap');
                if (validFileMap) {
                    validFileMap.delete(imgName);
                    this.localCache.set('validFileMap', validFileMap);
                    const input = currentDialog.find('input[type="file"]').clone()[0];
                    const dataTransfer = new DataTransfer();
                    validFileMap.forEach((file) => {
                        dataTransfer.items.add(file);
                    });
                    input.files = dataTransfer.files;
                    currentDialog.find('input[type="file"]').replaceWith(input);
                }
                $(imgElement).remove();
                menu.remove();
            });
            const closeMenu = (e) => {
                if (!menu.is(e.target) && menu.has(e.target).length === 0) {
                    menu.remove();
                    $(document).off('click', closeMenu);
                }
            };
            setTimeout(() => {
                $(document).on('click', closeMenu);
            }, 0);
        });
        this.showRowContextMenu = (event) => __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            const mouseEvent = event;
            const $row = $(event.currentTarget);
            const rowId = $row.attr('id');
            this.createContextMenu([
                {
                    label: 'Удалить',
                    idAction: "deleteSgiButton",
                    action: () => {
                        const sgi = this.localCache.get(rowId);
                        if (sgi.agree) {
                            this.createNotification('Нельзя удалять согласованное мероприятие!', NotificationType.ERROR);
                            return;
                        }
                        this.createConfirmationDialog("Подтвердите удаление мероприятия").then((confirmed) => {
                            // @ts-ignore
                            if (confirmed) {
                                this.deleteEntity(`/api/sgi/delete/${rowId}`).then(() => {
                                    this.deleteRow(rowId);
                                    this.localCache.delete(rowId);
                                    if (sgi.parent != null) {
                                        const parentSgi = this.localCache.get(sgi.parent);
                                        parentSgi.subSGI = parentSgi.subSGI.filter((sub) => sub.id !== sgi.id);
                                        this.localCache.set(parentSgi.id, parentSgi);
                                        if (parentSgi.subSGI.length === 0) {
                                            this.updateRow(parentSgi, parentSgi.id);
                                        }
                                    }
                                    this.createNotification("Мероприятие успешно удалено", NotificationType.SUCCESS);
                                }).catch((error) => {
                                    this.createNotification("Возникла ошибка при удалении мероприятия", NotificationType.ERROR);
                                    console.error(error);
                                });
                            }
                        });
                    }
                },
                {
                    label: 'Печать',
                    idAction: "printSgiButton",
                    action: () => {
                        window.open(`/api/report/print/sgi?ids=${[...this.selectedRows].join(',')}`);
                    }
                }
            ], mouseEvent.clientX, mouseEvent.clientY);
        });
        this.createHandler('click', '#create-button', () => this.dialog.open('create-dialog'), true);
        this.createHandler('click', '#createBtn', this.createSgi, true);
        this.createHandler('click', '.area-modal', this.workWithModal.bind(this), true);
        this.createHandler('click', '.file-upload', this.uploadImages.bind(this), true);
        this.createHandler('click', '.editing-btn', this.edit.bind(this), true);
        this.createHandler('click', '.execution-btn', this.fact.bind(this), true);
        this.createHandler('click', '#toggleAgreement', this.agree.bind(this), true);
        this.createHandler('click', 'dialog img', this.zoomImageOnClick.bind(this), true);
        this.createHandler('contextmenu', 'img', this.imageContextMenu.bind(this), true);
        this.createHandler('dblclick', '.row-items-row', this.selecteRow.bind(this), false);
        this.createHandler('contextmenu', '.selected-row', this.showRowContextMenu.bind(this), true);
        this.createHandler('click', '.pagination .page-btn', this.enterPage.bind(this), true);
        this.createHandler('click', '#filter-button', this.filterDialog.bind(this), true);
        this.createHandler('click', '.print-menu-item', this.printFromMenu.bind(this), true);
        this.createHandler('click', '.document', this.openDocument.bind(this), true);
        this.createHandler('click', '.download', this.handleDownloadFile.bind(this), true);
        this.createHandler('click', '.hamburger', this.openSubSgi.bind(this), false);
    }
    createRow(sgi) {
        var _a;
        const hamburger = `
            <label class="hamburger tooltip-trigger" data-description="Раскрыть список подзадач">
                <input type="checkbox">
                <svg viewBox="0 0 32 32">
                    <path class="line line-top-bottom" d="M27 10 13 10C10.8 10 9 8.2 9 6 9 3.5 10.8 2 13 2 15.2 2 17 3.8 17 6L17 26C17 28.2 18.8 30 21 30 23.2 30 25 28.2 25 26 25 23.8 23.2 22 21 22L7 22"></path>
                    <path class="line" d="M7 16 27 16"></path>
                </svg>                    
            </label>`;
        const borderClass = sgi.color === 'RED' ? 'border-danger' : sgi.color === 'YELLOW' ? 'border-warning' : sgi.color === 'GREEN' ? 'border-good' : '';
        const innerRows = ((_a = sgi.subSGI) === null || _a === void 0 ? void 0 : _a.map(sub => this.createInnerRow(sub, sgi)).join('')) || '';
        const row = `
         <div class="row-items" data-index="${sgi.id}">
            <div class="row-items-row ${sgi.color === 'GREY' ? 'complete' : ''}" id="${sgi.id}" data-inner="true">
                <div class="row-item" data-field="number" style="width: var(--no);">
                    ${sgi.subSGI && sgi.subSGI.length > 0 ? hamburger : ''}
                    <span class="${borderClass}">${sgi.number}</span>
                </div>
                <div class="row-item" data-field="workcenter" style="width: var(--workcenter);">${sgi.workcenter}</div>
                <div class="row-item" data-field="event" style="width: var(--event);">${sgi.event}</div>
                <div class="row-item" data-field="actions" style="width: var(--action);">${sgi.actions}</div>
                <div class="row-item" data-field="departament" style="width: var(--department);">${sgi.departmentName}</div>
                <div class="row-item" data-field="employee" style="width: var(--employee);">${sgi.employee.name}</div>
                <div class="row-item" data-field="desiredDate" style="width: var(--desiredDate);">${this.formatDate(sgi.desiredDate)}</div>
                <div class="row-item" data-field="note" style="width: var(--note);">${sgi.note}</div>
                <div class="row-item" data-field="planDate" style="width: var(--planDate);">
                   <span class="${borderClass}">${this.formatDate(sgi.planDate)}</span>
                </div>
                <div class="row-item" data-field="comment" style="width: var(--comment);">${sgi.comment}</div>
                <div class="row-item" style="width: var(--editing);">
                    <button type="button" name="editingButton" class="btn btn-info btn-sm editing-btn tooltip-trigger" data-description="Открыть окно редактирования">
                        <i class="bi bi-pencil-square"></i>
                    </button>
                </div>
                <div class="row-item" style="width: var(--executions);">
                    <button type="button" name="executionButton" class="btn btn-info btn-sm execution-btn tooltip-trigger" data-description="Открыть окно факта выполнения">
                        ✔
                    </button>
                </div>
                <div class="row-item" style="width: var(--status);">
                    <div class="checkbox-wrapper-31">
                        <input type="checkbox" id="toggleAgreement" ${sgi.agree ? 'checked' : ''}>
                        <svg viewBox="0 0 35.6 35.6">
                            <circle class="background" cx="17.8" cy="17.8" r="17.8"></circle>
                            <circle class="stroke" cx="17.8" cy="17.8" r="14.37"></circle>
                            <polyline class="check" points="11.78 18.12 15.55 22.23 25.17 12.87"></polyline>
                        </svg>
                    </div>
                </div>
            </div>
            <div class="row-items-inner-row">
                ${innerRows}
            </div>
         </div>`;
        return $(row);
    }
    createInnerRow(sgi, inner) {
        const borderClass = sgi.color === 'RED'
            ? 'border-danger' :
            sgi.color === 'YELLOW'
                ? 'border-warning' :
                sgi.color === 'GREEN'
                    ? 'border-good' : '';
        const hamburger = `
            <label class="hamburger">
                <input type="checkbox">
                <svg viewBox="0 0 32 32">
                    <path class="line line-top-bottom" d="M27 10 13 10C10.8 10 9 8.2 9 6 9 3.5 10.8 2 13 2 15.2 2 17 3.8 17 6L17 26C17 28.2 18.8 30 21 30 23.2 30 25 28.2 25 26 25 23.8 23.2 22 21 22L7 22"></path>
                    <path class="line" d="M7 16 27 16"></path>
                </svg>                    
            </label>`;
        const row = `
            <div class="row-items-row ${sgi.color === 'GREY' ? 'complete' : ''}" id="${sgi.id}" data-inner="true">
                <div class="row-item" data-field="number" style="width: var(--no);">
                    <span class="${borderClass}"></span>
                </div>
                <div class="row-item" data-field="workcenter" style="width: var(--workcenter);">${sgi.workcenter}</div>
                <div class="row-item" data-field="event" style="width: var(--event);">${sgi.event}</div>
                <div class="row-item" data-field="actions" style="width: var(--action);">${sgi.actions}</div>
                <div class="row-item" data-field="departament" style="width: var(--department);">${sgi.departmentName}</div>
                <div class="row-item" data-field="employee" style="width: var(--employee);">${sgi.employee.name}</div>
                <div class="row-item" data-field="desiredDate" style="width: var(--desiredDate);">${this.formatDate(sgi.desiredDate)}</div>
                <div class="row-item" data-field="note" style="width: var(--note);">${sgi.note}</div>
                <div class="row-item" data-field="planDate" style="width: var(--planDate);">
                   <span class="${borderClass}">${this.formatDate(sgi.planDate)}</span>
                </div>
                <div class="row-item" data-field="comment" style="width: var(--comment);">${sgi.comment}</div>
                <div class="row-item" style="width: var(--editing);">
                    <button type="button" class="btn btn-info btn-sm editing-btn">
                        <i class="bi bi-pencil-square"></i>
                    </button>                            
                </div>
                <div class="row-item" style="width: var(--executions);">
                    <button type="button" class="btn btn-info btn-sm execution-btn">
                        ✔
                    </button>
                </div>
                <div class="row-item" style="width: var(--status);">
                    <div class="checkbox-wrapper-31">
                        <input type="checkbox" id="toggleAgreement" ${sgi.agree ? 'checked' : ''}>
                        <svg viewBox="0 0 35.6 35.6">
                            <circle class="background" cx="17.8" cy="17.8" r="17.8"></circle>
                            <circle class="stroke" cx="17.8" cy="17.8" r="14.37"></circle>
                            <polyline class="check" points="11.78 18.12 15.55 22.23 25.17 12.87"></polyline>
                        </svg>
                    </div>
                </div>
            </div>`;
        const parentRow = $(`.row-items-row[id="${inner}"]`);
        // parentRow.closest('.row-items').children('.row-items-inner-row').append(row);
        if (parentRow.find('.hamburder').length === 0) {
            parentRow.find('.row-item').first().append(hamburger);
        }
        return row;
    }
    onScroll() {
    }
    buildPagination(data, count) {
        const $p = $('.pagination');
        $p.empty();
        const totalElement = count;
        const totalPages = Math.ceil(totalElement / this.itemsPerPage);
        if (totalPages <= 1)
            return;
        const createBtn = (label, page, extraClass = '') => {
            const btn = $(`<button class="btn btn-secondary page-btn ${extraClass}" data-page="${page}">${label}</button>`);
            if (page === this.currentPage)
                btn.addClass('active');
            return btn;
        };
        // Предыдущая страница
        if (this.currentPage > 1) {
            $p.append(createBtn('‹', this.currentPage - 1, 'prev-btn'));
        }
        else {
            $p.append($('<button class="btn btn-secondary" disabled>‹</button>'));
        }
        // Номера страниц
        for (let i = 1; i <= totalPages; i++) {
            const btn = createBtn(i, i);
            if (i === totalPages) {
                btn.attr('id', 'last-page');
            }
            $p.append(btn);
        }
        // Следующая страница
        if (this.currentPage < totalPages) {
            $p.append(createBtn('›', this.currentPage + 1, 'next-btn'));
        }
        else {
            $p.append($('<button class="btn btn-secondary" disabled>›</button>'));
        }
        data.forEach((sgi) => {
            sgi.subSGI.forEach((sub) => {
                // this.createInnerRow(sub, sgi.id);
                this.localCache.set(sub.id, sub);
            });
        });
    }
    enterPage(event) {
        return __awaiter(this, void 0, void 0, function* () {
            const page = parseInt($(event.currentTarget).data('page'), 10);
            if (this.currentPage === page)
                return;
            if (!isNaN(page) && page >= 1) {
                const unlock = this.lockScreen();
                try {
                    const data = yield $.ajax({
                        url: '/api/sgi/get-page-sgi',
                        type: 'GET',
                        data: {
                            page: page,
                            size: this.itemsPerPage
                        }
                    });
                    this.localCache.clear();
                    $(`.table-content-rows`).empty();
                    data.data.forEach((sgi) => {
                        const row = this.createRow(sgi);
                        $(`.table-content-rows`).append(row);
                        this.localCache.set(sgi.id, sgi);
                        if (sgi.subSGI && sgi.subSGI.length) {
                            sgi.subSGI.forEach(subSgi => this.localCache.set(subSgi.id, subSgi));
                        }
                    });
                    this.applyFiltersToCurrentPage();
                }
                catch (error) {
                    console.error('Ошибка загрузки страницы:', error);
                }
                finally {
                    this.currentPage = page;
                    unlock();
                }
            }
        });
    }
    workWithModal(event) {
        return __awaiter(this, void 0, void 0, function* () {
            const modalDiv = $(event.currentTarget);
            const fieldName = modalDiv.attr('data-field');
            const currentId = modalDiv.closest('.row-items-row').attr('id');
            let selected;
            if (fieldName === 'subDivision' || fieldName === 'employee') {
                const isEmployee = fieldName === 'employee';
                const dialog = $(isEmployee ? '#employeeDialog' : '#subDivisionDialog');
                const rowContainer = dialog.find('.dialog-content-rows');
                const searchInput = dialog.find('.choice-field input');
                const changeButton = $(isEmployee ? '#changeEmployee' : '#changeSubDivision');
                const data = yield this.cache.get(fieldName);
                const filteredEmployees = data.filter(employee => ['EVENT', 'CONTROL'].some(role => role === employee.role));
                const renderRows = (items) => {
                    rowContainer.empty();
                    items.forEach(item => {
                        var _a;
                        rowContainer.append(`
                    <div class="dialog-content-rows-row" id="${item.id}">
                        <div class="content-row-column col-250">${item.name}</div>
                        ${isEmployee ? `<div class="content-row-column col-250">${((_a = item.subDivision) === null || _a === void 0 ? void 0 : _a.name) || ''}</div>` : ''}
                    </div>`);
                    });
                };
                renderRows(filteredEmployees);
                searchInput.off('input').on('input', function () {
                    const searchText = $(this).val().toString().toLowerCase().trim();
                    const filtered = data.filter((e) => e.name.toLowerCase().includes(searchText));
                    renderRows(filtered);
                });
                this.dialog.open('employeeDialog');
                rowContainer.off('click').on('click', '.dialog-content-rows-row', function (e) {
                    const id = $(e.currentTarget).attr('id');
                    selected = data.find((e) => e.id === Number(id));
                    $('.dialog-content-rows-row').removeClass('selected');
                    $(this).addClass('selected');
                });
                changeButton.off('click').on('click', () => {
                    if (!selected) {
                        this.createNotification(`Выберите ${isEmployee ? 'сотрудника' : 'подразделение'} из списка`, NotificationType.WARNING);
                        return;
                    }
                    modalDiv.text(selected.name);
                    modalDiv.val(selected.name);
                    if (isEmployee) {
                        const employeeJson = JSON.stringify(selected);
                        $('#create-dialog').find('input[name="hiddenEmployee"]').val(employeeJson);
                    }
                    if (currentId) {
                        this.saveMassive[currentId] = Object.assign(Object.assign({}, this.saveMassive[currentId]), { [fieldName]: selected });
                    }
                    else {
                        this.saveMassive[fieldName] = selected;
                    }
                    modalDiv.addClass('change-textarea');
                    this.dialog.close('employeeDialog');
                });
            }
            modalDiv.addClass('change');
        });
    }
    fact(event) {
        return __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            const currentRow = $(event.target).closest('.row-items-row');
            const currentId = $(currentRow).attr('id');
            // @ts-ignore
            const currentSGI = this.localCache.get(currentId);
            const dialog = $('#execution-dialog');
            const executionDialog = document.getElementById('execution-dialog');
            Object.keys(currentSGI.factExecution).forEach(key => {
                const value = currentSGI.factExecution[key];
                const field = executionDialog === null || executionDialog === void 0 ? void 0 : executionDialog.querySelector(`[data-field="${key}"]`);
                if (!field || key === 'imagesFactSGI')
                    return;
                if (key === 'executionDate') {
                    field.value = value ? value.split('.').reverse().join('-') : '';
                }
                else if (field) {
                    field.value = value || '';
                }
            });
            const self = this;
            $(document).off('click', '#execution-dialog #saveBtn').on('click', '#execution-dialog #saveBtn', function () {
                return __awaiter(this, void 0, void 0, function* () {
                    if (currentSGI.agree) {
                        this.createNotification('Нельзя редактировать выполненное мероприятие', NotificationType.INFO);
                        return;
                    }
                    if (dialog.find(`[data-field="executionDate"]`).val() === '') {
                        this.createNotification('Не заполнена дата выполнения', NotificationType.INFO);
                        return;
                    }
                    const formData = new FormData();
                    formData.append('id', currentId);
                    formData.append('factExecutionSGIBool', 'true');
                    $(dialog).find('[data-field]').each((_, el) => {
                        if (el.type !== 'file') {
                            formData.append(el.dataset.field, el.value);
                        }
                        else {
                            for (let file of el.files || []) {
                                formData.append(el.dataset.field, file);
                            }
                        }
                    });
                    try {
                        const updateSGI = yield $.ajax({
                            url: '/api/sgi/update',
                            type: 'PATCH',
                            data: formData,
                            processData: false,
                            contentType: false,
                            dataType: 'json'
                        });
                        self.localCache.set(currentId, updateSGI);
                        self.localCache.delete('validFileMap');
                        yield self.updateRow(updateSGI, null);
                        self.createNotification('Факт выполнения сохранен', NotificationType.SUCCESS);
                        self.dialog.close('execution-dialog');
                    }
                    catch (error) {
                        self.dialog.close('execution-dialog');
                        self.createNotification('Редактировать может только создатель задачи', NotificationType.WARNING);
                        console.error(error);
                    }
                });
            });
            this.dialog.open('execution-dialog');
            yield this.renderImages(dialog, 'fact', currentSGI, currentSGI.factExecution.imagesFactSGI);
            // Клик на крестик
            dialog.find('#cancelButton').off('click').on('click', () => {
                this.localCache.delete('validFileMap');
                this.localCache.delete('imagesMap');
                this.dialog.close('execution-dialog');
            });
            // Клик вне диалога
            dialog.off('click').on('click', (e) => {
                if (e.target.nodeName === 'DIALOG') {
                    this.localCache.delete('validFileMap');
                    this.localCache.delete('imagesMap');
                    this.dialog.close('execution-dialog');
                }
            });
            // Нажатие esc
            $(document).off('keydown').on('keydown', (e) => {
                if (e.key === 'Escape' || e.key === 'Esc') {
                    this.localCache.delete('validFileMap');
                    this.localCache.delete('imagesMap');
                    this.dialog.close('execution-dialog');
                }
            });
        });
    }
    agree(event) {
        return __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            const checkbox = event.target;
            const isChecked = checkbox.checked;
            const currentRow = $(checkbox).closest('.row-items-row');
            const currentId = $(currentRow).attr('id');
            // @ts-ignore
            const currentSGI = this.localCache.get(currentId);
            const self = this;
            const formData = new FormData();
            formData.append("id", currentId);
            formData.append("agreed", isChecked.toString());
            if (!currentSGI.planDate) {
                this.createNotification('Не заполнено поле планируемый срок!', NotificationType.ERROR);
                checkbox.checked = !isChecked;
                return;
            }
            if (isChecked && currentSGI.subSGI && !currentSGI.subSGI.every(sub => sub.agree)) {
                this.createNotification('Все подзадачи должны быть согласованы!', NotificationType.ERROR);
                checkbox.checked = !isChecked;
                return;
            }
            if (!isChecked && (currentSGI === null || currentSGI === void 0 ? void 0 : currentSGI.parent) && this.localCache.get(currentSGI.parent).agree) {
                this.createNotification('Нельзя отменить согласование подзадачи, если родительская задача согласована!', NotificationType.ERROR);
                checkbox.checked = !isChecked;
                return;
            }
            try {
                yield $.ajax({
                    url: '/api/sgi/agree',
                    method: 'PATCH',
                    data: formData,
                    contentType: false,
                    processData: false
                });
                currentSGI.agree = isChecked;
                this.localCache.set(currentId, currentSGI);
                if (isChecked) {
                    currentRow.addClass('complete');
                }
                else {
                    currentRow.removeClass('complete');
                }
                currentRow.find('input[type="checkbox"]').prop('checked', isChecked);
                this.createNotification(isChecked ? 'Мероприятие согласовано' : 'Согласование отменено', NotificationType.SUCCESS);
            }
            catch (error) {
                checkbox.checked = !isChecked;
                this.createNotification('Вы не можете закрывать/открывать мероприятие', NotificationType.ERROR);
            }
        });
    }
    selecteRow(event) {
        const row = $(event.target).closest('.row-items-row');
        const rowId = $(row).attr('id');
        if (row.hasClass('selected-row')) {
            row.removeClass('selected-row');
            this.selectedRows.delete(rowId);
        }
        else {
            row.addClass('selected-row');
            this.selectedRows.add(rowId);
        }
    }
    filterDialog(event) {
        return __awaiter(this, void 0, void 0, function* () {
            event.preventDefault();
            const dialog = $('#filter-dialog');
            this.dialog.open('filter-dialog');
            // Заполняем select сотрудников
            const employeeField = dialog.find('[data-field="employee"]');
            employeeField.empty();
            employeeField.append($('<option>', { value: '', text: 'Все сотрудники' }));
            const employeesData = yield this.cache.get('employee');
            const filteredEmployees = employeesData.filter((employee) => ['EVENT', 'CONTROL'].some((role) => role === employee.role));
            filteredEmployees.forEach((employee) => {
                employeeField.append($('<option>', {
                    value: employee.name,
                    text: employee.name
                }));
            });
            // Сохраняем контекст this
            const self = this;
            // Обработчик применения фильтров
            $(document).off('click', '#filtered').on('click', '#filtered', function () {
                var _a, _b, _c, _d, _e, _f, _g, _h;
                self.filters = {
                    number: ((_a = dialog.find('[data-field="number"]').val()) === null || _a === void 0 ? void 0 : _a.toString().trim()) || '',
                    workcenter: ((_b = dialog.find('[data-field="workcenter"]').val()) === null || _b === void 0 ? void 0 : _b.toString().trim()) || '',
                    event: ((_c = dialog.find('[data-field="event"]').val()) === null || _c === void 0 ? void 0 : _c.toString().trim()) || '',
                    actions: ((_d = dialog.find('[data-field="actions"]').val()) === null || _d === void 0 ? void 0 : _d.toString().trim()) || '',
                    departament: (dialog.find('[data-field="departament"] option:selected').text().trim() === 'Выберите отдел') ? '' : dialog.find('[data-field="departament"] option:selected').text().trim(),
                    employee: ((_e = dialog.find('[data-field="employee"]').val()) === null || _e === void 0 ? void 0 : _e.toString()) || '',
                    desiredDate: ((_f = dialog.find('[data-field="desiredDate"]').val()) === null || _f === void 0 ? void 0 : _f.toString()) || '',
                    planDate: ((_g = dialog.find('[data-field="planDate"]').val()) === null || _g === void 0 ? void 0 : _g.toString()) || '',
                    note: ((_h = dialog.find('[data-field="note"]').val()) === null || _h === void 0 ? void 0 : _h.toString().trim()) || ''
                };
                // Применяем фильтры к текущей странице
                self.applyFiltersToCurrentPage();
                self.dialog.close('filter-dialog');
            });
            // Обработчик сброса фильтров
            dialog.find('#default-filter').off('click').on('click', function () {
                dialog.find('input, textarea, select').val('');
                $('.row-items').show();
                this.dialog('filter-dialog').close();
            });
            // Клик на крестик
            dialog.find('#cancelButton').off('click').on('click', (e) => {
                this.localCache.delete('validFileMap');
                this.localCache.delete('imagesMap');
                this.dialog.close('filter-dialog');
            });
            // Клик вне диалога
            dialog.off('click').on('click', (e) => {
                if (e.target.nodeName === 'DIALOG') {
                    this.localCache.delete('validFileMap');
                    this.localCache.delete('imagesMap');
                    e.target.close();
                }
            });
        });
    }
    applyFiltersToCurrentPage() {
        let hasActiveFilters = false;
        Object.keys(this.filters).forEach(key => {
            const value = this.filters[key];
            if (value && value.trim() !== '') {
                hasActiveFilters = true;
            }
        });
        if (hasActiveFilters) {
            const rows = document.querySelectorAll('.row-items');
            rows.forEach(row => {
                var _a;
                let notMatch = null;
                for (const key of Object.keys(this.filters)) {
                    const value = this.filters[key];
                    if (!value)
                        continue;
                    notMatch = true;
                    const cell = row.querySelector(`[data-field="${key}"]`);
                    if (!cell)
                        continue;
                    const cellValue = ((_a = cell.textContent) === null || _a === void 0 ? void 0 : _a.trim()) || '';
                    if (key === 'desiredDate' || key === 'planDate') {
                        const formattedDate = this.formatDate(value);
                        if (cellValue === formattedDate) {
                            notMatch = false;
                            break;
                        }
                    }
                    else if (cellValue.toLowerCase() === value.toLowerCase()) {
                        notMatch = false;
                        break;
                    }
                }
                row.style.display = notMatch != null && notMatch ? 'none' : '';
            });
        }
    }
    printFromMenu(event) {
        const department = $(event.target).data('department');
        $('<a>', {
            href: `/api/report/print/sgi?department=${department}`,
            download: ''
        }).appendTo('body')[0].click().remove();
    }
    openDocument(event) {
        return __awaiter(this, void 0, void 0, function* () {
            const dialog = $('#documentDialog');
            const currentSGIId = $(event.currentTarget).attr('id');
            const sgi = this.localCache.get(currentSGIId);
            const rowContainer = dialog.find('.dialog-content-rows');
            rowContainer.empty();
            if (sgi.documentId !== null) {
                const document = yield this.requestToApi(`/api/document/get-document/${sgi.documentId}`, "GET");
                this.localCache.set('document', document);
                document.files.forEach((file) => {
                    rowContainer.append(`
                    <div class="dialog-content-rows-row" id="${file.id}">
                        <div class="content-row-column col-450">${file.baseFileName}</div>
                        <div class="content-row-column col-100">${file.type}</div>
                        <div class="content-row-column col-100"><i style="float: right; font-size:1rem; padding: 12px 10px" class="download fas fa-download"></i></div>
                    </div>`);
                });
            }
            rowContainer.append(`
            <div class="dialog-content-rows-row">
                <div class="content-row-column col-450"></div>
                <div class="content-row-column col-100"></div>
                <div class="content-row-column col-100">
                    <i style="float: right; font-size:1rem; padding: 12px 10px" class="uploadIcon upload-file fas fa-file-upload" onclick="$('#fileInput').click()"></i>
                    <input type="file" id="fileInput" style="display: none;"/>
                </div>
            </div>`);
            $(document).off('change', '#fileInput').on('change', '#fileInput', (e) => this.addFileToDocument(e, currentSGIId));
            $(document).on('contextmenu', '.dialog-content-rows-row', (event) => {
                const $row = $(event.currentTarget);
                const fileId = $row.attr('id');
                if (!fileId) {
                    return;
                }
                event.preventDefault();
                const mouseEvent = event;
                this.createContextMenu([
                    {
                        label: 'Удалить файл',
                        idAction: "deleteFileDocumentButton",
                        action: () => {
                            this.deleteEntity(`/api/document/delete-file-from-document/${fileId}`).then(() => {
                                this.createNotification('Файл успешно удален', NotificationType.SUCCESS);
                                this.deleteRow(fileId);
                            });
                        }
                    }
                ], mouseEvent.clientX, mouseEvent.clientY);
            });
            this.dialog.open('documentDialog');
        });
    }
    addFileToDocument(event, sgiId) {
        const formData = new FormData();
        const currentInput = event.currentTarget;
        const sgi = this.localCache.get(sgiId);
        if (currentInput.files) {
            Array.from(currentInput.files).forEach(file => {
                formData.append('files', file);
            });
        }
        const url = sgi.documentId
            ? `/api/document/add-file-to-document-and-get/${sgi.documentId}`
            : `/api/sgi/create-document/${sgi.id}`;
        const requestType = sgi.documentId ? 'PATCH' : 'POST';
        this.requestToApi(url, requestType, formData).then((document) => {
            const dialog = $('#documentDialog');
            const rowContainer = dialog.find('.dialog-content-rows');
            rowContainer.empty();
            for (const file of document.files) {
                rowContainer.append(`
                    <div class="dialog-content-rows-row" id="${file.id}">
                        <div class="content-row-column col-450">${file.baseFileName}</div>
                        <div class="content-row-column col-100">${file.type}</div>
                        <div class="content-row-column col-100"><i style="float: right; font-size:1rem; padding: 12px 10px" class="download fas fa-download"></i></div>
                    </div>`);
            }
            rowContainer.append(`
            <div class="dialog-content-rows-row">
                <div class="content-row-column col-450"></div>
                <div class="content-row-column col-100"></div>
                <div class="content-row-column col-100">
                    <i style="float: right; font-size:1rem; padding: 12px 10px" class="uploadIcon upload-file fas fa-file-upload" onclick="$('#fileInput').click()"></i>
                    <input type="file" id="fileInput" style="display: none;"/>
                </div>
            </div>`);
            sgi.documentId = document.id;
            this.localCache.set(sgi.id, sgi);
            this.createNotification("Файлы добавлены", NotificationType.SUCCESS);
        }).catch(console.error);
        currentInput.value = '';
    }
    handleDownloadFile(event) {
        const fileId = $(event.target).closest('.dialog-content-rows-row').attr('id');
        this.downloadFile(`/api/document/download-document-file/${fileId}`).catch(console.error);
    }
}
$(document).ready(() => {
    new Sgi();
});
