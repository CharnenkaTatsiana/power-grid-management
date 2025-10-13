// Основной JavaScript файл
document.addEventListener('DOMContentLoaded', function() {
    console.log('Power Grid System initialized');

    // Обработка форм
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const inputs = this.querySelectorAll('input[required]');
            let valid = true;

            inputs.forEach(input => {
                if (!input.value.trim()) {
                    valid = false;
                    input.style.borderColor = '#dc3545';
                } else {
                    input.style.borderColor = '';
                }
            });

            if (!valid) {
                e.preventDefault();
                showMessage('Заполните все обязательные поля', 'error');
            }
        });
    });

    // Показать/скрыть пароль
    const togglePassword = document.querySelector('.toggle-password');
    if (togglePassword) {
        togglePassword.addEventListener('click', function() {
            const passwordInput = this.previousElementSibling;
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            this.textContent = type === 'password' ? '👁️' : '👁️‍🗨️';
        });
    }
});

// Функция показа сообщений
function showMessage(message, type = 'info') {
    const messageDiv = document.createElement('div');
    messageDiv.className = `alert alert-${type}`;
    messageDiv.textContent = message;

    // Добавляем сообщение в начало body
    document.body.insertBefore(messageDiv, document.body.firstChild);

    // Автоматическое удаление через 5 секунд
    setTimeout(() => {
        messageDiv.remove();
    }, 5000);
}

// API функции
const API = {
    async request(url, options = {}) {
        try {
            const response = await fetch(url, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('API request failed:', error);
            showMessage('Ошибка соединения с сервером', 'error');
            throw error;
        }
    }
};