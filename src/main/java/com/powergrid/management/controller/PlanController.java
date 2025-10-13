package com.powergrid.management.controller;

import com.powergrid.management.config.HibernateUtil;
import com.powergrid.management.dto.PlanDTO;
import com.powergrid.management.dto.PlanItemDTO;
import com.powergrid.management.dto.ReferenceItemDTO;
import com.powergrid.management.model.*;
import com.powergrid.management.service.ReportGenerationService;
import com.powergrid.management.service.UserService;
import org.hibernate.Session;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.Year;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlanController extends HttpServlet {
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
                listPlans(request, response, user);
            } else if (action.equals("view")) {
                viewPlan(request, response, user);
            } else if (action.equals("create")) {
                showCreateForm(request, response, user);
            } else if (action.equals("edit")) {
                showEditForm(request, response, user);
            } else {
                listPlans(request, response, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void listPlans(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<PlanDTO> plans = session.createQuery(
                    "SELECT new com.powergrid.management.dto.PlanDTO(" +
                            "p.id, p.planYear, b.name, e.name, a.name, u.username, p.createdDate) " +
                            "FROM Plan p " +
                            "LEFT JOIN p.branch b " +
                            "LEFT JOIN b.enterprise e " +
                            "LEFT JOIN e.association a " +
                            "LEFT JOIN p.createdBy u " +
                            "ORDER BY p.planYear DESC, p.id DESC", PlanDTO.class).list();

            Long planCount = session.createQuery("SELECT COUNT(p) FROM Plan p", Long.class)
                    .uniqueResult();

            request.setAttribute("plans", plans);
            request.setAttribute("user", user);
            request.setAttribute("planCount", planCount != null ? planCount : 0L);

            request.getRequestDispatcher("/views/plans/list.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка загрузки планов: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void viewPlan(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try {
            Long planId = Long.parseLong(request.getParameter("id"));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Plan plan = session.createQuery(
                                "SELECT p FROM Plan p WHERE p.id = :id", Plan.class)
                        .setParameter("id", planId)
                        .uniqueResult();

                if (plan == null) {
                    request.setAttribute("error", "План не найден с ID: " + planId);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                PlanDTO planDTO = createPlanDTO(plan);
                List<PlanItemDTO> planItems = getPlanItemsDTO(session, planId);

                request.setAttribute("plan", planDTO);
                request.setAttribute("planItems", planItems);
                request.setAttribute("user", user);

                request.getRequestDispatcher("/views/plans/view.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Неверный ID плана");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка загрузки плана: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private PlanDTO createPlanDTO(Plan plan) {
        String branchName = plan.getBranch() != null ? plan.getBranch().getName() : null;
        String enterpriseName = plan.getBranch() != null && plan.getBranch().getEnterprise() != null ?
                plan.getBranch().getEnterprise().getName() : null;
        String associationName = plan.getBranch() != null && plan.getBranch().getEnterprise() != null &&
                plan.getBranch().getEnterprise().getAssociation() != null ?
                plan.getBranch().getEnterprise().getAssociation().getName() : null;
        String createdBy = plan.getCreatedBy() != null ? plan.getCreatedBy().getUsername() : null;

        return new PlanDTO(plan.getId(), plan.getPlanYear(),
                branchName, enterpriseName, associationName,
                createdBy, plan.getCreatedDate());
    }

    private List<PlanItemDTO> getPlanItemsDTO(Session session, Long planId) {
        String itemsHql = "SELECT new com.powergrid.management.dto.PlanItemDTO(" +
                "pi.id, wt.name, pi.q1Plan, pi.q2Plan, pi.q3Plan, pi.q4Plan, pi.annualPlan) " +
                "FROM PlanItem pi " +
                "LEFT JOIN pi.workType wt " +
                "WHERE pi.plan.id = :planId " +
                "ORDER BY wt.name";

        return session.createQuery(itemsHql, PlanItemDTO.class)
                .setParameter("planId", planId)
                .list();
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String branchesHql = "SELECT b.id, b.name, e.name, a.name " +
                    "FROM Branch b " +
                    "LEFT JOIN b.enterprise e " +
                    "LEFT JOIN e.association a " +
                    "ORDER BY a.name, e.name, b.name";

            List<Object[]> branchResults = session.createQuery(branchesHql, Object[].class).list();

            List<Map<String, Object>> branches = new ArrayList<>();
            for (Object[] row : branchResults) {
                Map<String, Object> branchInfo = new HashMap<>();
                branchInfo.put("id", row[0]);
                branchInfo.put("name", row[1]);
                branchInfo.put("enterpriseName", row[2]);
                branchInfo.put("associationName", row[3]);
                branches.add(branchInfo);
            }

            String workTypesHql = "FROM WorkType wt ORDER BY wt.name";
            List<WorkType> workTypeEntities = session.createQuery(workTypesHql, WorkType.class).list();

            List<ReferenceItemDTO> workTypes = workTypeEntities.stream()
                    .map(wt -> new ReferenceItemDTO(wt.getId(), wt.getName()))
                    .collect(Collectors.toList());

            request.setAttribute("workTypes", workTypes);
            request.setAttribute("branches", branches);
            request.setAttribute("user", user);
            request.setAttribute("currentYear", Year.now().getValue());

            request.getRequestDispatcher("/views/plans/create.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try {
            Long planId = Long.parseLong(request.getParameter("id"));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                String planHql = "SELECT new com.powergrid.management.dto.PlanDTO(" +
                        "p.id, p.planYear, " +
                        "b.name, e.name, a.name, " +
                        "u.username, p.createdDate) " +
                        "FROM Plan p " +
                        "LEFT JOIN p.branch b " +
                        "LEFT JOIN b.enterprise e " +
                        "LEFT JOIN e.association a " +
                        "LEFT JOIN p.createdBy u " +
                        "WHERE p.id = :id";

                PlanDTO plan = session.createQuery(planHql, PlanDTO.class)
                        .setParameter("id", planId)
                        .uniqueResult();

                if (plan == null) {
                    request.setAttribute("error", "План не найден");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                String itemsHql = "SELECT new com.powergrid.management.dto.PlanItemDTO(" +
                        "pi.id, wt.name, pi.q1Plan, pi.q2Plan, pi.q3Plan, pi.q4Plan, pi.annualPlan) " +
                        "FROM PlanItem pi " +
                        "LEFT JOIN pi.workType wt " +
                        "WHERE pi.plan.id = :planId " +
                        "ORDER BY wt.name";

                List<PlanItemDTO> planItems = session.createQuery(itemsHql, PlanItemDTO.class)
                        .setParameter("planId", planId)
                        .list();

                request.setAttribute("plan", plan);
                request.setAttribute("planItems", planItems);
                request.setAttribute("user", user);

                request.getRequestDispatcher("/views/plans/edit.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Неверный ID плана");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
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
            if ("create".equals(action)) {
                createPlan(request, response, user);
            } else if ("update".equals(action)) {
                updatePlan(request, response, user);
            } else if ("delete".equals(action)) {
                deletePlan(request, response, user);
            } else {
                listPlans(request, response, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void createPlan(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        Session session = null;
        org.hibernate.Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            int year = Integer.parseInt(request.getParameter("year"));
            Long branchId = Long.parseLong(request.getParameter("branchId"));

            Branch branch = session.get(Branch.class, branchId);

            if (branch == null) {
                throw new ServletException("Филиал не найден с ID: " + branchId);
            }

            // Проверяем, не существует ли уже план для этого филиала на указанный год
            String existingPlanHql = "SELECT COUNT(p) FROM Plan p WHERE p.planYear = :year AND p.branch.id = :branchId";
            Long existingCount = session.createQuery(existingPlanHql, Long.class)
                    .setParameter("year", year)
                    .setParameter("branchId", branchId)
                    .uniqueResult();

            if (existingCount > 0) {
                throw new ServletException("План на " + year + " год для филиала " + branch.getName() + " уже существует");
            }

            Plan plan = new Plan();
            plan.setPlanYear(year);
            plan.setBranch(branch);
            plan.setCreatedBy(user);

            session.persist(plan);
            session.flush();

            Long planId = plan.getId();

            // Создаем позиции плана
            String[] workTypeIds = request.getParameterValues("workTypeId");
            String[] q1Values = request.getParameterValues("q1");
            String[] q2Values = request.getParameterValues("q2");
            String[] q3Values = request.getParameterValues("q3");
            String[] q4Values = request.getParameterValues("q4");

            if (workTypeIds != null && workTypeIds.length > 0) {
                for (int i = 0; i < workTypeIds.length; i++) {
                    Long workTypeId = Long.parseLong(workTypeIds[i]);
                    WorkType workType = session.get(WorkType.class, workTypeId);

                    if (workType != null) {
                        PlanItem item = new PlanItem();
                        item.setPlan(plan);
                        item.setWorkType(workType);

                        double q1 = parseDoubleSafe(q1Values, i);
                        double q2 = parseDoubleSafe(q2Values, i);
                        double q3 = parseDoubleSafe(q3Values, i);
                        double q4 = parseDoubleSafe(q4Values, i);

                        item.setQ1Plan(q1);
                        item.setQ2Plan(q2);
                        item.setQ3Plan(q3);
                        item.setQ4Plan(q4);

                        double annualPlan = q1 + q2 + q3 + q4;
                        item.setAnnualPlan(annualPlan);

                        session.persist(item);
                    }
                }
            }

            transaction.commit();

            // Автоматически создаем формы отчетов для филиала
            try {
                Plan reloadedPlan = session.get(Plan.class, planId);
                reportGenerationService.createMonthlyReportFormsForPlan(reloadedPlan);
                System.out.println("=== DEBUG: Report forms created successfully");
            } catch (Exception e) {
                System.err.println("=== DEBUG: Error creating report forms: " + e.getMessage());
                e.printStackTrace();
                // Не прерываем выполнение, просто логируем ошибку
            }

            response.sendRedirect(request.getContextPath() + "/plans?action=view&id=" + planId);

        } catch (Exception e) {
            System.err.println("=== DEBUG: Error in createPlan: " + e.getMessage());
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            request.setAttribute("error", "Ошибка создания плана: " + e.getMessage());
            showCreateForm(request, response, user);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    private double parseDoubleSafe(String[] values, int index) {
        if (values != null && index < values.length && values[index] != null && !values[index].isEmpty()) {
            try {
                return Double.parseDouble(values[index]);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private void updatePlan(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Long planId = Long.parseLong(request.getParameter("id"));
            Plan plan = session.get(Plan.class, planId);

            if (plan == null) {
                throw new ServletException("План не найден");
            }

            String[] itemIds = request.getParameterValues("itemId");
            String[] q1Values = request.getParameterValues("q1");
            String[] q2Values = request.getParameterValues("q2");
            String[] q3Values = request.getParameterValues("q3");
            String[] q4Values = request.getParameterValues("q4");

            if (itemIds != null) {
                for (int i = 0; i < itemIds.length; i++) {
                    Long itemId = Long.parseLong(itemIds[i]);
                    PlanItem item = session.get(PlanItem.class, itemId);

                    if (item != null) {
                        item.setQ1Plan(parseDoubleSafe(q1Values, i));
                        item.setQ2Plan(parseDoubleSafe(q2Values, i));
                        item.setQ3Plan(parseDoubleSafe(q3Values, i));
                        item.setQ4Plan(parseDoubleSafe(q4Values, i));

                        double annualPlan = item.getQ1Plan() + item.getQ2Plan() + item.getQ3Plan() + item.getQ4Plan();
                        item.setAnnualPlan(annualPlan);
                    }
                }
            }

            session.getTransaction().commit();

            request.getSession().setAttribute("success", "План успешно обновлен");
            response.sendRedirect(request.getContextPath() + "/plans?action=view&id=" + plan.getId());

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка обновления плана: " + e.getMessage());
            showEditForm(request, response, user);
        }
    }

    private void deletePlan(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        Session session = null;
        org.hibernate.Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Long planId = Long.parseLong(request.getParameter("id"));

            Plan plan = session.get(Plan.class, planId);

            if (plan == null) {
                throw new ServletException("План не найден");
            }

            System.out.println("=== DEBUG: Starting deletion of plan ID: " + planId);

            // Удаляем связанные отчеты
            try {
                reportGenerationService.deleteReportsForPlan(plan);
                System.out.println("=== DEBUG: Reports deleted successfully for plan ID: " + plan.getId());
            } catch (Exception e) {
                System.err.println("=== DEBUG: Error deleting reports: " + e.getMessage());
                e.printStackTrace();
            }

            // Удаляем позиции плана
            try {
                String deleteItemsHql = "DELETE FROM PlanItem pi WHERE pi.plan.id = :planId";
                int itemsDeleted = session.createQuery(deleteItemsHql)
                        .setParameter("planId", planId)
                        .executeUpdate();
                System.out.println("=== DEBUG: Deleted " + itemsDeleted + " plan items");
            } catch (Exception e) {
                System.err.println("=== DEBUG: Error deleting plan items: " + e.getMessage());
                e.printStackTrace();
            }

            // Удаляем сам план
            session.delete(plan);
            System.out.println("=== DEBUG: Plan deleted successfully");

            transaction.commit();
            System.out.println("=== DEBUG: Plan deletion completed successfully");

            request.getSession().setAttribute("success", "План и связанные отчеты успешно удалены");
            response.sendRedirect(request.getContextPath() + "/plans?action=list");

        } catch (Exception e) {
            System.err.println("=== DEBUG: Error deleting plan: " + e.getMessage());
            e.printStackTrace();

            if (transaction != null) {
                try {
                    transaction.rollback();
                    System.err.println("=== DEBUG: Transaction rolled back due to error");
                } catch (Exception rollbackEx) {
                    System.err.println("=== DEBUG: Error during rollback: " + rollbackEx.getMessage());
                }
            }

            request.setAttribute("error", "Ошибка удаления плана: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}