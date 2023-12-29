/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl;

import com.ybm.exchangeDataRepo.ExchangeDataService;
import com.ybm.ruleEngine.dslResolver.DSLResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ExchangeDataDSLResolver implements DSLResolver {
    private static final String DSL_RESOLVER_KEYWORD = "exchangedata";
    private static final String DUPLICATE_CHECK = "duplicatecheck";
    private static final String GET_EXCHANGE_DATA = "getdata";


    @Autowired
    private ExchangeDataService exchangeDataService;

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
        //TODO:: Implementation Pending
        return null;
    }

    @Override
    public Object resolveValue(String keyword, Integer index) {
        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        return null;
    }

    @Override
    public Object resolveValue(String keyword, String[] parameters) {

        Object result = null;

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(DUPLICATE_CHECK)){
            int count =
                    exchangeDataService.getExchangeDataCountByMessageIdAndNotExchId(
                            parameters[0],
                            parameters[1],
                            parameters[2],
                            parameters[3]
                    );

            result = count > 0;
        }

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(GET_EXCHANGE_DATA)){
            result = exchangeDataService.getExchangeDataByMessageId( parameters[0], parameters[1], parameters[2] );
        }


        return result;
    }

    @Override
    public Object resolveValue(String keyword, String[] parameters, Integer index) {
        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        return null;
    }
}
