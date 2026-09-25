package org.example.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * Structured output returned by the triage endpoint.
 */
@Data
@Builder
public class TriageResponse {
    private String plan;
    private List<String> toolCalls;
    private String diagnosis;
    private List<String> nextSteps;
    private Long createdTicketId;
}
