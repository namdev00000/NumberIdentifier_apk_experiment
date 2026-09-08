# Number Identifier — Android Version 1

A beginner-friendly Kotlin Android application that accepts an integer and identifies the mathematical number sets and basic properties that apply to it.

## Version 1 supports

- Natural numbers (using the convention {1, 2, 3, ...})
- Whole numbers
- Integers
- Rational numbers
- Irrational numbers
- Real numbers
- Positive / Negative / Zero
- Even / Odd
- Prime / Composite

## Important scope

Version 1 accepts **integers only**. Decimal, fraction, root, and camera/OCR support are planned for later versions.

## Project structure

```text
NumberIdentifier/
├── app/
│   ├── src/main/java/com/example/numberidentifier/
│   │   ├── MainActivity.kt
│   │   └── NumberClassifier.kt
│   ├── src/test/java/com/example/numberidentifier/
│   │   └── NumberClassifierTest.kt
│   └── build.gradle.kts
├── .github/workflows/build-apk.yml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Build without Android Studio

This repository is prepared to build on GitHub Actions without Android Studio installed on your computer.

The workflow installs Java 17, Android SDK packages, and Gradle 8.9, runs the unit tests, builds a debug APK, and uploads the APK as a GitHub Actions artifact.

The project intentionally does not include a Gradle Wrapper JAR because this package was prepared without network access to download that binary. The GitHub workflow therefore uses `gradle/actions/setup-gradle` to install Gradle directly.

## APK output

After a successful GitHub Actions run:

`app/build/outputs/apk/debug/app-debug.apk`

You can download the artifact from the workflow run and install it on an Android device.
