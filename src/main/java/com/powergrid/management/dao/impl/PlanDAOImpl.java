package com.powergrid.management.dao.impl;

import com.powergrid.management.dao.PlanDAO;
import com.powergrid.management.model.Association;
import com.powergrid.management.model.Branch;
import com.powergrid.management.model.Enterprise;
import com.powergrid.management.model.Plan;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Year;
import java.util.List;
import java.util.Optional;

public class PlanDAOImpl extends BaseDAOImpl<Plan, Long> implements PlanDAO {

    private static final Logger logger = LoggerFactory.getLogger(PlanDAOImpl.class);

    @Override
    public Optional<Plan> findByBranchAndYear(Branch branch, Year year) {
        logger.debug("Finding plan by branch: {} and year: {}", branch.getName(), year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Plan> query = session.createQuery(
                    "FROM Plan p WHERE p.branch = :branch AND p.planYear = :year", Plan.class);
            query.setParameter("branch", branch);
            query.setParameter("year", year);
            Plan plan = query.uniqueResult();
            transaction.commit();

            logger.debug("Found plan by branch and year: {}", plan != null);
            return Optional.ofNullable(plan);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding plan by branch and year", e);
            throw new RuntimeException("Error finding plan by branch and year", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public Optional<Plan> findByEnterpriseAndYear(Enterprise enterprise, Year year) {
        logger.debug("Finding plan by enterprise: {} and year: {}", enterprise.getName(), year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Plan> query = session.createQuery(
                    "FROM Plan p WHERE p.enterprise = :enterprise AND p.planYear = :year", Plan.class);
            query.setParameter("enterprise", enterprise);
            query.setParameter("year", year);
            Plan plan = query.uniqueResult();
            transaction.commit();

            logger.debug("Found plan by enterprise and year: {}", plan != null);
            return Optional.ofNullable(plan);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding plan by enterprise and year", e);
            throw new RuntimeException("Error finding plan by enterprise and year", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public Optional<Plan> findByAssociationAndYear(Association association, Year year) {
        logger.debug("Finding plan by association: {} and year: {}", association.getName(), year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Plan> query = session.createQuery(
                    "FROM Plan p WHERE p.association = :association AND p.planYear = :year", Plan.class);
            query.setParameter("association", association);
            query.setParameter("year", year);
            Plan plan = query.uniqueResult();
            transaction.commit();

            logger.debug("Found plan by association and year: {}", plan != null);
            return Optional.ofNullable(plan);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding plan by association and year", e);
            throw new RuntimeException("Error finding plan by association and year", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Plan> findByYear(Year year) {
        logger.debug("Finding plans by year: {}", year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Plan> query = session.createQuery(
                    "FROM Plan p WHERE p.planYear = :year", Plan.class);
            query.setParameter("year", year);
            List<Plan> plans = query.getResultList();
            transaction.commit();

            logger.debug("Found {} plans for year: {}", plans.size(), year);
            return plans;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding plans by year: {}", year, e);
            throw new RuntimeException("Error finding plans by year", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Plan> findByBranch(Branch branch) {
        logger.debug("Finding plans by branch: {}", branch.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Plan> query = session.createQuery(
                    "FROM Plan p WHERE p.branch = :branch", Plan.class);
            query.setParameter("branch", branch);
            List<Plan> plans = query.getResultList();
            transaction.commit();

            logger.debug("Found {} plans for branch: {}", plans.size(), branch.getName());
            return plans;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding plans by branch: {}", branch.getName(), e);
            throw new RuntimeException("Error finding plans by branch", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Plan> findByEnterprise(Enterprise enterprise) {
        logger.debug("Finding plans by enterprise: {}", enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Plan> query = session.createQuery(
                    "FROM Plan p WHERE p.enterprise = :enterprise", Plan.class);
            query.setParameter("enterprise", enterprise);
            List<Plan> plans = query.getResultList();
            transaction.commit();

            logger.debug("Found {} plans for enterprise: {}", plans.size(), enterprise.getName());
            return plans;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding plans by enterprise: {}", enterprise.getName(), e);
            throw new RuntimeException("Error finding plans by enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Plan> findByAssociation(Association association) {
        logger.debug("Finding plans by association: {}", association.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Plan> query = session.createQuery(
                    "FROM Plan p WHERE p.association = :association", Plan.class);
            query.setParameter("association", association);
            List<Plan> plans = query.getResultList();
            transaction.commit();

            logger.debug("Found {} plans for association: {}", plans.size(), association.getName());
            return plans;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding plans by association: {}", association.getName(), e);
            throw new RuntimeException("Error finding plans by association", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Plan> findPlansWithItemsByYear(Year year) {
        logger.debug("Finding plans with items by year: {}", year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Plan> query = session.createQuery(
                    "SELECT DISTINCT p FROM Plan p LEFT JOIN FETCH p.planItems WHERE p.planYear = :year", Plan.class);
            query.setParameter("year", year);
            List<Plan> plans = query.getResultList();
            transaction.commit();

            logger.debug("Found {} plans with items for year: {}", plans.size(), year);
            return plans;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding plans with items by year: {}", year, e);
            throw new RuntimeException("Error finding plans with items by year", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public boolean existsByBranchAndYear(Branch branch, Year year) {
        logger.debug("Checking plan existence by branch: {} and year: {}", branch.getName(), year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Plan p WHERE p.branch = :branch AND p.planYear = :year", Long.class);
            query.setParameter("branch", branch);
            query.setParameter("year", year);
            Long count = query.uniqueResult();
            transaction.commit();

            boolean exists = count != null && count > 0;
            logger.debug("Plan exists by branch and year: {}", exists);
            return exists;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error checking plan existence by branch and year", e);
            throw new RuntimeException("Error checking plan existence", e);
        } finally {
            closeSession(session);
        }
    }

    // Остальные методы реализуются по аналогии...

    @Override
    public boolean existsByEnterpriseAndYear(Enterprise enterprise, Year year) {
        // Аналогично existsByBranchAndYear
        return findByEnterpriseAndYear(enterprise, year).isPresent();
    }

    @Override
    public boolean existsByAssociationAndYear(Association association, Year year) {
        // Аналогично existsByBranchAndYear
        return findByAssociationAndYear(association, year).isPresent();
    }

    @Override
    public long countByYear(Year year) {
        logger.debug("Counting plans by year: {}", year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Plan p WHERE p.planYear = :year", Long.class);
            query.setParameter("year", year);
            Long count = query.uniqueResult();
            transaction.commit();

            logger.debug("Count of plans for year {}: {}", year, count);
            return count != null ? count : 0;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting plans by year: {}", year, e);
            throw new RuntimeException("Error counting plans by year", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long countByBranch(Branch branch) {
        // Аналогично countByYear
        return findByBranch(branch).size();
    }

    @Override
    public long countByEnterprise(Enterprise enterprise) {
        // Аналогично countByYear
        return findByEnterprise(enterprise).size();
    }

    @Override
    public long countByAssociation(Association association) {
        // Аналогично countByYear
        return findByAssociation(association).size();
    }

    @Override
    public void deleteByBranchAndYear(Branch branch, Year year) {
        logger.debug("Deleting plans by branch: {} and year: {}", branch.getName(), year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "DELETE FROM Plan p WHERE p.branch = :branch AND p.planYear = :year");
            query.setParameter("branch", branch);
            query.setParameter("year", year);
            int deleted = query.executeUpdate();
            transaction.commit();

            logger.debug("Deleted {} plans by branch and year", deleted);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error deleting plans by branch and year", e);
            throw new RuntimeException("Error deleting plans by branch and year", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void deleteByEnterpriseAndYear(Enterprise enterprise, Year year) {
        // Аналогично deleteByBranchAndYear
        logger.debug("Deleting plans by enterprise: {} and year: {}", enterprise.getName(), year);
        // Реализация по аналогии с deleteByBranchAndYear
    }

    @Override
    public void deleteByAssociationAndYear(Association association, Year year) {
        // Аналогично deleteByBranchAndYear
        logger.debug("Deleting plans by association: {} and year: {}", association.getName(), year);
        // Реализация по аналогии с deleteByBranchAndYear
    }
}