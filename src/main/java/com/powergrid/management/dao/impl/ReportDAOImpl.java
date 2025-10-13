package com.powergrid.management.dao.impl;

import com.powergrid.management.dao.ReportDAO;
import com.powergrid.management.model.Association;
import com.powergrid.management.model.Branch;
import com.powergrid.management.model.Enterprise;
import com.powergrid.management.model.Report;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class ReportDAOImpl extends BaseDAOImpl<Report, Long> implements ReportDAO {

    private static final Logger logger = LoggerFactory.getLogger(ReportDAOImpl.class);

    @Override
    public List<Integer> findAvailableYears() {
        logger.debug("Finding available years with reports");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Integer> query = session.createQuery(
                    "SELECT DISTINCT YEAR(r.reportPeriod) FROM Report r ORDER BY YEAR(r.reportPeriod) DESC",
                    Integer.class);
            List<Integer> years = query.getResultList();
            transaction.commit();

            logger.debug("Found {} available years: {}", years.size(), years);
            return years;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding available years", e);
            throw new RuntimeException("Error finding available years", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public Optional<Report> findByBranchAndPeriod(Branch branch, YearMonth period) {
        logger.debug("Finding report by branch: {} and period: {}", branch.getName(), period);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Report> query = session.createQuery(
                    "FROM Report r WHERE r.branch = :branch AND r.reportPeriod = :period", Report.class);
            query.setParameter("branch", branch);
            query.setParameter("period", period);
            Report report = query.uniqueResult();
            transaction.commit();

            logger.debug("Found report by branch and period: {}", report != null);
            return Optional.ofNullable(report);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding report by branch and period", e);
            throw new RuntimeException("Error finding report by branch and period", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Report> findByBranchAndYear(Branch branch, int year) {
        logger.debug("Finding reports by branch: {} and year: {}", branch.getName(), year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Report> query = session.createQuery(
                    "FROM Report r WHERE r.branch = :branch AND YEAR(r.reportPeriod) = :year", Report.class);
            query.setParameter("branch", branch);
            query.setParameter("year", year);
            List<Report> reports = query.getResultList();
            transaction.commit();

            logger.debug("Found {} reports for branch: {} and year: {}", reports.size(), branch.getName(), year);
            return reports;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding reports by branch and year", e);
            throw new RuntimeException("Error finding reports by branch and year", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Report> findByEnterpriseAndPeriod(Enterprise enterprise, YearMonth period) {
        logger.debug("Finding reports by enterprise: {} and period: {}", enterprise.getName(), period);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Report> query = session.createQuery(
                    "SELECT r FROM Report r WHERE r.branch.enterprise = :enterprise AND r.reportPeriod = :period",
                    Report.class);
            query.setParameter("enterprise", enterprise);
            query.setParameter("period", period);
            List<Report> reports = query.getResultList();
            transaction.commit();

            logger.debug("Found {} reports for enterprise: {} and period: {}",
                    reports.size(), enterprise.getName(), period);
            return reports;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding reports by enterprise and period", e);
            throw new RuntimeException("Error finding reports by enterprise and period", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Report> findByAssociationAndPeriod(Association association, YearMonth period) {
        logger.debug("Finding reports by association: {} and period: {}", association.getName(), period);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Report> query = session.createQuery(
                    "SELECT r FROM Report r WHERE r.branch.enterprise.association = :association AND r.reportPeriod = :period",
                    Report.class);
            query.setParameter("association", association);
            query.setParameter("period", period);
            List<Report> reports = query.getResultList();
            transaction.commit();

            logger.debug("Found {} reports for association: {} and period: {}",
                    reports.size(), association.getName(), period);
            return reports;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding reports by association and period", e);
            throw new RuntimeException("Error finding reports by association and period", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Report> findByPeriod(YearMonth period) {
        logger.debug("Finding reports by period: {}", period);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Report> query = session.createQuery(
                    "FROM Report r WHERE r.reportPeriod = :period", Report.class);
            query.setParameter("period", period);
            List<Report> reports = query.getResultList();
            transaction.commit();

            logger.debug("Found {} reports for period: {}", reports.size(), period);
            return reports;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding reports by period: {}", period, e);
            throw new RuntimeException("Error finding reports by period", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Report> findByYear(int year) {
        logger.debug("Finding reports by year: {}", year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Report> query = session.createQuery(
                    "FROM Report r WHERE YEAR(r.reportPeriod) = :year", Report.class);
            query.setParameter("year", year);
            List<Report> reports = query.getResultList();
            transaction.commit();

            logger.debug("Found {} reports for year: {}", reports.size(), year);
            return reports;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding reports by year: {}", year, e);
            throw new RuntimeException("Error finding reports by year", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Report> findByBranch(Branch branch) {
        logger.debug("Finding reports by branch: {}", branch.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Report> query = session.createQuery(
                    "FROM Report r WHERE r.branch = :branch ORDER BY r.reportPeriod DESC", Report.class);
            query.setParameter("branch", branch);
            List<Report> reports = query.getResultList();
            transaction.commit();

            logger.debug("Found {} reports for branch: {}", reports.size(), branch.getName());
            return reports;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding reports by branch: {}", branch.getName(), e);
            throw new RuntimeException("Error finding reports by branch", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Report> findByEnterprise(Enterprise enterprise) {
        logger.debug("Finding reports by enterprise: {}", enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Report> query = session.createQuery(
                    "SELECT r FROM Report r WHERE r.branch.enterprise = :enterprise ORDER BY r.reportPeriod DESC",
                    Report.class);
            query.setParameter("enterprise", enterprise);
            List<Report> reports = query.getResultList();
            transaction.commit();

            logger.debug("Found {} reports for enterprise: {}", reports.size(), enterprise.getName());
            return reports;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding reports by enterprise: {}", enterprise.getName(), e);
            throw new RuntimeException("Error finding reports by enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Report> findByAssociation(Association association) {
        logger.debug("Finding reports by association: {}", association.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Report> query = session.createQuery(
                    "SELECT r FROM Report r WHERE r.branch.enterprise.association = :association ORDER BY r.reportPeriod DESC",
                    Report.class);
            query.setParameter("association", association);
            List<Report> reports = query.getResultList();
            transaction.commit();

            logger.debug("Found {} reports for association: {}", reports.size(), association.getName());
            return reports;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding reports by association: {}", association.getName(), e);
            throw new RuntimeException("Error finding reports by association", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Report> findReportsWithItemsByPeriod(YearMonth period) {
        logger.debug("Finding reports with items by period: {}", period);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Report> query = session.createQuery(
                    "SELECT DISTINCT r FROM Report r LEFT JOIN FETCH r.reportItems WHERE r.reportPeriod = :period",
                    Report.class);
            query.setParameter("period", period);
            List<Report> reports = query.getResultList();
            transaction.commit();

            logger.debug("Found {} reports with items for period: {}", reports.size(), period);
            return reports;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding reports with items by period: {}", period, e);
            throw new RuntimeException("Error finding reports with items by period", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public boolean existsByBranchAndPeriod(Branch branch, YearMonth period) {
        logger.debug("Checking if report exists for branch: {} and period: {}", branch.getName(), period);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(r) FROM Report r WHERE r.branch = :branch AND r.reportPeriod = :period",
                    Long.class);
            query.setParameter("branch", branch);
            query.setParameter("period", period);
            Long count = query.uniqueResult();
            transaction.commit();

            boolean exists = count != null && count > 0;
            logger.debug("Report exists for branch {} and period {}: {}", branch.getName(), period, exists);
            return exists;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error checking if report exists for branch: {} and period: {}", branch.getName(), period, e);
            throw new RuntimeException("Error checking if report exists", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long countByPeriod(YearMonth period) {
        logger.debug("Counting reports by period: {}", period);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(r) FROM Report r WHERE r.reportPeriod = :period",
                    Long.class);
            query.setParameter("period", period);
            Long count = query.uniqueResult();
            transaction.commit();

            long result = count != null ? count : 0L;
            logger.debug("Counted {} reports for period: {}", result, period);
            return result;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting reports by period: {}", period, e);
            throw new RuntimeException("Error counting reports by period", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long countByBranch(Branch branch) {
        logger.debug("Counting reports by branch: {}", branch.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(r) FROM Report r WHERE r.branch = :branch",
                    Long.class);
            query.setParameter("branch", branch);
            Long count = query.uniqueResult();
            transaction.commit();

            long result = count != null ? count : 0L;
            logger.debug("Counted {} reports for branch: {}", result, branch.getName());
            return result;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting reports by branch: {}", branch.getName(), e);
            throw new RuntimeException("Error counting reports by branch", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long countByEnterprise(Enterprise enterprise) {
        logger.debug("Counting reports by enterprise: {}", enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(r) FROM Report r WHERE r.branch.enterprise = :enterprise",
                    Long.class);
            query.setParameter("enterprise", enterprise);
            Long count = query.uniqueResult();
            transaction.commit();

            long result = count != null ? count : 0L;
            logger.debug("Counted {} reports for enterprise: {}", result, enterprise.getName());
            return result;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting reports by enterprise: {}", enterprise.getName(), e);
            throw new RuntimeException("Error counting reports by enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long countByAssociation(Association association) {
        logger.debug("Counting reports by association: {}", association.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(r) FROM Report r WHERE r.branch.enterprise.association = :association",
                    Long.class);
            query.setParameter("association", association);
            Long count = query.uniqueResult();
            transaction.commit();

            long result = count != null ? count : 0L;
            logger.debug("Counted {} reports for association: {}", result, association.getName());
            return result;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting reports by association: {}", association.getName(), e);
            throw new RuntimeException("Error counting reports by association", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void deleteByBranchAndPeriod(Branch branch, YearMonth period) {
        logger.debug("Deleting report by branch: {} and period: {}", branch.getName(), period);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "DELETE FROM Report r WHERE r.branch = :branch AND r.reportPeriod = :period");
            query.setParameter("branch", branch);
            query.setParameter("period", period);
            int deletedCount = query.executeUpdate();
            transaction.commit();

            logger.debug("Deleted {} reports for branch: {} and period: {}", deletedCount, branch.getName(), period);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error deleting report by branch: {} and period: {}", branch.getName(), period, e);
            throw new RuntimeException("Error deleting report by branch and period", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<YearMonth> findAvailablePeriodsByBranch(Branch branch) {
        logger.debug("Finding available periods by branch: {}", branch.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<YearMonth> query = session.createQuery(
                    "SELECT DISTINCT r.reportPeriod FROM Report r WHERE r.branch = :branch ORDER BY r.reportPeriod DESC",
                    YearMonth.class);
            query.setParameter("branch", branch);
            List<YearMonth> periods = query.getResultList();
            transaction.commit();

            logger.debug("Found {} available periods for branch: {}", periods.size(), branch.getName());
            return periods;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding available periods by branch: {}", branch.getName(), e);
            throw new RuntimeException("Error finding available periods by branch", e);
        } finally {
            closeSession(session);
        }
    }
}