package com.powergrid.management.service;

import com.powergrid.management.dao.WorkTypeDAO;
import com.powergrid.management.dao.impl.WorkTypeDAOImpl;
import com.powergrid.management.model.NetworkType;
import com.powergrid.management.model.WorkType;
import java.util.List;
import java.util.Optional;

public class WorkTypeService {
    private final WorkTypeDAO workTypeDAO = new WorkTypeDAOImpl();

    public List<WorkType> getAllWorkTypes() {
        return workTypeDAO.findAll();
    }

    public WorkType getWorkTypeById(Long id) {
        return workTypeDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work type not found with id: " + id));
    }

    public WorkType createWorkType(String name, NetworkType networkType) {
        validateWorkTypeData(name, networkType);

        // Проверяем уникальность имени
        if (workTypeDAO.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Work type with name '" + name + "' already exists");
        }

        WorkType workType = new WorkType();
        workType.setName(name);
        workType.setNetworkType(networkType);

        return workTypeDAO.save(workType);
    }

    public WorkType updateWorkType(Long id, String name, NetworkType networkType) {
        validateWorkTypeData(name, networkType);

        WorkType existingWorkType = getWorkTypeById(id);

        // Проверяем уникальность имени (исключая текущий тип работ)
        workTypeDAO.findByName(name).ifPresent(wt -> {
            if (!wt.getId().equals(id)) {
                throw new IllegalArgumentException("Work type with name '" + name + "' already exists");
            }
        });

        existingWorkType.setName(name);
        existingWorkType.setNetworkType(networkType);

        return workTypeDAO.update(existingWorkType);
    }

    public void deleteWorkType(Long id) {
        WorkType workType = getWorkTypeById(id);

        // TODO: Проверить, используется ли тип работ в планах или отчетах
        // if (isWorkTypeInUse(id)) {
        //     throw new IllegalStateException("Cannot delete work type that is used in plans or reports");
        // }

        workTypeDAO.delete(workType);
    }

    public List<WorkType> getWorkTypesByNetworkType(NetworkType networkType) {
        return workTypeDAO.findByNetworkType(networkType);
    }

    public Optional<WorkType> findWorkTypeByName(String name) {
        return workTypeDAO.findByName(name);
    }

    public void initializeDefaultWorkTypes() {
        // Основная сеть
        createWorkTypeIfNotExists("Ремонт ВЛ 35 кВ", NetworkType.MAIN_NETWORK);
        createWorkTypeIfNotExists("Ремонт ВЛ 110 кВ", NetworkType.MAIN_NETWORK);
        createWorkTypeIfNotExists("Ремонт ВЛ 220 кВ", NetworkType.MAIN_NETWORK);
        createWorkTypeIfNotExists("Ремонт ВЛ 330 кВ", NetworkType.MAIN_NETWORK);
        createWorkTypeIfNotExists("Ремонт ВЛ 750 кВ", NetworkType.MAIN_NETWORK);
        createWorkTypeIfNotExists("Замена опор на ВЛ 35 кВ и выше", NetworkType.MAIN_NETWORK);
        createWorkTypeIfNotExists("Замена провода на ВЛ 35 кВ и выше", NetworkType.MAIN_NETWORK);
        createWorkTypeIfNotExists("Замена г/троса на ВЛ 35 кВ и выше", NetworkType.MAIN_NETWORK);
        createWorkTypeIfNotExists("Расчистка просек ВЛ 35 кВ и выше", NetworkType.MAIN_NETWORK);

        // Распределительная сеть
        createWorkTypeIfNotExists("Ремонт ВЛ 0,4 кВ", NetworkType.DISTRIBUTION_NETWORK);
        createWorkTypeIfNotExists("Ремонт ВЛ 10(6) кВ", NetworkType.DISTRIBUTION_NETWORK);
        createWorkTypeIfNotExists("Замена опор на ВЛ 10(6) кВ", NetworkType.DISTRIBUTION_NETWORK);
        createWorkTypeIfNotExists("Замена опор на ВЛ 0,4 кВ", NetworkType.DISTRIBUTION_NETWORK);
        createWorkTypeIfNotExists("Замена провода на ВЛ 10(6) кВ", NetworkType.DISTRIBUTION_NETWORK);
        createWorkTypeIfNotExists("Замена провода на ВЛ 0,4 кВ", NetworkType.DISTRIBUTION_NETWORK);
        createWorkTypeIfNotExists("Ремонт РП,ТП, КТП", NetworkType.DISTRIBUTION_NETWORK);
        createWorkTypeIfNotExists("Замена изношенных КЛ 10(6) кВ", NetworkType.DISTRIBUTION_NETWORK);
        createWorkTypeIfNotExists("Расчистка просек ВЛ 10(6) кВ", NetworkType.DISTRIBUTION_NETWORK);
    }

    private void createWorkTypeIfNotExists(String name, NetworkType networkType) {
        if (findWorkTypeByName(name).isEmpty()) {
            createWorkType(name, networkType);
        }
    }

    private void validateWorkTypeData(String name, NetworkType networkType) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Work type name cannot be empty");
        }
        if (networkType == null) {
            throw new IllegalArgumentException("Network type cannot be null");
        }
    }

    public long getWorkTypeCount() {
        return 0;
    }
}