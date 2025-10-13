<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Планы работ филиалов</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .plan-card {
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 15px;
            background: white;
            transition: box-shadow 0.3s ease;
        }
        .plan-card:hover {
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .actions-column {
            white-space: nowrap;
        }
        .hierarchy-path {
            font-size: 0.85em;
            color: #6c757d;
            margin-top: 5px;
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
        .logout-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(220, 53, 69, 0.4);
            color: white;
        }
    </style>
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>🏢 Планы работ филиалов</h1>
        <div>
            <a href="${pageContext.request.contextPath}/plans?action=create" class="btn btn-primary">
                ➕ Создать план филиала
            </a>
            <a href="${pageContext.request.contextPath}/reports" class="btn btn-outline-secondary">
                📊 К отчетам
            </a>
            <a href="${pageContext.request.contextPath}/logout" class="logout-btn"
               onclick="return confirm('Вы уверены, что хотите выйти?')">
                🚪 Выйти
            </a>
        </div>
    </div>

    <c:if test="${not empty success}">
        <div class="alert alert-success">✅ ${success}</div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">❌ ${error}</div>
    </c:if>

    <div class="card">
        <div class="card-header">
            <h5 class="card-title mb-0">
                Список планов филиалов
                <span class="badge bg-primary">${planCount}</span>
            </h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty plans}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Год</th>
                                <th>Филиал</th>
                                <th>Иерархия</th>
                                <th>Автор</th>
                                <th>Дата создания</th>
                                <th>Действия</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="plan" items="${plans}">
                                <tr>
                                    <td><strong>#${plan.id}</strong></td>
                                    <td>
                                        <span class="badge bg-dark">${plan.planYear}</span>
                                    </td>
                                    <td>
                                        <i class="fas fa-building"></i> ${plan.branchName}
                                    </td>
                                    <td class="hierarchy-path">
                                        <small>
                                                ${plan.branchName}
                                            <c:if test="${not empty plan.enterpriseName}">
                                                → ${plan.enterpriseName}
                                            </c:if>
                                            <c:if test="${not empty plan.associationName}">
                                                → ${plan.associationName}
                                            </c:if>
                                        </small>
                                    </td>
                                    <td>👤 ${plan.createdBy}</td>
                                    <td>
                                        <small class="text-muted">
                                                ${plan.createdDate}
                                        </small>
                                    </td>
                                    <td class="actions-column">
                                        <a href="${pageContext.request.contextPath}/plans?action=view&id=${plan.id}"
                                           class="btn btn-sm btn-outline-primary" title="Просмотр">
                                            👁️
                                        </a>
                                        <a href="${pageContext.request.contextPath}/plans?action=edit&id=${plan.id}"
                                           class="btn btn-sm btn-outline-secondary" title="Редактировать">
                                            ✏️
                                        </a>
                                        <form action="${pageContext.request.contextPath}/plans" method="post"
                                              style="display: inline;"
                                              onsubmit="return confirm('Удалить план филиала #${plan.id}? Все связанные отчеты также будут удалены.');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${plan.id}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Удалить">
                                                🗑️
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-5">
                        <div class="text-muted">
                            <h4>📭 Планы филиалов не найдены</h4>
                            <p>Создайте первый план филиала для начала работы</p>
                            <p class="small">При создании плана автоматически будут созданы формы отчетов на весь год</p>
                            <a href="${pageContext.request.contextPath}/plans?action=create" class="btn btn-primary mt-3">
                                ➕ Создать план филиала
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Автоматическое скрытие сообщений
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            const bsAlert = new bootstrap.Alert(alert);
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