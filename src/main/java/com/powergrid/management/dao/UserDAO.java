package com.powergrid.management.dao;

import com.powergrid.management.model.Branch;
import com.powergrid.management.model.Enterprise;
import com.powergrid.management.model.Role;
import com.powergrid.management.model.User;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface UserDAO extends BaseDAO<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findByRoles(List<Role> roles);

    List<User> findByBranch(Branch branch);

    List<User> findByEnterprise(Enterprise enterprise);

    List<User> findByAssociation(Long associationId);

    List<User> findActiveUsers();

    List<User> findInactiveUsers();

    boolean existsByUsername(String username);

    long countByRole(Role role);

    long countByBranch(Branch branch);

    long countByEnterprise(Enterprise enterprise);

    void updatePassword(Long userId, String encryptedPassword);

    void updateLastLogin(Long userId);

    void deactivateUser(Long userId);

    void activateUser(Long userId);

}
