package com.powergrid.management.dao;

import com.powergrid.management.model.Branch;
import com.powergrid.management.model.Enterprise;

import java.util.List;
import java.util.Optional;
import com.powergrid.management.model.Branch;

import java.io.Serializable;

public interface BranchDAO extends BaseDAO<Branch, Long> {

    Optional<Branch> findByNameAndEnterprise(String name, Enterprise enterprise);

    List<Branch> findByEnterprise(Enterprise enterprise);

    List<Branch> findByEnterprise(Enterprise enterprise, int offset, int limit);

    List<Branch> findByNameContaining(String nameFragment);

    List<Branch> findByNameContaining(String nameFragment, Enterprise enterprise);

    boolean existsByNameAndEnterprise(String name, Enterprise enterprise);

    long countByEnterprise(Enterprise enterprise);

    // Дополнительные методы
    List<Branch> findAllOrderedByName();

    List<Branch> findAllOrderedByEnterpriseAndName();

    List<Object[]> getBranchStatistics();

    List<Branch> findBranchesWithoutUsers();

    List<Branch> findBranchesWithoutPlans(Short year);

    int updateEnterpriseForBranch(Long branchId, Long newEnterpriseId);

    int deleteByEnterprise(Enterprise enterprise);

    List<Branch> findBranchesWithRecentActivity(int days);
}