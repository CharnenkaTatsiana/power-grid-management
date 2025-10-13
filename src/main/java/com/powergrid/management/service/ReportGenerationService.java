package com.powergrid.management.service;

import com.powergrid.management.config.HibernateUtil;
import com.powergrid.management.dto.ReportItemDTO;
import com.powergrid.management.model.*;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGenerationService {

    /**
     * Создает формы месячных отчетов для плана филиала
     */
    public void createMonthlyReportFormsForPlan(Plan plan) {
        System.out.println("=== DEBUG: Creating monthly report forms for plan ID: " + plan.getId());

        // Проверяем, что это план филиала
        if (plan.getBranch() == null) {
            System.out.println("=== DEBUG: Skipping report generation - not a branch plan");
            return;
        }

        Session session = null;
        org.hibernate.Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Перезагружаем план с связями
            Plan reloadedPlan = session.createQuery(
                            "SELECT p FROM Plan p " +
                                    "LEFT JOIN FETCH p.branch b " +
                                    "WHERE p.id = :planId", Plan.class)
                    .setParameter("planId", plan.getId())
                    .uniqueResult();

            if (reloadedPlan == null) {
                throw new RuntimeException("Не удалось перезагрузить план с ID: " + plan.getId());
            }

            // Получаем все позиции плана
            List<PlanItem> planItems = session.createQuery(
                            "FROM PlanItem pi WHERE pi.plan.id = :planId", PlanItem.class)
                    .setParameter("planId", reloadedPlan.getId())
                    .list();

            System.out.println("=== DEBUG: Found " + planItems.size() + " plan items");

            // Создаем формы отчетов для каждого месяца года
            for (int month = 1; month <= 12; month++) {
                try {
                    createMonthlyReportForm(session, reloadedPlan, planItems, month);
                    System.out.println("=== DEBUG: Created report form for month " + month);
                } catch (Exception e) {
                    System.err.println("=== DEBUG: Error creating report form for month " + month + ": " + e.getMessage());
                    // Продолжаем создание остальных месяцев
                }
            }

            transaction.commit();
            System.out.println("=== DEBUG: Monthly report forms created successfully for plan ID: " + plan.getId());

        } catch (Exception e) {
            System.err.println("=== DEBUG: Error creating report forms: " + e.getMessage());
            e.printStackTrace();
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("=== DEBUG: Error during rollback: " + rollbackEx.getMessage());
                }
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Создает форму отчета для конкретного месяца
     */
    private void createMonthlyReportForm(Session session, Plan plan, List<PlanItem> planItems, int month) {
        try {
            String monthName = getMonthName(month);
            int year = plan.getPlanYear();
            String reportPeriod = String.format("%04d-%02d", year, month);

            // Проверяем, не существует ли уже отчет для этого периода
            String existingReportHql = "SELECT COUNT(r) FROM Report r WHERE r.branch.id = :branchId AND r.reportPeriod = :reportPeriod";
            Long existingCount = session.createQuery(existingReportHql, Long.class)
                    .setParameter("branchId", plan.getBranch().getId())
                    .setParameter("reportPeriod", reportPeriod)
                    .uniqueResult();

            if (existingCount > 0) {
                System.out.println("=== DEBUG: Report already exists for period " + reportPeriod + ", skipping");
                return;
            }

            Report report = new Report();
            report.setTitle(String.format("Форма отчета за %s %d", monthName, year));
            report.setBranch(plan.getBranch());
            report.setReportType(ReportType.MONTHLY);
            report.setIsFilled(false);
            report.setCreatedDate(LocalDateTime.now());
            report.setCreatedBy(plan.getCreatedBy());

            // Устанавливаем период отчета
            LocalDateTime periodStart = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime periodEnd = periodStart.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);

            report.setPeriodStart(periodStart);
            report.setPeriodEnd(periodEnd);
            report.setReportPeriod(reportPeriod);

            session.persist(report);
            session.flush();

            System.out.println("=== DEBUG: Created report with ID: " + report.getId() + " for period: " + reportPeriod);

            // Создаем пустые позиции отчета для заполнения инженером
            int itemsCreated = 0;
            for (PlanItem planItem : planItems) {
                try {
                    ReportItem reportItem = createEmptyReportItem(report, planItem, month);
                    session.persist(reportItem);
                    itemsCreated++;
                } catch (Exception e) {
                    System.err.println("=== DEBUG: Error creating report item for work type: " +
                            (planItem.getWorkType() != null ? planItem.getWorkType().getName() : "null"));
                }
            }

            System.out.println("=== DEBUG: Created " + itemsCreated + " report items for report ID: " + report.getId());

        } catch (Exception e) {
            System.err.println("=== DEBUG: Error creating report form for month " + month + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Создает пустую позицию отчета для заполнения инженером
     */
    private ReportItem createEmptyReportItem(Report report, PlanItem planItem, int month) {
        ReportItem reportItem = new ReportItem();
        reportItem.setReport(report);
        reportItem.setWorkType(planItem.getWorkType());

        // Устанавливаем только плановые значения из плана
        reportItem.setAnnualPlan(planItem.getAnnualPlan());

        // Квартальный план с нарастающим итогом (правильная логика)
        reportItem.setQuarterPlan(calculateCumulativeQuarterPlan(planItem, month));

        // Фактические данные - NULL, будут заполняться инженером
        reportItem.setMonthFact(null);
        reportItem.setCumulativeFact(null); // Теперь рассчитывается автоматически
        reportItem.setAnnualPercentage(null);
        reportItem.setQuarterPercentage(null);

        return reportItem;
    }

    /**
     * Рассчитывает квартальный план с нарастающим итогом (исправленная логика)
     */
    private Double calculateCumulativeQuarterPlan(PlanItem planItem, int month) {
        double cumulativePlan = 0.0;

        // Определяем, до какого квартала включительно нужно суммировать
        int maxQuarter = (month - 1) / 3 + 1;

        // Суммируем планы всех кварталов до текущего включительно
        for (int quarter = 1; quarter <= maxQuarter; quarter++) {
            double quarterPlan = getQuarterPlan(planItem, quarter);
            cumulativePlan += quarterPlan;
        }

        System.out.println("=== DEBUG: Month " + month + " - Cumulative quarter plan: " + cumulativePlan +
                " (sum of quarters 1 to " + maxQuarter + ")");

        return cumulativePlan;
    }

    /**
     * Получает план для указанного квартала
     */
    private Double getQuarterPlan(PlanItem planItem, int quarter) {
        switch (quarter) {
            case 1: return planItem.getQ1Plan() != null ? planItem.getQ1Plan() : 0.0;
            case 2: return planItem.getQ2Plan() != null ? planItem.getQ2Plan() : 0.0;
            case 3: return planItem.getQ3Plan() != null ? planItem.getQ3Plan() : 0.0;
            case 4: return planItem.getQ4Plan() != null ? planItem.getQ4Plan() : 0.0;
            default: return 0.0;
        }
    }

    /**
     * Возвращает русское название месяца
     */
    private String getMonthName(int month) {
        String[] monthNames = {
                "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        };
        return monthNames[month - 1];
    }

    /**
     * Получает все отчеты для плана (для отладки)
     */
    public List<Report> getReportsForPlan(Plan plan) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Report r WHERE r.branch.id = :branchId AND r.reportPeriod LIKE :yearPattern " +
                                    "ORDER BY r.reportPeriod", Report.class)
                    .setParameter("branchId", plan.getBranch().getId())
                    .setParameter("yearPattern", plan.getPlanYear() + "-%")
                    .list();
        } catch (Exception e) {
            System.err.println("=== DEBUG: Error getting reports for plan: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Удаляет все отчеты, связанные с планом
     */
    public void deleteReportsForPlan(Plan plan) {
        System.out.println("=== DEBUG: Deleting reports for plan ID: " + plan.getId());

        Session session = null;
        org.hibernate.Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Branch branch = plan.getBranch();
            Integer planYear = plan.getPlanYear();

            if (branch == null || planYear == null) {
                System.out.println("=== DEBUG: Branch or plan year is null, skipping report deletion");
                return;
            }

            System.out.println("=== DEBUG: Deleting reports for branch: " + branch.getId() + ", year: " + planYear);

            // 1. Сначала удаляем позиции отчетов
            String deleteItemsHql = "DELETE FROM ReportItem ri WHERE ri.report.id IN " +
                    "(SELECT r.id FROM Report r WHERE r.branch.id = :branchId AND r.reportPeriod LIKE :yearPattern)";

            int itemsDeleted = session.createQuery(deleteItemsHql)
                    .setParameter("branchId", branch.getId())
                    .setParameter("yearPattern", planYear + "-%")
                    .executeUpdate();

            System.out.println("=== DEBUG: Deleted " + itemsDeleted + " report items");

            // 2. Затем удаляем сами отчеты
            String deleteReportsHql = "DELETE FROM Report r WHERE r.branch.id = :branchId " +
                    "AND r.reportPeriod LIKE :yearPattern";

            int reportsDeleted = session.createQuery(deleteReportsHql)
                    .setParameter("branchId", branch.getId())
                    .setParameter("yearPattern", planYear + "-%")
                    .executeUpdate();

            transaction.commit();
            System.out.println("=== DEBUG: Successfully deleted " + reportsDeleted + " reports and " + itemsDeleted + " report items for plan ID: " + plan.getId());

        } catch (Exception e) {
            System.err.println("=== DEBUG: Error deleting reports for plan: " + e.getMessage());
            e.printStackTrace();
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("=== DEBUG: Error during rollback: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Ошибка удаления отчетов для плана: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Обновляет фактические данные в отчете
     */
    public void updateReportFactData(Long reportId, List<ReportItemDTO> reportItems) {
        Session session = null;
        org.hibernate.Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Report currentReport = session.get(Report.class, reportId);
            if (currentReport == null) {
                throw new RuntimeException("Отчет не найден");
            }

            // Обновляем флаг заполнения
            currentReport.setIsFilled(true);

            for (ReportItemDTO itemDTO : reportItems) {
                ReportItem reportItem = session.get(ReportItem.class, itemDTO.getId());
                if (reportItem != null) {
                    // Обновляем фактические данные за месяц
                    reportItem.setMonthFact(itemDTO.getMonthFact());

                    // Автоматически рассчитываем накопленный факт
                    Double cumulativeFact = calculateCumulativeFact(session, reportItem, currentReport);
                    reportItem.setCumulativeFact(cumulativeFact);

                    // Пересчитываем проценты выполнения
                    reportItem.calculatePercentages();
                }
            }

            transaction.commit();
            System.out.println("=== DEBUG: Report fact data updated successfully");

        } catch (Exception e) {
            System.err.println("=== DEBUG: Error updating report fact data: " + e.getMessage());
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Ошибка обновления отчета: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Метод для автоматического расчета накопленного факта (исправленная логика)
     */
    private Double calculateCumulativeFact(Session session, ReportItem currentItem, Report currentReport) {
        try {
            String currentPeriod = currentReport.getReportPeriod();
            int currentYear = Integer.parseInt(currentPeriod.substring(0, 4));
            int currentMonth = Integer.parseInt(currentPeriod.substring(5, 7));

            System.out.println("=== DEBUG: Calculating cumulative fact for period: " + currentPeriod +
                    ", year: " + currentYear + ", month: " + currentMonth);

            // Получаем все отчеты за текущий год до текущего месяца ВКЛЮЧИТЕЛЬНО
            List<Report> allReportsThisYear = session.createQuery(
                            "FROM Report r WHERE r.branch.id = :branchId " +
                                    "AND r.reportPeriod LIKE :yearPattern " +
                                    "AND r.reportType = :reportType " +
                                    "ORDER BY r.reportPeriod", Report.class)
                    .setParameter("branchId", currentReport.getBranch().getId())
                    .setParameter("yearPattern", currentYear + "-%")
                    .setParameter("reportType", ReportType.MONTHLY)
                    .list();

            double cumulativeFact = 0.0;
            int processedMonths = 0;

            // Суммируем фактические данные за все месяцы до текущего включительно
            for (Report report : allReportsThisYear) {
                String reportPeriod = report.getReportPeriod();
                int reportMonth = Integer.parseInt(reportPeriod.substring(5, 7));

                // Останавливаемся на текущем месяце
                if (reportMonth > currentMonth) {
                    break;
                }

                // Получаем фактические данные для этого отчета
                List<ReportItem> items = session.createQuery(
                                "FROM ReportItem ri WHERE ri.report.id = :reportId AND ri.workType.id = :workTypeId", ReportItem.class)
                        .setParameter("reportId", report.getId())
                        .setParameter("workTypeId", currentItem.getWorkType().getId())
                        .list();

                for (ReportItem item : items) {
                    if (item.getMonthFact() != null) {
                        cumulativeFact += item.getMonthFact();
                        processedMonths++;
                        System.out.println("=== DEBUG: Added month fact " + item.getMonthFact() +
                                " from period " + reportPeriod + ", cumulative: " + cumulativeFact);
                    }
                }
            }

            System.out.println("=== DEBUG: Final cumulative fact: " + cumulativeFact +
                    " (processed " + processedMonths + " months)");

            return cumulativeFact;

        } catch (Exception e) {
            System.err.println("=== DEBUG: Error calculating cumulative fact: " + e.getMessage());
            e.printStackTrace();
            return currentItem.getMonthFact() != null ? currentItem.getMonthFact() : 0.0;
        }
    }

    /**
     * Получает отчет по ID
     */
    public Report getReportById(Long reportId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Report.class, reportId);
        } catch (Exception e) {
            System.err.println("=== DEBUG: Error getting report by ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Получает все отчеты для филиала за указанный год
     */
    public List<Report> getReportsForBranchAndYear(Long branchId, Integer year) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Report r WHERE r.branch.id = :branchId AND r.reportPeriod LIKE :yearPattern " +
                                    "ORDER BY r.reportPeriod", Report.class)
                    .setParameter("branchId", branchId)
                    .setParameter("yearPattern", year + "-%")
                    .list();
        } catch (Exception e) {
            System.err.println("=== DEBUG: Error getting reports for branch and year: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Проверяет, существует ли отчет для указанного периода и филиала
     */
    public boolean reportExistsForPeriod(Long branchId, String reportPeriod) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(r) FROM Report r WHERE r.branch.id = :branchId AND r.reportPeriod = :reportPeriod", Long.class)
                    .setParameter("branchId", branchId)
                    .setParameter("reportPeriod", reportPeriod)
                    .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("=== DEBUG: Error checking report existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Пересчитывает все показатели для отчета (накопленный факт, проценты)
     */
    public void recalculateReportData(Long reportId) {
        Session session = null;
        org.hibernate.Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Report report = session.get(Report.class, reportId);
            if (report == null) {
                throw new RuntimeException("Отчет не найден");
            }

            // Получаем все позиции отчета
            List<ReportItem> reportItems = session.createQuery(
                            "FROM ReportItem ri WHERE ri.report.id = :reportId", ReportItem.class)
                    .setParameter("reportId", reportId)
                    .list();

            // Пересчитываем для каждой позиции
            for (ReportItem item : reportItems) {
                // Пересчитываем накопленный факт
                Double cumulativeFact = calculateCumulativeFact(session, item, report);
                item.setCumulativeFact(cumulativeFact);

                // Пересчитываем проценты выполнения
                item.calculatePercentages();

                System.out.println("=== DEBUG: Recalculated item - MonthFact: " + item.getMonthFact() +
                        ", CumulativeFact: " + cumulativeFact);
            }

            transaction.commit();
            System.out.println("=== DEBUG: Report data recalculated successfully for report ID: " + reportId);

        } catch (Exception e) {
            System.err.println("=== DEBUG: Error recalculating report data: " + e.getMessage());
            e.printStackTrace();
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Ошибка пересчета данных отчета: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // ==================== МЕТОДЫ ДЛЯ ОТЧЕТОВ ПРЕДПРИЯТИЙ ====================

    /**
     * Упрощенная генерация отчета предприятия (заглушка)
     */
    public Report generateEnterpriseReport(Long enterpriseId, Integer year, Integer month, User createdBy) {
        System.out.println("=== GENERATING ENTERPRISE REPORT (STUB) ===");
        System.out.println("Enterprise: " + enterpriseId + ", Year: " + year + ", Month: " + month);

        Session session = null;
        org.hibernate.Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Получаем предприятие
            Enterprise enterprise = session.get(Enterprise.class, enterpriseId);
            if (enterprise == null) {
                throw new RuntimeException("Предприятие не найдено с ID: " + enterpriseId);
            }

            // Создаем простой отчет-заглушку
            String monthName = getMonthName(month);
            Report enterpriseReport = new Report();
            enterpriseReport.setTitle("Отчет предприятия '" + enterprise.getName() + "' за " + monthName + " " + year + " года");
            enterpriseReport.setEnterprise(enterprise);
            enterpriseReport.setReportType(ReportType.MONTHLY);
            enterpriseReport.setIsFilled(true);
            enterpriseReport.setCreatedDate(LocalDateTime.now());
            enterpriseReport.setCreatedBy(createdBy);

            // Простые даты
            enterpriseReport.setPeriodStart(LocalDateTime.of(year, month, 1, 0, 0));
            enterpriseReport.setPeriodEnd(LocalDateTime.of(year, month, 28, 23, 59));
            enterpriseReport.setReportPeriod(String.format("%04d-%02d", year, month));

            session.persist(enterpriseReport);
            session.flush();

            System.out.println("=== ENTERPRISE REPORT CREATED: " + enterpriseReport.getId());

            // Создаем несколько тестовых позиций
            createStubReportItems(session, enterpriseReport);

            transaction.commit();

            System.out.println("=== ENTERPRISE REPORT GENERATION COMPLETED ===");
            return enterpriseReport;

        } catch (Exception e) {
            System.err.println("=== ERROR IN STUB GENERATION: " + e.getMessage());
            e.printStackTrace();
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Ошибка создания заглушки: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Создает тестовые позиции отчета
     */
    private void createStubReportItems(Session session, Report enterpriseReport) {
        try {
            // Получаем несколько типов работ
            List<WorkType> workTypes = session.createQuery(
                            "FROM WorkType wt ORDER BY wt.name", WorkType.class)
                    .setMaxResults(5) // Ограничим для теста
                    .list();

            System.out.println("=== CREATING STUB ITEMS: " + workTypes.size() + " work types");

            for (int i = 0; i < workTypes.size(); i++) {
                WorkType workType = workTypes.get(i);

                ReportItem item = new ReportItem();
                item.setReport(enterpriseReport);
                item.setWorkType(workType);

                // Тестовые данные
                double baseValue = 100 * (i + 1);
                item.setAnnualPlan(baseValue * 12);
                item.setQuarterPlan(baseValue * 3);
                item.setMonthFact(baseValue);
                item.setCumulativeFact(baseValue * (i + 1));
                item.calculatePercentages();

                session.persist(item);
                System.out.println("=== CREATED ITEM: " + workType.getName() +
                        " - Plan: " + item.getAnnualPlan() +
                        " - Fact: " + item.getMonthFact());
            }

        } catch (Exception e) {
            System.err.println("=== ERROR CREATING STUB ITEMS: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Заглушка для агрегации - просто логируем
     */
    private void aggregateBranchReportsToEnterprise(Session session, Report enterpriseReport,
                                                    Long enterpriseId, String reportPeriod) {
        System.out.println("=== AGGREGATION STUB CALLED ===");
        System.out.println("Enterprise Report ID: " + enterpriseReport.getId());
        System.out.println("Would aggregate data from branches for period: " + reportPeriod);

        // Просто логируем что мы бы сделали, но не делаем реальной агрегации
        try {
            List<Branch> branches = session.createQuery(
                            "FROM Branch b WHERE b.enterprise.id = :enterpriseId", Branch.class)
                    .setParameter("enterpriseId", enterpriseId)
                    .list();

            System.out.println("=== WOULD AGGREGATE FROM " + branches.size() + " BRANCHES:");
            for (Branch branch : branches) {
                System.out.println(" - Branch: " + branch.getName());
            }
        } catch (Exception e) {
            System.err.println("=== ERROR IN AGGREGATION STUB: " + e.getMessage());
        }
    }

    /**
     * Получает список предприятий
     */
    public List<Enterprise> getAllEnterprises() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Enterprise e ORDER BY e.name", Enterprise.class).list();
        } catch (Exception e) {
            System.err.println("=== DEBUG: Error getting enterprises: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Получает статус заполнения отчетов филиалов предприятия за период
     */
    public EnterpriseReportStatus getEnterpriseReportStatus(Long enterpriseId, Integer year, Integer month) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Enterprise enterprise = session.get(Enterprise.class, enterpriseId);
            if (enterprise == null) {
                throw new RuntimeException("Предприятие не найдено");
            }

            String reportPeriod = String.format("%04d-%02d", year, month);

            // Получаем все филиалы предприятия
            List<Branch> branches = session.createQuery(
                            "FROM Branch b WHERE b.enterprise.id = :enterpriseId ORDER BY b.name", Branch.class)
                    .setParameter("enterpriseId", enterpriseId)
                    .list();

            EnterpriseReportStatus status = new EnterpriseReportStatus();
            status.setEnterprise(enterprise);
            status.setYear(year);
            status.setMonth(month);
            status.setReportPeriod(reportPeriod);
            status.setTotalBranches(branches.size());

            // Проверяем статус каждого филиала
            int filledCount = 0;
            List<BranchReportStatus> branchStatuses = new ArrayList<>();

            for (Branch branch : branches) {
                BranchReportStatus branchStatus = new BranchReportStatus();
                branchStatus.setBranch(branch);

                Report filledReport = session.createQuery(
                                "FROM Report r WHERE r.branch.id = :branchId " +
                                        "AND r.reportPeriod = :reportPeriod " +
                                        "AND r.isFilled = true", Report.class)
                        .setParameter("branchId", branch.getId())
                        .setParameter("reportPeriod", reportPeriod)
                        .uniqueResult();

                branchStatus.setHasFilledReport(filledReport != null);
                if (filledReport != null) {
                    filledCount++;
                }

                branchStatuses.add(branchStatus);
            }

            status.setFilledBranches(filledCount);
            status.setBranchStatuses(branchStatuses);
            status.setAllBranchesFilled(filledCount == branches.size());

            return status;

        } catch (Exception e) {
            System.err.println("=== DEBUG: Error getting enterprise report status: " + e.getMessage());
            throw new RuntimeException("Ошибка получения статуса отчетов предприятия: " + e.getMessage(), e);
        }
    }

    /**
     * Класс для статуса отчета предприятия
     */
    public static class EnterpriseReportStatus {
        private Enterprise enterprise;
        private Integer year;
        private Integer month;
        private String reportPeriod;
        private int totalBranches;
        private int filledBranches;
        private boolean allBranchesFilled;
        private List<BranchReportStatus> branchStatuses;

        // Геттеры и сеттеры
        public Enterprise getEnterprise() { return enterprise; }
        public void setEnterprise(Enterprise enterprise) { this.enterprise = enterprise; }

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }

        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }

        public String getReportPeriod() { return reportPeriod; }
        public void setReportPeriod(String reportPeriod) { this.reportPeriod = reportPeriod; }

        public int getTotalBranches() { return totalBranches; }
        public void setTotalBranches(int totalBranches) { this.totalBranches = totalBranches; }

        public int getFilledBranches() { return filledBranches; }
        public void setFilledBranches(int filledBranches) { this.filledBranches = filledBranches; }

        public boolean isAllBranchesFilled() { return allBranchesFilled; }
        public void setAllBranchesFilled(boolean allBranchesFilled) { this.allBranchesFilled = allBranchesFilled; }

        public List<BranchReportStatus> getBranchStatuses() { return branchStatuses; }
        public void setBranchStatuses(List<BranchReportStatus> branchStatuses) { this.branchStatuses = branchStatuses; }
    }

    /**
     * Класс для статуса отчета филиала
     */
    public static class BranchReportStatus {
        private Branch branch;
        private boolean hasFilledReport;

        // Геттеры и сеттеры
        public Branch getBranch() { return branch; }
        public void setBranch(Branch branch) { this.branch = branch; }

        public boolean isHasFilledReport() { return hasFilledReport; }
        public void setHasFilledReport(boolean hasFilledReport) { this.hasFilledReport = hasFilledReport; }
    }
}