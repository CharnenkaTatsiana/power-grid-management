package com.powergrid.management.dao;

import com.powergrid.management.model.Association;
import com.powergrid.management.model.Branch;
import com.powergrid.management.model.Enterprise;
import com.powergrid.management.model.Plan;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import com.powergrid.management.model.Plan;

import java.io.Serializable;
public interface PlanDAO extends BaseDAO<Plan, Long> {

    Optional<Plan> findByBranchAndYear(Branch branch, Year year);

    Optional<Plan> findByEnterpriseAndYear(Enterprise enterprise, Year year);

    Optional<Plan> findByAssociationAndYear(Association association, Year year);

    List<Plan> findByYear(Year year);

    List<Plan> findByBranch(Branch branch);

    List<Plan> findByEnterprise(Enterprise enterprise);

    List<Plan> findByAssociation(Association association);

    List<Plan> findPlansWithItemsByYear(Year year);

    boolean existsByBranchAndYear(Branch branch, Year year);

    boolean existsByEnterpriseAndYear(Enterprise enterprise, Year year);

    boolean existsByAssociationAndYear(Association association, Year year);

    long countByYear(Year year);

    long countByBranch(Branch branch);

    long countByEnterprise(Enterprise enterprise);

    long countByAssociation(Association association);

    void deleteByBranchAndYear(Branch branch, Year year);

    void deleteByEnterpriseAndYear(Enterprise enterprise, Year year);

    void deleteByAssociationAndYear(Association association, Year year);
}