/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.payloadtypes.*;
import com.ybm.dataMapping.processor.DataEnrichmentProcessing;
import com.ybm.dataMapping.processor.DataMapProcessing;
import com.ybm.dataMapping.visitor.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class DataMappingProcessor {
    public static enum MessageFormat {JSON, XML, YAML, PROP, CSV, TEXT, UNKNOWN}
    public static String transformMessage(String dataName, MessageFormat fromMessageFormat, MessageFormat toMessageFormat, String message) {
        String returnResult = null;

        PayloadMessageInterface payloadMessageInterface = null;
        switch ( fromMessageFormat )
        {
            case JSON: {
                try {
                    payloadMessageInterface = new JsonMessage( dataName, message );
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case XML: {
                try {
                    payloadMessageInterface = new XMLMessage( dataName, message );
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case YAML: {
                try {
                    payloadMessageInterface = new YamlMessage( dataName, message );
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case PROP: {
                try {
                    payloadMessageInterface = new PropMessage( dataName, message );
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case CSV: {
                try {
                    payloadMessageInterface = new CsvMessage( dataName, message );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case TEXT: {
                payloadMessageInterface = new TextMessage( dataName, message );
                break;
            }
            default:
                return "Unknown message format to be transformed.";
        }

        //Processing Logic
        payloadMessageInterface.processor( new DataEnrichmentProcessing() );
        //TODO:: If configured apply transformation mapper
        payloadMessageInterface.processor( new DataMapProcessing() );

        switch ( toMessageFormat )
        {
            case JSON: {
                returnResult = payloadMessageInterface.accept( new ToJsonTransformerVisitor() );
                break;
            }
            case XML: {
                returnResult = payloadMessageInterface.accept( new ToXmlTransformerVisitor() );
                break;
            }
            case YAML: {
                returnResult = payloadMessageInterface.accept( new ToYamlTransformerVisitor() );
                break;
            }
            case PROP: {
                returnResult = payloadMessageInterface.accept( new ToPropTransformerVisitor() );
                break;
            }
            case CSV: {
                returnResult = payloadMessageInterface.accept( new ToCsvTransformerVisitor() );
                break;
            }
            case TEXT: {
                returnResult = payloadMessageInterface.accept( new ToTextTransformerVisitor() );
                break;
            }
            default:
                returnResult = payloadMessageInterface.accept( new DefaultTransformerVisitor() );
        }


        try {

            //TODO:: If configured apply default common wrapper around the payload
            payloadMessageInterface = new MessageWrapper( dataName, returnResult );
            returnResult = payloadMessageInterface.accept(new MessageWrapperVisitor() );

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return returnResult;
    }
}
