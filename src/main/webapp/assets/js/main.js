// Power Grid Management System - Main JavaScript
class PowerGridSystem {
    constructor() {
        this.init();
    }

    init() {
        console.log('⚡ Power Grid System initialized');
        this.setupEventListeners();
        this.checkSessionStatus();
        this.setupServiceWorker();
    }

    setupEventListeners() {
        // Form validation
        this.setupFormValidation();

        // Navigation
        this.setupNavigation();

        // Notifications
        this.setupNotifications();
    }

    setupFormValidation() {
        const forms = document.querySelectorAll('form[data-validate]');

        forms.forEach(form => {
            form.addEventListener('submit', (e) => {
                if (!this.validateForm(form)) {
                    e.preventDefault();
                    this.showNotification('Пожалуйста, исправьте ошибки в форме', 'error');
                }
            });
        });
    }

    validateForm(form) {
        let isValid = true;
        const inputs = form.querySelectorAll('input[required], select[required]');

        inputs.forEach(input => {
            this.clearError(input);

            if (!input.value.trim()) {
                this.showError(input, 'Это поле обязательно для заполнения');
                isValid = false;
            }

            // Email validation
            if (input.type === 'email' && input.value) {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(input.value)) {
                    this.showError(input, 'Введите корректный email адрес');
                    isValid = false;
                }
            }

            // Password strength
            if (input.type === 'password' && input.value) {
                if (input.value.length < 6) {
                    this.showError(input, 'Пароль должен содержать минимум 6 символов');
                    isValid = false;
                }
            }
        });

        return isValid;
    }

    showError(input, message) {
        this.clearError(input);

        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.textContent = message;
        errorDiv.style.cssText = `
            color: #ef4444;
            font-size: 0.875rem;
            margin-top: 0.25rem;
        `;

        input.style.borderColor = '#ef4444';
        input.parentNode.appendChild(errorDiv);
    }

    clearError(input) {
        const errorDiv = input.parentNode.querySelector('.error-message');
        if (errorDiv) {
            errorDiv.remove();
        }
        input.style.borderColor = '';
    }

    setupNavigation() {
        // Smooth scrolling
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });

        // Active navigation
        const currentPath = window.location.pathname;
        document.querySelectorAll('nav a').forEach(link => {
            if (link.getAttribute('href') === currentPath) {
                link.classList.add('active');
            }
        });
    }

    setupNotifications() {
        // Global notification system
        window.showNotification = this.showNotification;
    }

    showNotification(message, type = 'info', duration = 5000) {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <span class="notification-icon">${this.getNotificationIcon(type)}</span>
                <span class="notification-message">${message}</span>
                <button class="notification-close">&times;</button>
            </div>
        `;

        // Styles
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${this.getNotificationColor(type)};
            color: white;
            padding: 1rem;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            z-index: 10000;
            max-width: 400px;
            animation: slideIn 0.3s ease;
        `;

        document.body.appendChild(notification);

        // Close button
        notification.querySelector('.notification-close').addEventListener('click', () => {
            notification.remove();
        });

        // Auto remove
        setTimeout(() => {
            if (notification.parentNode) {
                notification.style.animation = 'slideOut 0.3s ease';
                setTimeout(() => notification.remove(), 300);
            }
        }, duration);
    }

    getNotificationIcon(type) {
        const icons = {
            success: '✅',
            error: '❌',
            warning: '⚠️',
            info: 'ℹ️'
        };
        return icons[type] || icons.info;
    }

    getNotificationColor(type) {
        const colors = {
            success: '#10b981',
            error: '#ef4444',
            warning: '#f59e0b',
            info: '#3b82f6'
        };
        return colors[type] || colors.info;
    }

    checkSessionStatus() {
        // Check if user is logged in
        if (document.body.classList.contains('authenticated')) {
            this.startSessionTimer();
        }
    }

    startSessionTimer() {
        let timeout = 30 * 60 * 1000; // 30 minutes

        const warningTime = 5 * 60 * 1000; // 5 minutes warning

        setTimeout(() => {
            this.showNotification(
                'Сессия скоро завершится. Сохраните изменения.',
                'warning',
                60000
            );
        }, timeout - warningTime);

        setTimeout(() => {
            this.showNotification(
                'Сессия завершена. Пожалуйста, войдите снова.',
                'error',
                10000
            );
            // Redirect to login after notification
            setTimeout(() => {
                window.location.href = '/auth-enhanced?session=expired';
            }, 10000);
        }, timeout);
    }

    setupServiceWorker() {
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.register('/sw.js')
                .then(registration => {
                    console.log('ServiceWorker registered: ', registration);
                })
                .catch(error => {
                    console.log('ServiceWorker registration failed: ', error);
                });
        }
    }

    // API methods
    async apiRequest(url, options = {}) {
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
            this.showNotification('Ошибка соединения с сервером', 'error');
            throw error;
        }
    }

    // Utility methods
    formatDate(date) {
        return new Date(date).toLocaleDateString('ru-RU', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    formatNumber(number) {
        return new Intl.NumberFormat('ru-RU').format(number);
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new PowerGridSystem();
});

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
    
    .notification-close {
        background: none;
        border: none;
        color: white;
        font-size: 1.2rem;
        cursor: pointer;
        margin-left: 1rem;
    }
    
    .notification-content {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
`;
document.head.appendChild(style);