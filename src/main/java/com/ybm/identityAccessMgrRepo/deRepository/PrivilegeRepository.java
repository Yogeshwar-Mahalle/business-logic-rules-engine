/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.identityAccessMgrRepo.deRepository;

import com.ybm.identityAccessMgrRepo.entities.PrivilegeDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRepository extends JpaRepository<PrivilegeDbModel, Long>
{
    void deleteByPrivilegeName(String privilegeName);

    Optional<PrivilegeDbModel> findByPrivilegeName(String privilegeName);
}
