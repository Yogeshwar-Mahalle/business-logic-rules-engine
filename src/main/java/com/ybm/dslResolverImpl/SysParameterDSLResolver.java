/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl;

import com.ybm.ruleEngine.dslResolver.DSLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SysParameterDSLResolver implements DSLResolver {
    private static final Logger LOG = LoggerFactory.getLogger(SysParameterDSLResolver.class);
    private static final String DSL_RESOLVER_KEYWORD = "SYSPARAMETER";
    private static final String INTEREST_RATE = "INTERESTRATE";
    private static final String ELIGIBLE_AMOUNT = "ELIGIBLEAMOUNT";

    @Override
    public String getResolverKeyword() {
        return DSL_RESOLVER_KEYWORD;
    }

    @Override
    public Object resolveValue() {
        //TODO:: Implementation Pending
        return null;
    }

    @Override
    public Object resolveValue(String keyword) {

        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(INTEREST_RATE)){
            //Code to calculate the current variable interest rates.
            return 9.0;
        }

        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(ELIGIBLE_AMOUNT)){
            //Code to see the bank eligibility amount of giving loan for this current year according to risk calculation.
            return 50000.0;
        }

        return null;
    }

    @Override
    public Object resolveValue(String keyword, Integer index) {
        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        return null;
    }

    @Override
    public Object resolveValue(String keyword, String[] parameters) {
        //TODO:: Implementation Pending
        return null;
    }

    @Override
    public Object resolveValue(String keyword, String[] parameters, Integer index) {
        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        return null;
    }
}
