# Mistake Pattern Analyzer

A modern Java Swing desktop application for tracking, analyzing, and learning from your mistakes. Built with clean OOP principles, file-based persistence, and a minimal, flat UI design.

![Java](https://img.shields.io/badge/Java-17+-orange)
![Swing](https://img.shields.io/badge/GUI-Swing-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## 📋 Features

- **Full CRUD Operations**: Create, Read, Update, and Delete mistakes
- **Auto-Category Detection**: Keyword-based classification of mistakes
- **Pattern Analysis**: Identifies recurring mistake patterns
- **Prevention Suggestions**: Actionable advice based on detected patterns
- **File Persistence**: Text-based storage (no database required)
- **Modern UI**: Clean, flat, minimal design with proper color scheme

## 🏗️ Project Structure

```
src/com/mistakeanalyzer/
├── Main.java                    # Application entry point
├── model/
│   ├── Mistake.java            # Entity class
│   ├── MistakeCategory.java    # Category enum with suggestions
│   └── Severity.java           # Severity enum
├── service/
│   ├── MistakeService.java     # Business logic & CRUD operations
│   └── PatternAnalyzerService.java  # Pattern detection logic
├── util/
│   └── FileIO.java             # File handling utility
├── exception/
│   ├── InvalidMistakeException.java  # Input validation exception
│   └── FileOperationException.java   # File I/O exception
└── ui/
    └── MistakeAnalyzerUI.java  # Swing UI (single page)
```

## 🚀 How to Run

### Prerequisites

- Java 17 or higher
- No external dependencies required

### Compile and Run

```bash
# Navigate to project directory
cd "Mistake Pattern Analyzer"

# Compile all Java files
javac -d out src/com/mistakeanalyzer/**/*.java src/com/mistakeanalyzer/*.java

# Run the application
java -cp out com.mistakeanalyzer.Main
```

### Alternative (Single Command)

```bash
# Compile
javac -d out -sourcepath src src/com/mistakeanalyzer/Main.java

# Run
java -cp out com.mistakeanalyzer.Main
```

## 📊 Pattern Detection Logic

### 1. Category Detection (Keyword-Based)

When you enter a mistake description, the system scans for keywords:

| Keywords                        | Category        |
| ------------------------------- | --------------- |
| "late", "delay", "postpone"     | PROCRASTINATION |
| "forgot", "rushed", "hurry"     | POOR_PLANNING   |
| "assumed", "ignored", "skipped" | OVERCONFIDENCE  |
| "distracted", "unfocused"       | LACK_OF_FOCUS   |
| "bug", "error", "crash"         | TECHNICAL       |
| "misunderstood", "unclear"      | COMMUNICATION   |
| No matches                      | UNKNOWN         |

**Algorithm:**

1. Convert description to lowercase
2. Count keyword matches for each category
3. Select the category with the highest match count

### 2. Pattern Detection (Frequency-Based)

| Threshold               | Status           |
| ----------------------- | ---------------- |
| Same category ≥ 3 times | Pattern Detected |
| Same category ≥ 5 times | Critical Pattern |

### 3. Prevention Suggestions

Each category maps to an actionable suggestion:

| Category        | Suggestion                        |
| --------------- | --------------------------------- |
| PROCRASTINATION | "Use time-boxing and deadlines"   |
| POOR_PLANNING   | "Plan tasks before execution"     |
| OVERCONFIDENCE  | "Add validation checkpoints"      |
| LACK_OF_FOCUS   | "Reduce distractions"             |
| TECHNICAL       | "Improve testing and code review" |
| COMMUNICATION   | "Clarify requirements early"      |

## 🖥️ UI Layout

```
┌─────────────────────────────────────────────────────────────┐
│  📊 Mistake Pattern Analyzer                                │
│  Track, analyze, and learn from your mistakes               │
├─────────────────────────────────────────────────────────────┤
│  [Description*] [..............................]  [Severity] │
│  [Resolution ] [..............................]  [Date    ] │
│                                    [Clear][Delete][Update][Add] │
├─────────────────────────────────────────────────────────────┤
│  📋 Recorded Mistakes                                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ ID    │ Description │ Category │ Severity │ Date    │   │
│  ├───────┼─────────────┼──────────┼──────────┼─────────┤   │
│  │ MST-1 │ Missed...   │ Procrast │ High     │ 2024... │   │
│  │ MST-2 │ Forgot...   │ Poor Pl. │ Medium   │ 2024... │   │
│  └──────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────┤
│  🔍 Pattern Insights    │    💡 Prevention Suggestion        │
│  ┌──────────────────┐   │    ┌────────────────────────────┐  │
│  │ ⚠️ CRITICAL:     │   │    │ Most Frequent: Procrastin │  │
│  │ PROCRASTINATION  │   │    │ → Use time-boxing and...  │  │
│  └──────────────────┘   │    └────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 💾 Data Storage

Data is stored in `mistakes_data.txt` using pipe-delimited format:

```
# Mistake Pattern Analyzer Data File - DO NOT EDIT MANUALLY
MST-A1B2C3D4|Description text|PROCRASTINATION|HIGH|2024-01-15|Resolution text
```

## 🛡️ Exception Handling

The application handles:

- ✅ File not found (creates new file)
- ✅ Invalid date format
- ✅ Empty description validation
- ✅ Corrupted file data (skips invalid lines)
- ✅ All operations wrapped in try-catch

## 📝 Code Quality

- Clean OOP principles (encapsulation, separation of concerns)
- Meaningful class and method names
- Comprehensive inline comments
- No external dependencies
- Compiles and runs without configuration

## 🎨 UI Design Principles

- **Flat Design**: No gradients or heavy borders
- **Neutral Palette**: Light backgrounds, dark text
- **Consistent Spacing**: Uniform padding and margins
- **Color-Coded Severity**: Visual priority indication
- **Hover Effects**: Interactive button feedback
- **Clean Typography**: Segoe UI font family

## 📄 License

This project is created for educational purposes.

---

**Built with ❤️ using Java Swing**
