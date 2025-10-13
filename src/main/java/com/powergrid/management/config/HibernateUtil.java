package com.powergrid.management.config;

import com.powergrid.management.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // настройки Hibernate для MySQL
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                settings.put(Environment.URL, "jdbc:mysql://localhost:3306/powergrid_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                settings.put(Environment.USER, "powergrid_user");
                settings.put(Environment.PASS, "powergrid_password");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.FORMAT_SQL, "true");
                settings.put(Environment.HBM2DDL_AUTO, "update");
                settings.put(Environment.AUTOCOMMIT, "false");


                settings.put(Environment.C3P0_MIN_SIZE, "5");
                settings.put(Environment.C3P0_MAX_SIZE, "20");
                settings.put(Environment.C3P0_TIMEOUT, "300");
                settings.put(Environment.C3P0_MAX_STATEMENTS, "50");

                configuration.setProperties(settings);

                // Добавление аннотированных классов
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Association.class);
                configuration.addAnnotatedClass(Enterprise.class);
                configuration.addAnnotatedClass(Branch.class);
                configuration.addAnnotatedClass(WorkType.class);
                configuration.addAnnotatedClass(Plan.class);
                configuration.addAnnotatedClass(PlanItem.class);
                configuration.addAnnotatedClass(Report.class);
                configuration.addAnnotatedClass(ReportItem.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                logger.info("Hibernate SessionFactory created successfully for MySQL");

            } catch (Exception e) {
                logger.error("Failed to create Hibernate SessionFactory", e);
                // Более детальная информация об ошибке
                if (e.getCause() != null) {
                    logger.error("Root cause: {}", e.getCause().getMessage());
                }
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }


    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed");
        }
    }

    public static void initializeDatabase() {
        try (var session = getSessionFactory().openSession()) {
            logger.info("MySQL database connection test successful");
        } catch (Exception e) {
            logger.error("MySQL database connection test failed", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static void initializeTestData() {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            // Проверяем, есть ли уже пользователь
            User existingUser = session.createQuery("FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", "testuser")
                    .uniqueResult();

            if (existingUser == null) {
                User testUser = new User();
                testUser.setUsername("testuser");
                testUser.setPasswordHashFromString("password123");
                testUser.setFullName("Test User");
                testUser.setEmail("test@example.com");
                testUser.setActive(true);

                session.persist(testUser);
                System.out.println("=== DEBUG: Test user created successfully");
            } else {
                System.out.println("=== DEBUG: Test user already exists");
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("=== DEBUG: Error creating test data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

