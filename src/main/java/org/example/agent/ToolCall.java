package org.example.agent;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single tool invocation parsed from LLM output.
 *
 * @param toolName the name of the tool to invoke
 * @param input    the input passed to the tool
 */
public record ToolCall(String toolName, String input) {

    /**
     * Parses LLM output for blocks formatted as:
     * <pre>
     * TOOL_CALL: tool_name
     * INPUT: some input
     * </pre>
     *
     * @param txt raw LLM text containing tool calls
     * @return list of parsed tool calls
     */
    public static List<ToolCall> parse(String txt) {
        List<ToolCall> calls = new ArrayList<>();
        String[] lines = txt.split("\n");
        String tool = null, input = null;

        for (String line : lines) {
            if (line.startsWith("TOOL_CALL:")) {
                tool = line.substring("TOOL_CALL:".length()).trim();
                input = null;
            } else if (line.startsWith("INPUT:") && tool != null) {
                input = line.substring("INPUT:".length()).trim();
                calls.add(new ToolCall(tool, input));
                tool = null;
                input = null;
            }
        }
        return calls;
    }
}

