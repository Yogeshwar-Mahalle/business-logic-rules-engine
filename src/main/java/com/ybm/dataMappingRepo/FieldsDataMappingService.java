/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMappingRepo;

import com.google.common.base.Enums;
import com.ybm.dataMappingRepo.dbRepository.FieldsDataMappingRepository;
import com.ybm.dataMappingRepo.entities.FieldsDataMappingDbModel;
import com.ybm.dataMappingRepo.models.FieldsDataMapping;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class FieldsDataMappingService {

    @Autowired
    private FieldsDataMappingRepository fieldsDataMappingRepository;

    public FieldsDataMapping getFieldsDataMappingById(String uniqueExchangeID) {
        Optional<FieldsDataMappingDbModel> FieldsDataMappingDbModel = fieldsDataMappingRepository.findById(uniqueExchangeID);

        return FieldsDataMappingDbModel.map(this::mapFieldsDataMappingFromDbModel).orElse(null);

    }

    @Transactional
    public FieldsDataMapping saveFieldsDataMapping(FieldsDataMapping FieldsDataMapping) {
        FieldsDataMappingDbModel FieldsDataMappingDbModel = mapFieldsDataMappingToDbModel(FieldsDataMapping);
        FieldsDataMappingDbModel = fieldsDataMappingRepository.save(FieldsDataMappingDbModel);

        return mapFieldsDataMappingFromDbModel(FieldsDataMappingDbModel);
    }


    private FieldsDataMapping mapFieldsDataMappingFromDbModel(FieldsDataMappingDbModel fieldsDataMappingDbModel){

        DataTrimType dataTrimType = Enums.getIfPresent(DataTrimType.class, fieldsDataMappingDbModel.getDataTrimType().toUpperCase())
                .or(DataTrimType.NONE);

        return FieldsDataMapping.builder()
                .fieldDataMappingId(fieldsDataMappingDbModel.getFieldDataMappingId())
                .sourceDataType(fieldsDataMappingDbModel.getSourceDataType())
                .fieldSequenceNumber(fieldsDataMappingDbModel.getFieldSequenceNumber())
                .fieldDataMappingParentId(fieldsDataMappingDbModel.getFieldDataMappingParentId())
                .uniqueFieldName(fieldsDataMappingDbModel.getUniqueFieldName())
                .fieldDisplayName(fieldsDataMappingDbModel.getFieldDisplayName())
                .outputJsonPath(fieldsDataMappingDbModel.getOutputJsonPath())
                .sourceDataFormat(fieldsDataMappingDbModel.getSourceDataFormat())
                .sourcePath(fieldsDataMappingDbModel.getSourcePath())
                .startPosition(fieldsDataMappingDbModel.getStartPosition())
                .length(fieldsDataMappingDbModel.getLength())
                .separator(fieldsDataMappingDbModel.getSeparator())
                .startDelimiter(fieldsDataMappingDbModel.getStartDelimiter())
                .endDelimiter(fieldsDataMappingDbModel.getEndDelimiter())
                .dataTrimType(dataTrimType)
                .createTimeStamp(fieldsDataMappingDbModel.getCreateTimeStamp())
                .updateTimeStamp(fieldsDataMappingDbModel.getUpdateTimeStamp())
                .build();

    }

    private FieldsDataMappingDbModel mapFieldsDataMappingToDbModel(FieldsDataMapping fieldsDataMapping){

        return FieldsDataMappingDbModel.builder()
                .fieldDataMappingId( fieldsDataMapping.getFieldDataMappingId() == null ?
                        fieldsDataMapping.getSourceDataType() + "~" + fieldsDataMapping.getFieldSequenceNumber() + "~" + fieldsDataMapping.getUniqueFieldName() :
                        fieldsDataMapping.getFieldDataMappingId() )
                .sourceDataType(fieldsDataMapping.getSourceDataType())
                .fieldSequenceNumber(fieldsDataMapping.getFieldSequenceNumber())
                .fieldDataMappingParentId(fieldsDataMapping.getFieldDataMappingParentId())
                .uniqueFieldName(fieldsDataMapping.getUniqueFieldName())
                .fieldDisplayName(fieldsDataMapping.getFieldDisplayName())
                .outputJsonPath(fieldsDataMapping.getOutputJsonPath())
                .sourceDataFormat(fieldsDataMapping.getSourceDataFormat())
                .sourcePath(fieldsDataMapping.getSourcePath())
                .startPosition(fieldsDataMapping.getStartPosition())
                .length(fieldsDataMapping.getLength())
                .separator(fieldsDataMapping.getSeparator())
                .startDelimiter(fieldsDataMapping.getStartDelimiter())
                .endDelimiter(fieldsDataMapping.getEndDelimiter())
                .dataTrimType(fieldsDataMapping.getDataTrimType().name())
                .createTimeStamp(fieldsDataMapping.getCreateTimeStamp() == null ? new Date() : fieldsDataMapping.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
