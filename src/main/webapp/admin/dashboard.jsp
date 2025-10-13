<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.powergrid.management.model.User" %>
<%@ page import="com.powergrid.management.model.Role" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !currentUser.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    List<User> users = (List<User>) request.getAttribute("users");

    // Вычисляем статистику
    int activeCount = 0;
    int adminCount = 0;
    int totalUsers = 0;

    if (users != null) {
        totalUsers = users.size();
        for (User user : users) {
            if (user.isActive()) activeCount++;
            if (user.isAdmin()) adminCount++;
        }
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Админ-панель - Управление пользователями</title>
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
        }

        .header {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header h1 {
            color: #333;
            margin: 0;
            font-size: 28px;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 15px;
            font-size: 14px;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            text-align: center;
            font-size: 14px;
            transition: all 0.3s ease;
            font-weight: 500;
        }

        .btn-primary {
            background: #007bff;
            color: white;
        }

        .btn-primary:hover {
            background: #0056b3;
            transform: translateY(-2px);
        }

        .btn-success {
            background: #28a745;
            color: white;
        }

        .btn-success:hover {
            background: #1e7e34;
            transform: translateY(-2px);
        }

        .btn-warning {
            background: #ffc107;
            color: #212529;
        }

        .btn-warning:hover {
            background: #e0a800;
            transform: translateY(-2px);
        }

        .btn-danger {
            background: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background: #c82333;
            transform: translateY(-2px);
        }

        .btn-info {
            background: #17a2b8;
            color: white;
        }

        .btn-info:hover {
            background: #138496;
            transform: translateY(-2px);
        }

        .btn-logout {
            background: #6c757d;
            color: white;
        }

        .btn-logout:hover {
            background: #545b62;
            transform: translateY(-2px);
        }

        .card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            padding: 25px;
            margin-bottom: 20px;
        }

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f8f9fa;
        }

        .card-header h2 {
            color: #333;
            margin: 0;
            font-size: 22px;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 25px;
        }

        .stat-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
        }

        .stat-number {
            font-size: 32px;
            font-weight: bold;
            margin: 10px 0;
        }

        .stat-label {
            font-size: 14px;
            opacity: 0.9;
        }

        .table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .table th,
        .table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }

        .table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #495057;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .table tr:hover {
            background: #f8f9fa;
            transition: background 0.2s ease;
        }

        .status-active {
            color: #28a745;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 5px;
        }

        .status-active::before {
            content: "●";
            font-size: 12px;
        }

        .status-inactive {
            color: #dc3545;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 5px;
        }

        .status-inactive::before {
            content: "●";
            font-size: 12px;
        }

        .role-badge {
            display: inline-block;
            padding: 4px 8px;
            background: #e9ecef;
            border-radius: 4px;
            font-size: 11px;
            margin: 2px;
            font-weight: 500;
        }

        .role-admin {
            background: #dc3545;
            color: white;
        }

        .role-manager {
            background: #fd7e14;
            color: white;
        }

        .role-engineer {
            background: #20c997;
            color: white;
        }

        .role-viewer {
            background: #6c757d;
            color: white;
        }

        .alert {
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            border-left: 4px solid;
            animation: slideIn 0.3s ease;
        }

        @keyframes slideIn {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .alert-success {
            background: #d4edda;
            color: #155724;
            border-color: #c3e6cb;
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border-color: #f5c6cb;
        }

        .alert-info {
            background: #d1ecf1;
            color: #0c5460;
            border-color: #bee5eb;
        }

        .actions {
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }

        .actions .btn {
            padding: 6px 12px;
            font-size: 12px;
            border-radius: 4px;
        }

        .password-strength {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 10px;
            font-size: 10px;
            font-weight: bold;
            text-transform: uppercase;
        }

        .strength-weak { background: #dc3545; color: white; }
        .strength-medium { background: #ffc107; color: #212529; }
        .strength-strong { background: #28a745; color: white; }
        .strength-very-strong { background: #20c997; color: white; }

        .quick-actions {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .search-box {
            margin-bottom: 20px;
        }

        .search-input {
            width: 100%;
            max-width: 400px;
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }

        .search-input:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
        }

        .last-login {
            font-size: 11px;
            color: #6c757d;
        }

        @media (max-width: 768px) {
            .header {
                flex-direction: column;
                gap: 15px;
                text-align: center;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .table {
                font-size: 12px;
            }

            .table th,
            .table td {
                padding: 8px 10px;
            }

            .actions {
                flex-direction: column;
            }

            .quick-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>🚀 Админ-панель системы</h1>
        <div class="user-info">
            <span>Добро пожаловать, <strong><%= currentUser.getFullName() %></strong></span>
            <a href="<%= request.getContextPath() %>/logout" class="btn btn-logout">🚪 Выйти</a>
        </div>
    </div>

    <%-- Сообщения об успехе/ошибке --%>
    <% if (request.getAttribute("success") != null) { %>
    <div class="alert alert-success">
        ✅ <%= request.getAttribute("success") %>
    </div>
    <% } %>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error">
        ❌ <%= request.getAttribute("error") %>
    </div>
    <% } %>

    <% if (request.getAttribute("info") != null) { %>
    <div class="alert alert-info">
        ℹ️ <%= request.getAttribute("info") %>
    </div>
    <% } %>

    <%-- Статистика --%>
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Всего пользователей</div>
            <div class="stat-number"><%= totalUsers %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Активных</div>
            <div class="stat-number"><%= activeCount %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Администраторов</div>
            <div class="stat-number"><%= adminCount %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Последний вход</div>
            <div class="stat-number">
                <%= currentUser.getLastLogin() != null ?
                        new java.text.SimpleDateFormat("dd.MM.yyyy").format(currentUser.getLastLogin()) : "—" %>
            </div>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            <h2>👥 Управление пользователями</h2>
            <div class="quick-actions">
                <a href="<%= request.getContextPath() %>/admin/dashboard?action=new" class="btn btn-success">
                    ➕ Добавить пользователя
                </a>
            </div>
        </div>

        <%-- Поиск --%>
        <div class="search-box">
            <input type="text" class="search-input" placeholder="🔍 Поиск пользователей..."
                   onkeyup="filterUsers(this.value)">
        </div>

        <table class="table" id="usersTable">
            <thead>
            <tr>
                <th>ID</th>
                <th>Логин</th>
                <th>ФИО</th>
                <th>Email</th>
                <th>Роли</th>
                <th>Статус</th>
                <th>Дата создания</th>
                <th>Последний вход</th>
                <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            <% if (users != null && !users.isEmpty()) { %>
            <% for (User user : users) {
                String roleClass = "";
                if (user.isAdmin()) roleClass = "role-admin";
                else if (user.hasRole(Role.ASSOCIATION_MANAGER) || user.hasRole(Role.ENTERPRISE_MANAGER))
                    roleClass = "role-manager";
                else if (user.hasRole(Role.ASSOCIATION_ENGINEER) || user.hasRole(Role.ENTERPRISE_ENGINEER) || user.hasRole(Role.BRANCH_ENGINEER))
                    roleClass = "role-engineer";
                else roleClass = "role-viewer";
            %>
            <tr class="user-row">
                <td><%= user.getId() %></td>
                <td>
                    <strong><%= user.getUsername() %></strong>
                    <% if (user.getId().equals(currentUser.getId())) { %>
                    <span style="color: #007bff; font-size: 10px;">(Вы)</span>
                    <% } %>
                </td>
                <td><%= user.getFullName() %></td>
                <td><%= user.getEmail() != null ? user.getEmail() : "—" %></td>
                <td>
                    <% if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                        for (Role role : user.getRoles()) { %>
                    <span class="role-badge <%= roleClass %>"><%= role.name() %></span>
                    <% } } else { %>
                    <span style="color: #6c757d; font-size: 12px;">—</span>
                    <% } %>
                </td>
                <td>
                                    <span class="<%= user.isActive() ? "status-active" : "status-inactive" %>">
                                        <%= user.isActive() ? "Активен" : "Неактивен" %>
                                    </span>
                </td>
                <td>
                    <%= user.getCreatedAt() != null ?
                            new java.text.SimpleDateFormat("dd.MM.yyyy").format(user.getCreatedAt()) : "—" %>
                </td>
                <td>
                    <% if (user.getLastLogin() != null) { %>
                    <div><%= new java.text.SimpleDateFormat("dd.MM.yyyy").format(user.getLastLogin()) %></div>
                    <div class="last-login"><%= new java.text.SimpleDateFormat("HH:mm").format(user.getLastLogin()) %></div>
                    <% } else { %>
                    <span style="color: #6c757d;">—</span>
                    <% } %>
                </td>
                <td class="actions">
                    <a href="<%= request.getContextPath() %>/admin/dashboard?action=edit&id=<%= user.getId() %>"
                       class="btn btn-primary" title="Редактировать">✏️</a>

                    <% if (!user.getId().equals(currentUser.getId())) { %>
                    <a href="<%= request.getContextPath() %>/admin/dashboard?action=resetPassword&id=<%= user.getId() %>"
                       class="btn btn-warning"
                       onclick="return confirm('Сбросить пароль для пользователя <%= user.getUsername() %>? Будет сгенерирован новый пароль.')"
                       title="Сбросить пароль">🔑</a>

                    <% if (user.isActive()) { %>
                    <a href="<%= request.getContextPath() %>/admin/dashboard?action=deactivate&id=<%= user.getId() %>"
                       class="btn btn-info"
                       onclick="return confirm('Деактивировать пользователя <%= user.getUsername() %>?')"
                       title="Деактивировать">⏸️</a>
                    <% } else { %>
                    <a href="<%= request.getContextPath() %>/admin/dashboard?action=activate&id=<%= user.getId() %>"
                       class="btn btn-info"
                       onclick="return confirm('Активировать пользователя <%= user.getUsername() %>?')"
                       title="Активировать">▶️</a>
                    <% } %>

                    <a href="<%= request.getContextPath() %>/admin/dashboard?action=delete&id=<%= user.getId() %>"
                       class="btn btn-danger"
                       onclick="return confirm('Вы уверены, что хотите удалить пользователя <%= user.getUsername() %>?')"
                       title="Удалить">🗑️</a>
                    <% } else { %>
                    <span style="color: #6c757d; font-size: 11px;">Текущий пользователь</span>
                    <% } %>
                </td>
            </tr>
            <% } %>
            <% } else { %>
            <tr>
                <td colspan="9" style="text-align: center; color: #6c757d; padding: 40px;">
                    <div style="font-size: 48px; margin-bottom: 10px;">👥</div>
                    <div>Нет пользователей в системе</div>
                    <a href="<%= request.getContextPath() %>/admin/dashboard?action=new"
                       class="btn btn-success" style="margin-top: 15px;">
                        ➕ Добавить первого пользователя
                    </a>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>

<script>
    // Фильтрация пользователей
    function filterUsers(searchText) {
        const rows = document.querySelectorAll('.user-row');
        const searchLower = searchText.toLowerCase();

        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(searchLower) ? '' : 'none';
        });
    }

    // Автофокус на поиске
    document.addEventListener('DOMContentLoaded', function() {
        const searchInput = document.querySelector('.search-input');
        if (searchInput) {
            searchInput.focus();
        }
    });
</script>
</body>
</html>