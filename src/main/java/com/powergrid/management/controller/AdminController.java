package com.powergrid.management.controller;

import com.powergrid.management.model.Role;
import com.powergrid.management.model.User;
import com.powergrid.management.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminController extends HttpServlet {
    private UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/plans");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            showAdminDashboard(request, response);
        } else {
            switch (action) {
                case "users":
                    showUsersList(request, response);
                    break;
                case "edit":
                    showEditUserForm(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                case "new":
                    showNewUserForm(request, response);
                    break;
                default:
                    showAdminDashboard(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/plans");
            return;
        }

        String action = request.getParameter("action");

        if ("create".equals(action)) {
            createUser(request, response);
        } else if ("update".equals(action)) {
            updateUser(request, response);
        }
    }

    private void showAdminDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<User> users = userService.getAllUsers();
        request.setAttribute("users", users);
        request.setAttribute("roles", Role.values());
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }

    private void showUsersList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<User> users = userService.getAllUsers();
        request.setAttribute("users", users);
        request.setAttribute("roles", Role.values());
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }

    private void showNewUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("roles", Role.values());
        request.getRequestDispatcher("/admin/user-form.jsp").forward(request, response);
    }

    private void showEditUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long userId = Long.parseLong(request.getParameter("id"));
            User editUser = userService.getUserById(userId);

            if (editUser != null) {
                request.setAttribute("editUser", editUser);
                request.setAttribute("roles", Role.values());
                request.getRequestDispatcher("/admin/user-form.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Пользователь не найден");
                showUsersList(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Неверный ID пользователя");
            showUsersList(request, response);
        }
    }

    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            User newUser = new User();
            newUser.setUsername(request.getParameter("username"));
            newUser.setFullName(request.getParameter("fullName"));
            newUser.setEmail(request.getParameter("email"));
            newUser.setActive("on".equals(request.getParameter("active")));

            // Устанавливаем пароль
            String password = request.getParameter("password");
            if (password != null && !password.trim().isEmpty()) {

                // УПРОЩЕННАЯ ВАЛИДАЦИЯ ПАРОЛЯ
                if (!isPasswordValid(password)) {
                    request.setAttribute("error",
                            "Пароль не соответствует требованиям безопасности. " +
                                    "Должен содержать минимум 6 символов и включать буквы и цифры.");
                    showNewUserForm(request, response);
                    return;
                }

                // Показываем силу пароля
                int strength = userService.getPasswordStrengthPercentage(password);
                System.out.println("Создание пользователя с паролем силы: " + strength + "%");


                newUser.setPasswordHashFromString(password);
            } else {
                request.setAttribute("error", "Пароль обязателен для заполнения");
                showNewUserForm(request, response);
                return;
            }

            // Устанавливаем роли
            String[] roleNames = request.getParameterValues("roles");
            Set<Role> roles = new HashSet<>();
            if (roleNames != null) {
                for (String roleName : roleNames) {
                    try {
                        roles.add(Role.valueOf(roleName));
                    } catch (IllegalArgumentException e) {
                        // Пропускаем неверные роли
                    }
                }
            }
            newUser.setRoles(roles);

            // СОХРАНЯЕМ БЕЗ ВАЛИДАЦИИ (используем простой метод)
            userService.createUserSimple(newUser);

            request.setAttribute("success", "Пользователь успешно создан");
            showAdminDashboard(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка при создании пользователя: " + e.getMessage());
            showNewUserForm(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long userId = Long.parseLong(request.getParameter("id"));
            User user = userService.getUserById(userId);

            if (user != null) {
                user.setUsername(request.getParameter("username"));
                user.setFullName(request.getParameter("fullName"));
                user.setEmail(request.getParameter("email"));
                user.setActive("on".equals(request.getParameter("active")));

                // Обновляем пароль, если указан новый
                String password = request.getParameter("password");
                if (password != null && !password.trim().isEmpty()) {
                    // УПРОЩЕННАЯ ВАЛИДАЦИЯ ПАРОЛЯ
                    if (!isPasswordValid(password)) {
                        request.setAttribute("error",
                                "Пароль не соответствует требованиям безопасности. " +
                                        "Должен содержать минимум 6 символов и включать буквы и цифры.");
                        showEditUserForm(request, response);
                        return;
                    }
                    user.setPasswordHashFromString(password);
                }

                // Обновляем роли
                String[] roleNames = request.getParameterValues("roles");
                Set<Role> roles = new HashSet<>();
                if (roleNames != null) {
                    for (String roleName : roleNames) {
                        try {
                            roles.add(Role.valueOf(roleName));
                        } catch (IllegalArgumentException e) {
                            // Пропускаем неверные роли
                        }
                    }
                }
                user.setRoles(roles);

                userService.updateUser(user);
                request.setAttribute("success", "Пользователь успешно обновлен");
            } else {
                request.setAttribute("error", "Пользователь не найден");
            }

            showAdminDashboard(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка при обновлении пользователя: " + e.getMessage());
            showAdminDashboard(request, response);
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long userId = Long.parseLong(request.getParameter("id"));
            User currentUser = (User) request.getSession().getAttribute("user");

            // Не позволяем удалить самого себя
            if (currentUser != null && currentUser.getId().equals(userId)) {
                request.setAttribute("error", "Нельзя удалить текущего пользователя");
            } else {
                userService.deleteUser(userId);
                request.setAttribute("success", "Пользователь успешно удален");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка при удалении пользователя: " + e.getMessage());
        }

        showAdminDashboard(request, response);
    }

    /**
     * УПРОЩЕННАЯ ВАЛИДАЦИЯ ПАРОЛЯ
     */
    private boolean isPasswordValid(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;

            // Если есть и буквы и цифры - уже достаточно
            if (hasLetter && hasDigit) {
                return true;
            }
        }

        // Минимум 6 символов и содержит хотя бы буквы
        return password.length() >= 6 && hasLetter;
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            return user != null && user.isAdmin();
        }
        return false;
    }
}