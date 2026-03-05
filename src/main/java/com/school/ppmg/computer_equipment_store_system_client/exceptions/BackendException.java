package com.school.ppmg.computer_equipment_store_system_client.exceptions;

import java.util.Map;

public class BackendException extends RuntimeException {

    private final Map<String, String> fieldErrors;

    public BackendException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}