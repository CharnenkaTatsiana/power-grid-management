package com.powergrid.management.controller;

import com.powergrid.management.model.Role;
import com.powergrid.management.model.User;
import com.powergrid.management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private UserService userService;

    private AdminController adminController;
    private User adminUser;

    @BeforeEach
    void setUp() throws Exception {
        adminController = new AdminController();

        // Inject mock UserService using reflection
        Field userServiceField = AdminController.class.getDeclaredField("userService");
        userServiceField.setAccessible(true);
        userServiceField.set(adminController, userService);

        // Setup admin user
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setFullName("Administrator");
        adminUser.setRoles(new HashSet<>(Arrays.asList(Role.ADMIN)));
    }

    @Test
    void testDoGet_ShowDashboard() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn(null);
        when(userService.getAllUsers()).thenReturn(Arrays.asList(adminUser));
        when(request.getRequestDispatcher("/admin/dashboard.jsp")).thenReturn(requestDispatcher);

        adminController.doGet(request, response);

        verify(request).setAttribute(eq("users"), anyList());
        verify(request).setAttribute(eq("roles"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGet_ShowNewUserForm() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("new");
        when(request.getRequestDispatcher("/admin/user-form.jsp")).thenReturn(requestDispatcher);

        adminController.doGet(request, response);

        verify(request).setAttribute(eq("roles"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGet_NonAdminAccess() throws ServletException, IOException {
        User nonAdminUser = new User();
        nonAdminUser.setUsername("user");
        nonAdminUser.setRoles(new HashSet<>(Arrays.asList(Role.VIEWER)));

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(nonAdminUser);

        adminController.doGet(request, response);

        verify(response).sendRedirect(contains("/plans"));
    }

    @Test
    void testDoPost_CreateUser() throws ServletException, IOException {
        // Настраиваем все необходимые параметры
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("username")).thenReturn("newuser");
        when(request.getParameter("fullName")).thenReturn("New User");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("active")).thenReturn("on");
        when(request.getParameterValues("roles")).thenReturn(new String[]{"VIEWER"});
        when(userService.getPasswordStrengthPercentage("password123")).thenReturn(80);
        when(userService.getAllUsers()).thenReturn(Arrays.asList(adminUser));
        when(request.getRequestDispatcher("/admin/dashboard.jsp")).thenReturn(requestDispatcher);

        adminController.doPost(request, response);

        verify(userService).createUserSimple(any(User.class));
        verify(request).setAttribute(eq("success"), contains("успешно создан"));
    }

    @Test
    void testDoPost_CreateUserWithoutPassword() throws ServletException, IOException {
        // Настраиваем только необходимые параметры для этого теста
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("username")).thenReturn("newuser");
        when(request.getParameter("fullName")).thenReturn("New User");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn(""); // Пустой пароль
        when(request.getParameter("active")).thenReturn("on");
        when(request.getRequestDispatcher("/admin/user-form.jsp")).thenReturn(requestDispatcher);

        adminController.doPost(request, response);

        verify(userService, never()).createUserSimple(any(User.class));
        verify(request).setAttribute(eq("error"), eq("Пароль обязателен для заполнения"));
    }

    @Test
    void testDoPost_UpdateUser() throws ServletException, IOException {
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("existinguser");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("2");
        when(request.getParameter("username")).thenReturn("updateduser");
        when(request.getParameter("fullName")).thenReturn("Updated User");
        when(request.getParameter("email")).thenReturn("updated@example.com");
        when(request.getParameter("password")).thenReturn("newpassword");
        when(request.getParameter("active")).thenReturn("on");
        when(request.getParameterValues("roles")).thenReturn(new String[]{"ADMIN"});
        when(userService.getUserById(2L)).thenReturn(existingUser);

        // УДАЛЕНО: ненужная заглушка для getPasswordStrengthPercentage
        // when(userService.getPasswordStrengthPercentage("newpassword")).thenReturn(80);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(adminUser, existingUser));
        when(request.getRequestDispatcher("/admin/dashboard.jsp")).thenReturn(requestDispatcher);

        adminController.doPost(request, response);

        verify(userService).updateUser(any(User.class));
        verify(request).setAttribute(eq("success"), contains("успешно обновлен"));
    }

    @Test
    void testDoPost_CreateUserWithNullPassword() throws ServletException, IOException {
        // Тест с null паролем
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("username")).thenReturn("newuser");
        when(request.getParameter("fullName")).thenReturn("New User");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn(null); // Null пароль
        when(request.getParameter("active")).thenReturn("on");
        when(request.getRequestDispatcher("/admin/user-form.jsp")).thenReturn(requestDispatcher);

        adminController.doPost(request, response);

        verify(userService, never()).createUserSimple(any(User.class));
        verify(request).setAttribute(eq("error"), eq("Пароль обязателен для заполнения"));
    }

    @Test
    void testDoPost_CreateUserWithWeakPassword() throws ServletException, IOException {
        // Тест со слабым паролем
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("username")).thenReturn("newuser");
        when(request.getParameter("fullName")).thenReturn("New User");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn("123"); // Слабый пароль
        when(request.getParameter("active")).thenReturn("on");

        // УДАЛЕНО: ненужная заглушка для getPasswordStrengthPercentage
        // when(userService.getPasswordStrengthPercentage("123")).thenReturn(20);

        when(request.getRequestDispatcher("/admin/user-form.jsp")).thenReturn(requestDispatcher);

        adminController.doPost(request, response);

        verify(userService, never()).createUserSimple(any(User.class));
        verify(request).setAttribute(eq("error"), eq("Пароль не соответствует требованиям безопасности. Должен содержать минимум 6 символов и включать буквы и цифры."));
    }
}