# Number Identifier — Version 4

A kid-friendly Android math utility app built in Kotlin.

## V4 main areas

1. Check the Number
2. Find the Number
3. Calculate Numbers
4. Indian Currency Calculator
5. Unit Conversion
6. Age Calculation
7. Information Chart

## V4 UX

- Light / Dark / System Auto theme.
- System Auto uses local time: 06:00–18:59 light and 19:00–05:59 dark.
- Welcome popup on launch with Good morning / Good afternoon / Good evening / Good night.
- Name can be remembered forever or requested every launch.
- Sound can be enabled or disabled in Settings.
- Floating calculator button at the bottom-right.
- Rounded, colorful cards with high-contrast result blocks.
- Number checking uses green YES and red NO markers.

## Build

This repository is configured for GitHub Actions. The workflow installs Java, Android SDK support, Gradle 8.9, builds `assembleDebug`, and uploads the APK artifact.

## Note

The app is intentionally implemented with Android platform views and no external UI dependency so the project stays easy to understand and build.
