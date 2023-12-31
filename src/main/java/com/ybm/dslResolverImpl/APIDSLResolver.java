/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.ruleEngine.dslResolver.DSLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Component
public class APIDSLResolver implements DSLResolver {
    private static final Logger LOG = LoggerFactory.getLogger(APIDSLResolver.class);
    private static final String DSL_RESOLVER_KEYWORD = "API";

    @Value( "${dsl.external.interface.url}" )
    private String externInterfaceUrl;

    @Value( "${dsl.external.interface.security.type}" )
    private String externInterfaceSecType;

    @Value( "${dsl.external.interface.security.user}" )
    private String externInterfaceSecUser;

    @Value( "${dsl.external.interface.security.user}" )
    private String externInterfaceSecSecret;

    @Override
    public String getResolverKeyword() {
        return DSL_RESOLVER_KEYWORD;
    }

    @Override
    public Object resolveValue() {
        String response = null;

        WebClient webClient = WebClient
                .builder()
                .baseUrl(externInterfaceUrl)
                .filter(basicAuthentication(externInterfaceSecUser, externInterfaceSecSecret))
                //.defaultCookie("Key", "Value")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        response = webClient.get()
                /*.uri(uriBuilder -> uriBuilder
                        .path(contextPath)
                        //.query(finalUrlQuery)
                        .build())*/
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        return response;
    }

    @Override
    public Object resolveValue(String contextPath) {
        String response = null;

        WebClient webClient = WebClient
                .builder()
                .baseUrl(externInterfaceUrl)
                .filter(basicAuthentication(externInterfaceSecUser, externInterfaceSecSecret))
                //.defaultCookie("Key", "Value")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String fullContextPath = "/" + contextPath;
        response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(fullContextPath)
                        //.query(finalUrlQuery)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        return response;
    }

    @Override
    public Object resolveValue(String contextPath, Integer index) {
        String response = null;

        WebClient webClient = WebClient
                .builder()
                .baseUrl(externInterfaceUrl)
                .filter(basicAuthentication(externInterfaceSecUser, externInterfaceSecSecret))
                //.defaultCookie("Key", "Value")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String fullContextPath = "/" + contextPath;
        response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(fullContextPath)
                        //.query(finalUrlQuery)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        return response;
    }

    @Override
    public Object resolveValue(String contextPath, String[] parameters) {
        String response = null;

        WebClient webClient = WebClient
                .builder()
                .baseUrl(externInterfaceUrl)
                //.filter(basicAuthentication(externInterfaceSecUser, externInterfaceSecSecret))
                //.defaultCookie("Key", "Value")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Map<String, String> headers = new LinkedHashMap<>();
        String urlQuery;
        String fullContextPath = "/" + contextPath;
        if ( parameters.length > 1 && parameters[1].contains("?"))
        {
            urlQuery = parameters.length > 1 ? parameters[1] : "";
        }
        else
        {
            urlQuery = "";
            for(int i = 1; i < parameters.length - 1; i++)
            {
                String parameter = parameters[i];
                parameter = parameter.charAt(0) == '\"' ? parameter.substring(1) : parameter;
                parameter = parameter.charAt(parameter.length() - 1 ) == '\"' ? parameter.substring(0, parameter.length() - 1) : parameter;

                if(parameter.contains("{"))
                {
                    ObjectMapper jsonMapper = new ObjectMapper();
                    try {
                        headers = jsonMapper.readValue(parameter, new TypeReference<LinkedHashMap<String, String>>() {});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    fullContextPath += "/" + parameter;
                }
            }
        }

        MediaType acceptedMediaType = MediaType.APPLICATION_JSON;
        if(parameters.length >= 3)
            acceptedMediaType = parameters[3].equalsIgnoreCase("xml") ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;

        if( parameters[0].equalsIgnoreCase("GET") || parameters[0].equalsIgnoreCase("\"GET\"") )
        {
            String finalFullContextPath = fullContextPath;

            response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(finalFullContextPath)
                            //.query(urlQuery)
                            .build())
                    .accept(acceptedMediaType)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();
        }
        else if( parameters[0].equalsIgnoreCase("POST") || parameters[0].equalsIgnoreCase("\"POST\"") )
        {
            String reqBody = parameters.length > 1 ? parameters[parameters.length - 1] : "";
            String finalFullContextPath1 = fullContextPath;
            Map<String, String> finalHeaders = headers;
            response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(finalFullContextPath1)
                            .build())
                    .accept(acceptedMediaType)
                    .headers( reqHeaders -> {

                        finalHeaders.forEach((key, value) -> {
                            List<String> valueList = new ArrayList<>();
                            valueList.add(value);
                            reqHeaders.put(key, valueList);
                        });

                    } )
                    .bodyValue(reqBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();

            if(acceptedMediaType == MediaType.APPLICATION_XML)
                response = response.replace("\"", "\\\"");
        }

        return response;
    }

    @Override
    public Object resolveValue(String contextPath, String[] parameters, Integer index) {
        String response = null;

        WebClient webClient = WebClient
                .builder()
                .baseUrl(externInterfaceUrl)
                .filter(basicAuthentication(externInterfaceSecUser, externInterfaceSecSecret))
                //.defaultCookie("Key", "Value")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String urlQuery;
        String fullContextPath = "/" + contextPath;
        if ( parameters.length > 1 && parameters[1].contains("?"))
        {
            urlQuery = parameters.length > 1 ? parameters[1] : "";
        }
        else
        {
            urlQuery = "";
            for(int i = 1; i < parameters.length; i++)
            {
                String parameter = parameters[i];
                parameter = parameter.charAt(0) == '\"' ? parameter.substring(1) : parameter;
                parameter = parameter.charAt(parameter.length() - 1 ) == '\"' ? parameter.substring(0, parameter.length() - 1) : parameter;
                fullContextPath += "/" + parameter;
            }
        }

        if( parameters[0].equalsIgnoreCase("GET") || parameters[0].equalsIgnoreCase("\"GET\"") )
        {
            String finalFullContextPath = fullContextPath;
            response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(finalFullContextPath)
                            .query(urlQuery)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();
        }
        else if( parameters[0].equalsIgnoreCase("POST") || parameters[0].equalsIgnoreCase("\"POST\"") )
        {
            String reqBody = parameters.length > 1 ? parameters[1] : "";
            String finalFullContextPath1 = fullContextPath;
            response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(finalFullContextPath1)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(reqBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();
        }

        return response;
    }
}
