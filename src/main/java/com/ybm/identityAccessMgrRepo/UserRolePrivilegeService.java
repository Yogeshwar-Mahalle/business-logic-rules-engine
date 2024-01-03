/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.identityAccessMgrRepo;

import com.ybm.identityAccessMgrRepo.deRepository.PrivilegeRepository;
import com.ybm.identityAccessMgrRepo.deRepository.RoleRepository;
import com.ybm.identityAccessMgrRepo.deRepository.UserRepository;
import com.ybm.identityAccessMgrRepo.entities.PrivilegeDbModel;
import com.ybm.identityAccessMgrRepo.entities.RoleDbModel;
import com.ybm.identityAccessMgrRepo.entities.UserDbModel;
import com.ybm.identityAccessMgrRepo.models.Privilege;
import com.ybm.identityAccessMgrRepo.models.Role;
import com.ybm.identityAccessMgrRepo.models.StatusType;
import com.ybm.identityAccessMgrRepo.models.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserRolePrivilegeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    public List<User> getAllUsers() {

        return userRepository.findAll().stream()
                .map(
                        this::mapUserFromDbModel
                )
                .collect(Collectors.toList());
    }

    public User getUserByUserId(String userId) {
        Optional<UserDbModel> userDbModel = userRepository.findByUserId(userId);

        return userDbModel.map(this::mapUserFromDbModel).orElse(null);
    }

    @Transactional
    public User saveUser(User user) {
        UserDbModel userDbModel = mapUserToDbModel(user);
        userDbModel = userRepository.save(userDbModel);

        return mapUserFromDbModel(userDbModel);
    }

    @Transactional
    public List<User> removeUserByUserId(String userId) {
        if( userId == null )
            return null;

        userRepository.deleteByUserId(userId);

        return userRepository.findAll().stream()
                .map(
                        this::mapUserFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<Role> getAllRoles() {

        return roleRepository.findAll().stream()
                .map(
                        this::mapRoleFromDbModel
                )
                .collect(Collectors.toList());
    }

    public Role getRoleByRoleName(String roleName) {
        Optional<RoleDbModel> roleDbModel = roleRepository.findByRoleName(roleName);

        return roleDbModel.map(this::mapRoleFromDbModel).orElse(null);
    }

    @Transactional
    public Role saveRole(Role role) {
        RoleDbModel roleDbModel = mapRoleToDbModel(role);
        roleDbModel = roleRepository.save(roleDbModel);

        return mapRoleFromDbModel(roleDbModel);
    }

    @Transactional
    public List<Role> removeRoleByRoleName(String roleName) {
        if( roleName == null )
            return null;

        roleRepository.deleteByRoleName(roleName);

        return roleRepository.findAll().stream()
                .map(
                        this::mapRoleFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<Privilege> getAllPrivilege() {

        return privilegeRepository.findAll().stream()
                .map(
                        this::mapPrivilegeFromDbModel
                )
                .collect(Collectors.toList());
    }

    public Privilege getPrivilegeByPrivilegeName(String privilegeName) {
        Optional<PrivilegeDbModel> privilegeDbModel = privilegeRepository.findByPrivilegeName(privilegeName);

        return privilegeDbModel.map(this::mapPrivilegeFromDbModel).orElse(null);
    }

    @Transactional
    public Privilege savePrivilege(Privilege privilege) {
        PrivilegeDbModel privilegeDbModel = mapPrivilegeToDbModel(privilege);
        privilegeDbModel = privilegeRepository.save(privilegeDbModel);

        return mapPrivilegeFromDbModel(privilegeDbModel);
    }

    @Transactional
    public List<Privilege> removePrivilegeByPrivilegeName(String privilegeName) {
        if( privilegeName == null )
            return null;

        privilegeRepository.deleteByPrivilegeName(privilegeName);

        return privilegeRepository.findAll().stream()
                .map(
                        this::mapPrivilegeFromDbModel
                )
                .collect(Collectors.toList());
    }

    private User mapUserFromDbModel(UserDbModel userDbModel){

        /*List<Role> roles = null;
        if( userDbModel.getRoles() != null ) {
            roles = userDbModel.getRoles().stream()
                    .map(
                            this::mapRoleFromDbModel
                    )
                    .collect(Collectors.toList());
        }*/

        return User.builder()
                .id(userDbModel.getId())
                .userId(userDbModel.getUserId())
                .firstName(userDbModel.getFirstName())
                .lastName(userDbModel.getLastName())
                .email(userDbModel.getEmail())
                .password(userDbModel.getPassword())
                .enabled(userDbModel.getEnabled())
                .tokenExpired(userDbModel.getTokenExpired())
                //.roles(roles)
                .status(StatusType.valueOf(userDbModel.getStatus()))
                .createTimeStamp(userDbModel.getCreateTimeStamp())
                .updateTimeStamp(userDbModel.getUpdateTimeStamp())
                .build();

    }

    private UserDbModel mapUserToDbModel(User user){

        List<RoleDbModel> roleDbModelList = null;
        if( user.getRoles() != null ) {
            roleDbModelList = user.getRoles().stream()
                    .map(
                            this::mapRoleToDbModel
                    )
                    .collect(Collectors.toList());
        }

        return UserDbModel.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .enabled(user.getEnabled())
                .tokenExpired(user.getTokenExpired())
                .roles(roleDbModelList)
                .status(String.valueOf(user.getStatus()))
                .createTimeStamp(user.getCreateTimeStamp() == null ? new Date() : user.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

    private Role mapRoleFromDbModel(RoleDbModel roleDbModel){

        List<User> users = null;
        if( roleDbModel.getUsers() != null ) {
            users = roleDbModel.getUsers().stream()
                    .map(
                            this::mapUserFromDbModel
                    )
                    .collect(Collectors.toList());
        }

        List<Privilege> privileges = null;
        if( roleDbModel.getPrivileges() != null ) {
            privileges = roleDbModel.getPrivileges().stream()
                    .map(
                            this::mapPrivilegeFromDbModel
                    )
                    .collect(Collectors.toList());
        }

        return Role.builder()
                .id(roleDbModel.getId())
                .roleName(roleDbModel.getRoleName())
                .users(users)
                .privileges(privileges)
                .status(StatusType.valueOf(roleDbModel.getStatus()))
                .createTimeStamp(roleDbModel.getCreateTimeStamp())
                .updateTimeStamp(roleDbModel.getUpdateTimeStamp())
                .build();

    }

    private RoleDbModel mapRoleToDbModel(Role role){

        List<UserDbModel> userDbModeList = null;
        if( role.getUsers() != null ) {
            userDbModeList = role.getUsers().stream()
                    .map(
                            this::mapUserToDbModel
                    )
                    .collect(Collectors.toList());
        }

        List<PrivilegeDbModel> privilegeDbModelList = null;
        if( role.getPrivileges() != null ) {
            privilegeDbModelList = role.getPrivileges().stream()
                    .map(
                            this::mapPrivilegeToDbModel
                    )
                    .collect(Collectors.toList());
        }

        return RoleDbModel.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .users(userDbModeList)
                .privileges(privilegeDbModelList)
                .status(String.valueOf(role.getStatus()))
                .createTimeStamp(role.getCreateTimeStamp() == null ? new Date() : role.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

    private Privilege mapPrivilegeFromDbModel(PrivilegeDbModel privilegeDbModel){

        /*List<Role> roles = null;
        if( privilegeDbModel.getRoles() != null ) {
            roles = privilegeDbModel.getRoles().stream()
                    .map(
                            this::mapRoleFromDbModel
                    )
                    .collect(Collectors.toList());
        }*/

        return Privilege.builder()
                .id(privilegeDbModel.getId())
                .privilegeName(privilegeDbModel.getPrivilegeName())
                //.roles(roles)
                .status(StatusType.valueOf(privilegeDbModel.getStatus()))
                .createTimeStamp(privilegeDbModel.getCreateTimeStamp())
                .updateTimeStamp(privilegeDbModel.getUpdateTimeStamp())
                .build();

    }

    private PrivilegeDbModel mapPrivilegeToDbModel(Privilege privilege){

        List<RoleDbModel> roleDbModelList = null;
        if( privilege.getRoles() != null ) {
            roleDbModelList = privilege.getRoles().stream()
                    .map(
                            this::mapRoleToDbModel
                    )
                    .collect(Collectors.toList());
        }

        return PrivilegeDbModel.builder()
                .id(privilege.getId())
                .privilegeName(privilege.getPrivilegeName())
                .roles(roleDbModelList)
                .status(String.valueOf(privilege.getStatus()))
                .createTimeStamp(privilege.getCreateTimeStamp() == null ? new Date() : privilege.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }
}
