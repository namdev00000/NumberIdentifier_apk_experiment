# Number Identifier Android App — Project History & Development Log

## Project Overview

**Project name:** Number Identifier  
**Platform:** Android  
**Language:** Kotlin  
**Build system:** Gradle  
**Source control:** GitHub  
**CI/CD:** GitHub Actions  

This project is being developed with the help of **ChatGPT (GPT-5.6 Luna)** as an AI development assistant. ChatGPT has helped with planning, UI concepts, Kotlin/Android project structure, code generation, build configuration, troubleshooting, GitHub Actions configuration, and iterative improvements.

The development approach is incremental: build a working version, test it, preserve it, then improve it.

---

# 1. Original Idea

The original idea was to create an Android application that accepts a number and identifies mathematical properties or number categories.

The initial concept included:

- Natural Number
- Whole Number
- Integer
- Rational Number
- Irrational Number
- Real Number
- Positive Number
- Negative Number
- Zero
- Even Number
- Odd Number
- Prime Number
- Composite Number

The concept later expanded into a collection of small mathematical tools.

---

# 2. Overall Development Path

```text
Idea
  ↓
Planning
  ↓
Visual Prototype
  ↓
Kotlin Android Project
  ↓
Version 1
  ↓
Version 2
  ↓
Version 3
  ↓
GitHub
  ↓
GitHub Actions
  ↓
Gradle
  ↓
APK
  ↓
Android Phone Testing
  ↓
Future Versions
```

---

# 3. Version 1 — First Working Prototype

## Goal

Create the first functional Android application.

## Main elements

Version 1 introduced:

- Kotlin Android project
- Basic Android UI
- Number input field
- Analyze button
- Number classification logic
- Basic result display
- App logo asset
- Android project/build configuration
- Initial GitHub Actions workflow

## Number classification

The initial logic was designed around categories such as:

```text
Natural Number
Whole Number
Integer
Rational Number
Irrational Number
Real Number
Positive Number
Negative Number
Zero
Even Number
Odd Number
Prime Number
Composite Number
```

### Example

Input:

```text
7
```

Possible results:

```text
Natural      ✓
Whole        ✓
Integer      ✓
Rational     ✓
Irrational   ✗
Real         ✓
Positive     ✓
Negative     ✗
Zero         ✗
Odd          ✓
Prime        ✓
Composite    ✗
```

---

# 4. First GitHub Actions Problem — Gradle Wrapper

The first GitHub Actions build failed with:

```text
./gradlew: No such file or directory
```

The initial workflow tried to execute:

```bash
./gradlew assembleDebug
```

The project did not contain the Gradle Wrapper at that point.

For the experiment, the workflow was changed to install/use a specified Gradle version:

```yaml
- name: Setup Gradle
  uses: gradle/actions/setup-gradle@v6
  with:
    gradle-version: '8.9'

- name: Build APK
  run: gradle assembleDebug
```

This allowed the GitHub runner to invoke Gradle without relying on `./gradlew`.

---

# 5. Second GitHub Actions Problem — Wrong Directory

Another build failed because GitHub Actions was running Gradle from the wrong directory.

The Android project was nested inside the repository.

Example:

```text
Repository
└── NumberIdentifier-Android-Final-v1
    └── NumberIdentifier
        ├── app
        ├── build.gradle.kts
        └── settings.gradle.kts
```

The workflow therefore needed a matching `working-directory`.

The debugging process demonstrated an important rule:

> Always inspect the actual GitHub repository structure before configuring the Gradle working directory.

---

# 6. Version 2 — UI Improvements

## Problem discovered on the real Android phone

After installing the first APK, the UI looked too basic and some text overlapped near the top of the screen.

The main concern was the placement of content relative to the Android system status bar and edge-to-edge behavior.

## Version 2 goals

Version 2 improved:

- Top spacing
- Typography
- App bar
- Input field
- Buttons
- Card layout
- Visual hierarchy
- Overall appearance

The goal was to make the application look more like a polished Android application.

---

# 7. Version 3 — Feature and UI Expansion

Version 3 expanded the application significantly.

## Design requirements

The requested visual direction became:

