package com.medilabo.diabetesassessment.controller;

import com.medilabo.diabetesassessment.exception.PatientNotFoundException;
import com.medilabo.diabetesassessment.service.DiabetesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DiabetesController.class)
@TestPropertySource(properties = "jwt.secret=TestSecretKeyForUnitTestingPurposesAtLeast256BitsLong!")
class DiabetesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiabetesService diabetesService;

    @MockitoBean
    private com.medilabo.diabetesassessment.security.JwtUtil jwtUtil;

    @Test
    @WithMockUser
    void getDiabetesRisk_ValidPatient_ReturnsOkWithRiskDto() throws Exception {
        when(diabetesService.calculateRisk(1L)).thenReturn("None");

        mockMvc.perform(get("/api/risk/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patId").value(1))
                .andExpect(jsonPath("$.expectedRisks").value("None"));
    }

    @Test
    @WithMockUser
    void getDiabetesRisk_PatientNotFound_ReturnsNotFound() throws Exception {
        when(diabetesService.calculateRisk(99L))
                .thenThrow(new PatientNotFoundException("Patient not found"));

        mockMvc.perform(get("/api/risk/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Patient not found"))
                .andExpect(jsonPath("$.message").value("Patient not found"));
    }

    @Test
    @WithMockUser
    void getDiabetesRisk_ReturnsCorrectRiskLevel_ForEarlyOnset() throws Exception {
        when(diabetesService.calculateRisk(2L)).thenReturn("Early Onset");

        mockMvc.perform(get("/api/risk/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patId").value(2))
                .andExpect(jsonPath("$.expectedRisks").value("Early Onset"));
    }

    @Test
    void getDiabetesRisk_Unauthenticated_ReturnsClientError() throws Exception {
        mockMvc.perform(get("/api/risk/1"))
                .andExpect(status().is4xxClientError());
    }
}
