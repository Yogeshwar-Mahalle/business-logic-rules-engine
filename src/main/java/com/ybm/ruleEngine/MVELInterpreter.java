/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine;

import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MVELInterpreter {

    public boolean evaluateMvelExpression( String expression, Map<String, Object> inputObjects){
        try {
            return MVEL.evalToBoolean(expression,inputObjects);
        }catch (Exception e){
            log.error("Can not parse Mvel Expression : {} Error: {}", expression, e.getMessage());
        }
        return false;
    }

    public void deriveMvelExpressionAction( String expression, Map<String, Object> inputObjects){
        try {
            MVEL.eval(expression,inputObjects);
        }catch (Exception e){
            log.error("Can not parse Mvel Expression : {} Error: {}", expression, e.getMessage());
        }
    }
}
