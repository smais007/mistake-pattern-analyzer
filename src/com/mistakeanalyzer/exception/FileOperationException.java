package com.mistakeanalyzer.exception;

/**
 * Custom exception for file operation errors.
 * Handles corrupted data, file not found, and I/O errors.
 */
public class FileOperationException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    // Type of file operation that failed
    private OperationType operationType;
    
    /**
     * Enum for different file operation types
     */
    public enum OperationType {
        READ, WRITE, DELETE, PARSE
    }
    
    /**
     * Constructor with message only.
     * @param message Error description
     */
    public FileOperationException(String message) {
        super(message);
    }
    
    /**
     * Constructor with message and operation type.
     * @param message Error description
     * @param operationType Type of operation that failed
     */
    public FileOperationException(String message, OperationType operationType) {
        super(message);
        this.operationType = operationType;
    }
    
    /**
     * Constructor with message and cause.
     * @param message Error description
     * @param cause Original exception
     */
    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor with message, operation type, and cause.
     * @param message Error description
     * @param operationType Type of operation that failed
     * @param cause Original exception
     */
    public FileOperationException(String message, OperationType operationType, Throwable cause) {
        super(message, cause);
        this.operationType = operationType;
    }
    
    /**
     * Returns the type of operation that failed.
     * @return Operation type or null
     */
    public OperationType getOperationType() {
        return operationType;
    }
}
