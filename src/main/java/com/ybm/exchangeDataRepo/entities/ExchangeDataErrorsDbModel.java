/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo.entities;


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
@Table(name = "EXCHANGE_DATA_ERRORS")
@IdClass(ExchangeDataErrorsDbModel.IdClass.class)
public class ExchangeDataErrorsDbModel implements Serializable {

    @Id
    @Column(name = "UNIQUE_EXCHANGE_ID", length = 128, nullable=false)
    private String uniqueExchangeId;

    @Id
    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "ERROR_MESSAGE", length = 2097152, nullable=false)
    private String errorMessage;

    @Data
    static class IdClass implements Serializable {
        private String uniqueExchangeId;
        private Date createTimeStamp;
    }

}

