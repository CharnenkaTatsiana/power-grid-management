<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="enterprise-status">
    <c:if test="${not empty status}">
        <!-- Статистика -->
        <div class="row mb-3">
            <div class="col-md-4">
                <div class="card text-center bg-light">
                    <div class="card-body py-2">
                        <h6 class="text-primary mb-1">${status.totalBranches}</h6>
                        <small class="text-muted">Всего филиалов</small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center bg-light">
                    <div class="card-body py-2">
                        <h6 class="text-success mb-1">${status.filledBranches}</h6>
                        <small class="text-muted">Заполнено отчетов</small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center bg-light">
                    <div class="card-body py-2">
                        <c:choose>
                            <c:when test="${status.allBranchesFilled}">
                                <h6 class="text-success mb-1">100%</h6>
                            </c:when>
                            <c:otherwise>
                                <h6 class="text-warning mb-1">
                                    <fmt:formatNumber value="${(status.filledBranches / status.totalBranches) * 100}" pattern="#"/>%
                                </h6>
                            </c:otherwise>
                        </c:choose>
                        <small class="text-muted">Готовность</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- Прогресс-бар -->
        <div class="mb-3">
            <div class="d-flex justify-content-between mb-1">
                <small>Прогресс заполнения:</small>
                <small>${status.filledBranches}/${status.totalBranches}</small>
            </div>
            <div class="progress" style="height: 8px;">
                <c:set var="progressPercent" value="${(status.filledBranches / status.totalBranches) * 100}"/>
                <div class="progress-bar
                    <c:choose>
                        <c:when test="${progressPercent == 100}">bg-success</c:when>
                        <c:when test="${progressPercent >= 50}">bg-warning</c:when>
                        <c:otherwise>bg-danger</c:otherwise>
                    </c:choose>"
                     role="progressbar"
                     style="width: ${progressPercent}%"
                     aria-valuenow="${progressPercent}"
                     aria-valuemin="0"
                     aria-valuemax="100">
                </div>
            </div>
        </div>

        <!-- Сообщение о статусе -->
        <c:choose>
            <c:when test="${status.allBranchesFilled}">
                <div class="alert alert-success py-2 mb-3">
                    <small>✅ <strong>Все отчеты заполнены!</strong> Отчет РУП-облэнерго будет полным.</small>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alert alert-warning py-2 mb-3">
                    <small>⚠️ <strong>Не все отчеты заполнены.</strong> В отчете РУП-облэнерго будут только данные по ${status.filledBranches} из ${status.totalBranches} филиалов.</small>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Быстрый просмотр статуса филиалов -->
        <details>
            <summary class="btn btn-sm btn-outline-secondary">
                📋 Показать детали по филиалам
            </summary>
            <div class="mt-2">
                <div class="row">
                    <c:forEach var="branchStatus" items="${status.branchStatuses}">
                        <div class="col-12 mb-1">
                            <div class="d-flex justify-content-between align-items-center">
                                <small>${branchStatus.branch.name}</small>
                                <span class="badge
                                    <c:choose>
                                        <c:when test="${branchStatus.hasFilledReport}">bg-success</c:when>
                                        <c:otherwise>bg-warning</c:otherwise>
                                    </c:choose>" style="font-size: 0.7em;">
                                    <c:choose>
                                        <c:when test="${branchStatus.hasFilledReport}">✅ Заполнен</c:when>
                                        <c:otherwise>⏳ Ожидает</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </details>
    </c:if>
</div>