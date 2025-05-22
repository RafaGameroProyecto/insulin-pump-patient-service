package com.insulinpump.patientservice.service;


import com.insulinpump.patientservice.exception.DuplicateMedicalIdException;
import com.insulinpump.patientservice.exception.PatientNotFoundException;
import com.insulinpump.patientservice.model.DiabetesType;
import com.insulinpump.patientservice.model.Patient;
import com.insulinpump.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patient Service Tests")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setup() {
        patient1 = new Patient();
        patient1.setId(1L);
        patient1.setName("Juan Pérez");
        patient1.setAge(35);
        patient1.setMedicalId("MED123");
        patient1.setDiabetesType(DiabetesType.TYPE_1);
        patient1.setEmail("juan@example.com");
        patient1.setPhone("+34123456789");

        patient2 = new Patient();
        patient2.setId(2L);
        patient2.setName("María García");
        patient2.setAge(42);
        patient2.setMedicalId("MED456");
        patient2.setDiabetesType(DiabetesType.TYPE_2);
        patient2.setEmail("maria@example.com");
        patient2.setPhone("+34987654321");
    }

    @Test
    @DisplayName("Debería obtener todos los pacientes")
    void whenGetAllPatients_thenReturnPatientsList() {
        // Given
        when(patientRepository.findAll()).thenReturn(Arrays.asList(patient1, patient2));

        // When
        List<Patient> patients = patientService.getAllPatients();

        // Then
        assertEquals(2, patients.size());
        assertTrue(patients.contains(patient1));
        assertTrue(patients.contains(patient2));
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un paciente por ID válido")
    void whenGetPatientById_withValidId_thenReturnPatient() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));

        // When
        Patient found = patientService.getPatientById(1L);

        // Then
        assertNotNull(found);
        assertEquals("Juan Pérez", found.getName());
        assertEquals("MED123", found.getMedicalId());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el paciente no existe")
    void whenGetPatientById_withInvalidId_thenThrowException() {
        // Given
        when(patientRepository.findById(3L)).thenReturn(Optional.empty());

        // When/Then
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () -> {
            patientService.getPatientById(3L);
        });

        assertEquals("Paciente no encontrado con ID: 3", exception.getMessage());
        verify(patientRepository, times(1)).findById(3L);
    }

    @Test
    @DisplayName("Debería crear un paciente exitosamente")
    void whenCreatePatient_thenReturnSavedPatient() {
        // Given
        when(patientRepository.existsByMedicalId("MED123")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        // When
        Patient created = patientService.createPatient(patient1);

        // Then
        assertNotNull(created);
        assertEquals("Juan Pérez", created.getName());
        verify(patientRepository, times(1)).existsByMedicalId("MED123");
        verify(patientRepository, times(1)).save(patient1);
    }

    @Test
    @DisplayName("Debería lanzar excepción al crear paciente con ID médico duplicado")
    void whenCreatePatient_withDuplicateMedicalId_thenThrowException() {
        // Given
        when(patientRepository.existsByMedicalId("MED123")).thenReturn(true);

        // When/Then
        DuplicateMedicalIdException exception = assertThrows(DuplicateMedicalIdException.class, () -> {
            patientService.createPatient(patient1);
        });

        assertEquals("Ya existe un paciente con el ID médico: MED123", exception.getMessage());
        verify(patientRepository, times(1)).existsByMedicalId("MED123");
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Debería actualizar un paciente exitosamente")
    void whenUpdatePatient_withValidId_thenReturnUpdatedPatient() {
        // Given
        Patient updatedPatient = new Patient();
        updatedPatient.setName("Juan Pérez Actualizado");
        updatedPatient.setAge(36);
        updatedPatient.setMedicalId("MED123");
        updatedPatient.setDiabetesType(DiabetesType.TYPE_1);
        updatedPatient.setEmail("juan.updated@example.com");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        // When
        Patient result = patientService.updatePatient(1L, updatedPatient);

        // Then
        assertNotNull(result);
        assertEquals("Juan Pérez Actualizado", result.getName());
        assertEquals("juan.updated@example.com", result.getEmail());
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Debería eliminar un paciente exitosamente")
    void whenDeletePatient_withValidId_thenDeletePatient() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));
        doNothing().when(patientRepository).delete(patient1);

        // When
        patientService.deletePatient(1L);

        // Then
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).delete(patient1);
    }

    @Test
    @DisplayName("Debería asignar dispositivo a paciente exitosamente")
    void whenAssignDeviceToPatient_thenReturnUpdatedPatient() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));
        patient1.setDeviceId(100L);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        // When
        Patient result = patientService.assignDeviceToPatient(1L, 100L);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getDeviceId());
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Debería obtener paciente por ID de dispositivo")
    void whenGetPatientByDeviceId_thenReturnPatient() {
        // Given
        patient1.setDeviceId(100L);
        when(patientRepository.findByDeviceId(100L)).thenReturn(Optional.of(patient1));

        // When
        Patient found = patientService.getPatientByDeviceId(100L);

        // Then
        assertNotNull(found);
        assertEquals(100L, found.getDeviceId());
        assertEquals("Juan Pérez", found.getName());
        verify(patientRepository, times(1)).findByDeviceId(100L);
    }
}

