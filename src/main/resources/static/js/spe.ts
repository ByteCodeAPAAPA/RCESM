// @ts-ignore
declare const $: any;

class Spe extends Base {

    constructor(itemsPerPage = Infinity, visibleRow = Infinity) {
        super($(`.table-body`), itemsPerPage, visibleRow, () => {
            this.displayPage('/api/spe/get-page-spe', undefined, (data: any[]) => this.fullData(data)).catch(console.error);
        });
        this.createHandler('click', '.circle-header', this.selecteRows.bind(this),true);
        this.createHandler('click', '.circle-row', this.selecteRow.bind(this), true);
        this.createHandler('click', '#edit-button', () => {
            if (!this.editMode) {
                this.enableEditMode()
            } else this.disableEditMode();
        }, true);
        this.createHandler('click', '#print-button', this.print = this.print.bind(this), true);
        this.createHandler('click', '#create-fgis-button', () => this.dialog.open('create-fgis-dialog'), true);
        this.createHandler('click', '#create-button', () => this.dialog.open('create-dialog'), true);
        this.createHandler('click', '#save-button', () => this.saveSpe(), true);
        this.createHandler('input', '[data-name]', this.inputChanges.bind(this), true);
        this.createHandler('click', '.area-modal', this.workWithModal.bind(this), true);
        this.createHandler('click', '.document', this.openDocument.bind(this), true);
        this.createHandler('change', '#fileInput', this.addFileToDocument.bind(this), true);
        this.createHandler('click', '.download', this.handleDownloadFile.bind(this), true);
        this.createHandler('click', '#createFgisBtn', this.createFgisSpe, true)
        this.createHandler('click', '#createBtn', this.createSpe, true);
        this.createHandler('click', '.filter-status', this.filterButtonHandler, true);
        this.createHandler('click', '.employee-button', this.employeeHandler.bind(this), true);
        this.createHandler('click', '.subdivision-button', this.subDivisionHandler.bind(this), true);
        this.createHandler('input', '#searchInput', (event) => {
            this.searchText = $(event.target).val().toString().toLowerCase().trim();
            this.applyFilters();
        }, true);
        this.createHandler('contextmenu', '.table-row.selected', this.showRowContextMenu, true);
    }

    currentStatus = 'NONE';
    currentSubDivision = '';
    currentEmployee = '';
    searchText = '';

    editMode: boolean = false;

    public override createRow(spe: SpeIn): any {
        const status = (() => {
            switch (spe.status) {
                case 'NONE':
                    return 'Новый';
                case 'WRITE_OFF':
                    return 'Списан';
                case 'VERIFICATION_REQUIRED':
                    return 'Требуется поверка';
                case 'EXPIRED':
                    return 'Срок поверки истек';
                case 'AT_INSPECTION':
                    return 'На поверке';
                case 'CORRECTED':
                    return 'Исправен'
                case 'REPAIR':
                    return 'На ремонте'
            }
        })();
        const row = `
                <div class="table-row" id="${spe.id}" data-index="${spe.id}">
                    <div class="table-cell" style="width: var(--equipment);">
                        <div class="equipment">
                            <div class="circle circle-row tooltip-trigger" data-description="Выделить строку"></div>
                            <div data-name="name" contenteditable="false" padding-left="18px">
                                ${spe.name}
                            </div>
                            <div class="equipments">
                                <div class="equipment-type">
                                    <div contenteditable="false" data-name="type">
                                        ${spe.type}
                                    </div>
                                </div>
                                <div class="equipment-number">
                                    <div contenteditable="false" data-name="outNumber">
                                        ${spe.outNumber}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="table-cell" style="width: var(--characteristics);">
                        <div class="characteristics">
                            <div contenteditable="false" data-name="accuracyClass">
                                ${spe.accuracyClass}
                            </div>
                            <div contenteditable="false" data-name="limitMeasurement">
                                ${spe.limitMeasurement}
                            </div>
                        </div>
                    </div>
                    <div class="table-cell" style="width: var(--subdivision);">
                        <div contenteditable="false" data-name="subDivision">
                            ${spe.subDivision.name}
                        </div>
                    </div>
                    <div class="table-cell" style="width: var(--responsible);">
                        <div contenteditable="false" class="responsible" data-name="employee">
                            ${spe.employee.name}
                        </div>
                    </div>
                    <div class="table-cell" style="width: var(--mark);">
                        <div contenteditable="false" data-name="mark">
                            ${spe.mark != null ? spe.mark : ''}
                        </div>
                    </div>
                    <div class="table-cell" style="width: var(--preparationDate);">
                        <div contenteditable="false" data-name="datePreparation">
                            ${this.formatDate(spe.datePreparation)}
                        </div>
                    </div>
                    <div class="table-cell" style="width: var(--verificationDate);">
                        <div contenteditable="false" data-name="dateVerification">
                            ${this.formatDate(spe.dateVerification)}
                        </div>
                    </div>
                    <div class="table-cell" style="width: var(--certificate);">
                        <div contenteditable="false" data-name="certificateNumber">
                            ${spe.certificateNumber}
                        </div>
                    </div>
                    <div class="table-cell" style="width: var(--periodicity);">
                        <div contenteditable="false" data-name="periodicity">
                            ${spe.periodicity}
                        </div> месяцев
                    </div>
                    <div class="table-cell" style="width: var(--file);">
                        <i class="document fa-solid fa-file tooltip-trigger" data-description="Открыть окно документа"></i>
                    </div>
                    <div class="table-cell" style="width: var(--status);">
                        <span class="status-indicator" style="background-color: ${this.calculateColor(spe.color)}" data-status="${spe.status}" data-document="${spe.documentId != null ? 'true' : 'false'}">
                           ${status}
                        </span>
                    </div>
                </div>`;
        return $(row);
    }

