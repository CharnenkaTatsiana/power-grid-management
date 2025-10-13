<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ошибка - Power Grid Management</title>
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
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .error-container {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            width: 100%;
            text-align: center;
        }

        .error-icon {
            font-size: 4em;
            margin-bottom: 20px;
        }

        .error-code {
            font-size: 3em;
            color: #e74c3c;
            margin-bottom: 10px;
        }

        .error-title {
            font-size: 1.5em;
            color: #2c3e50;
            margin-bottom: 15px;
        }

        .error-message {
            color: #7f8c8d;
            margin-bottom: 25px;
            line-height: 1.6;
        }

        .actions {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
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

        .technical-details {
            margin-top: 20px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 5px;
            text-align: left;
            font-size: 12px;
            color: #666;
            display: none;
        }

        .toggle-details {
            background: none;
            border: none;
            color: #3498db;
            cursor: pointer;
            font-size: 12px;
            margin-top: 10px;
        }
    </style>
</head>
<body>
<div class="error-container">
    <div class="error-icon">⚠️</div>
    <div class="error-code">Ошибка</div>
    <div class="error-title">Что-то пошло не так</div>

    <div class="error-message">
        <%
            String errorMessage = (String) request.getAttribute("error");
            if (errorMessage != null) {
                out.print(errorMessage);
            } else if (exception != null) {
                out.print(exception.getMessage());
            } else {
                out.print("Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.");
            }
        %>
    </div>

    <div class="actions">
        <a href="<%= request.getContextPath() %>/plans" class="btn">📋 К планам</a>
        <a href="<%= request.getContextPath() %>/reports" class="btn btn-secondary">📊 К отчетам</a>
        <a href="<%= request.getContextPath() %>/" class="btn">🏠 На главную</a>
    </div>

    <button class="toggle-details" onclick="toggleDetails()">Показать технические детали</button>

    <div class="technical-details" id="technicalDetails">
        <strong>URI:</strong> <%= request.getRequestURI() %><br>
        <strong>Статус:</strong> <%= response.getStatus() %><br>
        <strong>Сервлет:</strong> <%= request.getAttribute("javax.servlet.error.servlet_name") %><br>
        <% if (exception != null) { %>
        <strong>Исключение:</strong> <%= exception.getClass().getName() %><br>
        <strong>Сообщение:</strong> <%= exception.getMessage() %><br>
        <strong>StackTrace:</strong><br>
        <pre><%
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            exception.printStackTrace(pw);
            out.print(sw.toString());
        %></pre>
        <% } %>
    </div>
</div>

<script>
    function toggleDetails() {
        const details = document.getElementById('technicalDetails');
        const button = document.querySelector('.toggle-details');

        if (details.style.display === 'block') {
            details.style.display = 'none';
            button.textContent = 'Показать технические детали';
        } else {
            details.style.display = 'block';
            button.textContent = 'Скрыть технические детали';
        }
    }
</script>
</body>
</html>
