package com.powergrid.management.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH = 16;

    /**
     * Генерирует случайную соль
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Проверяет сложность пароля
     */
    public static boolean isPasswordStrong(String password) {
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

    /**
     * Генерирует случайный пароль
     */
    public static String generateRandomPassword(int length) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String all = letters + digits;

        // Гарантируем минимальную длину
        int actualLength = Math.max(length, 8);
        StringBuilder password = new StringBuilder();

        // Гарантируем хотя бы одну букву и одну цифру
        password.append(letters.charAt(RANDOM.nextInt(letters.length())));
        password.append(digits.charAt(RANDOM.nextInt(digits.length())));

        // Заполняем оставшуюся длину
        for (int i = 2; i < actualLength; i++) {
            password.append(all.charAt(RANDOM.nextInt(all.length())));
        }

        // Перемешиваем символы
        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }

    /**
     * Получает силу пароля в процентах
     */
    public static int getPasswordStrength(String password) {
        if (password == null) return 0;

        int strength = 0;

        // Длина пароля (основной критерий)
        if (password.length() >= 6) strength += 40;
        if (password.length() >= 8) strength += 30;
        if (password.length() >= 12) strength += 20;

        // Сложность (дополнительные бонусы)
        if (password.matches(".*[A-Z].*") && password.matches(".*[a-z].*")) strength += 10;
        if (password.matches(".*[0-9].*")) strength += 10;
        if (password.matches(".*[^A-Za-z0-9].*")) strength += 10;

        return Math.min(strength, 100);
    }


    public static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }
}