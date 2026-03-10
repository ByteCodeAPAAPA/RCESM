let currentPage = 1; //Текущая страница
const itemsPerPage = 100; //Начальное кол-во строк на странице
let loadLines = 0; //Загружено строк
let firstLoad = true; //Первая загрузка
let customerOrderId;

//Обработчик ресайза колонок
$('.table-header-resizer').on('mousedown', function (e) {
    e.preventDefault();

    const $resizer = $(this);
    const $parentHeader = $resizer.closest('[data-name]');
    const dataName = $parentHeader.data('name');
    const startX = e.clientX;
    const startWidth = parseFloat($parentHeader.css('width'));

    const minWidthValue = getComputedStyle(document.documentElement)
        .getPropertyValue(`--${'default-' + dataName}`)
        .trim();

    const minWidth = parseFloat(minWidthValue) * 16;

    function doResize(e) {
        let newWidth = startWidth + (e.clientX - startX);
        newWidth = Math.max(minWidth, newWidth);

        $(':root').css(`--${dataName}`, (newWidth / 16) + 'rem');
    }

    function stopResize() {
        $(window).off('mousemove', doResize)
            .off('mouseup', stopResize);
    }

    $(window).on('mousemove', doResize)
        .on('mouseup', stopResize);
});
//Обработчик раскрытия вложенных строк
$(document).on('click', '.hamburger', function (e) {
    if ($(e.target).is('input')) {
        return;
    }
    e.preventDefault();

    const $checkbox = $(this).find('input[type="checkbox"]');
    const $currentRow = $(this).closest('.row');
    const $lineContainer = $currentRow.find('.line-container');
    const $thirdLine = $lineContainer.children('.third-line');
    const spinner = `
            <div class="dot-spinner">
                <div class="dot-spinner__dot"></div>
                <div class="dot-spinner__dot"></div>
                <div class="dot-spinner__dot"></div>
                <div class="dot-spinner__dot"></div>
                <div class="dot-spinner__dot"></div>
                <div class="dot-spinner__dot"></div>
                <div class="dot-spinner__dot"></div>
                <div class="dot-spinner__dot"></div>
            </div> `;

    (async () => {
        if (!$checkbox.prop('checked') && !$currentRow.hasClass('cached')) {
            $currentRow.children('.hamburger').addClass('none')
            $currentRow.prepend(spinner);
            await loadChild($currentRow);
            $currentRow.children('.dot-spinner').remove();
            $currentRow.children('.hamburger').removeClass('none');
        }
        const $innerRows = $currentRow.closest('.table-rows-items').children('.inner-rows')
            .children('.table-rows-items').children('.row');

        if ($currentRow.data('level') === 0) {
            $lineContainer.slideToggle();
        }

        $checkbox.prop('checked', !$checkbox.prop('checked'));
        $thirdLine.slideToggle(100);
        if ($currentRow.data('level') > 0 && !$lineContainer.hasClass('has-children')) {
            $lineContainer.addClass('has-children');
        } else if ($lineContainer.has('.has-children')) {
            $lineContainer.removeClass('has-children');
        }
        $innerRows.closest('.table-rows-items').closest('.inner-rows').slideToggle(1100);
    })();
});
//Обработчик загрузки доп строк
$(document).on('click', '.load-more', async function (e) {
    currentPage++;
    await displayPage(currentPage, customerOrderId);
});
//Динамическая загрузка/удаление строк при скролле(не работает)
$('main').on('scroll', async function () {
    const rows = $(this);
    const rowHeight = 25;
    const visibleRows = Math.ceil(rows.height() / rowHeight);
    const buffer = 5; // Буферные строки сверху и снизу

    function renderRows() {
        const scrollTop = rows.scrollTop();
        const firstVisible = Math.floor(scrollTop / rowHeight) - buffer;
        const lastVisible = firstVisible + visibleRows + 2 * buffer;

        rows.empty();

        const topPlaceholder = $('<div>').addClass('placeholder');
        topPlaceholder.height(Math.max(0, firstVisible) * rowHeight);
        rows.append(topPlaceholder);

        // Добавляем видимые строки
        for (let i = Math.max(0, firstVisible); i <= Math.min(loadLines - 1, lastVisible); i++) {
            const row = $('<div>').addClass('row').text(data[i]);
            rows.append(row);
        }

        // Добавляем нижний плейсхолдер
        const bottomPlaceholder = $('<div>').addClass('placeholder');
        bottomPlaceholder.height(Math.max(0, loadLines - lastVisible - 1) * rowHeight);
        rows.append(bottomPlaceholder);
    }
});
//Обработчик нажатия на кнопку печать отчета
$('.print-report').click(function () {
    const $btn = $(this).prop('disabled', true);
    const customerOrderId = $('#customerOrderId').data('id');
    $('<a>', {href: `/report/print?customerOrderId=${customerOrderId}`, download: ''})
        .appendTo('body')[0].click()
        .remove();
    $btn.prop('disabled', false);
});
//Обработчик нажатия на кнопку сформировать
$('.build-form').click(async function () {
    const customerOrderId = 0;
    await displayPage(1, customerOrderId);
});
//Обработчики работы с модальным окном выбора ЗК
$('.change-order-button').click(async function () {
    const customerOrders = await $.get('/spm-api/getBurningAndAllCustomerOrder');
    const primaryOrders = customerOrders.burning;
    const allCustomerOrder = customerOrders.all;
    let selectedCustomerOrder = null; // Храним весь объект заказа
    const rowContainer = $('.dialog-content-rows');
    const searchInput = $('.choice-field input');

    function renderOrders(orders) {
        rowContainer.empty();
        for (const co of orders) {
            rowContainer.append(`
                <div class="dialog-content-rows-row" data-id="${co.id}">
                    <div class="content-row-column col-25">${co.name}</div>
                    <div class="content-row-column col-150">${formatDate(co.planDate)}</div>
                    <div class="content-row-column col-150">${formatDate(co.contractDate)}</div>
                    <div class="content-row-column col-25">${co.site}</div>
                </div>`
            );
        }
    }

    renderOrders(primaryOrders);

    document.getElementById('customerOrderDialog').showModal();

    searchInput.off('input').on('input', function () {
        const searchText = $(this).val().toLowerCase().trim();
        const filteredOrders = searchText === ''
            ? primaryOrders
            : allCustomerOrder.filter(co => co.name.toLowerCase().includes(searchText));
        renderOrders(filteredOrders);
    });

    // Обработчик выбора строки
    rowContainer.off('click').on('click', '.dialog-content-rows-row', function() {
        const orderId = $(this).data('id');
        const ordersArray = searchInput.val() ? allCustomerOrder : primaryOrders;

        selectedCustomerOrder = ordersArray.find(order => order.id === orderId);

        $('.dialog-content-rows-row').removeClass('selected');
        $(this).addClass('selected');
    });

    // Обработчик подтверждения выбора
    $('#changeCustomerOrder').off('click').on('click', async function () {
        if (!selectedCustomerOrder) {
            alert('Выберите заказ из списка');
            return;
        }

        $('#customerOrderId')
            .val(selectedCustomerOrder.name)
            .data('id', selectedCustomerOrder.id);

        document.getElementById('customerOrderDialog').close();

        // Сброс и загрузка данных
        $(".table-rows").empty();
        $('.total-count').empty();
        $('.load-count span:eq(0)').empty();
        $('.load-count span:eq(1)').empty();
        firstLoad = true;
        loadLines = 0;
        customerOrderId = selectedCustomerOrder.id;
        await displayPage(1, selectedCustomerOrder.id);
    });
});

