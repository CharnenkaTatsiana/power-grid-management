<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.powergrid.management.model.*" %>
<%@ page import="com.powergrid.management.dto.*" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) request.getAttribute("user");
    Report report = (Report) request.getAttribute("report");
    List<ReportItemDTO> reportItems = (List<ReportItemDTO>) request.getAttribute("reportItems");

    if (user == null || report == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Редактирование отчета - Power Grid Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>✏️ Редактирование отчета</h1>
        <a href="${pageContext.request.contextPath}/reports?action=view&id=<%= report.getId() %>" class="btn btn-secondary">
            ← Назад к просмотру
        </a>
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
                <%= report.getTitle() %>
                <span class="badge bg-warning">Редактирование</span>
            </h5>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/reports" method="post">
                <input type="hidden" name="action" value="updateFactData">
                <input type="hidden" name="reportId" value="<%= report.getId() %>">

                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead class="table-dark">
                        <tr>
                            <th>Тип работ</th>
                            <th>Годовой план</th>
                            <th>Квартальный план (нарастающий)</th>
                            <th>Факт за месяц</th>
                            <th>% выполнения годового</th>
                            <th>% выполнения квартального</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (ReportItemDTO item : reportItems) { %>
                        <tr>
                            <td>
                                <%= item.getWorkTypeName() %>
                                <input type="hidden" name="itemId" value="<%= item.getId() %>">
                            </td>
                            <td><%= String.format("%.2f", item.getAnnualPlan() != null ? item.getAnnualPlan() : 0) %></td>
                            <td><%= String.format("%.2f", item.getQuarterPlan() != null ? item.getQuarterPlan() : 0) %></td>
                            <td>
                                <input type="number" step="0.01" name="monthFact"
                                       value="<%= item.getMonthFact() != null ? String.format("%.2f", item.getMonthFact()) : "" %>"
                                       class="form-control form-control-sm" style="width: 120px;">
                            </td>
                            <td class="<%= getPercentageClass(item.getAnnualPercentage()) %>">
                                <%= item.getAnnualPercentage() != null ? String.format("%.1f%%", item.getAnnualPercentage()) : "—" %>
                            </td>
                            <td class="<%= getPercentageClass(item.getQuarterPercentage()) %>">
                                <%= item.getQuarterPercentage() != null ? String.format("%.1f%%", item.getQuarterPercentage()) : "—" %>
                            </td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>

                <div class="mt-3">
                    <button type="submit" class="btn btn-success">💾 Сохранить изменения</button>
                    <a href="${pageContext.request.contextPath}/reports?action=view&id=<%= report.getId() %>"
                       class="btn btn-secondary">❌ Отмена</a>
                </div>
            </form>
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
</script>
</body>
</html>

<%!
    private String getPercentageClass(Double percentage) {
        if (percentage == null) return "";
        if (percentage >= 80) return "text-success fw-bold";
        if (percentage >= 50) return "text-warning";
        return "text-danger";
    }
%>