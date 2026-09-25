package org.example.tools;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tool that searches local knowledge-base markdown files for relevant snippets.
 */
@Component
@RequiredArgsConstructor
public class KnowledgeBaseTool implements Tool {

    private final ResourceLoader resourceLoader;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() { return "knowledge_base_search"; }

    /**
     * {@inheritDoc}
     */
    @Override
    public String description() {
        return "Searches local KB markdown files for similar errors and returns relevant snippets.";
    }

    /**
     * Runs a naive keyword search against bundled KB markdown files.
     *
     * @param input error text / query used to derive keywords
     * @return matching KB snippets, or a message indicating no matches
     */
    @Override
    public String execute(String input) {
        // naive keyword search for demo (fast + reliable)
        List<String> keywords = Arrays.stream(input.toLowerCase().split("\\W+"))
                .filter(k -> k.length() >= 5)
                .distinct()
                .limit(10)
                .toList();

        try {
            Resource folder = resourceLoader.getResource("classpath:kb/");
            // For demo simplicity, list known files (or keep a small registry)
            List<String> files = List.of("timeouts.md", "db-deadlocks.md", "auth-issues.md","cpu-spikes.md","memory-leaks.md","startup-failures.md");

            List<String> matches = new ArrayList<>();
            for (String f : files) {
                Resource r = resourceLoader.getResource("classpath:kb/" + f);
                String text = new String(r.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

                long score = keywords.stream().filter(text.toLowerCase()::contains).count();
                if (score > 0) {
                    matches.add("FILE=" + f + " score=" + score + "\n" + snippet(text, keywords));
                }
            }

            if (matches.isEmpty()) return "No similar KB entries found.";
            return matches.stream().collect(Collectors.joining("\n\n---\n\n"));
        } catch (Exception e) {
            return "KB search error: " + e.getMessage();
        }
    }

    /**
     * Returns a context window around the first matched keyword.
     *
     * @param text     full KB file text
     * @param keywords keywords to search for
     * @return a substring centered on the first match
     */
    private String snippet(String text, List<String> keywords) {
        String lower = text.toLowerCase();
        int idx = -1;
        for (String k : keywords) {
            idx = lower.indexOf(k);
            if (idx >= 0) break;
        }
        if (idx < 0) return text.substring(0, Math.min(300, text.length()));
        int start = Math.max(0, idx - 120);
        int end = Math.min(text.length(), idx + 220);
        return text.substring(start, end);
    }
}
