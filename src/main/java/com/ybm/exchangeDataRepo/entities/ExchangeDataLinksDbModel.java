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
@Table(name = "EXCHANGE_DATA_LINKS")
@IdClass(ExchangeDataLinksDbModel.IdClass.class)
public class ExchangeDataLinksDbModel implements Serializable {

    @Id
    @Column(name = "EXCHANGE_LINK_ID", length = 128, nullable=false)
    private String exchangeLinkId;

    @Id
    @Column(name = "UNIQUE_EXCHANGE_ID", length = 128, nullable=false)
    private String uniqueExchangeId;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;
    
    @Data
    static class IdClass implements Serializable {
        private String exchangeLinkId;
        private String uniqueExchangeId;
    }

}

