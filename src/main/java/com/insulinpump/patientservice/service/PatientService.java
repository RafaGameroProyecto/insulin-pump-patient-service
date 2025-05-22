package com.insulinpump.patientservice.service;

import com.insulinpump.patientservice.exception.PatientNotFoundException;
import com.insulinpump.patientservice.exception.DuplicateMedicalIdException;
import com.insulinpump.patientservice.model.Patient;
import com.insulinpump.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        log.info("Obteniendo todos los pacientes");
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        log.info("Buscando paciente con ID: {}", id);
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con ID: " + id));
    }

    public Patient getPatientByMedicalId(String medicalId) {
        log.info("Buscando paciente con ID médico: {}", medicalId);
        return patientRepository.findByMedicalId(medicalId)
                .orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con ID médico: " + medicalId));
    }

    @Transactional
    public Patient createPatient(Patient patient) {
        log.info("Creando nuevo paciente con ID médico: {}", patient.getMedicalId());

        if (patientRepository.existsByMedicalId(patient.getMedicalId())) {
            throw new DuplicateMedicalIdException("Ya existe un paciente con el ID médico: " + patient.getMedicalId());
        }

        return patientRepository.save(patient);
    }

    @Transactional
    public Patient updatePatient(Long id, Patient patientDetails) {
        log.info("Actualizando paciente con ID: {}", id);
        Patient patient = getPatientById(id);

        // Verificar si el nuevo medicalId ya existe (si es diferente al actual)
        if (!patient.getMedicalId().equals(patientDetails.getMedicalId()) &&
                patientRepository.existsByMedicalId(patientDetails.getMedicalId())) {
            throw new DuplicateMedicalIdException("Ya existe un paciente con el ID médico: " + patientDetails.getMedicalId());
        }

        patient.setName(patientDetails.getName());
        patient.setAge(patientDetails.getAge());
        patient.setMedicalId(patientDetails.getMedicalId());
        patient.setDeviceId(patientDetails.getDeviceId());
        patient.setDiabetesType(patientDetails.getDiabetesType());
        patient.setEmail(patientDetails.getEmail());
        patient.setPhone(patientDetails.getPhone());
        patient.setWeight(patientDetails.getWeight());
        patient.setHeight(patientDetails.getHeight());
        patient.setEmergencyContact(patientDetails.getEmergencyContact());

        return patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(Long id) {
        log.info("Eliminando paciente con ID: {}", id);
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
    }

    @Transactional
    public Patient assignDeviceToPatient(Long patientId, Long deviceId) {
        log.info("Asignando dispositivo {} al paciente {}", deviceId, patientId);
        Patient patient = getPatientById(patientId);
        patient.setDeviceId(deviceId);
        return patientRepository.save(patient);
    }

    public Patient getPatientByDeviceId(Long deviceId) {
        log.info("Buscando paciente con dispositivo ID: {}", deviceId);
        return patientRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con dispositivo ID: " + deviceId));
    }
}
