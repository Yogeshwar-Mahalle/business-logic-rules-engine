/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine.dslResolver;

public interface DSLResolver {
    String getResolverKeyword();
    Object resolveValue(String keyword);
}
