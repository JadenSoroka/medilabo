package com.medilabo.frontend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;

import com.medilabo.frontend.domain.Note;
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

    private MockWebServer patientMockServer;
    private MockWebServer notesMockServer;
    private MockWebServer diabetesRiskMockServer;
    private FrontendService frontendService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        patientMockServer = new MockWebServer();
        patientMockServer.start();
        notesMockServer = new MockWebServer();
        notesMockServer.start();
        diabetesRiskMockServer = new MockWebServer();
        diabetesRiskMockServer.start();

        String patientBaseUrl = patientMockServer.url("/").toString();
        if (patientBaseUrl.endsWith("/")) {
            patientBaseUrl = patientBaseUrl.substring(0, patientBaseUrl.length() - 1);
        }
        String notesBaseUrl = notesMockServer.url("/").toString();
        if (notesBaseUrl.endsWith("/")) {
            notesBaseUrl = notesBaseUrl.substring(0, notesBaseUrl.length() - 1);
        }

        String diabetesRiskBaseUrl = diabetesRiskMockServer.url("/").toString();
        if (diabetesRiskBaseUrl.endsWith("/")) {
            diabetesRiskBaseUrl = diabetesRiskBaseUrl.substring(0, diabetesRiskBaseUrl.length() - 1);
        }

        frontendService = new FrontendService(patientBaseUrl, notesBaseUrl, diabetesRiskBaseUrl);
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        patientMockServer.shutdown();
        notesMockServer.shutdown();
        diabetesRiskMockServer.shutdown();
    }

    @Test
    void getPatientInfoFormatsNameAndParsesResponse() throws Exception {
        patientMockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                        "{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"dateOfBirth\":\"01/01/1970\",\"gender\":\"M\",\"address\":\"Paris\",\"phone\":\"111-222\"}"));

        Patient patient = frontendService.getPatientInfo("John Doe");

        RecordedRequest request = patientMockServer.takeRequest();
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
        patientMockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                        "[{\"id\":2,\"firstName\":\"Ana\",\"lastName\":\"Trujillo\",\"dateOfBirth\":\"12/12/1980\",\"gender\":\"F\",\"address\":\"London\",\"phone\":\"333-444\"}]"));

        List<Patient> patients = frontendService.getAllPatients();

        RecordedRequest request = patientMockServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/patients", request.getPath());
        assertEquals(1, patients.size());
        assertEquals(2L, patients.get(0).id());
    }

    @Test
    void getPatientNotesReturnsList() throws Exception {
        notesMockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                        "[{\"noteId\":\"abc1\",\"patId\":5,\"patient\":\"John Doe\",\"note\":\"Feeling well\"}]"));

        List<Note> notes = frontendService.getPatientNotes(5L);

        RecordedRequest request = notesMockServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/notes/5", request.getPath());
        assertEquals(1, notes.size());
        assertEquals("abc1", notes.get(0).noteId());
        assertEquals(5L, notes.get(0).patId());
        assertEquals("Feeling well", notes.get(0).note());
    }

    @Test
    void createPatientPostsBodyAndReturnsPatient() throws Exception {
        patientMockServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type", "application/json")
                .setBody(
                        "{\"id\":3,\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"dateOfBirth\":\"02/02/1990\",\"gender\":\"F\",\"address\":\"Boston\",\"phone\":\"555-666\"}"));

        Patient requestPatient = new Patient(null, "Jane", "Doe", "02/02/1990", 'F', "Boston", "555-666");
        Patient created = frontendService.createPatient(requestPatient);

        RecordedRequest request = patientMockServer.takeRequest();
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
    void createNotePostsBodyToNotesService() throws Exception {
        notesMockServer.enqueue(new MockResponse().setResponseCode(201));

        Note note = new Note(null, 7L, "Jane Doe", "Patient reported headache");
        frontendService.createNote(note);

        RecordedRequest request = notesMockServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/notes", request.getPath());

        JsonNode payload = objectMapper.readTree(request.getBody().readUtf8());
        assertEquals(7, payload.get("patId").asLong());
        assertEquals("Jane Doe", payload.get("patient").asText());
        assertEquals("Patient reported headache", payload.get("note").asText());
    }

    @Test
    void updateNoteSendsPutRequest() throws Exception {
        notesMockServer.enqueue(new MockResponse().setResponseCode(200));

        Note note = new Note("abc1", 7L, "Jane Doe", "Updated note content");
        frontendService.updateNote("abc1", note);

        RecordedRequest request = notesMockServer.takeRequest();
        assertEquals("PUT", request.getMethod());
        assertEquals("/api/notes/abc1", request.getPath());

        JsonNode payload = objectMapper.readTree(request.getBody().readUtf8());
        assertEquals("Updated note content", payload.get("note").asText());
    }

    @Test
    void deleteNoteSendsDeleteRequest() throws Exception {
        notesMockServer.enqueue(new MockResponse().setResponseCode(200));

        frontendService.deleteNote("abc1");

        RecordedRequest request = notesMockServer.takeRequest();
        assertEquals("DELETE", request.getMethod());
        assertEquals("/api/notes/abc1", request.getPath());
    }

    @Test
    void updatePatientSendsPutRequest() throws Exception {
        patientMockServer.enqueue(new MockResponse().setResponseCode(200));

        Patient requestPatient = new Patient(null, "Sam", "Lee", "03/03/2000", 'O', "Berlin", "777-888");
        frontendService.updatePatient(9L, requestPatient);

        RecordedRequest request = patientMockServer.takeRequest();
        assertEquals("PUT", request.getMethod());
        assertEquals("/api/patients/9", request.getPath());

        JsonNode payload = objectMapper.readTree(request.getBody().readUtf8());
        assertEquals(true, payload.get("id").isNull());
        assertEquals("Sam", payload.get("firstName").asText());
    }

    @Test
    void deletePatientSendsDeleteRequest() throws Exception {
        patientMockServer.enqueue(new MockResponse().setResponseCode(200));

        frontendService.deletePatient(4L);

        RecordedRequest request = patientMockServer.takeRequest();
        assertEquals("DELETE", request.getMethod());
        assertEquals("/api/patients/4", request.getPath());
    }
}
