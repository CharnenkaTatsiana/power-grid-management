package com.powergrid.management.controller;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Получаем текущую сессию, не создавая новую
        HttpSession session = request.getSession(false);

        if (session != null) {
            // Получаем имя пользователя для логирования
            String username = (String) session.getAttribute("username");

            // Очищаем все атрибуты сессии
            session.removeAttribute("username");
            session.removeAttribute("user");
            session.removeAttribute("userRoles");
            session.removeAttribute("branch");
            session.removeAttribute("enterprise");
            session.removeAttribute("association");

            // Инвалидируем сессию
            session.invalidate();

            System.out.println("=== DEBUG: User '" + username + "' logged out successfully");
        }

        // Удаляем куки если они есть
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName()) || "rememberMe".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }

        // Перенаправляем на страницу логина с сообщением об успешном выходе
        response.sendRedirect(request.getContextPath() + "/login.jsp?message=logout_success");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
