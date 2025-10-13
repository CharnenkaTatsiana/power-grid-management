package com.powergrid.management.dao;

import com.powergrid.management.dao.impl.*;

/**
 * Фабрика для создания экземпляров DAO
 */
public class DAOFactory {

    private static volatile DAOFactory instance;

    private final UserDAO userDAO;
    private final PlanDAO planDAO;
    private final ReportDAO reportDAO;
    private final AssociationDAO associationDAO;
    private final EnterpriseDAO enterpriseDAO;
    private final BranchDAO branchDAO;
    private final WorkTypeDAO workTypeDAO;

    private DAOFactory() {
        this.userDAO = new UserDAOImpl();
        this.planDAO = new PlanDAOImpl();
        this.reportDAO = new ReportDAOImpl();
        this.associationDAO = new AssociationDAOImpl();
        this.enterpriseDAO = new EnterpriseDAOImpl();
        this.branchDAO = new BranchDAOImpl();
        this.workTypeDAO = new WorkTypeDAOImpl();
    }

    public static DAOFactory getInstance() {
        if (instance == null) {
            synchronized (DAOFactory.class) {
                if (instance == null) {
                    instance = new DAOFactory();
                }
            }
        }
        return instance;
    }

    // Геттеры для DAO
    public UserDAO getUserDAO() { return userDAO; }
    public PlanDAO getPlanDAO() { return planDAO; }
    public ReportDAO getReportDAO() { return reportDAO; }
    public AssociationDAO getAssociationDAO() { return associationDAO; }
    public EnterpriseDAO getEnterpriseDAO() { return enterpriseDAO; }
    public BranchDAO getBranchDAO() { return branchDAO; }
    public WorkTypeDAO getWorkTypeDAO() { return workTypeDAO; }
}