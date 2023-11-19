/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl;

import com.ybm.dslResolverImpl.dbRepository.IndustryDataRepository;
import com.ybm.dslResolverImpl.entities.IndustryDataDbModel;
import com.ybm.ruleEngine.dslResolver.DSLResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndustryDataDSLResolver implements DSLResolver {
    private static final String DSL_RESOLVER_KEYWORD = "industry";
    private static final String NCC_TYPE = "ncctype";
    private static final String NCC_BIC = "nccbic";

    private static final String NCC_TYPE_VALUE = "ncctypevalue";
    private static final String NCC_BIC_VALUE = "nccbicvalue";

    @Autowired
    private IndustryDataRepository industryDataRepository;

    @Override
    public String getResolverKeyword() {
        return DSL_RESOLVER_KEYWORD;
    }

    @Override
    public Object resolveValue(String keyword) {
        //TODO:: Implementation Pending
        return null;
    }

    @Override
    public Object resolveValueByParameter(String keyword, String parameter) {
        IndustryDataDbModel industryDataDbModel = null;
        Object result = null;

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(NCC_TYPE)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "NCC_TYPE", parameter);
            result = industryDataDbModel;
        }

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(NCC_BIC)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "NCC_BIC", parameter);
            result = industryDataDbModel;
        }

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(NCC_TYPE_VALUE)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "NCC_TYPE", parameter);
            result = industryDataDbModel != null ? industryDataDbModel.getValueField() : "null";
        }

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(NCC_BIC_VALUE)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "NCC_BIC", parameter);
            result = industryDataDbModel != null ? industryDataDbModel.getValueField() : "null";
        }

        return result;
    }
}
