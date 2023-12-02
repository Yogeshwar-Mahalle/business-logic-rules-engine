/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl;

import com.ybm.ruleEngine.dslResolver.DSLResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Component
public class APIDSLResolver implements DSLResolver {
    private static final String DSL_RESOLVER_KEYWORD = "api";

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

        response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath)
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

        response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath)
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
                .filter(basicAuthentication(externInterfaceSecUser, externInterfaceSecSecret))
                //.defaultCookie("Key", "Value")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        if( parameters[0].equalsIgnoreCase("GET") )
        {
            String urlQuery = parameters.length > 1 ? parameters[1] : "";
            response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(contextPath)
                            .query(urlQuery)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();
        }
        else if( parameters[0].equalsIgnoreCase("POST") )
        {
            String reqBody = parameters.length > 1 ? parameters[1] : "";
            response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(contextPath)
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

        if( parameters[0].equalsIgnoreCase("GET") )
        {
            String urlQuery = parameters.length > 1 ? parameters[1] : "";
            response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(contextPath)
                            .query(urlQuery)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();
        }
        else if( parameters[0].equalsIgnoreCase("POST") )
        {
            String reqBody = parameters.length > 1 ? parameters[1] : "";
            response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(contextPath)
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
