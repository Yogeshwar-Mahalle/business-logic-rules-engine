/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.ybm.rulesBusinessSetupRepo.BusinessRulesService;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RuleEngineTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private BusinessRulesService businessRulesServiceMock;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        //when(businessRulesServiceMock.getAllRuleByNamespace(Mockito.any())).thenReturn(getListOfRules());
        when(businessRulesServiceMock.getAllRules()).thenReturn(getListOfRules());
    }

    @Test
    public void verifyGetAllRules() throws Exception {
        mockMvc.perform(get("/get-all-rules")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()
        );
    }

    @Test
    public void verifyPostCarLoanRuleOne() throws Exception {

        String payload = "{\n" +
                "    \"cibilScore\": 800,\n" +
                "    \"firstName\": \"Mark\",\n" +
                "    \"lastName\": \"K\",\n" +
                "    \"age\": \"25\",\n" +
                "    \"accountNumber\": 123456789,\n" +
                "    \"bank\": \"ABC BANK\",\n" +
                "    \"requestedLoanAmount\": 1000000.0,\n" +
                "    \"monthlySalary\": 70000.0\n" +
                "}";


        MvcResult mvcResult = mockMvc.perform(post("/loan")
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk()
                ).andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        String expectedResponseBody = "{\n" +
                "    \"approvalStatus\": true,\n" +
                "    \"interestRate\": 9.0,\n" +
                "    \"processingFees\": 2000,\n" +
                "    \"sanctionedPercentage\": 90,\n" +
                "    \"accountNumber\": 123456789\n" +
                "}";

        assertThat(expectedResponseBody).isEqualToIgnoringWhitespace(actualResponseBody);
    }

    @Test
    public void verifyPostCarLoanRuleThree() throws Exception {

        String payload = "{\n" +
                "    \"cibilScore\": 400,\n" +
                "    \"firstName\": \"Jhone\",\n" +
                "    \"lastName\": \"L\",\n" +
                "    \"age\": \"25\",\n" +
                "    \"accountNumber\": 123456789,\n" +
                "    \"bank\": \"ABC BANK\",\n" +
                "    \"requestedLoanAmount\": 800000.0,\n" +
                "    \"monthlySalary\": 30000.0,\n" +
                "    \"address\": [ \"Tower-2\", \"Airport Road\", \"Yerwada\"],\n" +
                "    \"city\": \"Pune\",\n" +
                "    \"state\" : \"Maharashtra\",\n" +
                "    \"country\": \"India\"\n" +
                "}";

        MvcResult mvcResult = mockMvc.perform(post("/loan")
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk()
                ).andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        String expectedResponseBody = "{\n" +
                "    \"approvalStatus\": true,\n" +
                "    \"interestRate\": 9.0,\n" +
                "    \"address\": \"Tower-2 Airport Road Yerwada Pune Maharashtra India\",\n" +
                "    \"processingFees\": 1000,\n" +
                "    \"sanctionedPercentage\": 75,\n" +
                "    \"accountNumber\": 123456789,\n" +
                "}";

        assertThat(expectedResponseBody).isEqualToIgnoringWhitespace(actualResponseBody);
    }

    private List<BusinessLogicRule> getListOfRules(){

        BusinessLogicRule businessLogicRule1 = BusinessLogicRule.builder()
                .ruleType("LOAN")
                .ruleId("LOAN" + "~" + "1")
                .ruleName("BusinessLogicRule-1")
                .condition("inPayload.monthlySalary >= 50000.0 && inPayload.cibilScore >= 500 && inPayload.requestedLoanAmount<1500000 && $(bank.eligibleAmount) >= 50000.0")
                .action("outPayload.put(\"approvalStatus\",true); outPayload.put(\"interestRate\",$(bank.interestRateRate)); outPayload.put(\"sanctionedPercentage\",90);outPayload.put(\"processingFees\",2000);outPayload.put(\"accountNumber\",inPayload.accountNumber);")
                .priority(1)
                .description("A person is eligible for loan?")
                .status("AC")
                .createTimeStamp(new Date())
                .updateTimeStamp(new Date())
                .linkedEntity("IN")
                .build();
        BusinessLogicRule businessLogicRule2 = BusinessLogicRule.builder()
                .ruleType("LOAN")
                .ruleId("LOAN" + "~" + "2")
                .ruleName("BusinessLogicRule-2")
                .condition("(inPayload.monthlySalary < 50000.0 && inPayload.cibilScore <= 300 && inPayload.requestedLoanAmount >= 1000000) || $(bank.eligibleAmount) < 50000.0")
                .action("outPayload.put(\"approvalStatus\",true); outPayload.put(\"interestRate\",0.0); outPayload.put(\"sanctionedPercentage\",0.0);outPayload.put(\"processingFees\",0);outPayload.put(\"accountNumber\",inPayload.accountNumber);")
                .priority(2)
                .description("A person is eligible for car loan?")
                .status("AC")
                .createTimeStamp(new Date())
                .updateTimeStamp(new Date())
                .linkedEntity("IN")
                .build();
        BusinessLogicRule businessLogicRule3 = BusinessLogicRule.builder()
                .ruleType("LOAN")
                .ruleId("LOAN" + "~" + "3")
                .ruleName("BusinessLogicRule-3")
                .condition("inPayload.monthlySalary >= 20000.0 && inPayload.cibilScore >= 300 && inPayload.cibilScore < 500 && inPayload.requestedLoanAmount <= 1000000 && $(bank.eligibleAmount) >= 50000.0")
                .action("outPayload.put(\"approvalStatus\",true); outPayload.put(\"interestRate\",$(bank.interestRate)); outPayload.put(\"sanctionedPercentage\",70);outPayload.put(\"processingFees\",1000);outPayload.put(\"accountNumber\",inPayload.accountNumber);")
                .priority(3)
                .description("A person is eligible for car loan?")
                .status("AC")
                .createTimeStamp(new Date())
                .updateTimeStamp(new Date())
                .linkedEntity("IN")
                .build();
        BusinessLogicRule businessLogicRule4 = BusinessLogicRule.builder()
                .ruleType("LOAN")
                .ruleId("LOAN" + "~" + "4")
                .ruleName("BusinessLogicRule-4")
                .condition("inPayload.monthlySalary >= 50000 && inPayload.cibilScore >= 800 && inPayload.requestedLoanAmount < 4000000 && $(bank.eligibleAmount) >= 50000.0")
                .action("outPayload.put('approvalStatus',true);outPayload.put('interestRate',$(bank.interestRate));outPayload.put('sanctionedPercentage',90);outPayload.put('accountNumber',inPayload.accountNumber);outPayload.put('processingFees',8000);")
                .priority(4)
                .description("A person is eligible for Home loan?")
                .status("AC")
                .createTimeStamp(new Date())
                .updateTimeStamp(new Date())
                .linkedEntity("IN")
                .build();
        BusinessLogicRule businessLogicRule5 = BusinessLogicRule.builder()
                .ruleType("LOAN")
                .ruleId("LOAN" + "~" + "5")
                .ruleName("BusinessLogicRule-5")
                .condition("inPayload.monthlySalary >= 35000 && inPayload.monthlySalary <= 50000 && inPayload.cibilScore <= 500 && inPayload.requestedLoanAmount < 2000000 && $(bank.eligibleAmount) >= 50000.0")
                .action("outPayload.put(\"approvalStatus\",true);outPayload.put('interestRate',$(bank.interestRate));outPayload.put(\"sanctionedPercentage\",60);outPayload.put('accountNumber',inPayload.accountNumber);outPayload.put(\"processingFees\",2000);")
                .priority(5)
                .description("A person is eligible for Home loan?")
                .status("AC")
                .createTimeStamp(new Date())
                .updateTimeStamp(new Date())
                .linkedEntity("IN")
                .build();

        return Lists.newArrayList(businessLogicRule1, businessLogicRule2, businessLogicRule3, businessLogicRule4, businessLogicRule5);
    }
}
