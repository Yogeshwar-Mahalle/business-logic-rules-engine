/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToTextTransformerVisitor implements VisitorInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ToTextTransformerVisitor.class);
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getResult() {
        return null;
    }
}
