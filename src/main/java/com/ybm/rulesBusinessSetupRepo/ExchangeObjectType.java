/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

public enum ExchangeObjectType {
    INPUT_MESSAGE("inMessage"),
    INPUT_PAYLOAD("inPayload"),
    INPUT_HEADERS("inHeaders"),
    INPUT_PROPERTIES("inProperties"),
    PROCESSING_PAYLOAD("payload"),
    OUTPUT_PAYLOAD("outPayload"),
    OUTPUT_HEADERS("outHeaders"),
    OUTPUT_PROPERTIES("outProperties"),
    INPUT_EXTENSION_DATA("inExtData"),
    OUTPUT_EXTENSION_DATA("outExtData"),
    CUSTOM_FIELDS("customFields");

    private final String label;
    ExchangeObjectType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
