/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

public class YamlMessage implements PayloadMessageInterface {
    public YamlMessage(String message) {
    }

    @Override
    public String accept(TransformVisitorInterface transformVisitorInterface) {

        return "YamlMessage";
    }

    @Override
    public void processor(ProcessingInterface processingInterface) {

    }
}
