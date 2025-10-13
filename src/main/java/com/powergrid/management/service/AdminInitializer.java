package com.powergrid.management.service;

import com.powergrid.management.config.HibernateUtil;
import com.powergrid.management.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class AdminInitializer {

    public static void initializeAdmin() {
        System.out.println("=== STARTING ADMIN INITIALIZATION ===");

        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            System.out.println("=== CHECKING IF ADMIN EXISTS ===");

            // Проверяем, существует ли уже администратор
            User existingAdmin = session.createQuery("FROM User u WHERE u.username = 'admin'", User.class)
                    .uniqueResult();

            if (existingAdmin == null) {
                System.out.println("=== CREATING NEW ADMIN USER ===");

                // Создаем нового администратора
                User admin = new User();
                admin.setUsername("admin");
                admin.setFullName("Администратор Системы");
                admin.setEmail("admin@powergrid.com");
                admin.setActive(true);

                // Устанавливаем пароль
                String hashedPassword = hashPassword("password123");
                admin.setPasswordHashFromString(hashedPassword);

                session.save(admin);
                transaction.commit();

                System.out.println("=== ADMIN CREATED SUCCESSFULLY ===");
                System.out.println("Username: admin");
                System.out.println("Password: password123");
                System.out.println("Hashed password: " + hashedPassword);

            } else {
                System.out.println("=== ADMIN ALREADY EXISTS ===");
                System.out.println("Admin ID: " + existingAdmin.getId());
                System.out.println("Admin username: " + existingAdmin.getUsername());
                System.out.println("Is active: " + existingAdmin.isActive());
            }

        } catch (Exception e) {
            System.out.println("=== ERROR CREATING ADMIN ===");
            e.printStackTrace();

            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        System.out.println("=== ADMIN INITIALIZATION COMPLETED ===");
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}