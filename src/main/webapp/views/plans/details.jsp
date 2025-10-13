<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${plan.planName} - Power Grid System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>


        .summary-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin: 2rem 0;
        }
        .summary-card {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            text-align: center;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .summary-value {
            font-size: 1.5rem;
            font-weight: bold;
            color: #2563eb;
            margin: 0.5rem 0;
        }
        .summary-label {
            color: #666;
            font-size: 0.9rem;
        }
        .statistics-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 2rem;
            margin: 2rem 0;
        }
        .statistics-card {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .progress-bar {
            background: #e5e7eb;
            border-radius: 10px;
            height: 10px;
            margin: 0.5rem 0;
            overflow: hidden;
        }
        .progress-fill {
            height: 100%;
            background: #2563eb;
            border-radius: 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="plan-details">

        <div class="summary-cards">
            <div class="summary-card">
                <div class="summary-label">Общая сумма</div>
                <div class="summary-value">
                    <fmt:formatNumber value="${planSummary.totalAnnual}" pattern="#,##0.00"/> руб.
                </div>
            </div>
            <div class="summary-card">
                <div class="summary-label">Средний квартал</div>
                <div class="summary-value">
                    <fmt:formatNumber value="${planSummary.averageQuarter}" pattern="#,##0.00"/> руб.
                </div>
            </div>
            <div class="summary-card">
                <div class="summary-label">1 квартал</div>
                <div class="summary-value">
                    <fmt:formatNumber value="${planSummary.totalQ1}" pattern="#,##0.00"/> руб.
                </div>
            </div>
            <div class="summary-card">
                <div class="summary-label">2 квартал</div>
                <div class="summary-value">
                    <fmt:formatNumber value="${planSummary.totalQ2}" pattern="#,##0.00"/> руб.
                </div>
            </div>
            <div class="summary-card">
                <div class="summary-label">3 квартал</div>
                <div class="summary-value">
                    <fmt:formatNumber value="${planSummary.totalQ3}" pattern="#,##0.00"/> руб.
                </div>
            </div>
            <div class="summary-card">
                <div class="summary-label">4 квартал</div>
                <div class="summary-value">
                    <fmt:formatNumber value="${planSummary.totalQ4}" pattern="#,##0.00"/> руб.
                </div>
            </div>
        </div>

        <!-- Статистика -->
        <div class="statistics-grid">
            <div class="statistics-card">
                <h3>Распределение по типам сетей</h3>
                <div style="margin: 1rem 0;">
                    <div style="display: flex; justify-content: space-between;">
                        <span>Основная сеть:</span>
                        <span><fmt:formatNumber value="${planStatistics.mainNetworkTotal}" pattern="#,##0.00"/> руб.</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${planStatistics.mainNetworkPercentage}%"></div>
                    </div>
                    <div style="text-align: center; color: #666;">
                        <fmt:formatNumber value="${planStatistics.mainNetworkPercentage}" pattern="#.##"/>%
                    </div>
                </div>
                <div style="margin: 1rem 0;">
                    <div style="display: flex; justify-content: space-between;">
                        <span>Распределительная сеть:</span>
                        <span><fmt:formatNumber value="${planStatistics.distributionNetworkTotal}" pattern="#,##0.00"/> руб.</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${planStatistics.distributionNetworkPercentage}%"></div>
                    </div>
                    <div style="text-align: center; color: #666;">
                        <fmt:formatNumber value="${planStatistics.distributionNetworkPercentage}" pattern="#.##"/>%
                    </div>
                </div>
            </div>

            <div class="statistics-card">
                <h3>Количество позиций</h3>
                <div style="margin: 1rem 0;">
                    <div style="display: flex; justify-content: space-between; margin: 0.5rem 0;">
                        <span>Всего позиций:</span>
                        <span style="font-weight: bold;">${planStatistics.totalItems}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin: 0.5rem 0;">
                        <span>Основная сеть:</span>
                        <span>${planStatistics.mainNetworkItems}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin: 0.5rem 0;">
                        <span>Распределительная сеть:</span>
                        <span>${planStatistics.distributionNetworkItems}</span>
                    </div>
                </div>
            </div>
        </div>


    </div>
</div>

<script>

</script>
</body>
</html>