    public override onScroll() {
    }

    public override async print(): Promise<void> {
        if (!this.selectedRows || this.selectedRows.size === 0) {
            return this.createNotification('Не выбрано ни одной строки', NotificationType.WARNING);
        }
        this.reports = [
            {
                name: 'Извещения о предъявлении СИ на поверку/калибровку',
                api: '/api/report/print/spe',
                params: Array.from(this.selectedRows).map(id => `idList=${id}`).join('&')
            },
            {
                name: 'Графики поверки (калибровки) средств измерений',
                api: '/api/report/print/spe-schedule',
                params: Array.from(this.selectedRows).map(id => `idList=${id}`).join('&'),
                function: async (format: string) => {
                    const nonOrganization: string[] = []
                    const groupByOrganization = Array.from(this.selectedRows)
                        .reduce((map, id) => {
                            const item = this.localCache.get(Number(id)) as SpeIn;
                            const org = item.organization;
                            if (org == null) {
                                nonOrganization.push(item.outNumber);
                            } else {
                                map.set(org, [...(map.get(org) || []), item]);
                            }
                            return map;
                        }, new Map<string, SpeIn[]>());

                    if (nonOrganization.length > 0) {
                        this.createNotification("Оборудование без организации не попавшие в отчет: " + nonOrganization.join(', '), NotificationType.INFO);
                    }
                    for (const [organization, speList] of Array.from(groupByOrganization)) {
                        const params = `?format=${format}&${speList.map(spe => `idList=${spe.id}`).join('&')}`;
                        await this.downloadFile('/api/report/print/spe-schedule', params);
                    }
                }
            }
        ];
        return super.print();
    }

    private saveSpe() {
        if (Object.keys(this.saveMassive).length === 0) {
            return;
        }
        const itemsArray = Object.keys(this.saveMassive).map(id => {
            const cacheData = this.localCache.get(Number(id)) as SpeIn;
            return {
                id: id,
                version: cacheData.version,
                changes: this.saveMassive[id]
            };
        });
        this.save('/api/spe/update', ...itemsArray).then(() => {
            this.disableEditMode();
            itemsArray.forEach((id) => this.selectedRows.delete(Number(id)));
        });
    }

    private fullData(data: SpeIn[]): void {
        $('#total-units').text(data.length);
        $('#written-off').text(data.filter(s => s.status === 'WRITE_OFF').length);
        $('#verification-required').text(data.filter(s => s.status === 'VERIFICATION_REQUIRED').length);
        $('#verification-period-has-expired').text(data.filter(s => s.status === 'EXPIRED').length);
        $('#at-inspection').text(data.filter(s => s.status === 'AT_INSPECTION').length);
        $('#at-repair').text(data.filter(s => s.status === 'REPAIR').length);
        $('#no-document').text(data.filter(s => s.documentId === null).length);
    }

