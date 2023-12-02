/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.interfaces;

public interface VisitorInterface {
    public void visit(PayloadMessageInterface payloadMessageInterface);

    public String getResult();
}
