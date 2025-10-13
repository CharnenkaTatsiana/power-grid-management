<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.powergrid.management.model.*" %>
<%@ page import="com.powergrid.management.dto.*" %>
<%@ page import="java.util.List" %>
<%
  User user = (User) request.getAttribute("user");
  PlanDTO plan = (PlanDTO) request.getAttribute("plan");
  List<PlanItemDTO> planItems = (List<PlanItemDTO>) request.getAttribute("planItems");
  String planType = (String) request.getAttribute("planType");

  if (user == null || plan == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
  }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>План #<%= plan.getId() %> - Power Grid Management</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; padding: 20px; }
    .container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 10px; box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1); overflow: hidden; }
    .header { background: #2c3e50; color: white; padding: 30px; text-align: center; }
    .header h1 { margin-bottom: 10px; font-size: 2.5em; }
    .user-info { background: #34495e; padding: 15px 30px; color: white; display: flex; justify-content: space-between; align-items: center; }
    .content { padding: 30px; }
    .btn { display: inline-block; padding: 12px 24px; background: #3498db; color: white; text-decoration: none; border-radius: 5px; transition: background 0.3s; border: none; cursor: pointer; }
    .btn:hover { background: #2980b9; }
    .btn-success { background: #27ae60; }
    .btn-success:hover { background: #219a52; }
    .btn-warning { background: #f39c12; }
    .btn-warning:hover { background: #e67e22; }
    .btn-danger { background: #e74c3c; }
    .btn-danger:hover { background: #c0392b; }
    .btn-secondary { background: #95a5a6; }
    .btn-secondary:hover { background: #7f8c8d; }

    .plan-info { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 30px; border-left: 4px solid #3498db; }
    .plan-info h3 { margin-bottom: 15px; color: #2c3e50; }
    .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; }
    .info-item { display: flex; flex-direction: column; }
    .info-label { font-weight: 600; color: #7f8c8d; font-size: 0.9em; margin-bottom: 5px; }
    .info-value { font-size: 1.1em; color: #2c3e50; }

    .plan-type-badge {
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 0.8em;
      font-weight: bold;
      color: white;
      display: inline-block;
      margin-left: 10px;
    }
    .type-branch { background: #3498db; }
    .type-enterprise { background: #27ae60; }
    .type-association { background: #9b59b6; }

    .reports-info { background: #e8f4fd; padding: 20px; border-radius: 8px; margin-bottom: 30px; border-left: 4px solid #3498db; }
    .reports-info h3 { margin-bottom: 15px; color: #2c3e50; }

    table { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }
    th { background: #34495e; color: white; padding: 15px; text-align: left; font-weight: 600; }
    td { padding: 12px 15px; border-bottom: 1px solid #ecf0f1; }
    tr:hover { background: #f8f9fa; }
    .total-row { background: #2c3e50; color: white; font-weight: bold; }
    .total-row td { border-bottom: none; }

    .actions { display: flex; gap: 10px; margin-top: 20px; flex-wrap: wrap; }

    .success-message {
      background: #d4edda;
      color: #155724;
      padding: 12px;
      border-radius: 5px;
      margin-bottom: 20px;
      border: 1px solid #c3e6cb;
    }

    .warning-message {
      background: #fff3cd;
      color: #856404;
      padding: 12px;
      border-radius: 5px;
      margin-bottom: 20px;
      border: 1px solid #ffeaa7;
    }

    .error-message {
      background: #f8d7da;
      color: #721c24;
      padding: 12px;
      border-radius: 5px;
      margin-bottom: 20px;
      border: 1px solid #f5c6cb;
    }

    .auto-generated-note {
      background: #e8f6f3;
      color: #1d6fa5;
      padding: 15px;
      border-radius: 5px;
      margin-bottom: 20px;
      border-left: 4px solid #1abc9c;
    }

    .hierarchy-path {
      font-size: 0.9em;
      color: #7f8c8d;
      margin-top: 5px;
    }

    .quarter-section {
      margin-bottom: 10px;
    }
    .quarter-label {
      font-weight: 600;
      color: #2c3e50;
      margin-bottom: 5px;
    }
    .quarter-value {
      font-size: 1.1em;
      color: #3498db;
    }
  </style>
</head>
<body>
<div class="container">
  <div class="header">
    <h1>
      📊 План ремонтов #<%= plan.getId() %>
      <%
        String typeClass = "";
        String typeText = "";
        if (plan.getBranchName() != null) {
          typeClass = "type-branch";
          typeText = "🏢 Филиал";
        } else if (plan.getEnterpriseName() != null) {
          typeClass = "type-enterprise";
          typeText = "🏭 Предприятие";
        } else if (plan.getAssociationName() != null) {
          typeClass = "type-association";
          typeText = "🏛️ Объединение";
        }
      %>
      <span class="plan-type-badge <%= typeClass %>"><%= typeText %></span>
    </h1>
    <p>Детальная информация о плане ремонтных работ</p>
  </div>

  <div class="user-info">
    <div>
      <strong>Пользователь:</strong> <%= user.getFullName() %> (<%= user.getUsername() %>)
    </div>
    <div>
      <a href="<%= request.getContextPath() %>/plans?tab=<%= planType != null ? planType : "branches" %>" class="btn btn-secondary">← Назад к списку</a>
    </div>
  </div>

  <div class="content">
    <!-- Сообщения -->
    <%
      String success = (String) request.getSession().getAttribute("success");
      String warning = (String) request.getSession().getAttribute("warning");
      String error = (String) request.getAttribute("error");

      if (success != null) {
        request.getSession().removeAttribute("success");
    %>
    <div class="success-message">
      ✅ <%= success %>
    </div>
    <% } %>

    <% if (warning != null) {
      request.getSession().removeAttribute("warning");
    %>
    <div class="warning-message">
      ⚠️ <%= warning %>
    </div>
    <% } %>

    <% if (error != null) { %>
    <div class="error-message">
      ❌ <%= error %>
    </div>
    <% } %>

    <!-- Информация об автоматической генерации -->
    <% if (plan.getBranchName() == null) { %>
    <div class="auto-generated-note">
      <strong>ℹ️ Автоматически сгенерированный план</strong>
      <p>Этот план был автоматически создан системой на основе данных нижестоящих подразделений и не подлежит ручному редактированию.</p>
    </div>
    <% } %>

    <div class="plan-info">
      <h3>📋 Основная информация</h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">ID плана:</span>
          <span class="info-value">#<%= plan.getId() %></span>
        </div>
        <div class="info-item">
          <span class="info-label">Год:</span>
          <span class="info-value">🗓️ <%= plan.getPlanYear() %></span>
        </div>
        <% if (plan.getBranchName() != null) { %>
        <div class="info-item">
          <span class="info-label">Филиал:</span>
          <span class="info-value">🏢 <%= plan.getBranchName() %></span>
        </div>
        <% } %>
        <% if (plan.getEnterpriseName() != null) { %>
        <div class="info-item">
          <span class="info-label">Предприятие:</span>
          <span class="info-value">🏭 <%= plan.getEnterpriseName() %></span>
        </div>
        <% } %>
        <% if (plan.getAssociationName() != null) { %>
        <div class="info-item">
          <span class="info-label">Объединение:</span>
          <span class="info-value">🏛️ <%= plan.getAssociationName() %></span>
        </div>
        <% } %>
        <div class="info-item">
          <span class="info-label">Создан:</span>
          <span class="info-value">
            <% if (plan.getCreatedDate() != null) { %>
              📅 <%= plan.getCreatedDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) %>
            <% } else { %>
              —
            <% } %>
          </span>
        </div>
        <div class="info-item">
          <span class="info-label">Автор:</span>
          <span class="info-value">👤 <%= plan.getCreatedBy() != null ? plan.getCreatedBy() : "—" %></span>
        </div>
      </div>

      <!-- Иерархия -->
      <% if (plan.getBranchName() != null || plan.getEnterpriseName() != null || plan.getAssociationName() != null) { %>
      <div class="hierarchy-path" style="margin-top: 15px; padding-top: 15px; border-top: 1px solid #e9ecef;">
        <strong>Иерархия:</strong>
        <span>
          <%
            List<String> hierarchy = new java.util.ArrayList<>();
            if (plan.getBranchName() != null) hierarchy.add("🏢 " + plan.getBranchName());
            if (plan.getEnterpriseName() != null) hierarchy.add("🏭 " + plan.getEnterpriseName());
            if (plan.getAssociationName() != null) hierarchy.add("🏛️ " + plan.getAssociationName());
          %>
          <%= String.join(" → ", hierarchy) %>
        </span>
      </div>
      <% } %>
    </div>

    <!-- Сводка по кварталам -->
    <% if (planItems != null && !planItems.isEmpty()) {
      double totalQ1 = 0;
      double totalQ2 = 0;
      double totalQ3 = 0;
      double totalQ4 = 0;
      double totalAnnual = 0;

      for (PlanItemDTO item : planItems) {
        totalQ1 += item.getQ1Plan() != null ? item.getQ1Plan() : 0;
        totalQ2 += item.getQ2Plan() != null ? item.getQ2Plan() : 0;
        totalQ3 += item.getQ3Plan() != null ? item.getQ3Plan() : 0;
        totalQ4 += item.getQ4Plan() != null ? item.getQ4Plan() : 0;
        totalAnnual += item.getAnnualPlan() != null ? item.getAnnualPlan() : 0;
      }
    %>
    <div class="plan-info">
      <h3>📈 Сводка по кварталам</h3>
      <div class="info-grid">
        <div class="info-item">
          <div class="quarter-section">
            <div class="quarter-label">1 квартал (Q1)</div>
            <div class="quarter-value"><%= String.format("%.2f", totalQ1) %></div>
          </div>
        </div>
        <div class="info-item">
          <div class="quarter-section">
            <div class="quarter-label">2 квартал (Q2)</div>
            <div class="quarter-value"><%= String.format("%.2f", totalQ2) %></div>
          </div>
        </div>
        <div class="info-item">
          <div class="quarter-section">
            <div class="quarter-label">3 квартал (Q3)</div>
            <div class="quarter-value"><%= String.format("%.2f", totalQ3) %></div>
          </div>
        </div>
        <div class="info-item">
          <div class="quarter-section">
            <div class="quarter-label">4 квартал (Q4)</div>
            <div class="quarter-value"><%= String.format("%.2f", totalQ4) %></div>
          </div>
        </div>
        <div class="info-item">
          <div class="quarter-section">
            <div class="quarter-label" style="color: #2c3e50; font-size: 1.1em;">Годовой итог</div>
            <div class="quarter-value" style="color: #27ae60; font-size: 1.3em; font-weight: bold;">
              <%= String.format("%.2f", totalAnnual) %>
            </div>
          </div>
        </div>
      </div>
    </div>
    <% } %>

    <h3>🔧 Позиции плана</h3>

    <% if (planItems != null && !planItems.isEmpty()) { %>
    <table>
      <thead>
      <tr>
        <th>Тип работ</th>
        <th>Q1</th>
        <th>Q2</th>
        <th>Q3</th>
        <th>Q4</th>
        <th>Годовой объем</th>
      </tr>
      </thead>
      <tbody>
      <%
        double totalQ1 = 0;
        double totalQ2 = 0;
        double totalQ3 = 0;
        double totalQ4 = 0;
        double totalAnnual = 0;

        for (PlanItemDTO item : planItems) {
          totalQ1 += item.getQ1Plan() != null ? item.getQ1Plan() : 0;
          totalQ2 += item.getQ2Plan() != null ? item.getQ2Plan() : 0;
          totalQ3 += item.getQ3Plan() != null ? item.getQ3Plan() : 0;
          totalQ4 += item.getQ4Plan() != null ? item.getQ4Plan() : 0;
          totalAnnual += item.getAnnualPlan() != null ? item.getAnnualPlan() : 0;
      %>
      <tr>
        <td>🔩 <%= item.getWorkTypeName() != null ? item.getWorkTypeName() : "—" %></td>
        <td><%= String.format("%.2f", item.getQ1Plan() != null ? item.getQ1Plan() : 0) %></td>
        <td><%= String.format("%.2f", item.getQ2Plan() != null ? item.getQ2Plan() : 0) %></td>
        <td><%= String.format("%.2f", item.getQ3Plan() != null ? item.getQ3Plan() : 0) %></td>
        <td><%= String.format("%.2f", item.getQ4Plan() != null ? item.getQ4Plan() : 0) %></td>
        <td><strong><%= String.format("%.2f", item.getAnnualPlan() != null ? item.getAnnualPlan() : 0) %></strong></td>
      </tr>
      <% } %>
      <tr class="total-row">
        <td><strong>📊 ИТОГО:</strong></td>
        <td><strong><%= String.format("%.2f", totalQ1) %></strong></td>
        <td><strong><%= String.format("%.2f", totalQ2) %></strong></td>
        <td><strong><%= String.format("%.2f", totalQ3) %></strong></td>
        <td><strong><%= String.format("%.2f", totalQ4) %></strong></td>
        <td><strong><%= String.format("%.2f", totalAnnual) %></strong></td>
      </tr>
      </tbody>
    </table>
    <% } else { %>
    <div style="text-align: center; padding: 40px; color: #7f8c8d;">
      <h3>📭 Позиции плана не найдены</h3>
      <p>Добавьте позиции для отображения детальной информации</p>
    </div>
    <% } %>

    <div class="actions">
      <!-- Кнопки для планов филиалов (ручное редактирование) -->
      <% if (plan.getBranchName() != null) { %>
      <a href="<%= request.getContextPath() %>/plans?action=edit&id=<%= plan.getId() %>" class="btn">
        ✏️ Редактировать план
      </a>

      <form action="<%= request.getContextPath() %>/plans" method="post" style="display: inline;">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="id" value="<%= plan.getId() %>">
        <button type="submit" class="btn btn-danger"
                onclick="return confirm('Удалить план #<%= plan.getId() %>? Все связанные отчеты также будут удалены.')">
          🗑️ Удалить
        </button>
      </form>
      <% } else { %>
      <!-- Информация для автоматически сгенерированных планов -->
      <span class="btn btn-secondary" style="background: #95a5a6; cursor: default;">
          🔄 Автоматически генерируется
        </span>
      <% } %>

      <!-- Кнопка возврата с учетом активной вкладки -->
      <a href="<%= request.getContextPath() %>/plans?tab=<%= planType != null ? planType : "branches" %>" class="btn btn-secondary">
        ← Вернуться к списку
      </a>

      <!-- Ссылка на отчеты если это план филиала -->
      <% if (plan.getBranchName() != null) { %>
      <a href="<%= request.getContextPath() %>/reports?branchId=<%= plan.getBranchName() %>&year=<%= plan.getPlanYear() %>"
         class="btn btn-warning">
        📊 Смотреть отчеты
      </a>
      <% } %>
    </div>
  </div>
</div>

<script>
  // Автоматическое скрытие сообщений через 5 секунд
  setTimeout(function() {
    const messages = document.querySelectorAll('.success-message, .warning-message, .error-message');
    messages.forEach(function(message) {
      message.style.transition = 'opacity 0.5s ease';
      message.style.opacity = '0';
      setTimeout(function() {
        message.remove();
      }, 500);
    });
  }, 5000);

  // Подтверждение удаления с дополнительной информацией
  document.addEventListener('DOMContentLoaded', function() {
    const deleteButtons = document.querySelectorAll('button[onclick*="confirm"]');
    deleteButtons.forEach(function(button) {
      button.addEventListener('click', function(e) {
        if (!confirm(this.getAttribute('onclick').match(/return confirm\('([^']+)'\)/)[1])) {
          e.preventDefault();
        }
      });
    });
  });
</script>
</body>
</html>