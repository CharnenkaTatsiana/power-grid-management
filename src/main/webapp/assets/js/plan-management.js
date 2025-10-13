/**
 * Модуль для управления планами
 */
class PlanManager {
    constructor() {
        this.apiBaseUrl = '';
        this.currentPlanId = null;
        this.init();
    }

    init() {
        this.bindEvents();
        this.setupAutoCalculation();
    }

    bindEvents() {
        // Автоматическое сохранение при изменении
        document.querySelectorAll('input[type="number"]').forEach(input => {
            input.addEventListener('change', this.debounce(this.autoSave.bind(this), 1000));
        });

        // Валидация значений
        document.querySelectorAll('input[type="number"]').forEach(input => {
            input.addEventListener('blur', this.validateInput.bind(this));
        });

        // Подтверждение отправки на утверждение
        const submitButtons = document.querySelectorAll('button[data-action="submit-plan"]');
        submitButtons.forEach(button => {
            button.addEventListener('click', this.confirmSubmission.bind(this));
        });
    }

    setupAutoCalculation() {
        // Автоматический пересчет при изменении значений
        const calculationInputs = document.querySelectorAll('.calculation-input');
        calculationInputs.forEach(input => {
            input.addEventListener('input', this.calculateTotals.bind(this));
        });
    }

    // Автоматическое сохранение
    async autoSave(event) {
        const input = event.target;
        const planItemId = this.getPlanItemIdFromInput(input);

        if (!planItemId) return;

        const data = this.collectPlanItemData(planItemId);

        try {
            await this.savePlanItem(planItemId, data);
            this.showNotification('Изменения сохранены', 'success');
        } catch (error) {
            this.showNotification('Ошибка сохранения', 'error');
            console.error('Auto-save error:', error);
        }
    }

    // Сбор данных элемента плана
    collectPlanItemData(planItemId) {
        return {
            annual: this.getValue(`items[${planItemId}].annual`),
            q1: this.getValue(`items[${planItemId}].q1`),
            q2: this.getValue(`items[${planItemId}].q2`),
            q3: this.getValue(`items[${planItemId}].q3`),
            q4: this.getValue(`items[${planItemId}].q4`)
        };
    }