    private enableEditMode(row?: any): void {
        const dateTime: string[] = ['datePreparation', 'dateVerification'];

        const processElement = ($div: any) => {
            const text = $div.text().trim();
            const dataName: string = $div.attr('data-name');
            let element: any;

            if (dataName === 'mark') {
                element = $(`<select data-name="${dataName}"></select>`);
                const statuses = ['исправен', 'списан', 'на поверке', 'ремонт'];
                if (text === '' || text === null) {
                    element.append($(`<option selected value=""></option>`));
                }
                statuses.forEach(status => {
                    const isSelected = text !== '' && text !== null && status === text;
                    element.append($(`<option ${isSelected ? 'selected' : ''}>${status}</option>`));
                });
            } else if (dateTime.indexOf(dataName) !== -1) {
                const rowId = Number($(row).attr('id'));
                const value = this.localCache.get(rowId)[dataName];
                element = $(`<input type="date" data-name="${dataName}">`).val(value);
            } else {
                element = $div;
                element.attr('contenteditable', 'true');
            }

            if (dataName === 'employee' || dataName === 'subDivision') {
                element.addClass('area-modal').attr('contenteditable', 'false');
            }

            $div.replaceWith(element);
        };

        if (row) {
            $(row).find('div[contenteditable="false"]').each(function () {
                processElement($(this));
            });
            this.editMode = true;
            return;
        }

        for (const rowId of this.selectedRows) {
            row = $(`.table-row[id="${rowId}"]`);
            row.find('div[contenteditable="false"]').each(function () {
                processElement($(this));
            });
        }
        this.editMode = true;
        $('#edit-button').addClass('active');
    }

    private disableEditMode(row?: any): void {
        if (this.editMode &&
            Object.keys(this.saveMassive).length > 0 &&
            ((row && row.find('.change').length > 0) || $('.table-row .change').length > 0)) {
            this.createNotification("Сохраните изменения", NotificationType.WARNING);
            return;
        }
        const dateTime = ['datePreparation', 'dateVerification'];

        const processElement = ($field: any) => {
            const dataName = $field.attr("data-name");
            let value: string;
            if (dateTime.indexOf(dataName) !== -1) {
                value = this.formatDate($field.val());
            } else {
                value = $field.is('select') ? $field.find('option:selected').text() : $field.text();
            }
            $field.replaceWith(`<div data-name="${dataName}" contenteditable="false">${value}</div>`);
        };
        if (row) {
            $(row).find('div[contenteditable="true"], select[data-name], input[data-name]').each(function () {
                processElement($(this));
            });
            return;
        }

        for (const rowId of this.selectedRows) {
            const $row = $(`.table-row[id="${rowId}"]`);
            $row.find('div[contenteditable="true"], select[data-name], input[data-name]').each(function () {
                processElement($(this));
            });
        }
        this.editMode = false;
        $('#edit-button').removeClass('active');
    }

    private async selecteRows(event: Event): Promise<void> {
        if (this.editMode) {
            this.createNotification('Выключите режим редактирования', NotificationType.INFO);
            return;
        }
        const circle = $(event.currentTarget);
        const allRows = $('.table-row:visible');

        if (circle.hasClass('active')) {
            this.selectedRows.clear();
            allRows.removeClass('selected');
            allRows.each((_, row) => {
                const circle = $(row).find('.circle-row');
                circle.removeClass('active-critical');
            });
            circle.removeClass('active');
        } else {
            this.selectedRows.clear();
            allRows.each((_, row) => {
                const circle = $(row).find('.circle-row')
                const rowId = $(row).attr('id');
                this.selectedRows.add(rowId);
                $(row).addClass('selected');
                circle.addClass('active-critical')
            });
            circle.addClass('active');
        }
    }

