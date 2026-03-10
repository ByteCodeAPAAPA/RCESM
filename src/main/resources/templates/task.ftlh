<#--<!DOCTYPE html>-->
<#--<html lang="en">-->
<#--<head>-->
<#--    <meta charset="UTF-8">-->
<#--    <meta name="viewport" content="width=device-width, initial-scale=1.0">-->
<#--    <link rel="stylesheet" href="/css/bootstrap.min.css">-->
<#--    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>-->
<#--    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>-->
<#--    <title>Страница ССЗ</title>-->
<#--</head>-->
<#--<style>-->
<#--    .hidden {-->
<#--        display: none;-->
<#--        text-align: center;-->
<#--        color: green;-->
<#--    }-->
<#--</style>-->
<#--<body>-->
<#--<div>-->
<#--    <h2  class="d-flex justify-content-center">ССЗ на ${dateStart!""}</h2>-->
<#--    <div style="justify-content: left">-->
<#--        <a href="/menu" class="btn btn-outline-secondary d-none d-md-block" style="width: 100px">Перейти в меню</a>-->
<#--    </div>-->
<#--    <table class="table table-striped table-hover table-bordered">-->
<#--        <h3 class="d-flex justify-content-center text-warning mt-4 ">Задач в работе: ${valueTaskListSize}</h3>-->
<#--        <thead>-->
<#--        <tr>-->
<#--            <th>№</th>-->
<#--            <th>ДСЕ/Заказ</th>-->
<#--            <th>Описание</th>-->
<#--            <th>План</th>-->
<#--            <th>Выполнено</th>-->
<#--            <th>Дата начала</th>-->
<#--            <th>Дата завершения</th>-->
<#--            <th>Узел ПЛМ</th>-->
<#--            <th>ПрП</th>-->
<#--            <th>Выполнено</th>        <!-- Тут как я понимаю отметка о выполнении &ndash;&gt;-->
<#--            <th>Действия</th>-->
<#--        </tr>-->
<#--        </thead>-->
<#--        <tbody>-->
<#--        <#list taskList as task>-->
<#--            <tr id="${task.getJobstep().getId()}">-->
<#--                <td>...</td>-->
<#--                <td>${(task.getJobstep().getJobcomponent().getItem().getName())!""}-->
<#--&lt;#&ndash;                    /${(task.getJobstep().getJobcomponent().getPrimarydemand().getCustomerorder().getStrCode())!""}&ndash;&gt;-->
<#--                </td>-->
<#--                <td>${task.getJobstep().getDescription()}</td>-->
<#--                <td>${task.getQtyProduction()}</td>-->
<#--                <td class="finish-cell">${task.getQtyFinished()}</td>-->
<#--                <td>${task.getJobstep().getFormattedDateStart()}</td>-->
<#--                <td>${task.getJobstep().getFormattedDateEnd()}</td>-->
<#--                <td>${task.getJobstep().getMlmNode().getName()}</td>-->
<#--                <td>${task.getJobstep().getJobcomponent().getPrimarydemand().getStormSingleString()}</td>-->
<#--                <td>-->
<#--                    <button type="button" class="btn btn-primary btnOpenModal" data-bs-toggle="modal" data-bs-target="#staticBackdrop" data-id="${task.getId()}">Выполнено</button>-->
<#--                </td>-->
<#--                <td>-->
<#--                    <button type="button" class="btn btn-primary btnInWork"-->
<#--                            data-bs-toggle="modal"-->
<#--                            data-bs-target="#staticWorkBackdrop"-->
<#--                            data-id="${task.getJobstep().getId()}"-->
<#--                            data-workMlmNodeId="${task.getJobstep().getMlmNode().getId()}"-->


<#--                    >Взять в работу</button>-->
<#--                </td>-->
<#--            </tr>-->
<#--        </#list>-->
<#--        </tbody>-->
<#--    </table>-->

<#--    <div style="display: flex; justify-content: center;">-->
<#--        <button class="btn btn-info d-flex justify-content-center" id="watchFinsfQtyId">-->
<#--            Просмотр выполненных задач на участке-->
<#--        </button>-->
<#--    </div>-->

