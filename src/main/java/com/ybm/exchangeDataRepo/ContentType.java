/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo;

import lombok.Getter;

@Getter
public enum ContentType {
    JSON("application/json"),
    XML("application/xml"),
    TEXT("application/text");

    public final String label;

    ContentType(String label) {
        this.label = label;
    }

    public static ContentType setLabel(String label) {
        if (label.equals(JSON.label))
            return JSON;
        else if (label.equals(XML.label))
            return XML;
        else if (label.equals(TEXT.label))
            return TEXT;
        else
            return JSON;
    }
}

