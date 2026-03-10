interface FileDTO {
    name: string;
    data: [];
}

interface ReportItem {
    api: string;
    name: string;
    params: any;
    function?: Function;
}

interface RequestDataDTO {
    data: any;
    count: number
}

enum NotificationType {
    SUCCESS = 'success',
    ERROR = 'error',
    WARNING = 'warning',
    INFO = 'info'
}

enum Color {
    NONE = 'NONE',
    RED = 'RED',
    GREEN = 'GREEN',
    YELLOW = "YELLOW",
    GREY = 'GREY',
    BLUE = 'BLUE'
}

abstract class Base {
    private locks = new Map<string, boolean>();
    private handlers: { event: string, selector: string, handler: Function }[] = [];
    public selectedRows = new Set<string | number>();
    public localCache = new Map<string | number, object>();
    public readonly itemsPerPage: number;
    private readonly visibleRow: number;
    public currentPage: number = 1;
    public saveMassive: object = {};
    public reports: ReportItem[];

    public readonly rowContainer: any;

    protected cache: CacheBormash = new CacheBormashImpl();
    protected dialog: Dialog = new DialogImpl();

    protected constructor(rowContainer: any,
                          itemsPerPage: number = Infinity,
                          visibleRow = Infinity,
                          ...initCallbacks: Function[]) {
        this.rowContainer = rowContainer;
        this.itemsPerPage = itemsPerPage;
        this.visibleRow = visibleRow;
        this.init(...initCallbacks);
    }

    private init(...callbacks: Function[]) {
        $(() => {
            this.createHandler('mouseenter', '.tooltip-trigger', this.showToolTip.bind(this), true);
            $(this.rowContainer).on('scroll', this.onScroll.bind(this));
            this.createNotificationContainer();
            this.initializeHandlers();

            callbacks.forEach(callback => callback());
        });
    }

    private initializeHandlers(): void {
        this.handlers.forEach(({event, selector, handler}) => {
            $(document).on(event, selector, handler);
        });
    }

    //Блокировка параллельного выполнения
    private lock = (fn: Function) => async (...args: any[]): Promise<void> => {
        const key = fn.name;
        if (this.locks.get(key)) return;

        this.locks.set(key, true);
        try {
            return await fn(...args);
        } finally {
            this.locks.set(key, false);
        }
    };

    public readonly createHandler = (event: string, selector: string, handler: Function, locked: boolean = false): void => {
        this.handlers.push({
            event,
            selector,
            handler: locked ? this.lock(handler) : handler,
        });
    };

    public readonly createNotificationContainer = () => {
        const notificationsContainer = $(`<div id="notifications-container" popover="manual"></div>`);
        $('body').append(notificationsContainer);
    }

    //Всегда должен возвращать jquery объект в виде any
    public abstract createRow(item: any): any;

    //Обработчик при скролле в rowContainer
    public abstract onScroll(): void;

    public readonly updateRow = (item: any, rowIndex: string | number): void => {
        const $oldRow = $(`[data-index="${rowIndex}"]`);
        const $newRow = this.createRow(item).hide();

        $oldRow.fadeOut(100, () => {
            $oldRow.replaceWith($newRow);
            $newRow.fadeIn(280);
            this.localCache.set(item.id, item);
        });
    };

    public readonly switchVisibilityRow = (rowIndex: string | number, hide: boolean): void => {
        $(`[data-index="${rowIndex}"]`)[hide ? 'fadeOut' : 'fadeIn'](300);
    };

    public deleteRow (rowIndex: string | number): void {
        const $row = $(`#${rowIndex}`);
        $row.fadeOut(300, () => {
            $row.remove();
            this.localCache.delete(rowIndex);
        });
    }

    public async displayPage  (url: string, param?: object, ...callbacks: Function[]): Promise<void> {
        if (this.currentPage > 1) {
            param = {...param, page: this.currentPage};
        }
        const request: RequestDataDTO = await this.requestToApi(url, 'GET', param);

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

        callbacks.forEach(callback => callback?.(request.data, request.count));
    };

