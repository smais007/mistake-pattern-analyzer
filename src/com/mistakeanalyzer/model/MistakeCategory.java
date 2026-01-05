package com.mistakeanalyzer.model;

/**
 * Enum representing different categories of mistakes.
 * Each category has an associated prevention suggestion.
 */
public enum MistakeCategory {
    PROCRASTINATION("Use time-boxing and deadlines"),
    POOR_PLANNING("Plan tasks before execution"),
    OVERCONFIDENCE("Add validation checkpoints"),
    LACK_OF_FOCUS("Reduce distractions"),
    TECHNICAL("Improve testing and code review"),
    COMMUNICATION("Clarify requirements early"),
    UNKNOWN("Review and analyze the situation");

    private final String suggestion;

    MistakeCategory(String suggestion) {
        this.suggestion = suggestion;
    }

    /**
     * Returns the prevention suggestion for this category.
     * @return Prevention suggestion string
     */
    public String getSuggestion() {
        return suggestion;
    }
}
