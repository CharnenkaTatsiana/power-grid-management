<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Отчеты филиалов</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .empty-reports {
            text-align: center;
            padding: 40px;
            color: #6c757d;
        }
        .enterprise-report-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
        }
        .association-report-btn {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            border: none;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
        }
        .logout-btn {
            background: linear-gradient(135deg, #dc3545 0%, #e83e8c 100%);
            border: none;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
        }
        .enterprise-report-btn:hover,
        .association-report-btn:hover,
        .logout-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
            color: white;
        }
        .header-actions {
            display: flex;
            gap: 10px;
            align-items: center;
            flex-wrap: wrap;
        }
        @media (max-width: 768px) {
            .header-actions {
                flex-direction: column;
                align-items: flex-start;
            }
        }
    </style>
</head>
<body>
<div class="container mt-4">
    <!-- Верхняя панель с кнопками -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>📊 Отчеты филиалов</h1>
        <div class="header-actions">
            <a href="${pageContext.request.contextPath}/association-reports" class="association-report-btn">
                🏛️ Отчеты по ГПО "Белэнерго"
            </a>
            <a href="${pageContext.request.contextPath}/enterprise-reports" class="enterprise-report-btn">
                🏭 Отчеты РУП-облэнерго
            </a>
            <a href="${pageContext.request.contextPath}/plans" class="btn btn-secondary">📋 К планам</a>
            <a href="${pageContext.request.contextPath}/logout" class="logout-btn"
               onclick="return confirm('Вы уверены, что хотите выйти?')">
                🚪 Выйти
            </a>
        </div>
    </div>

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

    <div class="card">
        <div class="card-header">
            <h5 class="card-title mb-0">🏢 Список отчетов филиалов</h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty reports}">
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Название</th>
                                <th>Тип</th>
                                <th>Филиал</th>
                                <th>Период</th>
                                <th>Статус</th>
                                <th>Действия</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="report" items="${reports}">
                                <tr>
                                    <td>${report.id}</td>
                                    <td>${report.title}</td>
                                    <td>
                                            <span class="badge
                                                <c:choose>
                                                    <c:when test="${report.reportType.name() == 'MONTHLY'}">bg-primary</c:when>
                                                    <c:when test="${report.reportType.name() == 'QUARTERLY'}">bg-success</c:when>
                                                    <c:otherwise>bg-info</c:otherwise>
                                                </c:choose>">
                                                    ${report.reportType}
                                            </span>
                                    </td>
                                    <td>🏢 ${report.branchName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty report.reportPeriod}">
                                                ${report.reportPeriod}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">—</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${report.isFilled}">
                                                <span class="badge bg-success">✅ Заполнен</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning">⚠️ Не заполнен</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/reports?action=view&id=${report.id}"
                                           class="btn btn-sm btn-outline-primary">👁️ Просмотр</a>
                                        <c:if test="${not report.isFilled}">
                                            <a href="${pageContext.request.contextPath}/reports?action=fill&id=${report.id}"
                                               class="btn btn-sm btn-outline-warning">✏️ Заполнить</a>
                                        </c:if>
                                        <c:if test="${report.isFilled}">
                                            <a href="${pageContext.request.contextPath}/reports?action=edit&id=${report.id}"
                                               class="btn btn-sm btn-outline-info">🔄 Редактировать</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-reports">
                        <h3>📭 Отчеты не найдены</h3>
                        <p>Отчеты автоматически создаются при создании планов филиалов</p>
                        <p class="text-muted">Создайте план филиала, и формы отчетов будут созданы автоматически</p>
                        <div class="mt-3">
                            <a href="${pageContext.request.contextPath}/plans?action=create" class="btn btn-primary me-2">
                                📋 Создать план филиала
                            </a>
                            <a href="${pageContext.request.contextPath}/enterprise-reports" class="enterprise-report-btn me-2">
                                🏭 Отчеты РУП-облэнерго
                            </a>
                            <a href="${pageContext.request.contextPath}/association-reports" class="association-report-btn">
                                🏛️ Отчеты ГПО "Белэнерго"
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Блок информации о отчетах предприятия -->
    <div class="card mt-4">
        <div class="card-header bg-info text-white">
            <h5 class="card-title mb-0">🏭 Отчеты предприятия</h5>
        </div>
        <div class="card-body">
            <div class="row">
                <div class="col-md-8">
                    <h6>Что такое отчеты РУП-облэнерго?</h6>
                    <p class="mb-2">Отчеты предприятия агрегируют данные из отчетов всех филиалов РУП-облэнерго за выбранный период.</p>
                    <ul class="mb-3">
                        <li>📊 Сводная статистика по всем филиалам</li>
                        <li>🏭 Автоматическое суммирование показателей</li>
                        <li>📈 Анализ выполнения планов в масштабе РУП-облэнерго</li>
                        <li>⚡ Быстрая генерация после заполнения отчетов филиалов</li>
                    </ul>
                </div>
                <div class="col-md-4 text-center">
                    <a href="${pageContext.request.contextPath}/enterprise-reports" class="enterprise-report-btn btn-lg">
                        🚀 Сгенерировать отчет РУП-облэнерго
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Блок информации о отчетах объединения -->
    <div class="card mt-4">
        <div class="card-header bg-success text-white">
            <h5 class="card-title mb-0">🏛️ Отчеты по ГПО "Белэнерго"</h5>
        </div>
        <div class="card-body">
            <div class="row">
                <div class="col-md-8">
                    <h6>Что такое отчеты ГПО "Белэнерго"?</h6>
                    <p class="mb-2">Отчеты ГПО "Белэнерго" агрегируют данные из отчетов всех РУП-облэнерго.</p>
                    <ul class="mb-3">
                        <li>🏛️ Сводная статистика по всем РУП-облэнерго</li>
                        <li>📊 Агрегация данных на уровне ГПО "Белэнерго"</li>
                        <li>📈 Стратегический анализ выполнения планов</li>
                        <li>⚡ Автоматическое формирование на основе отчетов РУП-облэнерго</li>
                    </ul>
                </div>
                <div class="col-md-4 text-center">
                    <a href="${pageContext.request.contextPath}/association-reports" class="association-report-btn btn-lg">
                        🚀 Сгенерировать отчет ГПО "Белэнерго"
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Автоматическое скрытие alert через 5 секунд
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Подтверждение выхода
    document.addEventListener('DOMContentLoaded', function() {
        const logoutBtn = document.querySelector('.logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', function(e) {
                if (!confirm('Вы уверены, что хотите выйти?')) {
                    e.preventDefault();
                }
            });
        }
    });
</script>
</body>
</html>