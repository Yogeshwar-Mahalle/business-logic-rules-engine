/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine.dslResolver;

public interface DSLResolver {
    String getResolverKeyword();
    Object resolveValue();
    Object resolveValue(String keyword);
    Object resolveValue(String keyword, Integer index);

    Object resolveValue(String keyword, String[] parameters);
    Object resolveValue(String keyword, String[] parameters, Integer index);
}
