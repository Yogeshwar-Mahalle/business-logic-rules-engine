/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.identityAccessMgrRepo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Privilege implements Serializable {
    private Long id;
    private String privilegeName;
    private Collection<Role> roles;
    @Builder.Default
    private StatusType status = StatusType.AC;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
