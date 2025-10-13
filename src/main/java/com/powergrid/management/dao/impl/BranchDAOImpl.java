package com.powergrid.management.dao.impl;

import com.powergrid.management.dao.BranchDAO;
import com.powergrid.management.model.Branch;
import com.powergrid.management.model.Enterprise;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class BranchDAOImpl extends BaseDAOImpl<Branch, Long> implements BranchDAO {

    private static final Logger logger = LoggerFactory.getLogger(BranchDAOImpl.class);

    @Override
    public Optional<Branch> findByNameAndEnterprise(String name, Enterprise enterprise) {
        logger.debug("Finding branch by name: {} and enterprise: {}", name, enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Branch> query = session.createQuery(
                    "FROM Branch b WHERE b.name = :name AND b.enterprise = :enterprise", Branch.class);
            query.setParameter("name", name);
            query.setParameter("enterprise", enterprise);
            Branch branch = query.uniqueResult();
            transaction.commit();

            logger.debug("Found branch by name and enterprise: {}", branch != null);
            return Optional.ofNullable(branch);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding branch by name and enterprise", e);
            throw new RuntimeException("Error finding branch by name and enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Branch> findByEnterprise(Enterprise enterprise) {
        logger.debug("Finding branches by enterprise: {}", enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Branch> query = session.createQuery(
                    "FROM Branch b WHERE b.enterprise = :enterprise ORDER BY b.name", Branch.class);
            query.setParameter("enterprise", enterprise);
            List<Branch> branches = query.getResultList();
            transaction.commit();

            logger.debug("Found {} branches for enterprise: {}", branches.size(), enterprise.getName());
            return branches;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding branches by enterprise: {}", enterprise.getName(), e);
            throw new RuntimeException("Error finding branches by enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Branch> findByEnterprise(Enterprise enterprise, int offset, int limit) {
        logger.debug("Finding branches by enterprise: {} with offset: {}, limit: {}",
                enterprise.getName(), offset, limit);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Branch> query = session.createQuery(
                    "FROM Branch b WHERE b.enterprise = :enterprise ORDER BY b.name", Branch.class);
            query.setParameter("enterprise", enterprise);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<Branch> branches = query.getResultList();
            transaction.commit();

            logger.debug("Found {} branches for enterprise: {}", branches.size(), enterprise.getName());
            return branches;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding branches by enterprise with pagination", e);
            throw new RuntimeException("Error finding branches by enterprise with pagination", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Branch> findByNameContaining(String nameFragment) {
        logger.debug("Finding branches by name containing: {}", nameFragment);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Branch> query = session.createQuery(
                    "FROM Branch b WHERE LOWER(b.name) LIKE LOWER(:name) ORDER BY b.name", Branch.class);
            query.setParameter("name", "%" + nameFragment + "%");
            List<Branch> branches = query.getResultList();
            transaction.commit();

            logger.debug("Found {} branches containing: {}", branches.size(), nameFragment);
            return branches;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding branches by name containing: {}", nameFragment, e);
            throw new RuntimeException("Error finding branches by name fragment", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Branch> findByNameContaining(String nameFragment, Enterprise enterprise) {
        logger.debug("Finding branches by name containing: {} and enterprise: {}", nameFragment, enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Branch> query = session.createQuery(
                    "FROM Branch b WHERE LOWER(b.name) LIKE LOWER(:name) AND b.enterprise = :enterprise ORDER BY b.name",
                    Branch.class);
            query.setParameter("name", "%" + nameFragment + "%");
            query.setParameter("enterprise", enterprise);
            List<Branch> branches = query.getResultList();
            transaction.commit();

            logger.debug("Found {} branches containing: {} for enterprise: {}",
                    branches.size(), nameFragment, enterprise.getName());
            return branches;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding branches by name and enterprise", e);
            throw new RuntimeException("Error finding branches by name and enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public boolean existsByNameAndEnterprise(String name, Enterprise enterprise) {
        logger.debug("Checking if branch exists by name: {} and enterprise: {}", name, enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Branch b WHERE b.name = :name AND b.enterprise = :enterprise", Long.class);
            query.setParameter("name", name);
            query.setParameter("enterprise", enterprise);
            Long count = query.uniqueResult();
            transaction.commit();

            boolean exists = count != null && count > 0;
            logger.debug("Branch exists by name and enterprise: {}", exists);
            return exists;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error checking branch existence by name and enterprise", e);
            throw new RuntimeException("Error checking branch existence", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long countByEnterprise(Enterprise enterprise) {
        logger.debug("Counting branches by enterprise: {}", enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Branch b WHERE b.enterprise = :enterprise", Long.class);
            query.setParameter("enterprise", enterprise);
            Long count = query.uniqueResult();
            transaction.commit();

            logger.debug("Count of branches for enterprise {}: {}", enterprise.getName(), count);
            return count != null ? count : 0;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting branches by enterprise: {}", enterprise.getName(), e);
            throw new RuntimeException("Error counting branches by enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Branch> findAllOrderedByName() {
        logger.debug("Finding all branches ordered by name");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Branch> query = session.createQuery(
                    "FROM Branch b ORDER BY b.name", Branch.class);
            List<Branch> branches = query.getResultList();
            transaction.commit();

            logger.debug("Found {} branches ordered by name", branches.size());
            return branches;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding all branches ordered by name", e);
            throw new RuntimeException("Error finding all branches ordered by name", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Branch> findAllOrderedByEnterpriseAndName() {
        logger.debug("Finding all branches ordered by enterprise and name");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Branch> query = session.createQuery(
                    "FROM Branch b ORDER BY b.enterprise.name, b.name", Branch.class);
            List<Branch> branches = query.getResultList();
            transaction.commit();

            logger.debug("Found {} branches ordered by enterprise and name", branches.size());
            return branches;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding all branches ordered by enterprise and name", e);
            throw new RuntimeException("Error finding all branches ordered by enterprise and name", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Object[]> getBranchStatistics() {
        logger.debug("Getting branch statistics");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Object[]> query = session.createQuery(
                    "SELECT b.enterprise, COUNT(b) FROM Branch b GROUP BY b.enterprise", Object[].class);
            List<Object[]> statistics = query.getResultList();
            transaction.commit();

            logger.debug("Retrieved branch statistics for {} enterprises", statistics.size());
            return statistics;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error getting branch statistics", e);
            throw new RuntimeException("Error getting branch statistics", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Branch> findBranchesWithoutUsers() {
        logger.debug("Finding branches without users");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Branch> query = session.createQuery(
                    "FROM Branch b WHERE b.id NOT IN (SELECT u.branch.id FROM User u WHERE u.branch IS NOT NULL)",
                    Branch.class);
            List<Branch> branches = query.getResultList();
            transaction.commit();

            logger.debug("Found {} branches without users", branches.size());
            return branches;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding branches without users", e);
            throw new RuntimeException("Error finding branches without users", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Branch> findBranchesWithoutPlans(Short year) {
        logger.debug("Finding branches without plans for year: {}", year);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Branch> query = session.createQuery(
                    "FROM Branch b WHERE b.id NOT IN " +
                            "(SELECT p.branch.id FROM Plan p WHERE p.planYear = :year AND p.branch IS NOT NULL)",
                    Branch.class);
            query.setParameter("year", year);
            List<Branch> branches = query.getResultList();
            transaction.commit();

            logger.debug("Found {} branches without plans for year: {}", branches.size(), year);
            return branches;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding branches without plans", e);
            throw new RuntimeException("Error finding branches without plans", e);
        } finally {
            closeSession(session);
        }
    }

    public int updateEnterpriseForBranch(Long branchId, Long newEnterpriseId) {
        logger.debug("Updating enterprise for branch id: {} to enterprise id: {}", branchId, newEnterpriseId);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "UPDATE Branch b SET b.enterprise.id = :newEnterpriseId WHERE b.id = :branchId");
            query.setParameter("newEnterpriseId", newEnterpriseId);
            query.setParameter("branchId", branchId);
            int updated = query.executeUpdate();
            transaction.commit();

            logger.debug("Updated enterprise for branch {}: {} rows affected", branchId, updated);
            return updated;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error updating enterprise for branch", e);
            throw new RuntimeException("Error updating enterprise for branch", e);
        } finally {
            closeSession(session);
        }
    }

    public int deleteByEnterprise(Enterprise enterprise) {
        logger.debug("Deleting branches by enterprise: {}", enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            // Сначала проверяем, нет ли связанных данных
            Query<Long> checkUsersQuery = session.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.branch.enterprise = :enterprise", Long.class);
            checkUsersQuery.setParameter("enterprise", enterprise);
            Long userCount = checkUsersQuery.uniqueResult();

            Query<Long> checkPlansQuery = session.createQuery(
                    "SELECT COUNT(p) FROM Plan p WHERE p.branch.enterprise = :enterprise", Long.class);
            checkPlansQuery.setParameter("enterprise", enterprise);
            Long planCount = checkPlansQuery.uniqueResult();

            Query<Long> checkReportsQuery = session.createQuery(
                    "SELECT COUNT(r) FROM Report r WHERE r.branch.enterprise = :enterprise", Long.class);
            checkReportsQuery.setParameter("enterprise", enterprise);
            Long reportCount = checkReportsQuery.uniqueResult();

            if ((userCount != null && userCount > 0) ||
                    (planCount != null && planCount > 0) ||
                    (reportCount != null && reportCount > 0)) {
                throw new IllegalStateException(
                        String.format("Cannot delete branches of enterprise %s because they have associated data: " +
                                        "%d users, %d plans, %d reports",
                                enterprise.getName(),
                                userCount != null ? userCount : 0,
                                planCount != null ? planCount : 0,
                                reportCount != null ? reportCount : 0));
            }

            Query<?> deleteQuery = session.createQuery(
                    "DELETE FROM Branch b WHERE b.enterprise = :enterprise");
            deleteQuery.setParameter("enterprise", enterprise);
            int deleted = deleteQuery.executeUpdate();
            transaction.commit();

            logger.debug("Deleted {} branches of enterprise: {}", deleted, enterprise.getName());
            return deleted;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error deleting branches by enterprise: {}", enterprise.getName(), e);
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            throw new RuntimeException("Error deleting branches by enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Branch> findBranchesWithRecentActivity(int days) {
        logger.debug("Finding branches with recent activity within {} days", days);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            // Находим филиалы, у которых есть отчеты за последние N дней
            Query<Branch> query = session.createQuery(
                    "SELECT DISTINCT r.branch FROM Report r WHERE r.reportPeriod >= CURRENT_DATE - :days " +
                            "ORDER BY r.branch.name", Branch.class);
            query.setParameter("days", days);
            List<Branch> branches = query.getResultList();
            transaction.commit();

            logger.debug("Found {} branches with recent activity within {} days", branches.size(), days);
            return branches;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding branches with recent activity", e);
            throw new RuntimeException("Error finding branches with recent activity", e);
        } finally {
            closeSession(session);
        }
    }
}
