const itemsPerPage = 16; //Начальное кол-во строк на странице
const localCache = new Map();

let currentPage = 0;
let totalPagesCount = 1;
let selectedRows = [];
let filters;

//Обработчик работы с окном создания задачи
$(document).on('click', '#createSGI', async function (e) {
    e.preventDefault();

    const dialog = $('#create-dialog');
    dialog.find('[name]').val('');
    // reset file input properly
    dialog.find('input[type="file"]').each(function () {
        const input = $(this).clone();
        input.val('');
        $(this).replaceWith(input);
    });
    dialog.find('.file-list').empty();

    const field = dialog.find('[name="employee"]');
    field.find('option').not(':first').remove();
    const employeesData = await cache.get('employee');
    const filteredEmployees = employeesData.filter(employee =>
        ['EVENT', 'CONTROL'].includes(employee.role)
    );
    filteredEmployees.forEach(employee => {
        field.append($('<option>', {text: employee.name}));
    });
    //Клик вне диалога
    dialog.off('click').on('click', (e) => {
        if (e.target.nodeName === 'DIALOG') {
            localCache.delete('validFileMap');
            localCache.delete('imagesMap');
            e.target.close();
        }
    });
    //Клик по "Отменить"
    dialog.on('click', '#cancelButton', () => {
        localCache.delete('validFileMap');
        localCache.delete('imagesMap');
        dialog[0].close();
    });
    //Нажатие esc
    $(document).on('keydown', (e) => {
        if (e.key === 'Escape' || e.key === 'Esc') {
            localCache.delete('validFileMap');
            localCache.delete('imagesMap');
            dialog[0].close();
        }
    });

    dialog[0].showModal();
});
//Обработчик создания задачи
$(document).on('click', '#createBtn', async function (e) {
    e.preventDefault();

    const button = $(this);
    const form = button.closest('form')[0];
    const formData = new FormData(form);
    const formObject = {};
    formData.forEach((value, key) => {
        if (key === 'additionalFiles') {
            const files = form.querySelector('input[name="additionalFiles"]').files;
            formObject[key] = files;
        } else if (key === 'employee') {
            // Для сотрудника - парсим JSON
            try {
                formObject[key] = JSON.parse(value);
            } catch {
                formObject[key] = value;
            }
        } else {
            formObject[key] = value;
        }
    });
    const files = formObject.additionalFiles;
    delete formObject.additionalFiles;

    const jsonData = JSON.stringify(formObject);

    const newSGI = await $.ajax({
        url: '/api/sgi/create',
        type: 'POST',
        data: jsonData,
        contentType: 'application/json',
        dataType: 'json'
    });
    localCache.set(newSGI.id, newSGI);
    dialog[0].close();
    if (totalRows >= itemsPerPage && !dialog.find('[name="parentId"]').val().length) {
        saveBtn.disabled = false;
        return alert("Создано мероприятие под номером: " + newSGI.number);
    } else {
        await createRow(newSGI, dialog.find('[name="parentId"]').val().length ? newSGI.parent.id : null);
        saveBtn.disabled = false;
    }
});
//Обработчик открытия подзадач
$(document).on('click', '.hamburger', function (e) {
    if ($(e.target).is('input')) {
        return;
    }
    const $currentRow = $(this).closest('.row-items-row');
    const $innerRows = $currentRow.siblings('.row-items-inner-row');
    $innerRows.slideToggle(400);
});
//Обработчик работы с окном редактирования
$(document).on('click', '.editing-btn', async function (e) {
    const currentRow = e.target.closest('.row-items-row');
    const currentIndex = $(currentRow).index();
    const currentId = $(currentRow).data('id');
    const currentSGI = localCache.get(currentId);
    const dialog = $('#editing-dialog');

    for (const [key, value] of Object.entries(currentSGI)) {
        const field = dialog.find(`[data-field="${key}"]`);
        if (!field.length) continue;
        if (key === 'employee') {
            field.empty();
            const employeesData = await cache.get('employee');
            const filteredEmployees = employeesData.filter(employee =>
                ['EVENT', 'CONTROL'].includes(employee.role)
            );
            filteredEmployees.forEach(employee => {
                field.append(
                    $('<option>', {
                        text: employee.name
                    })
                );
            });
        }
        if (key === 'imagesSGI') continue;
        field.val(value || '');
    }
    if ($(currentRow).data('inner') === undefined && !dialog.find('#createSubSGI').length) {
        dialog.find('.modal-footer').prepend(`<button class="btn btn-primary" id="createSubSGI">Создать подзадачу</button>`);
    }

    await renderImages(dialog, 'edit', currentSGI, currentSGI.imagesSGI);
    dialog[0].showModal();

    $('#editing-dialog #saveBtn').off('click').on('click', async function (e) {
        if (currentSGI.agree) {
            return alert("Нельзя редактировать выполненное мероприятие")
        }
        e.preventDefault;

        const formData = new FormData();
        formData.append('id', currentId);
        formData.append('factExecutionSGIBool', false)
        $(dialog).find('[data-field]').each((_, el) => {
            if (el.type !== 'file') {
                formData.append(el.dataset.field, el.value);
            } else {
                for (let file of el.files) {
                    formData.append(el.dataset.field, file);
                }
            }
        });
        const updateSGI = await $.ajax({
            url: '/api/sgi/update',
            type: 'PATCH',
            data: formData,
            processData: false,
            contentType: false,
            dataType: 'json',
            error: () => {
                dialog[0].close();
                dialog.find('#createSubSGI').remove();
                alert('Редактировать может только создатель задачи')
            }
        });
        localCache.set(currentId, updateSGI);
        localCache.delete('validFileMap');
        await createRow(updateSGI, null);
        dialog[0].close();
        dialog.find('#createSubSGI').remove();
    });
    $('#createSubSGI').off('click').on('click', async function () {
        if (currentSGI.agree) {
            return alert("Нельзя редактировать выполненное мероприятие")
        }
        dialog[0].close();
        dialog.find('#createSubSGI').remove();
        const createDialog = $('#create-dialog');
        createDialog.find('[name="parentId"]').val(currentId);

        const field = createDialog.find('[name="employee"]');
        field.find('option').not(':first').remove();
        const employeesData = await cache.get('employee');
        const filteredEmployees = employeesData.filter(employee =>
            ['EVENT', 'CONTROL'].includes(employee.role)
        );
        filteredEmployees.forEach(employee => {
            field.append($('<option>', {text: employee.name})
            );
        });

        createDialog[0].showModal();
    });
    //Клик на крестик
    dialog.find('#cancelButton').off('click').on('click', () => {
        localCache.delete('validFileMap');
        localCache.delete('imagesMap');
        dialog[0].close();
        dialog.find('#createSubSGI').remove();
    });
    //Клик вне диалога
    dialog.off('click').on('click', (e) => {
        if (e.target.nodeName === 'DIALOG') {
            localCache.delete('validFileMap');
            localCache.delete('imagesMap');
            e.target.close();
            dialog.find('#createSubSGI').remove();
        }
    });
    //Нажатие esc
    $(document).on('keydown', (e) => {
        if (e.key === 'Escape' || e.key === 'Esc') {
            localCache.delete('validFileMap');
            localCache.delete('imagesMap');
            dialog[0].close();
            dialog.find('#createSubSGI').remove();
        }
    });
});
//Обработчик работы с окном факт выполнения
$(document).on('click', '.execution-btn', async function (e) {
    e.preventDefault;

    const currentRow = e.target.closest('.row-items-row');
    const currentId = $(currentRow).data('id');
    const currentSGI = localCache.get(currentId);
    const dialog = $('#execution-dialog');

    const executionDialog = document.getElementById('execution-dialog');
    for (const [key, value] of Object.entries(currentSGI.factExecution || {})) {
        const field = executionDialog.querySelector(`[data-field="${key}"]`);
        if (!field || key === 'imagesFactSGI') continue;
        if (key === 'executionDate') {
            field.value = value ? value.split('.').reverse().join('-') : '';
        } else {
            field.value = value || '';
        }
    }

    $(document).on('click', '#execution-dialog #saveBtn', async function () {
        if (currentSGI.agree) {
            return alert("Нельзя редактировать выполненное мероприятие")
        }
        if (dialog.find(`[data-field="executionDate"]`).val() === '') return alert("Не заполнена дата выполнения")
        const formData = new FormData();
        formData.append('id', currentId);
        formData.append('factExecutionSGIBool', true)
        $(dialog).find('[data-field]').each((_, el) => {
            if (el.type !== 'file') {
                formData.append(el.dataset.field, el.value);
            } else {
                for (let file of el.files) {
                    formData.append(el.dataset.field, file);
                }
            }
        });
        const updateSGI = await $.ajax({
            url: '/api/sgi/update',
            type: 'PATCH',
            data: formData,
            processData: false,
            contentType: false,
            dataType: 'json',
            error: () => {
                dialog[0].close();
                alert('Редактировать может только создатель задачи')
            }
        });
        localCache.set(currentId, updateSGI);
        localCache.delete('validFileMap');
        await createRow(updateSGI, null);
        dialog[0].close();
    });

    await renderImages(dialog, 'fact', currentSGI, currentSGI.factExecutionSGI?.imagesFactSGI);
    dialog[0].showModal();

    //Клик на крестик
    dialog.find('#cancelButton').off('click').on('click', () => {
        localCache.delete('validFileMap');
        localCache.delete('imagesMap');
        dialog[0].close();
    });
    //Клик вне диалога
    dialog.off('click').on('click', (e) => {
        if (e.target.nodeName === 'DIALOG') {
            localCache.delete('validFileMap');
            localCache.delete('imagesMap');
            e.target.close();
        }
    });
    //Нажатие esc
    $(document).on('keydown', (e) => {
        if (e.key === 'Escape' || e.key === 'Esc') {
            localCache.delete('validFileMap');
            localCache.delete('imagesMap');
            dialog[0].close();
        }
    });
});
//Обработчик работы с фильтрами
$(document).on('click', '.btn-filters', async function (e) {
    e.preventDefault();

    const dialog = $('#filter-dialog');
    dialog[0].showModal();

    // Заполняем select сотрудников
    const employeeField = dialog.find('[data-field="employee"]');
    employeeField.empty();
    employeeField.append($('<option>', {value: '', text: 'Все сотрудники'}));

    const employeesData = await cache.get('employee');
    const filteredEmployees = employeesData.filter(employee =>
        ['EVENT', 'CONTROL'].includes(employee.role)
    );

    filteredEmployees.forEach(employee => {
        employeeField.append($('<option>', {
            value: employee.name,
            text: employee.name
        }));
    });

    // Обработчик применения фильтров
    dialog.find('#filtered').off('click').on('click', function () {
         filters = {
            number: dialog.find('[data-field="number"]').val().trim(),
            workcenter: dialog.find('[data-field="workcenter"]').val().trim(),
            event: dialog.find('[data-field="event"]').val().trim(),
            actions: dialog.find('[data-field="actions"]').val().trim(),
            department: (dialog.find('[data-field="department"] option:selected').text().trim() === 'Выберите отдел') ? '' : dialog.find('[data-field="department"] option:selected').text().trim(),
            employee: dialog.find('[data-field="employee"]').val(),
            desiredDate: dialog.find('[data-field="desiredDate"]').val(),
            planDate: dialog.find('[data-field="planDate"]').val(),
            note: dialog.find('[data-field="note"]').val().trim()
        };

        // Применяем фильтры к текущей странице
        applyFiltersToCurrentPage(filters);
        dialog[0].close();
    });

    // Обработчик сброса фильтров
    dialog.find('#default-filter').off('click').on('click', function () {
        dialog.find('input, textarea, select').val('');
        // Показываем все строки на текущей странице
        $('.table-content-row').show();
    });

    //Клик на крестик
    dialog.find('#cancelButton').off('click').on('click', (e) => {
        localCache.delete('validFileMap');
        localCache.delete('imagesMap');
        dialog[0].close();
    });

    //Клик вне диалога
    dialog.off('click').on('click', (e) => {
        if (e.target.nodeName === 'DIALOG') {
            localCache.delete('validFileMap');
            localCache.delete('imagesMap');
            e.target.close();
        }
    });
});
// Функция применения фильтров к текущей странице
function applyFiltersToCurrentPage(filters) {
    const rows = document.querySelectorAll('.row-items');

    rows.forEach(row => {
        let notMatch = null;

        for (const [key, value] of Object.entries(filters)) {
            if (!value) continue;
            notMatch = true

            const cell = row.querySelector(`[data-field="${key}"]`);
            if (!cell) continue;

            const cellValue = cell.textContent.trim();

            if (key === 'desiredDate' || key === 'planDate') {
                const formattedDate = formatDate(value);
                if (cellValue === formattedDate) {
                    notMatch = false;
                    break;
                }
            } else if (cellValue.toLowerCase() === value.toLowerCase()) {
                notMatch = false;
                break;
            }
        }

        row.style.display = notMatch != null && notMatch ? 'none' : '';
    });
}
//Обработчик согласования
$(document).on('click', '#toggleAgreement', async function (event) {
    event.preventDefault();
    const isChecked = this.checked;
    const currentRow = $(this).closest('.row-items-row');
    const currentId = $(currentRow).data('id');
    const currentSGI = localCache.get(currentId);

    const formData = new FormData();
    formData.append("id", currentId);
    formData.append("agreed", isChecked);

    if (currentSGI.planDate === null || currentSGI.planDate === "") return alert("Не заполнено поле планируемый срок!");
    if (isChecked && currentSGI.subSGI && !currentSGI.subSGI?.every(sub => sub.agree)) return alert("Все подзадачи должны быть согласованы!");
    if (!isChecked && currentSGI?.parent && currentSGI.parent.agree) return alert("Нельзя отменить согласование подзадачи, если родительская задача согласована!");
    await $.ajax({
        url: '/api/sgi/agree',
        method: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function () {
            currentSGI.agree = isChecked;
            localCache.set(currentId, currentSGI);
            if (isChecked) {
                currentRow.addClass('complete');
            } else {
                currentRow.removeClass('complete');
            }
            currentRow.find('#toggleAgreement').prop('checked', isChecked);
        },
        error: function () {
            alert('Вы не можете закрывать заявку');
        }
    });
});
//Обработчик фото добавление фото
$(document).on('click', '.file-upload', async function () {
    $(this).prop('disabled', true);
    const currentDialog = $(this).closest('dialog');
    const inputFiles = currentDialog.find('[name="additionalFiles"]');
    const imageContainer = currentDialog.find('.file-list');

    //Добавление фото в инпут
    inputFiles.off('change').on('change', async function (e) {
        e.preventDefault();

        const input = e.target;
        const files = input.files;
        input.files = new DataTransfer().files;

        if (!localCache.has('imagesMap')) {
            localCache.set('imagesMap', new Map);
        }
        for (let file of files) {
            if (!localCache.get('imagesMap').has(file.name)) {
                localCache.get('imagesMap').set(file.name, file);
            }
        }
        const dataTransfer = new DataTransfer();
        for (const [fileName, file] of localCache.get('imagesMap')) {
            if (file instanceof File) {
                dataTransfer.items.add(file)
                const imageUrl = URL.createObjectURL(file);
                const fileItem = `
                <div class="file-item">
                    <img src="${imageUrl}" alt="${file.name}">
                </div>`;
                imageContainer.append(fileItem);
                localCache.get('imagesMap').set(file.name, null);
            }
        }
        const validFileMap = localCache.has('validFileMap') ? localCache.get('validFileMap') : new Map();
        for (let file of dataTransfer.files) {
            validFileMap.set(file.name, file);
        }
        localCache.set('validFileMap', validFileMap);
        input.files = Array.from(validFileMap.values()).reduce((dt, file) => (dt.items.add(file), dt), new DataTransfer()).files;
    });
    inputFiles.click();
    $(document).on('click', () => $('.context-menu').remove());
    $(this).prop('disabled', false);
});
//Удаление фото ПКМ В диалоге
$('dialog').on('contextmenu', 'img', function(e) {
    e.preventDefault();
    const currentDialog = $(e.target).closest('dialog');
    $('.context-menu').remove();
    let menu = $('<div class="context-menu"><button class="context-btn">Удалить</button></div>');
    $(currentDialog).append(menu);
    let dialogOffset = $(currentDialog).offset();
    menu.css({
        'position': 'absolute',
        'top': (e.pageY - dialogOffset.top) + 'px',
        'left': (e.pageX - dialogOffset.left) + 'px',
        'background': '#f8f9fa',
        'border': '1px solid #dee2e6',
        'padding': '8px',
        'border-radius': '4px',
        'box-shadow': '0 4px 12px rgba(0,0,0,0.15)'
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
        const imgName = $(e.target).attr('alt');
        if (localCache.has('imagesMap')) {
            localCache.get('imagesMap').delete(imgName);
        }
        const validFileMap = localCache.get('validFileMap');
        validFileMap.delete(imgName)
        localCache.set('validFileMap', validFileMap)
        let input = currentDialog.find('input[type="file"]').clone()[0];
        input.files = Array.from(validFileMap.values()).reduce((dt, file) => (dt.items.add(file), dt), new DataTransfer()).files;
        currentDialog.find('input[type="file"]').replaceWith(input);
        $(e.target).remove();
        menu.remove();
    });
});
//ЛКМ по фото для приближения
$(document).on('click', 'dialog img', function(e) {
    e.preventDefault();
    e.stopPropagation();

    const imgSrc = $(this).attr('src');
    const imgAlt = $(this).attr('alt');
    const currentDialog = $(this).closest('dialog');

    if (!$('#imagePreviewModal').length) {

        currentDialog.append(`
            <div id="imagePreviewModal" style="display: none; position: fixed; z-index: 10000; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.9);">
                <span class="close" style="position: absolute; top: 15px; right: 35px; color: #f1f1f1; font-size: 40px; font-weight: bold; cursor: pointer;">&times;</span>
                <img class="modal-content" id="previewImage" style="margin: auto; display: block; width: 80%; max-width: 700px; margin-top: 40px;">
            </div>
        `);

        // Обработчики для закрытия модального окна
        $(document).on('click', '#imagePreviewModal .close, #imagePreviewModal', function(e) {
            if (e.target.id === 'imagePreviewModal' || e.target.className === 'close') {
                $('#imagePreviewModal').hide();
            }
        });

        // Закрытие по ESC
        $(document).on('keydown', function(e) {
            if (e.key === 'Escape' && $('#imagePreviewModal').is(':visible')) {
                $('#imagePreviewModal').hide();
            }
        });
    }

    $('#previewImage').attr('src', imgSrc).attr('alt', imgAlt);
    $('#imagePreviewModal').show();
});
//Обработчик двойного клика таблицы
$(document).off('dblclick').on('dblclick', '.row-items-row', function () {
    const row = $(this);
    const rowId = $(row).data('id');

    if (row.hasClass('selected-row')) {
        row.removeClass('selected-row');
        selectedRows = selectedRows.filter(id => id !== rowId);
    } else {
        row.addClass('selected-row');
        if (!selectedRows.includes(rowId)) {
            selectedRows.push(rowId);
        }
    }

    $('#deleteRowBtn').text(selectedRows.length > 1 ?
        `Удалить ${selectedRows.length} строк` :
        'Удалить строку');
});
//Обработчик ПКМ по строке таблицы
$(document).off('contextmenu').on('contextmenu', '.row-items-row', function (e) {
    e.preventDefault();
    currentRow = $(this);

    if (selectedRows.length > 0) {
        $('#customContextMenu').css({
            top: e.pageY + 'px',
            left: e.pageX + 'px',
            display: 'block'
        });
    }
    $('#deleteRowBtn').off('click').on('click', function () {
        if (selectedRows.length > 0) {
            deleteSgi(selectedRows);
        } else if (currentRow) {
            const rowId = $(currentRow).data('id');
            deleteSgi([rowId]);
        }
    });

    function deleteSgi(rowIds) {
        if (!rowIds || rowIds.length === 0) return;
        if (!confirm(`Вы уверены, что хотите удалить ${rowIds.length > 1 ? 'выбранные строки' : 'эту строку'}?`)) {
            return;
        }

        $.ajax({
            url: '/api/sgi/delete',
            type: 'DELETE',
            contentType: 'application/json',
            data: JSON.stringify(rowIds),
            success: function () {
                rowIds.forEach(id => {
                    $('.row-items-row[data-id="' + id + '"]').remove();
                    localCache.get(id).subSGI?.forEach(sub => {
                        $('.row-items-row[data-id="' + sub.id + '"]').remove();
                    });
                    localCache.delete(id);
                });
                selectedRows = selectedRows.filter(id => !rowIds.includes(id));

                $('#customContextMenu').hide();
            },
            error: function (xhr) {
                alert('Ошибка при удалении: ' + (xhr.responseJSON?.message || xhr.statusText));
            }
        });
    }

    $('#printRowBtn').off('click').on('click', () => {
        if (selectedRows.length > 0) {
            window.open(`/api/report/print/sgi?ids=${selectedRows.join(',')}`);
        }
        $('#customContextMenu').hide();
    });
});
//Обработчик печати из менью
$('.print-menu-item').on('click', function () {
    const department = $(this).data('department');
    $('<a>', {
        href: `/api/report/print/sgi?department=${department}`,
        download: ''
    }).appendTo('body')[0].click().remove();
});

