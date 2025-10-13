package com.powergrid.management.service;

import com.powergrid.management.config.HibernateUtil;
import com.powergrid.management.model.User;
import com.powergrid.management.util.PasswordUtils;
import org.hibernate.Session;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

public class UserService {

    public User authenticate(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("=== AUTHENTICATION DEBUG ===");
            System.out.println("Username: " + username);

            User user = session.createQuery("FROM User u WHERE u.username = :username AND u.active = true", User.class)
                    .setParameter("username", username)
                    .uniqueResult();

            System.out.println("User found: " + (user != null));

            if (user != null) {
                byte[] storedBytes = user.getPasswordHash();
                System.out.println("Raw bytes length: " + storedBytes.length);

                // Получим хранимый пароль как строку
                String storedPassword = user.getPasswordHashAsString();
                System.out.println("Stored password: '" + storedPassword + "'");

                // ВАРИАНТ 1: Простое сравнение строк (если пароль хранится как plain text)
                if (storedPassword != null && storedPassword.equals(password)) {
                    System.out.println("=== SUCCESS: Plain text match ===");
                    return user;
                }

                // ВАРИАНТ 2: Сравнение hex представления (если пароль хранится как hex)
                String storedHex = bytesToHex(storedBytes);
                String inputHex = stringToHex(password);
                System.out.println("Stored hex: " + storedHex);
                System.out.println("Input hex: " + inputHex);

                if (storedHex != null && storedHex.equalsIgnoreCase(inputHex)) {
                    System.out.println("=== SUCCESS: Hex match ===");
                    return user;
                }

                // ВАРИАНТ 3: Сравнение хешей SHA-256 (если пароль хранится как хеш)
                String inputHash = hashPassword(password);
                String storedHash = bytesToHex(storedBytes); // Уже в hex формате

                System.out.println("Stored hash: " + storedHash);
                System.out.println("Input hash: " + inputHash);

                if (storedHash != null && storedHash.equalsIgnoreCase(inputHash)) {
                    System.out.println("=== SUCCESS: Hash match ===");
                    return user;
                }

                System.out.println("=== FAILED: No match found ===");
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Хеширует пароль с использованием SHA-256
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Преобразует байты в hex строку
     */
    private String bytesToHex(byte[] bytes) {
        if (bytes == null) return null;

        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Преобразует строку в hex представление
     */
    private String stringToHex(String input) {
        if (input == null) return null;

        StringBuilder hexString = new StringBuilder();
        for (char c : input.toCharArray()) {
            String hex = Integer.toHexString(c);
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Создание пользователя с проверкой сложности пароля
     */
    public void createUserWithValidation(User user) {
        // Проверяем сложность пароля
        String password = user.getPasswordHashAsString();
        if (!isPasswordValid(password)) {
            throw new IllegalArgumentException("Пароль не соответствует требованиям безопасности. " +
                    "Пароль должен содержать минимум 6 символов и включать буквы и цифры.");
        }

        // Получаем силу пароля для логирования
        int passwordStrength = PasswordUtils.getPasswordStrength(password);
        System.out.println("Создание пользователя с паролем силы: " + passwordStrength + "%");

        saveUser(user);
    }

    /**
     * Упрощенная валидация пароля
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

    /**
     * Создание пользователя с автоматической генерацией пароля
     */
    public User createUserWithGeneratedPassword(String username, String fullName, String email) {
        // Генерируем случайный пароль
        String generatedPassword = PasswordUtils.generateRandomPassword(10);
        System.out.println("Сгенерированный пароль: " + generatedPassword);

        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setActive(true);

        // Устанавливаем сгенерированный пароль как plain text
        user.setPasswordHashFromString(generatedPassword);

        saveUser(user);
        return user;
    }

    /**
     * Сброс пароля пользователя
     */
    public String resetUserPassword(Long userId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        // Генерируем новый пароль
        String newPassword = PasswordUtils.generateRandomPassword(8);

        // Сохраняем как plain text для совместимости
        user.setPasswordHashFromString(newPassword);
        updateUser(user);

        return newPassword;
    }

    /**
     * Проверка сложности пароля
     */
    public boolean validatePasswordStrength(String password) {
        return isPasswordValid(password);
    }

    /**
     * Получение силы пароля в процентах
     */
    public int getPasswordStrengthPercentage(String password) {
        return PasswordUtils.getPasswordStrength(password);
    }

    /**
     * Обновление пароля пользователя с проверкой
     */
    public void updateUserPassword(Long userId, String newPassword) {
        if (!isPasswordValid(newPassword)) {
            throw new IllegalArgumentException("Новый пароль не соответствует требованиям безопасности. " +
                    "Пароль должен содержать минимум 6 символов и включать буквы и цифры.");
        }

        User user = getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        // Сохраняем как plain text для совместимости
        user.setPasswordHashFromString(newPassword);
        updateUser(user);
    }

    /**
     * Быстрый метод создания пользователя без строгой валидации
     */
    public void createUserSimple(User user) {
        // Минимальная проверка - только длина
        String password = user.getPasswordHashAsString();
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов");
        }

        saveUser(user);
    }

    /**
     * Создание пользователя с хешированием пароля
     */
    public void createUserWithHashedPassword(User user) {
        String password = user.getPasswordHashAsString();
        if (!isPasswordValid(password)) {
            throw new IllegalArgumentException("Пароль не соответствует требованиям безопасности.");
        }

        // Хешируем пароль перед сохранением
        String hashedPassword = hashPassword(password);
        user.setPasswordHashFromString(hashedPassword);

        saveUser(user);
    }

    /**
     * Конвертирует существующие пароли в хеши
     */
    public void convertPasswordsToHashes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).list();

            for (User user : users) {
                String currentPassword = user.getPasswordHashAsString();

                // Если пароль не похож на хеш (меньше 64 символов), конвертируем
                if (currentPassword != null && currentPassword.length() < 64) {
                    String hashedPassword = hashPassword(currentPassword);
                    user.setPasswordHashFromString(hashedPassword);
                    updateUser(user);
                    System.out.println("Converted password for user: " + user.getUsername());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User u ORDER BY u.username", User.class)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public User getUserById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving user", e);
        }
    }

    public void updateUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating user", e);
        }
    }

    public void deleteUser(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting user", e);
        }
    }

    /**
     * Вспомогательный метод для отладки - показывает формат пароля
     */
    public void debugUserPassword(Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            System.out.println("=== DEBUG USER PASSWORD ===");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password bytes length: " + user.getPasswordHash().length);
            System.out.println("Password as string: '" + user.getPasswordHashAsString() + "'");
            System.out.println("Password as hex: " + bytesToHex(user.getPasswordHash()));
            System.out.println("Is likely hash: " + (user.getPasswordHashAsString().length() == 64));
        }
    }
}