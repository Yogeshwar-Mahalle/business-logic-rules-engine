/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.ybm.guiRepo.GUIFieldsLabelService;
import com.ybm.guiRepo.models.GUIFieldsLabel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@EnableMethodSecurity
@RequestMapping(path = "/gui")
public class GUIMetaDataRestController {
    private static final Logger LOG = LoggerFactory.getLogger(GUIMetaDataRestController.class);
    @Autowired
    private GUIFieldsLabelService guiFieldsLabelService;


    @GetMapping(value = "/get-all-gui-field-labels")
    public ResponseEntity<?> getAllGUIFieldLabels() {
        List<GUIFieldsLabel> allGUIFieldsLabel = guiFieldsLabelService.getAllGUIFieldsLabels();
        return ResponseEntity.ok(allGUIFieldsLabel);
    }

    @GetMapping(value = "/get-interface-profile/{fieldUniqueName}")
    public ResponseEntity<?> getGUIFieldLabelById(@PathVariable("fieldUniqueName") String fieldUniqueName) {
        GUIFieldsLabel guiFieldsLabel =
                guiFieldsLabelService.getGUIFieldsLabelByFieldUniqueName(fieldUniqueName);
        return ResponseEntity.ok(guiFieldsLabel);
    }

    @GetMapping(value = "/get-interface-profile/{fieldParentUniqueName}")
    public ResponseEntity<?> getGUIFieldLabelsByParentId(@PathVariable("fieldParentUniqueName") String fieldParentUniqueName) {
        List<GUIFieldsLabel> guiFieldsLabelList =
                guiFieldsLabelService.getGUIFieldsLabelByFieldParentUniqueName(fieldParentUniqueName);
        return ResponseEntity.ok(guiFieldsLabelList);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/update-gui-field-label", consumes = "application/json", produces="application/json")
    public ResponseEntity<?> updateGUIFieldLabel(@RequestBody GUIFieldsLabel guiFieldsLabel) {
        GUIFieldsLabel guiFieldsLabelUpdated = guiFieldsLabelService.saveGUIFieldsLabel(guiFieldsLabel);
        return ResponseEntity.ok(guiFieldsLabelUpdated);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path="/update-all-gui-field-labels")
    public ResponseEntity<?> removeGUIFieldLabel(@PathVariable List<GUIFieldsLabel> guiFieldsLabelList) {
        List<GUIFieldsLabel> guiFieldsLabelListUpdated = guiFieldsLabelService.saveGUIFieldsLabels(guiFieldsLabelList);
        return ResponseEntity.ok(guiFieldsLabelListUpdated);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path="/remove-gui-field-label/{fieldUniqueName}")
    public ResponseEntity<?> removeGUIFieldLabel(@PathVariable String fieldUniqueName) {
        List<GUIFieldsLabel> guiFieldsLabelList = guiFieldsLabelService.removeGUIFieldsLabel(fieldUniqueName);
        return ResponseEntity.ok(guiFieldsLabelList);
    }

}
