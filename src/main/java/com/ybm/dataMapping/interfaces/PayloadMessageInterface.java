/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.interfaces;

import java.util.HashMap;
import java.util.Map;

public interface PayloadMessageInterface {
    Map<String, Object> dataMap = new HashMap<>();
    public String accept(TransformVisitorInterface transformVisitorInterface);
    public void processor(ProcessingInterface processingInterface);
}