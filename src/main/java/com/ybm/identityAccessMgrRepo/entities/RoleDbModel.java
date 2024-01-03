/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.identityAccessMgrRepo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ROLES")
@IdClass(RoleDbModel.IdClass.class)
public class RoleDbModel {

    @Id
    @Column(name = "ID", length = 128, nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ROLE_NAME", unique = true, length = 25, nullable=false)
    private String roleName;

    @Column(name = "STATUS", length = 15, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @ManyToMany(mappedBy = "roles")
    private Collection<UserDbModel> users;

    @ManyToMany
    @JoinTable(
            name = "ROLE_PRIVILEGES",
            joinColumns = @JoinColumn(
                    name = "ROLE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(
                    name = "PRIVILEGE_ID", referencedColumnName = "ID"))
    private Collection<PrivilegeDbModel> privileges;

    @Data
    static class IdClass implements Serializable {
        private Long id;
    }
}
