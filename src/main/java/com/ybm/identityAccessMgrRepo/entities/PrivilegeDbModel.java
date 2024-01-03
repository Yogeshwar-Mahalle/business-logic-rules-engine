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
@Table(name = "PRIVILEGE")
@IdClass(PrivilegeDbModel.IdClass.class)
public class PrivilegeDbModel {

    @Id
    @Column(name = "ID", length = 128, nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "PRIVILEGE_NAME", length = 25, unique = true, nullable=false)
    private String privilegeName;

    @Column(name = "STATUS", length = 15, nullable=false)
    private String status ;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @ManyToMany(mappedBy = "privileges")
    private Collection<RoleDbModel> roles;

    @Data
    static class IdClass implements Serializable {
        private Long id;
    }
}
