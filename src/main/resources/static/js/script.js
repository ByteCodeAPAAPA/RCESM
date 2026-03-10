document.addEventListener('DOMContentLoaded', function () {
    // ==============================
    // 1. DOM Elements
    // ==============================
    const modalElement = document.getElementById('universalModal');
    const modal = modalElement ? new bootstrap.Modal(modalElement) : null;
    const listElement = document.getElementById('modalItemList');
    const searchInput = document.getElementById('modalSearchInput');
    const loadingIndicator = document.getElementById('modalLoading');
    const errorBlock = document.getElementById('modalError');
    const saveBtn = document.getElementById('modalSaveBtn');
    const footer = document.getElementById('modalFooter');
    const saveCommentId = document.getElementById('commentOtkId');
    const openBtn = document.getElementById('openOtkId');
    const textarea = document.getElementById('commentOtk');
    const qtyField = document.getElementById('qtyCreateField');
    const controlField = document.getElementById('divControlId');
    const reasonField = document.getElementById('divReasonsId');
    const commentField = document.getElementById('divCommentId');
    const customerField = document.getElementById('divCustomerId');
    const entityId = document.getElementById('id');
    const photoModalElement = document.getElementById('photoModal');
    const photoModalInstance = photoModalElement ? new bootstrap.Modal(photoModalElement) : null;
    const viewRequestForm = document.getElementById('viewRequestForm');
    const photoContainer = document.getElementById('photoContainer');
    const fullPhoto = document.getElementById('fullPhoto');

    // ==============================
    // 2. Utilities
    // ==============================
    let timeout;

    /**
     * Fetch data from server
     */
    async function fetchData(endpoint, param) {
        const url = new URL('/api/request/' + endpoint, window.location.origin);
        url.searchParams.append('param', param != null ? param : bidType);
        const response = await fetch(url.toString());
        return response.json();
    }

    /**
     * Show notification
     */
    function notification(message, duration = 3000, type = 'info') {
        const container = document.getElementById('notification-container');
        if (!container) return;
        const notification = document.createElement('div');

        notification.className = `notification ${type}`;
        notification.textContent = message;

        container.appendChild(notification);
        setTimeout(() => notification.classList.add('show'), 10);

        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => notification.remove(), 500);
        }, duration);
    }

    // ==============================
    // 3. Data Saving
    // ==============================
    async function saveData(images, customerOrder) {
        if (!viewRequestForm) return;

        const formData = new FormData(viewRequestForm);
        const data = Object.fromEntries(formData.entries());

        if (images) data.images = images;
        if (customerOrder) data.customerOrder = customerOrder;

        const url = new URL('/api/request/update', window.location.origin);
        if (!entityId) return;
        url.searchParams.append('bidType', bidType);
        url.searchParams.append('id', entityId.value);
        url.searchParams.append('sendMessage', data.sendToTelegram);

        delete data.sendToTelegram;

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Class-Name': bidType,
                'X-Entity-Id': entityId.value
            },
            body: JSON.stringify(data),
        });

        if (response.status === 403) {
            window.location.href = `/error?message=Нет доступа к закрытию или редактированию заявки`;
            return;
        }

        notification('Запись сохранена', 3000, 'success');
        if (images) renderPhotos(images);
    }

    // ==============================
    // 4. Photo Management
    // ==============================

    // Attach listeners once using event delegation
    if (photoContainer) {
        photoContainer.addEventListener('click', function (e) {
            const target = e.target;
            if (target.classList.contains('delete-photo-btn')) {
                const index = target.dataset.index;
                deletePhoto(index);
            } else if (target.tagName === 'IMG') {
                const imageUrl = target.src;
                if (fullPhoto && photoModalInstance) {
                    fullPhoto.src = imageUrl;
                    photoModalInstance.show();
                }
            }
        });
    }

    if (fullPhoto) {
        fullPhoto.addEventListener('click', function () {
            this.style.display = 'none';
        });
    }

    function renderPhotos(images) {
        if (!photoContainer) return;
        photoContainer.innerHTML = '';

        if (!images || images.length === 0) {
            photoContainer.innerHTML = '<div class="no-photos">Нет прикрепленных фото</div>';
            return;
        }

        images.forEach((imgData, index) => {
            const imgWrapper = document.createElement('div');
            imgWrapper.className = 'photo-wrapper';
            const imageUrl = typeof imgData === 'string' ? imgData : imgData.data;

            imgWrapper.innerHTML = `
                <img src="${imageUrl}" class="attached-photo">
                <button class="delete-photo-btn" data-index="${index}">Удалить</button>
            `;
            photoContainer.appendChild(imgWrapper);
        });
    }

    async function deletePhoto(index) {
        if (!entityId) return;
        const id = entityId.value;
        let images = await fetchData('images', id);
        const imageToDelete = images[index];
        if (!imageToDelete) return;

        const reqId = imageToDelete.mainlink;
        const imageId = imageToDelete.id;

        try {
            const response = await fetch('/api/request/delete-images', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    id: imageId,
                    reqId: reqId
                })
            });

            if (response.ok) {
                await response.json();
                Swal.fire({
                    icon: 'success',
                    title: 'Успех!',
                    text: 'Данные успешно удалены!',
                    timer: 1000,
                    showConfirmButton: false
                });
            } else {
                const errorText = await response.text();
                throw new Error(errorText);
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Ошибка!',
                text: error.message || 'Произошла ошибка при удалении данных',
                timer: 5000
            });
        }


        images.splice(index, 1);
        renderPhotos(images);

        if (fullPhoto) {
            const currentSrc = fullPhoto.src;
            const deletedSrc = typeof imageToDelete === 'string' ? imageToDelete : imageToDelete.data;
            if (currentSrc === deletedSrc) {
                fullPhoto.style.display = 'none';
                fullPhoto.src = '';
            }
        }
    }

    // ==============================
    // 5. Input Handling with Debounce
    // ==============================
    if (typeof viewForm !== 'undefined' && viewForm && document.title.includes('Заявка на вызов')) {
        const delayedSave = (callback) => {
            clearTimeout(timeout);
            timeout = setTimeout(callback, 1500);
        };

        // Event listeners for input fields
        document.getElementById('comment')?.addEventListener('input', () => delayedSave(() => saveData()));
        document.getElementById('commentAgreed')?.addEventListener('input', () => delayedSave(() => saveData()));

        const customerOrderString = document.getElementById('customerOrderString');
        if (customerOrderString) {
            customerOrderString.addEventListener('input', function () {
                const customerOrderValue = this.value;
                delayedSave(() => saveData(null, customerOrderValue));
            });
        }


        // Photo modal handling
        const openPhotoModalBtn = document.getElementById('openPhotoModal');
        if (openPhotoModalBtn) {
            openPhotoModalBtn.addEventListener('click', async () => {
                if (!entityId) return;
                const images = await fetchData('images', entityId.value);
                renderPhotos(images);
                if (photoModalElement) {
                    photoModalElement.classList.add('open');
                }
            });
        }

        const addPhotoBtn = document.getElementById('addPhotoBtn');
        const photoInput = document.getElementById('photoInput');
        if (addPhotoBtn) {
            addPhotoBtn.addEventListener('click', () => {
                if (photoInput) {
                    photoInput.click();
                }
            });
        }

        if (photoInput) {
            photoInput.addEventListener('change', async function (event) {
                const files = Array.from(event.target.files);
                if (files.length === 0 || !entityId) return;

                let images = await fetchData('images', entityId.value) || [];

                for (const file of files) {
                    const tempPreview = document.createElement('div');
                    tempPreview.className = 'photo-wrapper temporary';
                    tempPreview.innerHTML = `
                        <img src="" class="attached-photo loading">
                        <button class="delete-photo-btn" disabled>Удалить</button>
                    `;
                    document.getElementById('photoContainer')?.prepend(tempPreview);

                    const reader = new FileReader();

                    await new Promise((resolve, reject) => {
                        reader.onload = async (e) => {
                            try {
                                const img = tempPreview.querySelector('img');
                                if (img) {
                                    img.src = e.target.result;
                                    img.classList.remove('loading');
                                }
                                images.push(e.target.result);
                                await saveData(images);
                                // The line below was causing re-rendering issues, saveData already calls renderPhotos
                                // renderPhotos(images);
                                resolve();
                            } catch (err) {
                                reject(err);
                            }
                        };
                        reader.onerror = () => {
                            tempPreview.innerHTML = '<div class="error">Ошибка загрузки</div>';
                            reject(new Error('Ошибка чтения файла'));
                        };
                        reader.readAsDataURL(file);
                    });
                }
                event.target.value = '';
            });
        }

        const fullPhotoModal = document.getElementById('fullPhotoModal');
        if (fullPhotoModal) {
            fullPhotoModal.addEventListener('click', function (event) {
                if (event.target === this || event.target.classList.contains('close')) {
                    this.classList.remove('open');
                }
            });
        }
    }

    // ==============================
    // 6. Form Validation
    // ==============================
    document.querySelector('form')?.addEventListener('submit', function (event) {
        const requiredFields = document.querySelectorAll('[data-required]');
        let valid = true;

        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                valid = false;
                field.classList.add('error-field');
            } else {
                field.classList.remove('error-field');
            }
        });

        if (!valid) {
            event.preventDefault();
            notification('Заполните обязательные поля', 3000, 'error');
        }
    });


    // ==============================
    // 8. Action Buttons Handler
    // ==============================
    const actionButtons = document.querySelectorAll('button.work[data-param]');
    const actionReworkButton = document.getElementById('successRework');

    if (actionReworkButton) {
        actionReworkButton.addEventListener('click', () => handleReworkClick(actionReworkButton));
    }

    const handleClick = async (button) => {
        const requestId = button.dataset.param;
        const status = button.dataset.status;
        let description;

        const inconsistencyInput = document.getElementById('inconsistencyJson1');
        const inconsistencyData = inconsistencyInput?.value || '';
        const descriptionsCompleted = document.getElementById('descriptionCompletedId')?.value || '';

        if (typeof bidType !== 'undefined' && bidType === 'otk') {
            description = document.getElementById('description2')?.value || '';
            if (description === '') {
                description = document.getElementById('description1')?.value || '';
            }
        } else if (typeof bidType !== 'undefined' && bidType === 'technologist') {
            description = document.getElementById('descriptionTechnologyId')?.value || '';
        } else {
            description = document.getElementById('description')?.value || '';
        }

        let qty = document.getElementById('qtyCompleted')?.value || '';

        const formData = {
            requestId: requestId,
            description: description,
            status: status,
            qtyCompleted: qty,
            inconsistencyData: inconsistencyData,
            descriptionsCompleted: descriptionsCompleted
        };
        try {
            const response = await fetch('/api/request/in-work', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });
            const text = await response.text();
            if (response.ok) {
                let message = 'Данные успешно сохранены!';
                try {
                    const data = JSON.parse(text);
                    message = data.message || data;
                } catch {
                    message = text;
                }
                Swal.fire({
                    icon: 'success',
                    title: 'Успех!',
                    text: message,
                    timer: 2000,
                    showConfirmButton: false
                });
                setTimeout(() => location.reload(), 2000);
            } else {
                throw new Error(text);
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Ошибка!',
                text: error.message || 'Произошла ошибка при сохранении данных',
                timer: 5000
            });
        }
    };

    const handleReworkClick = async (button) => {
        const status = button.dataset.status;
        const requestId = button.dataset.param;
        const formData = {
            status: status,
            requestId: requestId
        };
        await fetch('/api/request/in-work', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
    };

    actionButtons.forEach(button => {
        button.addEventListener('click', () => handleClick(button));
    });

    // ==============================
    // 9. Universal Modal Logic
    // ==============================
    (function () {
        let multiple = false;
        let selected = new Map();
        let originalData = [];
        let inputId, hiddenId;

        if (!searchInput || !listElement || !errorBlock || !loadingIndicator || !footer || !modalElement || !saveBtn) {
            return;
        }

        searchInput.classList.remove('visible');

        searchInput.addEventListener('input', () => {
            const query = searchInput.value.toLowerCase();
            renderList(originalData.filter(item => item.name.toLowerCase().includes(query)));
        });

        function renderList(data) {
            listElement.innerHTML = '';
            data.forEach(item => {
                const li = document.createElement('li');
                li.className = 'list-group-item list-group-item-action selectable';
                li.textContent = item.name;
                li.dataset.entity = encodeURIComponent(JSON.stringify(item));

                if (multiple && selected.has(item.name)) {
                    li.classList.add('active');
                }

                li.addEventListener('click', () => {
                    const currentInput = document.getElementById(inputId);
                    const currentHidden = document.getElementById(hiddenId);

                    if (!currentInput || !currentHidden) return;

                    if (multiple) {
                        if (selected.has(item.name)) {
                            selected.delete(item.name);
                            li.classList.remove('active');
                        } else {
                            selected.set(item.name, item);
                            li.classList.add('active');
                        }
                        const arr = Array.from(selected.values());
                        currentInput.value = arr.map(e => e.name).join(', ');
                        currentHidden.value = JSON.stringify(arr);
                    } else {
                        handleItemSelection(item);
                        currentInput.value = item.name;
                        currentHidden.value = JSON.stringify(item);
                        if (modal) modal.hide();
                        if (typeof saveData === 'function') saveData();
                    }
                });
                listElement.appendChild(li);
            });
        }

        window.openUniversalModal = async function ({
                                                        endpoint,
                                                        param,
                                                        inputFieldId,
                                                        hiddenFieldId,
                                                        isMultiple = false,
                                                        title = 'Выберите элемент'
                                                    }) {
            inputId = inputFieldId;
            hiddenId = hiddenFieldId;
            multiple = isMultiple;
            selected = new Map();
            originalData = [];

            const hiddenField = document.getElementById(hiddenId);

            modalElement.querySelector('.modal-title').textContent = title;
            searchInput.value = '';
            listElement.innerHTML = '';
            errorBlock.classList.add('d-none');
            loadingIndicator.classList.remove('d-none');
            footer.classList.toggle('d-none', !multiple);

            if (multiple && hiddenField) {
                try {
                    const raw = hiddenField.value;
                    const parsed = JSON.parse(raw || '[]');
                    parsed.forEach(e => e?.name && selected.set(e.name, e));
                } catch (e) {
                    console.warn('Ошибка парсинга выбранных значений:', e);
                }
            }

            try {
                const url = new URL('/api/' + endpoint, window.location.origin);
                url.searchParams.append('param', param || '');
                const response = await fetch(url);
                const data = await response.json();
                originalData = Array.isArray(data) ? data : [];

                if (originalData.length > 5) {
                    searchInput.classList.add('visible');
                } else {
                    searchInput.classList.remove('visible');
                }
                renderList(originalData);
            } catch (e) {
                errorBlock.classList.remove('d-none');
            } finally {
                loadingIndicator.classList.add('d-none');
                if (modal) modal.show();
            }
        };

        saveBtn.addEventListener('click', () => {
            const currentInput = document.getElementById(inputId);
            const currentHidden = document.getElementById(hiddenId);
            if (!currentInput || !currentHidden) return;

            const arr = Array.from(selected.values());
            currentInput.value = arr.map(e => e.name).join(', ');
            currentHidden.value = JSON.stringify(arr);
            if (modal) modal.hide();
            if (typeof saveData === 'function') saveData();
        });
    })();

    // ==============================
    // 10. Modal Openers
    // ==============================
    document.querySelectorAll('.openModal').forEach(button => {
        button.addEventListener('click', () => {
            const {
                endpoint,
                inputId,
                hiddenEntity,
                param = '',
                multiple,
                title = 'Выберите элемент'
            } = button.dataset;
            if (window.openUniversalModal) {
                window.openUniversalModal({
                    endpoint,
                    param,
                    inputFieldId: inputId,
                    hiddenFieldId: hiddenEntity,
                    isMultiple: multiple === 'true',
                    title
                });
            }
        });
    });

    // ==============================
    // 11. Comment Saving
    // ==============================
    if (saveCommentId) {
        saveCommentId.addEventListener('click', () => {
            const id = saveCommentId.getAttribute('data-param');
            const commentEl = document.getElementById('commentOtk');
            const comment = commentEl ? commentEl.value : '';

            fetch('/api/request/comment-bid', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'id': id,
                    'comment': comment
                })
            }).catch(error => {
                alert('Ошибка при отправке запроса');
            });
        });
    }

    // ==============================
    // 12. Inconsistencies Form
    // ==============================
    const form = document.getElementById('inconsistenciesForm');
    const addInconsistenciesModalEl = document.getElementById('addInconsistenciesModal');
    const mainModalElement = document.getElementById('exampleModalOtk');
    let addInconsistenciesModal = null;

    if (addInconsistenciesModalEl) {
        addInconsistenciesModal = new bootstrap.Modal(addInconsistenciesModalEl);
    }

    if (mainModalElement) {
        mainModalElement.addEventListener('hidden.bs.modal', function () {
            const backdrops = document.querySelectorAll('.modal-backdrop');
            backdrops.forEach(backdrop => backdrop.remove());
            document.body.classList.remove('modal-open');
            document.body.style.overflow = '';
            document.body.style.paddingRight = '';
        });
        mainModalElement.addEventListener('hide.bs.modal', function () {
            const backdrops = document.querySelectorAll('.modal-backdrop');
            if (backdrops.length > 1) {
                backdrops[backdrops.length - 1].remove();
            }
        });
    }

    if (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());

            fetch(form.action, {
                method: 'POST',
                body: JSON.stringify(data),
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            }).then(response => {
                if (response.ok) {
                    return response.json().then(() => {
                        if (addInconsistenciesModal) addInconsistenciesModal.hide();
                        form.reset();
                        Swal.fire({
                            icon: 'success',
                            title: 'Успех!',
                            text: 'Данные успешно сохранены!',
                            timer: 2000,
                            showConfirmButton: false
                        });
                    });
                } else {
                    return response.json().then(errorData => {
                        throw new Error(errorData.error || 'Произошла ошибка');
                    });
                }
            }).catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: 'Ошибка!',
                    text: error.message || 'Произошла ошибка при сохранении данных',
                    timer: 5000
                });
            });
        });
    }


    const openModalBtn = document.getElementById('openModalBtn');
    if (openModalBtn) {
        openModalBtn.addEventListener('click', function () {
            const firstModalElement = document.getElementById('exampleModalOtk');
            const firstModal = bootstrap.Modal.getInstance(firstModalElement);

            if (firstModal) {
                firstModal.hide();
                firstModalElement.addEventListener('hidden.bs.modal', function () {
                    setTimeout(() => {
                        if (addInconsistenciesModal) addInconsistenciesModal.show();
                    }, 300);
                }, {
                    once: true
                });
            } else {
                if (addInconsistenciesModal) addInconsistenciesModal.show();
            }
            if (form) form.reset();
        });
    }

    if (addInconsistenciesModalEl) {
        addInconsistenciesModalEl.addEventListener('hidden.bs.modal', function () {
            const firstModalElement = document.getElementById('exampleModalOtk');
            if (firstModalElement) {
                const firstModal = new bootstrap.Modal(firstModalElement);
                firstModal.show();
            }
            if (form) form.reset();
        });
        addInconsistenciesModalEl.addEventListener('hide.bs.modal', function () {
            const backdrops = document.querySelectorAll('.modal-backdrop');
            if (backdrops.length > 1) {
                backdrops[backdrops.length - 1].remove();
            }
        });
    }


    function fixModalBackdrops() {
        const backdrops = document.querySelectorAll('.modal-backdrop');
        if (backdrops.length > 1) {
            for (let i = 1; i < backdrops.length; i++) {
                backdrops[i].remove();
            }
        }
    }

    setInterval(fixModalBackdrops, 100);
    document.addEventListener('click', function (e) {
        if (e.target.classList.contains('modal-backdrop')) {
            fixModalBackdrops();
        }
    });

    // ==============================
    // 13. Comment Toggle
    // ==============================
    if (openBtn && textarea && saveCommentId) {
        openBtn.addEventListener('click', function () {
            if (textarea.style.display === 'none' || textarea.style.display === '') {
                textarea.style.display = 'block';
                saveCommentId.style.display = 'inline-block';
            } else {
                textarea.style.display = 'none';
                saveCommentId.style.display = 'none';
            }
        });
    }

    // ==============================
    // 14. Item Selection Handler
    // ==============================
    function handleItemSelection(selectedItem) {
        const itemName = selectedItem.name || selectedItem;
        const fieldsToToggle = [qtyField, controlField, reasonField, commentField, customerField];
        const shouldHide = itemName === 'Карта раскроя';

        fieldsToToggle.forEach(field => {
            if (field) {
                field.style.display = shouldHide ? 'none' : 'block';
            }
        });
    }

    // ==============================
    // 15. Completed Field Editing
    // ==============================
    if (typeof viewForm !== 'undefined' && viewForm) {
        const successQtyInput = document.getElementById('qtyCompleted');
        const rejectedBlock = document.getElementById('rejectedBidOtk');
        const successIdButton = document.getElementById('successId');

        if (typeof bidQty !== 'undefined' && bidQty > 0 && successQtyInput && rejectedBlock) {
            successQtyInput.addEventListener('input', function () {
                const enteredValue = parseInt(successQtyInput.value, 10);
                if (!isNaN(enteredValue) && enteredValue < bidQty) {
                    rejectedBlock.classList.remove('d-none');
                } else {
                    rejectedBlock.classList.add('d-none');
                }
            });
        }

        if (successIdButton) {
            successIdButton.addEventListener('click', function (e) {
                const form = document.querySelector('.modal-content');
                const qtyInput = document.getElementById('qtyCompleted');
                if (qtyInput && !qtyInput.checkValidity()) {
                    e.preventDefault();
                    e.stopPropagation();
                    form?.classList.add('was-validated');
                    qtyInput.focus();
                }
            });
        }


        const qtyCompletedInput = document.getElementById('qtyCompleted');
        const movedQuantitySpan = document.getElementById('movedQuantity');

        if (qtyCompletedInput && movedQuantitySpan && typeof bidQty !== 'undefined') {
            const maxQty = bidQty;

            function updateMovedQuantity() {
                let completedQty = parseInt(qtyCompletedInput.value);

                if (isNaN(completedQty) || completedQty < 0) {
                    completedQty = 0;
                } else if (completedQty > maxQty) {
                    completedQty = maxQty;
                    qtyCompletedInput.value = maxQty;
                }

                const movedQty = maxQty - completedQty;
                movedQuantitySpan.textContent = movedQty;
                movedQuantitySpan.style.color = movedQty > 0 ? 'red' : 'green';
            }

            qtyCompletedInput.addEventListener('input', updateMovedQuantity);
            qtyCompletedInput.addEventListener('change', updateMovedQuantity);

            updateMovedQuantity(); // Initial call
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        const modal = document.getElementById('updateEmployeeModalId');

        modal.addEventListener('show.bs.modal', function (event) {
            // Получаем requestId из кнопки, которая открыла модалку
            const button = event.relatedTarget; // Кнопка, которая открыла модалку
            const requestId = button.getAttribute('data-param') || '${bid.getId()}';

            // Устанавливаем в скрытое поле
            document.getElementById('requestIdInput').value = requestId;
            console.log('Modal opened with requestId:', requestId);
        });
    });

    document.addEventListener('DOMContentLoaded', function () {
        const modal = document.getElementById('updateEmployeeModalId');

        modal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const requestId = button ? button.getAttribute('data-request-id') : '${bid.getId()}';

            document.getElementById('requestIdInput').value = requestId;
            console.log('Request ID установлен:', requestId);
        });

        // Начальное значение на случай прямого открытия
        document.getElementById('requestIdInput').value = '${bid.getId()}';
    });

    if (typeof viewForm !== 'undefined' && viewForm) {
        document.getElementById('updateUserModalSaveBtn').addEventListener('click', function () {
            const requestId = this.getAttribute('data-req');
            const selectElement = document.getElementById('masterSelect').value;
            fetch("/api/request/create-by", {
                method: 'POST',
                body: JSON.stringify({
                    requestId: requestId,
                    user: selectElement
                }),
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            }).then(response => {
                if (response.ok) {
                    return response.text().then((text) => {
                        if (form) form.reset();
                        Swal.fire({
                            icon: 'success',
                            title: 'Успех!',
                            text: text || 'Данные успешно сохранены!',
                            timer: 2000,
                            showConfirmButton: false
                        });
                        $('#updateEmployeeModalId').modal('hide');
                    });
                } else {
                    return response.json().then(errorData => {
                        throw new Error(errorData.error || 'Произошла ошибка');
                    });
                }
            })
        });
    }
});