<#--    <h3 class="hidden" id="qtyFinId">Выполненных задач: ${valueFinishedListSize!"0"}</h3>-->
<#--    <table id="tableFinishedId" class="table table-striped table-hover table-bordered">-->
<#--        <thead>-->
<#--        <tr>-->
<#--            <th>№</th>-->
<#--            <th>ДСЕ/Заказ</th>-->
<#--            <th>Описание</th>-->
<#--            <th>План</th>-->
<#--            <th>Выполнено</th>-->
<#--            <th>Дата начала</th>-->
<#--            <th>Дата завершения</th>-->
<#--            <th>Узел ПЛМ</th>-->
<#--            <th>ПрП</th>-->
<#--        </tr>-->
<#--        </thead>-->
<#--        <tbody>-->
<#--        <#list finished as task>-->
<#--            <tr id="${task.getJobstep().getId()}">-->
<#--                <td>...</td>-->
<#--                <td>${(task.getJobstep().getJobcomponent().getItem().getName())!""}/${(task.getJobstep().getId())!""}</td>-->
<#--                <td>${task.getJobstep().getDescription()}</td>-->
<#--                <td>${task.getQtyProduction()}</td>-->
<#--                <td class="finish-cell">${task.getQtyFinished()}</td>-->
<#--                <td>${task.getJobstep().getFormattedDateStart()}</td>-->
<#--                <td>${task.getJobstep().getFormattedDateEnd()}</td>-->
<#--                <td>${task.getJobstep().getMlmNode().getName()}</td>-->
<#--                <td>${task.getJobstep().getJobcomponent().getPrimarydemand().getStormSingleString()}</td>-->
<#--                <td>Выполнено</td>-->
<#--            </tr>-->
<#--        </#list>-->
<#--        </tbody>-->
<#--    </table>-->
<#--</div>-->

<#--<!-- Modal &ndash;&gt;-->
<#--<div class="modal fade" id="staticBackdrop" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">-->
<#--    <div class="modal-dialog">-->
<#--        <div class="modal-content">-->
<#--            <div class="modal-header">-->
<#--                <h5 class="modal-title" id="staticBackdropLabel">Выполнение ССЗ</h5>-->
<#--                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>-->
<#--            </div>-->
<#--            <div class="modal-body">-->
<#--                <div class="mb-3">-->
<#--                    <label for="qtyFin" class="d-flex justify-content-center form-label">Кол-во выполненного</label>-->
<#--                    <input type="number" class="form-control" id="qtyFinishedId" name="qty">-->
<#--                </div>-->
<#--            </div>-->
<#--            <div class="modal-footer">-->
<#--                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>-->
<#--                <button type="submit" class="btn btn-primary" id="saveId">Сохранить</button>-->
<#--            </div>-->
<#--        </div>-->
<#--    </div>-->
<#--</div>-->

<#--<div class="modal fade" id="staticWorkBackdrop" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticWorkBackdropLabel" aria-hidden="true">-->
<#--    <div class="modal-dialog">-->
<#--        <div class="modal-content">-->
<#--            <div class="modal-header">-->
<#--                <h5 class="modal-title" id="staticWorkBackdropLabel">Выполнение ССЗ</h5>-->
<#--                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>-->
<#--            </div>-->
<#--&lt;#&ndash;            <div class="modal-body">&ndash;&gt;-->
<#--&lt;#&ndash;                <div class="mb-3">&ndash;&gt;-->
<#--&lt;#&ndash;                    <label for="qtyWorkFin" class="d-flex justify-content-center form-label">Кол-во которое взято в работу</label>&ndash;&gt;-->
<#--&lt;#&ndash;                    <input type="number" class="form-control" id="qtyWorkId" name="qtyWork">&ndash;&gt;-->
<#--&lt;#&ndash;                </div>&ndash;&gt;-->
<#--&lt;#&ndash;            </div>&ndash;&gt;-->
<#--            <div class="modal-footer">-->
<#--                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>-->
<#--                <button type="submit" class="btn btn-primary" id="saveWorkId">Сохранить</button>-->
<#--            </div>-->
<#--        </div>-->
<#--    </div>-->
<#--</div>-->

<#--<div class="modal fade" id="errorBackdrop" tabindex="-1" aria-labelledby="errorBackdropLabel" aria-hidden="true">-->
<#--    <div class="modal-dialog">-->
<#--        <div class="modal-content">-->
<#--            <div class="modal-header">-->
<#--                <h5 class="modal-title" id="errorBackdropLabel">Ошибка</h5>-->
<#--                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Закрыть"></button>-->
<#--            </div>-->
<#--            <div class="modal-body">-->
<#--                <div id="errorMessage" style="color:red;"></div>-->
<#--            </div>-->
<#--            <div class="modal-footer">-->
<#--                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>-->
<#--            </div>-->
<#--        </div>-->
<#--    </div>-->
<#--</div>-->

<#--<script>-->
<#--    document.addEventListener("DOMContentLoaded", function() {-->
<#--        const elementShow = document.getElementById("tableFinishedId");-->
<#--        const infoElements = document.getElementsByClassName('hidden');-->
<#--        elementShow.style.display = "none";-->
<#--        const btn = document.getElementById('watchFinsfQtyId');-->
<#--        const info = infoElements[0];-->

