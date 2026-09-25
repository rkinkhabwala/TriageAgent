package org.example.tools;

import org.example.tickets.Ticket;
import org.example.tickets.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Tool that creates an incident triage ticket in the database.
 */
@Component
@RequiredArgsConstructor
public class TicketTool implements Tool {

    private final TicketRepository repo;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() { return "create_ticket"; }

    /**
     * {@inheritDoc}
     */
    @Override
    public String description() {
        return "Creates an incident triage ticket in the DB. Input should be structured text.";
    }

    /**
     * Parses the structured input, builds a Ticket entity, and persists it.
     *
     * @param input structured text containing service=..., environment=..., summary=..., details=...
     * @return a confirmation message with the created ticket ID
     */
    @Override
    public String execute(String input) {
        // Very simple parsing for demo purposes
        // Expect lines: service=..., environment=..., summary=..., details=...
        String service = pick(input, "service=");
        String env = pick(input, "environment=");
        String summary = pick(input, "summary=");
        String details = pick(input, "details=");

        Ticket t = repo.save(Ticket.builder()
                .service(service)
                .environment(env)
                .summary(summary)
                .details(details)
                .build());

        return "Created ticket id=" + t.getId();
    }

    /**
     * Extracts the value following a key from a multi-line string.
     *
     * @param input source text
     * @param key   key to search for, including the trailing "="
     * @return the value, or an empty string if the key is missing
     */
    private String pick(String input, String key) {
        int i = input.indexOf(key);
        if (i < 0) return "";
        int start = i + key.length();
        int end = input.indexOf("\n", start);
        if (end < 0) end = input.length();
        return input.substring(start, end).trim();
    }
}
