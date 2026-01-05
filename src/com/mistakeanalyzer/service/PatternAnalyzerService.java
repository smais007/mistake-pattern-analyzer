package com.mistakeanalyzer.service;

import com.mistakeanalyzer.model.MistakeCategory;

import java.util.*;

/**
 * Service class for analyzing patterns in mistakes.
 * Uses rule-based keyword matching for category detection.
 * Identifies frequent patterns and provides prevention suggestions.
 */
public class PatternAnalyzerService {
    
    // Threshold for detecting a pattern (same category appears this many times)
    private static final int PATTERN_THRESHOLD = 3;
    
    // Threshold for critical pattern (same category appears this many times)
    private static final int CRITICAL_PATTERN_THRESHOLD = 5;
    
    // Keyword mappings for each category
    // Each category has a list of keywords that indicate that category
    private static final Map<MistakeCategory, List<String>> CATEGORY_KEYWORDS;
    
    static {
        // Initialize keyword mappings
        CATEGORY_KEYWORDS = new HashMap<>();
        
        // Keywords indicating procrastination
        CATEGORY_KEYWORDS.put(MistakeCategory.PROCRASTINATION, 
            Arrays.asList("late", "delay", "delayed", "postpone", "postponed", 
                         "procrastinate", "procrastinated", "put off", "tomorrow"));
        
        // Keywords indicating poor planning
        CATEGORY_KEYWORDS.put(MistakeCategory.POOR_PLANNING, 
            Arrays.asList("forgot", "forgotten", "rushed", "rush", "hurry", "hurried",
                         "no plan", "unplanned", "last minute", "unprepared"));
        
        // Keywords indicating overconfidence
        CATEGORY_KEYWORDS.put(MistakeCategory.OVERCONFIDENCE, 
            Arrays.asList("assumed", "assume", "ignored", "ignore", "skipped",
                         "skip", "overconfident", "easy", "obvious", "didn't check"));
        
        // Keywords indicating lack of focus
        CATEGORY_KEYWORDS.put(MistakeCategory.LACK_OF_FOCUS, 
            Arrays.asList("distracted", "distraction", "unfocused", "lost focus",
                         "interrupted", "multitask", "multitasking", "sidetracked"));
        
        // Keywords indicating technical issues
        CATEGORY_KEYWORDS.put(MistakeCategory.TECHNICAL, 
            Arrays.asList("bug", "error", "crash", "exception", "code", "syntax",
                         "compile", "runtime", "debug", "fix", "broken", "failed"));
        
        // Keywords indicating communication issues
        CATEGORY_KEYWORDS.put(MistakeCategory.COMMUNICATION, 
            Arrays.asList("misunderstood", "misunderstand", "miscommunication",
                         "unclear", "confused", "wrong requirement", "didn't ask",
                         "should have asked", "misread", "misinterpreted"));
    }
    
    /**
     * Detects the category of a mistake based on its description.
     * Uses keyword matching against predefined keyword lists.
     * 
     * ALGORITHM:
     * 1. Convert description to lowercase for case-insensitive matching
     * 2. Check each category's keywords against the description
     * 3. Count matches for each category
     * 4. Return the category with most matches, or UNKNOWN if no matches
     * 
     * @param description The mistake description text
     * @return Detected MistakeCategory
     */
    public MistakeCategory detectCategory(String description) {
        if (description == null || description.trim().isEmpty()) {
            return MistakeCategory.UNKNOWN;
        }
        
        String lowerDesc = description.toLowerCase();
        Map<MistakeCategory, Integer> matchCounts = new HashMap<>();
        
        // Count keyword matches for each category
        for (Map.Entry<MistakeCategory, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            int count = 0;
            for (String keyword : entry.getValue()) {
                if (lowerDesc.contains(keyword.toLowerCase())) {
                    count++;
                }
            }
            if (count > 0) {
                matchCounts.put(entry.getKey(), count);
            }
        }
        
        // Find category with highest match count
        MistakeCategory bestMatch = MistakeCategory.UNKNOWN;
        int maxCount = 0;
        
        for (Map.Entry<MistakeCategory, Integer> entry : matchCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                bestMatch = entry.getKey();
            }
        }
        
