// @ts-ignore
declare const $: any;

class NtDocuments extends Base {

    constructor(itemsPerPage = Infinity, visibleRow = Infinity) {
        super($(`.table-body`), itemsPerPage, visibleRow, () => {
            this.displayPage('/api/ntd/get-page-ntd', undefined).catch(console.error);
        });
        this.createHandler('click', '.circle-row', this.selecteRow.bind(this), true);
        this.createHandler('click', '#edit-button', () => {
            if (!this.editMode) {
                this.enableEditMode()
            } else this.disableEditMode();
        }, true);
        this.createHandler('click', '#create-button', () => this.dialog.open('create-dialog'), true);
        this.createHandler('click', '#save-button', () => this.saveNtd(), true);
        this.createHandler('input', '[data-name]', this.inputChanges.bind(this), true);
        this.createHandler('click', '.document', this.openDocument.bind(this), true);
        this.createHandler('click', '.references', this.openReferences.bind(this), true);
        this.createHandler('click', '.download', this.handleDownloadFile.bind(this), true);
        this.createHandler('click', '#createBtn', this.createNtd, true);
        this.createHandler('contextmenu', '.table-row.selected', this.showRowContextMenu, true);
        this.createHandler('input', '#searchInput', (event) => {
            this.searchText = $(event.target).val().toString().toLowerCase().trim();
            this.applyFilters();
        }, true);
    }

    searchText = '';
    editMode: boolean = false;

    public createRow(ntd: NtdIn) {
        const row = `
            <div class="table-row" id="${ntd.id}" data-index="${ntd.id}">
                <div class="table-cell" style="width: var(--name); position: relative">
                    <div class="circle circle-row tooltip-trigger" data-description="Выделить строку"></div>
                    <div class="field-container" data-name="name" contenteditable="false">
                        ${ntd.name}
                    </div>
                </div>
                <div class="table-cell" style="width: var(--type);">
                    <div class="field-container center" data-name="type" contenteditable="false">
                        ${ntd.type}
                    </div>
                </div>
                <div class="table-cell" style="width: var(--dateVerification);">
                    <div class="field-container center" data-name="dateVerification" contenteditable="false">
                        ${this.formatDate(ntd.dateVerification)}
                    </div>
                </div>
                <div class="table-cell" style="width: var(--file); padding: 0">
                    <div data-name="document" contenteditable="false" style="height: 100%; width: 100%">
                        <div class="frame" style="background-color: ${this.calculateColor(ntd.color)}">
                            <i class="document fa-solid fa-file tooltip-trigger" data-description="Открыть окно документа"></i>
                        </div>
                    </div>
                </div>
                <div class="table-cell" style="width: var(--references); padding: 0">
                    <div data-name="references" contenteditable="false">
                        <i class="references fa-solid fa-book tooltip-trigger" data-description="Открыть окно связанных документов"></i>
                    </div>
                </div>
                <div class="table-cell" style="width: var(--comment);">
                    <div class="field-container" data-name="comment" contenteditable="false">
                        ${ntd.comment}
                    </div>
                </div>
            </div>`;

        return $(row);
    }

    public onScroll(): void {
    }

    private saveNtd() {
        if (Object.keys(this.saveMassive).length === 0) {
            return;
        }
        const itemsArray = Object.keys(this.saveMassive).map(id => {
            const cacheData = this.localCache.get(id) as NtdIn;
            return {
                id: id,
                version: cacheData.version,
                changes: this.saveMassive[id]
            };
        });
        this.save('/api/ntd/update', ...itemsArray).then(() => {
            this.disableEditMode();
            itemsArray.forEach((id) => this.selectedRows.delete(id.toString()));
        });
    }

    private createNtd = async (event: Event) => {
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
            dateVerification: dialog.find('input[name="dateVerification"]').val(),
            comment: dialog.find('textarea[name="comment"]').val()
        };

