package com.mistakeanalyzer.service;

import com.mistakeanalyzer.exception.FileOperationException;
import com.mistakeanalyzer.exception.InvalidMistakeException;
import com.mistakeanalyzer.model.Mistake;
import com.mistakeanalyzer.model.MistakeCategory;
import com.mistakeanalyzer.model.Severity;
import com.mistakeanalyzer.util.FileIO;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling all business logic for Mistake management.
 * Implements CRUD operations with validation and persistence.
 * Acts as a bridge between the UI and data layers.
 */
public class MistakeService {
    
    // File I/O utility for persistence
    private final FileIO fileIO;
    
    // Pattern analyzer for category detection
    private final PatternAnalyzerService patternAnalyzer;
    
    // In-memory list of all mistakes (loaded from file)
    private List<Mistake> mistakes;
    
    /**
     * Constructor initializes services and loads existing data.
     */
    public MistakeService() {
        this.fileIO = new FileIO();
        this.patternAnalyzer = new PatternAnalyzerService();
        this.mistakes = new ArrayList<>();
        
        // Load existing data from file
        loadData();
    }
    
    /**
     * Loads all mistake data from the file into memory.
     * Called during initialization.
     */
    private void loadData() {
        try {
            mistakes = fileIO.loadAll();
            System.out.println("Loaded " + mistakes.size() + " mistakes from file.");
        } catch (FileOperationException e) {
            System.err.println("Warning: Could not load data - " + e.getMessage());
            mistakes = new ArrayList<>();
        }
    }
    
    /**
     * Saves all mistakes to the file.
     * Called after every modification.
     * @throws FileOperationException if save fails
     */
    private void saveData() throws FileOperationException {
        fileIO.saveAll(mistakes);
    }
    
    /**
     * Creates and adds a new mistake.
     * Validates input, auto-detects category, and persists.
     * 
     * @param description Description of the mistake
     * @param severity Severity level
     * @param dateStr Date string in yyyy-MM-dd format
     * @param resolution Optional resolution/lesson learned
     * @return The created Mistake object
     * @throws InvalidMistakeException if validation fails
     * @throws FileOperationException if save fails
     */
    public Mistake addMistake(String description, Severity severity, String dateStr, String resolution)
            throws InvalidMistakeException, FileOperationException {
        
        // Validate inputs
        validateDescription(description);
        LocalDate date = validateAndParseDate(dateStr);
        
        if (severity == null) {
            throw new InvalidMistakeException("Severity must be selected", "severity");
        }
        
        // Generate unique ID
        String id = generateId();
        
        // Auto-detect category based on description
        MistakeCategory category = patternAnalyzer.detectCategory(description);
        
        // Create new mistake
        Mistake mistake = new Mistake(id, description.trim(), category, severity, date, 
                                      resolution != null ? resolution.trim() : "");
        
        // Add to list and persist
        mistakes.add(mistake);
        saveData();
        
        return mistake;
    }
    
    /**
     * Updates an existing mistake.
     * @param id ID of the mistake to update
     * @param description New description (null to keep existing)
     * @param severity New severity (null to keep existing)
     * @param dateStr New date string (null to keep existing)
     * @param resolution New resolution
     * @return Updated Mistake object
     * @throws InvalidMistakeException if validation fails or mistake not found
     * @throws FileOperationException if save fails
     */
    public Mistake updateMistake(String id, String description, Severity severity, 
                                  String dateStr, String resolution)
            throws InvalidMistakeException, FileOperationException {
        
        // Find the mistake
        Mistake mistake = findById(id);
        if (mistake == null) {
            throw new InvalidMistakeException("Mistake not found with ID: " + id, "id");
        }
        
        // Update fields if provided
        if (description != null && !description.trim().isEmpty()) {
            validateDescription(description);
            mistake.setDescription(description.trim());
            // Re-detect category when description changes
            mistake.setCategory(patternAnalyzer.detectCategory(description));
        }
        
        if (severity != null) {
            mistake.setSeverity(severity);
        }
        
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            LocalDate date = validateAndParseDate(dateStr);
            mistake.setDate(date);
        }
        
