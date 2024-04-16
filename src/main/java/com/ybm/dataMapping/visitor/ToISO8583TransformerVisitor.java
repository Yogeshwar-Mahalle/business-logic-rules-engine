/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToISO8583TransformerVisitor implements VisitorInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ToISO8583TransformerVisitor.class);
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getResult() {

        StringBuilder returnResult = new StringBuilder((String) m_PayloadMessageInterface.getDataMap().get(m_PayloadMessageInterface.getRootNode()));

        if( returnResult.isEmpty() )
        {
            returnResult = new StringBuilder();
            for( Object textLine : m_PayloadMessageInterface.getDataMap().values() )
            {
                returnResult.append(textLine.toString());
                returnResult.append("\n");
            }
        }

        return returnResult.toString();
    }
}
