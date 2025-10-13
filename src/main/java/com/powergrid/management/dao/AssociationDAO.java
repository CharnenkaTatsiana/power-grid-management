package com.powergrid.management.dao;

import com.powergrid.management.model.Association;
import java.util.Optional;
import com.powergrid.management.model.Association;

import java.io.Serializable;

public interface AssociationDAO extends BaseDAO<Association, Long> {
    Optional<Association> findByName(String name);
    boolean existsByName(String name);
}
