/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.guiRepo;

import com.ybm.guiRepo.dbRepository.GUIFieldsLabelRepository;
import com.ybm.guiRepo.entities.GUIFieldsLabelDbModel;
import com.ybm.guiRepo.models.GUIFieldsLabel;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GUIFieldsLabelService {

    @Autowired
    private GUIFieldsLabelRepository guiFieldsLabelRepository;

    public List<GUIFieldsLabel> getAllGUIFieldsLabels() {

        return guiFieldsLabelRepository.findAll().stream()
                .map(
                        this::mapGUIFieldsLabelFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<GUIFieldsLabel> getGUIFieldsLabelByFieldParentUniqueName(String fieldParentUniqueName) {

        return guiFieldsLabelRepository.findByFieldParentUniqueName(fieldParentUniqueName).stream()
                .map(
                        this::mapGUIFieldsLabelFromDbModel
                )
                .collect(Collectors.toList());

    }

    public GUIFieldsLabel getGUIFieldsLabelByFieldUniqueName(String fieldUniqueName) {

        Optional<GUIFieldsLabelDbModel> guiFieldsLabelDbModel = guiFieldsLabelRepository.findById(fieldUniqueName);

        return guiFieldsLabelDbModel.map(this::mapGUIFieldsLabelFromDbModel).orElse(null);
    }

    @Transactional
    public GUIFieldsLabel saveGUIFieldsLabel(GUIFieldsLabel guiFieldsLabel) {
        GUIFieldsLabelDbModel guiFieldsLabelDbModel = mapGUIFieldsLabelToDbModel(guiFieldsLabel);
        guiFieldsLabelDbModel = guiFieldsLabelRepository.save(guiFieldsLabelDbModel);

        return mapGUIFieldsLabelFromDbModel(guiFieldsLabelDbModel);
    }

    @Transactional
    public List<GUIFieldsLabel> saveGUIFieldsLabels(List<GUIFieldsLabel> guiFieldsLabelList) {

        if( guiFieldsLabelList == null )
            return null;

        List<GUIFieldsLabelDbModel> listGUIFieldsLabelDbModel = guiFieldsLabelList
                .stream()
                .map(
                        this::mapGUIFieldsLabelToDbModel
                )
                .toList();

        return guiFieldsLabelRepository.saveAll(listGUIFieldsLabelDbModel)
                .stream()
                .map(
                        this::mapGUIFieldsLabelFromDbModel
                )
                .toList();
    }

    @Transactional
    public List<GUIFieldsLabel> removeGUIFieldsLabel(String fieldUniqueName) {
        guiFieldsLabelRepository.deleteById(fieldUniqueName);

        return getAllGUIFieldsLabels();
    }


    private GUIFieldsLabel mapGUIFieldsLabelFromDbModel(GUIFieldsLabelDbModel guiFieldsLabelDbModel){

        return GUIFieldsLabel.builder()
                .fieldUniqueName(guiFieldsLabelDbModel.getFieldUniqueName())
                .fieldParentUniqueName(guiFieldsLabelDbModel.getFieldParentUniqueName())
                .fieldHTMLId(guiFieldsLabelDbModel.getFieldHTMLId())
                .fieldLabel(guiFieldsLabelDbModel.getFieldLabel())
                .fieldToolTip(guiFieldsLabelDbModel.getFieldToolTip())
                .fieldHTMLType(guiFieldsLabelDbModel.getFieldHTMLType())
                .fieldDataType(guiFieldsLabelDbModel.getFieldDataType())
                .fieldDataFormat(guiFieldsLabelDbModel.getFieldDataFormat())
                .fieldDefaultValue(guiFieldsLabelDbModel.getFieldDefaultValue())
                .fieldMinLength(guiFieldsLabelDbModel.getFieldMinLength())
                .fieldMaxLength(guiFieldsLabelDbModel.getFieldMaxLength())
                .hiddenDataFieldFlag(guiFieldsLabelDbModel.getHiddenDataFieldFlag())
                .createTimeStamp(guiFieldsLabelDbModel.getCreateTimeStamp())
                .build();

    }

    private GUIFieldsLabelDbModel mapGUIFieldsLabelToDbModel(GUIFieldsLabel guiFieldsLabel){

        return GUIFieldsLabelDbModel.builder()
                .fieldUniqueName(guiFieldsLabel.getFieldUniqueName())
                .fieldParentUniqueName(guiFieldsLabel.getFieldParentUniqueName())
                .fieldHTMLId(guiFieldsLabel.getFieldHTMLId())
                .fieldLabel(guiFieldsLabel.getFieldLabel())
                .fieldToolTip(guiFieldsLabel.getFieldToolTip())
                .fieldHTMLType(guiFieldsLabel.getFieldHTMLType())
                .fieldDataType(guiFieldsLabel.getFieldDataType())
                .fieldDataFormat(guiFieldsLabel.getFieldDataFormat())
                .fieldDefaultValue(guiFieldsLabel.getFieldDefaultValue())
                .fieldMinLength(guiFieldsLabel.getFieldMinLength())
                .fieldMaxLength(guiFieldsLabel.getFieldMaxLength())
                .hiddenDataFieldFlag(guiFieldsLabel.getHiddenDataFieldFlag())
                .createTimeStamp(guiFieldsLabel.getCreateTimeStamp() == null ? new Date() : guiFieldsLabel.getCreateTimeStamp())
                .build();

    }

}
