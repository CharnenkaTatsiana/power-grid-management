package com.powergrid.management.dao;

import com.powergrid.management.model.Association;
import com.powergrid.management.model.Enterprise;

import java.util.List;
import java.util.Optional;
import com.powergrid.management.model.Enterprise;

import java.io.Serializable;

public interface EnterpriseDAO extends BaseDAO<Enterprise, Long> {

    Optional<Enterprise> findByNameAndAssociation(String name, Association association);

    List<Enterprise> findByAssociation(Association association);

    List<Enterprise> findByAssociation(Association association, int offset, int limit);

    List<Enterprise> findByNameContaining(String nameFragment);

    List<Enterprise> findByNameContaining(String nameFragment, Association association);

    boolean existsByNameAndAssociation(String name, Association association);

    long countByAssociation(Association association);

    // Дополнительные методы
    List<Enterprise> findAllOrderedByName();

    List<Enterprise> findAllOrderedByAssociationAndName();

    List<Object[]> getEnterpriseStatistics();

    List<Enterprise> findEnterprisesWithoutBranches();

    List<Enterprise> findEnterprisesWithoutUsers();

    List<Enterprise> findEnterprisesWithoutPlans(Short year);

    int updateAssociationForEnterprise(Long enterpriseId, Long newAssociationId);

    int deleteByAssociation(Association association);

    List<Enterprise> findEnterprisesWithRecentActivity(int days);

    List<Object[]> getEnterpriseBranchStatistics();

    long getTotalBranchesByEnterprise(Long enterpriseId);

    long getActiveBranchesByEnterprise(Long enterpriseId, int days);
}
