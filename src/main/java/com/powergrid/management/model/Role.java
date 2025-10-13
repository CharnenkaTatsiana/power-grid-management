package com.powergrid.management.model;

import java.io.Serializable;

public enum Role implements Serializable {
    ADMIN,
    ASSOCIATION_ENGINEER,
    ENTERPRISE_ENGINEER,
    BRANCH_ENGINEER,
    ENTERPRISE_MANAGER,
    ASSOCIATION_MANAGER,
    VIEWER
}
