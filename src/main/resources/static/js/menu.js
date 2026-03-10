let chartInstance;
let customTooltip = null;

document.addEventListener('DOMContentLoaded', function () {
    const graf = document.getElementById("graphButton");
    if (!graf) return;

    customTooltip = document.createElement('div');
    customTooltip.id = 'chartCustomTooltip';
    customTooltip.style.cssText = `
        position: absolute;
        display: none;
        background: white;
        border: 1px solid #ccc;
        border-radius: 6px;
        padding: 15px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 1000;
        max-width: 400px;
        min-width: 250px;
        font-family: Arial, sans-serif;
        pointer-events: auto;
    `;
    document.body.appendChild(customTooltip);

    graf.addEventListener("click", function () {
        const titleElement = document.getElementById('chartTitle');
        const canvasElement = document.getElementById('myChart');

        if (!canvasElement.style.display || canvasElement.style.display === "none") {
            const ctx = canvasElement.getContext('2d');

            const labels = [];
            const today = new Date();

            for (let i = dailyCounts.length - 1; i >= 0; i--) {
                const date = new Date(today);
                date.setDate(today.getDate() - i);
                labels.push(date.toLocaleDateString('ru-RU'));
            }

            chartInstance = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: 'ОГК',
                            backgroundColor: 'rgba(0, 0, 0, 1)',
                            borderColor: 'rgba(0, 0, 0, 1)',
                            data: dailyCountConstructor,
                            fill: true
                        },
                        {
                            label: 'ОГТ',
                            backgroundColor: 'rgba(0, 0, 255, 1)',
                            borderColor: 'rgba(0, 0, 255, 1)',
                            data: dailyCountTechnologist,
                            fill: true
                        },
                        {
                            label: 'ОТК',
                            backgroundColor: 'rgba(128, 0, 128, 1)',
                            borderColor: 'rgba(128, 0, 128, 1)',
                            data: dailyCountOtk,
                            fill: true
                        }
                    ]
                },
                options: {
                    responsive: true,
                    onClick: function(event, chartElements) {
                        if (chartElements && chartElements.length > 0) {
                            const element = chartElements[0];
                            showCustomTooltip(event, element);
                        } else {
                            if (customTooltip.style.display === 'block') {
                                hideCustomTooltip();
                            }
                        }
                    },
                    plugins: {
                        legend: {
                            display: true,
                            position: 'right',
                        },
                        tooltip: {
                            enabled: false
                        }
                    },
                },
            });

            const canvasRect = canvasElement.getBoundingClientRect();

            function showCustomTooltip(event, element) {
                const datasetIndex = element.datasetIndex;
                const dataIndex = element.index;
                const datasetLabel = chartInstance.data.datasets[datasetIndex].label;
                const date = labels[dataIndex];
                const count = chartInstance.data.datasets[datasetIndex].data[dataIndex];

                let applications = [];

                if (datasetIndex === 0) {
                    applications = dailyApplicationsConstructor[dataIndex] || [];
                } else if (datasetIndex === 1) {
                    applications = dailyApplicationsTechnologist[dataIndex] || [];
                } else if (datasetIndex === 2) {
                    applications = dailyApplicationsOtk[dataIndex] || [];
                }

                let tooltipContent = `
                    <div style="margin-bottom: 10px;">
                        <h4 style="margin: 0 0 8px 0; color: #333;">${datasetLabel}</h4>
                        <div style="color: #666; font-size: 14px; margin-bottom: 12px;">
                            Дата: ${date}<br>
                            Заявок: ${count}
                        </div>
                `;

                if (applications.length > 0) {
                    tooltipContent += `
                        <div style="font-weight: bold; margin-bottom: 8px; color: #444;">
                            Список заявок:
                        </div>
                        <div style="max-height: 200px; overflow-y: auto;">
                    `;

                    applications.forEach(app => {
                        const url = `/view/${app.number}`;
                        let statusClass = '';
                        if (app.status === 'Новый') {
                            statusClass = 'status-new';
                        } else if (app.status === 'Закрыт') {
                            statusClass = 'status-closed';
                        } else if (app.status === 'Забракована') {
                            statusClass = 'status-rejected';
                        } else if (app.status === 'В работе') {
                            statusClass = 'status-work'
                        }
                        tooltipContent += `
                            <a href="${url}" target="_blank" class="tooltip-link ${statusClass}">
                                <strong>№${app.number}</strong>
                                ${app.title ? `<span>${app.title}</span>` : ''}
                            </a>
                        `;
                    });

                    tooltipContent += `</div>`;
                } else {
                    tooltipContent += `
                        <div style="padding: 10px; background: #f9f9f9; border-radius: 4px; color: #888; text-align: center;">
                            Нет заявок
                        </div>
                    `;
                }

                tooltipContent += `</div>`;

                const x = event.native.clientX;
                const y = event.native.clientY;

                customTooltip.innerHTML = tooltipContent;
                customTooltip.style.display = 'block';

                const tooltipWidth = customTooltip.offsetWidth;
                const tooltipHeight = customTooltip.offsetHeight;
                const windowWidth = window.innerWidth;
                const windowHeight = window.innerHeight;

                let left = x + 15;
                let top = y + 15;

                if (left + tooltipWidth > windowWidth) {
                    left = x - tooltipWidth - 15;
                }

                if (top + tooltipHeight > windowHeight) {
                    top = y - tooltipHeight - 15;
                }

                customTooltip.style.left = left + 'px';
                customTooltip.style.top = top + 'px';
            }

            function hideCustomTooltip() {
                customTooltip.style.display = 'none';
            }

            canvasElement.style.display = 'block';
            titleElement.style.display = 'block';
            this.textContent = 'Скрыть график';
        } else {
            chartInstance.destroy();
            canvasElement.style.display = 'none';
            titleElement.style.display = 'none';
            this.textContent = 'Показать график';
        }
    });
});