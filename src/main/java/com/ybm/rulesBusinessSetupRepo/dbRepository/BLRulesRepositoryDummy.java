/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.google.common.collect.Lists;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleDbModel;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BLRulesRepositoryDummy {

    private List<BLRuleDbModel> rulesList;

    public BLRulesRepositoryDummy() {

        BLRuleDbModel businessLogicRule1 = BLRuleDbModel.builder()
                .ruleType("LOAN")
                .ruleName("BusinessLogicRule-1")
                .ruleId("LOAN" + "~" + "1")
                .condition("inPayload.monthlySalary >= 50000.0 && inPayload.cibilScore >= 500 && inPayload.requestedLoanAmount<1500000 && $(bank.eligibleAmount) >= 50000.0")
                .action("outPayload.put(\"approvalStatus\",true); outPayload.put(\"interestRate\",$(bank.interestRate)); outPayload.put(\"sanctionedPercentage\",90);outPayload.put(\"processingFees\",2000);outPayload.put(\"accountNumber\",inPayload.accountNumber);")
                .priority(1)
                .description("A person is eligible for loan?")
                .status("AC")
                .createTimeStamp(new Date())
                .updateTimeStamp(new Date())
                .linkedEntity("IN")
                .build();

        BLRuleDbModel businessLogicRule2 = BLRuleDbModel.builder()
                .ruleType("LOAN")
                .ruleName("BusinessLogicRule-2")
                .ruleId("LOAN" + "~" + "2")
                .condition("(inPayload.monthlySalary < 50000.0 && inPayload.cibilScore <= 300 && inPayload.requestedLoanAmount >= 1000000) || $(bank.eligibleAmount) < 50000.0")
                .action("outPayload.put(\"approvalStatus\",true); outPayload.put(\"interestRate\",0.0); outPayload.put(\"sanctionedPercentage\",0.0);outPayload.put(\"processingFees\",0);outPayload.put(\"accountNumber\",inPayload.accountNumber);")
                .priority(2)
                .description("A person is eligible for car loan?")
                .status("AC")
                .createTimeStamp(new Date())
                .updateTimeStamp(new Date())
                .linkedEntity("IN")
                .build();

        BLRuleDbModel businessLogicRule3 = BLRuleDbModel.builder()
                .ruleType("LOAN")
                .ruleName("BusinessLogicRule-3")
                .ruleId("LOAN" + "~" + "3")
                .condition("inPayload.monthlySalary >= 20000.0 && inPayload.cibilScore >= 300 && inPayload.cibilScore < 500 && inPayload.requestedLoanAmount <= 1000000 && $(bank.eligibleAmount) >= 50000.0")
                .action("outHeaders.put(\"content-type\",\"application/json\");" +
                        "outProperties.put(\"processedFlag\",true);" +
                        "outProperties.put(\"LOAN_TYPE\",inProperties.LOAN_TYPE);" +
                        "foreach( pair : inPayload.entrySet() ) { outPayload.put(pair.getKey(), pair.getValue()); };" +
                        "outPayload.put(\"approvalStatus\",true);" +
                        "inProperties.LOAN_TYPE == \"HOUSE_LOAN\" ? outPayload.put(\"interestRate\",$(bank.interestRate)) : outPayload.put(\"interestRate\",12.0);" +
                        "outPayload.put(\"sanctionedPercentage\",70);" +
                        "outPayload.put(\"processingFees\",1000);" +
                        "outPayload.put(\"accountNumber\",inPayload.accountNumber);" +
                        "outProperties.put(\"annualSalary\",inPayload.monthlySalary * 12);" +
                        "outProperties.put(\"flatAnnualIncomeTaxPaid\",outProperties.annualSalary * 0.3);" +
                        "outProperties.put(\"takeHomeAnnualSalary\",outProperties.annualSalary - outProperties.flatAnnualIncomeTaxPaid);" +
                        "outProperties.put(\"middleName\",inPayload.fullName.substring(inPayload.fullName.indexOf(' '), inPayload.fullName.indexOf(' ', inPayload.fullName.indexOf(' ')+1)).trim());" +
                        "outProperties.put(\"array\", [{\"firstNm\": \"Yogeshwar\",\"middleNm\": \"B\",\"lastNm\": \"Mahalle\",},{\"firstNm\": \"123\",\"middleNm\": \"abc\",\"lastNm\": \"XYZ\",}]);" +
                        "address=\"\";" +
                        "foreach(adr: inPayload.address){ address += adr + ' '; };" +
                        "outProperties.put(\"ADDRESS\",address);" +
                        "outPayload.put(\"address\",outProperties.ADDRESS + inPayload.city + ' ' + inPayload.state + ' ' + " +
                        "  if(inPayload.?country != empty && true ) { inPayload.country } else { \"IN\" } );" )
                .priority(3)
                .description("A person is eligible for car loan?")
                .status("AC")
                .createTimeStamp(new Date())
                .updateTimeStamp(new Date())
                .linkedEntity("IN")
                .build();

        BLRuleDbModel businessLogicRule4 = BLRuleDbModel.builder()
                .ruleType("LOAN")
                .ruleName("BusinessLogicRule-4")
                .ruleId("LOAN" + "~" + "4")
                .condition("inPayload.monthlySalary >= 50000 && inPayload.cibilScore >= 800 && inPayload.requestedLoanAmount < 4000000 && $(bank.eligibleAmount) >= 50000.0")
                .action("outPayload.put('approvalStatus',true);outPayload.put('interestRate',$(bank.interestRate));outPayload.put('sanctionedPercentage',90);outPayload.put('accountNumber',inPayload.accountNumber);outPayload.put('processingFees',8000);")
                .priority(4)
                .description("A person is eligible for Home loan?")
                .status("AC")
                .createTimeStamp(new Date())
                .updateTimeStamp(new Date())
                .linkedEntity("IN")
                .build();

        BLRuleDbModel businessLogicRule5 = BLRuleDbModel.builder()
                .ruleType("LOAN")
                .ruleName("BusinessLogicRule-5")
                .ruleId("LOAN" + "~" + "5")
                .condition("inPayload.monthlySalary >= 35000 && inPayload.monthlySalary <= 50000 && inPayload.cibilScore <= 500 && inPayload.requestedLoanAmount < 2000000 && $(bank.eligibleAmount) >= 50000.0")
                .action("outPayload.put(\"approvalStatus\",true);outPayload.put('interestRate',$(bank.interestRate));outPayload.put(\"sanctionedPercentage\",60);outPayload.put('accountNumber',inPayload.accountNumber);outPayload.put(\"processingFees\",2000);")
                .priority(5)
                .description("A person is eligible for Home loan?")
                .status("AC")
                .createTimeStamp(new Date())
                .updateTimeStamp(new Date())
                .linkedEntity("IN")
                .build();

        this.rulesList = Lists.newArrayList(businessLogicRule1, businessLogicRule2, businessLogicRule3, businessLogicRule4, businessLogicRule5);
    }

    public List<BLRuleDbModel> findByRuleType(String ruleType) {
        return rulesList.stream()
                .filter(r -> Objects.equals(r.getRuleType(), ruleType))
                .toList();
    }

    public List<BLRuleDbModel> findByLinkedEntityAndRuleType(String linkedEntity, String ruleType) {
        return rulesList.stream()
                .filter(r -> Objects.equals(r.getLinkedEntity(), linkedEntity) && Objects.equals(r.getRuleType(), ruleType))
                .toList();
    }

    public List<BLRuleDbModel> findByLinkedEntity(String linkedEntity) {
        return rulesList.stream()
                .filter(r -> Objects.equals(r.getLinkedEntity(), linkedEntity))
                .toList();
    }

    public BLRuleDbModel save(BLRuleDbModel blRuleDbModel) {

        BLRuleDbModel found = rulesList.stream().filter(r -> Objects.equals(r.getRuleId(), blRuleDbModel.getRuleId()))
                .findFirst()
                .orElse(null);

        if(found != null) {
            rulesList.replaceAll(r -> Objects.equals(r.getRuleId(), blRuleDbModel.getRuleId()) ? blRuleDbModel : r);
        } else {
            rulesList.add(blRuleDbModel);
        }

        return blRuleDbModel;

    }

    public List<BLRuleDbModel> saveAll(List<BLRuleDbModel> listBLRuleDBModel) {

        for (BLRuleDbModel blRuleDbModel : listBLRuleDBModel) {
            BLRuleDbModel found = rulesList.stream().filter(r -> Objects.equals(r.getRuleId(), blRuleDbModel.getRuleId()))
                    .findFirst()
                    .orElse(null);

            if(found != null) {
                rulesList.replaceAll(r -> Objects.equals(r.getRuleId(), blRuleDbModel.getRuleId()) ? blRuleDbModel : r);
            } else {
                rulesList.add(blRuleDbModel);
            }
        }

        return rulesList;
    }

    public List<BLRuleDbModel> findAll() {
        return rulesList;
    }

    public void deleteById(String ruleID) {
        List<BLRuleDbModel> removeRulesList = rulesList.stream()
                .filter(r -> Objects.equals(r.getRuleId(), ruleID))
                .toList();
        rulesList.removeAll(removeRulesList);
    }

    public Optional<BLRuleDbModel> findById(String ruleID) {
        BLRuleDbModel rule = rulesList.stream().filter(r -> Objects.equals(r.getRuleId(), ruleID.toUpperCase()))
                .findFirst()
                .orElse(null);

        return Optional.ofNullable(rule);
    }
}
