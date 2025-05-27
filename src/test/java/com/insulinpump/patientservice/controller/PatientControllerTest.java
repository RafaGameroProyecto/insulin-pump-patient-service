package com.insulinpump.patientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insulinpump.patientservice.exception.GlobalExceptionHandler;
import com.insulinpump.patientservice.exception.PatientNotFoundException;
import com.insulinpump.patientservice.model.DiabetesType;
import com.insulinpump.patientservice.model.Patient;
import com.insulinpump.patientservice.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patient Controller Tests - Standalone")
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(patientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        // Crear datos de test
        setupTestData();
    }

    void setupTestData() {
        patient1 = new Patient();
        patient1.setId(1L);
        patient1.setName("Juan Pérez");
        patient1.setAge(35);
        patient1.setMedicalId("MED123");
        patient1.setDiabetesType(DiabetesType.TYPE_1);
        patient1.setEmail("juan@example.com");
        patient1.setPhone("+34123456789");
        patient1.setWeight(75.5f);
        patient1.setHeight(175.0f);

        patient2 = new Patient();
        patient2.setId(2L);
        patient2.setName("María García");
        patient2.setAge(42);
        patient2.setMedicalId("MED456");
        patient2.setDiabetesType(DiabetesType.TYPE_2);
        patient2.setEmail("maria@example.com");
        patient2.setPhone("+34987654321");
        patient2.setWeight(68.0f);
        patient2.setHeight(162.0f);
    }

    @Test
    @DisplayName("GET /api/patients - Debería retornar lista de pacientes")
    void should_get_all_patients() throws Exception {
        // Given
        List<Patient> patients = Arrays.asList(patient1, patient2);
        when(patientService.getAllPatients()).thenReturn(patients);

        // When & Then
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Juan Pérez"))
                .andExpect(jsonPath("$[0].medicalId").value("MED123"))
                .andExpect(jsonPath("$[1].name").value("María García"))
                .andExpect(jsonPath("$[1].medicalId").value("MED456"));

        verify(patientService, times(1)).getAllPatients();
    }

    @Test
    @DisplayName("GET /api/patients/{id} - Debería retornar paciente por ID")
    void should_get_patient_by_id() throws Exception {
        // Given
        when(patientService.getPatientById(1L)).thenReturn(patient1);

        // When & Then
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.medicalId").value("MED123"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.diabetesType").value("TYPE_1"));

        verify(patientService, times(1)).getPatientById(1L);
    }

    @Test
    @DisplayName("GET /api/patients/{id} - Debería retornar 404 cuando paciente no existe")
    void should_return_404_when_patient_not_found() throws Exception {
        // Given
        when(patientService.getPatientById(999L))
                .thenThrow(new PatientNotFoundException("Paciente no encontrado con ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/patients/999"))
                .andExpect(status().isNotFound());

        verify(patientService, times(1)).getPatientById(999L);
    }

    @Test
    @DisplayName("GET /api/patients/medical/{medicalId} - Debería retornar paciente por ID médico")
    void should_get_patient_by_medical_id() throws Exception {
        // Given
        when(patientService.getPatientByMedicalId("MED123")).thenReturn(patient1);

        // When & Then
        mockMvc.perform(get("/api/patients/medical/MED123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.medicalId").value("MED123"));

        verify(patientService, times(1)).getPatientByMedicalId("MED123");
    }

    @Test
    @DisplayName("GET /api/patients/device/{deviceId} - Debería retornar paciente por ID de dispositivo")
    void should_get_patient_by_device_id() throws Exception {
        // Given
        patient1.setDeviceId(100L);
        when(patientService.getPatientByDeviceId(100L)).thenReturn(patient1);

        // When & Then
        mockMvc.perform(get("/api/patients/device/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(100))
                .andExpect(jsonPath("$.name").value("Juan Pérez"));

        verify(patientService, times(1)).getPatientByDeviceId(100L);
    }

    @Test
    @DisplayName("POST /api/patients - Debería crear nuevo paciente")
    void should_create_patient() throws Exception {
        // Given
        when(patientService.createPatient(any(Patient.class))).thenReturn(patient1);

        // When & Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.medicalId").value("MED123"))
                .andExpect(jsonPath("$.diabetesType").value("TYPE_1"));

        verify(patientService, times(1)).createPatient(any(Patient.class));
    }

    @Test
    @DisplayName("POST /api/patients - Debería retornar error de validación con datos inválidos")
    void should_return_400_for_invalid_patient_data() throws Exception {
        // Given - Crear paciente con datos inválidos
        Patient invalidPatient = new Patient();
        invalidPatient.setName(""); // Nombre vacío (inválido)
        invalidPatient.setAge(-5); // Edad negativa (inválida)
        invalidPatient.setMedicalId(""); // ID médico vacío (inválido)
        invalidPatient.setEmail("invalid-email"); // Email inválido

        // When & Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).createPatient(any(Patient.class));
    }

    @Test
    @DisplayName("PUT /api/patients/{id} - Debería actualizar paciente")
    void should_update_patient() throws Exception {
        // Given
        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setName("Juan Pérez Actualizado");
        updatedPatient.setAge(36);
        updatedPatient.setMedicalId("MED123");
        updatedPatient.setDiabetesType(DiabetesType.TYPE_1);
        updatedPatient.setEmail("juan.updated@example.com");

        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(updatedPatient);

        // When & Then
        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Pérez Actualizado"))
                .andExpect(jsonPath("$.age").value(36))
                .andExpect(jsonPath("$.email").value("juan.updated@example.com"));

        verify(patientService, times(1)).updatePatient(eq(1L), any(Patient.class));
    }

    @Test
    @DisplayName("DELETE /api/patients/{id} - Debería eliminar paciente")
    void should_delete_patient() throws Exception {
        // Given
        doNothing().when(patientService).deletePatient(1L);

        // When & Then
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1L);
    }

    @Test
    @DisplayName("PUT /api/patients/{patientId}/device/{deviceId} - Debería asignar dispositivo")
    void should_assign_device_to_patient() throws Exception {
        // Given
        patient1.setDeviceId(100L);
        when(patientService.assignDeviceToPatient(1L, 100L)).thenReturn(patient1);

        // When & Then
        mockMvc.perform(put("/api/patients/1/device/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(100))
                .andExpect(jsonPath("$.name").value("Juan Pérez"));

        verify(patientService, times(1)).assignDeviceToPatient(1L, 100L);
    }

    @Test
    @DisplayName("POST /api/patients - Debería manejar paciente con datos completos")
    void should_create_patient_with_complete_data() throws Exception {
        // Given
        Patient completePatient = new Patient();
        completePatient.setId(3L);
        completePatient.setName("Carlos López");
        completePatient.setAge(28);
        completePatient.setMedicalId("MED789");
        completePatient.setDiabetesType(DiabetesType.TYPE_1);
        completePatient.setEmail("carlos@example.com");
        completePatient.setPhone("+34555123456");
        completePatient.setWeight(80.0f);
        completePatient.setHeight(180.0f);
        completePatient.setEmergencyContact("Ana López - +34555654321");

        when(patientService.createPatient(any(Patient.class))).thenReturn(completePatient);

        // When & Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completePatient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Carlos López"))
                .andExpect(jsonPath("$.weight").value(80.0))
                .andExpect(jsonPath("$.height").value(180.0))
                .andExpect(jsonPath("$.emergencyContact").value("Ana López - +34555654321"));

        verify(patientService, times(1)).createPatient(any(Patient.class));
    }

    @Test
    @DisplayName("GET /api/patients - Debería manejar lista vacía")
    void should_handle_empty_patient_list() throws Exception {
        // Given
        when(patientService.getAllPatients()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(patientService, times(1)).getAllPatients();
    }

    @Test
    @DisplayName("PUT /api/patients/{id} - Debería manejar actualización con ID no existente")
    void should_handle_update_non_existing_patient() throws Exception {
        // Given
        when(patientService.updatePatient(eq(999L), any(Patient.class)))
                .thenThrow(new PatientNotFoundException("Paciente no encontrado con ID: 999"));

        // When & Then
        mockMvc.perform(put("/api/patients/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient1)))
                .andExpect(status().isNotFound());

        verify(patientService, times(1)).updatePatient(eq(999L), any(Patient.class));
    }
}