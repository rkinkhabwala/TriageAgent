package org.example.llm;

/**
 * Abstraction for an LLM completion client.
 */
public interface LlmClient {

    /**
     * Sends a prompt to the LLM and returns the generated completion.
     *
     * @param prompt the prompt text
     * @return the LLM response text
     */
    String complete(String prompt);
}