- Black-and-white color combination
- Kids-friendly interface
- Clear icons
- Large readable controls
- Feature-based navigation
- Individual feature pop-ups/dialogs
- Close button for pop-ups
- Cleaner Android app logo
- Theme control

---

# 8. Theme System

The application was designed to support:

```text
System
Light
Dark
```

### System theme

The System option follows the device's current Android theme.

Therefore the app can operate in:

```text
Light mode
Dark mode
System mode
```

---

# 9. Version 3 — Eight Main Features

## Feature 1 — Even or Odd

The user enters a number.

The application determines whether the number is:

```text
Even
or
Odd
```

The result should be presented clearly.

---

## Feature 2 — Square

The user enters a number.

The application calculates:

```text
n²
```

Example:

```text
5² = 25
```

---

## Feature 3 — Square Root

The application calculates:

```text
√n
```

Example:

```text
√25 = 5
```

The UI should also handle numbers that do not have an integer square root.

---

## Feature 4 — Cube and Cube Root

The application provides:

```text
Cube:
n³

Cube Root:
∛n
```

Example:

```text
3³ = 27
∛27 = 3
```

---

## Feature 5 — Multiplication Table

The user enters a number and receives its table.

Example:

```text
2 × 1 = 2
2 × 2 = 4
2 × 3 = 6
...
2 × 10 = 20
```

---

## Feature 6 — Composite Number

The app first explains the concept.

A composite number is a positive integer greater than 1 that has more than two positive factors.

Example:

```text
12 → Composite
```

The user can then enter a number and check whether it is composite.

---

## Feature 7 — Prime Number

The app first explains the concept.

A prime number is a positive integer greater than 1 with exactly two positive factors:

```text
1
and
itself
```

Example:

```text
7 → Prime
```

The user can then enter a number and check whether it is prime.

---

## Feature 8 — Decimal Number

The app checks whether the entered input represents a decimal value.

Examples:

```text
3.14
2.5
-7.25
```

The exact validation rules can be refined in future versions.

---

# 10. User Interface Design

The requested UI direction is:

```text
Simple
Clear
Kids-friendly
Readable
Interactive
Black-and-white
```

Each feature should visually communicate:

```text
Icon
Feature name
Short explanation
```

Selecting a feature should open a popup/dialog:

```text
Feature card
     ↓
Popup / Dialog
     ↓
Explanation
     ↓
Input
     ↓
Calculate / Check
     ↓
Result
     ↓
Close
```

---

# 11. Logo Improvements

The original generated logo did not render properly in the first application.

The later project used a cleaner Android-friendly logo asset.

Future improvements can include:

- Launcher icon variants
- Proper adaptive icon support
- Different density sizes
- Improved small-size readability

---

# 12. GitHub and APK Workflow

The complete build workflow is:

```text
Kotlin Android project
        ↓
GitHub repository
        ↓
GitHub Actions
        ↓
Set up Java
        ↓
Set up Android SDK
        ↓
Set up Gradle
        ↓
gradle assembleDebug
        ↓
app-debug.apk
        ↓
Upload artifact
        ↓
Download APK
        ↓
Install on Android phone
```

A successful Actions run creates an APK artifact that can be downloaded from the workflow.

---

# 13. GitHub Branching Strategy

To preserve earlier versions and experiment safely, use Git branches and commits.

Recommended structure:

```text
main
 │
 ├── feature/ui
 ├── feature/theme
 ├── feature/prime-composite
 ├── feature/camera
 └── feature/ocr
```

Important principles:

- `main` should contain a stable version.
- New experiments can use separate branches.
- Git commits preserve the history of file changes.
- A previous commit can be inspected or restored.
- A feature branch can be merged into `main` when it is stable.

Possible version tags:

```text
v1.0
v2.0
v3.0
v3.1
v3.2
```

---

# 14. Development Lessons

## Kotlin

Kotlin is the programming language used to write the Android application.

It is not the APK itself.

## Android Studio

Android Studio is a common Android development environment, but it is not required for the GitHub-based APK build workflow used in this experiment.

## Gradle

Gradle is the build system used to compile and package the Android project.

Conceptually:

```text
Android project
      ↓
Gradle
      ↓
APK
```

## GitHub

