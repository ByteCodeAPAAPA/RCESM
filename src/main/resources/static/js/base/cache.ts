interface CacheBormash {
    endpoints: { [key: string]: string };
    get<T = any>(key: string): Promise<T>;
    set(key: string, data: any): this;
}

// @ts-ignore
class CacheBormashImpl implements CacheBormash {
    endpoints: { [key: string]: string } = {
        employee: '/api/employees',
        subDivision: '/api/sub-divisions'
    };

    async get<T>(key: string): Promise<T> {
        const cached = sessionStorage.getItem(key);
        if (cached) {
            return JSON.parse(cached) as T;
        }

        const endpoint = this.endpoints[key];
        if (!endpoint) {
            throw new Error(`Такого api нет: ${key}`);
        }

        const response = await fetch(endpoint);
        if (!response.ok) {
            throw new Error(`Возникла ошибка сервера: ${response.status}`);
        }

        const data: T = await response.json();
        this.set(key, data);
        return data;
    }

    set(key: string, data: any): this {
        sessionStorage.setItem(key, JSON.stringify(data));
        return this;
    }
}