//Создание строки
async function createRow(item, type, parentId, level, hasChildren, isLast, hasNext) {
    const hamburger = `
        <label class="hamburger">
            <input type="checkbox">
            <svg viewBox="0 0 32 32">
                <path class="line line-top-bottom"
                    d="M27 10 13 10C10.8 10 9 8.2 9 6 9 3.5 10.8 2 13 2 15.2 2 17 3.8 17 6L17 26C17 28.2 18.8 30 21 30 23.2 30 25 28.2 25 26 25 23.8 23.2 22 21 22L7 22"></path>
                <path class="line" d="M7 16 27 16"></path>
            </svg>
        </label>`
    let rowsContainer;
    let row;
    if (type === 'pd') {
        rowsContainer = $(".table-rows");
        row = `
            <div class="table-rows-items">
                <div class="row" data-parent-id="#" data-id="${item.jobComponent.id}" data-level="0" data-has-next="${String(hasNext)}" style="font-weight: bold;">
                    ${hasChildren ? hamburger : ""}
                    <div class="row-item" style="width: var(--primarydemand)" data-name="primarydemand">
                        <p>
                            ${item.name}
                            <span class="line-container">
                                <span class="third-line"></span>
                            </span>
                        </p>
                    </div>
                    <div class="row-item" style="width: var(--item)" data-name="item">${item.jobComponent.name}</div>
                    <div class="row-item" style="width: var(--mlm-node)" data-name="mlm-node"></div>
                    <div class="row-item" style="width: var(--description)" data-name="description"></div>
                    <div class="row-item numbers" style="width: var(--qty)" data-name="qty">${item.jobComponent.qty}</div>
                    <div class="row-item numbers" style="width: var(--qty-finished)" data-name="qty-finished">${item.jobComponent.qtyFinished}</div>
                    <div class="row-item numbers" style="width: var(--resourcetime)" data-name="resourcetime"></div>
                    <div class="row-item" style="width: var(--date-start)" data-name="date-start">${formatDate(item.jobComponent.dateStart)}</div>
                    <div class="row-item" style="width: var(--date-end)" data-name="date-end">${formatDate(item.jobComponent.dateEnd)}</div>
                    <div class="row-item" style="width: var(--date-calc-start)" data-name="date-calc-start"></div>
                    <div class="row-item" style="width: var(--date-calc-end)" data-name="date-calc-end"></div>
                </div>
                <div class="inner-rows"></div>
            </div>`;
        rowsContainer.append(row);
    } else {
        rowsContainer = $(`[data-id="${parentId}"]`).closest('.table-rows-items').children('.inner-rows');
        const primaryDemand = $(`[data-id="${parentId}"] .row-item[data-name="primarydemand"] p`).text().trim();
        row = `
            <div class="table-rows-items">
                <div class="row" data-parent-id="${parentId}" data-id="${item.id}" data-level="${level}" data-has-next="${String(hasNext)}" style="vertical-align: ${type === 'jc' ? "super" : "sub"};
                                                                                                                font-size: ${type === 'jc' ? "0.825rem" : "0.750rem"};
                                                                                                                ${type === "jc" ? " text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);" : ""}">
                    ${type === "jc" ? hasChildren ? hamburger : "" : ""}
                    <div class="row-item first-element" style="width: var(--primarydemand); padding-left: ${(1.5 + (level * 0.5)) + 'rem'}" data-name="primarydemand">
                        <p>
                            ${type === "jc" ? item.pdName : primaryDemand}
                            <span class="line-container ${isLast ? 'last-line' : ''}">
                                <span class="third-line"></span>
                                ${isLast && hasNext ? '<span class="second-line"></span>' : ''}
                            </span>
                        </p>
                    </div>
                    <div class="row-item" style="width: var(--item)" data-name="item">${item.name}</div>
                    <div class="row-item" style="width: var(--mlm-node);" data-name="mlm-node">${type === 'jc' ? "" : item.mlmNode}</div>
                    <div class="row-item" style="width: var(--description);" data-name="description">${type === 'jc' ? "" : item.description}</div>
                    <div class="row-item numbers" style="width: var(--qty);" data-name="qty">${item.qty}</div>
                    <div class="row-item numbers" style="width: var(--qty-finished);" data-name="qty-finished">${item.qtyFinished}</div>
                    <div class="row-item numbers" style="width: var(--resourcetime);" data-name="resourcetime">${type === 'jc' ? "" : item.resourceTime}</div>
                    <div class="row-item" style="width: var(--date-start);" data-name="date-start">${formatDate(item.dateStart)}</div>
                    <div class="row-item" style="width: var(--date-end);" data-name="date-end">${formatDate(item.dateEnd)}</div>
                    <div class="row-item" style="width: var(--date-calc-start);" data-name="date-calc-start">${type === 'jc' ? "" : formatDate(item.dateCalcStart)}</div>
                    <div class="row-item" style="width: var(--date-calc-end);" data-name="date-calc-end">${formatDate(item.dateCalcEnd)}</div>
                </div>
                <div class="inner-rows"></div>
            </div>`;
        rowsContainer.append(row);
    }
}

