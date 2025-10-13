<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Проверяем, если пользователь уже авторизован, перенаправляем на планы
    if (session != null && session.getAttribute("username") != null) {
        response.sendRedirect(request.getContextPath() + "/plans");
        return;
    }

    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход в систему - Power Grid Management</title>
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
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .login-container {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 400px;
        }

        .login-header {
            text-align: center;
            margin-bottom: 30px;
        }

        .login-header h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 24px;
        }

        .login-header p {
            color: #666;
            font-size: 14px;
        }

        .form-group {
            margin-bottom: 20px;
            position: relative;
        }

        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #333;
            font-weight: 500;
        }

        .form-group input {
            width: 100%;
            padding: 12px;
            border: 2px solid #e1e5e9;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
            padding-right: 40px;
        }

        .form-group input:focus {
            outline: none;
            border-color: #667eea;
        }

        .password-toggle {
            position: absolute;
            right: 12px;
            top: 35px;
            background: none;
            border: none;
            cursor: pointer;
            color: #666;
            font-size: 14px;
        }

        .password-toggle:hover {
            color: #333;
        }

        .btn {
            width: 100%;
            padding: 12px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background 0.3s;
        }

        .btn:hover {
            background: #5a6fd8;
        }

        .error-message {
            background: #fee;
            color: #c33;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #fcc;
        }

        .test-credentials {
            margin-top: 20px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 5px;
            font-size: 12px;
            color: #666;
        }

        .test-credentials h4 {
            margin-bottom: 8px;
            color: #333;
        }
    </style>
</head>
<body>
<div class="login-container">
    <div class="login-header">
        <h1>Power Grid Management</h1>
        <p>Система управления ремонтами</p>
    </div>

    <% if (error != null) { %>
    <div class="error-message">
        <%= error %>
    </div>
    <% } %>

    <form action="<%= request.getContextPath() %>/login" method="post">
        <div class="form-group">
            <label for="username">Имя пользователя:</label>
            <input type="text" id="username" name="username" required value="testuser">
        </div>

        <div class="form-group">
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required value="password123">
            <button type="button" class="password-toggle" id="togglePassword">
                👁️
            </button>
        </div>

        <button type="submit" class="btn">Войти</button>
    </form>


</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const togglePassword = document.getElementById('togglePassword');
        const passwordInput = document.getElementById('password');

        togglePassword.addEventListener('click', function() {
            // Переключаем тип поля ввода
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);

            // Меняем иконку
            this.textContent = type === 'password' ? '👁️' : '🔒';
        });
    });
</script>
</body>
</html>