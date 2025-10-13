package com.powergrid.management.service;

import com.powergrid.management.config.HibernateUtil;
import com.powergrid.management.model.*;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DatabaseResetService {

    public void resetDatabase() {
        System.out.println("=== Starting database reset ===");

        Session session = null;
        org.hibernate.Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Удаляем данные в правильном порядке (с учетом foreign key constraints)
            deleteData(session, "ReportItem");
            deleteData(session, "Report");
            deleteData(session, "PlanItem");
            deleteData(session, "Plan");
            deleteData(session, "User");
            deleteData(session, "Branch");
            deleteData(session, "Enterprise");
            deleteData(session, "Association");
            deleteData(session, "WorkType");



            transaction.commit();
            System.out.println("=== Database reset completed successfully ===");

        } catch (Exception e) {
            System.err.println("=== Error during database reset: " + e.getMessage());
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    private void deleteData(Session session, String entityName) {
        try {
            String hql = "DELETE FROM " + entityName;
            int count = session.createQuery(hql).executeUpdate();
            System.out.println("Deleted " + count + " records from " + entityName);
        } catch (Exception e) {
            System.err.println("Error deleting from " + entityName + ": " + e.getMessage());
        }
    }


}