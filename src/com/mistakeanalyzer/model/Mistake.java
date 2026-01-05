package com.mistakeanalyzer.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Entity class representing a Mistake.
 * Implements Serializable for file-based persistence.
 */
public class Mistake implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Date format used for parsing and formatting dates
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private String id;
    private String description;
    private MistakeCategory category;
    private Severity severity;
    private LocalDate date;
    private String resolution;

    /**
     * Default constructor
     */
    public Mistake() {
    }

    /**
     * Full constructor for creating a Mistake instance.
     * @param id Unique identifier
     * @param description Description of the mistake
     * @param category Category of the mistake (auto-detected or manual)
     * @param severity Severity level
     * @param date Date when the mistake occurred
     * @param resolution Resolution or lesson learned
     */
    public Mistake(String id, String description, MistakeCategory category, 
                   Severity severity, LocalDate date, String resolution) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.severity = severity;
        this.date = date;
        this.resolution = resolution;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MistakeCategory getCategory() {
        return category;
    }

    public void setCategory(MistakeCategory category) {
        this.category = category;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    /**
     * Returns formatted date string.
     * @return Date as formatted string
     */
    public String getFormattedDate() {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * Converts the Mistake to a delimited string for file storage.
     * @return Pipe-delimited string representation
     */
    public String toFileString() {
        return String.join("|",
                id,
                description.replace("|", "\\|"),  // Escape delimiter in description
                category.name(),
                severity.name(),
                getFormattedDate(),
                resolution != null ? resolution.replace("|", "\\|") : ""
        );
    }

    /**
     * Creates a Mistake object from a delimited file string.
     * @param line Pipe-delimited string from file
     * @return Mistake object
     * @throws IllegalArgumentException if line format is invalid
     */
    public static Mistake fromFileString(String line) throws IllegalArgumentException {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty line cannot be parsed");
        }
        
        String[] parts = line.split("\\|", -1);  // -1 to include trailing empty strings
        
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid data format: expected at least 5 fields");
        }

        Mistake mistake = new Mistake();
        mistake.setId(parts[0]);
        mistake.setDescription(parts[1].replace("\\|", "|"));  // Unescape delimiter
        mistake.setCategory(MistakeCategory.valueOf(parts[2]));
        mistake.setSeverity(Severity.valueOf(parts[3]));
        mistake.setDate(LocalDate.parse(parts[4], DATE_FORMATTER));
        mistake.setResolution(parts.length > 5 ? parts[5].replace("\\|", "|") : "");
        
        return mistake;
    }

    @Override
    public String toString() {
        return "Mistake{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", severity=" + severity +
                ", date=" + date +
                ", resolution='" + resolution + '\'' +
                '}';
    }
}