async function loadSGI(page = 0) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/api/sgi/get-page-sgi',
            type: 'GET',
            data: {
                page: page,
                size: itemsPerPage
            },
            success: function (data, textStatus, jqXHR) {
                resolve({
                    content: data.content,
                    totalPages: data.totalPages,
                    totalRaw: data.totalRaw
                });
            }
        });
    });
}

async function buildPagination(totalPages, current) {
    const $p = $('.pagination');
    $p.empty();

    const createBtn = (label, page, extraClass = '') => {
        const btn = $(`<button class="btn btn-secondary page-btn ${extraClass}" data-page="${page}">${label}</button>`);
        if (page === current) btn.addClass('active');
        return btn;
    };

    // Prev
    if (current > 1) {
        $p.append(createBtn('‹', current - 1, 'prev-btn'));
    } else {
        $p.append($('<button class="btn btn-secondary" disabled>‹</button>'));
    }

    // Показываем компактную навигацию: максимум 7 кнопок (приближённо)
    const maxButtons = 7;
    let start = Math.max(1, current - Math.floor(maxButtons / 2));
    let end = start + maxButtons - 1;
    if (end > totalPages) {
        end = totalPages;
        start = Math.max(1, end - maxButtons + 1);
    }

    if (start > 1) {
        $p.append(createBtn('1', 1));
        if (start > 2) $p.append($('<span class="dots">...</span>'));
    }

    for (let i = start; i <= end; i++) {
        $p.append(createBtn(i, i));
    }

    if (end < totalPages) {
        if (end < totalPages - 1) $p.append($('<span class="dots">...</span>'));
        $p.append(createBtn(totalPages, totalPages));
    }

    // Next
    if (current < totalPages) {
        $p.append(createBtn('›', current + 1, 'next-btn'));
    } else {
        $p.append($('<button class="btn btn-secondary" disabled>›</button>'));
    }
}

