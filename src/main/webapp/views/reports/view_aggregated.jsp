<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.powergrid.management.model.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    User user = (User) request.getAttribute("user");
    Report report = (Report) request.getAttribute("report");
    List<ReportItem> reportItems = (List<ReportItem>) request.getAttribute("reportItems");
    Boolean readOnly = (Boolean) request.getAttribute("readOnly");
    String reportType = (String) request.getAttribute("reportType");

    if (user == null || report == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Агрегированный отчет - Power Grid Management</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; padding: 20px; }
        .container { max-width: 1400px; margin: 0 auto; background: white; border-radius: 10px; box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1); overflow: hidden; }
        .header { background: #2c3e50; color: white; padding: 30px; text-align: center; }
        .header h1 { margin-bottom: 10px; font-size: 2.5em; }
        .user-info { background: #34495e; padding: 15px 30px; color: white; display: flex; justify-content: space-between; align-items: center; }
        .content { padding: 30px; }
        .btn { display: inline-block; padding: 12px 24px; background: #3498db; color: white; text-decoration: none; border-radius: 5px; transition: background 0.3s; }
        .btn:hover { background: #2980b9; }
        .btn-secondary { background: #95a5a6; }
        .btn-secondary:hover { background: #7f8c8d; }

        .report-info { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 30px; border-left: 4px solid #3498db; }
        .report-info h3 { margin-bottom: 15px; color: #2c3e50; }
        .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; }
        .info-item { display: flex; flex-direction: column; }
        .info-label { font-weight: 600; color: #7f8c8d; font-size: 0.9em; margin-bottom: 5px; }
        .info-value { font-size: 1.1em; color: #2c3e50; }

        table { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }
        th { background: #34495e; color: white; padding: 15px; text-align: left; font-weight: 600; }
        td { padding: 12px 15px; border-bottom: 1px solid #ecf0f1; }
        tr:hover { background: #f8f9fa; }
        .total-row { background: #2c3e50; color: white; font-weight: bold; }
        .total-row td { border-bottom: none; }

        .actions { display: flex; gap: 10px; margin-top: 20px; flex-wrap: wrap; }

        .type-badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
            color: white;
        }
        .type-monthly { background: #3498db; }
        .type-quarterly { background: #9b59b6; }
        .type-annual { background: #e67e22; }

        .read-only-note {
            background: #e8f4fd;
            color: #2c3e50;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #3498db;
        }

        .level-badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
            color: white;
        }
        .level-enterprise { background: #3498db; }
        .level-association { background: #9b59b6; }

        .percentage-cell {
            font-weight: bold;
        }
        .percentage-high { color: #27ae60; }
        .percentage-medium { color: #f39c12; }
        .percentage-low { color: #e74c3c; }

        .success-message {
            background: #d4edda;
            color: #155724;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
        }

        .error-message {
            background: #f8d7da;
            color: #721c24;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>📊 Агрегированный отчет</h1>
        <p>Данные сформированы автоматически на основе нижестоящих подразделений</p>
    </div>

    <div class="user-info">
        <div>
            <strong>Пользователь:</strong> <%= user.getFullName() %> (<%= user.getUsername() %>)
        </div>
        <div>
            <a href="<%= request.getContextPath() %>/reports" class="btn btn-secondary">← Назад к списку</a>
        </div>
    </div>

    <div class="content">
        <!-- Сообщения -->
        <%
            String success = (String) request.getSession().getAttribute("success");
            String error = (String) request.getAttribute("error");

            if (success != null) {
                request.getSession().removeAttribute("success");
        %>
        <div class="success-message">
            ✅ <%= success %>
        </div>
        <% } %>

        <% if (error != null) { %>
        <div class="error-message">
            ❌ <%= error %>
        </div>
        <% } %>

        <div class="read-only-note">
            <strong>ℹ️ Информация:</strong> Это агрегированный отчет. Данные рассчитываются автоматически на основе отчетов нижестоящих подразделений и не подлежат редактированию.
        </div>

        <div class="report-info">
            <h3>📋 Основная информация</h3>
            <div class="info-grid">
                <div class="info-item">
                    <span class="info-label">ID отчета:</span>
                    <span class="info-value">#<%= report.getId() %></span>
                </div>
                <div class="info-item">
                    <span class="info-label">Название:</span>
                    <span class="info-value"><%= report.getTitle() %></span>
                </div>
                <div class="info-item">
                    <span class="info-label">Тип отчета:</span>
                    <span class="info-value">
                        <%
                            String typeClass = "type-monthly";
                            String type = report.getReportType().toString();
                            if (type.equals("QUARTERLY")) typeClass = "type-quarterly";
                            else if (type.equals("ANNUAL")) typeClass = "type-annual";
                        %>
                        <span class="type-badge <%= typeClass %>"><%= type %></span>
                    </span>
                </div>
                <div class="info-item">
                    <span class="info-label">Уровень:</span>
                    <span class="info-value">
                        <% if ("enterprise".equals(reportType)) { %>
                            <span class="level-badge level-enterprise">🏭 Предприятие</span>
                        <% } else if ("association".equals(reportType)) { %>
                            <span class="level-badge level-association">🏛️ Объединение</span>
                        <% } %>
                    </span>
                </div>
                <div class="info-item">
                    <span class="info-label">Организация:</span>
                    <span class="info-value">
                        <% if (report.getEnterprise() != null) { %>
                            🏭 <%= report.getEnterprise().getName() %>
                        <% } else if (report.getAssociation() != null) { %>
                            🏛️ <%= report.getAssociation().getName() %>
                        <% } else { %>
                            <span style="color: #95a5a6;">—</span>
                        <% } %>
                    </span>
                </div>
                <div class="info-item">
                    <span class="info-label">Период:</span>
                    <span class="info-value">
                        <% if (report.getPeriodStart() != null && report.getPeriodEnd() != null) { %>
                            📅 <%= report.getPeriodStart().format(dateFormatter) %> - <%= report.getPeriodEnd().format(dateFormatter) %>
                        <% } else { %>
                            <span style="color: #95a5a6;">—</span>
                        <% } %>
                    </span>
                </div>
                <div class="info-item">
                    <span class="info-label">Создан:</span>
                    <span class="info-value">
                        <% if (report.getCreatedDate() != null) { %>
                            📅 <%= report.getCreatedDate().format(datetimeFormatter) %>
                        <% } else { %>
                            <span style="color: #95a5a6;">—</span>
                        <% } %>
                    </span>
                </div>
                <div class="info-item">
                    <span class="info-label">Автор:</span>
                    <span class="info-value">👤 <%= report.getCreatedBy() != null ? report.getCreatedBy().getUsername() : "—" %></span>
                </div>
            </div>
        </div>

        <h3>📈 Данные отчета</h3>

        <% if (reportItems != null && !reportItems.isEmpty()) { %>
        <table>
            <thead>
            <tr>
                <th>Тип работ</th>
                <th>Годовой план</th>
                <th>Квартальный план</th>
                <th>Факт за месяц</th>
                <th>Накопленный факт</th>
                <th>% выполнения годового</th>
                <th>% выполнения квартального</th>
            </tr>
            </thead>
            <tbody>
            <%
                double totalAnnualPlan = 0;
                double totalQuarterPlan = 0;
                double totalMonthFact = 0;
                double totalCumulativeFact = 0;

                for (ReportItem item : reportItems) {
                    totalAnnualPlan += item.getAnnualPlan() != null ? item.getAnnualPlan() : 0;
                    totalQuarterPlan += item.getQuarterPlan() != null ? item.getQuarterPlan() : 0;
                    totalMonthFact += item.getMonthFact() != null ? item.getMonthFact() : 0;
                    totalCumulativeFact += item.getCumulativeFact() != null ? item.getCumulativeFact() : 0;
            %>
            <tr>
                <td>🔩 <%= item.getWorkType() != null ? item.getWorkType().getName() : "—" %></td>
                <td><%= String.format("%.2f", item.getAnnualPlan() != null ? item.getAnnualPlan() : 0) %></td>
                <td><%= String.format("%.2f", item.getQuarterPlan() != null ? item.getQuarterPlan() : 0) %></td>
                <td><%= String.format("%.2f", item.getMonthFact() != null ? item.getMonthFact() : 0) %></td>
                <td><%= String.format("%.2f", item.getCumulativeFact() != null ? item.getCumulativeFact() : 0) %></td>
                <td class="percentage-cell
                    <% if (item.getAnnualPercentage() != null) {
                         if (item.getAnnualPercentage() >= 80) { %>percentage-high
                    <% } else if (item.getAnnualPercentage() >= 50) { %>percentage-medium
                    <% } else { %>percentage-low<% } } %>">
                    <%= item.getAnnualPercentage() != null ? String.format("%.1f%%", item.getAnnualPercentage()) : "—" %>
                </td>
                <td class="percentage-cell
                    <% if (item.getQuarterPercentage() != null) {
                         if (item.getQuarterPercentage() >= 80) { %>percentage-high
                    <% } else if (item.getQuarterPercentage() >= 50) { %>percentage-medium
                    <% } else { %>percentage-low<% } } %>">
                    <%= item.getQuarterPercentage() != null ? String.format("%.1f%%", item.getQuarterPercentage()) : "—" %>
                </td>
            </tr>
            <% } %>
            <tr class="total-row">
                <td><strong>📊 ИТОГО:</strong></td>
                <td><strong><%= String.format("%.2f", totalAnnualPlan) %></strong></td>
                <td><strong><%= String.format("%.2f", totalQuarterPlan) %></strong></td>
                <td><strong><%= String.format("%.2f", totalMonthFact) %></strong></td>
                <td><strong><%= String.format("%.2f", totalCumulativeFact) %></strong></td>
                <td><strong>
                    <%
                        double totalAnnualPercentage = totalAnnualPlan > 0 ? (totalCumulativeFact / totalAnnualPlan) * 100 : 0;
                        String annualPercentageClass = "percentage-high";
                        if (totalAnnualPercentage < 80) annualPercentageClass = "percentage-medium";
                        if (totalAnnualPercentage < 50) annualPercentageClass = "percentage-low";
                    %>
                    <span class="<%= annualPercentageClass %>"><%= String.format("%.1f%%", totalAnnualPercentage) %></span>
                </strong></td>
                <td><strong>
                    <%
                        double totalQuarterPercentage = totalQuarterPlan > 0 ? (totalCumulativeFact / totalQuarterPlan) * 100 : 0;
                        String quarterPercentageClass = "percentage-high";
                        if (totalQuarterPercentage < 80) quarterPercentageClass = "percentage-medium";
                        if (totalQuarterPercentage < 50) quarterPercentageClass = "percentage-low";
                    %>
                    <span class="<%= quarterPercentageClass %>"><%= String.format("%.1f%%", totalQuarterPercentage) %></span>
                </strong></td>
            </tr>
            </tbody>
        </table>
        <% } else { %>
        <div style="text-align: center; padding: 40px; color: #7f8c8d;">
            <h3>📭 Данные отчета не найдены</h3>
            <p>Отчет не содержит данных для отображения</p>
        </div>
        <% } %>

        <div class="actions">
            <a href="<%= request.getContextPath() %>/reports" class="btn btn-secondary">
                ← Вернуться к списку
            </a>
        </div>
    </div>
</div>

<script>
    // Автоматическое скрытие сообщений через 5 секунд
    setTimeout(function() {
        const messages = document.querySelectorAll('.success-message, .error-message');
        messages.forEach(function(message) {
            message.style.transition = 'opacity 0.5s ease';
            message.style.opacity = '0';
            setTimeout(function() {
                message.remove();
            }, 500);
        });
    }, 5000);
</script>
</body>
</html>