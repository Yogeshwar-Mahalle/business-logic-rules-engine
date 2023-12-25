/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.interfaces;

import java.util.LinkedHashMap;

public interface PayloadMessageInterface {
    public String accept(VisitorInterface visitorInterface);
    public void processor(ProcessingInterface processingInterface);

    public boolean validate();

    public String getRootNode();
    public LinkedHashMap<String, Object> getDataMap();
}
