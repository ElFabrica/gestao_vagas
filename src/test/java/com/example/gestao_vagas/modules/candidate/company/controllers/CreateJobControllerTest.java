package com.example.gestao_vagas.modules.candidate.company.controllers;

import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.modules.candidate.utils.TestsUtils;
import com.example.gestao_vagas.modules.company.entities.CompanyEntity;
import com.example.gestao_vagas.modules.company.entities.dto.CreateJobDTO;
import com.example.gestao_vagas.modules.company.entities.repositories.CompanyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CreateJobControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CompanyRepository companyRepository;

    @Before
    public void setup(){
        mvc = MockMvcBuilders.
                webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void shouldBeAbleToCreateANewJob(){
    try{
        var company = CompanyEntity.builder()
                .description("COMPANY_DESCRIPTION")
                .email("email@gmail.com")
                .password("123456789@")
                .username("fabrigas")
                .name("arthorias de astora")
                .build();

        company = companyRepository.saveAndFlush(company);

        var createdJobDTO = CreateJobDTO.builder()
                .benefits("BENEFITS_TEST")
                .description("DESCRIPTION_TEST")
                .level("LEVEL_TEST");

        mvc.perform(MockMvcRequestBuilders.post("/company/job/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestsUtils.objectToJSON(createdJobDTO))
                        .header("Authorization", TestsUtils.generateToken(company.getId(), "ElFabrica@1234#"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }catch (Exception e){
        e.printStackTrace();
    }}

    @Test
    public void shouldNotBeAbleToCreateANewJobIfCompanyNotFound(){

            var createdJobDTO = CreateJobDTO.builder()
                    .benefits("BENEFITS_TEST")
                    .description("DESCRIPTION_TEST")
                    .level("LEVEL_TEST")
                    .build();
        try {
        mvc.perform(MockMvcRequestBuilders.post("/company/job/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsUtils.objectToJSON(createdJobDTO))
                .header("Authorization",
                        TestsUtils.generateToken(UUID.randomUUID(), "ElFabrica@1234#")))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
