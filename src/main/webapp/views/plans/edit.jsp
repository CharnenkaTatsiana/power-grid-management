<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Редактирование плана - Power Grid System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .plan-header {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
        }
        .plan-items {
            background: white;
            border-radius: 8px;
            overflow: hidden;
        }
        .plan-item {
            display: grid;
            grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr;
            gap: 1rem;
            padding: 1rem;
            border-bottom: 1px solid #eee;
            align-items: center;
        }
        .plan-item-header {
            background: #f8f9fa;
            font-weight: bold;
        }
        .plan-item:last-child {
            border-bottom: none;
        }
        .total-row {
            background: #e3f2fd;
            font-weight: bold;
        }
        input[type="number"] {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .network-type-badge {
            display: inline-block;
            padding: 0.25rem 0.5rem;
            border-radius: 4px;
            font-size: 0.75rem;
            margin-left: 0.5rem;
        }
        .main-network { background: #e3f2fd; color: #1565c0; }
        .distribution-network { background: #f3e5f5; color: #7b1fa2; }
    </style>
</head>
<body>
<div class="container">
    <div class="plan-header">
        <h1>✏️ Редактирование плана</h1>
        <h2>${plan.planName}</h2>
        <p>Статус:
            <span class="plan-status status-${plan.status.name().toLowerCase()}">
                ${plan.status.name()}
            </span>
        </p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/plans/${plan.id}/update" method="post" id="planForm">
        <div class="plan-items">
            <!-- Заголовок таблицы -->
            <div class="plan-item plan-item-header">
                <div>Тип работ</div>
                <div>Годовой план</div>
                <div>1 квартал</div>
                <div>2 квартал</div>
                <div>3 квартал</div>
                <div>4 квартал</div>
            </div>

            <!-- Элементы плана -->
            <c:forEach var="item" items="${plan.planItems}">
                <div class="plan-item">
                    <div>
                            ${item.workType.name}
                        <span class="network-type-badge ${item.workType.networkType.name().toLowerCase().replace('_', '-')}">
                                ${item.workType.networkType == 'MAIN_NETWORK' ? 'Основная сеть' : 'Распределительная сеть'}
                        </span>
                    </div>

                    <div>
                        <input type="number" name="items[${item.workType.id}].annual"
                               value="<fmt:formatNumber value="${item.annualPlan}" pattern="#.##"/>"
                               step="0.01" min="0" onchange="calculateQuarters(${item.workType.id})">
                    </div>

                    <div>
                        <input type="number" name="items[${item.workType.id}].q1"
                               value="<fmt:formatNumber value="${item.q1Plan}" pattern="#.##"/>"
                               step="0.01" min="0" onchange="calculateAnnual(${item.workType.id})">
                    </div>

                    <div>
                        <input type="number" name="items[${item.workType.id}].q2"
                               value="<fmt:formatNumber value="${item.q2Plan}" pattern="#.##"/>"
                               step="0.01" min="0" onchange="calculateAnnual(${item.workType.id})">
                    </div>

                    <div>
                        <input type="number" name="items[${item.workType.id}].q3"
                               value="<fmt:formatNumber value="${item.q3Plan}" pattern="#.##"/>"
                               step="0.01" min="0" onchange="calculateAnnual(${item.workType.id})">
                    </div>

                    <div>
                        <input type="number" name="items[${item.workType.id}].q4"
                               value="<fmt:formatNumber value="${item.q4Plan}" pattern="#.##"/>"
                               step="0.01" min="0" onchange="calculateAnnual(${item.workType.id})">
                    </div>
                </div>
            </c:forEach>

            <!-- Итоговая строка -->
            <div class="plan-item total-row">
                <div><strong>ИТОГО:</strong></div>
                <div><strong id="totalAnnual">${plan.totalAnnual}</strong></div>
                <div><strong id="totalQ1">0</strong></div>
                <div><strong id="totalQ2">0</strong></div>
                <div><strong id="totalQ3">0</strong></div>
                <div><strong id="totalQ4">0</strong></div>
            </div>
        </div>

        <div class="form-actions" style="margin-top: 2rem; display: flex; gap: 1rem;">
            <button type="submit" class="btn btn-primary">Сохранить изменения</button>
            <a href="${pageContext.request.contextPath}/plans/${plan.id}" class="btn btn-secondary">Отмена</a>

            <c:if test="${currentUser.admin}">
                <button type="button" onclick="submitPlan()" class="btn btn-success">
                    Отправить на утверждение
                </button>
            </c:if>
        </div>
    </form>
</div>

<script>
    // Расчет годовой суммы из кварталов
    function calculateAnnual(workTypeId) {
        const q1 = parseFloat(document.querySelector(`input[name="items[${workTypeId}].q1"]`).value) || 0;
        const q2 = parseFloat(document.querySelector(`input[name="items[${workTypeId}].q2"]`).value) || 0;
        const q3 = parseFloat(document.querySelector(`input[name="items[${workTypeId}].q3"]`).value) || 0;
        const q4 = parseFloat(document.querySelector(`input[name="items[${workTypeId}].q4"]`).value) || 0;

        const annual = q1 + q2 + q3 + q4;
        document.querySelector(`input[name="items[${workTypeId}].annual"]`).value = annual.toFixed(2);

        updateTotals();
    }

    // Распределение годовой суммы по кварталам (равномерно)
    function calculateQuarters(workTypeId) {
        const annual = parseFloat(document.querySelector(`input[name="items[${workTypeId}].annual"]`).value) || 0;
        const quarterValue = (annual / 4).toFixed(2);

        document.querySelector(`input[name="items[${workTypeId}].q1"]`).value = quarterValue;
        document.querySelector(`input[name="items[${workTypeId}].q2"]`).value = quarterValue;
        document.querySelector(`input[name="items[${workTypeId}].q3"]`).value = quarterValue;
        document.querySelector(`input[name="items[${workTypeId}].q4"]`).value = quarterValue;

        updateTotals();
    }

    // Обновление итоговых сумм
    function updateTotals() {
        let totalAnnual = 0, totalQ1 = 0, totalQ2 = 0, totalQ3 = 0, totalQ4 = 0;

        document.querySelectorAll('.plan-item:not(.plan-item-header):not(.total-row)').forEach(row => {
            const inputs = row.querySelectorAll('input[type="number"]');
            if (inputs.length >= 5) {
                totalAnnual += parseFloat(inputs[0].value) || 0;
                totalQ1 += parseFloat(inputs[1].value) || 0;
                totalQ2 += parseFloat(inputs[2].value) || 0;
                totalQ3 += parseFloat(inputs[3].value) || 0;
                totalQ4 += parseFloat(inputs[4].value) || 0;
            }
        });

        document.getElementById('totalAnnual').textContent = totalAnnual.toFixed(2);
        document.getElementById('totalQ1').textContent = totalQ1.toFixed(2);
        document.getElementById('totalQ2').textContent = totalQ2.toFixed(2);
        document.getElementById('totalQ3').textContent = totalQ3.toFixed(2);
        document.getElementById('totalQ4').textContent = totalQ4.toFixed(2);
    }

    // Отправка плана на утверждение
    function submitPlan() {
        if (confirm('Отправить план на утверждение? После этого редактирование будет невозможно.')) {
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '${pageContext.request.contextPath}/plans/${plan.id}/status';

            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'status';
            input.value = 'SUBMITTED';
            form.appendChild(input);

            document.body.appendChild(form);
            form.submit();
        }
    }

    // Инициализация итогов при загрузке
    document.addEventListener('DOMContentLoaded', updateTotals);
</script>
</body>
</html>
