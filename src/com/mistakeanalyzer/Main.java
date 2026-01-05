package com.mistakeanalyzer;

import com.mistakeanalyzer.ui.MistakeAnalyzerUI;
import javax.swing.*;

/**
 * Main entry point for the Mistake Pattern Analyzer application.
 * 
 * This application helps users track their mistakes, automatically categorize them,
 * detect recurring patterns, and provide prevention suggestions.
 * 
 * PATTERN DETECTION LOGIC:
 * ========================
 * 
 * 1. CATEGORY DETECTION (Keyword-Based):
 *    When a user enters a mistake description, the system scans for keywords:
 *    
 *    - "late", "delay" → PROCRASTINATION
 *    - "forgot", "rushed" → POOR_PLANNING  
 *    - "assumed", "ignored" → OVERCONFIDENCE
 *    - "distracted" → LACK_OF_FOCUS
 *    - "bug", "error" → TECHNICAL
 *    - "misunderstood" → COMMUNICATION
 *    - No matches → UNKNOWN
 *    
 *    The algorithm counts keyword matches for each category and selects
 *    the one with the most matches.
 * 
 * 2. PATTERN DETECTION (Frequency-Based):
 *    - If same category appears >= 3 times → "Pattern Detected"
 *    - If same category appears >= 5 times → "Critical Pattern"
 *    
 *    This helps identify recurring behavioral issues.
 * 
 * 3. PREVENTION SUGGESTIONS:
 *    Each category has a mapped suggestion:
 *    - PROCRASTINATION → "Use time-boxing and deadlines"
 *    - POOR_PLANNING → "Plan tasks before execution"
 *    - OVERCONFIDENCE → "Add validation checkpoints"
 *    - LACK_OF_FOCUS → "Reduce distractions"
 *    - TECHNICAL → "Improve testing and code review"
 *    - COMMUNICATION → "Clarify requirements early"
 * 
 * FEATURES:
 * - Full CRUD operations for mistakes
 * - File-based persistence (text file storage)
 * - Modern, minimal Swing UI
 * - Auto-category detection
 * - Pattern analysis and visualization
 * - Prevention suggestions
 * 
 * @author Mistake Pattern Analyzer Team
 * @version 1.0
 */
public class Main {
    
    /**
     * Application entry point.
     * Sets up look and feel and launches the UI on the Event Dispatch Thread.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set system look and feel for native appearance
        try {
            // Try to use a modern look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Additional UI tweaks for modern appearance
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            
        } catch (Exception e) {
            // Fall back to default if system L&F not available
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Launch UI on the Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(() -> {
            try {
                MistakeAnalyzerUI ui = new MistakeAnalyzerUI();
                ui.setVisible(true);
                
                System.out.println("Mistake Pattern Analyzer started successfully!");
                System.out.println("Data file: mistakes_data.txt");
                
            } catch (Exception e) {
                // Show error dialog if UI fails to initialize
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