    // Сохранение элемента плана через API
    async savePlanItem(planItemId, data) {
        const response = await fetch(`${this.apiBaseUrl}/api/plan-items/${planItemId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error('Failed to save plan item');
        }

        return await response.json();
    }

    // Расчет итоговых значений
    calculateTotals() {
        let totalAnnual = 0;
        let totalQ1 = 0;
        let totalQ2 = 0;
        let totalQ3 = 0;
        let totalQ4 = 0;

        document.querySelectorAll('.plan-item:not(.header)').forEach(row => {
            const annual = this.parseNumber(row.querySelector('input[name$=".annual"]'));
            const q1 = this.parseNumber(row.querySelector('input[name$=".q1"]'));
            const q2 = this.parseNumber(row.querySelector('input[name$=".q2"]'));
            const q3 = this.parseNumber(row.querySelector('input[name$=".q3"]'));
            const q4 = this.parseNumber(row.querySelector('input[name$=".q4"]'));

            totalAnnual += annual;
            totalQ1 += q1;
            totalQ2 += q2;
            totalQ3 += q3;
            totalQ4 += q4;
        });

        this.updateTotalDisplay('totalAnnual', totalAnnual);
        this.updateTotalDisplay('totalQ1', totalQ1);
        this.updateTotalDisplay('totalQ2', totalQ2);
        this.updateTotalDisplay('totalQ3', totalQ3);
        this.updateTotalDisplay('totalQ4', totalQ4);
    }

    // Валидация ввода
    validateInput(event) {
        const input = event.target;
        const value = parseFloat(input.value);

        if (value < 0) {
            this.showInputError(input, 'Значение не может быть отрицательным');
            return false;
        }

        if (isNaN(value)) {
            this.showInputError(input, 'Введите числовое значение');
            return false;
        }

        this.clearInputError(input);
        return true;
    }

    // Подтверждение отправки на утверждение
    confirmSubmission(event) {
        if (!confirm('Вы уверены, что хотите отправить план на утверждение? После этого редактирование будет невозможно.')) {
            event.preventDefault();
            return false;
        }
        return true;
    }

    // Вспомогательные методы
    getPlanItemIdFromInput(input) {
        const name = input.getAttribute('name');
        const match = name.match(/items\[(\d+)\]/);
        return match ? match[1] : null;
    }

    getValue(name) {
        const input = document.querySelector(`input[name="${name}"]`);
        return input ? parseFloat(input.value) || 0 : 0;
    }

    parseNumber(input) {
        return input ? parseFloat(input.value) || 0 : 0;
    }

    updateTotalDisplay(elementId, value) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = value.toFixed(2);
        }
    }

    showInputError(input, message) {
        this.clearInputError(input);

        const errorDiv = document.createElement('div');
        errorDiv.className = 'input-error';
        errorDiv.textContent = message;
        errorDiv.style.cssText = 'color: #dc3545; font-size: 0.875rem; margin-top: 0.25rem;';

        input.style.borderColor = '#dc3545';
        input.parentNode.appendChild(errorDiv);
    }

    clearInputError(input) {
        const errorDiv = input.parentNode.querySelector('.input-error');
        if (errorDiv) {
            errorDiv.remove();
        }
        input.style.borderColor = '';
    }

    showNotification(message, type = 'info') {
        // Используем существующую систему уведомлений
        if (window.showNotification) {
            window.showNotification(message, type);
        } else {
            console.log(`${type.toUpperCase()}: ${message}`);
        }
    }

    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', () => {
    window.planManager = new PlanManager();
});

/**
 * Утилиты для работы с планами
 */
const PlanUtils = {
    // Форматирование чисел
    formatNumber(value, decimals = 2) {
        return parseFloat(value || 0).toFixed(decimals);
    },

    // Расчет годовой суммы из кварталов
    calculateAnnual(q1, q2, q3, q4) {
        return (parseFloat(q1) || 0) + (parseFloat(q2) || 0) +
            (parseFloat(q3) || 0) + (parseFloat(q4) || 0);
    },

    // Равномерное распределение годовой суммы по кварталам
    distributeAnnual(annual) {
        const quarterValue = (parseFloat(annual) || 0) / 4;
        return {
            q1: quarterValue,
            q2: quarterValue,
            q3: quarterValue,
            q4: quarterValue
        };
    },

    // Валидация плана
    validatePlan(planData) {
        const errors = [];

        if (!planData.planYear || planData.planYear < 2000 || planData.planYear > 2100) {
            errors.push('Некорректный год плана');
        }

        if (!planData.planItems || planData.planItems.length === 0) {
            errors.push('План должен содержать хотя бы одну позицию');
        }

        let totalAnnual = 0;
        planData.planItems.forEach((item, index) => {
            if (item.annualPlan < 0) {
                errors.push(`Позиция ${index + 1}: годовая сумма не может быть отрицательной`);
            }
            if (item.q1Plan < 0 || item.q2Plan < 0 || item.q3Plan < 0 || item.q4Plan < 0) {
                errors.push(`Позиция ${index + 1}: квартальные суммы не могут быть отрицательными`);
            }

            totalAnnual += item.annualPlan;
        });

        if (totalAnnual === 0) {
            errors.push('Общая сумма плана не может быть равна нулю');
        }

        return errors;
    },

    // Экспорт плана в Excel
    async exportToExcel(planId, format = 'xlsx') {
        try {
            const response = await fetch(`/api/plans/${planId}/export?format=${format}`);
            if (!response.ok) throw new Error('Export failed');

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `plan-${planId}.${format}`;
            a.click();
            window.URL.revokeObjectURL(url);

            return true;
        } catch (error) {
            console.error('Export error:', error);
            return false;
        }
    }
};

// Глобальное использование
window.PlanUtils = PlanUtils;