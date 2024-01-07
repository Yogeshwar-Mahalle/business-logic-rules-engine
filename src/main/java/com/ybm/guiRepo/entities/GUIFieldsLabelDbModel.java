/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.guiRepo.entities;

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
@Table(name = "GUI_FIELDS_LABEL")
@IdClass(GUIFieldsLabelDbModel.IdClass.class)
public class GUIFieldsLabelDbModel {
    @Id
    @Column(name = "FIELD_UNIQUE_NAME", length = 128, nullable=false)
    private String fieldUniqueName;

    @Column(name = "FIELD_PARENT_UNIQUE_NAME", length = 128, nullable=false)
    private String fieldParentUniqueName;

    @Column(name = "FIELD_HTML_ID", length = 50, nullable=false)
    private String fieldHTMLId;

    @Column(name = "FIELD_LABEL", length = 70, nullable=false)
    private String fieldLabel;

    @Column(name = "FIELD_TOOL_TIP", length = 100)
    private String fieldToolTip;

    @Column(name = "FIELD_HTML_TYPE", length = 20, nullable=false)
    private String fieldHTMLType;

    @Column(name = "FIELD_DATA_TYPE", length = 20)
    private String fieldDataType;

    @Column(name = "FIELD_DATA_FORMAT", length = 50)
    private String fieldDataFormat;

    @Column(name = "FIELD_DEFAULT_VALUE", length = 100)
    private String fieldDefaultValue;

    @Column(name = "FIELD_MIN_LENGTH")
    private Integer fieldMinLength;

    @Column(name = "FIELD_MAX_LENGTH")
    private Integer fieldMaxLength;

    @Column(name = "HIDDEN_DATA_FIELD_FLAG")
    private Boolean hiddenDataFieldFlag;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String fieldUniqueName;
    }
}
