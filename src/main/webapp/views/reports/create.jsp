<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.powergrid.management.model.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDateTime" %>
<%
  User user = (User) request.getAttribute("user");
  List<Branch> branches = (List<Branch>) request.getAttribute("branches");
  LocalDateTime currentDate = (LocalDateTime) request.getAttribute("currentDate");

  if (user == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Создание отчета - Power Grid Management</title>
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      min-height: 100vh;
      padding: 20px;
    }

    .container {
      max-width: 800px;
      margin: 0 auto;
      background: white;
      border-radius: 10px;
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
      overflow: hidden;
    }

    .header {
      background: #2c3e50;
      color: white;
      padding: 30px;
      text-align: center;
    }

    .header h1 {
      margin-bottom: 10px;
      font-size: 2.5em;
    }

    .user-info {
      background: #34495e;
      padding: 15px 30px;
      color: white;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .content {
      padding: 30px;
    }

    .btn {
      display: inline-block;
      padding: 12px 24px;
      background: #3498db;
      color: white;
      text-decoration: none;
      border-radius: 5px;
      transition: background 0.3s;
      border: none;
      cursor: pointer;
      font-size: 14px;
    }

    .btn:hover {
      background: #2980b9;
    }

    .btn-secondary {
      background: #95a5a6;
    }

    .btn-secondary:hover {
      background: #7f8c8d;
    }

    .btn-success {
      background: #27ae60;
    }

    .btn-success:hover {
      background: #219a52;
    }

    .form-group {
      margin-bottom: 20px;
    }

    .form-label {
      display: block;
      margin-bottom: 8px;
      font-weight: 600;
      color: #2c3e50;
    }

    .form-control {
      width: 100%;
      padding: 12px;
      border: 2px solid #ecf0f1;
      border-radius: 5px;
      font-size: 14px;
      transition: border-color 0.3s;
    }

    .form-control:focus {
      outline: none;
      border-color: #3498db;
    }

    .form-select {
      width: 100%;
      padding: 12px;
      border: 2px solid #ecf0f1;
      border-radius: 5px;
      font-size: 14px;
      background: white;
      cursor: pointer;
    }

    .form-select:focus {
      outline: none;
      border-color: #3498db;
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 15px;
    }

    .card {
      background: #f8f9fa;
      padding: 20px;
      border-radius: 8px;
      border-left: 4px solid #3498db;
      margin-bottom: 20px;
    }

    .card h3 {
      margin-bottom: 15px;
      color: #2c3e50;
    }

    .actions {
      display: flex;
      gap: 15px;
      margin-top: 30px;
    }

    .error-message {
      background: #e74c3c;
      color: white;
      padding: 12px;
      border-radius: 5px;
      margin-bottom: 20px;
    }

    .info-message {
      background: #3498db;
      color: white;
      padding: 12px;
      border-radius: 5px;
      margin-bottom: 20px;
    }
  </style>
</head>
<body>
<div class="container">
  <div class="header">
    <h1>📊 Создание отчета</h1>
    <p>Заполните параметры для генерации нового отчета</p>
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
    <% if (request.getAttribute("error") != null) { %>
    <div class="error-message">
      ❌ <%= request.getAttribute("error") %>
    </div>
    <% } %>

    <div class="info-message">
      ℹ️ Отчет будет сгенерирован на основе текущих данных системы
    </div>

    <form action="<%= request.getContextPath() %>/reports" method="POST">
      <input type="hidden" name="action" value="generate">

      <div class="card">
        <h3>📋 Основная информация</h3>

        <div class="form-group">
          <label class="form-label" for="title">Название отчета *</label>
          <input type="text" id="title" name="title" class="form-control"
                 placeholder="Введите название отчета" required
                 value="Отчет за <%= currentDate.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy")) %>">
        </div>

        <div class="form-group">
          <label class="form-label" for="reportType">Тип отчета *</label>
          <select id="reportType" name="reportType" class="form-select" required>
            <option value="">Выберите тип отчета</option>
            <option value="DAILY">Ежедневный</option>
            <option value="WEEKLY">Еженедельный</option>
            <option value="MONTHLY" selected>Ежемесячный</option>
            <option value="QUARTERLY">Квартальный</option>
            <option value="ANNUAL">Годовой</option>
            <option value="OPERATIONAL">Оперативный</option>
            <option value="ANALYTICAL">Аналитический</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label" for="branchId">Филиал *</label>
          <select id="branchId" name="branchId" class="form-select" required>
            <option value="">Выберите филиал</option>
            <% if (branches != null) {
              for (Branch branch : branches) { %>
            <option value="<%= branch.getId() %>"><%= branch.getName() %></option>
            <% }
            } %>
          </select>
        </div>
      </div>

      <div class="card">
        <h3>📅 Период отчета</h3>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label" for="periodStart">Начало периода</label>
            <input type="date" id="periodStart" name="periodStart" class="form-control"
                   value="<%= currentDate.toLocalDate().withDayOfMonth(1).minusMonths(1).toString() %>">
          </div>

          <div class="form-group">
            <label class="form-label" for="periodEnd">Конец периода</label>
            <input type="date" id="periodEnd" name="periodEnd" class="form-control"
                   value="<%= currentDate.toLocalDate().withDayOfMonth(1).minusDays(1).toString() %>">
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Автоматические периоды:</label>
          <div style="display: flex; gap: 10px; margin-top: 10px;">
            <button type="button" class="btn" onclick="setLastMonth()">Прошлый месяц</button>
            <button type="button" class="btn" onclick="setCurrentQuarter()">Текущий квартал</button>
            <button type="button" class="btn" onclick="setCurrentYear()">Текущий год</button>
          </div>
        </div>
      </div>

      <div class="actions">
        <button type="submit" class="btn btn-success">
          🚀 Сгенерировать отчет
        </button>
        <a href="<%= request.getContextPath() %>/reports" class="btn btn-secondary">
          ❌ Отмена
        </a>
      </div>
    </form>
  </div>
</div>

<script>
  function setLastMonth() {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth() - 1, 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth(), 0);

    document.getElementById('periodStart').value = formatDate(firstDay);
    document.getElementById('periodEnd').value = formatDate(lastDay);
  }

  function setCurrentQuarter() {
    const now = new Date();
    const quarter = Math.floor(now.getMonth() / 3);
    const firstDay = new Date(now.getFullYear(), quarter * 3, 1);
    const lastDay = new Date(now.getFullYear(), quarter * 3 + 3, 0);

    document.getElementById('periodStart').value = formatDate(firstDay);
    document.getElementById('periodEnd').value = formatDate(lastDay);
  }

  function setCurrentYear() {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), 0, 1);
    const lastDay = new Date(now.getFullYear(), 11, 31);

    document.getElementById('periodStart').value = formatDate(firstDay);
    document.getElementById('periodEnd').value = formatDate(lastDay);
  }

  function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  // Установить прошлый месяц по умолчанию
  document.addEventListener('DOMContentLoaded', function() {
    setLastMonth();
  });
</script>
</body>
