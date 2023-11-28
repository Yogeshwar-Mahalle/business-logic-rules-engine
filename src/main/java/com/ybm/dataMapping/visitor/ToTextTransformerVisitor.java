/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

public class ToTextTransformerVisitor implements TransformVisitorInterface {
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getString() {
        return null;
    }
}
