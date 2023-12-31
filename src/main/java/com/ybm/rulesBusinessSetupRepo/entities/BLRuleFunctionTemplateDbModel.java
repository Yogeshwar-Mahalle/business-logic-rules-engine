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
@Table(name = "BL_RULE_FUNCTION_TEMPLATE")
@IdClass(BLRuleFunctionTemplateDbModel.IdClass.class)
public class BLRuleFunctionTemplateDbModel implements Serializable {

    @Id
    @Column(name = "LINKED_ENTITY", length = 25, nullable=false)
    private String linkedEntity;

    @Id
    @Column(name = "FUNCTION_ID", length = 128, nullable=false)
    private String functionId;

    @Id
    @Column(name = "FUNCTION_NAME", length = 64, nullable=false)
    private String functionName;

    @Column(name = "DESCRIPTION", length = 256)
    private String description;

    @Column(name = "FUNCTION_PARAMETERS", length = 256)
    private String functionParameters;

    @Column(name = "INCLUDE_FUNCTIONS_NAME_LIST", length = 5120)
    private String includeFunctionsNameList;

    @Column(name = "FUNCTION_LOGIC", length = 2097152)
    private String functionLogic;

    @Column(name = "STATUS", length = 2, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String linkedEntity;
        private String functionId;
        private String functionName;
    }

}

