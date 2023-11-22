/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

public class JsonMessage implements PayloadMessageInterface {
    public JsonMessage(String message) {
    }

    @Override
    public String accept(TransformVisitorInterface transformVisitorInterface) {

        return "JsonMessage";
    }

    @Override
    public void processor(ProcessingInterface processingInterface) {

    }
}
