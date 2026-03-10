window.cache = {
    endpoints: {
        employee: '/api/employees',
        subDivision: '/api/sub-divisions'
    },

    get: async function (key) {
        const cached = sessionStorage.getItem(key);
        if (cached) {
            return Promise.resolve(JSON.parse(cached));
        }

        if (this.endpoints[key]) {
            return await fetch(this.endpoints[key])
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Api запрос завершен с ошибкой: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    this.set(key, data);
                    return data;
                });
        }

        return Promise.reject(new Error(`Такого api нет в кеше: ${key}`));
    },

    set: function(key, data) {
        sessionStorage.setItem(key, JSON.stringify(data));
        return this;
    }
};