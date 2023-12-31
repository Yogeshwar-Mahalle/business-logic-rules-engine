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
@Table(name = "INTERFACE_PROPERTIES")
@IdClass(InterfacePropertyDbModel.IdClass.class)
public class InterfacePropertyDbModel implements Serializable {
    @Id
    @Column(name = "INTERFACE_ID", length = 128, nullable=false)
    String interfaceId;

    @Id
    @Column(name = "PROPERTY_NAME", length = 30, nullable=false)
    String propertyName;

    @Column(name = "PROPERTY_VALUE", length = 512, nullable=false)
    String propertyValue;

    @Column(name = "STATUS", length = 2, nullable=false)
    String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String interfaceId;
        private String propertyName;
    }
}