async function createRow(item, inner) {
    const hamburger = `
    <label class="hamburger">
        <input type="checkbox">
        <svg viewBox="0 0 32 32">
            <path class="line line-top-bottom" d="M27 10 13 10C10.8 10 9 8.2 9 6 9 3.5 10.8 2 13 2 15.2 2 17 3.8 17 6L17 26C17 28.2 18.8 30 21 30 23.2 30 25 28.2 25 26 25 23.8 23.2 22 21 22L7 22"></path>
            <path class="line" d="M7 16 27 16"></path>
        </svg>                    
    </label>`;

    const borderClass = item.color === 'RED'
        ? 'border-danger' :
        item.color === 'YELLOW'
            ? 'border-warning' :
            item.color === 'GREEN'
                ? 'border-good' : '';
    let row;
    if (inner == null) {
        row = `
                <div class="row-items">
                    <div class="row-items-row ${item.color === 'GREY' ? 'complete' : ''}" data-id="${item.id}">
                        <div class="row-item" data-field="number" style="width: var(--no);">
                            ${item.subSGI && item.subSGI.length > 0 ? hamburger : ''}
                            <span class="${borderClass}">${item.number}</span>
                        </div>
                        <div class="row-item" data-field="workcenter" style="width: var(--workcenter);">${item.workcenter}</div>
                        <div class="row-item" data-field="event" style="width: var(--event);">${item.event}</div>
                        <div class="row-item" data-field="actions" style="width: var(--action);">${item.actions}</div>
                        <div class="row-item" data-field="department" style="width: var(--department);">${item.departmentName}</div>
                        <div class="row-item" data-field="employee" style="width: var(--employee);">${item.employee.name}</div>
                        <div class="row-item" data-field="desiredDate" style="width: var(--desiredDate);">${formatDate(item.desiredDate)}</div>
                        <div class="row-item" data-field="note" style="width: var(--note);">${item.note}</div>
                        <div class="row-item" data-field="planDate" style="width: var(--planDate);">
                             <span class="${borderClass}">${formatDate(item.planDate)}</span>
                        </div>
                        <div class="row-item" data-field="comment" style="width: var(--comment);">${item.comment}</div>
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
                        <div class="row-item" style="width: var(--file); padding: 0">
                            <div data-field="document" contenteditable="false" style="height: 100%; width: 100%">
                                <div class="frame">
                                    <i class="document fa-solid fa-file"></i>
                                </div>
                            </div>
                        </div>
                        <div class="row-item" style="width: var(--status);">
                            <div class="checkbox-wrapper-31">
                                <input type="checkbox" id="toggleAgreement" ${item.agree ? 'checked' : ''}>
                                <svg viewBox="0 0 35.6 35.6">
                                    <circle class="background" cx="17.8" cy="17.8" r="17.8"></circle>
                                    <circle class="stroke" cx="17.8" cy="17.8" r="14.37"></circle>
                                    <polyline class="check" points="11.78 18.12 15.55 22.23 25.17 12.87"></polyline>
                                </svg>
                            </div>
                        </div>
                    </div>
                    <div class="row-items-inner-row">
                        ${item.subSGI && item.subSGI.length > 0 ? `
                            ${item.subSGI.map((subItem) => `
                                <div class="row-items-row ${subItem.color === 'GREY' ? 'complete' : ''}" data-id="${subItem.id}" data-inner="true">
                                    <div class="row-item" data-field="number" style="width: var(--no);">
                                        <span class="${borderClass}"></span>
                                    </div>
                                    <div class="row-item" data-field="workcenter" style="width: var(--workcenter);">${subItem.workcenter}</div>
                                    <div class="row-item" data-field="event" style="width: var(--event);">${subItem.event}</div>
                                    <div class="row-item" data-field="actions" style="width: var(--action);">${subItem.actions}</div>
                                    <div class="row-item" data-field="departament" style="width: var(--department);">${subItem.departmentName}</div>
                                    <div class="row-item" data-field="employee" style="width: var(--employee);">${subItem.employee.name}</div>
                                    <div class="row-item" data-field="desiredDate" style="width: var(--desiredDate);">${formatDate(subItem.desiredDate)}</div>
                                    <div class="row-item" data-field="note" style="width: var(--note);">${subItem.note}</div>
                                    <div class="row-item" data-field="planDate" style="width: var(--planDate);">
                                        <span class="${borderClass}">${formatDate(subItem.planDate)}</span>
                                    </div>
                                    <div class="row-item" data-field="comment" style="width: var(--comment);">${subItem.comment}</div>
                                    <div class="row-item" style="width: var(--editing);">
                                        <button type="button" class="btn btn-info btn-sm editing-btn">
                                            <i class="bi bi-pencil-square"></i>
                                        </button>
                                    </div>
                                    <div class="row-item" style="width: var(--file); padding: 0">
                                        <div data-name="document" contenteditable="false" style="height: 100%; width: 100%">
                                            <div class="frame">
                                                <i class="document fa-solid fa-file tooltip-trigger" data-description="Открыть окно документа"></i>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row-item" style="width: var(--executions);">
                                        <button type="button" class="btn btn-info btn-sm execution-btn">
                                            ✔
                                        </button>
                                    </div>
                                    <div class="row-item" style="width: var(--status);">
                                        <div class="checkbox-wrapper-31">
                                            <input type="checkbox" id="toggleAgreement" ${subItem.agree ? 'checked' : ''}>
                                            <svg viewBox="0 0 35.6 35.6">
                                                <circle class="background" cx="17.8" cy="17.8" r="17.8"></circle>
                                                <circle class="stroke" cx="17.8" cy="17.8" r="14.37"></circle>
                                                <polyline class="check" points="11.78 18.12 15.55 22.23 25.17 12.87"></polyline>
                                            </svg>
                                        </div>
                                    </div>
                                </div>
                            `).join('')}
                        ` : ''}
                    </div>
                </div>`;
    } else {
        row = `
            <div class="row-items-row ${item.color === 'GREY' ? 'complete' : ''}" data-id="${item.id}" data-inner="true">
                <div class="row-item" data-field="number" style="width: var(--no);">
                    <span class="${borderClass}"></span>
                </div>
                <div class="row-item" data-field="workcenter" style="width: var(--workcenter);">${item.workcenter}</div>
                <div class="row-item" data-field="event" style="width: var(--event);">${item.event}</div>
                <div class="row-item" data-field="actions" style="width: var(--action);">${item.actions}</div>
                <div class="row-item" data-field="departament" style="width: var(--department);">${item.departmentName}</div>
                <div class="row-item" data-field="employee" style="width: var(--employee);">${item.employee.name}</div>
                <div class="row-item" data-field="desiredDate" style="width: var(--desiredDate);">${formatDate(item.desiredDate)}</div>
                <div class="row-item" data-field="note" style="width: var(--note);">${item.note}</div>
                <div class="row-item ${borderClass}" data-field="planDate" style="width: var(--planDate);">
                   <span class="${borderClass}">${formatDate(item.planDate)}</span>
                </div>
                <div class="row-item" data-field="comment" style="width: var(--comment);">${item.comment}</div>
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
                        <input type="checkbox" id="toggleAgreement" ${item.agree ? 'checked' : ''}>
                        <svg viewBox="0 0 35.6 35.6">
                            <circle class="background" cx="17.8" cy="17.8" r="17.8"></circle>
                            <circle class="stroke" cx="17.8" cy="17.8" r="14.37"></circle>
                            <polyline class="check" points="11.78 18.12 15.55 22.23 25.17 12.87"></polyline>
                        </svg>
                    </div>
                </div>
            </div>`
    }
    const existingRow = $(`.row-items-row[data-id="${item.id}"]`);
    if (existingRow.length) {
        existingRow.replaceWith(row);
    } else if (inner == null) {
        $('.table-content-rows').append(row);
    } else {
        const parentRow = $(`.row-items-row[data-id="${inner}"]`);
        parentRow.closest('.row-items').children('.row-items-inner-row').append(row);
        if (parentRow.find('.hamburder').length === 0) {
            parentRow.find('.row-item').first().append(hamburger)
        }
    }
}

