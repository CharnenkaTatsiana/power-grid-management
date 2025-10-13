<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход в систему - Power Grid</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="container">
    <div class="auth-container">
        <div class="auth-header">
            <h1>⚡ Power Grid System</h1>
            <p>Вход в систему управления</p>
        </div>

        <div class="auth-card">
            <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-error">
                <%
                    String error = request.getParameter("error");
                    if ("auth".equals(error)) {
                        out.print("Неверное имя пользователя или пароль");
                    } else {
                        out.print("Ошибка авторизации");
                    }
                %>
            </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/auth/login" method="post">
                <div class="form-group">
                    <label for="username">Имя пользователя:</label>
                    <input type="text" id="username" name="username"
                           value="${param.username}" required autofocus>
                </div>

                <div class="form-group">
                    <label for="password">Пароль:</label>
                    <input type="password" id="password" name="password" required>
                </div>

                <button type="submit" class="btn-primary">Войти в систему</button>
            </form>

            <div class="auth-links">
                <a href="${pageContext.request.contextPath}/">← На главную</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>