    private async selecteRow(event: Event): Promise<void> {
        const circle = $(event.currentTarget);
        const currentRow = circle.closest('.table-row');
        const currentRowId: string = currentRow.attr('id');
        const changes = currentRow.find('.change').length;

        if (this.editMode && changes > 0) {
            this.createNotification("Сохраните изменения", NotificationType.WARNING);
            return
        }

        if (!this.selectedRows.has(currentRowId)) {
            this.selectedRows.add(currentRowId);
            currentRow.addClass('selected');
            circle.addClass('active-critical');
            if (this.editMode) {
                this.enableEditMode(currentRow);
            }
        } else {
            this.selectedRows.delete(currentRowId);
            this.disableEditMode(currentRow);
            currentRow.removeClass('selected');
            $('.circle-header').removeClass('active');
            circle.removeClass('active-critical');
        }
    }

    private inputChanges(event: Event): void {
        const $el = $(event.target);
        const id = $el.closest('.table-row').attr('id');
        const name = $el.attr('data-name');
        const value = $el.is('div') ? $el.text().trim() : $el.val();

        this.saveMassive[id] = {...this.saveMassive[id], [name]: value};
        $el.addClass('change');
    }

    private async workWithModal(event: Event): Promise<void> {
        const modalDiv = $(event.currentTarget);
        const fieldName = modalDiv.attr('data-name');
        const currentId = modalDiv.closest('.table-row').attr('id');
        let selected: any;

        if (fieldName === 'subDivision' || fieldName === 'employee') {
            const isEmployee = fieldName === 'employee';
            const dialog = $(isEmployee ? '#employeeDialog' : '#subDivisionDialog');
            const rowContainer = dialog.find('.dialog-content-rows');
            const searchInput = dialog.find('.choice-field input');
            const changeButton = $(isEmployee ? '#changeEmployee' : '#changeSubDivision');

            const data: any = await this.cache.get(fieldName);

            const renderRows = (items: any[]) => {
                rowContainer.empty();
                items.forEach(item => {
                    rowContainer.append(`
                    <div class="dialog-content-rows-row" data-id="${item.id}">
                        <div class="content-row-column col-250">${item.name}</div>
                        ${isEmployee ? `<div class="content-row-column col-250">${item.subDivision?.name || ''}</div>` : ''}
                    </div>`
                    );
                });
            };

            renderRows(data);

            searchInput.off('input').on('input', function () {
                const searchText = $(this).val().toString().toLowerCase().trim();
                const filtered = data.filter((e: any) => e.name.toLowerCase().includes(searchText));
                renderRows(filtered);
            });

            (dialog[0] as any).showModal();

            rowContainer.off('click').on('click', '.dialog-content-rows-row', function () {
                const id = $(this).data('id');
                selected = data.find((e: any) => e.id === id);
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

                if (currentId) {
                    this.saveMassive[currentId] = {
                        ...this.saveMassive[currentId],
                        [fieldName]: selected
                    };
                } else {
                    this.saveMassive[fieldName] = selected;
                }

                modalDiv.addClass('change-textarea');
                (dialog[0] as any).close();
            });
        }

        modalDiv.addClass('change');
    }

