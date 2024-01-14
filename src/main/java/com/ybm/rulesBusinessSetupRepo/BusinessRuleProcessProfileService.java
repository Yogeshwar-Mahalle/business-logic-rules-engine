/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleProcessProfileRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleProcessProfileDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleProcessProfile;
import com.ybm.rulesBusinessSetupRepo.models.StatusType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleProcessProfileService {

    @Autowired
    private BLRuleProcessProfileRepository blRuleProcessProfileRepository;

    public List<BusinessLogicRuleProcessProfile> getAllProcessProfiles() {
        return blRuleProcessProfileRepository.findAll().stream()
                .map(
                        this::mapRuleProcessProfileFromDbModel
                )
                .collect(Collectors.toList());
    }
    public BusinessLogicRuleProcessProfile getProcessProfileByEntityAndProfileName(String linkedEntity, String profileName) {
        Optional<BLRuleProcessProfileDbModel> blRuleProcessProfileDbModel =
                blRuleProcessProfileRepository.findByLinkedEntityAndProfileName(linkedEntity, profileName);
        return blRuleProcessProfileDbModel.map(this::mapRuleProcessProfileFromDbModel).orElse(null);
    }


    @Transactional
    public BusinessLogicRuleProcessProfile saveRuleProcessProfile(BusinessLogicRuleProcessProfile businessLogicRuleProcessProfile) {
        BLRuleProcessProfileDbModel blRuleProcessProfileDbModel = mapRuleProcessProfileToDbModel(businessLogicRuleProcessProfile);
        blRuleProcessProfileDbModel = blRuleProcessProfileRepository.save(blRuleProcessProfileDbModel);
        return mapRuleProcessProfileFromDbModel(blRuleProcessProfileDbModel);
    }

    @Transactional
    public List<BusinessLogicRuleProcessProfile> saveRuleProcessProfiles(List<BusinessLogicRuleProcessProfile> businessLogicRuleProcessProfiles) {

        List<BLRuleProcessProfileDbModel> listBLRuleProcessProfileDBModel = businessLogicRuleProcessProfiles.stream()
                .map(
                        this::mapRuleProcessProfileToDbModel
                )
                .collect(Collectors.toList());

        return blRuleProcessProfileRepository.saveAll(listBLRuleProcessProfileDBModel).stream()
                .map(
                        this::mapRuleProcessProfileFromDbModel
                )
                .collect(Collectors.toList());

    }

    @Transactional
    public List<BusinessLogicRuleProcessProfile> removeRuleProcessProfileByEntityAndProfileName(String linkedEntity, String profileName) {

        blRuleProcessProfileRepository.deleteByLinkedEntityAndProfileName(linkedEntity, profileName);

        return blRuleProcessProfileRepository.findAll().stream()
                .map(
                        this::mapRuleProcessProfileFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleProcessProfile mapRuleProcessProfileFromDbModel(BLRuleProcessProfileDbModel blRuleProcessProfileDbModel){

        return BusinessLogicRuleProcessProfile.builder()
                .linkedEntity(blRuleProcessProfileDbModel.getLinkedEntity())
                .profileName(blRuleProcessProfileDbModel.getProfileName())
                .description(blRuleProcessProfileDbModel.getDescription())
                .countryCode(blRuleProcessProfileDbModel.getCountryCode())
                .currencyCode(blRuleProcessProfileDbModel.getCurrencyCode())
                .businessDate(blRuleProcessProfileDbModel.getBusinessDate())
                .timeZone(blRuleProcessProfileDbModel.getTimeZone())
                .valueListType(blRuleProcessProfileDbModel.getValueListType())
                .status(StatusType.valueOf(blRuleProcessProfileDbModel.getStatus()))
                .createTimeStamp(blRuleProcessProfileDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleProcessProfileDbModel.getUpdateTimeStamp())
                .build();
    }

    private BLRuleProcessProfileDbModel mapRuleProcessProfileToDbModel(BusinessLogicRuleProcessProfile businessLogicRuleProcessProfile){

        return BLRuleProcessProfileDbModel.builder()
                .linkedEntity(businessLogicRuleProcessProfile.getLinkedEntity())
                .profileName(businessLogicRuleProcessProfile.getProfileName())
                .description(businessLogicRuleProcessProfile.getDescription())
                .countryCode(businessLogicRuleProcessProfile.getCountryCode())
                .currencyCode(businessLogicRuleProcessProfile.getCurrencyCode())
                .businessDate(businessLogicRuleProcessProfile.getBusinessDate() == null ? new Date() : businessLogicRuleProcessProfile.getBusinessDate())
                .timeZone(businessLogicRuleProcessProfile.getTimeZone())
                .valueListType(businessLogicRuleProcessProfile.getValueListType())
                .status(String.valueOf(businessLogicRuleProcessProfile.getStatus()))
                .createTimeStamp(businessLogicRuleProcessProfile.getCreateTimeStamp() == null ? new Date() : businessLogicRuleProcessProfile.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
