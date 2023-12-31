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
public class ValueListDSLResolver implements DSLResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ValueListDSLResolver.class);
    private static final String DSL_RESOLVER_KEYWORD = "VALUELIST";
    private static final String WORKFLOW = "WORKFLOW";

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
        if (keyword.equalsIgnoreCase(WORKFLOW)){
            industryDataDbModel = industryDataRepository.findByDataTypeAndKeyField( "WRK_FLOW_TYPE", parameters[0]);
            result = industryDataDbModel.getValueField();
        }

        return result;
    }

    @Override
    public Object resolveValue(String keyword, String[] parameters, Integer index) {
        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        return null;
    }
}
