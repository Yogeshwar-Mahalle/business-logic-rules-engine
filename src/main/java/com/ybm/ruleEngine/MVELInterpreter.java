/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine;

import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.ast.ASTNode;
import org.mvel2.integration.Interceptor;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class MVELInterpreter {

    static private Map<String, CompiledTemplate> preCompiledTemplateMap = new LinkedHashMap<>();

    public boolean isPreCompiledFuncTemplateExist( String templateID )
    {
        return preCompiledTemplateMap.containsKey( templateID );
    }

    public String evaluateMvelTemplate( String template, LinkedHashMap<String, Object> inputMap ){
        if ( template == null)
            return null;

        try {
            return (String) TemplateRuntime.eval(template, inputMap);
        }catch (Exception e){
            log.error("Can not parse Mvel Expression : {} Error: {}", template, e.getMessage());
        }
        return null;
    }

    public void compileMvelTemplate( String templateId, String template ){
        if ( templateId == null || template == null)
            return;

        try {
            // compile the template
            CompiledTemplate compiledTemplate = TemplateCompiler.compileTemplate(template);
            // store precompiled template in the memory map to access them
            preCompiledTemplateMap.put(templateId, compiledTemplate);
        }catch (Exception e){
            log.error("Can not compile Mvel Template : {} Error: {}", template, e.getMessage());
        }
    }

    public String execPreCompiledMvelTemplate( String templateId, LinkedHashMap<String, Object> inputMap ){
        if ( templateId == null )
            return null;

        try {
            // get precompiled template from the memory map to execute with input data
            CompiledTemplate compiledTemplate = preCompiledTemplateMap.get(templateId);
            // execute the template
            return (String) TemplateRuntime.execute(compiledTemplate, inputMap);
        }catch (Exception e){
            log.error("Can not execute Mvel PreCompiled Template : {} Error: {}", templateId, e.getMessage());
        }
        return null;
    }

    public boolean evaluateMvelExpression( String expression, LinkedHashMap<String, Object> inputMap ){
        if ( expression == null )
            return false;

        try {
            //TODO:: Need to cache it before uncommenting
            //compileMvelInterceptor( expression );

            evaluateMvelTemplate( expression, inputMap );

            return MVEL.evalToBoolean(expression,inputMap);
        }catch (Exception e){
            log.error("Can not parse Mvel Expression : {} Error: {}", expression, e.getMessage());
        }
        return false;
    }

    public void applyMvelExpressionAction( String expression, LinkedHashMap<String, Object> inputMap ){
        if ( expression == null )
            return;

        try {
            //TODO:: Need to cache it before uncommenting
            //compileMvelInterceptor( expression );

            evaluateMvelTemplate( expression, inputMap );

            MVEL.eval(expression, inputMap);
        }catch (Exception e){
            log.error("Can not parse Mvel Expression : {} Error: {}", expression, e.getMessage());
        }
    }

    public void compileMvelInterceptor( String expression ){
        if ( expression == null )
            return;

        try {
            // Create a new ParserContext
            ParserContext context = new ParserContext();
            //context.setStrictTypeEnforcement(true);

            Map<String, Interceptor> interceptors = new LinkedHashMap<String, Interceptor>();

            // Create a simple interceptor.
            Interceptor defaultInterceptor = new Interceptor() {
                public int doBefore(ASTNode node, VariableResolverFactory factory) {
                    log.error("Default Interceptor BEFORE : {} Error: {}", expression, node.getName());
                    return -1;
                }

                public int doAfter(Object value, ASTNode node, VariableResolverFactory factory) {
                    log.error("Default Interceptor AFTER : {} Error: {}", expression, node.getName());
                    return -1;
                }
            };

            // Now add the interceptor to the map.
            interceptors.put("Default", defaultInterceptor);

            // Add the interceptors map to the parser context.
            context.setInterceptors(interceptors);

            // Compile the expression.
            Serializable compiledExpression = MVEL.compileExpression(expression, context);
        }catch (Exception e){
            log.error("Can not parse Mvel Expression : {} Error: {}", expression, e.getMessage());
        }
    }
}
