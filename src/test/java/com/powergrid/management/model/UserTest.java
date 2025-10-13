package com.powergrid.management.model;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setUsername("testuser");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setActive(true);

        assertNotNull(user.getCreatedAt());
        assertEquals("testuser", user.getUsername());
        assertEquals("Test User", user.getFullName());
        assertTrue(user.isActive());
    }

    @Test
    void testPasswordHashConversion() {
        User user = new User();
        String testPassword = "testpassword123";

        // Устанавливаем пароль
        user.setPasswordHashFromString(testPassword);

        // Получаем обратно - это будет HEX представление
        String result = user.getPasswordHashAsString();

        // Проверяем, что это HEX строка (должна содержать только hex символы)
        assertNotNull(result);
        assertTrue(result.matches("[0-9a-f]+"));

        // Проверяем, что байтовое представление корректно
        byte[] expectedBytes = testPassword.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expectedBytes, user.getPasswordHash());
    }

    @Test
    void testPasswordHashHexConsistency() {
        User user = new User();
        String testPassword = "testpassword123";

        // Устанавливаем пароль
        user.setPasswordHashFromString(testPassword);

        // Получаем HEX представление
        String hexResult = user.getPasswordHashAsString();

        // Проверяем, что HEX представление корректно
        assertNotNull(hexResult);
        assertEquals(hexResult.length(), testPassword.length() * 2); // Каждый байт = 2 hex символа

        // Проверяем, что тот же пароль дает тот же HEX
        User user2 = new User();
        user2.setPasswordHashFromString(testPassword);
        assertEquals(hexResult, user2.getPasswordHashAsString());
    }

    @Test
    void testRoleManagement() {
        User user = new User();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        roles.add(Role.VIEWER);

        user.setRoles(roles);

        assertTrue(user.hasRole(Role.ADMIN));
        assertTrue(user.hasRole(Role.VIEWER));
        assertFalse(user.hasRole(Role.ENTERPRISE_MANAGER));
        assertTrue(user.isAdmin());
    }

    @Test
    void testIsAdmin_WithoutAdminRole() {
        User user = new User();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.VIEWER);
        user.setRoles(roles);

        assertFalse(user.isAdmin());
    }

    @Test
    void testActiveStatus() {
        User user = new User();

        // По умолчанию активен
        assertTrue(user.isActive());

        // Устанавливаем неактивным
        user.setActive(false);
        assertFalse(user.isActive());

        // Устанавливаем null
        user.setActive(null);
        assertTrue(user.isActive()); // Должен вернуть true по умолчанию
    }

    @Test
    void testTimestamps() {
        User user = new User();
        Date testDate = new Date();

        user.setCreatedAt(testDate);
        user.setLastLogin(testDate);

        assertEquals(testDate, user.getCreatedAt());
        assertEquals(testDate, user.getLastLogin());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFullName("Test User");
        user.setActive(true);

        String result = user.toString();

        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("Test User"));
        assertTrue(result.contains("active=true"));
    }

    @Test
    void testNullPasswordHash() {
        User user = new User();

        assertNull(user.getPasswordHashAsString());

        user.setPasswordHashFromString(null);
        assertNull(user.getPasswordHash());
    }

    @Test
    void testEmptyPasswordHash() {
        User user = new User();

        user.setPasswordHashFromString("");
        String result = user.getPasswordHashAsString();

        assertNotNull(result);
        assertEquals("", new String(user.getPasswordHash(), StandardCharsets.UTF_8));
    }
}