        // Resolution can be empty (to clear it)
        if (resolution != null) {
            mistake.setResolution(resolution.trim());
        }
        
        // Persist changes
        saveData();
        
        return mistake;
    }
    
    /**
     * Deletes a mistake by ID.
     * @param id ID of the mistake to delete
     * @return true if deleted successfully
     * @throws InvalidMistakeException if mistake not found
     * @throws FileOperationException if save fails
     */
    public boolean deleteMistake(String id) throws InvalidMistakeException, FileOperationException {
        Mistake mistake = findById(id);
        if (mistake == null) {
            throw new InvalidMistakeException("Mistake not found with ID: " + id, "id");
        }
        
        mistakes.remove(mistake);
        saveData();
        
        return true;
    }
    
    /**
     * Gets all mistakes.
     * @return List of all mistakes
     */
    public List<Mistake> getAllMistakes() {
        return new ArrayList<>(mistakes);
    }
    
    /**
     * Finds a mistake by its ID.
     * @param id Mistake ID
     * @return Mistake object or null if not found
     */
    public Mistake findById(String id) {
        if (id == null) return null;
        
        for (Mistake m : mistakes) {
            if (id.equals(m.getId())) {
                return m;
            }
        }
        return null;
    }
    
    /**
     * Gets list of all categories from current mistakes.
     * @return List of MistakeCategory
     */
    public List<MistakeCategory> getAllCategories() {
        return mistakes.stream()
                .map(Mistake::getCategory)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the most frequent mistake category.
     * @return Most frequent MistakeCategory or null
     */
    public MistakeCategory getMostFrequentCategory() {
        return patternAnalyzer.findMostFrequentCategory(getAllCategories());
    }
    
    /**
     * Gets the pattern analysis report.
     * @return Formatted pattern report string
     */
    public String getPatternReport() {
        return patternAnalyzer.generatePatternReport(getAllCategories());
    }
    
    /**
     * Gets the suggestion for a given category.
     * @param category The category
     * @return Suggestion string
     */
    public String getSuggestion(MistakeCategory category) {
        return patternAnalyzer.getSuggestion(category);
    }
    
    /**
     * Gets the pattern analyzer service.
     * @return PatternAnalyzerService instance
     */
    public PatternAnalyzerService getPatternAnalyzer() {
        return patternAnalyzer;
    }
    
    /**
     * Gets the total number of mistakes.
     * @return Count of mistakes
     */
    public int getMistakeCount() {
        return mistakes.size();
    }
    
    // ==================== Validation Methods ====================
    
    /**
     * Validates the description field.
     * @param description Description to validate
     * @throws InvalidMistakeException if validation fails
     */
    private void validateDescription(String description) throws InvalidMistakeException {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidMistakeException("Description cannot be empty", "description");
        }
        if (description.trim().length() < 5) {
            throw new InvalidMistakeException("Description must be at least 5 characters", "description");
        }
        if (description.length() > 500) {
            throw new InvalidMistakeException("Description cannot exceed 500 characters", "description");
        }
    }
    
    /**
     * Validates and parses a date string.
     * @param dateStr Date string in yyyy-MM-dd format
     * @return Parsed LocalDate
     * @throws InvalidMistakeException if date is invalid
     */
    private LocalDate validateAndParseDate(String dateStr) throws InvalidMistakeException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new InvalidMistakeException("Date cannot be empty", "date");
        }
        
        try {
            LocalDate date = LocalDate.parse(dateStr.trim(), Mistake.DATE_FORMATTER);
            
            // Don't allow future dates
            if (date.isAfter(LocalDate.now())) {
                throw new InvalidMistakeException("Date cannot be in the future", "date");
            }
            
            return date;
        } catch (DateTimeParseException e) {
            throw new InvalidMistakeException(
                "Invalid date format. Please use yyyy-MM-dd (e.g., 2024-01-15)",
                "date",
                e
            );
        }
    }
    
    /**
     * Generates a unique ID for a new mistake.
     * Uses UUID but shortened for readability.
     * @return Unique ID string
     */
    private String generateId() {
        return "MST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
