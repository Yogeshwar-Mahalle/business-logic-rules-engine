/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.processor;

import com.ybm.dataMapping.models.StandardFields;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMappingRepo.FieldsDataTransformMappingService;
import com.ybm.dataMappingRepo.models.FieldsDataTransformMapping;
import com.ybm.ruleEngine.MVELInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class DataMapProcessing implements ProcessingInterface {
    private static final Logger LOG = LoggerFactory.getLogger(DataMapProcessing.class);
    @Autowired
    protected MVELInterpreter mvelInterpreter;
    @Autowired
    private FieldsDataTransformMappingService fieldsDataTransformMappingService;

    private String m_TransformMapperId;

    private static LinkedHashMap<String, FieldsDataTransformMapping> transformationMappingScriptMap = new LinkedHashMap<>();

    public DataMapProcessing getInstance(String transformMapperId) {
        this.m_TransformMapperId = transformMapperId;
        return this;
    }

    @Override
    public void process(PayloadMessageInterface payloadMessageInterface) {

        FieldsDataTransformMapping fieldsDataTransformMapping;// = transformationMappingScriptMap.get(this.m_TransformMapperId);
        /*if ( fieldsDataTransformMapping == null )
        {*/
            fieldsDataTransformMapping =
                    fieldsDataTransformMappingService.getFieldsDataTransformMappingById(this.m_TransformMapperId);
            /*if( fieldsDataTransformMapping != null )
                transformationMappingScriptMap.put(this.m_TransformMapperId, fieldsDataTransformMapping);
        }*/

        if( fieldsDataTransformMapping != null ) {

            LinkedHashMap<String, Object> outdataMap = new LinkedHashMap<>();
            LinkedHashMap<String, Object> interfaceDetails = getInterfaceDataMap();

            LinkedHashMap<String, Object> transformationDataMap = new LinkedHashMap<>();
            transformationDataMap.put(StandardFields.INPUT_DATA.label, payloadMessageInterface.getDataMap());
            transformationDataMap.put(StandardFields.OUTPUT_DATA.label, outdataMap);
            transformationDataMap.put(StandardFields.INTERFACE.label, interfaceDetails);

            String dataTransformScript = "import java.util.*;\n";
            dataTransformScript += fieldsDataTransformMapping.getMappingExpressionScript();

            mvelInterpreter.applyMvelExpressionAction(dataTransformScript, transformationDataMap);
            payloadMessageInterface.getDataMap().clear(); //clear old input data
            payloadMessageInterface.getDataMap().putAll(outdataMap); // update all transformed data

            //Set Interface details for data enrichment
            payloadMessageInterface.getDataMap().put(StandardFields.INTERFACE.label, interfaceDetails);
        }
    }

    private static LinkedHashMap<String, Object> getInterfaceDataMap() {
        LinkedHashMap<String, Object> interfaceReq = new LinkedHashMap<>();
        LinkedHashMap<String, Object> interfaceDetails = new LinkedHashMap<>();
        interfaceDetails.put(StandardFields.INTERFACE_HTTP_METHOD.label, "GET");
        interfaceDetails.put(StandardFields.INTERFACE_CONTEXT_PATH.label, "");
        interfaceDetails.put(StandardFields.INTERFACE_QUERY.label, "");
        interfaceDetails.put(StandardFields.INTERFACE_REQ_BODY.label, interfaceReq);
        interfaceDetails.put(StandardFields.INTERFACE_RESPONSE.label, "INTERFACE_RES");
        return interfaceDetails;
    }
}
