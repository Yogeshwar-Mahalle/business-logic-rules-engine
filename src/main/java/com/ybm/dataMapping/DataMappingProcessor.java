/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping;

import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.payloadtypes.*;
import com.ybm.dataMapping.processor.DataEnrichmentProcessing;
import com.ybm.dataMapping.processor.DataMapProcessing;
import com.ybm.dataMapping.visitor.*;

public class DataMappingProcessor {
    public static enum MessageFormat {JSON, XML, TEXT, YAML}
    public static String transformMessage(MessageFormat fromMessageFormat, MessageFormat toMessageFormat, String message)
    {
        String returnResult = null;

        PayloadMessageInterface payloadMessageInterface = null;
        switch ( fromMessageFormat )
        {
            case JSON: {
                payloadMessageInterface = new JsonMessage( message );
                break;
            }
            case XML: {
                payloadMessageInterface = new XMLMessage( message );
                break;
            }
            case TEXT: {
                payloadMessageInterface = new TextMessage( message );
                break;
            }
            case YAML: {
                payloadMessageInterface = new YamlMessage( message );
                break;
            }
            default:
                return null;
        }

        //Processing Logic
        payloadMessageInterface.processor( new DataEnrichmentProcessing() );
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
            case TEXT: {
                returnResult = payloadMessageInterface.accept( new ToTextTransformerVisitor() );
                break;
            }
            case YAML: {
                returnResult = payloadMessageInterface.accept( new ToYamlTransformerVisitor() );
                break;
            }
            default:
                return null;
        }

        //If configured apply default common wrapper around the payload
        payloadMessageInterface = new MessageWrapper( returnResult );
        return payloadMessageInterface.accept( new ProcessingVisitor() );
    }
}
