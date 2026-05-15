# SmartCalc 🧮

A production-grade, all-in-one calculator Android app built with the latest Android tech stack. SmartCalc bundles everyday calculation and conversion tools into a single clean, fast, and beautiful app.

---

## 📸 Screenshots

> _Add your screenshots here once the app is built_

| Home | Basic Calculator | Date Calculator |
|------|-----------------|-----------------|
| ![Home](screenshots/home.png) | ![Calc](screenshots/calculator.png) | ![Date](screenshots/date.png) |

| Unit Converter | Temperature | BMI Calculator |
|----------------|-------------|----------------|
| ![Unit](screenshots/unit.png) | ![Temp](screenshots/temperature.png) | ![BMI](screenshots/bmi.png) |

| Discount Calculator | Loan Calculator |
|---------------------|-----------------|
| ![Discount](screenshots/discount.png) | ![Loan](screenshots/loan.png) |

---

## ✨ Features

### 🔢 Basic Calculator
- Standard arithmetic — addition, subtraction, multiplication, division
- Square root, percentage, toggle sign
- Expression display showing the full ongoing operation
- Haptic feedback on key press
- Handles edge cases — division by zero, chained operations, long number display

### 📅 Date Calculator
- **Difference mode** — calculate exact days, weeks, months and years between any two dates
- **Add / Subtract mode** — add or subtract years, months and days from any base date
- Native Material 3 date picker dialog
- Instant result update as dates change

### 📏 Unit Converter
- 9 categories — **Length, Weight, Volume, Area, Speed, Data Storage, Pressure, Energy, Time**
- 80+ units total across all categories
- Bottom sheet pickers for category and unit selection
- Swap button to instantly reverse conversion
- Formula hint bar showing the equation inline
- Handles scientific notation for very large/small values

### 🌡️ Temperature Converter
- Celsius, Fahrenheit and Kelvin
- Live conversion as you type
- Per-unit accent colours — blue for °C, orange for °F, purple for K
- Input badge highlights the source unit card

### ⚖️ BMI Calculator
- Metric (kg / cm) and Imperial (lb / ft / in) support
- Animated BMI count-up on result
- Circular arc gauge showing BMI position
- Gradient colour scale bar with animated dot indicator
- Full 8-category WHO classification table with colour coding and health advice
- Active category highlighted with tailored advice text

### 🏷️ Discount Calculator
- **Discount % mode** — enter price and discount %, get final price and savings
- **Find % mode** — enter original and final price, find what discount was applied
- **Reverse mode** — enter final price and discount %, find the original price
- Savings banner with animated highlight
- Clean result breakdown card per mode

### 🏦 Loan / EMI Calculator
- Inputs — loan amount, annual interest rate, tenure in years and months (synced)
- Animated EMI count-up on result
- Summary chips — principal, total interest, total payment in Indian format (L, Cr)
- Animated donut chart split between principal and interest
- Full amortization schedule — month-by-month EMI, principal, interest, balance
- First 6 rows always visible; rest expandable with smooth animation
- Compact Indian number formatting throughout

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| DI | Hilt (Dagger) |
| Navigation | Navigation Compose |
| State | Kotlin StateFlow / collectAsState |
| Animations | Compose Animation APIs + AnimatedContent |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 (Android 15) |
| Build | Gradle with Version Catalog (libs.versions.toml) |

---

## 🏗️ Project Structure

```
app/src/main/java/com/smartcalc/
│
├── MainActivity.kt
├── SmartCalcApp.kt                  # Hilt Application class
│
├── ui/
│   ├── theme/
│   │   └── Theme.kt                 # Colour palette, typography, dark/light schemes
│   ├── navigation/
│   │   ├── NavGraph.kt              # All routes wired with Navigation Compose
│   │   └── PlaceholderScreen.kt     # Coming soon screen for future features
│   ├── home/
│   │   └── HomeScreen.kt            # Dashboard grid with animated tool cards
│   ├── calculator/
│   │   └── BasicCalculatorScreen.kt
│   ├── datecalc/
│   │   └── DateCalculatorScreen.kt
│   ├── unitconverter/
│   │   └── UnitConverterScreen.kt
│   ├── temperature/
│   │   └── TemperatureScreen.kt
│   ├── bmi/
│   │   └── BmiScreen.kt
│   ├── discount/
│   │   └── DiscountScreen.kt
│   └── loan/
│       └── LoanScreen.kt
│
└── viewmodel/
    ├── BasicCalculatorViewModel.kt
    ├── DateCalculatorViewModel.kt
    ├── UnitConverterViewModel.kt
    ├── TemperatureViewModel.kt
    ├── BmiViewModel.kt
    ├── DiscountViewModel.kt
    └── LoanViewModel.kt
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 35

### Clone & Run

```bash
git clone https://github.com/YOUR_USERNAME/SmartCalc.git
cd SmartCalc
```

Open the project in Android Studio and let Gradle sync. Once sync is complete, run the app on an emulator or physical device.

### Gradle Memory (Important)

If you hit a Gradle build failure with a GC thrashing error, make sure your `gradle.properties` file in the root has:

```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
```

This raises the Gradle daemon heap from the default 512 MB to 2 GB, which Kotlin + Compose compilation requires.

---

## 🎨 Design System

The app uses a custom Material 3 colour scheme with an iOS-inspired palette:

| Token | Colour | Usage |
|-------|--------|-------|
| Primary | `#0A84FF` | Buttons, active states, highlights |
| Secondary | `#30D158` | Success, weight, area accents |
| Tertiary | `#FF9F0A` | Warnings, interest, amber accents |
| Error / Danger | `#FF375F` | Error states, discount accent |
| Surface 0 | `#0D0D0F` | True-black background |
| Surface 1 | `#1C1C1E` | Card backgrounds |
| Surface 2 | `#2C2C2E` | Elevated cards, button backgrounds |

Both dark and light themes are fully supported and follow the system setting automatically.

---

## 🗺️ Roadmap

- [x] Basic Calculator
- [x] Date Calculator
- [x] Unit Converter (9 categories, 80+ units)
- [x] Temperature Converter
- [x] BMI Calculator
- [x] Discount Calculator
- [x] Loan / EMI Calculator
- [ ] Tip Calculator
- [ ] Currency Converter (live exchange rates)
- [ ] Scientific Calculator
- [ ] History — save and revisit past calculations
- [ ] Widget support

---

## 🤝 Contributing

Pull requests are welcome. For major changes please open an issue first to discuss what you'd like to change.

1. Fork the repo
2. Create your feature branch — `git checkout -b feature/tip-calculator`
3. Commit your changes — `git commit -m 'Add tip calculator'`
4. Push to the branch — `git push origin feature/tip-calculator`
5. Open a Pull Request

---

## 📄 License

```
MIT License

Copyright (c) 2025 SmartCalc

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

> Built with ❤️ using Jetpack Compose
