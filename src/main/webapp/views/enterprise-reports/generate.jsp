<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Генерация отчета РУП-облэнерго</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .container {
            max-width: 800px;
        }
        .card {
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            border: none;
            border-radius: 10px;
        }
        .form-select {
            border-radius: 8px;
        }
        .btn-generate {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 8px;
            padding: 12px 30px;
            font-weight: 600;
        }
        .status-indicator {
            display: inline-block;
            width: 10px;
            height: 10px;
            border-radius: 50%;
            margin-right: 8px;
        }
        .status-ready { background-color: #28a745; }
        .status-waiting { background-color: #ffc107; }
    </style>
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">🏭 Генерация отчета предприятия</h4>
                </div>
                <div class="card-body">
                    <c:if test="${not empty success}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            ✅ ${success}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            ❌ ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <div class="alert alert-info">
                        <strong>💡 Информация:</strong> Отчет РУП-облэнерго будет сгенерирован автоматически
                        на основе данных отчетов филиалов. Если не все филиалы заполнили отчеты,
                        в сгенерированном отчете будут только данные по заполненным филиалам.
                    </div>

                    <form action="${pageContext.request.contextPath}/enterprise-reports" method="post">
                        <input type="hidden" name="action" value="generate">

                        <div class="row mb-4">
                            <div class="col-md-6">
                                <label for="enterpriseId" class="form-label">🏢 Предприятие</label>
                                <select class="form-select" id="enterpriseId" name="enterpriseId" required>
                                    <option value="">-- Выберите предприятие --</option>
                                    <c:forEach var="enterprise" items="${enterprises}">
                                        <option value="${enterprise.id}">${enterprise.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label for="year" class="form-label">📅 Год</label>
                                <select class="form-select" id="year" name="year" required>
                                    <option value="">-- Год --</option>
                                    <c:forEach var="year" begin="${currentYear - 2}" end="${currentYear + 1}">
                                        <option value="${year}" ${year == currentYear ? 'selected' : ''}>${year}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label for="month" class="form-label">📋 Месяц</label>
                                <select class="form-select" id="month" name="month" required>
                                    <option value="">-- Месяц --</option>
                                    <c:forEach var="month" items="${months}">
                                        <option value="${month.value}">${month.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <!-- Блок предварительной проверки статуса -->
                        <div id="statusCheck" class="mb-4" style="display: none;">
                            <div class="card">
                                <div class="card-header bg-light">
                                    <h6 class="mb-0">🔍 Предварительная проверка статуса</h6>
                                </div>
                                <div class="card-body">
                                    <div id="statusContent">
                                        <!-- Сюда будет загружаться статус через AJAX -->
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary me-md-2">
                                ← К отчетам филиалов
                            </a>
                            <button type="submit" class="btn btn-primary btn-generate" id="generateBtn">
                                🚀 Сгенерировать отчет РУП-облэнерго
                            </button>
                        </div>
                    </form>

                    <div class="mt-4">
                        <h6>📊 Доступные РУП-облэнерго:</h6>
                        <c:choose>
                            <c:when test="${not empty enterprises}">
                                <div class="list-group">
                                    <c:forEach var="enterprise" items="${enterprises}">
                                        <div class="list-group-item">
                                            <div class="d-flex w-100 justify-content-between">
                                                <h6 class="mb-1">🏭 ${enterprise.name}</h6>
                                                <small class="text-muted">ID: ${enterprise.id}</small>
                                            </div>
                                            <c:if test="${not empty enterprise.association}">
                                                <small class="text-muted">Объединение: ${enterprise.association.name}</small>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-warning">
                                    📭 РУП-облэнерго не найдены в системе
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Автоматическое скрытие alert через 5 секунд
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // AJAX проверка статуса при изменении полей
    document.addEventListener('DOMContentLoaded', function() {
        const enterpriseSelect = document.getElementById('enterpriseId');
        const yearSelect = document.getElementById('year');
        const monthSelect = document.getElementById('month');
        const statusCheck = document.getElementById('statusCheck');
        const statusContent = document.getElementById('statusContent');
        const generateBtn = document.getElementById('generateBtn');

        function checkStatus() {
            const enterpriseId = enterpriseSelect.value;
            const year = yearSelect.value;
            const month = monthSelect.value;

            if (enterpriseId && year && month) {
                // Показываем блок проверки
                statusCheck.style.display = 'block';
                statusContent.innerHTML = '<div class="text-center"><div class="spinner-border spinner-border-sm" role="status"></div> Проверяем статус...</div>';

                // AJAX запрос для проверки статуса
                fetch('${pageContext.request.contextPath}/enterprise-reports?action=status&enterpriseId=' + enterpriseId + '&year=' + year + '&month=' + month)
                    .then(response => response.text())
                    .then(html => {
                        statusContent.innerHTML = html;
                    })
                    .catch(error => {
                        statusContent.innerHTML = '<div class="alert alert-danger">Ошибка при проверке статуса</div>';
                        console.error('Error:', error);
                    });
            } else {
                statusCheck.style.display = 'none';
            }
        }

        // Слушаем изменения в полях формы
        enterpriseSelect.addEventListener('change', checkStatus);
        yearSelect.addEventListener('change', checkStatus);
        monthSelect.addEventListener('change', checkStatus);

        // Предварительная проверка формы
        document.querySelector('form').addEventListener('submit', function(e) {
            const enterpriseId = enterpriseSelect.value;
            const year = yearSelect.value;
            const month = monthSelect.value;

            if (!enterpriseId || !year || !month) {
                e.preventDefault();
                alert('Пожалуйста, заполните все поля');
            }
        });
    });
</script>
</body>
</html>
