/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfacesRepo.models;

public enum DirectionType {
    INCOMING("I"),
    OUTGOING("O");

    public final String label;

    DirectionType(String label) {
        this.label = label;
    }

    public static DirectionType setLabel(String label) {
        if (label.equals(INCOMING.label))
            return INCOMING;
        else if (label.equals(OUTGOING.label))
            return OUTGOING;
        else
            return OUTGOING;
    }
}
