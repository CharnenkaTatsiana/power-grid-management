package com.powergrid.management.controller;

import com.powergrid.management.model.Role;
import com.powergrid.management.model.User;
import com.powergrid.management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

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

    private LoginController loginController;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        loginController = new LoginController();

        // Inject mock UserService using reflection
        Field userServiceField = LoginController.class.getDeclaredField("userService");
        userServiceField.setAccessible(true);
        userServiceField.set(loginController, userService);

        // Setup test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        testUser.setRoles(new HashSet<>(Arrays.asList(Role.VIEWER)));
    }

    @Test
    void testDoGet_ShowLoginForm() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);

        loginController.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGet_RedirectIfLoggedIn() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);

        loginController.doGet(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    void testDoPost_SuccessfulLogin() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password123");
        when(userService.authenticate("testuser", "password123")).thenReturn(testUser);
        when(request.getSession()).thenReturn(session);

        loginController.doPost(request, response);

        verify(userService).updateUser(testUser);
        verify(session).setAttribute("username", "testuser");
        verify(session).setAttribute("user", testUser);
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testDoPost_FailedLogin() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(userService.authenticate("testuser", "wrongpassword")).thenReturn(null);
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);

        loginController.doPost(request, response);

        verify(request).setAttribute("error", "Неверное имя пользователя или пароль");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPost_AuthenticationException() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password");
        when(userService.authenticate(anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);

        loginController.doPost(request, response);

        verify(request).setAttribute(eq("error"), contains("Ошибка аутентификации"));
        verify(requestDispatcher).forward(request, response);
    }
}