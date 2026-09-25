package org.example.api;

import org.example.api.dto.IncidentRequest;
import org.example.api.dto.TriageResponse;
import org.example.agent.AgentOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoint for incident triage requests.
 */
@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final AgentOrchestrator agentOrchestrator;

    /**
     * Accepts an incident report and returns the triage result.
     *
     * @param req validated incident request body
     * @return the triage response produced by the agent orchestrator
     */
    @PostMapping("/triage")
    public TriageResponse triage(@Valid @RequestBody IncidentRequest req) {
        return agentOrchestrator.triage(req);
    }
}
