package com.powergrid.management.dao.impl;

import com.powergrid.management.dao.AssociationDAO;
import com.powergrid.management.model.Association;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Optional;

public class AssociationDAOImpl extends BaseDAOImpl<Association, Long> implements AssociationDAO {

    @Override
    public Optional<Association> findByName(String name) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();

            Query<Association> query = session.createQuery(
                    "FROM Association a WHERE a.name = :name", Association.class);
            query.setParameter("name", name);
            Association association = query.uniqueResult();
            transaction.commit();

            return Optional.ofNullable(association);

        } catch (Exception e) {
            rollbackTransaction(transaction);
            throw new RuntimeException("Error finding association by name", e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public boolean existsByName(String name) {
        return findByName(name).isPresent();
    }
}