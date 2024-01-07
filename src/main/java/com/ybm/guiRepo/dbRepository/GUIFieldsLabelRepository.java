/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.guiRepo.dbRepository;

import com.ybm.guiRepo.entities.GUIFieldsLabelDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface GUIFieldsLabelRepository extends JpaRepository<GUIFieldsLabelDbModel, String> {

    Collection<GUIFieldsLabelDbModel> findByFieldParentUniqueName(String fieldParentUniqueName);
}
