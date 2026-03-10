var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
// @ts-ignore
class CacheBormashImpl {
    constructor() {
        this.endpoints = {
            employee: '/api/employees',
            subDivision: '/api/sub-divisions'
        };
    }
    get(key) {
        return __awaiter(this, void 0, void 0, function* () {
            const cached = sessionStorage.getItem(key);
            if (cached) {
                return JSON.parse(cached);
            }
            const endpoint = this.endpoints[key];
            if (!endpoint) {
                throw new Error(`Такого api нет: ${key}`);
            }
            const response = yield fetch(endpoint);
            if (!response.ok) {
                throw new Error(`Возникла ошибка сервера: ${response.status}`);
            }
            const data = yield response.json();
            this.set(key, data);
            return data;
        });
    }
    set(key, data) {
        sessionStorage.setItem(key, JSON.stringify(data));
        return this;
    }
}