        return bestMatch;
    }
    
    /**
     * Analyzes a list of categories to find the most frequent one.
     * @param categories List of MistakeCategory values
     * @return Most frequent category, or null if list is empty
     */
    public MistakeCategory findMostFrequentCategory(List<MistakeCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        
        // Count occurrences of each category
        Map<MistakeCategory, Integer> frequencyMap = new HashMap<>();
        for (MistakeCategory category : categories) {
            frequencyMap.put(category, frequencyMap.getOrDefault(category, 0) + 1);
        }
        
        // Find the most frequent one
        MistakeCategory mostFrequent = null;
        int maxFrequency = 0;
        
        for (Map.Entry<MistakeCategory, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }
        
        return mostFrequent;
    }
    
    /**
     * Counts occurrences of each category in the given list.
     * @param categories List of categories
     * @return Map of category to count
     */
    public Map<MistakeCategory, Integer> getCategoryFrequencies(List<MistakeCategory> categories) {
        Map<MistakeCategory, Integer> frequencyMap = new HashMap<>();
        
        if (categories == null) {
            return frequencyMap;
        }
        
        for (MistakeCategory category : categories) {
            frequencyMap.put(category, frequencyMap.getOrDefault(category, 0) + 1);
        }
        
        return frequencyMap;
    }
    
    /**
     * Checks if a pattern exists for the given category count.
     * Pattern is detected when same category appears >= PATTERN_THRESHOLD times.
     * @param count Number of occurrences
     * @return true if pattern detected
     */
    public boolean isPatternDetected(int count) {
        return count >= PATTERN_THRESHOLD;
    }
    
    /**
     * Checks if a critical pattern exists for the given category count.
     * Critical pattern when same category appears >= CRITICAL_PATTERN_THRESHOLD times.
     * @param count Number of occurrences
     * @return true if critical pattern detected
     */
    public boolean isCriticalPattern(int count) {
        return count >= CRITICAL_PATTERN_THRESHOLD;
    }
    
    /**
     * Generates a pattern analysis report for display.
     * @param categories List of all mistake categories
     * @return Formatted analysis string
     */
    public String generatePatternReport(List<MistakeCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return "No mistakes recorded yet. Add some to see patterns!";
        }
        
        StringBuilder report = new StringBuilder();
        Map<MistakeCategory, Integer> frequencies = getCategoryFrequencies(categories);
        
        // Find patterns
        List<String> patterns = new ArrayList<>();
        List<String> criticalPatterns = new ArrayList<>();
        
        for (Map.Entry<MistakeCategory, Integer> entry : frequencies.entrySet()) {
            MistakeCategory cat = entry.getKey();
            int count = entry.getValue();
            
            if (isCriticalPattern(count)) {
                criticalPatterns.add(cat.name() + " (" + count + " times)");
            } else if (isPatternDetected(count)) {
                patterns.add(cat.name() + " (" + count + " times)");
            }
        }
        
        // Build report
        if (!criticalPatterns.isEmpty()) {
            report.append("⚠️ CRITICAL PATTERNS:\n");
            for (String pattern : criticalPatterns) {
                report.append("  • ").append(pattern).append("\n");
            }
        }
        
        if (!patterns.isEmpty()) {
            report.append("📊 Detected Patterns:\n");
            for (String pattern : patterns) {
                report.append("  • ").append(pattern).append("\n");
            }
        }
        
        if (criticalPatterns.isEmpty() && patterns.isEmpty()) {
            report.append("✅ No recurring patterns detected yet.");
        }
        
        return report.toString();
    }
    
    /**
     * Gets the prevention suggestion for a category.
     * @param category The mistake category
     * @return Prevention suggestion string
     */
    public String getSuggestion(MistakeCategory category) {
        return category != null ? category.getSuggestion() : "N/A";
    }
    
    /**
     * Gets the pattern detection threshold.
     * @return Pattern threshold value
     */
    public int getPatternThreshold() {
        return PATTERN_THRESHOLD;
    }
    
    /**
     * Gets the critical pattern threshold.
     * @return Critical pattern threshold value
     */
    public int getCriticalPatternThreshold() {
        return CRITICAL_PATTERN_THRESHOLD;
    }
}
