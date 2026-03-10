var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var NotificationType;
(function (NotificationType) {
    NotificationType["SUCCESS"] = "success";
    NotificationType["ERROR"] = "error";
    NotificationType["WARNING"] = "warning";
    NotificationType["INFO"] = "info";
})(NotificationType || (NotificationType = {}));
var Color;
(function (Color) {
    Color["NONE"] = "NONE";
    Color["RED"] = "RED";
    Color["GREEN"] = "GREEN";
    Color["YELLOW"] = "YELLOW";
    Color["GREY"] = "GREY";
    Color["BLUE"] = "BLUE";
})(Color || (Color = {}));
class Base {
    constructor(rowContainer, itemsPerPage = Infinity, visibleRow = Infinity, ...initCallbacks) {
        this.locks = new Map();
        this.handlers = [];
        this.selectedRows = new Set();
        this.localCache = new Map();
        this.currentPage = 1;
        this.saveMassive = {};
        this.cache = new CacheBormashImpl();
        this.dialog = new DialogImpl();
        //Блокировка параллельного выполнения
        this.lock = (fn) => (...args) => __awaiter(this, void 0, void 0, function* () {
            const key = fn.name;
            if (this.locks.get(key))
                return;
            this.locks.set(key, true);
            try {
                return yield fn(...args);
            }
            finally {
                this.locks.set(key, false);
            }
        });
        this.createHandler = (event, selector, handler, locked = false) => {
            this.handlers.push({
                event,
                selector,
                handler: locked ? this.lock(handler) : handler,
            });
        };
        this.createNotificationContainer = () => {
            const notificationsContainer = $(`<div id="notifications-container" popover="manual"></div>`);
            $('body').append(notificationsContainer);
        };
        this.updateRow = (item, rowIndex) => {
            const $oldRow = $(`[data-index="${rowIndex}"]`);
            const $newRow = this.createRow(item).hide();
            $oldRow.fadeOut(100, () => {
                $oldRow.replaceWith($newRow);
                $newRow.fadeIn(280);
                this.localCache.set(item.id, item);
            });
        };
        this.switchVisibilityRow = (rowIndex, hide) => {
            $(`[data-index="${rowIndex}"]`)[hide ? 'fadeOut' : 'fadeIn'](300);
        };
        this.save = (url, ...items) => __awaiter(this, void 0, void 0, function* () {
            const results = yield Promise.all(items.map(item => {
                const id = item.id;
                const version = item.version;
                const changes = item.changes;
                return this.requestToApi(`${url}/${id}${version != null ? `?version=${version}` : ''}`, 'PATCH', changes);
            }));
            results.forEach(item => {
                this.updateRow(item, item.id);
                delete this.saveMassive[item.id];
            });
            this.createNotification('Успешно обновлено', NotificationType.SUCCESS);
            return results;
        });
        this.requestToApi = (url, type, param) => __awaiter(this, void 0, void 0, function* () {
            return yield $.ajax({
                url: url,
                method: type,
                contentType: param instanceof FormData ? false : 'application/json',
                processData: !(param instanceof FormData),
                data: param instanceof FormData ? param : JSON.stringify(param)
            }).catch((xhr) => {
                const errorResponse = xhr.responseJSON;
                this.createNotification(errorResponse.message, errorResponse.notificationType);
            });
        });
        //Скачивает все файлы с api
        this.downloadFile = (url, params) => __awaiter(this, void 0, void 0, function* () {
            try {
                url = url + (params ? `?${new URLSearchParams(params).toString()}` : '');
                const response = yield this.requestToApi(url, 'GET');
                const files = Array.isArray(response) ? response : [response];
                for (const file of files) {
                    // @ts-ignore
                    //Тут может быть какая то ошибка
                    const binaryString = atob(file.data);
                    const uint8Array = new Uint8Array(binaryString.length);
                    for (let i = 0; i < binaryString.length; i++) {
                        uint8Array[i] = binaryString.charCodeAt(i);
                    }
                    const blob = new Blob([uint8Array]);
                    const objectUrl = URL.createObjectURL(blob);
                    const link = document.createElement('a');
                    link.href = objectUrl;
                    link.download = file.name;
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);
                    setTimeout(() => URL.revokeObjectURL(objectUrl), 250);
                    if (files.length > 1)
                        yield new Promise(resolve => setTimeout(resolve, 1250));
                }
            }
            catch (error) {
                this.createNotification('Ошибка при скачивании файла', NotificationType.ERROR);
                console.error(error);
            }
        });
        this.createEntity = (url, dto) => {
            return this.requestToApi(url, 'POST', dto);
        };
        this.deleteEntity = (url) => {
            return this.requestToApi(`${url}`, 'DELETE');
        };
        //Создание уведомления в левом верхнем углу
        this.createNotification = (message, type, params, error) => {
            try {
                const text = params ? message.replace(/{(\w+)}/g, (m, k) => params[k]) : message;
                const container = document.getElementById('notifications-container');
                const notification = document.createElement('div');
                notification.className = `notification ${type}`;
                notification.innerHTML = `<div class="msg">${text}</div>`;
                container.appendChild(notification);
                if (!container.matches(':popover-open')) {
                    container.showPopover();
                }
                if (error)
                    console.error(error);
                setTimeout(() => notification.classList.add('show'), 10);
                setTimeout(() => {
                    notification.classList.remove('show');
                    notification.classList.add('hiding');
                    setTimeout(() => {
                        notification.remove();
                        if (container.children.length === 0) {
                            container.hidePopover();
                        }
                    }, 350);
                }, 3000);
            }
            catch (error) {
                console.error(error);
            }
        };
        //Диалог с подтверждением действия
        this.createConfirmationDialog = this.lock((message, params) => {
            return new Promise((resolve) => {
                const text = params ? message.replace(/{(\w+)}/g, (m, k) => params[k]) : message;
                let $dialog = $('#confirmDialog');
                if ($dialog.length === 0) {
                    $dialog = $(`
                    <dialog id="confirmDialog" class="confirm-dialog">
                        <div class="confirm-content">
                            <div class="confirm-message" id="confirmMessage">${text}</div>
                            <div class="confirm-buttons">
                                <button class="confirm-btn confirm-cancel" id="confirmCancel">Отмена</button>
                                <button class="confirm-btn confirm-ok" id="confirmOk">Подтвердить</button>
                            </div>
                        </div>
                    </dialog>
                `);
                    $('body').append($dialog);
                }
                else {
                    $('#confirmMessage').text(text);
                }
                const cleanup = () => {
                    $('#confirmCancel').off('click');
                    $('#confirmOk').off('click');
                    this.dialog.close('confirmDialog');
                };
                $('#confirmCancel').on('click', () => {
                    cleanup();
                    resolve(false);
                });
                $('#confirmOk').on('click', () => {
                    cleanup();
                    resolve(true);
                });
                this.dialog.open('confirmDialog', {
                    clearFields: false,
                    onClose: () => {
                        cleanup();
                        resolve(false);
                    }
                });
            });
        });
        //Контекстное меню
        this.createContextMenu = (items, x, y) => {
            $('#context-menu').remove();
            const menu = $('<div id="context-menu" popover="manual"></div>');
            items.forEach(item => {
                const $item = $(`<div id="${item.idAction}">${item.label}</div>`);
                $item.on('click', () => {
                    item.action();
                    menu[0].hidePopover();
                });
                menu.append($item);
            });
            $('body').append(menu.css({
                left: x + 'px',
                top: y + 'px',
            }));
            menu[0].showPopover();
            $(document).one('click', (e) => {
                if (!$(e.target).closest('#context-menu').length) {
                    menu[0].hidePopover();
                }
            });
        };
        this.showToolTip = (event) => {
            const element = event.currentTarget;
            const tooltipTimeout = setTimeout(() => {
                const description = element.getAttribute('data-description');
                if (!description)
                    return;
                const tooltip = document.createElement('div');
                tooltip.className = 'custom-tooltip';
                tooltip.textContent = description;
                document.body.appendChild(tooltip);
                const rect = element.getBoundingClientRect();
                tooltip.style.position = 'absolute';
                tooltip.style.left = `${rect.left + window.pageXOffset}px`;
                tooltip.style.top = `${rect.bottom + window.pageYOffset + 5}px`;
                element._currentTooltip = tooltip;
            }, 450);
            element._tooltipTimeout = tooltipTimeout;
            const hideHandler = () => {
                clearTimeout(tooltipTimeout);
                if (element._currentTooltip) {
                    element._currentTooltip.remove();
                    element._currentTooltip = null;
                }
                element.removeEventListener('mouseleave', hideHandler);
            };
            element.addEventListener('mouseleave', hideHandler);
        };
        this.formatDate = (dateString) => {
            if (!dateString)
                return "";
            const date = new Date(dateString);
            return date.toLocaleDateString('ru-RU');
        };
        this.formatDateTime = (dateString) => {
            if (!dateString)
                return "";
            const date = new Date(dateString);
            return date.toLocaleString('ru-RU', {
                year: 'numeric',
                month: 'numeric',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
        };
        this.calculateColor = (color) => {
            switch (color) {
                case Color.NONE:
                    return 'var(--default-color, #f1f1f1)';
                case Color.RED:
                    return 'var(--critical-color, #ef4444)';
                case Color.GREEN:
                    return 'var(--success-color, #10b981)';
                case Color.YELLOW:
                    return 'var(--warning-color, #f59e0b)';
                case Color.BLUE:
                    return 'var(--info-color, #3b82f6)';
            }
        };
        this.lockScreen = (message = "Загрузка...") => {
            const overlay = $(`<div class="lock-overlay">${message}</div>`);
            $(document.body).addClass('locked').append(overlay);
            return () => {
                overlay.remove();
                $(document.body).removeClass('locked');
            };
        };
        this.rowContainer = rowContainer;
        this.itemsPerPage = itemsPerPage;
        this.visibleRow = visibleRow;
        this.init(...initCallbacks);
    }
    init(...callbacks) {
        $(() => {
            this.createHandler('mouseenter', '.tooltip-trigger', this.showToolTip.bind(this), true);
            $(this.rowContainer).on('scroll', this.onScroll.bind(this));
            this.createNotificationContainer();
            this.initializeHandlers();
            callbacks.forEach(callback => callback());
        });
    }
    initializeHandlers() {
        this.handlers.forEach(({ event, selector, handler }) => {
            $(document).on(event, selector, handler);
        });
    }
    deleteRow(rowIndex) {
        const $row = $(`#${rowIndex}`);
        $row.fadeOut(300, () => {
            $row.remove();
            this.localCache.delete(rowIndex);
        });
    }
    displayPage(url, param, ...callbacks) {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.currentPage > 1) {
                param = Object.assign(Object.assign({}, param), { page: this.currentPage });
            }
            const request = yield this.requestToApi(url, 'GET', param);
            const visibleItems = request.data.slice(0, this.visibleRow);
            const hiddenItems = request.data.slice(this.visibleRow);
            visibleItems.forEach(item => {
                this.localCache.set(item.id, item);
                const row = this.createRow(item);
                this.rowContainer.append(row);
            });
            hiddenItems.forEach(item => {
                this.localCache.set(item.id, item);
                const row = this.createRow(item).hide();
                this.rowContainer.append(row);
            });
            callbacks.forEach(callback => callback === null || callback === void 0 ? void 0 : callback(request.data, request.count));
        });
    }
    ;
    print(param) {
        return __awaiter(this, void 0, void 0, function* () {
            if (!this.reports.length)
                return this.createNotification("Нет доступных для печати отчетов", NotificationType.INFO);
            const dialogId = 'printDialog';
            $(`#${dialogId}`).remove();
            const $dialog = $(`
        <dialog id="${dialogId}" class="print-dialog">
            <div class="print-content">
                <h3>Выберите отчёт и формат</h3>
                <select id="reportSelect" class="print-select">
                    ${this.reports.map(r => `<option value="${r.api}">${r.name}</option>`).join('')}
                </select>
                <div class="format-block">
                    <div class="format-toggle">
                        <button type="button" class="format-btn active" data-format="PDF">PDF</button>
                        <button type="button" class="format-btn" data-format="XLSX">XLSX</button>
                    </div>
                </div>
                <div class="print-buttons">
                    <button id="printCancel">Отмена</button>
                    <button id="printOk">Печать</button>
                </div>
            </div>
        </dialog>
    `);
            let format = "PDF";
            $dialog.find('.format-btn').off('click').on('click', function () {
                $dialog.find('.format-btn').removeClass('active');
                $(this).addClass('active');
                format = $(this).data('format');
            });
            $('body').append($dialog);
            this.dialog.open(dialogId);
            return new Promise((resolve) => {
                $('#printCancel').on('click', () => {
                    this.dialog.close(dialogId);
                    $dialog.remove();
                    resolve();
                });
                $('#printOk').on('click', () => __awaiter(this, void 0, void 0, function* () {
                    const api = $('#reportSelect').val();
                    const report = this.reports.find(r => r.api === api);
                    this.dialog.close(dialogId);
                    $dialog.remove();
                    const unlock = this.lockScreen('Формирование отчета');
                    try {
                        if (report.function) {
                            yield report.function(format);
                            resolve();
                            return;
                        }
                        const params = `?format=${format}` + (report.params ? `&${new URLSearchParams(report.params).toString()}` : '');
                        yield this.downloadFile(report.api, params);
                    }
                    catch (e) {
                        this.createNotification('Ошибка при печати', NotificationType.ERROR);
                    }
                    finally {
                        unlock();
                        resolve();
                    }
                }));
            });
        });
    }
}
