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
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnterpriseReportController extends HttpServlet {
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
            if (action == null || action.equals("generateForm")) {
                showGenerateForm(request, response, user);
            } else if (action.equals("status")) {
                showReportStatus(request, response, user);
            } else if (action.equals("view")) {
                viewEnterpriseReport(request, response, user);
            } else {
                showGenerateForm(request, response, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка: " + e.getMessage());
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

        System.out.println("=== ENTERPRISE REPORT CONTROLLER POST CALLED ===");
        System.out.println("Action: " + action);
        System.out.println("Enterprise ID: " + request.getParameter("enterpriseId"));
        System.out.println("Year: " + request.getParameter("year"));
        System.out.println("Month: " + request.getParameter("month"));

        try {
            if ("generate".equals(action)) {
                processGenerateEnterpriseReport(request, response, user);
            } else {
                showGenerateForm(request, response, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка: " + e.getMessage());
            showGenerateForm(request, response, user);
        }
    }

    /**
     * Генерация отчета предприятия (обработчик формы)
     */
    private void processGenerateEnterpriseReport(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try {
            Long enterpriseId = Long.parseLong(request.getParameter("enterpriseId"));
            int year = Integer.parseInt(request.getParameter("year"));
            int month = Integer.parseInt(request.getParameter("month"));

            System.out.println("🚀 === STARTING ENTERPRISE REPORT GENERATION ===");

            ReportGenerationService service = new ReportGenerationService();
            Report enterpriseReport = service.generateEnterpriseReport(enterpriseId, year, month, user);

            System.out.println("✅ SUCCESS! Report created with ID: " + enterpriseReport.getId());

            // Добавляем сообщение в сессию для отображения на следующей странице
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("success",
                    "✅ Отчет предприятия успешно создан!<br>" +
                            "ID отчета: " + enterpriseReport.getId() + "<br>" +
                            "Название: " + enterpriseReport.getTitle());

            // Редирект на просмотр отчета
            response.sendRedirect(request.getContextPath() + "/enterprise-reports?action=view&id=" + enterpriseReport.getId());
            return; // Важно: добавить return после sendRedirect

        } catch (Exception e) {
            System.err.println("❌ === CONTROLLER ERROR ===");
            e.printStackTrace();

            String errorMessage = "❌ Ошибка генерации отчета: " + e.getMessage();
            request.setAttribute("error", errorMessage);
            showGenerateForm(request, response, user);
        }
    }

    /**
     * Форма генерации отчета предприятия
     */
    private void showGenerateForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Получаем список предприятий
            List<Enterprise> enterprises = reportGenerationService.getAllEnterprises();

            request.setAttribute("enterprises", enterprises);
            request.setAttribute("user", user);
            request.setAttribute("currentYear", Year.now().getValue());
            request.setAttribute("months", getMonths());

            request.getRequestDispatcher("/views/enterprise-reports/generate.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    /**
     * Статус отчетов филиалов предприятия
     */
    private void showReportStatus(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try {
            Long enterpriseId = Long.parseLong(request.getParameter("enterpriseId"));
            int year = Integer.parseInt(request.getParameter("year"));
            int month = Integer.parseInt(request.getParameter("month"));

            ReportGenerationService.EnterpriseReportStatus status =
                    reportGenerationService.getEnterpriseReportStatus(enterpriseId, year, month);

            request.setAttribute("status", status);
            request.setAttribute("user", user);

            request.getRequestDispatcher("/views/enterprise-reports/status.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка получения статуса: " + e.getMessage());
            showGenerateForm(request, response, user);
        }
    }

    /**
     * Просмотр отчета предприятия
     */
    private void viewEnterpriseReport(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try {
            Long reportId = Long.parseLong(request.getParameter("id"));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                // ИСПРАВЛЕННЫЙ ЗАПРОС - используем e.name вместо a.name для enterpriseName
                ReportDTO report = session.createQuery(
                                "SELECT new com.powergrid.management.dto.ReportDTO(" +
                                        "r.id, r.title, r.reportType, " +
                                        "r.createdDate, r.periodStart, r.periodEnd, " +
                                        "u.username, e.name, a.name, r.isFilled) " + // e.name вместо a.name
                                        "FROM Report r " +
                                        "LEFT JOIN r.createdBy u " +
                                        "LEFT JOIN r.enterprise e " +
                                        "LEFT JOIN e.association a " +
                                        "WHERE r.id = :id AND r.enterprise IS NOT NULL", ReportDTO.class)
                        .setParameter("id", reportId)
                        .uniqueResult();

                if (report == null) {
                    request.setAttribute("error", "Отчет предприятия не найден");
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
                request.setAttribute("readOnly", true);

                request.getRequestDispatcher("/views/enterprise-reports/view.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка загрузки отчета: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    /**
     * Возвращает список месяцев для выбора
     */
    private List<Map<String, Object>> getMonths() {
        List<Map<String, Object>> months = new ArrayList<>();
        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

        for (int i = 0; i < monthNames.length; i++) {
            Map<String, Object> month = new HashMap<>();
            month.put("value", i + 1);
            month.put("name", monthNames[i]);
            months.add(month);
        }
        return months;
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
}