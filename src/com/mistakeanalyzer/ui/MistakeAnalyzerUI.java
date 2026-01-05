package com.mistakeanalyzer.ui;

import com.mistakeanalyzer.exception.FileOperationException;
import com.mistakeanalyzer.exception.InvalidMistakeException;
import com.mistakeanalyzer.model.Mistake;
import com.mistakeanalyzer.model.MistakeCategory;
import com.mistakeanalyzer.model.Severity;
import com.mistakeanalyzer.service.MistakeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * Main UI class for the Mistake Pattern Analyzer application.
 * Single-page design with modern, minimal, flat UI styling.
 * 
 * Layout:
 * - Top: Input form panel
 * - Center: Table displaying all mistakes
 * - Bottom: Action buttons and pattern analysis panel
 */
public class MistakeAnalyzerUI extends JFrame {
    
    // ==================== Color Scheme (Flat, Modern) ====================
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color PANEL_COLOR = new Color(255, 255, 255);
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);      // Indigo
    private static final Color PRIMARY_HOVER = new Color(67, 56, 202);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);        // Red
    private static final Color DANGER_HOVER = new Color(220, 38, 38);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);       // Green
    private static final Color SUCCESS_HOVER = new Color(22, 163, 74);
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TABLE_HEADER_BG = new Color(249, 250, 251);
    private static final Color TABLE_SELECTION_BG = new Color(238, 242, 255);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    
    // ==================== Fonts ====================
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    // ==================== Services ====================
    private final MistakeService mistakeService;
    
    // ==================== UI Components ====================
    // Input fields
    private JTextArea descriptionField;
    private JComboBox<Severity> severityCombo;
    private JTextField dateField;
    private JTextArea resolutionField;
    
    // Table
    private JTable mistakeTable;
    private DefaultTableModel tableModel;
    
    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    
    // Analysis panel
    private JLabel frequentCategoryLabel;
    private JLabel suggestionLabel;
    private JTextArea patternReportArea;
    
    // Currently selected mistake ID (for update/delete operations)
    private String selectedMistakeId = null;
    
    /**
     * Constructor - sets up the entire UI.
     */
    public MistakeAnalyzerUI() {
        this.mistakeService = new MistakeService();
        initializeUI();
        loadTableData();
        updateAnalysisPanel();
    }
    
    /**
     * Initializes all UI components and layout.
     */
    private void initializeUI() {
        // Frame setup
        setTitle("Mistake Pattern Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create and add components
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Creates the header panel with title and input form.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("📊 Mistake Pattern Analyzer");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Track, analyze, and learn from your mistakes");
        subtitleLabel.setFont(BODY_FONT);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        
        JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(createInputFormPanel(), BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Creates the input form panel for adding/editing mistakes.
     */
    private JPanel createInputFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Description field (larger, multi-line)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Description *"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        descriptionField = createTextArea(3, 40);
        JScrollPane descScroll = new JScrollPane(descriptionField);
        descScroll.setBorder(createFieldBorder());
        descScroll.setPreferredSize(new Dimension(400, 70));
        formPanel.add(descScroll, gbc);
        
        // Severity dropdown
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Severity *"), gbc);
        
        gbc.gridx = 3; gbc.gridy = 0;
        severityCombo = createSeverityComboBox();
        formPanel.add(severityCombo, gbc);
        
        // Date field
        gbc.gridx = 4; gbc.gridy = 0;
        formPanel.add(createLabel("Date *"), gbc);
        
        gbc.gridx = 5; gbc.gridy = 0;
        dateField = createTextField(12);
        dateField.setText(LocalDate.now().format(Mistake.DATE_FORMATTER));
        formPanel.add(dateField, gbc);
        
        // Resolution field (spans second row)
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Resolution"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        resolutionField = createTextArea(2, 40);
        JScrollPane resScroll = new JScrollPane(resolutionField);
        resScroll.setBorder(createFieldBorder());
        resScroll.setPreferredSize(new Dimension(400, 50));
        formPanel.add(resScroll, gbc);
        
        // Buttons on the right
        gbc.gridx = 4; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createFormButtonsPanel(), gbc);
        
        return formPanel;
    }
    
    /**
     * Creates the form action buttons panel.
     */
    private JPanel createFormButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);
        
        addButton = createButton("➕ Add", PRIMARY_COLOR, PRIMARY_HOVER);
        updateButton = createButton("✏️ Update", SUCCESS_COLOR, SUCCESS_HOVER);
        deleteButton = createButton("🗑️ Delete", DANGER_COLOR, DANGER_HOVER);
        clearButton = createButton("↺ Clear", TEXT_SECONDARY, TEXT_PRIMARY);
        
        // Initially disable update/delete until a row is selected
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Add action listeners
        addButton.addActionListener(e -> handleAdd());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
        clearButton.addActionListener(e -> clearForm());
        
        panel.add(clearButton);
        panel.add(deleteButton);
        panel.add(updateButton);
        panel.add(addButton);
        
        return panel;
    }
    
    /**
     * Creates the center panel containing the mistakes table.
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(new EmptyBorder(20, 30, 10, 30));
        
        // Table section label
        JLabel tableLabel = new JLabel("📋 Recorded Mistakes");
        tableLabel.setFont(HEADING_FONT);
        tableLabel.setForeground(TEXT_PRIMARY);
        tableLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Create table
        createMistakeTable();
        JScrollPane tableScroll = new JScrollPane(mistakeTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        tableScroll.getViewport().setBackground(PANEL_COLOR);
        
        centerPanel.add(tableLabel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        
        return centerPanel;
    }
    
    /**
     * Creates and configures the mistakes table.
     */
    private void createMistakeTable() {
        // Table columns
        String[] columns = {"ID", "Description", "Category", "Severity", "Date", "Resolution"};
        
        // Table model (non-editable)
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells non-editable
            }
        };
        
        mistakeTable = new JTable(tableModel);
        mistakeTable.setFont(TABLE_FONT);
        mistakeTable.setRowHeight(40);
        mistakeTable.setShowGrid(false);
        mistakeTable.setIntercellSpacing(new Dimension(0, 0));
        mistakeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mistakeTable.setSelectionBackground(TABLE_SELECTION_BG);
        mistakeTable.setSelectionForeground(TEXT_PRIMARY);
        mistakeTable.setBackground(PANEL_COLOR);
        
        // Column widths
        mistakeTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // ID
        mistakeTable.getColumnModel().getColumn(1).setPreferredWidth(250);  // Description
        mistakeTable.getColumnModel().getColumn(2).setPreferredWidth(130);  // Category
        mistakeTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Severity
        mistakeTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Date
        mistakeTable.getColumnModel().getColumn(5).setPreferredWidth(200);  // Resolution
        
        // Style header
        JTableHeader header = mistakeTable.getTableHeader();
        header.setFont(HEADING_FONT);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        
        // Custom cell renderer for styling
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Alternate row colors
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? PANEL_COLOR : new Color(249, 250, 251));
                }
                
                // Color code severity
                if (column == 3 && value != null) {
                    String severity = value.toString();
                    if (severity.contains("High")) {
                        setForeground(DANGER_COLOR);
                    } else if (severity.contains("Medium")) {
                        setForeground(WARNING_COLOR);
                    } else {
                        setForeground(SUCCESS_COLOR);
                    }
                } else {
                    setForeground(isSelected ? TEXT_PRIMARY : TEXT_SECONDARY);
                }
                
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return c;
            }
        };
        
        // Apply renderer to all columns
        for (int i = 0; i < mistakeTable.getColumnCount(); i++) {
            mistakeTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        
        // Row selection listener - populate form when row selected
        mistakeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
    }
    
    /**
     * Creates the bottom panel with pattern analysis.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(PANEL_COLOR);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));
        
        // Analysis section
        JPanel analysisPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        analysisPanel.setOpaque(false);
        
        // Left: Pattern insights
        JPanel insightsPanel = new JPanel(new BorderLayout(0, 10));
        insightsPanel.setOpaque(false);
        
        JLabel insightsTitle = new JLabel("🔍 Pattern Insights");
        insightsTitle.setFont(HEADING_FONT);
        insightsTitle.setForeground(TEXT_PRIMARY);
        
        patternReportArea = new JTextArea(4, 30);
        patternReportArea.setFont(SMALL_FONT);
        patternReportArea.setForeground(TEXT_SECONDARY);
        patternReportArea.setBackground(new Color(249, 250, 251));
        patternReportArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        patternReportArea.setEditable(false);
        patternReportArea.setLineWrap(true);
        patternReportArea.setWrapStyleWord(true);
        
        JScrollPane reportScroll = new JScrollPane(patternReportArea);
        reportScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        insightsPanel.add(insightsTitle, BorderLayout.NORTH);
        insightsPanel.add(reportScroll, BorderLayout.CENTER);
        
        // Right: Suggestion panel
        JPanel suggestionPanel = new JPanel(new BorderLayout(0, 10));
        suggestionPanel.setOpaque(false);
        
        JLabel suggestionTitle = new JLabel("💡 Prevention Suggestion");
        suggestionTitle.setFont(HEADING_FONT);
        suggestionTitle.setForeground(TEXT_PRIMARY);
        
        JPanel suggestionContent = new JPanel(new GridLayout(2, 1, 0, 10));
        suggestionContent.setOpaque(false);
        
        frequentCategoryLabel = new JLabel("No patterns detected yet");
        frequentCategoryLabel.setFont(BODY_FONT);
        frequentCategoryLabel.setForeground(TEXT_PRIMARY);
        
        suggestionLabel = new JLabel("Add more mistakes to see suggestions");
        suggestionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        suggestionLabel.setForeground(PRIMARY_COLOR);
        
        suggestionContent.add(frequentCategoryLabel);
        suggestionContent.add(suggestionLabel);
        
        // Wrap in a styled panel
        JPanel suggestionBox = new JPanel(new BorderLayout());
        suggestionBox.setBackground(new Color(238, 242, 255));
        suggestionBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(199, 210, 254)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        suggestionBox.add(suggestionContent, BorderLayout.CENTER);
        
        suggestionPanel.add(suggestionTitle, BorderLayout.NORTH);
        suggestionPanel.add(suggestionBox, BorderLayout.CENTER);
        
        analysisPanel.add(insightsPanel);
        analysisPanel.add(suggestionPanel);
        
        // Stats bar at the very bottom
        JPanel statsBar = createStatsBar();
        
        bottomPanel.add(analysisPanel, BorderLayout.CENTER);
        bottomPanel.add(statsBar, BorderLayout.SOUTH);
        
        return bottomPanel;
    }
    
    /**
     * Creates a stats bar showing quick statistics.
     */
    private JPanel createStatsBar() {
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        statsBar.setOpaque(false);
        statsBar.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel statsLabel = new JLabel("📈 Total Mistakes: " + mistakeService.getMistakeCount());
        statsLabel.setFont(SMALL_FONT);
        statsLabel.setForeground(TEXT_SECONDARY);
        
        statsBar.add(statsLabel);
        
        return statsBar;
    }
    
    // ==================== Event Handlers ====================
    
    /**
     * Handles adding a new mistake.
     */
    private void handleAdd() {
        try {
            String description = descriptionField.getText().trim();
            Severity severity = (Severity) severityCombo.getSelectedItem();
            String date = dateField.getText().trim();
            String resolution = resolutionField.getText().trim();
            
            Mistake mistake = mistakeService.addMistake(description, severity, date, resolution);
            
            showSuccess("Mistake added successfully!\nDetected category: " + mistake.getCategory());
            clearForm();
            loadTableData();
            updateAnalysisPanel();
            
        } catch (InvalidMistakeException e) {
            showError(e.getUserFriendlyMessage());
        } catch (FileOperationException e) {
            showError("Failed to save: " + e.getMessage());
        }
    }
    
    /**
     * Handles updating an existing mistake.
     */
    private void handleUpdate() {
        if (selectedMistakeId == null) {
            showError("Please select a mistake to update");
            return;
        }
        
        try {
            String description = descriptionField.getText().trim();
            Severity severity = (Severity) severityCombo.getSelectedItem();
            String date = dateField.getText().trim();
            String resolution = resolutionField.getText().trim();
            
            mistakeService.updateMistake(selectedMistakeId, description, severity, date, resolution);
            
            showSuccess("Mistake updated successfully!");
            clearForm();
            loadTableData();
            updateAnalysisPanel();
            
        } catch (InvalidMistakeException e) {
            showError(e.getUserFriendlyMessage());
        } catch (FileOperationException e) {
            showError("Failed to save: " + e.getMessage());
        }
    }
    
    /**
     * Handles deleting a mistake.
     */
    private void handleDelete() {
        if (selectedMistakeId == null) {
            showError("Please select a mistake to delete");
            return;
        }
        
        // Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this mistake?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            mistakeService.deleteMistake(selectedMistakeId);
            showSuccess("Mistake deleted successfully!");
            clearForm();
            loadTableData();
            updateAnalysisPanel();
            
        } catch (InvalidMistakeException e) {
            showError(e.getUserFriendlyMessage());
        } catch (FileOperationException e) {
            showError("Failed to save: " + e.getMessage());
        }
    }
    
    /**
     * Handles table row selection - populates form with selected data.
     */
    private void handleTableSelection() {
        int selectedRow = mistakeTable.getSelectedRow();
        
        if (selectedRow >= 0) {
            // Get the ID from the first column
            selectedMistakeId = (String) tableModel.getValueAt(selectedRow, 0);
            
            // Find the mistake and populate form
            Mistake mistake = mistakeService.findById(selectedMistakeId);
            if (mistake != null) {
                descriptionField.setText(mistake.getDescription());
                severityCombo.setSelectedItem(mistake.getSeverity());
                dateField.setText(mistake.getFormattedDate());
                resolutionField.setText(mistake.getResolution());
                
                // Enable update/delete buttons
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        } else {
            selectedMistakeId = null;
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }
    
    /**
     * Clears the input form.
     */
    private void clearForm() {
        descriptionField.setText("");
        severityCombo.setSelectedIndex(0);
        dateField.setText(LocalDate.now().format(Mistake.DATE_FORMATTER));
        resolutionField.setText("");
        selectedMistakeId = null;
        mistakeTable.clearSelection();
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    /**
     * Loads data into the table from the service.
     */
    private void loadTableData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load all mistakes
        List<Mistake> mistakes = mistakeService.getAllMistakes();
        
        for (Mistake m : mistakes) {
            tableModel.addRow(new Object[]{
                m.getId(),
                truncateText(m.getDescription(), 50),
                formatCategory(m.getCategory()),
                m.getSeverity().getDisplayName(),
                m.getFormattedDate(),
                truncateText(m.getResolution(), 40)
            });
        }
    }
    
    /**
     * Updates the pattern analysis panel.
     */
    private void updateAnalysisPanel() {
        // Update pattern report
        String report = mistakeService.getPatternReport();
        patternReportArea.setText(report);
        
        // Update frequent category and suggestion
        MistakeCategory frequent = mistakeService.getMostFrequentCategory();
        
        if (frequent != null) {
            frequentCategoryLabel.setText("Most Frequent: " + formatCategory(frequent));
            suggestionLabel.setText("→ " + mistakeService.getSuggestion(frequent));
        } else {
            frequentCategoryLabel.setText("No patterns detected yet");
            suggestionLabel.setText("Add more mistakes to see suggestions");
        }
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Creates a styled label.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Creates a styled text field.
     */
    private JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(BODY_FONT);
        field.setBorder(createFieldBorder());
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
        return field;
    }
    
    /**
     * Creates a styled text area.
     */
    private JTextArea createTextArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setFont(BODY_FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }
    
    /**
     * Creates a styled combo box for severity selection.
     */
    private JComboBox<Severity> createSeverityComboBox() {
        JComboBox<Severity> combo = new JComboBox<>(Severity.values());
        combo.setFont(BODY_FONT);
        combo.setPreferredSize(new Dimension(150, 35));
        combo.setBackground(PANEL_COLOR);
        return combo;
    }
    
    /**
     * Creates a styled button with hover effects.
     */
    private JButton createButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(BODY_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Creates a border for input fields.
     */
    private javax.swing.border.Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(5, 10, 5, 10)
        );
    }
    
    /**
     * Formats category name for display.
     */
    private String formatCategory(MistakeCategory category) {
        if (category == null) return "N/A";
        String name = category.name().replace("_", " ");
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
    
    /**
     * Truncates text for table display.
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Shows a success message dialog.
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Shows an error message dialog.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
