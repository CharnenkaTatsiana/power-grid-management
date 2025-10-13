package com.powergrid.management.dao;

import com.powergrid.management.model.Association;
import com.powergrid.management.model.Branch;
import com.powergrid.management.model.Enterprise;
import com.powergrid.management.model.Report;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import com.powergrid.management.model.Report;

import java.io.Serializable;

public interface ReportDAO extends BaseDAO<Report, Long> {

    Optional<Report> findByBranchAndPeriod(Branch branch, YearMonth period);

    List<Report> findByBranchAndYear(Branch branch, int year);

    List<Report> findByEnterpriseAndPeriod(Enterprise enterprise, YearMonth period);

    List<Report> findByAssociationAndPeriod(Association association, YearMonth period);

    List<Report> findByPeriod(YearMonth period);

    List<Report> findByYear(int year);

    List<Report> findByBranch(Branch branch);

    List<Report> findByEnterprise(Enterprise enterprise);

    List<Report> findByAssociation(Association association);

    List<Report> findReportsWithItemsByPeriod(YearMonth period);

    boolean existsByBranchAndPeriod(Branch branch, YearMonth period);

    long countByPeriod(YearMonth period);

    long countByBranch(Branch branch);

    long countByEnterprise(Enterprise enterprise);

    long countByAssociation(Association association);

    void deleteByBranchAndPeriod(Branch branch, YearMonth period);

    List<YearMonth> findAvailablePeriodsByBranch(Branch branch);

    List<Integer> findAvailableYears();
}