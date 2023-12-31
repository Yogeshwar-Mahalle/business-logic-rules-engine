/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl;

import com.ybm.dslResolverImpl.dbRepository.IndustryDataRepository;
import com.ybm.dslResolverImpl.entities.IndustryDataDbModel;
import com.ybm.ruleEngine.dslResolver.DSLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndustryDataDSLResolver implements DSLResolver {
    private static final Logger LOG = LoggerFactory.getLogger(IndustryDataDSLResolver.class);
    private static final String DSL_RESOLVER_KEYWORD = "INDUSTRY";
    private static final String NCC_TYPE = "NCCTYPE";
    private static final String NCC_BIC = "NCCBIC";
    private static final String BIC_NCC = "BICNCC";

    private static final String NCC_TYPE_VALUE = "NCCTYPEVALUE";
    private static final String NCC_BIC_VALUE = "NCCBICVALUE";
    private static final String BIC_NCC_VALUE = "BICNCCVALUE";

    @Autowired
    private IndustryDataRepository industryDataRepository;

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
        IndustryDataDbModel industryDataDbModel = null;
        Object result = null;

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(NCC_TYPE)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "NCC_TYPE", parameters[0]);
            result = industryDataDbModel;
        }

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(BIC_NCC)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "BIC_NCC", parameters[0]);
            result = industryDataDbModel;
        }

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(NCC_BIC)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "NCC_BIC", parameters[0]);
            result = industryDataDbModel;
        }

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(NCC_TYPE_VALUE)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "NCC_TYPE", parameters[0]);
            result = industryDataDbModel != null ? industryDataDbModel.getValueField() : "null";
        }

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(BIC_NCC_VALUE)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "BIC_NCC", parameters[0]);
            result = industryDataDbModel != null ? industryDataDbModel.getValueField() : "null";
        }

        //By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(NCC_BIC_VALUE)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "NCC_BIC", parameters[0]);
            result = industryDataDbModel != null ? industryDataDbModel.getValueField() : "null";
        }

        return result;
    }

    @Override
    public Object resolveValue(String keyword, String[] parameters, Integer index) {
        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        return null;
    }
}
