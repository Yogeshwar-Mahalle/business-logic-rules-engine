/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.google.common.base.Enums;
import com.ybm.dataMapping.DataMappingProcessor;
import com.ybm.dataMappingRepo.FieldsDataTransformMappingService;
import com.ybm.dataMappingRepo.models.FieldsDataTransformMapping;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@EnableMethodSecurity
@RestController
public class TransformationRestController {
    private static final Logger LOG = LoggerFactory.getLogger(TransformationRestController.class);
    @Autowired
    private FieldsDataTransformMappingService fieldsDataTransformMappingService;

    @Autowired
    DataMappingProcessor dataMappingProcessor;

    @GetMapping(value = "/get-all-transform-mapper")
    public ResponseEntity<?> getAllTransformMapper() {
        List<FieldsDataTransformMapping> allFieldsDataTransformMapping =
                fieldsDataTransformMappingService.getAllFieldsDataTransformMapping();
        return ResponseEntity.ok(allFieldsDataTransformMapping);
    }

    @GetMapping(value = "/get-transform-mapper/{transformMapperId}")
    public ResponseEntity<?> getTransformMapperById(@PathVariable("transformMapperId") String transformMapperId) {
        FieldsDataTransformMapping fieldsDataTransformMapping =
                fieldsDataTransformMappingService.getFieldsDataTransformMappingById(transformMapperId);
        return ResponseEntity.ok(fieldsDataTransformMapping);
    }

    @GetMapping(value = "/get-transform-mapper/{transformMapperName}/{transformMapperVersion}")
    public ResponseEntity<?> getTransformMapperByName(@PathVariable("transformMapperName") String transformMapperName,
                                                      @PathVariable("transformMapperVersion") String transformMapperVersion) {
        FieldsDataTransformMapping fieldsDataTransformMapping =
                fieldsDataTransformMappingService.getFieldsDataTransformMappingByNameAndVersion(transformMapperName, transformMapperVersion);
        return ResponseEntity.ok(fieldsDataTransformMapping);
    }

    @PostMapping(value = "/update-transform-mapper")
    public ResponseEntity<?> updateTransformMapper(@RequestBody FieldsDataTransformMapping fieldsDataTransformMapping) {
        FieldsDataTransformMapping fieldsDataTransformMappingIUpdated =
                fieldsDataTransformMappingService.saveFieldsDataMapping(fieldsDataTransformMapping);
        return ResponseEntity.ok(fieldsDataTransformMappingIUpdated);
    }

    @PostMapping(value = "/update-all-transform-mapper")
    public ResponseEntity<?> updateTransformMapperList(@RequestBody List<FieldsDataTransformMapping> fieldsDataTransformMappingList) {
        List<FieldsDataTransformMapping> fieldsDataTransformMappingUpdated =
                fieldsDataTransformMappingService.saveFieldsDataMappingList(fieldsDataTransformMappingList);
        return ResponseEntity.ok(fieldsDataTransformMappingUpdated);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-transform-mapper/{transformMapperId}")
    public ResponseEntity<?> removeTransformMapperById(@PathVariable("transformMapperId") String transformMapperId) {
        List<FieldsDataTransformMapping> allRemainingFieldsDataTransformMapping =
                fieldsDataTransformMappingService.removeFieldsDataTransformMappingById(transformMapperId);
        return ResponseEntity.ok(allRemainingFieldsDataTransformMapping);
    }

    @PostMapping(value = "/submit/{dataName}/{fromFormat}/{toFormat}")
    public ResponseEntity<?> messageSubmit(@PathVariable(value = "dataName", required = true) String dataName,
                                           @PathVariable(value = "fromFormat", required = true) String fromFormat,
                                           @PathVariable(value = "toFormat", required = true) String toFormat,
                                           @RequestHeader LinkedHashMap<String, String> headers,
                                           @RequestBody String messageString) {

        DataMappingProcessor.MessageFormat fromMessageFormat = Enums.getIfPresent(DataMappingProcessor.MessageFormat.class, fromFormat.toUpperCase())
                .or(DataMappingProcessor.MessageFormat.UNKNOWN);
        DataMappingProcessor.MessageFormat toMessageFormat = Enums.getIfPresent(DataMappingProcessor.MessageFormat.class, toFormat.toUpperCase())
                .or(DataMappingProcessor.MessageFormat.UNKNOWN);

        String transformMapperName = headers.get("transform-mapper") == null ? headers.get("TRANSFORM-MAPPER") : headers.get("transform-mapper");
        String linkedEntity = headers.get("linked-entity") == null ? headers.get("LINKED-ENTITY") : headers.get("linked-entity");
        String version = headers.get("version") == null ? headers.get("VERSION") : headers.get("version");
        linkedEntity = linkedEntity == null ? "EuclidPro" : linkedEntity;
        version = version == null ? "01" : version;
        String result = dataMappingProcessor.transformMessage( dataName,
                                                                fromMessageFormat,
                                                                toMessageFormat,
                                                                messageString,
                                                                linkedEntity,
                                                                transformMapperName,
                                                                version );

        MediaType contentType = getMediaType(toMessageFormat);

        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(result.length())
                .body(result);
    }

    private static MediaType getMediaType(DataMappingProcessor.MessageFormat toMessageFormat) {
        MediaType contentType;

        switch (toMessageFormat)
        {
            case XML -> {
                contentType = MediaType.APPLICATION_XML;
            }
            case JSON -> {
                contentType = MediaType.APPLICATION_JSON;
            }
            case YAML -> {
                contentType = MediaType.valueOf("application/x-yaml");
            }
            case PROP -> {
                contentType = MediaType.valueOf("text/x-java-properties");
            }
            case CSV -> {
                contentType = MediaType.valueOf("text/csv");
            }
            case TEXT, ISO8583, SWIFT -> {
                contentType = MediaType.TEXT_PLAIN;
            }
            default -> {
                contentType = MediaType.TEXT_HTML;
            }
        }
        return contentType;
    }

}
