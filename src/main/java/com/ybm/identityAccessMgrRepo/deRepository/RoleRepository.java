/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.identityAccessMgrRepo.deRepository;

import com.ybm.identityAccessMgrRepo.entities.RoleDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleDbModel, Long>
{
    Optional<RoleDbModel> findByRoleName(String roleName);

    void deleteByRoleName(String roleName);
}
