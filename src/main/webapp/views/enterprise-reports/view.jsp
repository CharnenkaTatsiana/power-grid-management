<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Отчет РУП-облэнерго - Заглушка</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .stub-container {
            background: white;
            border-radius: 20px;
            padding: 3rem;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            text-align: center;
            max-width: 600px;
            margin: 2rem;
        }
        .construction-icon {
            font-size: 4rem;
            margin-bottom: 2rem;
        }
        .feature-list {
            text-align: left;
            margin: 2rem 0;
        }
        .btn-stub {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 12px 30px;
            border-radius: 50px;
            font-weight: 600;
            margin: 0.5rem;
        }
    </style>
</head>
<body>
<div class="stub-container">
    <div class="construction-icon">🚧</div>

    <h1 class="mb-3">Функция в разработке</h1>

    <p class="lead mb-4">
        Просмотр отчетов РУП-облэнерго временно недоступен.
        Мы активно работаем над реализацией этой функции.
    </p>

    <div class="alert alert-info mb-4">
        <strong>📊 Отчет успешно создан!</strong><br>
        ID отчета: <strong>${param.id}</strong><br>
        Данные сохранены в системе и будут доступны после завершения разработки.
    </div>

    <div class="feature-list">
        <h5>🔮 Что будет доступно в будущем:</h5>
        <ul>
            <li>Детальный просмотр агрегированных данных</li>
            <li>Статистика по филиалам предприятия</li>
            <li>Графики и диаграммы выполнения плана</li>
            <li>Сравнительный анализ показателей</li>
            <li>Экспорт отчетов в различные форматы</li>
        </ul>
    </div>

    <div class="mt-4">
        <a href="${pageContext.request.contextPath}/enterprise-reports" class="btn btn-stub">
            🔄 Создать новый отчет
        </a>
        <a href="${pageContext.request.contextPath}/reports" class="btn btn-outline-secondary">
            📊 К отчетам филиалов
        </a>
    </div>

    <div class="mt-4 text-muted">
        <small>
            💡 <strong>Совет:</strong> Вы можете продолжить работу с отчетами филиалов,
            пока функция находится в разработке.
        </small>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>