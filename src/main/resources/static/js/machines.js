var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
class MachineManager {
    constructor() {
        this.apiUrl = '/api/v1/machines';
        this.employeeApiUrl = '/api/employees?subdivision=2';
        this.choicesInstances = {};
        this.imageModalInstance = null;
        this.init();
    }
    init() {
        if (document.getElementById('machines-table-body')) {
            this.initListPage();
        }
        else if (document.getElementById('machine-form')) {
            this.initFormPage();
        }
        else if (document.getElementById('machine-content')) {
            this.initDetailsPage();
        }
    }
    fetchData(url_1) {
        return __awaiter(this, arguments, void 0, function* (url, options = {}) {
            const response = yield fetch(url, options);
            if (!response.ok) {
                const errorText = yield response.text();
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
            }
            if (response.status === 204) {
                return null;
            }
            const text = yield response.text();
            return text ? JSON.parse(text) : null;
        });
    }
    showLoading(show) {
        const indicator = document.getElementById('loading-indicator');
        if (indicator) {
            indicator.style.display = show ? 'block' : 'none';
        }
    }
    initListPage() {
        return __awaiter(this, void 0, void 0, function* () {
            this.showLoading(true);
            try {
                const machines = yield this.fetchData(this.apiUrl);
                const tableBody = document.getElementById('machines-table-body');
                const cardView = document.getElementById('machines-card-view');
                tableBody.innerHTML = '';
                cardView.innerHTML = '';
                machines.forEach(machine => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                    <td>${machine.name}</td>
                    <td>${machine.number}</td>
                    <td class="description-cell">${machine.description || '-'}</td>
                    <td>
                        <div class="d-flex justify-content-end gap-2">
                            <a href="/machines/${machine.number}" class="btn btn-sm btn-info icon-text" title="Просмотр">
                                <i class="bi bi-eye"></i>
                            </a>
                            <a href="/machines/${machine.number}/edit" class="btn btn-sm btn-primary icon-text" title="Редактировать">
                                <i class="bi bi-pencil"></i>
                            </a>
                        </div>
                    </td>
                `;
                    tableBody.appendChild(row);
                    const card = document.createElement('div');
                    card.className = 'card mb-3';
                    card.innerHTML = `
                        <div>
                            <h5 class="card-title mt-2 ms-2">${machine.name}</h5>
                            <h6 class="card-subtitle mb-2 ms-2 text-muted">Серийный номер: ${machine.number}</h6>
                            <p class="card-text ms-2">Описание: ${machine.description || 'Описание отсутствует.'}</p>
                            <div class="d-flex justify-content-end gap-2 mt-3 mb-2 me-2">
                                <a href="/machines/${machine.number}" class="btn btn-sm btn-info icon-text">
                                    <i class="bi bi-eye"></i>
                                    <span>Просмотр</span>
                                </a>
                                <a href="/machines/${machine.number}/edit" class="btn btn-sm btn-primary icon-text">
                                    <i class="bi bi-pencil"></i>
                                    <span>Редактировать</span>
                                </a>
                            </div>
                        </div>
                `;
                    cardView.appendChild(card);
                });
            }
            catch (error) {
                console.error("Failed to load machines:", error);
                alert('Не удалось загрузить список станков.');
            }
            finally {
                this.showLoading(false);
            }
        });
    }
    initFormPage() {
        return __awaiter(this, void 0, void 0, function* () {
            const form = document.getElementById('machine-form');
            const formTitle = document.getElementById('form-title');
            const numberInput = document.getElementById('number');
            const pathParts = window.location.pathname.split('/');
            const machineNumber = pathParts[pathParts.length - 2];
            const isEditMode = pathParts[pathParts.length - 1] === 'edit' && machineNumber;
            yield this.populateEmployeeSelects();
            if (isEditMode) {
                formTitle.textContent = 'Редактирование станка';
                this.showLoading(true);
                try {
                    const machine = yield this.fetchData(`${this.apiUrl}/${machineNumber}`);
                    document.getElementById('name').value = machine.name;
                    numberInput.value = machine.number.toString();
                    numberInput.readOnly = true;
                    document.getElementById('description').value = machine.description || '';
                    this.selectOptions('responsibleEmployees', machine.responsibleEmployeesList || []);
                    this.selectOptions('admittedEmployees', machine.admittedEmployeesList || []);
                }
                catch (error) {
                    console.error('Failed to load machine data for editing:', error);
                    alert('Не удалось загрузить данные станка.');
                }
                finally {
                    this.showLoading(false);
                }
            }
            form.addEventListener('submit', (e) => __awaiter(this, void 0, void 0, function* () {
                var _a, _b, _c, _d;
                e.preventDefault();
                const url = isEditMode ? `${this.apiUrl}/${machineNumber}` : this.apiUrl;
                try {
                    if (isEditMode) {
                        const responsibleEmployees = ((_a = this.choicesInstances['responsibleEmployees']) === null || _a === void 0 ? void 0 : _a.getValue(true)) || [];
                        const admittedEmployees = ((_b = this.choicesInstances['admittedEmployees']) === null || _b === void 0 ? void 0 : _b.getValue(true)) || [];
                        const machineData = {
                            name: form.elements.namedItem('name').value,
                            number: Number(form.elements.namedItem('number').value),
                            description: form.elements.namedItem('description').value,
                            responsibleEmployeesList: responsibleEmployees.map((name) => ({ name })),
                            admittedEmployeesList: admittedEmployees.map((name) => ({ name })),
                        };
                        yield this.fetchData(url, {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify(machineData)
                        });
                    }
                    else {
                        const formData = new FormData(form);
                        const responsibleEmployees = ((_c = this.choicesInstances['responsibleEmployees']) === null || _c === void 0 ? void 0 : _c.getValue(true)) || [];
                        const admittedEmployees = ((_d = this.choicesInstances['admittedEmployees']) === null || _d === void 0 ? void 0 : _d.getValue(true)) || [];
                        formData.delete('responsibleEmployeesList');
                        formData.delete('admittedEmployeesList');
                        responsibleEmployees.forEach((name) => formData.append('responsibleEmployeesList', name));
                        admittedEmployees.forEach((name) => formData.append('admittedEmployeesList', name));
                        yield this.fetchData(url, {
                            method: 'POST',
                            body: formData
                        });
                    }
                    alert(`Станок успешно ${isEditMode ? 'обновлен' : 'создан'}!`);
                    window.location.href = '/machines';
                }
                catch (error) {
                    console.error('Failed to save machine:', error);
                    alert('Ошибка при сохранении станка.');
                }
            }));
        });
    }
    populateEmployeeSelects() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const employees = yield this.fetchData(this.employeeApiUrl);
                const responsibleSelect = document.getElementById('responsibleEmployees');
                const admittedSelect = document.getElementById('admittedEmployees');
                responsibleSelect.innerHTML = '';
                admittedSelect.innerHTML = '';
                employees.forEach(emp => {
                    const option = new Option(emp.name, emp.name);
                    if (emp.role.includes('MASTER')) {
                        responsibleSelect.add(option);
                    }
                    else {
                        admittedSelect.add(option);
                    }
                });
                if (this.choicesInstances['responsibleEmployees'])
                    this.choicesInstances['responsibleEmployees'].destroy();
                if (this.choicesInstances['admittedEmployees'])
                    this.choicesInstances['admittedEmployees'].destroy();
                const choicesConfig = {
                    removeItemButton: true,
                    shouldSort: false,
                    placeholder: true,
                    placeholderValue: 'Выберите из списка...',
                    noChoicesText: 'Нет вариантов для выбора',
                    itemSelectText: 'Нажмите, чтобы выбрать',
                    searchPlaceholderValue: 'Начните ввод для поиска...',
                    noResultsText: 'Ничего не найдено',
                };
                this.choicesInstances['responsibleEmployees'] = new Choices(responsibleSelect, choicesConfig);
                this.choicesInstances['admittedEmployees'] = new Choices(admittedSelect, choicesConfig);
            }
            catch (error) {
                console.error('Failed to load employees:', error);
            }
        });
    }
    selectOptions(selectId, employeesToSelect) {
        const choiceInstance = this.choicesInstances[selectId];
        if (choiceInstance) {
            const employeeNames = employeesToSelect.map(e => e.name);
            setTimeout(() => {
                choiceInstance.setChoiceByValue(employeeNames);
            }, 150);
        }
    }
    initDetailsPage() {
        return __awaiter(this, void 0, void 0, function* () {
            const contentDiv = document.getElementById('machine-content');
            const pathParts = window.location.pathname.split('/');
            const machineNumber = pathParts[pathParts.length - 1];
            if (!machineNumber)
                return;
            const imageModalEl = document.getElementById('imageViewerModal');
            if (imageModalEl) {
                const modal = window.bootstrap.Modal.getOrCreateInstance(imageModalEl);
                this.imageModalInstance = modal;
                const closeButton = imageModalEl.querySelector('.btn-close');
                if (closeButton) {
                    closeButton.addEventListener('click', () => {
                        modal.hide();
                    });
                }
                imageModalEl.addEventListener('click', (e) => {
                    if (e.target === imageModalEl) {
                        modal.hide();
                    }
                });
            }
            this.showLoading(true);
            try {
                const machine = yield this.fetchData(`${this.apiUrl}/${machineNumber}`);
                document.getElementById('machine-name').textContent = machine.name;
                document.getElementById('edit-button').href = `/machines/${machine.number}/edit`;
                document.getElementById('machine-number').textContent = machine.number.toString();
                document.getElementById('machine-description').textContent = machine.description || 'Нет описания.';
                this.renderList('responsible-employees-list', machine.responsibleEmployeesList, 'Сотрудники не назначены.');
                this.renderList('admitted-employees-list', machine.admittedEmployeesList, 'Сотрудники не назначены.');
                this.renderDocumentList('documents-list', machine.pdfs, 'Документы не найдены.');
                this.renderPhotoGallery('photos-gallery', machine.imageUrls, 'Фотографии не найдены.');
                this.generateQrCode(machine.number);
                document.getElementById('delete-button').addEventListener('click', () => __awaiter(this, void 0, void 0, function* () {
                    if (confirm(`Вы уверены, что хотите удалить станок "${machine.name}"?`)) {
                        try {
                            yield this.fetchData(`${this.apiUrl}/${machine.number}`, { method: 'DELETE' });
                            alert('Станок успешно удален.');
                            window.location.href = '/machines';
                        }
                        catch (error) {
                            console.error('Failed to delete machine:', error);
                            alert('Не удалось удалить станок.');
                        }
                    }
                }));
                document.getElementById('upload-documents-button').addEventListener('click', () => __awaiter(this, void 0, void 0, function* () {
                    const fileInput = document.getElementById('new-documents');
                    if (fileInput.files && fileInput.files.length > 0) {
                        const formData = new FormData();
                        for (const file of Array.from(fileInput.files)) {
                            formData.append('files', file);
                        }
                        try {
                            yield this.fetchData(`${this.apiUrl}/${machine.number}/documents`, {
                                method: 'POST',
                                body: formData
                            });
                            alert('Документы успешно загружены.');
                            location.reload();
                        }
                        catch (error) {
                            console.error('Failed to upload documents:', error);
                            alert('Ошибка при загрузке документов.');
                        }
                    }
                }));
                contentDiv.style.display = 'block';
            }
            catch (error) {
                console.error('Failed to load machine details:', error);
                contentDiv.innerHTML = '<div class="alert alert-danger">Не удалось загрузить данные станка.</div>';
                contentDiv.style.display = 'block';
            }
            finally {
                this.showLoading(false);
            }
        });
    }
    renderList(elementId, items, emptyMessage) {
        const listElement = document.getElementById(elementId);
        listElement.innerHTML = '';
        if (!items || items.length === 0) {
            listElement.innerHTML = `<li class="list-group-item text-muted">${emptyMessage}</li>`;
            return;
        }
        items.forEach(item => {
            const li = document.createElement('li');
            li.className = 'list-group-item';
            const initials = item.name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
            li.innerHTML = `
                <div class="employee-item">
                    <div class="avatar-placeholder">${initials}</div>
                    <span>${item.name}</span>
                </div>
            `;
            listElement.appendChild(li);
        });
    }
    renderPhotoGallery(elementId, items, emptyMessage) {
        const galleryElement = document.getElementById(elementId);
        galleryElement.innerHTML = '';
        if (!items || items.length === 0) {
            galleryElement.innerHTML = `<div class="col"><p class="text-muted">${emptyMessage}</p></div>`;
            return;
        }
        const modalImageEl = document.getElementById('modalImage');
        items.forEach(item => {
            const col = document.createElement('div');
            col.className = 'col-md-4 mb-3';
            col.innerHTML = `
                    <div class="card">
                        <img src="${item.data}" class="img-fluid img-thumbnail" alt="${item.name}" style="height: 200px; object-fit: cover;">
                    </div>
                `;
            col.addEventListener('click', () => {
                if (modalImageEl && this.imageModalInstance) {
                    modalImageEl.src = item.data;
                    this.imageModalInstance.show();
                }
            });
            galleryElement.appendChild(col);
        });
    }
    renderDocumentList(elementId, items, emptyMessage) {
        const listElement = document.getElementById(elementId);
        listElement.innerHTML = '';
        if (!items || items.length === 0) {
            listElement.innerHTML = `<li class="list-group-item text-muted">${emptyMessage}</li>`;
            return;
        }
        items.forEach(item => {
            const li = document.createElement('li');
            li.className = 'list-group-item d-flex justify-content-between align-items-center';
            li.innerHTML = `
                            <div class="icon-text">
                                <i class="bi bi-file-earmark-pdf text-danger"></i>
                                <a href="/api/v1/machines/documents/${item.id}" target="_blank" rel="noopener noreferrer">${item.baseFileName}</a>
                            </div>
                            <button class="btn btn-sm btn-outline-danger delete-document-btn" data-doc-id="${item.id}" title="Удалить">
                                <i class="bi bi-trash"></i>
                            </button>
                        `;
            listElement.appendChild(li);
        });
        listElement.querySelectorAll('.delete-document-btn').forEach(button => {
            button.addEventListener('click', (e) => __awaiter(this, void 0, void 0, function* () {
                const docId = e.target.dataset.docId;
                if (docId && confirm('Вы уверены, что хотите удалить этот документ?')) {
                    try {
                        yield this.fetchData(`/api/v1/machines/documents/${docId}`, { method: 'DELETE' });
                        alert('Документ удален.');
                        location.reload();
                    }
                    catch (error) {
                        console.error('Failed to delete document:', error);
                        alert('Не удалось удалить документ.');
                    }
                }
            }));
        });
    }
    generateQrCode(machineNumber) {
        const qrCodeCanvas = document.getElementById('qr-code-canvas');
        const downLoadQrBtn = document.getElementById('download-qr-btn');
        if (!qrCodeCanvas || !downLoadQrBtn) {
            console.error('Элементы для QR-кода не найдены.');
            return;
        }
        const machineLink = `http://192.168.0.69:2520/machines/${machineNumber}`;
        QRCode.toCanvas(qrCodeCanvas, machineLink, { width: 200, margin: 1 }, (error) => {
            if (error) {
                console.error('Ошибка при генерации QR-кода:', error);
            }
            else {
                downLoadQrBtn.href = qrCodeCanvas.toDataURL('image/png');
            }
        });
    }
}
document.addEventListener('DOMContentLoaded', () => {
    new MachineManager();
});