GitHub stores the source project and its Git history.

## GitHub Actions

GitHub Actions provides automated workflows that can build the project remotely.

---

# 15. Important Build Troubleshooting Lessons

### Missing Gradle Wrapper

Error:

```text
./gradlew: No such file or directory
```

Meaning:

The project did not have the wrapper file required by that command.

Solution used in this experiment:

```text
Use a configured Gradle version through GitHub Actions.
```

### Wrong working directory

Error:

```text
No such file or directory
```

Meaning:

GitHub was trying to execute Gradle inside a directory that did not exist.

Solution:

Inspect the repository and set the correct `working-directory`.

### Old workflow run

When an old workflow run is re-run, it can continue using the workflow definition associated with that run.

When changing the workflow, it is safer to:

```text
Commit the new workflow
       ↓
Run the workflow again from the current branch
```

This ensures the latest committed workflow is used.

---

# 16. Current Project State

The project has progressed through:

```text
✅ Idea
✅ Planning
✅ Visual prototype
✅ Kotlin Android project
✅ Version 1
✅ Version 2 UI improvement
✅ Version 3 feature expansion
✅ GitHub repository
✅ GitHub Actions workflow
✅ Gradle build
✅ Successful APK generation
✅ APK downloaded
✅ APK installed/tested on Android phone
```

The project is now ready for continued development.

---

# 17. Future Roadmap

## V4 — UI refinement

Possible improvements:

- Better animations
- Better icons
- More polished dialogs
- Improved spacing
- Accessibility
- Landscape support
- Tablet support
- Improved onboarding

## V5 — More mathematical tools

Potential additions:

- Number-set classification
- Factors
- Multiples
- GCD
- LCM
- Percentage
- Powers
- Logarithms
- Absolute value
- Fraction simplification
- Prime factorization

## V6 — Mathematical expression input

Support inputs such as:

```text
2 + 5
4 × 8
√25
2³
3/4
```

Workflow:

```text
Expression
   ↓
Parser
   ↓
Calculation
   ↓
Result
```

## V7 — Camera recognition

Potential workflow:

```text
Camera
  ↓
Image
  ↓
OCR / recognition
  ↓
Detected number
  ↓
Mathematical feature
  ↓
Result
```

## V8 — Handwriting recognition

The app could attempt to recognize handwritten numbers.

## V9 — Learning mode

The app could teach the concept before showing the answer.

Example:

```text
What is a prime number?
        ↓
Simple explanation
        ↓
Example
        ↓
Enter a number
        ↓
Answer
        ↓
Why?
```

---

# 18. Recommended Development Method Going Forward

Do not repeatedly delete and replace the entire project.

Instead:

```text
Stable version
      ↓
Create branch
      ↓
Make a small change
      ↓
Commit
      ↓
Build APK
      ↓
Test on phone
      ↓
Fix problems
      ↓
Commit again
      ↓
Merge when stable
```

This preserves the project's history and makes future development much safer.

---

# 19. Example Git Commit Messages

Use descriptive commit messages:

```text
Add even odd feature
Improve dark theme
Fix top app bar overlap
Add prime number dialog
Improve multiplication table
Add square root feature
Update application logo
```

---

# 20. Long-Term Project Record

This Markdown file is intended to be a living project document.

For each future version, add:

- Version number
- New features
- UI changes
- Bugs found
- Bug fixes
- Build problems
- Build solutions
- GitHub changes
- APK release details
- Phone testing results
- Future ideas

That way, the project history remains understandable even after many versions.

---

# 21. Final Summary

The project started from a simple concept:

> Enter a number and identify or calculate something about it.

It developed into an educational mathematics Android application.

The development process now looks like:

```text
Idea
 ↓
AI-assisted planning
 ↓
Prototype
 ↓
Kotlin
 ↓
Android project
 ↓
UI
 ↓
Mathematical logic
 ↓
Testing
 ↓
GitHub
 ↓
GitHub Actions
 ↓
Gradle
 ↓
APK
 ↓
Android smartphone
```

The current application can continue evolving without starting from zero.

**Core principle:**

> Preserve stable versions, develop new features incrementally, test every important APK on the phone, and keep the Git history clean.
