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
@Table(name = "BL_RULE_FUNCTIONS")
@IdClass(BLRuleFunctionPnAprvlDbModel.IdClass.class)
public class BLRuleFunctionPnAprvlDbModel implements Serializable {

    @Id
    @Column(name = "FUNCTION_ID", length = 128, nullable=false)
    private String functionId;

    @Column(name = "FUNCTION_NAME", length = 50, nullable=false)
    private String functionName;

    @Column(name = "FUNCTION_PARAMETERS", length = 256)
    private String functionParameters;

    @Column(name = "FUNCTION_LOGIC", length = 2097152)
    private String functionLogic;

    @Column(name = "DESCRIPTION", length = 256)
    private String description;

    @Column(name = "STATUS", length = 25, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String functionId;
    }

}

