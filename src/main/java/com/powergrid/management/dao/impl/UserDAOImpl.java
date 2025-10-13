package com.powergrid.management.dao.impl;

import com.powergrid.management.dao.UserDAO;
import com.powergrid.management.model.Branch;
import com.powergrid.management.model.Enterprise;
import com.powergrid.management.model.Role;
import com.powergrid.management.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl extends BaseDAOImpl<User, Long> implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    @Override
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<User> query = session.createQuery(
                    "FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            User user = query.uniqueResult();
            transaction.commit();

            logger.debug("Found user by username {}: {}", username, user != null);
            return Optional.ofNullable(user);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding user by username: {}", username, e);
            throw new RuntimeException("Error finding user by username", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<User> findByRole(Role role) {
        logger.debug("Finding users by role: {}", role);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<User> query = session.createQuery(
                    "SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r = :role", User.class);
            query.setParameter("role", role);
            List<User> users = query.getResultList();
            transaction.commit();

            logger.debug("Found {} users with role: {}", users.size(), role);
            return users;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding users by role: {}", role, e);
            throw new RuntimeException("Error finding users by role", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<User> findByRoles(List<Role> roles) {
        logger.debug("Finding users by roles: {}", roles);

        if (roles == null || roles.isEmpty()) {
            return List.of();
        }

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<User> query = session.createQuery(
                    "SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r IN :roles", User.class);
            query.setParameter("roles", roles);
            List<User> users = query.getResultList();
            transaction.commit();

            logger.debug("Found {} users with roles: {}", users.size(), roles);
            return users;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding users by roles: {}", roles, e);
            throw new RuntimeException("Error finding users by roles", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<User> findByBranch(Branch branch) {
        logger.debug("Finding users by branch: {}", branch.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<User> query = session.createQuery(
                    "FROM User u WHERE u.branch = :branch", User.class);
            query.setParameter("branch", branch);
            List<User> users = query.getResultList();
            transaction.commit();

            logger.debug("Found {} users in branch: {}", users.size(), branch.getName());
            return users;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding users by branch: {}", branch.getName(), e);
            throw new RuntimeException("Error finding users by branch", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<User> findByEnterprise(Enterprise enterprise) {
        logger.debug("Finding users by enterprise: {}", enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<User> query = session.createQuery(
                    "FROM User u WHERE u.enterprise = :enterprise", User.class);
            query.setParameter("enterprise", enterprise);
            List<User> users = query.getResultList();
            transaction.commit();

            logger.debug("Found {} users in enterprise: {}", users.size(), enterprise.getName());
            return users;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding users by enterprise: {}", enterprise.getName(), e);
            throw new RuntimeException("Error finding users by enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<User> findByAssociation(Long associationId) {
        logger.debug("Finding users by association id: {}", associationId);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<User> query = session.createQuery(
                    "FROM User u WHERE u.association.id = :associationId", User.class);
            query.setParameter("associationId", associationId);
            List<User> users = query.getResultList();
            transaction.commit();

            logger.debug("Found {} users in association id: {}", users.size(), associationId);
            return users;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error finding users by association id: {}", associationId, e);
            throw new RuntimeException("Error finding users by association", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<User> findActiveUsers() {
        logger.debug("Finding active users");

        // В данной реализации считаем всех пользователей активными
        // В реальном приложении нужно добавить поле active/disabled
        return findAll();
    }

    @Override
    public List<User> findInactiveUsers() {
        logger.debug("Finding inactive users");

        // В данной реализации нет неактивных пользователей
        // В реальном приложении нужно добавить поле active/disabled
        return List.of();
    }

    @Override
    public boolean existsByUsername(String username) {
        logger.debug("Checking if user exists by username: {}", username);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM User u WHERE u.username = :username", Long.class);
            query.setParameter("username", username);
            Long count = query.uniqueResult();
            transaction.commit();

            boolean exists = count != null && count > 0;
            logger.debug("User exists by username {}: {}", username, exists);
            return exists;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error checking user existence by username: {}", username, e);
            throw new RuntimeException("Error checking user existence", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long countByRole(Role role) {
        logger.debug("Counting users by role: {}", role);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE r = :role", Long.class);
            query.setParameter("role", role);
            Long count = query.uniqueResult();
            transaction.commit();

            logger.debug("Count of users with role {}: {}", role, count);
            return count != null ? count : 0;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting users by role: {}", role, e);
            throw new RuntimeException("Error counting users by role", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long countByBranch(Branch branch) {
        logger.debug("Counting users by branch: {}", branch.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM User u WHERE u.branch = :branch", Long.class);
            query.setParameter("branch", branch);
            Long count = query.uniqueResult();
            transaction.commit();

            logger.debug("Count of users in branch {}: {}", branch.getName(), count);
            return count != null ? count : 0;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting users by branch: {}", branch.getName(), e);
            throw new RuntimeException("Error counting users by branch", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long countByEnterprise(Enterprise enterprise) {
        logger.debug("Counting users by enterprise: {}", enterprise.getName());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM User u WHERE u.enterprise = :enterprise", Long.class);
            query.setParameter("enterprise", enterprise);
            Long count = query.uniqueResult();
            transaction.commit();

            logger.debug("Count of users in enterprise {}: {}", enterprise.getName(), count);
            return count != null ? count : 0;

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error counting users by enterprise: {}", enterprise.getName(), e);
            throw new RuntimeException("Error counting users by enterprise", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void updatePassword(Long userId, String encryptedPassword) {
        logger.debug("Updating password for user id: {}", userId);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            // ВАЖНО: Используйте правильное имя поля - passwordHash
            Query<?> query = session.createQuery(
                    "UPDATE User u SET u.passwordHash = :password WHERE u.id = :userId");
            query.setParameter("password", encryptedPassword);
            query.setParameter("userId", userId);
            int updated = query.executeUpdate();
            transaction.commit();

            logger.debug("Updated password for user id {}: {} rows affected", userId, updated);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error updating password for user id: {}", userId, e);
            throw new RuntimeException("Error updating user password", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void updateLastLogin(Long userId) {
        logger.debug("Updating last login for user id: {}", userId);

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId");
            query.setParameter("lastLogin", LocalDateTime.now());
            query.setParameter("userId", userId);
            int updated = query.executeUpdate();
            transaction.commit();

            logger.debug("Updated last login for user id {}: {} rows affected", userId, updated);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logger.error("Error updating last login for user id: {}", userId, e);
            throw new RuntimeException("Error updating user last login", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void deactivateUser(Long userId) {
        logger.debug("Deactivating user id: {}", userId);


        logger.info("User deactivation requested for id: {}", userId);
    }

    @Override
    public void activateUser(Long userId) {
        logger.debug("Activating user id: {}", userId);


        logger.info("User activation requested for id: {}", userId);
    }
    public User saveWithDebug(User user) {
        logger.debug("=== DEBUG SAVE USER ===");
        logger.debug("User to save: username={}, email={}, roles={}",
                user.getUsername(), user.getEmail(), user.getRoles());

        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            logger.debug("Session opened, transaction started");

            // Проверяем, есть ли уже такой пользователь
            Long existingCount = session.createQuery(
                            "SELECT COUNT(*) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", user.getUsername())
                    .uniqueResult();

            logger.debug("Existing users with same username: {}", existingCount);

            session.saveOrUpdate(user);
            transaction.commit();

            logger.debug("✅ User saved successfully with ID: {}", user.getId());
            return user;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("❌ Error saving user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}