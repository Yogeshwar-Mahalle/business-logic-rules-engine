/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.guiRepo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GUIFieldsLabel {
    private String fieldUniqueName;
    private String fieldParentUniqueName;
    private String fieldHTMLId;
    private String fieldLabel;
    private String fieldToolTip;
    private String fieldHTMLType;
    private String fieldDataType;
    private String fieldDataFormat;
    private String fieldDefaultValue;
    private Integer fieldMinLength;
    private Integer fieldMaxLength;
    private Boolean hiddenDataFieldFlag;
    private Date createTimeStamp;
}
