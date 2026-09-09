# Number Identifier — Version 3

A kid-friendly, black-and-white Android learning app for exploring numbers.

## Features

1. Even or Odd
2. Square
3. Square Root
4. Cube and Cube Root
5. Multiplication Table (×1 to ×10)
6. Composite Number — explanation + checker
7. Prime Number — explanation + checker
8. Decimal Number — checker

## UI

- Black-and-white visual design
- Large, rounded feature cards
- Left-side feature menu (☰)
- Three-way theme selector: System, Light, Dark
- System theme follows the phone's light/dark appearance
- Each feature opens in a pop-up dialog and can be closed
- App uses a vector launcher icon so the icon remains crisp at different sizes

## Build

The project is a standard Gradle Android project. The GitHub Actions workflow installs Java, Android SDK tooling, and Gradle, then builds a debug APK.

## Theme behavior

- **System:** follows the phone's current light/dark appearance. If the phone is scheduled to switch appearance automatically, the app follows that system schedule.
- **Light:** always uses the light theme.
- **Dark:** always uses the dark theme.

## GitHub Actions

The included workflow is at `.github/workflows/build-apk.yml` and builds from the repository root. It uses Gradle 8.9 and uploads `app-debug.apk` as the `NumberIdentifier-APK` artifact.
