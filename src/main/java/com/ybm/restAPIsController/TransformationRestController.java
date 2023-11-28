/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.ybm.dataMapping.DataMappingProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
public class TransformationRestController {
    //@Autowired
    //private DataMappingProcessor dataMappingProcessor;

    @PostMapping(value = "/submit/{dataName}/{fromFormat}/{toFormat}")
    public ResponseEntity<?> messageSubmit(@PathVariable(value = "dataName", required = true) String dataName,
                                           @PathVariable(value = "fromFormat", required = true) String fromFormat,
                                           @PathVariable(value = "toFormat", required = true) String toFormat,
                                           @RequestHeader Map<String, String> headers,
                                           @RequestBody String messageString) {

        DataMappingProcessor.MessageFormat fromMessageFormat =
                DataMappingProcessor.MessageFormat.valueOf(fromFormat.toUpperCase());
        DataMappingProcessor.MessageFormat toMessageFormat =
                DataMappingProcessor.MessageFormat.valueOf(toFormat.toUpperCase());

        String result = DataMappingProcessor.transformMessage(dataName, fromMessageFormat, toMessageFormat, messageString);

        MediaType contentType = getMediaType(toMessageFormat);

        return ResponseEntity.ok()
                .contentType(contentType)
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
            case TEXT -> {
                contentType = MediaType.TEXT_PLAIN;
            }
            default -> {
                contentType = MediaType.ALL;
            }
        }
        return contentType;
    }

}
