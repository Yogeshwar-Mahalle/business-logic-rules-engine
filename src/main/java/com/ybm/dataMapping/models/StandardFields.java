/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.models;

import lombok.Getter;

@Getter
public enum StandardFields {
    INPUT_DATA("in"),
    OUTPUT_DATA("out"),
    INTERFACE("INTERFACE"),
    INTERFACE_HTTP_METHOD("interfaceHttpMethod"),
    INTERFACE_CONTEXT_PATH( "interfaceContextPath" ),
    INTERFACE_QUERY( "interfaceQuery" ),
    INTERFACE_REQ_BODY("interfaceReq"),
    INTERFACE_RESPONSE("interfaceResponseName");

    public final String label;

    StandardFields(String label) {
        this.label = label;
    }

    public static StandardFields setLabel(String label) {
        if (label.equals(INPUT_DATA.label))
            return INPUT_DATA;
        else if (label.equals(OUTPUT_DATA.label))
            return OUTPUT_DATA;
        else if (label.equals(INTERFACE.label))
            return INTERFACE;
        else if (label.equals(INTERFACE_HTTP_METHOD.label))
            return INTERFACE_HTTP_METHOD;
        else if (label.equals(INTERFACE_CONTEXT_PATH.label))
            return INTERFACE_CONTEXT_PATH;
        else if (label.equals(INTERFACE_QUERY.label))
            return INTERFACE_QUERY;
        else if (label.equals(INTERFACE_REQ_BODY.label))
            return INTERFACE_REQ_BODY;
        else if (label.equals(INTERFACE_RESPONSE.label))
            return INTERFACE_RESPONSE;
        else
            return null;
    }
}

