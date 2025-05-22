package com.insulinpump.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "La edad es obligatoria")
    @Positive(message = "La edad debe ser un número positivo")
    @Column(nullable = false)
    private Integer age;

    @NotBlank(message = "El ID médico es obligatorio")
    @Column(unique = true, nullable = false)
    private String medicalId;

    private Long deviceId;

    @Enumerated(EnumType.STRING)
    private DiabetesType diabetesType;

    @Email(message = "Debe ser un email válido")
    private String email;

    private String phone;

    // Datos adicionales del paciente
    @Positive(message = "El peso debe ser positivo")
    private Float weight;

    @Positive(message = "La altura debe ser positiva")
    private Float height;

    private String emergencyContact;
}