/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.interfaces;

public interface PayloadMessageInterface {
    public String accept(TransformVisitorInterface transformVisitorInterface);
    public void processor(ProcessingInterface processingInterface);
}