    public readonly save = async (url: string, ...items: any[]): Promise<any> => {
        const results = await Promise.all(items.map(item => {
            const id: number = item.id;
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
    }

    public readonly requestToApi = async (url: string, type: string, param?: object | FormData): Promise<any> => {
        return await $.ajax({
            url: url,
            method: type,
            contentType: param instanceof FormData ? false : 'application/json',
            processData: !(param instanceof FormData),
            data: param instanceof FormData ? param : JSON.stringify(param)
        }).catch((xhr) => {
            const errorResponse: ErrorResponse = xhr.responseJSON;
            this.createNotification(errorResponse.message, errorResponse.notificationType);
        });
    }

    public async print(param?: any): Promise<void> {
        if (!this.reports.length) return this.createNotification("Нет доступных для печати отчетов", NotificationType.INFO);

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
            format = $(this).data('format') as string;
        });

        $('body').append($dialog);
        this.dialog.open(dialogId);

        return new Promise<void>((resolve) => {
            $('#printCancel').on('click', () => {
                this.dialog.close(dialogId);
                $dialog.remove();
                resolve();
            });

            $('#printOk').on('click', async () => {
                const api = $('#reportSelect').val() as string;
                const report = this.reports.find(r => r.api === api);
                this.dialog.close(dialogId);
                $dialog.remove();

                const unlock = this.lockScreen('Формирование отчета');
                try {
                    if (report.function) {
                        await report.function(format);
                        resolve();
                        return;
                    }
                    const params = `?format=${format}` + (report.params ? `&${new URLSearchParams(report.params).toString()}` : '');
                    await this.downloadFile(report.api, params);
                } catch (e) {
                    this.createNotification('Ошибка при печати', NotificationType.ERROR);
                } finally {
                    unlock()
                    resolve();
                }
            });
        });
    }

    //Скачивает все файлы с api
    public readonly downloadFile = async (url: string, params?: any): Promise<void> => {
        try {
            url = url + (params ? `?${new URLSearchParams(params).toString()}` : '');
            const response = await this.requestToApi(url, 'GET') as FileDTO | FileDTO[];

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

                if (files.length > 1) await new Promise(resolve => setTimeout(resolve, 1250));
            }
        } catch (error) {
            this.createNotification('Ошибка при скачивании файла', NotificationType.ERROR);
            console.error(error);
        }
    }

    public readonly createEntity = (url: string, dto?: any): any => {
        return this.requestToApi(url, 'POST', dto);
    }

    public readonly deleteEntity = (url: string): Promise<void> => {
        return this.requestToApi(`${url}`, 'DELETE');
    }

    //Создание уведомления в левом верхнем углу
    public readonly createNotification = (
        message: string, type: NotificationType,
        params?: any, error?: Error): void => {
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
            if (error) console.error(error);
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
        } catch (error) {
            console.error(error);
        }
    };

    //Диалог с подтверждением действия
    public readonly createConfirmationDialog = this.lock((message: string, params?: any): Promise<boolean> => {
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
            } else {
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
    public readonly createContextMenu = (items: { label: string, idAction: string, action: () => void }[],
                                         x: number,
                                         y: number): void => {
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
    }

    private readonly showToolTip = (event: Event) => {
        const element = event.currentTarget as HTMLElement;

        const tooltipTimeout = setTimeout(() => {
            const description = element.getAttribute('data-description');
            if (!description) return;

            const tooltip = document.createElement('div');
            tooltip.className = 'custom-tooltip';
            tooltip.textContent = description;
            document.body.appendChild(tooltip);

            const rect = element.getBoundingClientRect();
            tooltip.style.position = 'absolute';
            tooltip.style.left = `${rect.left + window.pageXOffset}px`;
            tooltip.style.top = `${rect.bottom + window.pageYOffset + 5}px`;

            (element as any)._currentTooltip = tooltip;
        }, 450);

        (element as any)._tooltipTimeout = tooltipTimeout;

        const hideHandler = () => {
            clearTimeout(tooltipTimeout);
            if ((element as any)._currentTooltip) {
                (element as any)._currentTooltip.remove();
                (element as any)._currentTooltip = null;
            }
            element.removeEventListener('mouseleave', hideHandler);
        };

        element.addEventListener('mouseleave', hideHandler);
    };

    public readonly formatDate = (dateString: string): string => {
        if (!dateString) return "";
        const date = new Date(dateString);
        return date.toLocaleDateString('ru-RU');
    }

    public readonly formatDateTime = (dateString: string): string => {
        if (!dateString) return "";
        const date = new Date(dateString);
        return date.toLocaleString('ru-RU', {
            year: 'numeric',
            month: 'numeric',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    public readonly calculateColor = (color: Color): string => {
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
    }

    public readonly lockScreen = (message: string = "Загрузка..."): (() => void) => {
        const overlay = $(`<div class="lock-overlay">${message}</div>`);
        $(document.body).addClass('locked').append(overlay);

        return () => {
            overlay.remove();
            $(document.body).removeClass('locked');
        };
    };

}