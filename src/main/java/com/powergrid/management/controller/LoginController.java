package com.powergrid.management.controller;

import com.powergrid.management.model.Role;
import com.powergrid.management.model.User;
import com.powergrid.management.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class LoginController extends HttpServlet {
    private UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            redirectBasedOnRole(user, request, response);
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userService.authenticate(username, password);

            if (user != null) {
                // Отладочная информация
                System.out.println("=== LOGIN SUCCESS ===");
                System.out.println("Username: " + user.getUsername());
                System.out.println("Roles: " + user.getRoles());
                System.out.println("Is admin: " + user.isAdmin());

                // Обновляем время последнего входа
                user.setLastLogin(new java.util.Date());
                userService.updateUser(user);

                // Создаем сессию
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(30 * 60); // 30 минут

                // Редирект в зависимости от роли
                redirectBasedOnRole(user, request, response);
            } else {
                request.setAttribute("error", "Неверное имя пользователя или пароль");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка аутентификации: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void redirectBasedOnRole(User user, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("=== REDIRECT LOGIC ===");
        System.out.println("User: " + user.getUsername());
        System.out.println("Roles: " + user.getRoles());
        System.out.println("Is admin: " + user.isAdmin());

        if (user != null && user.isAdmin()) {
            System.out.println("=== REDIRECTING TO ADMIN DASHBOARD ===");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            System.out.println("=== REDIRECTING TO PLANS ===");
            response.sendRedirect(request.getContextPath() + "/plans");
        }
    }
}