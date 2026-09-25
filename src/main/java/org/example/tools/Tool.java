package org.example.tools;

/**
 * Contract for agent tools that can be invoked by the orchestrator.
 */
public interface Tool {

    /** @return the unique tool name used in LLM tool calls */
    String name();

    /** @return a human-readable description of what the tool does */
    String description();

    /**
     * Executes the tool with the provided input.
     *
     * @param input the input text from the LLM tool call
     * @return the tool result as a string
     */
    String execute(String input);
}
