/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.identityAccessMgrRepo.models;

import com.ybm.identityAccessMgrRepo.UserRolePrivilegeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class IAMDummyUserService implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRolePrivilegeService userRolePrivilegeService;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup)
            return;
        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        Role userRole = createRoleIfNotFound("ROLE_USER", Collections.singletonList(readPrivilege));

        //TODO: Remove later this dummy admin & test user
        User adminUser = new User();
        adminUser.setUserId("admin");
        adminUser.setFirstName("admin");
        adminUser.setLastName("admin");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setEmail("admin@admin.com");
        adminUser.setRoles(Collections.singletonList(adminRole));
        adminUser.setEnabled(true);
        adminUser.setStatus(StatusType.AC);

        adminUser = createUserIfNotFound(adminUser);

        User testUser = new User();
        testUser.setUserId("test");
        testUser.setFirstName("test");
        testUser.setLastName("test");
        testUser.setPassword(passwordEncoder.encode("test"));
        testUser.setEmail("test@test.com");
        testUser.setRoles(Collections.singletonList(userRole));
        testUser.setEnabled(true);
        adminUser.setStatus(StatusType.AC);

        testUser = createUserIfNotFound(testUser);

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String privilegeName) {

        Privilege privilege = userRolePrivilegeService.getPrivilegeByPrivilegeName(privilegeName);
        if (privilege == null) {
            privilege = new Privilege();
            privilege.setPrivilegeName(privilegeName);
            privilege.setStatus(StatusType.AC);
            privilege = userRolePrivilegeService.savePrivilege(privilege);
        }

        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(String roleName, Collection<Privilege> privileges) {

        Role role = userRolePrivilegeService.getRoleByRoleName(roleName);
        if (role == null) {
            role = new Role();
            role.setRoleName(roleName);
            role.setPrivileges(privileges);
            role.setStatus(StatusType.AC);
            role = userRolePrivilegeService.saveRole(role);
        }

        return role;
    }

    @Transactional
    User createUserIfNotFound(User user) {

        User userDB = userRolePrivilegeService.getUserByUserId(user.getUserId());
        if (userDB == null) {
            userDB = userRolePrivilegeService.saveUser(user);
        }

        return userDB;
    }


}
