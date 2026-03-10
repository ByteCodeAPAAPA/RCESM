class DialogImpl {
    constructor() {
        this.activeDialogs = new Map();
        this.BASE_Z_INDEX = 999;
        this.BACKDROP_Z_INDEX = 998;
        const rootStyle = getComputedStyle(document.documentElement);
        const cssZIndex = rootStyle.getPropertyValue('--dialog-z-index');
        this.currentZIndex = cssZIndex ? parseInt(cssZIndex) : this.BASE_Z_INDEX;
    }
    open(dialogId, options = {}) {
        const dialogElement = document.getElementById(dialogId);
        if (!dialogElement) {
            console.error(`Диалог с id ${dialogId} не найден`);
            return;
        }
        if (this.isOpen(dialogId)) {
            return;
        }
        dialogElement.style.zIndex = this.currentZIndex.toString();
        if (options.clearFields !== false) {
            this.clearDialog(dialogId);
        }
        if (this.activeDialogs.size === 0) {
            document.body.classList.add('no-scroll');
            this.addBackdrop();
        }
        else {
            this.activeDialogs.forEach((_, dialogId) => {
                const el = document.getElementById(dialogId);
                if (el)
                    el.style.display = 'none';
            });
        }
        dialogElement.showModal();
        this.activeDialogs.set(dialogId, this.currentZIndex);
        this.currentZIndex++;
        document.documentElement.style.setProperty('--dialog-z-index', this.currentZIndex.toString());
        this.setupCloseHandlers(dialogId, options.onClose);
        if (options.onOpen) {
            options.onOpen();
        }
    }
    close(dialogId) {
        const dialogElement = document.getElementById(dialogId);
        if (dialogElement) {
            if (dialogElement.close) {
                dialogElement.close();
            }
            else {
                dialogElement.style.display = 'none';
                dialogElement.removeAttribute('open');
            }
            this.activeDialogs.delete(dialogId);
            if (this.activeDialogs.size === 0) {
                document.body.classList.remove('no-scroll');
                this.removeBackdrop();
                const rootStyle = getComputedStyle(document.documentElement);
                const cssZIndex = rootStyle.getPropertyValue('--dialog-z-index');
                this.currentZIndex = cssZIndex ? parseInt(cssZIndex) : this.BASE_Z_INDEX;
            }
            else {
                const maxZIndex = Math.max(...Array.from(this.activeDialogs.values()));
                this.currentZIndex = maxZIndex + 1;
                document.documentElement.style.setProperty('--dialog-z-index', this.currentZIndex.toString());
                let topDialogId = '';
                this.activeDialogs.forEach((zIndex, id) => {
                    if (zIndex === maxZIndex)
                        topDialogId = id;
                });
                if (topDialogId) {
                    const topDialog = document.getElementById(topDialogId);
                    if (topDialog) {
                        topDialog.style.removeProperty('display');
                    }
                }
            }
        }
    }
    clearDialog(dialogId) {
        const dialog = document.getElementById(dialogId);
        if (!dialog)
            return;
        const inputs = dialog.querySelectorAll('input[name], textarea[name], select[name]');
        inputs.forEach((input) => {
            var _a;
            const htmlInput = input;
            if (htmlInput.type === 'file') {
                const newInput = htmlInput.cloneNode(false);
                (_a = htmlInput.parentNode) === null || _a === void 0 ? void 0 : _a.replaceChild(newInput, htmlInput);
            }
            else {
                htmlInput.value = '';
            }
        });
        const fileList = dialog.querySelector('.file-list');
        if (fileList) {
            fileList.innerHTML = '';
        }
        const localCache = window.localCache;
        if (localCache) {
            localCache.delete('validFileMap');
            localCache.delete('imagesMap');
        }
    }
    setupCloseHandlers(dialogId, onCloseCallback) {
        const dialogElement = document.getElementById(dialogId);
        if (!dialogElement)
            return;
        const handleBackdropClick = (e) => {
            const rect = dialogElement.getBoundingClientRect();
            const isInDialog = (rect.top <= e.clientY &&
                e.clientY <= rect.top + rect.height &&
                rect.left <= e.clientX &&
                e.clientX <= rect.left + rect.width);
            if (!isInDialog) {
                this.close(dialogId);
                if (onCloseCallback)
                    onCloseCallback();
            }
        };
        const handleKeyDown = (e) => {
            const maxZIndex = Math.max(...Array.from(this.activeDialogs.values()));
            const currentDialogZIndex = this.activeDialogs.get(dialogId);
            if (e.key === 'Escape' &&
                currentDialogZIndex === maxZIndex &&
                this.isOpen(dialogId)) {
                e.preventDefault();
                this.close(dialogId);
                if (onCloseCallback)
                    onCloseCallback();
            }
        };
        dialogElement.removeEventListener('click', handleBackdropClick);
        document.removeEventListener('keydown', handleKeyDown);
        if (!dialogElement.showModal) {
            dialogElement.addEventListener('click', handleBackdropClick);
        }
        document.addEventListener('keydown', handleKeyDown);
        const cancelBtn = dialogElement.querySelector('#cancelButton');
        if (cancelBtn) {
            const handleCancelClick = () => {
                this.close(dialogId);
                if (onCloseCallback)
                    onCloseCallback();
            };
            cancelBtn.removeEventListener('click', handleCancelClick);
            cancelBtn.addEventListener('click', handleCancelClick);
        }
    }
    addBackdrop() {
        if (!document.querySelector('.backdrop')) {
            const backdrop = document.createElement('div');
            backdrop.className = 'backdrop';
            document.body.appendChild(backdrop);
            backdrop.addEventListener('click', () => {
                this.closeAll();
            });
        }
    }
    removeBackdrop() {
        const backdrop = document.querySelector('.backdrop');
        if (backdrop) {
            backdrop.remove();
        }
    }
    isOpen(dialogId) {
        const dialogElement = document.getElementById(dialogId);
        if (!dialogElement)
            return false;
        if (dialogElement.open !== undefined) {
            return dialogElement.open;
        }
        if (dialogElement.hasAttribute('open')) {
            return true;
        }
        return this.activeDialogs.has(dialogId);
    }
    closeAll() {
        const dialogIds = Array.from(this.activeDialogs.keys());
        dialogIds.forEach(dialogId => {
            const dialogElement = document.getElementById(dialogId);
            if (dialogElement) {
                if (dialogElement.close) {
                    dialogElement.close();
                }
                else {
                    dialogElement.style.display = 'none';
                    dialogElement.removeAttribute('open');
                }
            }
        });
        this.activeDialogs.clear();
        document.body.classList.remove('no-scroll');
        this.removeBackdrop();
        const rootStyle = getComputedStyle(document.documentElement);
        const cssZIndex = rootStyle.getPropertyValue('--dialog-z-index');
        this.currentZIndex = cssZIndex ? parseInt(cssZIndex) : this.BASE_Z_INDEX;
    }
}
