/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRulesPnAprvlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class BusinessRulesPnAprvlService {
    @Autowired
    private BLRulesPnAprvlRepository blRulesPnAprvlRepository;
    @Autowired
    private BusinessRuleConditionPnAprvlService businessRuleConditionPnAprvlService;
    @Autowired
    private BusinessRuleActionPnAprvlService businessRuleActionPnAprvlService;



}
