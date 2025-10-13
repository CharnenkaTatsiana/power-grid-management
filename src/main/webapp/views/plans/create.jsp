<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.powergrid.management.model.*" %>
<%@ page import="com.powergrid.management.dto.ReferenceItemDTO" %>
<%@ page import="java.util.*" %>
<%
  User user = (User) request.getAttribute("user");
  List<ReferenceItemDTO> workTypes = (List<ReferenceItemDTO>) request.getAttribute("workTypes");
  List<Map<String, Object>> branches = (List<Map<String, Object>>) request.getAttribute("branches");
  Integer currentYear = (Integer) request.getAttribute("currentYear");

  if (user == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
  }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Создание плана - Power Grid Management</title>
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
      max-width: 1400px;
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

    .header p {
      opacity: 0.8;
      font-size: 1.1em;
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

    .btn-success {
      background: #27ae60;
    }

    .btn-success:hover {
      background: #219a52;
    }

    .btn-secondary {
      background: #95a5a6;
    }

    .btn-secondary:hover {
      background: #7f8c8d;
    }

    .form-container {
      background: #f8f9fa;
      padding: 30px;
      border-radius: 8px;
      margin-bottom: 30px;
    }

    .form-group {
      margin-bottom: 20px;
    }

    .form-group label {
      display: block;
      margin-bottom: 8px;
      font-weight: 600;
      color: #2c3e50;
    }

    .form-control {
      width: 100%;
      padding: 12px 15px;
      border: 2px solid #e9ecef;
      border-radius: 5px;
      font-size: 14px;
      transition: border-color 0.3s;
    }

    .form-control:focus {
      outline: none;
      border-color: #3498db;
      box-shadow: 0 0 5px rgba(52, 152, 219, 0.3);
    }

    .form-control.select-branch {
      font-size: 13px;
    }

    .branch-option {
      padding: 8px 12px;
    }

    .branch-hierarchy {
      font-size: 12px;
      color: #7f8c8d;
      margin-left: 5px;
    }

    .work-types-table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
      background: white;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .work-types-table th {
      background: #34495e;
      color: white;
      padding: 15px;
      text-align: left;
      font-weight: 600;
    }

    .work-types-table td {
      padding: 12px 15px;
      border-bottom: 1px solid #ecf0f1;
    }

    .work-types-table tr:hover {
      background: #f8f9fa;
    }

    .quarter-input {
      width: 90px;
      padding: 8px;
      border: 1px solid #ddd;
      border-radius: 4px;
      text-align: right;
      font-size: 14px;
    }

    .quarter-input:focus {
      outline: none;
      border-color: #3498db;
      box-shadow: 0 0 3px rgba(52, 152, 219, 0.3);
    }

    .actions {
      display: flex;
      gap: 15px;
      margin-top: 30px;
      flex-wrap: wrap;
    }

    .required {
      color: #e74c3c;
    }

    .info-note {
      background: #e8f4fd;
      color: #2c3e50;
      padding: 15px;
      border-radius: 5px;
      margin-bottom: 20px;
      border-left: 4px solid #3498db;
    }

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

    .empty-state {
      text-align: center;
      padding: 40px;
      color: #7f8c8d;
    }

    .empty-state h3 {
      margin-bottom: 10px;
      color: #34495e;
    }

    .work-type-info {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .work-type-name {
      font-weight: 500;
      color: #2c3e50;
    }

    .work-type-id {
      font-size: 0.85em;
      color: #7f8c8d;
    }

    .quarter-total {
      font-weight: 600;
      color: #2c3e50;
    }

    .calculation-hint {
      font-size: 12px;
      color: #7f8c8d;
      margin-top: 10px;
      text-align: center;
    }
  </style>
</head>
<body>
<div class="container">
  <div class="header">
    <h1>📋 Создание плана ремонтов</h1>
    <p>Создание годового плана ремонтных работ энергосетей</p>
  </div>

  <div class="user-info">
    <div>
      <strong>Пользователь:</strong> <%= user.getFullName() %> (<%= user.getUsername() %>)
    </div>
    <div>
      <a href="<%= request.getContextPath() %>/plans" class="btn btn-secondary">← Назад к списку</a>
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

    <div class="info-note">
      <strong>💡 Информация:</strong> При создании плана автоматически генерируются формы месячных отчетов для заполнения инженерами.
      Годовой план рассчитывается автоматически как сумма квартальных планов.
    </div>

    <form action="<%= request.getContextPath() %>/plans" method="post">
      <input type="hidden" name="action" value="create">

      <div class="form-container">
        <h3>📊 Основные параметры плана</h3>

        <div class="form-group">
          <label for="year">🗓️ Год плана <span class="required">*</span></label>
          <input type="number"
                 id="year"
                 name="year"
                 value="<%= currentYear != null ? currentYear : "" %>"
                 min="2020"
                 max="2030"
                 class="form-control"
                 required>
        </div>

        <div class="form-group">
          <label for="branchId">🏢 Филиал <span class="required">*</span></label>
          <select id="branchId" name="branchId" class="form-control select-branch" required>
            <option value="">-- Выберите филиал --</option>
            <%
              if (branches != null && !branches.isEmpty()) {
                for (Map<String, Object> branch : branches) {
                  Long branchId = (Long) branch.get("id");
                  String branchName = (String) branch.get("name");
                  String enterpriseName = (String) branch.get("enterpriseName");
                  String associationName = (String) branch.get("associationName");
            %>
            <option value="<%= branchId != null ? branchId : "" %>" class="branch-option">
              <%= branchName != null ? branchName : "" %>
              <span class="branch-hierarchy">
                <% if (enterpriseName != null && !enterpriseName.isEmpty()) { %>
                  → <%= enterpriseName %>
                  <% if (associationName != null && !associationName.isEmpty()) { %>
                    → <%= associationName %>
                  <% } %>
                <% } %>
              </span>
            </option>
            <%
                }
              }
            %>
          </select>
          <div style="margin-top: 5px; font-size: 12px; color: #7f8c8d;">
            💡 Иерархия: Филиал → РУП-облэнерго → ГПО "Белэнерго"
          </div>
        </div>
      </div>

      <div class="form-container">
        <h3>🔧 Позиции плана</h3>
        <p style="margin-bottom: 15px; color: #7f8c8d;">
          Укажите плановые объемы работ по кварталам. Годовой план рассчитается автоматически.
        </p>

        <% if (workTypes != null && !workTypes.isEmpty()) { %>
        <div class="table-responsive">
          <table class="work-types-table">
            <thead>
            <tr>
              <th style="width: 40%;">Тип работ</th>
              <th style="width: 15%;">Q1</th>
              <th style="width: 15%;">Q2</th>
              <th style="width: 15%;">Q3</th>
              <th style="width: 15%;">Q4</th>
              <th style="width: 15%;">Годовой (авто)</th>
            </tr>
            </thead>
            <tbody>
            <%
              for (int i = 0; i < workTypes.size(); i++) {
                ReferenceItemDTO workType = workTypes.get(i);
            %>
            <tr>
              <td>
                <input type="hidden" name="workTypeId" value="<%= workType.getId() %>">
                <div class="work-type-info">
                  <span class="work-type-name">🔩 <%= workType.getName() %></span>
                  <span class="work-type-id">ID: <%= workType.getId() %></span>
                </div>
              </td>
              <td>
                <input type="number"
                       name="q1"
                       value="0"
                       step="0.01"
                       min="0"
                       class="quarter-input q1-input"
                       placeholder="0.00"
                       data-index="<%= i %>"
                       onchange="calculateAnnual(<%= i %>)">
              </td>
              <td>
                <input type="number"
                       name="q2"
                       value="0"
                       step="0.01"
                       min="0"
                       class="quarter-input q2-input"
                       placeholder="0.00"
                       data-index="<%= i %>"
                       onchange="calculateAnnual(<%= i %>)">
              </td>
              <td>
                <input type="number"
                       name="q3"
                       value="0"
                       step="0.01"
                       min="0"
                       class="quarter-input q3-input"
                       placeholder="0.00"
                       data-index="<%= i %>"
                       onchange="calculateAnnual(<%= i %>)">
              </td>
              <td>
                <input type="number"
                       name="q4"
                       value="0"
                       step="0.01"
                       min="0"
                       class="quarter-input q4-input"
                       placeholder="0.00"
                       data-index="<%= i %>"
                       onchange="calculateAnnual(<%= i %>)">
              </td>
              <td>
                <span id="annual-<%= i %>" class="quarter-total">0.00</span>
              </td>
            </tr>
            <% } %>
            </tbody>
          </table>
        </div>

        <div class="calculation-hint">
          💡 Годовой объем рассчитывается автоматически как сумма квартальных значений
        </div>
        <% } else { %>
        <div class="empty-state">
          <h3>📭 Типы работ не найдены</h3>
          <p>Необходимо добавить типы работ в систему перед созданием плана</p>
          <div style="margin-top: 20px;">
            <a href="<%= request.getContextPath() %>/" class="btn btn-secondary">
              ← На главную
            </a>
          </div>
        </div>
        <% } %>
      </div>

      <div class="actions">
        <button type="submit" class="btn btn-success" id="submit-btn">
          💾 Создать план
        </button>
        <a href="<%= request.getContextPath() %>/plans" class="btn btn-secondary">
          ← Отмена
        </a>
      </div>
    </form>
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

  // Расчет годового плана для конкретной строки
  function calculateAnnual(index) {
    const q1 = parseFloat(document.querySelector(`.q1-input[data-index="${index}"]`).value) || 0;
    const q2 = parseFloat(document.querySelector(`.q2-input[data-index="${index}"]`).value) || 0;
    const q3 = parseFloat(document.querySelector(`.q3-input[data-index="${index}"]`).value) || 0;
    const q4 = parseFloat(document.querySelector(`.q4-input[data-index="${index}"]`).value) || 0;

    const annual = q1 + q2 + q3 + q4;
    document.getElementById(`annual-${index}`).textContent = annual.toFixed(2);
  }

  // Валидация числовых полей
  document.addEventListener('DOMContentLoaded', function() {
    const numberInputs = document.querySelectorAll('.quarter-input');
    numberInputs.forEach(function(input) {
      input.addEventListener('blur', function() {
        if (this.value < 0) {
          this.value = 0;
          const event = new Event('change');
          this.dispatchEvent(event);
        }
        if (this.value === '') {
          this.value = 0;
          const event = new Event('change');
          this.dispatchEvent(event);
        }
      });
    });

    // Валидация формы перед отправкой
    document.querySelector('form').addEventListener('submit', function(e) {
      const branchSelect = document.getElementById('branchId');
      if (!branchSelect.value) {
        e.preventDefault();
        alert('⚠️ Пожалуйста, выберите филиал для создания плана');
        branchSelect.focus();
        return;
      }

      // Проверяем, что есть хотя бы одно ненулевое значение
      const hasNonZeroValue = Array.from(document.querySelectorAll('.quarter-input'))
              .some(input => parseFloat(input.value) > 0);

      if (!hasNonZeroValue) {
        e.preventDefault();
        if (!confirm('⚠️ Все значения равны нулю. Вы уверены, что хотите создать пустой план?')) {
          return;
        }
      }
    });
  });

  // Подсветка выбранного филиала
  document.addEventListener('DOMContentLoaded', function() {
    const branchSelect = document.getElementById('branchId');
    if (branchSelect) {
      branchSelect.addEventListener('change', function() {
        const selectedOption = this.options[this.selectedIndex];
        if (selectedOption && selectedOption.value) {
          console.log('Выбран филиал:', selectedOption.textContent);
        }
      });
    }
  });
</script>
</body>
</html>