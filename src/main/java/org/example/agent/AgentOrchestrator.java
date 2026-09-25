package org.example.agent;

import org.example.api.dto.IncidentRequest;
import org.example.api.dto.TriageResponse;
import org.example.llm.LlmClient;
import org.example.tools.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Orchestrates the agentic triage flow: plan generation,
 * guarded tool execution, synthesis, and ticket creation.
 */
@Service
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final LlmClient llm;
    private final List<Tool> tools; // Spring injects all Tool beans

    /**
     * Runs the full triage pipeline for an incoming incident.
     * Builds a tool catalog, asks the LLM for a plan + tool calls,
     * executes allowed tools (KB search must precede ticket creation),
     * asks the LLM to synthesize a diagnosis, and finally creates a ticket.
     *
     * @param req the incident request
     * @return a structured triage response
     */
    public TriageResponse triage(IncidentRequest req) {
        Map<String, Tool> toolMap = new HashMap<>();
        for (Tool t : tools) toolMap.put(t.name(), t);

        // 1) Ask LLM for a plan + tool calls (simple “function calling” without extra libs)
        String toolCatalog = tools.stream()
                .map(t -> "- " + t.name() + ": " + t.description())
                .reduce("", (a,b) -> a + b + "\n");

        String planningPrompt = """
You are an Incident Triage Agent.
Return:
1) A short PLAN (max 5 bullets)
2) TOOL_CALLS in this exact format (0..N):
   TOOL_CALL: <tool_name>
   INPUT: <one line input>

Available tools:
%s

Incident:
service=%s
environment=%s
error_log=%s
""".formatted(toolCatalog, req.getService(), req.getEnvironment(), req.getErrorLog());

        String planAndCalls = llm.complete(planningPrompt);

        // 2) Execute tool calls
        List<String> executedCalls = new ArrayList<>();
        String kbResult = "";

        for (ToolCall call : ToolCall.parse(planAndCalls)) {
            Tool tool = toolMap.get(call.toolName());
            if (tool == null) continue;

            // Simple governance: only allow create_ticket after KB search happened
            if (call.toolName().equals("create_ticket") && kbResult.isBlank()) {
                executedCalls.add("BLOCKED create_ticket (must search KB first)");
                continue;
            }

            String result = tool.execute(call.input());
            executedCalls.add(call.toolName() + " => " + result);

            if (call.toolName().equals("knowledge_base_search")) {
                kbResult = result;
            }
        }

        // 3) Ask LLM to synthesize final diagnosis + next steps + ticket payload
        String synthesisPrompt = """
You are an Incident Triage Agent.
Use the incident + KB search results to produce:
- DIAGNOSIS (2-4 sentences)
- NEXT_STEPS (3-6 bullets)
- TICKET_PAYLOAD in this exact format:
  service=...
  environment=...
  summary=...
  details=...

Incident:
service=%s
environment=%s
error_log=%s

KB_RESULTS:
%s
""".formatted(req.getService(), req.getEnvironment(), req.getErrorLog(), kbResult);

        String synthesis = llm.complete(synthesisPrompt);

        // 4) Create ticket (final tool call, governed)
        Tool ticketTool = toolMap.get("create_ticket");
        String ticketResult = ticketTool.execute(extractTicketPayload(synthesis));
        Long ticketId = parseId(ticketResult);

        return TriageResponse.builder()
                .plan(extractPlan(planAndCalls))
                .toolCalls(executedCalls)
                .diagnosis(extractSection(synthesis, "DIAGNOSIS"))
                .nextSteps(extractBullets(synthesis, "NEXT_STEPS"))
                .createdTicketId(ticketId)
                .build();
    }

    /**
     * Extracts the ticket payload block from the LLM synthesis output.
     *
     * @param synthesis the raw synthesis text
     * @return the payload text, or an empty string if not found
     */
    private String extractTicketPayload(String synthesis) {
        int i = synthesis.indexOf("TICKET_PAYLOAD");
        if (i < 0) return "";
        return synthesis.substring(i).replace("TICKET_PAYLOAD", "").trim();
    }

    /**
     * Parses the ticket ID from the tool result string.
     *
     * @param ticketResult text such as "Created ticket id=12"
     * @return the parsed ticket ID, or null if not found
     */
    private Long parseId(String ticketResult) {
        // "Created ticket id=12"
        int i = ticketResult.indexOf("id=");
        if (i < 0) return null;
        return Long.parseLong(ticketResult.substring(i + 3).trim());
    }

    /**
     * Extracts the first plan section from the LLM output.
     *
     * @param txt raw LLM text
     * @return up to the first 10 lines as the plan
     */
    private String extractPlan(String txt) {
        // naive: take first 10 lines
        String[] lines = txt.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(lines.length, 10); i++) sb.append(lines[i]).append("\n");
        return sb.toString().trim();
    }

    /**
     * Extracts the text that follows a given header.
     *
     * @param txt    source text
     * @param header header to locate
     * @return the text after the header, or the original text if not found
     */
    private String extractSection(String txt, String header) {
        int i = txt.indexOf(header);
        if (i < 0) return txt;
        return txt.substring(i + header.length()).trim();
    }

    /**
     * Extracts bullet items found after the specified header.
     *
     * @param txt    source text
     * @param header header to locate
     * @return list of cleaned bullet strings
     */
    private List<String> extractBullets(String txt, String header) {
        int i = txt.indexOf(header);
        if (i < 0) return List.of();
        String sub = txt.substring(i);
        return Arrays.stream(sub.split("\n"))
                .filter(l -> l.trim().startsWith("-") || l.trim().startsWith("*"))
                .map(l -> l.replaceFirst("^[\\-*]\\s*", "").trim())
                .filter(s -> !s.isBlank())
                .toList();
    }
}

