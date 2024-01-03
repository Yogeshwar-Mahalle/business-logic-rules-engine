/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.identityAccessMgrRepo.deRepository;

import com.ybm.identityAccessMgrRepo.entities.UserDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDbModel, Long>
{
    Optional<UserDbModel> findByUserId(String userId);

    void deleteByUserId(String userId);
}
