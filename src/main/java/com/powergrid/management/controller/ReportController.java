package com.powergrid.management.controller;

import com.powergrid.management.config.HibernateUtil;
import com.powergrid.management.dto.ReportDTO;
import com.powergrid.management.dto.ReportItemDTO;
import com.powergrid.management.model.*;
import com.powergrid.management.service.ReportGenerationService;
import com.powergrid.management.service.UserService;
import org.hibernate.Session;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportController extends HttpServlet {
    private UserService userService = new UserService();
    private ReportGenerationService reportGenerationService = new ReportGenerationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String username = (String) session.getAttribute("username");
        User user = userService.findByUsername(username);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                listReports(request, response, user);
            } else if (action.equals("view")) {
                viewBranchReport(request, response, user);
            } else if (action.equals("fill")) {
                showFillForm(request, response, user);
            } else if (action.equals("edit")) {
                showEditForm(request, response, user);
            } else {
                listReports(request, response, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    /**
     * Список всех отчетов (только филиалов)
     */
    private void listReports(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Упрощенный запрос - только отчеты филиалов
            String hql = "SELECT new com.powergrid.management.dto.ReportDTO(" +
                    "r.id, r.title, r.reportType, " +
                    "r.createdDate, r.periodStart, r.periodEnd, " +
                    "u.username, b.name, r.isFilled) " +
                    "FROM Report r " +
                    "LEFT JOIN r.createdBy u " +
                    "LEFT JOIN r.branch b " +
                    "ORDER BY r.createdDate DESC";

            List<ReportDTO> reports = session.createQuery(hql, ReportDTO.class).list();

            // Устанавливаем reportPeriod для каждого отчета
            for (ReportDTO report : reports) {
                if (report.getPeriodStart() != null) {
                    String period = report.getPeriodStart().getYear() + "-" +
                            String.format("%02d", report.getPeriodStart().getMonthValue());
                    report.setReportPeriod(period);
                }
            }

            request.setAttribute("reports", reports);
            request.setAttribute("user", user);

            request.getRequestDispatcher("/views/reports/list.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка загрузки отчетов: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    /**
     * Просмотр отчета филиала (только чтение)
     */
    private void viewBranchReport(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try {
            Long reportId = Long.parseLong(request.getParameter("id"));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                ReportDTO report = session.createQuery(
                                "SELECT new com.powergrid.management.dto.ReportDTO(" +
                                        "r.id, r.title, r.reportType, " +
                                        "r.createdDate, r.periodStart, r.periodEnd, " +
                                        "u.username, b.name, r.isFilled) " +
                                        "FROM Report r " +
                                        "LEFT JOIN r.createdBy u " +
                                        "LEFT JOIN r.branch b " +
                                        "WHERE r.id = :id", ReportDTO.class)
                        .setParameter("id", reportId)
                        .uniqueResult();

                if (report == null) {
                    request.setAttribute("error", "Отчет филиала не найден");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                List<ReportItemDTO> reportItems = session.createQuery(
                                "SELECT new com.powergrid.management.dto.ReportItemDTO(" +
                                        "ri.id, wt.name, " +
                                        "ri.annualPlan, ri.quarterPlan, " +
                                        "ri.monthFact, ri.cumulativeFact, " +
                                        "ri.annualPercentage, ri.quarterPercentage) " +
                                        "FROM ReportItem ri " +
                                        "LEFT JOIN ri.workType wt " +
                                        "WHERE ri.report.id = :reportId " +
                                        "ORDER BY wt.name", ReportItemDTO.class)
                        .setParameter("reportId", reportId)
                        .list();

                request.setAttribute("report", report);
                request.setAttribute("reportItems", reportItems);
                request.setAttribute("user", user);
                request.setAttribute("readOnly", true); // Только просмотр

                request.getRequestDispatcher("/views/reports/view.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка загрузки отчета: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    /**
     * Форма редактирования отчета филиала
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try {
            Long reportId = Long.parseLong(request.getParameter("id"));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Report report = session.createQuery(
                                "SELECT r FROM Report r WHERE r.id = :id", Report.class)
                        .setParameter("id", reportId)
                        .uniqueResult();

                if (report == null) {
                    request.setAttribute("error", "Отчет филиала не найден");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                List<ReportItemDTO> reportItems = session.createQuery(
                                "SELECT new com.powergrid.management.dto.ReportItemDTO(" +
                                        "ri.id, wt.name, " +
                                        "ri.annualPlan, ri.quarterPlan, " +
                                        "ri.monthFact, ri.cumulativeFact, " +
                                        "ri.annualPercentage, ri.quarterPercentage) " +
                                        "FROM ReportItem ri " +
                                        "LEFT JOIN ri.workType wt " +
                                        "WHERE ri.report.id = :reportId " +
                                        "ORDER BY wt.name", ReportItemDTO.class)
                        .setParameter("reportId", reportId)
                        .list();

                request.setAttribute("report", report);
                request.setAttribute("reportItems", reportItems);
                request.setAttribute("user", user);

                request.getRequestDispatcher("/views/reports/edit.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    /**
     * Форма заполнения отчета филиала
     */
    private void showFillForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try {
            Long reportId = Long.parseLong(request.getParameter("id"));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Report report = session.createQuery(
                                "SELECT r FROM Report r WHERE r.id = :id", Report.class)
                        .setParameter("id", reportId)
                        .uniqueResult();

                if (report == null) {
                    request.setAttribute("error", "Отчет филиала не найден");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                List<ReportItemDTO> reportItems = session.createQuery(
                                "SELECT new com.powergrid.management.dto.ReportItemDTO(" +
                                        "ri.id, wt.name, " +
                                        "ri.annualPlan, ri.quarterPlan, " +
                                        "ri.monthFact, ri.cumulativeFact, " +
                                        "ri.annualPercentage, ri.quarterPercentage) " +
                                        "FROM ReportItem ri " +
                                        "LEFT JOIN ri.workType wt " +
                                        "WHERE ri.report.id = :reportId " +
                                        "ORDER BY wt.name", ReportItemDTO.class)
                        .setParameter("reportId", reportId)
                        .list();

                // Получаем ID плана для кнопки "Назад"
                Integer year = report.getReportPeriod() != null ?
                        Integer.parseInt(report.getReportPeriod().substring(0, 4)) : LocalDateTime.now().getYear();
                Long planId = session.createQuery(
                                "SELECT p.id FROM Plan p WHERE p.branch.id = :branchId AND p.planYear = :year", Long.class)
                        .setParameter("branchId", report.getBranch().getId())
                        .setParameter("year", year)
                        .uniqueResult();

                request.setAttribute("report", report);
                request.setAttribute("reportItems", reportItems);
                request.setAttribute("planId", planId);
                request.setAttribute("user", user);

                request.getRequestDispatcher("/views/reports/fill.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String username = (String) session.getAttribute("username");
        User user = userService.findByUsername(username);

        String action = request.getParameter("action");

        try {
            if ("updateFactData".equals(action)) {
                updateReportFactData(request, response, user);
            } else {
                listReports(request, response, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    /**
     * Обновление фактических данных (только для отчетов филиалов)
     */
    private void updateReportFactData(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try {
            Long reportId = Long.parseLong(request.getParameter("reportId"));
            String[] itemIds = request.getParameterValues("itemId");
            String[] monthFacts = request.getParameterValues("monthFact");

            List<ReportItemDTO> reportItems = new ArrayList<>();

            if (itemIds != null && monthFacts != null) {
                for (int i = 0; i < itemIds.length; i++) {
                    Long itemId = Long.parseLong(itemIds[i]);
                    Double monthFact = (monthFacts[i] != null && !monthFacts[i].isEmpty())
                            ? Double.parseDouble(monthFacts[i]) : 0.0;

                    ReportItemDTO itemDTO = new ReportItemDTO(itemId, monthFact);
                    reportItems.add(itemDTO);
                }
            }

            reportGenerationService.updateReportFactData(reportId, reportItems);

            request.getSession().setAttribute("success", "Данные отчета успешно обновлены");
            response.sendRedirect(request.getContextPath() + "/reports?action=view&id=" + reportId);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка обновления отчета: " + e.getMessage());
            try {
                Long reportId = Long.parseLong(request.getParameter("reportId"));
                response.sendRedirect(request.getContextPath() + "/reports?action=fill&id=" + reportId);
            } catch (Exception ex) {
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        }
    }
}