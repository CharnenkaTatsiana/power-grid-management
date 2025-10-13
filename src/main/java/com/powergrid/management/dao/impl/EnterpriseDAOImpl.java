package com.powergrid.management.dao.impl;

import com.powergrid.management.dao.EnterpriseDAO;
import com.powergrid.management.model.Association;
import com.powergrid.management.model.Enterprise;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class EnterpriseDAOImpl extends BaseDAOImpl<Enterprise, Long> implements EnterpriseDAO {

    private static final Logger logger = LoggerFactory.getLogger(EnterpriseDAOImpl.class);

    @Override
    public Optional<Enterprise> findByNameAndAssociation(String name, Association association) {
        logger.debug("Finding enterprise by name: {} and association: {}", name, association.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Enterprise> query = session.createQuery(
                    "FROM Enterprise e WHERE e.name = :name AND e.association = :association", Enterprise.class);
            query.setParameter("name", name);
            query.setParameter("association", association);
            Enterprise enterprise = query.uniqueResult();
            transaction.commit();

            logger.debug("Found enterprise by name and association: {}", enterprise != null);
            return Optional.ofNullable(enterprise);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding enterprise by name and association", e);
            throw new RuntimeException("Error finding enterprise by name and association", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Enterprise> findByAssociation(Association association) {
        logger.debug("Finding enterprises by association: {}", association.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Enterprise> query = session.createQuery(
                    "FROM Enterprise e WHERE e.association = :association ORDER BY e.name", Enterprise.class);
            query.setParameter("association", association);
            List<Enterprise> enterprises = query.getResultList();
            transaction.commit();

            logger.debug("Found {} enterprises for association: {}", enterprises.size(), association.getName());
            return enterprises;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding enterprises by association: {}", association.getName(), e);
            throw new RuntimeException("Error finding enterprises by association", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Enterprise> findByAssociation(Association association, int offset, int limit) {
        logger.debug("Finding enterprises by association: {} with offset: {}, limit: {}",
                association.getName(), offset, limit);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Enterprise> query = session.createQuery(
                    "FROM Enterprise e WHERE e.association = :association ORDER BY e.name", Enterprise.class);
            query.setParameter("association", association);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<Enterprise> enterprises = query.getResultList();
            transaction.commit();

            logger.debug("Found {} enterprises for association: {}", enterprises.size(), association.getName());
            return enterprises;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding enterprises by association with pagination", e);
            throw new RuntimeException("Error finding enterprises by association with pagination", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Enterprise> findByNameContaining(String nameFragment) {
        logger.debug("Finding enterprises by name containing: {}", nameFragment);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Enterprise> query = session.createQuery(
                    "FROM Enterprise e WHERE LOWER(e.name) LIKE LOWER(:name) ORDER BY e.name", Enterprise.class);
            query.setParameter("name", "%" + nameFragment + "%");
            List<Enterprise> enterprises = query.getResultList();
            transaction.commit();

            logger.debug("Found {} enterprises containing: {}", enterprises.size(), nameFragment);
            return enterprises;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding enterprises by name containing: {}", nameFragment, e);
            throw new RuntimeException("Error finding enterprises by name fragment", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Enterprise> findByNameContaining(String nameFragment, Association association) {
        logger.debug("Finding enterprises by name containing: {} and association: {}", nameFragment, association.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Enterprise> query = session.createQuery(
                    "FROM Enterprise e WHERE LOWER(e.name) LIKE LOWER(:name) AND e.association = :association ORDER BY e.name",
                    Enterprise.class);
            query.setParameter("name", "%" + nameFragment + "%");
            query.setParameter("association", association);
            List<Enterprise> enterprises = query.getResultList();
            transaction.commit();

            logger.debug("Found {} enterprises containing: {} for association: {}",
                    enterprises.size(), nameFragment, association.getName());
            return enterprises;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding enterprises by name and association", e);
            throw new RuntimeException("Error finding enterprises by name and association", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public boolean existsByNameAndAssociation(String name, Association association) {
        logger.debug("Checking if enterprise exists by name: {} and association: {}", name, association.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Enterprise e WHERE e.name = :name AND e.association = :association", Long.class);
            query.setParameter("name", name);
            query.setParameter("association", association);
            Long count = query.uniqueResult();
            transaction.commit();

            boolean exists = count != null && count > 0;
            logger.debug("Enterprise exists by name and association: {}", exists);
            return exists;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error checking enterprise existence by name and association", e);
            throw new RuntimeException("Error checking enterprise existence", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long countByAssociation(Association association) {
        logger.debug("Counting enterprises by association: {}", association.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Enterprise e WHERE e.association = :association", Long.class);
            query.setParameter("association", association);
            Long count = query.uniqueResult();
            transaction.commit();

            logger.debug("Count of enterprises for association {}: {}", association.getName(), count);
            return count != null ? count : 0;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting enterprises by association: {}", association.getName(), e);
            throw new RuntimeException("Error counting enterprises by association", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Enterprise> findAllOrderedByName() {
        logger.debug("Finding all enterprises ordered by name");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Enterprise> query = session.createQuery(
                    "FROM Enterprise e ORDER BY e.name", Enterprise.class);
            List<Enterprise> enterprises = query.getResultList();
            transaction.commit();

            logger.debug("Found {} enterprises ordered by name", enterprises.size());
            return enterprises;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding all enterprises ordered by name", e);
            throw new RuntimeException("Error finding all enterprises ordered by name", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Enterprise> findAllOrderedByAssociationAndName() {
        logger.debug("Finding all enterprises ordered by association and name");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Enterprise> query = session.createQuery(
                    "FROM Enterprise e ORDER BY e.association.name, e.name", Enterprise.class);
            List<Enterprise> enterprises = query.getResultList();
            transaction.commit();

            logger.debug("Found {} enterprises ordered by association and name", enterprises.size());
            return enterprises;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding all enterprises ordered by association and name", e);
            throw new RuntimeException("Error finding all enterprises ordered by association and name", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Object[]> getEnterpriseStatistics() {
        logger.debug("Getting enterprise statistics");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Object[]> query = session.createQuery(
                    "SELECT e.association, COUNT(e) FROM Enterprise e GROUP BY e.association", Object[].class);
            List<Object[]> statistics = query.getResultList();
            transaction.commit();

            logger.debug("Retrieved enterprise statistics for {} associations", statistics.size());
            return statistics;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error getting enterprise statistics", e);
            throw new RuntimeException("Error getting enterprise statistics", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Enterprise> findEnterprisesWithoutBranches() {
        logger.debug("Finding enterprises without branches");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Enterprise> query = session.createQuery(
                    "FROM Enterprise e WHERE e.id NOT IN (SELECT b.enterprise.id FROM Branch b)",
                    Enterprise.class);
            List<Enterprise> enterprises = query.getResultList();
            transaction.commit();

            logger.debug("Found {} enterprises without branches", enterprises.size());
            return enterprises;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding enterprises without branches", e);
            throw new RuntimeException("Error finding enterprises without branches", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Enterprise> findEnterprisesWithoutUsers() {
        logger.debug("Finding enterprises without users");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Enterprise> query = session.createQuery(
                    "FROM Enterprise e WHERE e.id NOT IN (SELECT u.enterprise.id FROM User u WHERE u.enterprise IS NOT NULL)",
                    Enterprise.class);
            List<Enterprise> enterprises = query.getResultList();
            transaction.commit();

            logger.debug("Found {} enterprises without users", enterprises.size());
            return enterprises;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding enterprises without users", e);
            throw new RuntimeException("Error finding enterprises without users", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Enterprise> findEnterprisesWithoutPlans(Short year) {
        logger.debug("Finding enterprises without plans for year: {}", year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Enterprise> query = session.createQuery(
                    "FROM Enterprise e WHERE e.id NOT IN " +
                            "(SELECT p.enterprise.id FROM Plan p WHERE p.planYear = :year AND p.enterprise IS NOT NULL)",
                    Enterprise.class);
            query.setParameter("year", year);
            List<Enterprise> enterprises = query.getResultList();
            transaction.commit();

            logger.debug("Found {} enterprises without plans for year: {}", enterprises.size(), year);
            return enterprises;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding enterprises without plans", e);
            throw new RuntimeException("Error finding enterprises without plans", e);
        } finally {
            closeSession(session);
        }
    }

    public int updateAssociationForEnterprise(Long enterpriseId, Long newAssociationId) {
        logger.debug("Updating association for enterprise id: {} to association id: {}", enterpriseId, newAssociationId);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "UPDATE Enterprise e SET e.association.id = :newAssociationId WHERE e.id = :enterpriseId");
            query.setParameter("newAssociationId", newAssociationId);
            query.setParameter("enterpriseId", enterpriseId);
            int updated = query.executeUpdate();
            transaction.commit();

            logger.debug("Updated association for enterprise {}: {} rows affected", enterpriseId, updated);
            return updated;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error updating association for enterprise", e);
            throw new RuntimeException("Error updating association for enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    public int deleteByAssociation(Association association) {
        logger.debug("Deleting enterprises by association: {}", association.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            // Сначала проверяем, нет ли связанных данных
            Query<Long> checkBranchesQuery = session.createQuery(
                    "SELECT COUNT(b) FROM Branch b WHERE b.enterprise.association = :association", Long.class);
            checkBranchesQuery.setParameter("association", association);
            Long branchCount = checkBranchesQuery.uniqueResult();

            Query<Long> checkUsersQuery = session.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.enterprise.association = :association", Long.class);
            checkUsersQuery.setParameter("association", association);
            Long userCount = checkUsersQuery.uniqueResult();

            Query<Long> checkPlansQuery = session.createQuery(
                    "SELECT COUNT(p) FROM Plan p WHERE p.enterprise.association = :association", Long.class);
            checkPlansQuery.setParameter("association", association);
            Long planCount = checkPlansQuery.uniqueResult();

            if ((branchCount != null && branchCount > 0) ||
                    (userCount != null && userCount > 0) ||
                    (planCount != null && planCount > 0)) {
                throw new IllegalStateException(
                        String.format("Cannot delete enterprises of association %s because they have associated data: " +
                                        "%d branches, %d users, %d plans",
                                association.getName(),
                                branchCount != null ? branchCount : 0,
                                userCount != null ? userCount : 0,
                                planCount != null ? planCount : 0));
            }

            Query<?> deleteQuery = session.createQuery(
                    "DELETE FROM Enterprise e WHERE e.association = :association");
            deleteQuery.setParameter("association", association);
            int deleted = deleteQuery.executeUpdate();
            transaction.commit();

            logger.debug("Deleted {} enterprises of association: {}", deleted, association.getName());
            return deleted;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error deleting enterprises by association: {}", association.getName(), e);
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            throw new RuntimeException("Error deleting enterprises by association", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Enterprise> findEnterprisesWithRecentActivity(int days) {
        logger.debug("Finding enterprises with recent activity within {} days", days);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            // Находим предприятия, у которых есть отчеты за последние N дней
            Query<Enterprise> query = session.createQuery(
                    "SELECT DISTINCT r.branch.enterprise FROM Report r WHERE r.reportPeriod >= CURRENT_DATE - :days " +
                            "ORDER BY r.branch.enterprise.name", Enterprise.class);
            query.setParameter("days", days);
            List<Enterprise> enterprises = query.getResultList();
            transaction.commit();

            logger.debug("Found {} enterprises with recent activity within {} days", enterprises.size(), days);
            return enterprises;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding enterprises with recent activity", e);
            throw new RuntimeException("Error finding enterprises with recent activity", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Object[]> getEnterpriseBranchStatistics() {
        logger.debug("Getting enterprise branch statistics");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Object[]> query = session.createQuery(
                    "SELECT e, COUNT(b), COALESCE(SUM(CASE WHEN b.id IN " +
                            "(SELECT u.branch.id FROM User u WHERE u.branch IS NOT NULL) THEN 1 ELSE 0 END), 0) " +
                            "FROM Enterprise e LEFT JOIN e.branches b GROUP BY e", Object[].class);
            List<Object[]> statistics = query.getResultList();
            transaction.commit();

            logger.debug("Retrieved enterprise branch statistics for {} enterprises", statistics.size());
            return statistics;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error getting enterprise branch statistics", e);
            throw new RuntimeException("Error getting enterprise branch statistics", e);
        } finally {
            closeSession(session);
        }
    }

    public long getTotalBranchesByEnterprise(Long enterpriseId) {
        logger.debug("Getting total branches count for enterprise id: {}", enterpriseId);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(b) FROM Branch b WHERE b.enterprise.id = :enterpriseId", Long.class);
            query.setParameter("enterpriseId", enterpriseId);
            Long count = query.uniqueResult();
            transaction.commit();

            logger.debug("Total branches for enterprise {}: {}", enterpriseId, count);
            return count != null ? count : 0;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error getting total branches by enterprise", e);
            throw new RuntimeException("Error getting total branches by enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    public long getActiveBranchesByEnterprise(Long enterpriseId, int days) {
        logger.debug("Getting active branches count for enterprise id: {} within {} days", enterpriseId, days);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(DISTINCT r.branch) FROM Report r " +
                            "WHERE r.branch.enterprise.id = :enterpriseId AND r.reportPeriod >= CURRENT_DATE - :days",
                    Long.class);
            query.setParameter("enterpriseId", enterpriseId);
            query.setParameter("days", days);
            Long count = query.uniqueResult();
            transaction.commit();

            logger.debug("Active branches for enterprise {} within {} days: {}", enterpriseId, days, count);
            return count != null ? count : 0;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error getting active branches by enterprise", e);
            throw new RuntimeException("Error getting active branches by enterprise", e);
        } finally {
            closeSession(session);
        }
    }
}
