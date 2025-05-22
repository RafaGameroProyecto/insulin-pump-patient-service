package com.insulinpump.patientservice.exception;

public class DuplicateMedicalIdException extends RuntimeException {
    public DuplicateMedicalIdException(String message) {
        super(message);
    }
}
