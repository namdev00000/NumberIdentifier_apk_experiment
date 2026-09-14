# Number Identifier — Development History

## V1 — First working Android prototype

- Kotlin Android project created.
- Basic number input and classification logic.
- Number categories included natural, whole, integer, rational, irrational, real, prime, composite, even, odd and sign.
- First GitHub Actions APK build established.

## V2 — UI improvement

- Improved top spacing and app bar.
- Improved typography, cards, input, buttons and general visual hierarchy.
- Addressed the early status-bar/top-text overlap seen on the physical phone.

## V3 — Feature-based interface

- Added feature cards and a left-side feature menu.
- Added light/dark/system theme selection.
- Added individual popup tools such as even/odd, square, square root, cube/cube root, multiplication table, prime/composite and decimal checks.
- Added cleaner vector app icon.
- GitHub Actions was adjusted to build the nested Android project.

## V4 — Main application structure redesign

V4 changes the app from a collection of small number tools into a broader everyday math utility application.

### Main functions

1. Check the Number
2. Find the Number
3. Calculate Numbers
4. Indian Currency Calculator
5. Unit Conversion
6. Age Calculation
7. Information Chart

### V4 theme and personalization

- Theme control at the top-right.
- Light, Dark and System/Auto modes.
- System/Auto uses local time: 06:00–18:59 light; 19:00–05:59 dark.
- Welcome popup on every app launch.
- Greeting changes with the time of day.
- User can save a name or choose to be asked every time.
- Saved name is displayed in the app heading.
- Sound can be switched on/off from Settings.

### Check the Number

Checks:

- Whole number
- Natural number
- Prime number
- Composite number
- Even number
- Odd number
- Rational number
- Irrational number
- Integer
- Complex number
- Perfect square
- Real square root exists
- Perfect cube
- Real cube root exists

The result is shown in a word-block layout using green YES and red NO markers.

For non-numeric input, the app distinguishes alphabetic text/special-character input instead of pretending it is a number.

Integer input can also show:

- Number of divisors
- List of divisors
- First 10 multiples

### Find the Number

- Square
- Square root
- Cube
- Cube root
- Multiplication table from ×1 to ×10

### Calculator

- Floating calculator button at the bottom-right.
- Popup calculator with basic arithmetic, decimal, percent, clear, delete and equals.

### Indian Currency Calculator

The V4 design includes requested denominations for notes and coins, with a rupee amount breakdown.

- Coins: ₹1, ₹2, ₹5, ₹10, ₹20
- Notes: ₹1, ₹5, ₹10, ₹20, ₹50, ₹100, ₹200, ₹500, ₹2,000

The ₹2,000 note is treated as a special legacy denomination in the UI because RBI withdrew it from circulation while continuing to treat it as legal tender.

### Unit Conversion

Distance:

- Millimeter
- Centimeter
- Meter
- Kilometer
- Inch
- Foot
- Mile

Weight:

- Milligram
- Gram
- Kilogram
- Tonne
- Ounce
- Pound
- Quintal

### Age Calculation

- Date of birth input in DD-MM-YYYY format.
- Calculates years, months and days relative to today's date.

### Information Chart

The information chart provides simple reference explanations for number concepts, units, currency and theme behavior.

## V4 build system

The V4 repository is intentionally arranged as a repo-ready Android project:

```text
app/
build.gradle.kts
settings.gradle.kts
gradle.properties
.github/workflows/build-apk.yml
```

GitHub Actions installs Java, Android SDK support and Gradle 8.9, then runs:

```text
gradle assembleDebug
```

The resulting APK is uploaded as the `NumberIdentifier-V4-APK` artifact.

## Future versions

Possible later additions:

- More unit categories
- Scientific calculator
- Expression parser
- Prime factorization
- GCD/LCM
- Fractions
- Percentage tools
- Camera/OCR input
- Handwriting recognition
- Learning mode
- More accessibility and animation improvements