async function renderImages(currentDialog, type, currentSGI, images) {
    // Конвертация base64 в File
    const base64ToFile = (base64, name) => {
        const arr = base64.split(','), mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
        for (let i = 0; i < n; i++) u8arr[i] = bstr.charCodeAt(i);
        return new File([u8arr], name, {type: mime});
    };
    const imageContainer = currentDialog.find('.file-list');
    imageContainer.empty();
    const validFileMap = new Map();
    if (!images || images === null) {
        const url = `/api/sgi/get-images-${type === 'fact' ? 'fact-sgi' : 'sgi'}`;
        images = await $.ajax({
            url: url,
            type: 'GET',
            data: { id:  type === 'fact' ? currentSGI.factExecution.id : currentSGI.id}
        });
        const processedImages = Array.isArray(images) ? images : [];
        if (type === 'fact') {
            currentSGI.factExecution.imagesFactSGI = processedImages
        } else {
            currentSGI.imagesSGI = processedImages;
        }
    }
    for (const image of images || []) {
        imageContainer.append(`
            <div class="file-item" id="${image.id}">
                <img src="${image.data}" alt="${image.name}">
            </div>`);
        localCache.set(image.name, null);
        validFileMap.set(image.name, base64ToFile(image.data, image.name));
    }
    localCache.set('validFileMap', validFileMap)
    let input = currentDialog.find('input[type="file"]').clone()[0];
    input.files = Array.from(validFileMap.values()).reduce((dt, file) => (dt.items.add(file), dt), new DataTransfer()).files;
    currentDialog.find('input[type="file"]').replaceWith(input);
}

