/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.processor;

import com.ybm.dataMapping.models.StandardFields;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Component
public class DataEnrichmentProcessing implements ProcessingInterface {
    private static final Logger LOG = LoggerFactory.getLogger(DataEnrichmentProcessing.class);

    @Value( "${transformer.external.interface.url}" )
    private String externInterfaceUrl;

    @Value( "${transformer.external.interface.security.type}" )
    private String externInterfaceSecType;

    @Value( "${transformer.external.interface.security.user}" )
    private String externInterfaceSecUser;

    @Value( "${transformer.external.interface.security.user}" )
    private String externInterfaceSecSecret;

    @Override
    public void process(PayloadMessageInterface payloadMessageInterface) {

        LinkedHashMap<String, Object> dataMap = payloadMessageInterface.getDataMap();
        LinkedHashMap<String, Object> interfaceDetails =
                dataMap != null ? (LinkedHashMap<String, Object>) dataMap.get(StandardFields.INTERFACE.label) : null;

        if( interfaceDetails != null ) {
            String httpMethod = (String) interfaceDetails.get(StandardFields.INTERFACE_HTTP_METHOD.label);
            String contextPath = (String) interfaceDetails.get(StandardFields.INTERFACE_CONTEXT_PATH.label);
            contextPath = contextPath == null ? "" : contextPath;

            WebClient webClient = WebClient
                    .builder()
                    .baseUrl(externInterfaceUrl)
                    .filter(basicAuthentication(externInterfaceSecUser, externInterfaceSecSecret))
                    //.defaultCookie("Key", "Value")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            String finalContextPath = contextPath;
            String response = null;
            if( httpMethod.equalsIgnoreCase("GET") )
            {
                String urlQuery = (String) interfaceDetails.get(StandardFields.INTERFACE_QUERY.label);
                urlQuery = urlQuery == null ? "" : urlQuery;
                String finalUrlQuery = urlQuery;

                response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(finalContextPath)
                                .query(finalUrlQuery)
                                .build())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .onErrorResume(e -> Mono.empty())
                        .block();
            }
            else if( httpMethod.equalsIgnoreCase("POST") )
            {
                String reqBody = (String) interfaceDetails.get(StandardFields.INTERFACE_REQ_BODY.label);
                reqBody = reqBody == null ? "" : reqBody;

                response = webClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(finalContextPath)
                                .build())
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(reqBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .onErrorResume(e -> Mono.empty())
                        .block();
            }

            //Set JSON interface response
            String responseFieldNm = (String) dataMap.get(StandardFields.INTERFACE_RESPONSE.label);

            if( responseFieldNm != null )
                dataMap.put(StandardFields.INTERFACE_RESPONSE.label, response);
        }

        if( dataMap != null )
            dataMap.remove(StandardFields.INTERFACE.label);
    }
}
