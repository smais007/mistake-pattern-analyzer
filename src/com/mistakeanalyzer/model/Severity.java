package com.mistakeanalyzer.model;

/**
 * Enum representing the severity level of a mistake.
 */
public enum Severity {
    LOW("Low Priority"),
    MEDIUM("Medium Priority"),
    HIGH("High Priority");

    private final String displayName;

    Severity(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns a human-readable display name.
     * @return Display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
