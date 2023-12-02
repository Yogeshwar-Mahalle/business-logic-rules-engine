/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMappingRepo;

import com.ybm.dataMappingRepo.dbRepository.FieldsDataTransformMappingRepository;
import com.ybm.dataMappingRepo.entities.FieldsDataTransformMappingDbModel;
import com.ybm.dataMappingRepo.models.FieldsDataTransformMapping;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class FieldsDataTransformMappingService {

    @Autowired
    private FieldsDataTransformMappingRepository fieldsDataTransformMappingRepository;

    public List<FieldsDataTransformMapping> getAllFieldsDataTransformMapping() {

        return fieldsDataTransformMappingRepository.findAll().stream()
                .map(
                        this::mapFieldsDataTransformMappingFromDbModel
                )
                .toList();
    }

    public FieldsDataTransformMapping getFieldsDataTransformMappingById(String transformMappingId) {
        if( transformMappingId == null )
            return null;

        Optional<FieldsDataTransformMappingDbModel> fieldsDataMappingDbModel = fieldsDataTransformMappingRepository.findById(transformMappingId);

        return fieldsDataMappingDbModel.map(this::mapFieldsDataTransformMappingFromDbModel).orElse(null);
    }

    public FieldsDataTransformMapping getFieldsDataTransformMappingByName(String transformMapperName) {
        if( transformMapperName == null )
            return null;

        Optional<FieldsDataTransformMappingDbModel> fieldsDataMappingDbModel = fieldsDataTransformMappingRepository.findByTransformMapperName(transformMapperName);

        return fieldsDataMappingDbModel.map(this::mapFieldsDataTransformMappingFromDbModel).orElse(null);
    }

    @Transactional
    public FieldsDataTransformMapping saveFieldsDataMapping(FieldsDataTransformMapping fieldsDataTransformMapping) {
        if( fieldsDataTransformMapping == null )
            return null;

        FieldsDataTransformMappingDbModel fieldsDataTransformMappingDbModel = mapFieldsDataTransformMappingToDbModel(fieldsDataTransformMapping);
        fieldsDataTransformMappingDbModel = fieldsDataTransformMappingRepository.save(fieldsDataTransformMappingDbModel);

        return mapFieldsDataTransformMappingFromDbModel(fieldsDataTransformMappingDbModel);
    }

    public List<FieldsDataTransformMapping> saveFieldsDataMappingList(List<FieldsDataTransformMapping> fieldsDataTransformMappingList) {
        if( fieldsDataTransformMappingList == null )
            return null;

        List<FieldsDataTransformMappingDbModel> listFieldsDataTransformMappingDbModel = fieldsDataTransformMappingList
                .stream()
                .map(
                        this::mapFieldsDataTransformMappingToDbModel
                )
                .collect(Collectors.toList());

        return fieldsDataTransformMappingRepository.saveAll(listFieldsDataTransformMappingDbModel)
                .stream()
                .map(
                        this::mapFieldsDataTransformMappingFromDbModel
                )
                .toList();
    }

    public List<FieldsDataTransformMapping> removeFieldsDataTransformMappingById(String transformMapperId) {
        if( transformMapperId == null )
            return null;

        fieldsDataTransformMappingRepository.deleteById(transformMapperId);

        return fieldsDataTransformMappingRepository.findAll().stream()
                .map(
                        this::mapFieldsDataTransformMappingFromDbModel
                )
                .collect(Collectors.toList());
    }

    private FieldsDataTransformMapping mapFieldsDataTransformMappingFromDbModel(FieldsDataTransformMappingDbModel fieldsDataTransformMappingDbModel){

        return FieldsDataTransformMapping.builder()
                .transformMappingId(fieldsDataTransformMappingDbModel.getTransformMappingId())
                .transformMapperName(fieldsDataTransformMappingDbModel.getTransformMapperName())
                .mappingExpressionScript(fieldsDataTransformMappingDbModel.getMappingExpressionScript())
                .createTimeStamp(fieldsDataTransformMappingDbModel.getCreateTimeStamp())
                .updateTimeStamp(fieldsDataTransformMappingDbModel.getUpdateTimeStamp())
                .build();

    }

    private FieldsDataTransformMappingDbModel mapFieldsDataTransformMappingToDbModel(FieldsDataTransformMapping fieldsDataTransformMapping){

        return FieldsDataTransformMappingDbModel.builder()
                .transformMappingId( fieldsDataTransformMapping.getTransformMappingId() == null ?
                        fieldsDataTransformMapping.getTransformMapperName() + "~" + new Date().getTime() :
                        fieldsDataTransformMapping.getTransformMappingId() )
                .transformMapperName(fieldsDataTransformMapping.getTransformMapperName())
                .mappingExpressionScript(fieldsDataTransformMapping.getMappingExpressionScript())
                .createTimeStamp(fieldsDataTransformMapping.getCreateTimeStamp() == null ? new Date() : fieldsDataTransformMapping.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }



}
