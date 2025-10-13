<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Управление планами</title>
    <style>
        body { font-family: Arial; margin: 20px; }
        .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .plan-card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .status { padding: 5px 10px; border-radius: 3px; color: white; }
        .status-draft { background: #ffc107; }
        .status-submitted { background: #17a2b8; }
        .status-approved { background: #28a745; }
        .status-rejected { background: #dc3545; }
        .actions { margin-top: 10px; }
        .btn { padding: 5px 10px; margin: 2px; text-decoration: none; border-radius: 3px; }
        .btn-primary { background: #007bff; color: white; }
        .btn-success { background: #28a745; color: white; }
        .btn-danger { background: #dc3545; color: white; }
    </style>
</head>
<body>
<div class="header">
    <h1>Управление планами</h1>
    <a href="create" class="btn btn-primary">Создать план</a>
</div>

<c:if test="${not empty param.success}">
    <div style="background: #d4edda; color: #155724; padding: 10px; margin: 10px 0; border-radius: 3px;">
        План успешно ${param.success}
    </div>
</c:if>

<c:if test="${not empty param.error}">
    <div style="background: #f8d7da; color: #721c24; padding: 10px; margin: 10px 0; border-radius: 3px;">
        Ошибка: ${param.error}
    </div>
</c:if>

<h2>Планы на ${currentYear} год</h2>

<c:forEach var="plan" items="${plans}">
    <div class="plan-card">
        <h3>${plan.planName}</h3>
        <p>Год: ${plan.planYear}</p>
        <p>Статус: <span class="status ${planStatusCssClass}">${planStatusDisplayName}</span></p>

        <div class="actions">
            <a href="${plan.id}" class="btn btn-primary">Управлять</a>
            <a href="${plan.id}/edit" class="btn btn-success">Редактировать</a>
            <c:if test="${plan.editable}">
                <a href="${plan.id}/delete" class="btn btn-danger"
                   onclick="return confirm('Удалить план?')">Удалить</a>
            </c:if>
        </div>
    </div>
</c:forEach>

<c:if test="${empty plans}">
    <p>Планы не найдены</p>
</c:if>
</body>
</html>
