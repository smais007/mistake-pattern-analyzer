package com.mistakeanalyzer.exception;

/**
 * Custom exception class for handling invalid mistake input.
 * This exception is thrown when user input validation fails.
 */
public class InvalidMistakeException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    // Field that caused the validation error
    private String fieldName;
    
    /**
     * Constructor with error message only.
     * @param message Error description
     */
    public InvalidMistakeException(String message) {
        super(message);
    }
    
    /**
     * Constructor with error message and field name.
     * @param message Error description
     * @param fieldName Name of the field that caused the error
     */
    public InvalidMistakeException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }
    
    /**
     * Constructor with error message and cause.
     * @param message Error description
     * @param cause Original exception that caused this error
     */
    public InvalidMistakeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor with message, field name, and cause.
     * @param message Error description
     * @param fieldName Name of the field that caused the error
     * @param cause Original exception
     */
    public InvalidMistakeException(String message, String fieldName, Throwable cause) {
        super(message, cause);
        this.fieldName = fieldName;
    }
    
    /**
     * Returns the name of the field that caused the validation error.
     * @return Field name or null if not specified
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * Returns a user-friendly error message.
     * @return Formatted error message
     */
    public String getUserFriendlyMessage() {
        if (fieldName != null && !fieldName.isEmpty()) {
            return String.format("Error in '%s': %s", fieldName, getMessage());
        }
        return getMessage();
    }
}