async function displayPage(page = 0) {
    $('.table-content-rows').empty();
    currentPage = page;

    const SGIPage = await loadSGI(page);
    totalPagesCount = SGIPage.totalPages;
    SGIPage.content.forEach((sgi, idx) => {
        localCache.set(sgi.id, sgi);
        if (sgi.subSGI && sgi.subSGI.length) sgi.subSGI.forEach(subSgi => localCache.set(subSgi.id, subSgi));
    });

    for (let i = 0; i < SGIPage.content.length; i++) {
        await createRow(SGIPage.content[i], null);
    }

    await buildPagination(totalPagesCount, currentPage);
    await applyFiltersToCurrentPage(filters)
}

$(document).on('click', '.pagination .page-btn', async function () {
    const page = parseInt($(this).data('page') - 1, 10);
    if (!isNaN(page) && page >= 0 && page <= totalPagesCount) {
        await displayPage(page);
    }
});

$(document).ready(async function () {
    await displayPage(0);
    const style = document.createElement('style');
    style.textContent = `
    .selected-row {
        background-color: #d4edff !important;
    }`;
    document.head.appendChild(style);
});

function formatDate(dateString) {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU');
}

//Мобильный позор
(function () {
    'use strict';

    const mq = window.matchMedia('(max-width: 768px)');

    function isMobile() {
        return mq.matches;
    }

    document.addEventListener('click', function (e) {
        const ham = e.target.closest('.hamburger');
        if (!ham) return;
        const rowItems = ham.closest('.row-items');
        if (!rowItems) return;
        const willOpen = !rowItems.classList.contains('open');
        rowItems.classList.toggle('open', willOpen);
        const checkbox = ham.querySelector('input[type="checkbox"]');
        if (checkbox) {
            try {
                checkbox.checked = willOpen;
            } catch (err) {
            }
        }
    });

    function wrapRowValues(root) {
        root = root || document;
        const rows = root.querySelectorAll('.table-content-rows .row-items-row');
        rows.forEach(row => {
            row.querySelectorAll('.row-item').forEach(item => {
                if (item.querySelector(':scope > .value')) return;
                const valueSpan = document.createElement('span');
                valueSpan.className = 'value';
                while (item.firstChild) {
                    valueSpan.appendChild(item.firstChild);
                }
                item.appendChild(valueSpan);
                item.dataset.mobileProcessed = '1';
                if (!item.dataset.field) {
                    if (item.querySelector('.editing-btn')) item.dataset.field = 'editing';
                    else if (item.querySelector('.execution-btn')) item.dataset.field = 'execution';
                    else if (item.querySelector('.checkbox-wrapper-31')) item.dataset.field = 'status';
                }
            });
        });
    }

    function unwrapRowValues(root) {
        root = root || document;
        const rows = root.querySelectorAll('.table-content-rows .row-items-row');
        rows.forEach(row => {
            row.querySelectorAll('.row-item').forEach(item => {
                const value = item.querySelector(':scope > .value');
                if (!value) return;
                while (value.firstChild) {
                    item.insertBefore(value.firstChild, value);
                }
                value.remove();
                delete item.dataset.mobileProcessed;
            });
        });
    }

    function applyResponsiveWrapping() {
        if (isMobile()) {
            wrapRowValues(document);
        } else {
            unwrapRowValues(document);
        }
    }

    document.addEventListener('DOMContentLoaded', applyResponsiveWrapping);
    window.addEventListener('load', applyResponsiveWrapping);
    mq.addEventListener ? mq.addEventListener('change', applyResponsiveWrapping) : mq.addListener(applyResponsiveWrapping);
    const container = document.querySelector('.table-content-rows');
    if (container) {
        const mo = new MutationObserver((mutations) => {
            if (isMobile()) {
                wrapRowValues(container);
            } else {
                unwrapRowValues(container);
            }
        });
        mo.observe(container, {childList: true, subtree: true});
    }
    setTimeout(applyResponsiveWrapping, 800);
    setTimeout(applyResponsiveWrapping, 1600);
})();
document.addEventListener("DOMContentLoaded", () => {
    document.body.addEventListener("click", (e) => {
        const hamburger = e.target.closest(".hamburger");
        if (!hamburger) return;

        const rowItems = hamburger.closest(".row-items");
        if (!rowItems) return;
        rowItems.classList.toggle("open");
        const checkbox = hamburger.querySelector("input[type=checkbox]");
        if (checkbox) {
            checkbox.checked = rowItems.classList.contains("open");
        }
    });
    document.body.addEventListener("keydown", (e) => {
        if ((e.key === "Enter" || e.key === " ") && e.target.closest(".hamburger")) {
            e.preventDefault();
            e.target.click();
        }
    });
});