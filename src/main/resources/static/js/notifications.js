let stompClient = null;

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/user/queue/notifications', function (notifications) {
            const payload = JSON.parse(notifications.body);
            showNotification(payload);
        });
    }, function (error) {
        setTimeout(connect, 5000);
    });
}

function showNotification(payload) {
    const container = document.getElementById('notification-toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = 'toast-notification';

    switch (payload.status) {
        case 'В работе':
            toast.classList.add('toast-warning');
            break;
        case 'Забракована':
            toast.classList.add('toast-error');
            break;
        case 'Закрыт':
            toast.classList.add('toast-success');
            break;
        case 'Новый':
        default:
            toast.classList.add('toast-info');
            break;
    }

    const iconDiv = document.createElement('div');
    iconDiv.className = 'toast-icon';

    const contentDiv = document.createElement('div');
    contentDiv.className = 'toast-content';

    const messageElement = document.createElement('p');
    messageElement.className = 'message';
    messageElement.textContent = payload.message;

    const button = document.createElement('a');
    button.className = 'toast-button';
    button.href = payload.link;
    button.textContent = 'Перейти к заявке';

    contentDiv.appendChild(messageElement);
    contentDiv.appendChild(button);
    toast.appendChild(iconDiv);
    toast.appendChild(contentDiv);

    container.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 10000);
}

document.addEventListener('DOMContentLoaded', connect);
