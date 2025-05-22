package com.insulinpump.patientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insulinpump.patientservice.model.DiabetesType;
import com.insulinpump.patientservice.model.Patient;
import com.insulinpump.patientservice.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@DisplayName("Patient Controller Tests")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient patient1;
    private Patient patient2;
    private List<Patient> patientList;

    @BeforeEach
    void setup() {
        patient1 = new Patient();
        patient1.setId(1L);
        patient1.setName("Juan Pérez");
        patient1.setAge(35);
        patient1.setMedicalId("MED123");
        patient1.setDiabetesType(DiabetesType.TYPE_1);
        patient1.setEmail("juan@example.com");

        patient2 = new Patient();
        patient2.setId(2L);
        patient2.setName("María García");
        patient2.setAge(42);
        patient2.setMedicalId("MED456");
        patient2.setDiabetesType(DiabetesType.TYPE_2);
        patient2.setEmail("maria@example.com");

        patientList = Arrays.asList(patient1, patient2);
    }

    @Test
    @DisplayName("GET /api/patients - Debería retornar lista de pacientes")
    void whenGetAllPatients_thenReturnJsonArray() throws Exception {
        // Given
        when(patientService.getAllPatients()).thenReturn(patientList);

        // When/Then
        mockMvc.perform(get("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Juan Pérez")))
                .andExpect(jsonPath("$[0].medicalId", is("MED123")))
                .andExpect(jsonPath("$[1].name", is("María García")))
                .andExpect(jsonPath("$[1].medicalId", is("MED456")));

        verify(patientService, times(1)).getAllPatients();
    }

    @Test
    @DisplayName("GET /api/patients/{id} - Debería retornar paciente por ID")
    void whenGetPatientById_thenReturnPatient() throws Exception {
        // Given
        when(patientService.getPatientById(1L)).thenReturn(patient1);

        // When/Then
        mockMvc.perform(get("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Juan Pérez")))
                .andExpect(jsonPath("$.medicalId", is("MED123")))
                .andExpect(jsonPath("$.email", is("juan@example.com")));

        verify(patientService, times(1)).getPatientById(1L);
    }

    @Test
    @DisplayName("POST /api/patients - Debería crear nuevo paciente")
    void whenCreatePatient_thenReturnCreatedPatient() throws Exception {
        // Given
        when(patientService.createPatient(any(Patient.class))).thenReturn(patient1);

        // When/Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Juan Pérez")))
                .andExpect(jsonPath("$.medicalId", is("MED123")));

        verify(patientService, times(1)).createPatient(any(Patient.class));
    }

    @Test
    @DisplayName("PUT /api/patients/{id} - Debería actualizar paciente")
    void whenUpdatePatient_thenReturnUpdatedPatient() throws Exception {
        // Given
        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(patient1);

        // When/Then
        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Juan Pérez")))
                .andExpect(jsonPath("$.medicalId", is("MED123")));

        verify(patientService, times(1)).updatePatient(eq(1L), any(Patient.class));
    }

    @Test
    @DisplayName("DELETE /api/patients/{id} - Debería eliminar paciente")
    void whenDeletePatient_thenReturnNoContent() throws Exception {
        // Given
        doNothing().when(patientService).deletePatient(1L);

        // When/Then
        mockMvc.perform(delete("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1L);
    }

    @Test
    @DisplayName("PUT /api/patients/{patientId}/device/{deviceId} - Debería asignar dispositivo")
    void whenAssignDeviceToPatient_thenReturnUpdatedPatient() throws Exception {
        // Given
        patient1.setDeviceId(100L);
        when(patientService.assignDeviceToPatient(1L, 100L)).thenReturn(patient1);

        // When/Then
        mockMvc.perform(put("/api/patients/1/device/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId", is(100)));

        verify(patientService, times(1)).assignDeviceToPatient(1L, 100L);
    }

    @Test
    @DisplayName("POST /api/patients - Debería retornar error de validación con datos inválidos")
    void whenCreatePatient_withInvalidData_thenReturnBadRequest() throws Exception {
        // Given
        Patient invalidPatient = new Patient();
        invalidPatient.setName(""); // Nombre vacío
        invalidPatient.setAge(-5); // Edad negativa
        invalidPatient.setEmail("invalid-email"); // Email inválido

        // When/Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).createPatient(any(Patient.class));
    }
}