    private async openDocument(event: Event): Promise<void> {
        const dialog = $('#documentDialog');
        const currentRow = $(event.currentTarget).closest('.table-row');
        const currentSpeId = currentRow.attr('id');
        const spe = this.localCache.get(Number(currentSpeId)) as SpeIn;
        const rowContainer = dialog.find('.dialog-content-rows');

        rowContainer.empty();

        if (spe.documentId !== null) {
            const document: any = await this.requestToApi(`/api/document/get-document/${spe.documentId}`, "GET");
            this.localCache.set('document', document);

            document.files.forEach((file: any) => {
                rowContainer.append(`
                <div class="dialog-content-rows-row" id="${file.id}">
                    <div class="content-row-column col-450">${file.baseFileName}</div>
                    <div class="content-row-column col-100">${file.type}</div>
                    <div class="content-row-column col-100"><i style="float: right" class="download fas fa-download"></i></div>
                </div>`
                );
            });
        }

        rowContainer.append(`
            <div class="dialog-content-rows-row">
                <div class="content-row-column col-450"></div>
                <div class="content-row-column col-100"></div>
                <div class="content-row-column col-100">
                    <i style="float: right" class="uploadIcon upload-file fas fa-file-upload" onclick="$('#fileInput').click()"></i>
                    <input type="file" id="fileInput" style="display: none;"/>
                </div>
            </div>`
        );

        const organizationSelect = $(`
            <select class="organization-select form-control">
                <option value="">Выберите организацию</option>
                <option value="organization1">Борисоглебский филиал ФБУ "Воронежский ЦСМ"</option>
                <option value="organization2">ФБУ "Воронежский ЦСМ"</option>
                <option value="organization3">ООО "СТАНДАРТ"</option>
            </select>
        `);

        if (spe.organization) {
            organizationSelect.find('option[value=""]').remove();
            organizationSelect.val(spe.organization);
        }

        dialog.find('.organization-row').empty().append(organizationSelect);

        $(document).off('change', '.organization-select').on('change', '.organization-select', (event: Event) => {
            const value = $(event.currentTarget).val();
            this.saveMassive[currentSpeId] = {...this.saveMassive[currentSpeId], organization: value};
            this.saveSpe();
        });

        $(document).off('change', '#fileInput').on('change', '#fileInput', (e) => this.addFileToDocument(e, currentSpeId));
        $(document).on('contextmenu', '.dialog-content-rows-row', (event: Event) => {
            const $row = $(event.currentTarget);
            const fileId = $row.attr('id');
            if (!fileId) {
                return;
            }
            event.preventDefault();
            const mouseEvent = event as MouseEvent;
            this.createContextMenu([
                {
                    label: 'Удалить файл',
                    action: () => {
                        this.deleteEntity(`/api/document/delete-file-from-document/${fileId}`).then(
                            () => {
                                this.createNotification('Файл успешно удален', NotificationType.SUCCESS);
                                this.deleteRow(fileId)
                            });
                    }
                }
            ], mouseEvent.clientX, mouseEvent.clientY);
        });

        this.dialog.open('documentDialog');
    }

    private addFileToDocument(event: Event, speId: string): void {
        const formData = new FormData();
        const currentInput = event.currentTarget as HTMLInputElement;
        const spe = this.localCache.get(Number(speId)) as SpeIn;

        if (currentInput.files) {
            Array.from(currentInput.files).forEach(file => {
                formData.append('files', file);
            });
        }

        const url = spe.documentId
            ? `/api/document/add-file-to-document-and-get/${spe.documentId}`
            : `/api/spe/create-document/${spe.id}`;

        const requestType = spe.documentId ? 'PATCH' : 'POST';

        this.requestToApi(url, requestType, formData).then((document: DocumentBormash) => {
            const dialog = $('#documentDialog');
            const rowContainer = dialog.find('.dialog-content-rows');
            rowContainer.empty();
            for (const file of document.files) {
                rowContainer.append(`
                    <div class="dialog-content-rows-row" id="${file.id}">
                        <div class="content-row-column col-450">${file.baseFileName}</div>
                        <div class="content-row-column col-100">${file.type}</div>
                        <div class="content-row-column col-100"><i style="float: right" class="download fas fa-download"></i></div>
                    </div>`
                );
            }
            rowContainer.append(`
            <div class="dialog-content-rows-row">
                <div class="content-row-column col-450"></div>
                <div class="content-row-column col-100"></div>
                <div class="content-row-column col-100">
                    <i style="float: right" class="uploadIcon upload-file fas fa-file-upload" onclick="$('#fileInput').click()"></i>
                    <input type="file" id="fileInput" style="display: none;"/>
                </div>
            </div>`
            );
            spe.documentId = document.id;
            this.localCache.set(spe.id, spe);
            this.createNotification("Файлы добавлены", NotificationType.SUCCESS);
        }).catch(console.error);

        currentInput.value = '';
    }

    private handleDownloadFile(event: Event): void {
        const fileId = $(event.target).closest('.dialog-content-rows-row').attr('id');
        this.downloadFile(`/api/document/download-document-file/${fileId}`).catch(console.error);
    }

