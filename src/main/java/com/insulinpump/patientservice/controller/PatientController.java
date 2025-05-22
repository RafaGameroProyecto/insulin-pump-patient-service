package com.insulinpump.patientservice.controller;

import com.insulinpump.patientservice.model.Patient;
import com.insulinpump.patientservice.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        log.info("GET /api/patients - Obteniendo todos los pacientes");
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        log.info("GET /api/patients/{} - Obteniendo paciente por ID", id);
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/medical-id/{medicalId}")
    public ResponseEntity<Patient> getPatientByMedicalId(@PathVariable String medicalId) {
        log.info("GET /api/patients/medical-id/{} - Obteniendo paciente por ID médico", medicalId);
        return ResponseEntity.ok(patientService.getPatientByMedicalId(medicalId));
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        log.info("POST /api/patients - Creando nuevo paciente");
        return new ResponseEntity<>(patientService.createPatient(patient), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        log.info("PUT /api/patients/{} - Actualizando paciente", id);
        return ResponseEntity.ok(patientService.updatePatient(id, patient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        log.info("DELETE /api/patients/{} - Eliminando paciente", id);
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{patientId}/device/{deviceId}")
    public ResponseEntity<Patient> assignDeviceToPatient(
            @PathVariable Long patientId,
            @PathVariable Long deviceId) {
        log.info("PUT /api/patients/{}/device/{} - Asignando dispositivo a paciente", patientId, deviceId);
        return ResponseEntity.ok(patientService.assignDeviceToPatient(patientId, deviceId));
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Patient> getPatientByDeviceId(@PathVariable Long deviceId) {
        log.info("GET /api/patients/device/{} - Obteniendo paciente por dispositivo", deviceId);
        return ResponseEntity.ok(patientService.getPatientByDeviceId(deviceId));
    }
}