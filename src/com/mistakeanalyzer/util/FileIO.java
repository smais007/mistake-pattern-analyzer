package com.mistakeanalyzer.util;

import com.mistakeanalyzer.exception.FileOperationException;
import com.mistakeanalyzer.exception.FileOperationException.OperationType;
import com.mistakeanalyzer.model.Mistake;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable utility class for file operations.
 * Handles reading and writing mistake data to/from text files.
 * Uses text-based storage with pipe-delimited format for easy debugging.
 */
public class FileIO {
    
    // Default file path for storing mistakes
    private static final String DEFAULT_FILE_PATH = "mistakes_data.txt";
    
    // Header line for the data file (helps identify valid files)
    private static final String FILE_HEADER = "# Mistake Pattern Analyzer Data File - DO NOT EDIT MANUALLY";
    
    private String filePath;
    
    /**
     * Default constructor using default file path.
     */
    public FileIO() {
        this.filePath = DEFAULT_FILE_PATH;
    }
    
    /**
     * Constructor with custom file path.
     * @param filePath Custom path for data storage
     */
    public FileIO(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * Saves a list of mistakes to the data file.
     * Overwrites existing file content.
     * @param mistakes List of mistakes to save
     * @throws FileOperationException if write operation fails
     */
    public void saveAll(List<Mistake> mistakes) throws FileOperationException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write(FILE_HEADER);
            writer.newLine();
            
            // Write each mistake as a line
            for (Mistake mistake : mistakes) {
                writer.write(mistake.toFileString());
                writer.newLine();
            }
            
        } catch (IOException e) {
            throw new FileOperationException(
                "Failed to save data to file: " + e.getMessage(),
                OperationType.WRITE,
                e
            );
        }
    }
    
    /**
     * Loads all mistakes from the data file.
     * Returns empty list if file doesn't exist (first run).
     * @return List of loaded mistakes
     * @throws FileOperationException if read operation fails or data is corrupted
     */
    public List<Mistake> loadAll() throws FileOperationException {
        List<Mistake> mistakes = new ArrayList<>();
        File file = new File(filePath);
        
        // If file doesn't exist, return empty list (first run scenario)
        if (!file.exists()) {
            return mistakes;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip header and empty lines
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    // Parse each line into a Mistake object
                    Mistake mistake = Mistake.fromFileString(line);
                    mistakes.add(mistake);
                } catch (IllegalArgumentException e) {
                    // Log warning but continue loading other records
                    System.err.println("Warning: Skipping corrupted data at line " + lineNumber + ": " + e.getMessage());
                }
            }
            
        } catch (FileNotFoundException e) {
            // File doesn't exist - return empty list
            return mistakes;
        } catch (IOException e) {
            throw new FileOperationException(
                "Failed to read data from file: " + e.getMessage(),
                OperationType.READ,
                e
            );
        }
        
        return mistakes;
    }
    
    /**
     * Appends a single mistake to the file.
     * Useful for adding new records without rewriting entire file.
     * @param mistake Mistake to append
     * @throws FileOperationException if append operation fails
     */
    public void append(Mistake mistake) throws FileOperationException {
        try {
            // Check if file exists and has header
            File file = new File(filePath);
            boolean needsHeader = !file.exists() || file.length() == 0;
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                if (needsHeader) {
                    writer.write(FILE_HEADER);
                    writer.newLine();
                }
                writer.write(mistake.toFileString());
                writer.newLine();
            }
            
        } catch (IOException e) {
            throw new FileOperationException(
                "Failed to append data to file: " + e.getMessage(),
                OperationType.WRITE,
                e
            );
        }
    }
    
    /**
     * Checks if the data file exists.
     * @return true if file exists, false otherwise
     */
    public boolean dataFileExists() {
        return new File(filePath).exists();
    }
    
    /**
     * Creates a backup of the current data file.
     * @return Path to the backup file
     * @throws FileOperationException if backup creation fails
     */
    public String createBackup() throws FileOperationException {
        String backupPath = filePath + ".backup";
        try {
            Files.copy(
                Paths.get(filePath),
                Paths.get(backupPath),
                StandardCopyOption.REPLACE_EXISTING
            );
            return backupPath;
        } catch (IOException e) {
            throw new FileOperationException(
                "Failed to create backup: " + e.getMessage(),
                OperationType.WRITE,
                e
            );
        }
    }
    
    /**
     * Deletes the data file (useful for testing or reset).
     * @return true if deletion was successful
     */
    public boolean deleteDataFile() {
        return new File(filePath).delete();
    }
    
    /**
     * Gets the current file path.
     * @return File path string
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * Sets a new file path.
     * @param filePath New file path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
