/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfacesRepo.entities;

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
@Table(name = "INTERFACE_PROFILE")
@IdClass(InterfaceProfileDbModel.IdClass.class)
public class InterfaceProfileDbModel implements Serializable {

    @Id
    @Column(name = "LINKED_ENTITY", length = 25, nullable=false)
    private String linkedEntity;

    @Id
    @Column(name = "INTERFACE_ID", length = 128, nullable=false)
    private String interfaceId;

    @Id
    @Column(name = "INTERFACE_NAME", length = 30, nullable=false)
    private String interfaceName;

    @Column(name = "COMMUNICATION_PROTOCOL", length = 5, nullable=false)
    private String communicationProtocol;

    @Column(name = "DIRECTION", length = 1, nullable=false)
    private char direction;

    @Column(name = "STATUS", length = 2, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String linkedEntity;
        private String interfaceId;
        private String interfaceName;
    }
}