    private createFgisSpe = async (event: Event) => {
        event.preventDefault();

        const button = $(event.target);
        const form = button.closest('form').get(0);
        const dialog = $('#create-fgis-dialog');

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        button.prop('disabled', true);

        const formData = {
            outNumber: dialog.find('textarea[name="outNumber"]').val(),
            accuracyClass: dialog.find('textarea[name="accuracyClass"]').val(),
            limitMeasurement: dialog.find('textarea[name="limitMeasurement"]').val(),
            subDivision: this.saveMassive['subDivision'],
            employee: this.saveMassive['employee'],
        };

        try {
            const newSPE: SpeIn = await this.createEntity('/api/spe/create-spe-fgis', formData);
            this.saveMassive = {};
            this.localCache.set(newSPE.id, newSPE);
            this.dialog.close('create-fgis-dialog');
            const newRow = this.createRow(newSPE);
            $(`.table-body`).append(newRow);
            button.prop('disabled', false);
        } catch (error) {
            this.saveMassive = {};
            // form.reset();
            if (error.status === 404) {
                this.createNotification('СИ не найдено в реестре ФГИС', NotificationType.WARNING);
                this.dialog.close('create-fgis-dialog');
                setTimeout(() => this.dialog.open('create-dialog'), 850);
            } else {
                this.createNotification('Ошибка при создании SPE', NotificationType.ERROR);
            }

            button.prop('disabled', false);
        }

    }

    private createSpe = async (event: Event) => {
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
            name: dialog.find('input[name="name"]').val(),
            type: dialog.find('textarea[name="type"]').val(),
            outNumber: dialog.find('textarea[name="outNumber"]').val(),
            accuracyClass: dialog.find('textarea[name="accuracyClass"]').val(),
            limitMeasurement: dialog.find('textarea[name="limitMeasurement"]').val(),
            subDivision: this.saveMassive['subDivision'],
            employee: this.saveMassive['employee'],
            periodicity: dialog.find('textarea[name="periodicity"]').val(),
            datePreparation: dialog.find('input[name="datePreparation"]').val(),
            dateVerification: dialog.find('input[name="dateVerification"]').val(),
            certificateNumber: dialog.find('textarea[name="certificateNumber"]').val(),
            organization: dialog.find('select[name="organization"]').val()
        };

