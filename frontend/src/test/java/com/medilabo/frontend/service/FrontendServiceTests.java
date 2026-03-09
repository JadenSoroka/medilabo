package com.medilabo.frontend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;

import com.medilabo.frontend.domain.Patient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FrontendServiceTests {

    private MockWebServer mockWebServer;
    private FrontendService frontendService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        frontendService = new FrontendService(baseUrl);
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getPatientInfoFormatsNameAndParsesResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                        "{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"dateOfBirth\":\"01/01/1970\",\"gender\":\"M\",\"address\":\"Paris\",\"phone\":\"111-222\"}"));

        Patient patient = frontendService.getPatientInfo("John Doe");

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/patients/John_Doe", request.getPath());
        assertNotNull(patient);
        assertEquals(1L, patient.id());
        assertEquals("John", patient.firstName());
        assertEquals("Doe", patient.lastName());
        assertEquals("01/01/1970", patient.dateOfBirth());
        assertEquals(Character.valueOf('M'), patient.gender());
        assertEquals("Paris", patient.address());
        assertEquals("111-222", patient.phone());
    }

    @Test
    void getAllPatientsReturnsList() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                        "[{\"id\":2,\"firstName\":\"Ana\",\"lastName\":\"Trujillo\",\"dateOfBirth\":\"12/12/1980\",\"gender\":\"F\",\"address\":\"London\",\"phone\":\"333-444\"}]"));

        List<Patient> patients = frontendService.getAllPatients();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/patients", request.getPath());
        assertEquals(1, patients.size());
        assertEquals(2L, patients.get(0).id());
    }

    @Test
    void createPatientPostsBodyAndReturnsPatient() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type", "application/json")
                .setBody(
                        "{\"id\":3,\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"dateOfBirth\":\"02/02/1990\",\"gender\":\"F\",\"address\":\"Boston\",\"phone\":\"555-666\"}"));

        Patient requestPatient = new Patient(null, "Jane", "Doe", "02/02/1990", 'F', "Boston", "555-666");
        Patient created = frontendService.createPatient(requestPatient);

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/patients", request.getPath());

        JsonNode payload = objectMapper.readTree(request.getBody().readUtf8());
        assertEquals("Jane", payload.get("firstName").asText());
        assertEquals("Doe", payload.get("lastName").asText());
        assertEquals("02/02/1990", payload.get("dateOfBirth").asText());
        assertEquals("F", payload.get("gender").asText());
        assertEquals("Boston", payload.get("address").asText());
        assertEquals("555-666", payload.get("phone").asText());

        assertNotNull(created);
        assertEquals(3L, created.id());
    }

    @Test
    void updatePatientSendsPutRequest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        Patient requestPatient = new Patient(null, "Sam", "Lee", "03/03/2000", 'O', "Berlin", "777-888");
        frontendService.updatePatient(9L, requestPatient);

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("PUT", request.getMethod());
        assertEquals("/api/patients/9", request.getPath());

        JsonNode payload = objectMapper.readTree(request.getBody().readUtf8());
        assertEquals(true, payload.get("id").isNull());
        assertEquals("Sam", payload.get("firstName").asText());
    }

    @Test
    void deletePatientSendsDeleteRequest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        frontendService.deletePatient(4L);

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("DELETE", request.getMethod());
        assertEquals("/api/patients/4", request.getPath());
    }
}