<#--        btn.addEventListener("click",function () {-->
<#--            if (elementShow.style.display === "none" && info.style.display === "none") {-->
<#--                elementShow.style.display = "block";-->
<#--                info.style.display = "block";-->
<#--            } else {-->
<#--                elementShow.style.display = "none";-->
<#--                info.style.display = "none";-->
<#--            }-->
<#--        })-->
<#--    });-->
<#--    const buttons = document.querySelectorAll('.btnOpenModal');-->
<#--    let selectedTaskId = null;-->
<#--    let currentButton = null;-->
<#--    const saveBtn = document.getElementById('saveId');-->

<#--    buttons.forEach((btn) => {-->
<#--        btn.addEventListener('click', function() {-->
<#--            selectedTaskId = this.getAttribute('data-id').replace(/\u00A0|\s+/g, '').trim();-->
<#--            currentButton = this;-->
<#--            console.log("ID:", selectedTaskId);-->
<#--        });-->
<#--    });-->

<#--    document.getElementById('saveId').addEventListener('click', () => {-->
<#--        const qty = document.getElementById('qtyFinishedId').value.trim();-->

<#--        if (!selectedTaskId || !currentButton) {-->
<#--            alert('Пожалуйста, выберите задачу.');-->
<#--            return;-->
<#--        }-->

<#--        saveBtn.disabled = true;-->

<#--        const idNumber = parseInt(selectedTaskId, 10);-->
<#--        const requestBody = {-->
<#--            data: [-->
<#--                {-->
<#--                    id: idNumber,-->
<#--                    qty_finished_input: parseFloat(qty)-->
<#--                }-->
<#--            ]-->
<#--        };-->

<#--        fetch('http://localhost:8080/api/stormcrud/upd/scmo_jm/ShiftTaskQtyControl', {-->
<#--            method: 'POST',-->
<#--            headers: {-->
<#--                'accept': 'application/json',-->
<#--                'Authorization': 'Bearer ${token}',-->
<#--                'Content-Type': 'application/json'-->
<#--            },-->
<#--            body: JSON.stringify(requestBody)-->
<#--        })-->
<#--            .then(response => response.json())-->
<#--            .then(data => {-->
<#--                console.log('Ответ от сервера:', data);-->
<#--                saveBtn.disabled = false;-->
<#--                if (data.recordsBatch && data.recordsBatch.errorList && data.recordsBatch.errorList.length > 0) {-->
<#--                    const errorObj = data.recordsBatch.errorList[0];-->
<#--                    const errorMessage = errorObj.message || 'Произошла ошибка.';-->
<#--                    document.getElementById('errorMessage').innerText = errorMessage;-->
<#--                    var errorModal = new bootstrap.Modal(document.getElementById('errorBackdrop'));-->
<#--                    errorModal.show();-->
<#--                } else {-->
<#--                    var modal = bootstrap.Modal.getInstance(document.getElementById('staticBackdrop'));-->
<#--                    if (modal) {-->
<#--                        modal.hide();-->
<#--                    }-->
<#--                    location.reload();-->
<#--                }-->
<#--            })-->
<#--            .catch(error => {-->
<#--                console.error('Ошибка:', error);-->
<#--                saveBtn.disabled = false;-->
<#--                document.getElementById('errorMessage').innerText = 'Произошла ошибка при отправке запроса.';-->
<#--                var errorModal = new bootstrap.Modal(document.getElementById('errorBackdrop'));-->
<#--                errorModal.show();-->
<#--            });-->
<#--    });-->
<#--</script>-->
<#--<script>-->
<#--    document.addEventListener("DOMContentLoaded", function() {-->

<#--        const buttons = document.querySelectorAll('.btnInWork');-->
<#--        let selectedJobStepId = null;-->
<#--        let mlmNodeId = null;-->

<#--        buttons.forEach((btn) => {-->
<#--            btn.addEventListener('click', function() {-->
<#--                selectedJobStepId = this.getAttribute('data-id').replace(/\u00A0|\s+/g, '').trim();-->
<#--                mlmNodeId = this.getAttribute('data-workMlmNodeId').replace(/\u00A0|\s+/g, '').trim();-->
<#--                console.log("ID:", selectedJobStepId);-->

<#--                // Отправка POST-запроса с выбранным ID-->
<#--                fetch('/api/shift-task/create-document', {-->
<#--                // fetch('/shift-task/stepbox', {-->
<#--                    method: 'POST',-->
<#--                    headers: {-->
<#--                        'Content-Type': 'application/json'-->
<#--                    },-->
<#--                    body: JSON.stringify({ primarydemand_list: selectedJobStepId}) // Передача ID в теле запроса-->
<#--                })-->
<#--                    .then(response => response.text())-->
<#--                    .then(data => {-->
<#--                        // Обработка ответа сервера-->
<#--                        console.log(data);-->
<#--                    })-->
<#--                    .catch(error => console.error('Ошибка:', error));-->
<#--            });-->
<#--        });-->
<#--    });-->
<#--</script>-->

<#--</body>-->
<#--</html>-->