        try {
            const newSPE: SpeIn = await this.createEntity('/api/spe/create-spe', formData);
            this.saveMassive = {};
            this.localCache.set(newSPE.id, newSPE);
            this.dialog.close("create-dialog");
            const newRow = this.createRow(newSPE);
            $(`.table-body`).append(newRow);
            button.prop('disabled', false);
        } catch (error) {
            this.saveMassive = {};
            form.reset();
            this.createNotification('Ошибка при создании SPE', NotificationType.ERROR);
            button.prop('disabled', false);
        }
    }

    private applyFilters = () => {
        if (this.editMode) {
            this.createNotification('Выключите режим редактирования', NotificationType.INFO);
            return;
        }
        $('.table-row').each((_, element) => {
            const row = $(element);

            row.removeClass('selected');
            row.find('.circle-row').removeClass('active-critical');
            const rowId = row.attr('id');
            this.selectedRows.delete(rowId);

            const statusMatch: boolean =
                this.currentStatus === 'NONE' ||
                (this.currentStatus === 'NO_DOCUMENT' ? row.find('[data-document="false"]').length > 0 :
                    row.find('[data-status]').attr('data-status') === this.currentStatus);
            const subDivisionMatch: boolean = !this.currentSubDivision || row.find('[data-name="subDivision"]').text().trim() === this.currentSubDivision;
            const employeeMatch: boolean = !this.currentEmployee || row.find('[data-name="employee"]').text().trim() === this.currentEmployee;
            const textMatch: boolean = this.searchText === '' || row.text().toLowerCase().includes(this.searchText.toLowerCase());
            row.toggle(statusMatch && subDivisionMatch && employeeMatch && textMatch);
        });
        $('.circle-header').removeClass('active');
    }

    private filterButtonHandler = (event: Event) => {
        this.currentStatus = $(event.target).data('status');
        $('.filter-btn').removeClass('active');
        $(event.target).addClass('active');
        this.applyFilters();
    }

    private async employeeHandler(event: Event) {
        const button = $(event.target);
        const dialog = $('#employeeDialog');
        const rowContainer = dialog.find('.dialog-content-rows');
        let selectedName = '';
        const cancelBtn = dialog.find('.close');
        const dialogName = dialog.find('.dialog-name');

        try {
            const employees: Employee[] = await this.cache.get('employee');

            function render(list: Employee[]) {
                rowContainer.empty();
                list.forEach(e => rowContainer.append(`
                    <div class="dialog-content-rows-row">
                        <div class="content-row-column col-250 filter">${e.name}</div>
                        <div class="content-row-column col-250">${e.subDivision.name}</div>
                    </div>`));
            }

            cancelBtn.text('Сбросить фильтры');
            dialogName.text('Фильтр по подразделению');
            render(employees);

            dialog.find('.choice-field input').on('input', function () {
                const search = $(this).val().toString().toLowerCase();
                const filtered = employees.filter((e: any) => e.name.toLowerCase().includes(search));
                render(filtered);
            });

            rowContainer.on('click', '.dialog-content-rows-row', function () {
                selectedName = $(this).find('.content-row-column.filter').text().trim();
            });

            $('#changeEmployee').on('click', () => {
                this.currentEmployee = selectedName;
                this.applyFilters();
                button.css('border-color', 'red');
                (dialog[0] as any).close();
            });

            $('.close').on('click', () => {
                this.currentEmployee = '';
                button.css('border-color', '#e2e8f0');
                this.applyFilters();
            });

            dialog.on('close', function () {
                cancelBtn.text('Отмена');
                dialogName.text('Окно выбора сотрудника');
            });

            (dialog[0] as any).showModal();
        } catch (error) {
            this.createNotification('Ошибка при загрузке сотрудников', NotificationType.ERROR);
        }
    }

    private async subDivisionHandler(event: Event) {
        const button = $(event.target);
        const dialog = $('#subDivisionDialog');
        const rowContainer = dialog.find('.dialog-content-rows');
        let selectedName = '';
        const cancelBtn = dialog.find('.close');
        const dialogName = dialog.find('.dialog-name');

        try {
            const subDivisions: SubDivision[] = await this.cache.get('subDivision');

            function render(list: SubDivision[]) {
                rowContainer.empty();
                list.forEach(e => rowContainer.append(`<div class="dialog-content-rows-row"><div class="content-row-column filter">${e.name}</div></div>`));
            }

            cancelBtn.text('Сбросить фильтры');
            dialogName.text('Фильтр по подразделению');
            render(subDivisions);

            dialog.find('.choice-field input').on('input', function () {
                const search = $(this).val().toString().toLowerCase();
                const filtered = subDivisions.filter((e: any) => e.name.toLowerCase().includes(search));
                render(filtered);
            });

            rowContainer.on('click', '.dialog-content-rows-row', function () {
                selectedName = $(this).find('.content-row-column.filter').text().trim();
            });

            $('#changeSubDivision').on('click', () => {
                this.currentSubDivision = selectedName;
                this.applyFilters();
                button.css('border-color', 'red');
                (dialog[0] as any).close();
            });

            $('.close').on('click', () => {
                this.currentSubDivision = '';
                button.css('border-color', '#e2e8f0');
                this.applyFilters();
            });

            dialog.on('close', function () {
                cancelBtn.text('Отмена');
                dialogName.text('Окно выбора подразделения');
            });

            (dialog[0] as any).showModal();
        } catch (error) {
            this.createNotification('Ошибка при загрузке подразделений', NotificationType.ERROR);
        }
    }

    private showRowContextMenu = (event: Event) => {
        if ($(event.target).is('div[contenteditable="true"]') || $(event.target).closest('div[contenteditable="true"]').length > 0) {
            return;
        }
        event.preventDefault();

        const mouseEvent = event as MouseEvent;
        const $row = $(event.currentTarget);
        const rowName = $row.find('[data-name="outNumber"]').text().trim();
        const rowId = $row.attr('id');

        this.createContextMenu([
            {
                label: 'Удалить',
                action: () => {
                    this.createConfirmationDialog("Подтвердите удаление оборудования: {outNumber}", {outNumber: rowName}).then((confirmed) => {
                        // @ts-ignore
                        if (confirmed) {
                            this.deleteEntity(`/api/spe/delete/${rowId}`).then(() => {
                                this.deleteRow(rowId);
                                this.createNotification("Оборудование успешно удалено", NotificationType.SUCCESS);
                            }).catch(() => {
                                this.createNotification("Возникла ошибка при удалении оборудования", NotificationType.ERROR);
                            });
                        }
                    });
                }
            }
        ], mouseEvent.clientX, mouseEvent.clientY);
    }

}

$(document).ready(() => {
    new Spe();
});
