package com.insulinpump.patientservice.repository;

import com.insulinpump.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByMedicalId(String medicalId);

    List<Patient> findByDeviceIdIsNotNull();

    Optional<Patient> findByDeviceId(Long deviceId);

    @Query("SELECT p FROM Patient p WHERE p.diabetesType = :diabetesType")
    List<Patient> findByDiabetesType(String diabetesType);

    boolean existsByMedicalId(String medicalId);
}