        try {
            const newNtd: NtdIn = await this.createEntity('/api/ntd/create-ntd', formData);
            this.saveMassive = {};
            this.localCache.set(newNtd.id, newNtd);
            this.dialog.close("create-dialog");
            const newRow = this.createRow(newNtd);
            $(`.table-body`).append(newRow);
            button.prop('disabled', false);
        } catch (error) {
            this.saveMassive = {};
            form.reset();
            this.createNotification('Ошибка при создании NTD', NotificationType.ERROR);
            button.prop('disabled', false);
        }
    }

    private enableEditMode(row?: any): void {
        const dateTime: string[] = ['dateVerification'];

        const processElement = ($div: any) => {
            const text = $div.text().trim();
            const dataName: string = $div.attr('data-name');
            let element: any;

            if (dateTime.indexOf(dataName) !== -1) {
                const rowId = $(row).attr('id');
                const value = this.localCache.get(rowId)[dataName];
                element = $(`<input type="date" data-name="${dataName}">`).val(value);
            } else {
                element = $div;
                element.attr('contenteditable', 'true');
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
        const dateTime = ['dateVerification'];
        const dTouch = ['document', 'references'];
        const center = ['dateVerification', 'type']

        const processElement = ($field: any) => {
            const dataName = $field.attr("data-name");
            let value: string;
            if (dTouch.indexOf(dataName) !== -1) {
                return;
            }
            if (dateTime.indexOf(dataName) !== -1) {
                value = this.formatDate($field.val());
            } else {
                value = $field.is('select') ? $field.find('option:selected').text() : $field.text();
            }
            $field.replaceWith(`<div class="field-container ${center.indexOf(dataName) !== -1 ? 'center' : ''}" data-name="${dataName}" contenteditable="false">${value}</div>`);
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

    private async openDocument(event: Event): Promise<void> {
        const dialog = $('#documentDialog');
        const currentRow = $(event.currentTarget).closest('.table-row');
        const currentNtdId = currentRow.attr('id');
        const ntd = this.localCache.get(currentNtdId) as NtdIn;
        const rowContainer = dialog.find('.dialog-content-rows');
        let document;

        rowContainer.empty();
        if (ntd.documentId !== null) {
            document = await this.requestToApi(`/api/document/get-document/${ntd.documentId}`, "GET");
            this.localCache.set('document', document);
            document.files.forEach((file: DocumentFile) => {
                this.createRowOnDocument(file);
            });
        }

        rowContainer.append(`
            <div class="dialog-content-rows-row" id="newFileRow" style="height: 50px">
                <div class="content-row-column col-450" style="border: none;"></div>
                <div class="content-row-column col-100"></div>
                <div class="content-row-column col-100 center" style="padding: 0;border-bottom: 1px solid var(--border-color);">
                    <i style="float: right" class="uploadIcon upload-file fas fa-file-upload tooltip-trigger" data-description="Добавить документацию" onclick="$('#fileInput').click()"></i>
                    <input type="file" id="fileInput" style="display: none;"/>
                </div>
            </div>`
        );

        dialog.off('change', '#fileInput').on('change', '#fileInput', (e) => {
            if (e.target.files && e.target.files.length > 0) {
                Array.from(e.target.files).forEach((file: File) => {
                    const fileName = file.name;
                    if ($('.dialog-content-rows').find(`.col-450:contains("${fileName}")`).length > 0) {
                        this.createNotification(`Файл "${fileName}" уже существует`, NotificationType.WARNING);
                        return;
                    } else {
                        this.addFileToDocument(e, currentNtdId);
                    }
                });
            }
        });

        dialog.off('contextmenu', '.dialog-content-rows-row').on('contextmenu', '.dialog-content-rows-row', (event: Event) => {
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

        dialog.off('change', '#reloadFileInput').on('change', '#reloadFileInput', (event: Event) => {
            const currentInput = event.currentTarget as HTMLInputElement;

            if (!currentInput.files || currentInput.files.length === 0) {
                return;
            }

            let multipartFile = currentInput.files[0];
            const fileId = $(currentInput).closest('.dialog-content-rows-row').attr('id');

            const unlock = this.lockScreen();

            this.deleteEntity(`/api/document/delete-file-from-document/${fileId}`).then(() => {
                const ntd = this.localCache.get(currentNtdId) as NtdIn;
                const formData = new FormData();
                formData.append('file', multipartFile);
                return this.requestToApi(`/api/document/add-file-2-document/${ntd.documentId}`, "PATCH", formData);
            }).then((file: DocumentFile) => {
                this.createRowOnDocument(file, fileId);
                return this.requestToApi(`/api/ntd/calculate-references?id=${currentNtdId}`, 'POST');
            }).then(() => {
                this.createNotification('Файл успешно перезагружен', NotificationType.SUCCESS);
                const ntd = this.localCache.get(currentNtdId) as NtdIn;
                ntd.references.forEach((refId: string) => {
                    const ref = this.localCache.get(refId) as NtdRefIn;
                    ref.color = Color.RED;
                    this.updateRow(ref, refId);
                });
            }).catch(error => {
                    this.createNotification('Произошла ошибка при перезагрузки документации', NotificationType.ERROR);
                }
                // @ts-ignore
            ).finally(() => unlock());
        });

        this.dialog.open('documentDialog');
    }

    private createRowOnDocument = (file: DocumentFile, index?: string) => {
        const rowContainer = $('#documentDialog').find('.dialog-content-rows');
        const rowHtml = `
                <div class="dialog-content-rows-row" id="${file.id}">
                    <div class="content-row-column col-450">${file.baseFileName}</div>
                    <div class="content-row-column col-100 center">
                        ${file.type}</div>
                    <div class="content-row-column col-100 file-items">
                        <i class="fas fa-arrows-rotate reload-icon tooltip-trigger" data-description="Обновить документацию" data-file-id="${file.id}" onclick="$('#reloadFileInput').click()"></i>
                        <input type="file" id="reloadFileInput" class="reload-file-input" style="display: none;"/>
                        <i style="float: right" class="download fas fa-download tooltip-trigger" data-description="Скачать документацию" data-file-id="${file.id}"></i>
                    </div>
                </div>`;
        if (index) {
            rowContainer.find(`#${index}`).replaceWith(rowHtml);
        } else {
            rowContainer.append(rowHtml);
        }
    };

    private addFileToDocument(event: Event, ntdId: string): void {
        const formData = new FormData();
        const currentInput = event.currentTarget as HTMLInputElement;
        const ntd = this.localCache.get(ntdId) as NtdIn;

        if (currentInput.files) {
            Array.from(currentInput.files).forEach(file => {
                formData.append('files', file);
            });
        }

        const url = ntd.documentId
            ? `/api/document/add-file-to-document/${ntd.documentId}`
            : `/api/ntd/create-document/${ntd.id}`;

        const requestType = ntd.documentId ? 'PATCH' : 'POST';
        const unlock = this.lockScreen();

        this.requestToApi(url, requestType, formData).then((files) => {
            const dialog = $('#documentDialog');
            const rowContainer = dialog.find('.dialog-content-rows');
            rowContainer.find('#newFileRow').remove();
            if (!ntd.documentId) {
                files = files.files;
            }
            for (const file of files) {
                this.createRowOnDocument(file);
            }
            rowContainer.append(`
                <div class="dialog-content-rows-row" id="newFileRow" style="height: 50px">
                    <div class="content-row-column col-450" style="border: none;"></div>
                    <div class="content-row-column col-100"></div>
                    <div class="content-row-column col-100 center" style="padding: 0;border-bottom: 1px solid var(--border-color);">
                        <i style="float: right" class="uploadIcon upload-file fas fa-file-upload tooltip-trigger" data-description="Добавить документацию" onclick="$('#fileInput').click()"></i>
                        <input type="file" id="fileInput" style="display: none;"/>
                    </div>
                </div>`
            );
            this.createNotification("Файлы добавлены", NotificationType.SUCCESS);
        }).catch(console.error)
            // @ts-ignore
            .finally(() => unlock());

        currentInput.value = '';
    }

    private async openReferences(event: Event): Promise<void> {
        const dialog = $('#referencesDialog');
        const currentRow = $(event.currentTarget).closest('.table-row');
        const currentNtdId = currentRow.attr('id');
        const ntd = this.localCache.get(currentNtdId) as NtdIn;
        const rowContainer = dialog.find('.dialog-content-rows');

        if (!dialog.find('.search-container').length) {
            dialog.find('.dialog-container-header').append(`
            <div class="search-container" style="margin: 10px 0;">
                <input type="text" id="search-input" class="search-input" placeholder="Поиск по наименованию..." 
                       style="width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
            </div>
        `);
        }

        rowContainer.empty();

        const renderReference = (references: NtdRefIn[], checked: boolean) => {
            references.forEach((ref: NtdRefIn) => {
                rowContainer.append(`
                    <div class="dialog-content-rows-row" id="${ref.id}" data-name="${ref.name.toLowerCase()}">
                        <div class="content-row-column" style="width: 500px">${ref.name}</div>
                        <div class="content-row-column center" style="width:150px;">${ref.type}</div>
                        <div class="content-row-column center" style="width:200px;">${this.formatDate(ref.dateVerification)}</div>
                        <div class="content-row-column" style="width: 100px; text-align: center">
                            <div class="frame" style="background-color: ${this.calculateColor(ref.color)}">
                                <i class="download-inn fa-solid fa-file tooltip-trigger" data-description="Скачать документ" id="${ref.documentId}"></i>
                            </div>
                        </div>
                        <div class="content-row-column" style="width: 500px">${ref.comment}</div>
                        <div class="content-row-column center" style="width: 100px">
                            <label class="container-checkbox tooltip-trigger" data-description="${checked ? 'Отвязать документацию' : 'Связать документацию'}">
                                <input class="checkbox" ${checked ? 'checked="checked"' : ''} type="checkbox">
                                <div class="checkmark"></div>
                            </label>
                        </div>
                    </div>`);
            });
        }

        if (ntd.references.length > 0) {
            const references: NtdRefIn[] = await this.requestToApi(`/api/ntd/get-references?ids=${ntd.references}`, "GET");
            this.localCache.set('references', references);
            renderReference(references, true);
        }
        const references: NtdRefIn[] = await this.requestToApi(`/api/ntd/get-all-references?id=${ntd.id}&ids=${ntd.references}`, "GET");
        renderReference(references, false);

        dialog.off('input', '.search-input').on('input', '.search-input', function () {
            const searchText = $(this).val().toString().toLowerCase();
            $('.dialog-content-rows-row').each(function () {
                const rowName = $(this).find('.content-row-column').first().text().toLowerCase();
                $(this).toggle(rowName.includes(searchText));
            });
        });

        dialog.off('click', '.container-checkbox').on('click', '.container-checkbox', async (event: Event) => {
            event.preventDefault();

            const $container = $(event.currentTarget);
            const $checkbox = $container.find('input[type="checkbox"]');
            const referenceId = $container.closest('.dialog-content-rows-row').attr('id');

            const newState = !$checkbox.prop('checked');

            if (newState) {
                await this.requestToApi(`/api/ntd/add-reference?id=${ntd.id}&referenceId=${referenceId}`, "PATCH");
                ntd.references.push(referenceId);
            } else {
                await this.requestToApi(`/api/ntd/remove-reference?id=${ntd.id}&referenceId=${referenceId}`, "PATCH");
                ntd.references = ntd.references.filter(ref => ref !== referenceId);
            }
            $checkbox.prop('checked', newState);
            $container.toggleClass('checked', newState);
        });

        dialog.off('click', '.download-inn').on('click', '.download-inn', (event: Event) => {
            const $icon = $(event.currentTarget);
            const documentId = $icon.attr('id');
            if (documentId) {
                this.downloadFile(`/api/document/download-all-document-file/${documentId}`).catch(console.error);
            } else {
                this.createNotification("Файл не прикреплен", NotificationType.INFO);
            }
        });

        this.dialog.open('referencesDialog', {
            onClose: () => {
                $('#referencesDialog .search-input').val('');
            }
        });
    }

    private handleDownloadFile(event: Event): void {
        const fileId = $(event.target).closest('.dialog-content-rows-row').attr('id');
        this.downloadFile(`/api/document/download-document-file/${fileId}`).catch(console.error);
    }

    private showRowContextMenu = (event: Event) => {
        if ($(event.target).is('div[contenteditable="true"]') || $(event.target).closest('div[contenteditable="true"]').length > 0) {
            return;
        }
        event.preventDefault();

        const mouseEvent = event as MouseEvent;
        const $row = $(event.currentTarget);
        const rowName = $row.find('[data-name="name"]').text().trim();
        const rowId = $row.attr('id');

        this.createContextMenu([
            {
                label: 'Удалить',
                action: () => {
                    this.createConfirmationDialog("Подтвердите удаление документации: {name}", {name: rowName}).then((confirmed) => {
                        // @ts-ignore
                        if (confirmed) {
                            this.deleteEntity(`/api/ntd/delete/${rowId}`).then(() => {
                                this.deleteRow(rowId);
                                this.createNotification("Документация успешно удалена", NotificationType.SUCCESS);
                            }).catch(() => {
                                this.createNotification("Возникла ошибка при удалении документации", NotificationType.ERROR);
                            });
                        }
                    });
                }
            }
        ], mouseEvent.clientX, mouseEvent.clientY);
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

            const textMatch: boolean = this.searchText === '' || row.text().toLowerCase().includes(this.searchText.toLowerCase());
            row.toggle(textMatch);
        });
    }
}

$(document).ready(() => {
    new NtDocuments();
});