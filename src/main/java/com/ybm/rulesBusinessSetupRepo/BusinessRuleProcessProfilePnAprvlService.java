/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleProcessProfilePnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleProcessProfilePnAprvlDbModel;
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
public class BusinessRuleProcessProfilePnAprvlService {

    @Autowired
    private BLRuleProcessProfilePnAprvlRepository blRuleProcessProfilePnAprvlRepository;

    public List<BusinessLogicRuleProcessProfile> getAllProcessProfilesPnAprvl() {
        return blRuleProcessProfilePnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleProcessProfilePnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }
    public BusinessLogicRuleProcessProfile getProcessProfilePnAprvlByEntityAndProfileName(String linkedEntity, String profileName) {
        Optional<BLRuleProcessProfilePnAprvlDbModel> blRuleProcessProfilePnAprvlDbModel =
                blRuleProcessProfilePnAprvlRepository.findByLinkedEntityAndProfileName(linkedEntity, profileName);
        return blRuleProcessProfilePnAprvlDbModel.map(this::mapRuleProcessProfilePnAprvlFromDbModel).orElse(null);
    }


    @Transactional
    public BusinessLogicRuleProcessProfile saveRuleProcessProfilePnAprvl(BusinessLogicRuleProcessProfile businessLogicRuleProcessProfile) {
        BLRuleProcessProfilePnAprvlDbModel blRuleProcessProfilePnAprvlDbModel = mapRuleProcessProfilePnAprvlToDbModel(businessLogicRuleProcessProfile);
        blRuleProcessProfilePnAprvlDbModel = blRuleProcessProfilePnAprvlRepository.save(blRuleProcessProfilePnAprvlDbModel);
        return mapRuleProcessProfilePnAprvlFromDbModel(blRuleProcessProfilePnAprvlDbModel);
    }

    @Transactional
    public List<BusinessLogicRuleProcessProfile> saveRuleProcessProfilesPnAprvl(List<BusinessLogicRuleProcessProfile> businessLogicRuleProcessProfiles) {

        List<BLRuleProcessProfilePnAprvlDbModel> listBLRuleProcessProfilePnAprvlDBModel = businessLogicRuleProcessProfiles.stream()
                .map(
                        this::mapRuleProcessProfilePnAprvlToDbModel
                )
                .collect(Collectors.toList());

        return blRuleProcessProfilePnAprvlRepository.saveAll(listBLRuleProcessProfilePnAprvlDBModel).stream()
                .map(
                        this::mapRuleProcessProfilePnAprvlFromDbModel
                )
                .collect(Collectors.toList());

    }

    @Transactional
    public List<BusinessLogicRuleProcessProfile> removeRuleProcessProfilePnAprvlByEntityAndProfileName(String linkedEntity, String profileName) {

        blRuleProcessProfilePnAprvlRepository.deleteByLinkedEntityAndProfileName(linkedEntity, profileName);

        return blRuleProcessProfilePnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleProcessProfilePnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleProcessProfile mapRuleProcessProfilePnAprvlFromDbModel(BLRuleProcessProfilePnAprvlDbModel blRuleProcessProfileDbModel){

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

    private BLRuleProcessProfilePnAprvlDbModel mapRuleProcessProfilePnAprvlToDbModel(BusinessLogicRuleProcessProfile businessLogicRuleProcessProfile){

        return BLRuleProcessProfilePnAprvlDbModel.builder()
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
