package org.example.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Incoming incident report payload.
 * service, environment, and errorLog are required.
 */
@Data
public class IncidentRequest {
    @NotBlank private String service;
    @NotBlank private String environment; // dev/stage/prod
    @NotBlank private String errorLog;    // pasted logs / stacktrace
}
