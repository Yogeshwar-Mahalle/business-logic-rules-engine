/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.ybm.interfacesRepo.InterfaceProfileService;
import com.ybm.interfacesRepo.InterfacePropertyService;
import com.ybm.interfacesRepo.models.InterfaceProfile;
import com.ybm.interfacesRepo.models.InterfaceProperty;
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
@EnableMethodSecurity
@RestController
public class InterfacesRestController {
    private static final Logger LOG = LoggerFactory.getLogger(InterfacesRestController.class);
    @Autowired
    private InterfaceProfileService interfaceProfileService;

    @Autowired
    InterfacePropertyService interfacePropertyService;


    @GetMapping(value = "/get-all-interface-profiles")
    public ResponseEntity<?> getAllInterfaceProfile() {
        List<InterfaceProfile> allInterfaceProfile =
                interfaceProfileService.getAllInterfaceProfile();
        return ResponseEntity.ok(allInterfaceProfile);
    }

    @GetMapping(value = "/get-interface-profiles/{interfaceId}")
    public ResponseEntity<?> getInterfaceProfileById(@PathVariable("interfaceId") String interfaceId) {
        InterfaceProfile InterfaceProfile =
                interfaceProfileService.getInterfaceProfileByInterfaceId(interfaceId);
        return ResponseEntity.ok(InterfaceProfile);
    }

    @GetMapping(value = "/get-interface-profiles/{linkedEntity}")
    public ResponseEntity<?> getInterfaceProfileByEntity(@PathVariable("linkedEntity") String linkedEntity) {
        List<InterfaceProfile> interfaceProfiles =
                interfaceProfileService.getInterfaceProfileByLinkedEntity(linkedEntity);
        return ResponseEntity.ok(interfaceProfiles);
    }

    @GetMapping(value = "/get-interface-profiles/{linkedEntity}/{status}")
    public ResponseEntity<?> getInterfaceProfileByEntityAndStatus(@PathVariable("linkedEntity") String linkedEntity,
                                                                  @PathVariable("status") String status) {
        List<InterfaceProfile> interfaceProfiles =
                interfaceProfileService.getInterfaceProfileByLinkedEntityAndStatus(linkedEntity, status);
        return ResponseEntity.ok(interfaceProfiles);
    }

    @GetMapping(value = "/get-interface-profiles/{linkedEntity}/{interfaceName}")
    public ResponseEntity<?> getInterfaceProfileByName(@PathVariable("linkedEntity") String linkedEntity,
                                                       @PathVariable("interfaceName") String interfaceName) {
        InterfaceProfile interfaceProfile =
                interfaceProfileService.getInterfaceProfileByLinkedEntityAndInterfaceName(linkedEntity, interfaceName);
        return ResponseEntity.ok(interfaceProfile);
    }

    @PostMapping(value = "/update-interface-profile")
    public ResponseEntity<?> updateInterfaceProfile(@RequestBody InterfaceProfile interfaceProfile) {
        InterfaceProfile interfaceProfileUpdated =
                interfaceProfileService.saveInterfaceProfile(interfaceProfile);
        return ResponseEntity.ok(interfaceProfileUpdated);
    }

    @PostMapping(value = "/update-all-interface-profiles")
    public ResponseEntity<?> updateInterfaceProfileList(@RequestBody List<InterfaceProfile> interfaceProfileList) {
        List<InterfaceProfile> interfaceProfileUpdated =
                interfaceProfileService.saveInterfaceProfileList(interfaceProfileList);
        return ResponseEntity.ok(interfaceProfileUpdated);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-interface-profile/{interfaceId}")
    public ResponseEntity<?> removeInterfaceProfileById(@PathVariable("transformMapperId") String interfaceId) {
        List<InterfaceProfile> allRemainingInterfaceProfile =
                interfaceProfileService.removeInterfaceProfileById(interfaceId);
        return ResponseEntity.ok(allRemainingInterfaceProfile);
    }


    @GetMapping(value = "/get-all-interface-properties")
    public ResponseEntity<?> getAllInterfaceProperties() {
        List<InterfaceProperty> allInterfaceProfile =
                interfacePropertyService.getAllInterfaceProperties();
        return ResponseEntity.ok(allInterfaceProfile);
    }

    @GetMapping(value = "/get-interface-properties/{interfaceId}")
    public ResponseEntity<?> getInterfacePropertiesById(@PathVariable("interfaceId") String interfaceId) {
        List<InterfaceProperty> interfaceProperties =
                interfacePropertyService.getInterfacePropertiesByInterfaceId(interfaceId);
        return ResponseEntity.ok(interfaceProperties);
    }

    @GetMapping(value = "/get-interface-properties/{interfaceId}/{status}")
    public ResponseEntity<?> getInterfacePropertiesById(@PathVariable("interfaceId") String interfaceId,
                                                        @PathVariable("status") String status) {
        List<InterfaceProperty> interfaceProperties =
                interfacePropertyService.getInterfacePropertiesByInterfaceIdAndStatus(interfaceId, status);
        return ResponseEntity.ok(interfaceProperties);
    }

    @GetMapping(value = "/get-interface-properties/{interfaceId}/{propertyName}")
    public ResponseEntity<?> getInterfacePropertiesByIdAndName(@PathVariable("interfaceId") String interfaceId,
                                                                @PathVariable("propertyName") String propertyName) {
        InterfaceProperty interfaceProperty =
                interfacePropertyService.getInterfacePropertiesByInterfaceIdAndPropertyName(interfaceId, propertyName);
        return ResponseEntity.ok(interfaceProperty);
    }

    @PostMapping(value = "/update-interface-property")
    public ResponseEntity<?> updateInterfaceProperty(@RequestBody InterfaceProperty interfaceProperty) {
        InterfaceProperty interfacePropertyUpdated =
                interfacePropertyService.saveInterfaceProperty(interfaceProperty);
        return ResponseEntity.ok(interfacePropertyUpdated);
    }

    @PostMapping(value = "/update-all-interface-properties")
    public ResponseEntity<?> updateInterfacePropertiesList(@RequestBody List<InterfaceProperty> interfacePropertyList) {
        List<InterfaceProperty> interfacePropertyUpdated =
                interfacePropertyService.saveInterfacePropertyList(interfacePropertyList);
        return ResponseEntity.ok(interfacePropertyUpdated);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-interface-property/{interfaceId}")
    public ResponseEntity<?> removeInterfacePropertyById(@PathVariable("interfaceId") String interfaceId) {
        List<InterfaceProperty> allRemainingInterfaceProperties =
                interfacePropertyService.removeInterfacePropertiesById(interfaceId);
        return ResponseEntity.ok(allRemainingInterfaceProperties);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-interface-property/{interfaceId}/{propertyName}")
    public ResponseEntity<?> removeInterfacePropertyById(@PathVariable("interfaceId") String interfaceId,
                                                         @PathVariable("propertyName") String propertyName) {
        List<InterfaceProperty> allRemainingInterfaceProperties =
                interfacePropertyService.removeInterfacePropertiesByIdAndName(interfaceId, propertyName);
        return ResponseEntity.ok(allRemainingInterfaceProperties);
    }

}
