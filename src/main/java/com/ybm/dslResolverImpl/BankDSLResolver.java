/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl;

import com.ybm.ruleEngine.dslResolver.DSLResolver;
import org.springframework.stereotype.Component;

@Component
public class BankDSLResolver implements DSLResolver {
    private static final String DSL_RESOLVER_KEYWORD = "bank";
    private static final String INTEREST_RATE = "interestRate";
    private static final String ELIGIBLE_AMOUNT = "eligibleAmount";

    @Override
    public String getResolverKeyword() {
        return DSL_RESOLVER_KEYWORD;
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
}
