/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.processor;

import com.ybm.dataMapping.StandardFields;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMappingRepo.FieldsDataTransformMappingService;
import com.ybm.dataMappingRepo.models.FieldsDataTransformMapping;
import com.ybm.ruleEngine.MVELInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataMapProcessing implements ProcessingInterface {
    @Autowired
    protected MVELInterpreter mvelInterpreter;
    @Autowired
    private FieldsDataTransformMappingService fieldsDataTransformMappingService;

    private String m_TransformMapperName;

    public DataMapProcessing getInstance(String transformMapperName) {
        this.m_TransformMapperName = transformMapperName;
        return this;
    }

    @Override
    public void process(PayloadMessageInterface payloadMessageInterface) {
        FieldsDataTransformMapping fieldsDataTransformMapping =
                fieldsDataTransformMappingService.getFieldsDataTransformMappingByName(this.m_TransformMapperName);

        if( fieldsDataTransformMapping != null ) {
            Map<String, Object> outdataMap = new HashMap<>();
            Map<String, Object> interfaceDetails = getInterfaceDataMap();

            Map<String, Object> transformationDataMap = new HashMap<>();
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

    private static Map<String, Object> getInterfaceDataMap() {
        Map<String, Object> interfaceReq = new HashMap<>();
        Map<String, Object> interfaceDetails = new HashMap<>();
        interfaceDetails.put(StandardFields.INTERFACE_HTTP_METHOD.label, "GET");
        interfaceDetails.put(StandardFields.INTERFACE_CONTEXT_PATH.label, "");
        interfaceDetails.put(StandardFields.INTERFACE_QUERY.label, "");
        interfaceDetails.put(StandardFields.INTERFACE_REQ_BODY.label, interfaceReq);
        interfaceDetails.put(StandardFields.INTERFACE_RESPONSE.label, "INTERFACE_RES");
        return interfaceDetails;
    }
}
