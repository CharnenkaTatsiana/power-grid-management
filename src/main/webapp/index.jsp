<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Power Grid Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .hero {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 4rem 0;
            text-align: center;
        }
        .feature-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 2rem;
            margin: 3rem 0;
        }
        .feature-card {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            text-align: center;
        }
        .demo-section {
            background: #f8f9fa;
            padding: 3rem 0;
            margin: 2rem 0;
        }
    </style>
</head>
<body>
<div class="container">
    <!-- Hero Section -->
    <section class="hero">
        <div class="hero-content">
            <h1>⚡ Power Grid Management System</h1>
            <p class="lead">Профессиональная платформа для управления энергосетями</p>
            <div class="cta-buttons">
                <a href="${pageContext.request.contextPath}/auth-enhanced" class="btn btn-primary btn-lg">
                    Войти в систему
                </a>
                <a href="${pageContext.request.contextPath}/auth-enhanced/register" class="btn btn-secondary btn-lg">
                    Зарегистрироваться
                </a>
            </div>
        </div>
    </section>

    <!-- Features -->
    <section class="features">
        <h2>Возможности системы</h2>
        <div class="feature-grid">
            <div class="feature-card">
                <div class="feature-icon">👥</div>
                <h3>Управление пользователями</h3>
                <p>Полный контроль над учетными записями и правами доступа</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">📊</div>
                <h3>Мониторинг в реальном времени</h3>
                <p>Отслеживание показателей энергопотребления и состояния сети</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">⚡</div>
                <h3>Управление нагрузкой</h3>
                <p>Оптимизация распределения энергетических ресурсов</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">📈</div>
                <h3>Аналитика и отчетность</h3>
                <p>Детальные отчеты и аналитические данные</p>
            </div>
        </div>
    </section>

    <!-- Demo Access -->
    <section class="demo-section">
        <div class="container">
            <h2>Демо-доступ</h2>
            <p>Протестируйте систему с предустановленными учетными записями:</p>

            <div class="demo-grid">
                <div class="demo-card">
                    <h4>👨‍💼 Администратор</h4>
                    <p><strong>Логин:</strong> admin</p>
                    <p><strong>Пароль:</strong> admin123</p>
                    <p>Полный доступ ко всем функциям системы</p>
                    <a href="${pageContext.request.contextPath}/auth-enhanced" class="btn btn-primary">
                        Войти как Администратор
                    </a>
                </div>

                <div class="demo-card">
                    <h4>👷‍♂️ Инженер</h4>
                    <p><strong>Логин:</strong> engineer</p>
                    <p><strong>Пароль:</strong> engineer123</p>
                    <p>Доступ к оперативному управлению</p>
                    <a href="${pageContext.request.contextPath}/auth-enhanced" class="btn btn-secondary">
                        Войти как Инженер
                    </a>
                </div>

                <div class="demo-card">
                    <h4>👀 Наблюдатель</h4>
                    <p><strong>Логин:</strong> viewer</p>
                    <p><strong>Пароль:</strong> viewer123</p>
                    <p>Просмотр статистики и отчетов</p>
                    <a href="${pageContext.request.contextPath}/auth-enhanced" class="btn btn-secondary">
                        Войти как Наблюдатель
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- System Info -->
    <section class="system-info">
        <h2>О системе</h2>
        <div class="info-grid">
            <div class="info-card">
                <h4>🛡️ Безопасность</h4>
                <p>BCrypt хеширование паролей, управление сессиями, ролевая модель доступа</p>
            </div>
            <div class="info-card">
                <h4>💾 База данных</h4>
                <p>MySQL с Hibernate ORM, оптимизированные запросы, транзакционность</p>
            </div>
            <div class="info-card">
                <h4>🎯 Технологии</h4>
                <p>Java Servlets, JSP, Hibernate, MySQL, Jetty, Maven</p>
            </div>
        </div>
    </section>
</div>

<footer class="footer">
    <div class="container">
        <p>&copy; 2024 Power Grid Management System. Все права защищены.</p>
        <p>Версия 1.0.0 | <a href="${pageContext.request.contextPath}/health">Статус системы</a></p>
    </div>
</footer>

<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>