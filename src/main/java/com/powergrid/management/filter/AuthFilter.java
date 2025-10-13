package com.powergrid.management.filter;

import com.powergrid.management.service.AdminInitializer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("=== AUTH FILTER INITIALIZATION STARTED ===");
        try {
            AdminInitializer.initializeAdmin();
            System.out.println("=== AUTH FILTER INITIALIZATION COMPLETED ===");
        } catch (Exception e) {
            System.out.println("=== AUTH FILTER INITIALIZATION FAILED ===");
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        System.out.println("=== FILTER PROCESSING PATH: " + path + " ===");

        // Разрешаем доступ к странице логина и статическим ресурсам без аутентификации
        if (path.startsWith("/login") ||
                path.startsWith("/assets/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.equals("/") ||
                path.equals("/login.jsp") ||
                path.contains(".css") ||
                path.contains(".js") ||
                path.contains(".png") ||
                path.contains(".jpg")) {

            System.out.println("=== ALLOWING ACCESS TO PUBLIC RESOURCE ===");
            chain.doFilter(request, response);
            return;
        }

        // Проверяем аутентификацию для всех других запросов
        if (session == null || session.getAttribute("username") == null) {
            System.out.println("=== USER NOT AUTHENTICATED, REDIRECTING TO LOGIN ===");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Пользователь аутентифицирован - пропускаем запрос
        System.out.println("=== USER AUTHENTICATED, ALLOWING ACCESS ===");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("=== AUTH FILTER DESTROYED ===");
    }
}