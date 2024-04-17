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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Component
public class DataMappingProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(DataMappingProcessor.class);
    @Autowired
    DataEnrichmentProcessing dataEnrichmentProcessing;

    @Autowired
    DataMapProcessing dataMapProcessing;
    public static enum MessageFormat {JSON, XML, YAML, PROP, CSV, TEXT, ISO8583, SWIFT, UNKNOWN}
    public String transformMessage(String dataName,
                                          MessageFormat fromMessageFormat,
                                          MessageFormat toMessageFormat,
                                          String message,
                                          String transformMapperName) {
        String returnResult = null;

        PayloadMessageInterface payloadMessageInterface = null;
        switch ( fromMessageFormat )
        {
            case JSON: {
                try {
                    payloadMessageInterface = new JsonMessage( dataName, message );
                } catch (JsonProcessingException e) {
                    LOG.error(e.getMessage());
                    throw new RuntimeException(e);
                }
                break;
            }
            case XML: {
                try {
                    payloadMessageInterface = new XMLMessage( dataName, message );
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    LOG.error(e.getMessage());
                    throw new RuntimeException(e);
                }
                break;
            }
            case YAML: {
                try {
                    payloadMessageInterface = new YamlMessage( dataName, message );
                } catch (JsonProcessingException e) {
                    LOG.error(e.getMessage());
                    throw new RuntimeException(e);
                }
                break;
            }
            case PROP: {
                try {
                    payloadMessageInterface = new PropMessage( dataName, message );
                } catch (JsonProcessingException e) {
                    LOG.error(e.getMessage());
                    throw new RuntimeException(e);
                }
                break;
            }
            case CSV: {
                try {
                    payloadMessageInterface = new CsvMessage( dataName, message );
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                    throw new RuntimeException(e);
                }
                break;
            }
            case TEXT: {
                payloadMessageInterface = new TextMessage( dataName, message );
                break;
            }
            case ISO8583: {
                try {
                    payloadMessageInterface = new ISO8583Message( dataName, message );
                } catch (JsonProcessingException e) {
                    LOG.error(e.getMessage());
                    throw new RuntimeException(e);
                }
                break;
            }
            case SWIFT: {
                payloadMessageInterface = new SWIFTMessage( dataName, message );
                break;
            }
            default: {
                try {
                    payloadMessageInterface = new JsonErrMessage( dataName, message );
                } catch (JsonProcessingException e) {
                    LOG.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }

        //Validate the input message format and store validation data in the map
        if ( !payloadMessageInterface.validate() ) {
            returnResult = payloadMessageInterface.accept(new DefaultTransformerVisitor());
        }
        else {

            //If configured apply transformation mapper
            payloadMessageInterface.processor( dataMapProcessing.getInstance( transformMapperName ) );

            //Fetch and enrich additional data by calling external interface if
            payloadMessageInterface.processor( dataEnrichmentProcessing );

            switch (toMessageFormat) {
                case JSON: {
                    returnResult = payloadMessageInterface.accept(new ToJsonTransformerVisitor());
                    try {
                        payloadMessageInterface = new JsonMessage( dataName, returnResult );
                    } catch (JsonProcessingException e) {
                        LOG.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case XML: {
                    returnResult = payloadMessageInterface.accept(new ToXmlTransformerVisitor());
                    try {
                        payloadMessageInterface = new XMLMessage( dataName, returnResult );
                    } catch (IOException | ParserConfigurationException | SAXException e) {
                        LOG.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case YAML: {
                    returnResult = payloadMessageInterface.accept(new ToYamlTransformerVisitor());
                    try {
                        payloadMessageInterface = new YamlMessage( dataName, returnResult );
                    } catch (JsonProcessingException e) {
                        LOG.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case PROP: {
                    returnResult = payloadMessageInterface.accept(new ToPropTransformerVisitor());
                    try {
                        payloadMessageInterface = new PropMessage( dataName, returnResult );
                    } catch (JsonProcessingException e) {
                        LOG.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case CSV: {
                    returnResult = payloadMessageInterface.accept(new ToCsvTransformerVisitor());
                    try {
                        payloadMessageInterface = new CsvMessage( dataName, returnResult );
                    } catch (IOException e) {
                        LOG.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case TEXT: {
                    returnResult = payloadMessageInterface.accept(new ToTextTransformerVisitor());
                    payloadMessageInterface = new TextMessage( dataName, returnResult );
                    break;
                }
                case ISO8583: {
                    returnResult = payloadMessageInterface.accept(new ToISO8583TransformerVisitor());
                    try {
                        payloadMessageInterface = new ISO8583Message( dataName, returnResult );
                    } catch (JsonProcessingException e) {
                        LOG.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case SWIFT: {
                    returnResult = payloadMessageInterface.accept(new ToSWIFTTransformerVisitor());
                    payloadMessageInterface = new SWIFTMessage( dataName, returnResult );
                    break;
                }
                default: {
                    returnResult = payloadMessageInterface.accept(new DefaultTransformerVisitor());
                    try {
                        payloadMessageInterface = new JsonErrMessage( dataName, returnResult );
                    } catch (JsonProcessingException e) {
                        LOG.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }

            //If configured apply validation on transformed message
            if ( !payloadMessageInterface.validate() ) {
                returnResult = payloadMessageInterface.accept(new DefaultTransformerVisitor());
            }
        }

        try {

            //TODO:: If configured apply default common wrapper around the payload
            payloadMessageInterface = new MessageWrapper( dataName, returnResult );
            returnResult = payloadMessageInterface.accept(new MessageWrapperVisitor() );

        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return returnResult;
    }
}
