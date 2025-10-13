package com.powergrid.management.dao.impl;

import com.powergrid.management.dao.WorkTypeDAO;
import com.powergrid.management.model.NetworkType;
import com.powergrid.management.model.WorkType;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class WorkTypeDAOImpl extends BaseDAOImpl<WorkType, Long> implements WorkTypeDAO {

    private static final Logger logger = LoggerFactory.getLogger(WorkTypeDAOImpl.class);

    @Override
    public Optional<WorkType> findByName(String name) {
        logger.debug("Finding work type by name: {}", name);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<WorkType> query = session.createQuery(
                    "FROM WorkType wt WHERE wt.name = :name", WorkType.class);
            query.setParameter("name", name);
            WorkType workType = query.uniqueResult();
            transaction.commit();

            logger.debug("Found work type by name {}: {}", name, workType != null);
            return Optional.ofNullable(workType);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding work type by name: {}", name, e);
            throw new RuntimeException("Error finding work type by name", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<WorkType> findByNetworkType(NetworkType networkType) {
        logger.debug("Finding work types by network type: {}", networkType);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<WorkType> query = session.createQuery(
                    "FROM WorkType wt WHERE wt.networkType = :networkType ORDER BY wt.name", WorkType.class);
            query.setParameter("networkType", networkType);
            List<WorkType> workTypes = query.getResultList();
            transaction.commit();

            logger.debug("Found {} work types for network type: {}", workTypes.size(), networkType);
            return workTypes;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding work types by network type: {}", networkType, e);
            throw new RuntimeException("Error finding work types by network type", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<WorkType> findByNetworkType(NetworkType networkType, int offset, int limit) {
        logger.debug("Finding work types by network type: {} with offset: {}, limit: {}",
                networkType, offset, limit);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<WorkType> query = session.createQuery(
                    "FROM WorkType wt WHERE wt.networkType = :networkType ORDER BY wt.name", WorkType.class);
            query.setParameter("networkType", networkType);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<WorkType> workTypes = query.getResultList();
            transaction.commit();

            logger.debug("Found {} work types for network type: {}", workTypes.size(), networkType);
            return workTypes;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding work types by network type with pagination", e);
            throw new RuntimeException("Error finding work types by network type with pagination", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public boolean existsByName(String name) {
        logger.debug("Checking if work type exists by name: {}", name);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM WorkType wt WHERE wt.name = :name", Long.class);
            query.setParameter("name", name);
            Long count = query.uniqueResult();
            transaction.commit();

            boolean exists = count != null && count > 0;
            logger.debug("Work type exists by name {}: {}", name, exists);
            return exists;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error checking work type existence by name: {}", name, e);
            throw new RuntimeException("Error checking work type existence", e);
        } finally {
            closeSession(session);
        }
    }

    public List<WorkType> findByNameContaining(String nameFragment) {
        logger.debug("Finding work types by name containing: {}", nameFragment);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<WorkType> query = session.createQuery(
                    "FROM WorkType wt WHERE LOWER(wt.name) LIKE LOWER(:name) ORDER BY wt.name", WorkType.class);
            query.setParameter("name", "%" + nameFragment + "%");
            List<WorkType> workTypes = query.getResultList();
            transaction.commit();

            logger.debug("Found {} work types containing: {}", workTypes.size(), nameFragment);
            return workTypes;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding work types by name containing: {}", nameFragment, e);
            throw new RuntimeException("Error finding work types by name fragment", e);
        } finally {
            closeSession(session);
        }
    }

    public List<WorkType> findByNameContaining(String nameFragment, NetworkType networkType) {
        logger.debug("Finding work types by name containing: {} and network type: {}", nameFragment, networkType);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<WorkType> query = session.createQuery(
                    "FROM WorkType wt WHERE LOWER(wt.name) LIKE LOWER(:name) AND wt.networkType = :networkType ORDER BY wt.name",
                    WorkType.class);
            query.setParameter("name", "%" + nameFragment + "%");
            query.setParameter("networkType", networkType);
            List<WorkType> workTypes = query.getResultList();
            transaction.commit();

            logger.debug("Found {} work types containing: {} for network type: {}",
                    workTypes.size(), nameFragment, networkType);
            return workTypes;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding work types by name and network type", e);
            throw new RuntimeException("Error finding work types by name and network type", e);
        } finally {
            closeSession(session);
        }
    }

    public long countByNetworkType(NetworkType networkType) {
        logger.debug("Counting work types by network type: {}", networkType);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM WorkType wt WHERE wt.networkType = :networkType", Long.class);
            query.setParameter("networkType", networkType);
            Long count = query.uniqueResult();
            transaction.commit();

            logger.debug("Count of work types for network type {}: {}", networkType, count);
            return count != null ? count : 0;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting work types by network type: {}", networkType, e);
            throw new RuntimeException("Error counting work types by network type", e);
        } finally {
            closeSession(session);
        }
    }

    public List<WorkType> findAllOrderedByName() {
        logger.debug("Finding all work types ordered by name");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<WorkType> query = session.createQuery(
                    "FROM WorkType wt ORDER BY wt.networkType, wt.name", WorkType.class);
            List<WorkType> workTypes = query.getResultList();
            transaction.commit();

            logger.debug("Found {} work types ordered by name", workTypes.size());
            return workTypes;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding all work types ordered by name", e);
            throw new RuntimeException("Error finding all work types ordered by name", e);
        } finally {
            closeSession(session);
        }
    }

    public List<WorkType> findAllOrderedByNetworkTypeAndName() {
        logger.debug("Finding all work types ordered by network type and name");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<WorkType> query = session.createQuery(
                    "FROM WorkType wt ORDER BY wt.networkType DESC, wt.name ASC", WorkType.class);
            List<WorkType> workTypes = query.getResultList();
            transaction.commit();

            logger.debug("Found {} work types ordered by network type and name", workTypes.size());
            return workTypes;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding all work types ordered by network type and name", e);
            throw new RuntimeException("Error finding all work types ordered by network type and name", e);
        } finally {
            closeSession(session);
        }
    }

    public List<Object[]> getWorkTypeStatistics() {
        logger.debug("Getting work type statistics");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Object[]> query = session.createQuery(
                    "SELECT wt.networkType, COUNT(wt) FROM WorkType wt GROUP BY wt.networkType", Object[].class);
            List<Object[]> statistics = query.getResultList();
            transaction.commit();

            logger.debug("Retrieved work type statistics for {} network types", statistics.size());
            return statistics;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error getting work type statistics", e);
            throw new RuntimeException("Error getting work type statistics", e);
        } finally {
            closeSession(session);
        }
    }

    public boolean isWorkTypeUsedInPlans(Long workTypeId) {
        logger.debug("Checking if work type with id {} is used in plans", workTypeId);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(pi) FROM PlanItem pi WHERE pi.workType.id = :workTypeId", Long.class);
            query.setParameter("workTypeId", workTypeId);
            Long count = query.uniqueResult();
            transaction.commit();

            boolean isUsed = count != null && count > 0;
            logger.debug("Work type with id {} is used in plans: {}", workTypeId, isUsed);
            return isUsed;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error checking if work type is used in plans", e);
            throw new RuntimeException("Error checking work type usage in plans", e);
        } finally {
            closeSession(session);
        }
    }

    public boolean isWorkTypeUsedInReports(Long workTypeId) {
        logger.debug("Checking if work type with id {} is used in reports", workTypeId);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(ri) FROM ReportItem ri WHERE ri.workType.id = :workTypeId", Long.class);
            query.setParameter("workTypeId", workTypeId);
            Long count = query.uniqueResult();
            transaction.commit();

            boolean isUsed = count != null && count > 0;
            logger.debug("Work type with id {} is used in reports: {}", workTypeId, isUsed);
            return isUsed;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error checking if work type is used in reports", e);
            throw new RuntimeException("Error checking work type usage in reports", e);
        } finally {
            closeSession(session);
        }
    }

    public List<WorkType> findUnusedWorkTypes() {
        logger.debug("Finding unused work types");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            // Находим типы работ, которые не используются ни в планах, ни в отчетах
            Query<WorkType> query = session.createQuery(
                    "FROM WorkType wt WHERE wt.id NOT IN (" +
                            "SELECT DISTINCT pi.workType.id FROM PlanItem pi) " +
                            "AND wt.id NOT IN (" +
                            "SELECT DISTINCT ri.workType.id FROM ReportItem ri)", WorkType.class);
            List<WorkType> unusedWorkTypes = query.getResultList();
            transaction.commit();

            logger.debug("Found {} unused work types", unusedWorkTypes.size());
            return unusedWorkTypes;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding unused work types", e);
            throw new RuntimeException("Error finding unused work types", e);
        } finally {
            closeSession(session);
        }
    }

    public int bulkUpdateNetworkType(NetworkType oldNetworkType, NetworkType newNetworkType) {
        logger.debug("Bulk updating work types from network type {} to {}", oldNetworkType, newNetworkType);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "UPDATE WorkType wt SET wt.networkType = :newNetworkType WHERE wt.networkType = :oldNetworkType");
            query.setParameter("newNetworkType", newNetworkType);
            query.setParameter("oldNetworkType", oldNetworkType);
            int updated = query.executeUpdate();
            transaction.commit();

            logger.debug("Bulk updated {} work types from {} to {}", updated, oldNetworkType, newNetworkType);
            return updated;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error bulk updating work types network type", e);
            throw new RuntimeException("Error bulk updating work types network type", e);
        } finally {
            closeSession(session);
        }
    }

    public int deleteByNetworkType(NetworkType networkType) {
        logger.debug("Deleting work types by network type: {}", networkType);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            // Сначала проверяем, не используются ли эти типы работ
            Query<Long> checkQuery = session.createQuery(
                    "SELECT COUNT(pi) FROM PlanItem pi WHERE pi.workType.networkType = :networkType", Long.class);
            checkQuery.setParameter("networkType", networkType);
            Long planUsage = checkQuery.uniqueResult();

            Query<Long> checkQuery2 = session.createQuery(
                    "SELECT COUNT(ri) FROM ReportItem ri WHERE ri.workType.networkType = :networkType", Long.class);
            checkQuery2.setParameter("networkType", networkType);
            Long reportUsage = checkQuery2.uniqueResult();

            if ((planUsage != null && planUsage > 0) || (reportUsage != null && reportUsage > 0)) {
                throw new IllegalStateException(
                        String.format("Cannot delete work types of network type %s because they are used in %d plans and %d reports",
                                networkType, planUsage != null ? planUsage : 0, reportUsage != null ? reportUsage : 0));
            }

            Query<?> deleteQuery = session.createQuery(
                    "DELETE FROM WorkType wt WHERE wt.networkType = :networkType");
            deleteQuery.setParameter("networkType", networkType);
            int deleted = deleteQuery.executeUpdate();
            transaction.commit();

            logger.debug("Deleted {} work types of network type: {}", deleted, networkType);
            return deleted;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error deleting work types by network type: {}", networkType, e);
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            throw new RuntimeException("Error deleting work types by network type", e);
        } finally {
            closeSession(session);
        }
    }

    public void initializeDefaultWorkTypes() {
        logger.info("Initializing default work types");

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            // Основная сеть
            createWorkTypeIfNotExists(session, "Ремонт ВЛ 35 кВ", NetworkType.MAIN_NETWORK);
            createWorkTypeIfNotExists(session, "Ремонт ВЛ 110 кВ", NetworkType.MAIN_NETWORK);
            createWorkTypeIfNotExists(session, "Ремонт ВЛ 220 кВ", NetworkType.MAIN_NETWORK);
            createWorkTypeIfNotExists(session, "Ремонт ВЛ 330 кВ", NetworkType.MAIN_NETWORK);
            createWorkTypeIfNotExists(session, "Ремонт ВЛ 750 кВ", NetworkType.MAIN_NETWORK);
            createWorkTypeIfNotExists(session, "Замена опор на ВЛ 35 кВ и выше", NetworkType.MAIN_NETWORK);
            createWorkTypeIfNotExists(session, "Замена провода на ВЛ 35 кВ и выше", NetworkType.MAIN_NETWORK);
            createWorkTypeIfNotExists(session, "Замена г/троса на ВЛ 35 кВ и выше", NetworkType.MAIN_NETWORK);


            // Распределительная сеть
            createWorkTypeIfNotExists(session, "Ремонт ВЛ 0,4 кВ", NetworkType.DISTRIBUTION_NETWORK);
            createWorkTypeIfNotExists(session, "Ремонт ВЛ 10(6) кВ", NetworkType.DISTRIBUTION_NETWORK);
            createWorkTypeIfNotExists(session, "Замена опор на ВЛ 10(6) кВ", NetworkType.DISTRIBUTION_NETWORK);
            createWorkTypeIfNotExists(session, "Замена опор на ВЛ 0,4 кВ", NetworkType.DISTRIBUTION_NETWORK);
            createWorkTypeIfNotExists(session, "Замена провода на ВЛ 10(6) кВ", NetworkType.DISTRIBUTION_NETWORK);
            createWorkTypeIfNotExists(session, "Замена провода на ВЛ 0,4 кВ", NetworkType.DISTRIBUTION_NETWORK);
            createWorkTypeIfNotExists(session, "Ремонт РП,ТП, КТП", NetworkType.DISTRIBUTION_NETWORK);


            transaction.commit();
            logger.info("Default work types initialization completed");

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error initializing default work types", e);
            throw new RuntimeException("Error initializing default work types", e);
        } finally {
            closeSession(session);
        }
    }

    private void createWorkTypeIfNotExists(Session session, String name, NetworkType networkType) {
        Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM WorkType wt WHERE wt.name = :name", Long.class);
        query.setParameter("name", name);
        Long count = query.uniqueResult();

        if (count == null || count == 0) {
            WorkType workType = new WorkType();
            workType.setName(name);
            workType.setNetworkType(networkType);
            session.save(workType);
            logger.debug("Created work type: {} - {}", networkType, name);
        }
    }
}
