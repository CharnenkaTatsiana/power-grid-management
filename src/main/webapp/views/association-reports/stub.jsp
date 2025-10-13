<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>Отчеты по объединению - Заглушка</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body {
      background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
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
      max-width: 700px;
      margin: 2rem;
    }
    .construction-icon {
      font-size: 5rem;
      margin-bottom: 2rem;
    }
    .feature-list {
      text-align: left;
      margin: 2rem 0;
    }
    .btn-stub {
      background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
      border: none;
      color: white;
      padding: 12px 30px;
      border-radius: 50px;
      font-weight: 600;
      margin: 0.5rem;
      text-decoration: none;
      display: inline-block;
      transition: all 0.3s ease;
    }
    .btn-stub:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4);
      color: white;
    }
    .btn-secondary-stub {
      background: #6c757d;
      border: none;
      color: white;
      padding: 12px 30px;
      border-radius: 50px;
      font-weight: 600;
      margin: 0.5rem;
      text-decoration: none;
      display: inline-block;
      transition: all 0.3s ease;
    }
    .btn-secondary-stub:hover {
      background: #5a6268;
      color: white;
      transform: translateY(-2px);
    }
  </style>
</head>
<body>
<div class="stub-container">
  <div class="construction-icon">🏛️</div>

  <h1 class="mb-3">Отчеты по объединению</h1>
  <h2 class="text-muted mb-4">Функция в разработке</h2>

  <p class="lead mb-4">
    Мы работаем над созданием системы отчетов для ГПО "Белэнерго".
    В ближайшее время здесь будет доступна агрегация данных со всех РУП-облэнерго.
  </p>

  <div class="alert alert-info mb-4">
    <strong>💡 Что будет доступно:</strong><br>
    • Сводные отчеты по всем РУП-облэнерго<br>
    • Анализ выполнения планов в масштабе ГПО "Белэнерго"<br>
    • Автоматическое формирование отчетов
  </div>

  <div class="feature-list">
    <h5>📊 Планируемый функционал:</h5>
    <div class="row">
      <div class="col-md-6">
        <ul>
          <li>Сводные показатели по ГПО "Белэнерго"</li>
          <li>Рейтинги РУП-облэнерго</li>
          <li>Анализ выполнения планов</li>
        </ul>
      </div>
      <div class="col-md-6">
        <ul>
          <li>Сравнительные отчеты</li>
          <li>Графики и диаграммы</li>
          <li>Экспорт в Excel/PDF</li>
        </ul>
      </div>
    </div>
  </div>

  <div class="mt-4">
    <a href="${pageContext.request.contextPath}/reports" class="btn-stub">
      📊 К отчетам филиалов
    </a>
    <a href="${pageContext.request.contextPath}/enterprise-reports" class="btn-stub">
      🏭 К отчетам предприятия
    </a>
    <a href="${pageContext.request.contextPath}/plans" class="btn-secondary-stub">
      📋 К планам
    </a>
  </div>

  <div class="mt-4 text-muted">
    <small>
      ⏳ <strong>Ориентировочный срок запуска:</strong> следующий квартал
    </small>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