//Загрузка вложенных строк строки
async function loadChild(row) {
    const rowId = row.data('id');
    const parentId = row.data('parent-id');
    const level = row.data('level');
    let hasNext = Boolean(row.data('has-next'));
    const result = await $.get('/spm-api/getChildJobComponentAndJobStepsForJobcomponentId', { jobComponentId: rowId });
    const childJobComponent = result.left;
    const jobSteps = result.right;
    for (jc of childJobComponent) {
        const isLast = (childJobComponent.indexOf(jc) === childJobComponent.length - 1) && (await $.get('spm-api/getJobStepsForJobComponentId', {jobComponentId: rowId})).length === 0;
        hasNext = result.left.indexOf(jc) < childJobComponent.length - 1 ? true : (await $.get('spm-api/getJobStepsForJobComponentId', {jobComponentId: rowId})).length > 0;
        await createRow(jc, 'jc', rowId, level + 1, jc.hasChildOrJobSteps, isLast, hasNext);
    }
    for (js of jobSteps) {
        const isLast = jobSteps.indexOf(js) === jobSteps.length - 1;
        await createRow(js, 'js', rowId, level + 1, false, isLast, hasNext && level > 0);
    }
    $(row).addClass('cached')
}

//Загрузка страниц
async function displayPage(page, customerOrderId) {
    async function loadPrimaryDemands(page) {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: '/spm-api/getPrimaryDemandForCustomerOrderId',
                type: 'GET',
                data: {
                    customerOrderId: customerOrderId,
                    page: page,
                    size: itemsPerPage
                },
                success: function (data, textStatus, jqXHR) {
                    const totalCount = parseInt(jqXHR.getResponseHeader('X-Total-Count'));
                    resolve({
                        data: data,
                        total: totalCount
                    });
                }
            });
        });
    }

    const primaryDemandsAndTotalCount = await loadPrimaryDemands(page);
    const primaryDemands = primaryDemandsAndTotalCount.data;
    const totalCount = primaryDemandsAndTotalCount.total;

    const rowsContainer = $(".table-rows");
    const totalCountHtml = $('.total-count');
    const loadCountHtml = $('.load-count span:eq(0)');

    if (!firstLoad) {
        loadLines = 0;
        loadCountHtml.empty();
        loadCountHtml.append(totalCount);
    } else loadLines += primaryDemands.length;

    if (page === 1 && firstLoad) {
        $('#table-data').empty();
        [...('Всего записей: ' + totalCount)].forEach((c, i) => setTimeout(() => totalCountHtml.append(c), 90 * i));
        [...('Загружено записей: ')].forEach((c, i) => setTimeout(() => loadCountHtml.append(c), 120 * i));
        firstLoad = false;
    }
    rowsContainer.children('.load-more').remove();

    const jobComponentIds = primaryDemands.map(demand => demand.jobComponent.id);
    const hasChildrenFlags = await $.get('/spm-api/jobComponentHasChildOrHasJobSteps', {jobComponentIdList: jobComponentIds})

    for (let i = 0; i < primaryDemands.length; i++) {
        await createRow(primaryDemands[i], 'pd', '#', 0, hasChildrenFlags[i]);
    }

    $('.load-count span:eq(1)').empty();
    setTimeout(() => {
            [...(String(loadLines))].forEach((c, i) => {
                setTimeout(() => {
                    $('.load-count span:eq(1)').append(c);
                }, 120 * i);
            });
        },
        1600);


    if (loadLines < totalCount) {
        rowsContainer.append(`<div class="load-more"><span>Загрузить ещё...</span></div>`)
    }
}

//Форматирование дат
function formatDate(dateString) {
    if (!dateString) return "&nbsp;";
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU');
}


