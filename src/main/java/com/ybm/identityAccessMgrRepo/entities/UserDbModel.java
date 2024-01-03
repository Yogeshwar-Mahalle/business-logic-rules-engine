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
@Table(name = "USERS")
@IdClass(UserDbModel.IdClass.class)
public class UserDbModel {

    @Id
    @Column(name = "ID", length = 128, nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "USER_ID", unique = true, length = 25, nullable=false)
    private String userId;

    @Column(name = "FIRST_NAME", length = 25, nullable=false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 25, nullable=false)
    private String lastName;

    @Column(name = "EMAIL", length = 30)
    private String email;

    @Column(name = "PASSWORD", length = 100)
    private String password;

    @Column(name = "ENABLED", length = 1, nullable=false)
    private Boolean enabled;

    @Column(name = "TOKEN_EXPIRED", length = 1)
    private Boolean tokenExpired;

    @Column(name = "STATUS", length = 15, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @ManyToMany
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(
                    name = "USER_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(
                    name = "ROLE_ID", referencedColumnName = "ID"))
    private Collection<RoleDbModel> roles;


    @Data
    static class IdClass implements Serializable {
        private Long id;
    }
}
