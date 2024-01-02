/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BL_RULE_ENTITY")
@IdClass(BLRuleEntityDbModel.IdClass.class)
public class BLRuleEntityDbModel implements Serializable {

    @Id
    @Column(name = "ENTITY_NAME", length = 25, nullable=false)
    private String entityName;

    @Column(name = "DESCRIPTION", length = 256)
    private String description;

    @Column(name = "STATUS", length = 2, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String entityName;
    }

}

