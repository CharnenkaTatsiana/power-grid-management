package com.powergrid.management.dao.impl;

import com.powergrid.management.dao.BaseDAO;
import com.powergrid.management.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

/**
 * Базовая реализация DAO с использованием Hibernate
 */
public abstract class BaseDAOImpl<T, ID extends Serializable> implements BaseDAO<T, ID> {

    private static final Logger logger = LoggerFactory.getLogger(BaseDAOImpl.class);
    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseDAOImpl() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Session getCurrentSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    protected Session openSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    @Override
    public Optional<T> findById(ID id) {
        logger.debug("Finding {} by id: {}", entityClass.getSimpleName(), id);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            T entity = session.get(entityClass, id);
            transaction.commit();

            logger.debug("Found {}: {}", entityClass.getSimpleName(), entity != null);
            return Optional.ofNullable(entity);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding {} by id: {}", entityClass.getSimpleName(), id, e);
            throw new RuntimeException("Error finding entity by id", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<T> findAll() {
        logger.debug("Finding all {}", entityClass.getSimpleName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<T> query = session.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            List<T> entities = query.getResultList();
            transaction.commit();

            logger.debug("Found {} {}", entities.size(), entityClass.getSimpleName());
            return entities;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding all {}", entityClass.getSimpleName(), e);
            throw new RuntimeException("Error finding all entities", e);
        } finally {
            closeSession(session);
        }
    }
    @Override
    public List<T> findAll(int offset, int limit) {
        logger.debug("Finding {} with offset: {}, limit: {}", entityClass.getSimpleName(), offset, limit);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<T> query = session.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<T> entities = query.getResultList();
            transaction.commit();

            logger.debug("Found {} {}", entities.size(), entityClass.getSimpleName());
            return entities;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding {} with pagination", entityClass.getSimpleName(), e);
            throw new RuntimeException("Error finding entities with pagination", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public T save(T entity) {
        logger.debug("Saving {}", entityClass.getSimpleName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            session.save(entity);
            transaction.commit();

            logger.debug("Successfully saved {}", entityClass.getSimpleName());
            return entity;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error saving {}", entityClass.getSimpleName(), e);
            throw new RuntimeException("Error saving entity", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public T update(T entity) {
        logger.debug("Updating {}", entityClass.getSimpleName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            session.update(entity);
            transaction.commit();

            logger.debug("Successfully updated {}", entityClass.getSimpleName());
            return entity;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error updating {}", entityClass.getSimpleName(), e);
            throw new RuntimeException("Error updating entity", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public T saveOrUpdate(T entity) {
        logger.debug("Saving or updating {}", entityClass.getSimpleName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(entity);
            transaction.commit();

            logger.debug("Successfully saved or updated {}", entityClass.getSimpleName());
            return entity;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error saving or updating {}", entityClass.getSimpleName(), e);
            throw new RuntimeException("Error saving or updating entity", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void delete(T entity) {
        logger.debug("Deleting {}", entityClass.getSimpleName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            session.delete(entity);
            transaction.commit();

            logger.debug("Successfully deleted {}", entityClass.getSimpleName());

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error deleting {}", entityClass.getSimpleName(), e);
            throw new RuntimeException("Error deleting entity", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void deleteById(ID id) {
        logger.debug("Deleting {} by id: {}", entityClass.getSimpleName(), id);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.delete(entity);
            }
            transaction.commit();

            logger.debug("Successfully deleted {} by id: {}", entityClass.getSimpleName(), id);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error deleting {} by id: {}", entityClass.getSimpleName(), id, e);
            throw new RuntimeException("Error deleting entity by id", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public boolean existsById(ID id) {
        logger.debug("Checking existence of {} by id: {}", entityClass.getSimpleName(), id);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            T entity = session.get(entityClass, id);
            transaction.commit();

            boolean exists = entity != null;
            logger.debug("{} exists by id {}: {}", entityClass.getSimpleName(), id, exists);
            return exists;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error checking existence of {} by id: {}", entityClass.getSimpleName(), id, e);
            throw new RuntimeException("Error checking entity existence", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long count() {
        logger.debug("Counting {}", entityClass.getSimpleName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM " + entityClass.getSimpleName(), Long.class);
            Long count = query.uniqueResult();
            transaction.commit();

            logger.debug("Count of {}: {}", entityClass.getSimpleName(), count);
            return count != null ? count : 0;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting {}", entityClass.getSimpleName(), e);
            throw new RuntimeException("Error counting entities", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<T> findByExample(T example) {
        logger.debug("Finding {} by example", entityClass.getSimpleName());

        // Базовая реализация QBE - в реальном приложении нужно использовать Criteria API
        // Здесь упрощенная версия
        return findAll(); // Заглушка - в реальности нужно реализовать QBE
    }

    protected void rollbackTransaction(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
                logger.debug("Transaction rolled back successfully");
            } catch (Exception rollbackEx) {
                logger.error("Error rolling back transaction", rollbackEx);
            }
        }
    }

    protected void closeSession(Session session) {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                logger.debug("Session closed successfully");
            } catch (Exception closeEx) {
                logger.error("Error closing session", closeEx);
            }
        }
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }
}
