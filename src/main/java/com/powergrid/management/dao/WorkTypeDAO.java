package com.powergrid.management.dao;

import com.powergrid.management.model.NetworkType;
import com.powergrid.management.model.WorkType;

import java.util.List;
import java.util.Optional;
import com.powergrid.management.model.WorkType;

import java.io.Serializable;

public interface WorkTypeDAO extends BaseDAO<WorkType, Long> {

    Optional<WorkType> findByName(String name);

    List<WorkType> findByNetworkType(NetworkType networkType);

    List<WorkType> findByNetworkType(NetworkType networkType, int offset, int limit);

    List<WorkType> findByNameContaining(String nameFragment);

    List<WorkType> findByNameContaining(String nameFragment, NetworkType networkType);

    List<WorkType> findAllOrderedByName();

    List<WorkType> findAllOrderedByNetworkTypeAndName();

    boolean existsByName(String name);

    long countByNetworkType(NetworkType networkType);

    List<Object[]> getWorkTypeStatistics();

    boolean isWorkTypeUsedInPlans(Long workTypeId);

    boolean isWorkTypeUsedInReports(Long workTypeId);

    List<WorkType> findUnusedWorkTypes();

    int bulkUpdateNetworkType(NetworkType oldNetworkType, NetworkType newNetworkType);

    int deleteByNetworkType(NetworkType networkType);

    void initializeDefaultWorkTypes();
}
