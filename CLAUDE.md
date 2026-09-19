# WealthBuilder Design System Rules

This document provides comprehensive guidance for integrating Figma designs into the WealthBuilder codebase using the Model Context Protocol (MCP).

---

## Project Overview

**WealthBuilder** is a Kotlin Multiplatform (KMP) application built with Compose Multiplatform, supporting Android and iOS. The app focuses on personal finance tracking, habit building, and trading journal functionality with a dark, premium aesthetic.

**Target Platforms:**
- Android (minSdk: 24, targetSdk: 35)
- iOS (Arm64, Simulator)

**Build System:** Gradle with Kotlin DSL

---

## 1. Design System Structure

### 1.1 Token Definitions

#### Color Tokens
**Location:** `composeApp/src/commonMain/kotlin/co/za/xdcodez/wealthbuilder/theme/Colors.kt`

```kotlin
// Primary brand colors
val primary = Color(0xFF1E88E5)  // Blue accent
val surface = Color(0xFF121212)   // Card/surface backgrounds
val background = Color(0xFF222222) // Screen background

// Additional semantic colors used throughout
val accentGold = Color(0xFFD4AF37)  // Selection/highlight color
```

#### Gradient System
**Location:** `composeApp/src/commonMain/kotlin/co/za/xdcodez/wealthbuilder/theme/LevelUpTheme.kt`

The app uses a sophisticated gradient background system with multiple options:

```kotlin
// Default background gradient (currently active: gradientOption0)
Brush.linearGradient(
    colors = listOf(
        Color(0xff000000),
        Color(0xff1B1B1B),
        Color(0xff495057),
        Color(0xff363636),
        Color(0xff363636),
        Color(0xff1B1B1B),
        Color(0xff000000),
    ),
    start = Offset.Infinite.copy(x = Float.POSITIVE_INFINITY, y = 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY)
)
```

**Available gradient options:**
- `gradientOption0()` - Default dark gradient with gray tones
- `gradientOption1()` - Subtle blue tint for atmospheric effect
- `gradientOption2()` - Primary accent highlights
- `gradientOption3()` - Deep blue gradient
- `gradientOption4()` - Minimal blue hint

#### Alpha/Opacity Tokens
```kotlin
// Common opacity values used throughout the app
Color.White.copy(alpha = 0.12f)  // Card backgrounds (lighter)
Color.White.copy(alpha = 0.05f)  // Card backgrounds (darker)
Color.White.copy(alpha = 0.08f)  // Input field backgrounds
Color.White.copy(alpha = 0.3f)   // Border highlights
Color.White.copy(alpha = 0.1f)   // Border subtle
Color(0x99FFFFFF)                // Secondary text (60% opacity)
Color(0x44FFFFFF)                // Placeholder text (27% opacity)
Color(0x33FFFFFF)                // Track/inactive elements (20% opacity)
Color.Gray.copy(alpha = 0.5f)    // Bottom navigation background
```

#### Spacing Tokens
Standard Material 3 spacing with common values:
```kotlin
2.dp   // Minimal spacing
4.dp   // Tight spacing
6.dp   // Progress bar height
8.dp   // Small spacing, corner radius
10.dp  // Medium spacing
12.dp  // Card padding, standard spacing
16.dp  // Large spacing, bottom nav item padding
18.dp  // Bottom navigation bottom padding
```

#### Shape Tokens
```kotlin
RoundedCornerShape(12.dp)  // Card corners (standard)
RoundedCornerShape(8.dp)   // Small components, input fields, nav bar
RoundedCornerShape(16.dp)  // Bottom nav items
```

### 1.2 Typography Tokens

Using Material 3 typography system:

```kotlin
// Common typography styles used
MaterialTheme.typography.headlineSmall    // App bar titles
MaterialTheme.typography.titleLarge       // Major headings
MaterialTheme.typography.titleMedium      // Section headers, card titles
MaterialTheme.typography.bodyMedium       // Body text
MaterialTheme.typography.labelLarge       // Emphasized labels
MaterialTheme.typography.labelMedium      // Small labels, bottom nav text
```

**Custom text styles:**
```kotlin
// Input field text
TextStyle(
    color = Color.White,
    fontSize = 15.sp,
    lineHeight = 15.sp
)
```

---

## 2. Component Library

### 2.1 Component Locations

**Common Widgets:** `composeApp/src/commonMain/kotlin/co/za/xdcodez/wealthbuilder/common/widgets/`

**Feature-Specific Components:**
- Finance: `composeApp/src/commonMain/kotlin/co/za/xdcodez/wealthbuilder/finance/presentation/composables/`
- Navigation: `composeApp/src/commonMain/kotlin/co/za/xdcodez/wealthbuilder/navigation/`

### 2.2 Core Components

#### CardComposable
**Path:** `common/widgets/CardComposable.kt`

**Purpose:** Reusable glassmorphic card with gradient background and border

**Usage:**
```kotlin
CardComposable(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
)
```

**Design Pattern:**
- Gradient background: White 12% → 5% alpha
- Border gradient: White 30% → 10% alpha
- Border width: 0.5dp
- Corner radius: 12dp
- Internal padding: 12dp
- Content: Column layout with start alignment

#### CustomTextField
**Path:** `common/widgets/CustomTextField.kt`

**Purpose:** Styled text input with focus states and optional prefix

**Usage:**
```kotlin
CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    filter: ((String) -> String)? = null
)
```

**Design Pattern:**
- Background: White 8% alpha
- Focused border: Primary color (1dp)
- Unfocused border: Transparent
- Corner radius: 8dp
- Padding: 10dp horizontal, 12dp vertical
- Text color: White
- Placeholder: White 27% alpha (0x44FFFFFF)
- Prefix color: White 60% alpha (0x99FFFFFF)
- Cursor: Primary color
- Font size: 15sp

#### CustomAppTopBar
**Path:** `common/widgets/CustomAppTopBar.kt`

**Purpose:** Reusable app bar with title and optional back navigation

**Usage:**
```kotlin
CustomAppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showNavigationIcon: Boolean = false,
    onNavigate: () -> Unit = {},
    content: @Composable () -> Unit
)
```

**Design Pattern:**
- Title alignment: Center
- Title style: headlineSmall
- Text color: White
- Back icon: AutoMirrored ArrowBack
- Bottom padding: 10dp

#### ModernBottomNavigationBar
**Path:** `navigation/CustomDrawer.kt`

**Purpose:** Animated bottom navigation with expanding labels

**Usage:**
```kotlin
ModernBottomNavigationBar(navController: NavController)
```

**Design Pattern:**
- Background: Gray 50% alpha (0.5f)
- Corner radius: 8dp
- Padding: 12dp horizontal, 18dp bottom, 8dp vertical
- Selected state:
  - Background: White 5% alpha
  - Icon/text color: Gold (#D4AF37)
  - Scale: 1.1x
  - Shows label
- Unselected state:
  - Background: Transparent
  - Icon color: White 50% alpha
  - Scale: 1.0x
  - No label
- Animation: 300ms tween
- Icon size: 24dp
- Label font size: 12sp

#### BudgetCategoryCardComposable
**Path:** `finance/presentation/composables/BudgetCategoryCardComposable.kt`

**Purpose:** Budget category display with progress tracking

**Design Pattern:**
- Uses CardComposable base
- Category name: UPPERCASE, titleMedium, primary color
- Progress bar: 6dp height, rounded caps, primary color
- Amounts: White text
- Labels: White 60% alpha
- Navigation icon: Primary color

#### SummaryCategoryCardComposable
**Path:** `finance/presentation/composables/BudgetCategoryCardComposable.kt`

**Purpose:** Compact budget category summary

**Design Pattern:**
- Background: White 5% alpha
- Corner radius: 8dp
- Padding: 8dp horizontal, 4dp vertical
- Progress-dependent color coding
- Progress bar: 6dp height with percentage display

### 2.3 Component Patterns

**Glassmorphic Cards:**
All cards follow a consistent glassmorphic design:
- Semi-transparent white backgrounds (5-12% alpha)
- Gradient borders with varying opacity
- Rounded corners (8-12dp)
- Consistent internal padding (12dp)

**Interactive Elements:**
- Scale animations on interaction (1.0x → 1.1x)
- Color transitions (300ms tween)
- Ripple effects disabled (null indication)
- Custom MutableInteractionSource for state tracking

**Text Hierarchy:**
- Primary headings: titleMedium/titleLarge, primary color or white
- Body text: bodyMedium, white
- Secondary labels: labelMedium, white 60% alpha
- Placeholders: white 27% alpha

---

## 3. Frameworks & Libraries

### 3.1 UI Framework

**Compose Multiplatform** (Kotlin Multiplatform)
- Material 3 components
- Jetpack Compose runtime
- Navigation Compose

**Key Dependencies:**
```kotlin
// Compose
implementation(compose.runtime)
implementation(compose.foundation)
implementation(compose.material3)
implementation(compose.ui)
implementation(compose.components.resources)
implementation(compose.components.uiToolingPreview)

// Navigation
implementation(libs.navigation.compose)
```

### 3.2 State Management

**ViewModel + StateFlow pattern:**
- ViewModels for business logic
- StateFlow for state management
- Koin for dependency injection

```kotlin
// Koin modules
implementation(libs.koin.core)
implementation(libs.koin.compose)
implementation(libs.koin.compose.viewmodel)
implementation(libs.lifecycle.viewmodel)
```

### 3.3 Backend

**Firebase Firestore:**
```kotlin
implementation("dev.gitlive:firebase-firestore:2.4.0")
```

### 3.4 Additional Libraries

```kotlin
// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

// Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

// Date/Time
implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

// Swipe gestures
implementation("me.saket.swipe:swipe:1.3.0")
```

---

## 4. Asset Management

### 4.1 Asset Storage

**Compose Resources Location:**
`composeApp/src/commonMain/composeResources/drawable/`

**Platform-Specific Resources:**
- Android: `composeApp/src/androidMain/res/drawable/`
- iOS: Handled through Compose resources

### 4.2 Asset Types

**Vector Icons (.xml):**
- Navigation icons: `ic_home.xml`, `ic_finance.xml`, `ic_exercise.xml`, etc.
- Feature icons: `ic_brain.xml`, `ic_gym.xml`, `ic_habits.xml`, `ic_impulse.xml`

**Raster Images (.png):**
- Ring graphics: `cardio_run.png`, `focus_ring.png`, `gym_ring.png`, `impulse_ring.png`

### 4.3 Asset Access Pattern

**Using Compose Resources:**
```kotlin
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import wealthbuilder.composeapp.generated.resources.Res
import wealthbuilder.composeapp.generated.resources.ic_finance

// Usage
Icon(
    painter = painterResource(Res.drawable.ic_finance),
    contentDescription = "Finance",
    tint = Color.White
)
```

**Resource generation:**
- Resources are auto-generated at build time
- Import from `wealthbuilder.composeapp.generated.resources`
- Type-safe resource access via `Res` object

### 4.4 Asset Optimization

- Vector graphics preferred for icons (XML format)
- PNG used for complex graphics/images
- No explicit CDN configuration (Firebase backend handles storage)
- Resources bundled with app at compile time

---

## 5. Icon System

### 5.1 Icon Storage

**Primary Location:** `composeApp/src/commonMain/composeResources/drawable/`

**Icon Naming Convention:**
```
ic_[feature]_[variant?].xml
```

**Examples:**
- `ic_home.xml` - Home navigation icon
- `ic_finance.xml` - Finance/budget navigation icon
- `ic_gym.xml` - Gym/fitness icon (used for journal)
- `ic_habits.xml` - Habits tracking icon
- `ic_impulse.xml` - Impulse/habits icon
- `ic_brain.xml` - Mental/focus icon
- `ic_profile.xml` - User profile icon
- `ic_exercise.xml` - Exercise tracking icon

### 5.2 Icon Usage Pattern

**Material Icons:**
```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.MoreVert

// Usage
Icon(
    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
    tint = Color.White,
    contentDescription = "Navigate back"
)
```

**Custom Icons (Compose Resources):**
```kotlin
import org.jetbrains.compose.resources.painterResource
import wealthbuilder.composeapp.generated.resources.Res
import wealthbuilder.composeapp.generated.resources.ic_finance

// Usage
Icon(
    painter = painterResource(Res.drawable.ic_finance),
    contentDescription = "Finance",
    tint = MaterialTheme.colorScheme.primary,
    modifier = Modifier.size(24.dp)
)
```

### 5.3 Icon Specifications

**Standard Size:** 24dp × 24dp
**Viewport:** 960 × 960 (for custom icons)
**Fill Color:** `#e3e3e3` (light gray) - will be tinted at runtime
**Tint Colors:**
- Primary: `MaterialTheme.colorScheme.primary` (#1E88E5)
- White: `Color.White`
- Accent Gold: `Color(0xFFD4AF37)` (for selected states)
- Muted: `Color.White.copy(alpha = 0.5f)` (for unselected states)

### 5.4 Icon Categories

**Navigation Icons:**
- Bottom navigation: `ic_finance`, `ic_impulse`, `ic_gym`
- Top app bar: Material Icons (ArrowBack, MoreVert)
- Inline navigation: Material Icons (KeyboardArrowRight)

**Feature Icons:**
- Habits: `ic_habits`, `ic_impulse`, `ic_brain`
- Fitness: `ic_exercise`, `ic_gym`
- Profile: `ic_profile`
- Home: `ic_home`

---

## 6. Styling Approach

### 6.1 CSS Methodology

**Compose UI (not CSS)**
The app uses Jetpack/Compose Multiplatform declarative UI, not traditional CSS.

**Styling is achieved through:**
- Modifier chains
- Material 3 theming
- Custom composable components
- Inline styling with Compose APIs

### 6.2 Styling Patterns

#### Modifier Chains
```kotlin
Modifier
    .fillMaxWidth()
    .padding(12.dp)
    .clip(RoundedCornerShape(8.dp))
    .background(Color.White.copy(alpha = 0.05f))
    .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(8.dp)
    )
```

**Order matters:**
1. Size modifiers (fillMaxWidth, height, size)
2. Padding
3. Shape (clip)
4. Visual effects (background, border)
5. Interaction (clickable)

#### Theme-Aware Styling
```kotlin
// Always use MaterialTheme for consistency
color = MaterialTheme.colorScheme.primary
style = MaterialTheme.typography.titleMedium
```

#### Gradients and Brushes
```kotlin
// Background gradients
.background(
    brush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.12f),
            Color.White.copy(alpha = 0.05f)
        )
    )
)

// Border gradients
.border(
    width = 0.5.dp,
    brush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.1f)
        )
    ),
    shape = RoundedCornerShape(12.dp)
)
```

### 6.3 Global Styles

**Theme Provider:** `WealthBuilderTheme`
**Location:** `composeApp/src/commonMain/kotlin/co/za/xdcodez/wealthbuilder/theme/LevelUpTheme.kt`

```kotlin
@Composable
fun WealthBuilderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorTheme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientOption0()),
            content = { content() }
        )
    }
}
```

**Key points:**
- All screens wrapped in `WealthBuilderTheme`
- Scaffold with transparent container color
- Global gradient background applied at theme level
- Material 3 color scheme provides semantic colors

### 6.4 Responsive Design

#### Adaptive Layouts
```kotlin
// Fill available width with weight
Row {
    Text("Label", modifier = Modifier.weight(1f))
    Icon(...)
}

// Responsive padding
.padding(horizontal = 16.dp, vertical = 8.dp)
```

#### Platform-Specific Code
```kotlin
// Expect/Actual pattern for platform differences
expect val platformModule: Module

// Android-specific
androidMain.dependencies {
    implementation(libs.koin.android)
}

// iOS-specific
iosMain.dependencies {
    // iOS-specific deps
}
```

#### Screen Size Handling
- Uses fillMaxWidth() for adaptive width
- Vertical scrolling with LazyColumn/Column
- Bottom navigation adapts to content
- No explicit breakpoints (single mobile-first design)

---

## 7. Project Structure

### 7.1 Overall Organization

```
composeApp/src/
├── commonMain/kotlin/co/za/xdcodez/wealthbuilder/
│   ├── common/                    # Shared utilities and widgets
│   │   ├── widgets/              # Reusable UI components
│   │   ├── Extensions.kt         # Extension functions
│   │   └── Util.kt              # Utility functions
│   │
│   ├── theme/                    # Design tokens and theme
│   │   ├── Colors.kt
│   │   └── LevelUpTheme.kt
│   │
│   ├── navigation/               # Navigation setup
│   │   ├── CustomDrawer.kt      # Bottom nav bar
│   │   ├── Destination.kt       # Route definitions
│   │   └── RootNavigationGraph.kt
│   │
│   ├── di/                       # Dependency injection
│   │   ├── Module.kt            # Koin modules
│   │   ├── initKoin.kt
│   │   └── AppDatabase.kt
│   │
│   ├── [feature]/                # Feature modules (see below)
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   │
│   ├── App.kt                    # Root app component
│   └── Platform.kt              # Platform abstractions
│
├── commonMain/composeResources/  # Shared resources
│   └── drawable/                # Icons and images
│
├── androidMain/                  # Android-specific
├── iosMain/                      # iOS-specific
└── webMain/                      # Web-specific (unused)
```

### 7.2 Feature Module Structure

**Clean Architecture Pattern:**
Each feature follows a layered architecture:

```
[feature]/
├── data/
│   ├── model/                   # Data transfer objects
│   └── Firebase[Feature]RepositoryImpl.kt
│
├── domain/
│   ├── model/                   # Domain entities
│   ├── dto/                     # Data transfer objects
│   └── [Feature]Repository.kt  # Repository interface
│
└── presentation/
    ├── [screen]/                # Screen-specific UI
    │   ├── [Screen]Route.kt    # Composable screen
    │   ├── [Screen]ViewModel.kt
    │   ├── [Screen]State.kt
    │   └── [Screen]Actions.kt
    └── composables/             # Reusable feature components
```

### 7.3 Current Features

#### Finance Module
**Path:** `finance/`

**Screens:**
- `budgetOverview/` - Main budget dashboard
- `budgetTransactions/` - Transaction list and management
- `createbudget/` - Budget creation wizard
- `composables/` - Budget-specific components

**Key Components:**
- BudgetCategoryCardComposable
- MonthlyBudgetComposable
- TransactionsBottomSheet

#### Journal Module
**Path:** `journal/`

**Screens:**
- `home/` - Trading journal home
- `details/` - Day detail view

**Domain Models:**
- TradeEntry
- TradingConfig
- AccountBalance
- SessionStatus

#### Habits Module
**Path:** `habits/`

**Screens:**
- `today/` - Daily habit check-in

**Domain Models:**
- Habit
- HabitStreak
- QuarterlyGoal
- Badge

### 7.4 Navigation Architecture

**Type-Safe Navigation:**
```kotlin
sealed class Destination(val route: String) {
    object BudgetTransactionsDestination: Destination("budget_transaction/{monthId}/{categoryId}") {
        fun createRoute(monthId: String, categoryId: String) =
            "budget_transaction/$monthId/$categoryId"
    }
}
```

**Bottom Navigation:**
```kotlin
sealed class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: DrawableResource
) {
    object Habits : BottomNavDestination("habits", "Habits", Res.drawable.ic_impulse)
    object Finance : BottomNavDestination("finance", "Budget", Res.drawable.ic_finance)
    object Journal : BottomNavDestination("journal", "Trading", Res.drawable.ic_gym)
}
```

**Shared ViewModel Pattern:**
```kotlin
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController,
    parentRoute: String
): T {
    val parentEntry = remember(this) {
        navController.getBackStackEntry(parentRoute)
    }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}
```

### 7.5 Dependency Injection

**Koin Setup:**
```kotlin
val sharedModule = module {
    singleOf(::FirebaseBudgetRepositoryImpl).bind<BudgetRepository>()
    singleOf(::FirebaseTradingRepositoryImpl).bind<JournalRepository>()
    singleOf(::FirebaseHabitsRepositoryImpl).bind<HabitsRepository>()

    viewModelOf(::BudgetScreenViewModel)
    viewModelOf(::CreateBudgetViewModel)
    viewModelOf(::TransactionsViewModel)
    viewModelOf(::JournalHomeViewModel)
    viewModelOf(::DayDetailViewModel)
    viewModelOf(::TodayCheckInViewModel)
}
```

**Platform Modules:**
- `expect val platformModule: Module` - Platform-specific dependencies
- Android: Provides Firebase Android SDK dependencies
- iOS: Provides iOS-specific dependencies

---

## 8. Figma Integration Guidelines

### 8.1 Design-to-Code Workflow

When converting Figma designs to code for this project:

1. **Color Mapping:**
   - Primary actions/highlights → `MaterialTheme.colorScheme.primary` (#1E88E5)
   - Selected states → `Color(0xFFD4AF37)` (gold)
   - Text → `Color.White` or `Color(0x99FFFFFF)` for secondary
   - Backgrounds → Use existing glassmorphic card patterns
   - Always maintain alpha values for consistency

2. **Typography:**
   - Map to Material 3 typography scale
   - Major headings → `titleLarge`
   - Section headers → `titleMedium`
   - Body → `bodyMedium`
   - Labels → `labelMedium` or `labelLarge`

3. **Spacing:**
   - Use 4dp grid system
   - Common values: 4dp, 8dp, 12dp, 16dp
   - Card padding: 12dp standard
   - Screen padding: 12dp horizontal, 18dp bottom

4. **Components:**
   - Check for existing components before creating new ones
   - Use `CardComposable` for any card-based layouts
   - Use `CustomTextField` for all text inputs
   - Use `CustomAppTopBar` for screen headers

5. **Responsive Behavior:**
   - Use `fillMaxWidth()` for horizontal expansion
   - Use `weight(1f)` for proportional layouts
   - Wrap scrollable content in `LazyColumn` or `Column` with scroll modifier

### 8.2 Component Creation Pattern

When creating new components from Figma:

```kotlin
@Composable
fun NewComponent(
    // State parameters first
    data: DataModel,
    // Callbacks
    onAction: () -> Unit = {},
    // UI customization last
    modifier: Modifier = Modifier
) {
    // Use existing base components
    CardComposable(modifier = modifier) {
        // Component content
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            // Content here
        }
    }
}

// Always include preview
@Preview
@Composable
fun NewComponentPreview() {
    WealthBuilderTheme {
        NewComponent(
            data = DataModel(/* sample data */),
            onAction = {}
        )
    }
}
```

### 8.3 Common Patterns to Replicate

**Glassmorphic Cards:**
```kotlin
.clip(RoundedCornerShape(12.dp))
.background(
    brush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.12f),
            Color.White.copy(alpha = 0.05f)
        )
    )
)
.border(
    width = 0.5.dp,
    brush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.1f)
        )
    ),
    shape = RoundedCornerShape(12.dp)
)
```

**Progress Indicators:**
```kotlin
LinearProgressIndicator(
    modifier = Modifier.fillMaxWidth().height(6.dp),
    progress = { progressValue },
    color = MaterialTheme.colorScheme.primary,
    trackColor = Color(0x33FFFFFF),
    strokeCap = StrokeCap.Round
)
```

**Interactive Animations:**
```kotlin
val scale by animateFloatAsState(
    targetValue = if (isSelected) 1.1f else 1f,
    animationSpec = tween(300)
)

val backgroundColor by animateColorAsState(
    targetValue = if (isSelected)
        Color.White.copy(alpha = 0.05f)
    else
        Color.Transparent,
    animationSpec = tween(300)
)
```

### 8.4 Asset Export from Figma

**Icons:**
- Export as SVG
- Convert to Android Vector Drawable (XML)
- Place in `composeApp/src/commonMain/composeResources/drawable/`
- Follow naming convention: `ic_[feature]_[variant?].xml`
- Use 24dp × 24dp artboard
- Set viewport to 960 × 960

**Images:**
- Export as PNG
- Use @2x or @3x resolution for high DPI
- Optimize file size
- Place in `composeApp/src/commonMain/composeResources/drawable/`

**Colors:**
- Extract as hex values
- Add to `Colors.kt` with semantic names
- Use existing colors when possible

**Typography:**
- Map to Material 3 typography scale
- Do not create custom text styles unless necessary
- Font: Default system font (no custom fonts currently)

---

## 9. Code Style Guidelines

### 9.1 Naming Conventions

**Files:**
- Screens: `[Feature]Screen.kt`
- ViewModels: `[Feature]ViewModel.kt`
- State: `[Feature]State.kt`
- Composables: `[Purpose]Composable.kt`

**Composables:**
- PascalCase: `BudgetCategoryCardComposable`
- Descriptive names indicating purpose
- Suffix with "Composable" for reusable components

**Variables:**
- camelCase: `val currentRoute`, `val isSelected`
- Descriptive names, avoid abbreviations
- State variables: `val state by viewModel.state.collectAsState()`

**Constants:**
- camelCase for regular vals: `val primary = Color(0xFF1E88E5)`
- SCREAMING_SNAKE_CASE for compile-time constants only

### 9.2 Composable Structure

**Parameter Order:**
1. State/data parameters
2. Callbacks/actions
3. Modifier (always defaulted, always last)

```kotlin
@Composable
fun CustomComponent(
    // 1. State
    title: String,
    data: DataModel,
    isSelected: Boolean = false,
    // 2. Callbacks
    onClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    // 3. UI customization
    modifier: Modifier = Modifier
) { /* ... */ }
```

### 9.3 File Organization

**Imports:**
```kotlin
// Android/Compose imports first
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme

// Project imports
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme

// Resource imports last
import org.jetbrains.compose.resources.painterResource
import wealthbuilder.composeapp.generated.resources.Res
```

**File Contents Order:**
1. Package declaration
2. Imports
3. Main composable
4. Supporting composables
5. Preview composables
6. Data classes / sealed classes (if small)

---

## 10. Common Pitfalls & Best Practices

### 10.1 Do's ✅

- **Always use existing components** before creating new ones
- **Wrap screens in `WealthBuilderTheme`**
- **Use `MaterialTheme.colorScheme`** for colors
- **Add `@Preview` annotations** to all composables
- **Use `remember` for state** that shouldn't recompose
- **Use `collectAsState()`** for StateFlow
- **Follow the 4dp spacing grid**
- **Use `Modifier.fillMaxWidth()`** for responsive width
- **Use `weight(1f)` in Row/Column** for proportional layouts
- **Handle loading/error states** in ViewModels

### 10.2 Don'ts ❌

- **Don't hardcode colors** - use theme colors or defined tokens
- **Don't create custom fonts** - use Material 3 typography
- **Don't ignore the existing gradient system**
- **Don't use plain Card/Surface** - use `CardComposable`
- **Don't use plain TextField/OutlinedTextField** - use `CustomTextField`
- **Don't forget to handle dark theme** (app is dark-only currently)
- **Don't create platform-specific UI** unless absolutely necessary
- **Don't use magic numbers** - define constants for repeated values
- **Don't nest too many Composables** - extract to separate functions
- **Don't use `LaunchedEffect` without cleanup** for long-running operations

### 10.3 Performance Considerations

- **Use `derivedStateOf`** for computed values
- **Use `remember { }` for expensive calculations**
- **Avoid recomposition** - use immutable data classes
- **Use `LazyColumn` instead of `Column` + `verticalScroll`** for lists
- **Use `key()` in LazyColumn** for stable items
- **Hoist state** to the appropriate level

### 10.4 Architecture Patterns

**Repository Pattern:**
```kotlin
interface FeatureRepository {
    suspend fun getData(): Result<Data>
}

class FirebaseFeatureRepositoryImpl : FeatureRepository {
    override suspend fun getData(): Result<Data> {
        // Implementation
    }
}
```

**ViewModel Pattern:**
```kotlin
class FeatureViewModel(
    private val repository: FeatureRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FeatureState())
    val state: StateFlow<FeatureState> = _state.asStateFlow()

    fun onAction(action: FeatureAction) {
        when (action) {
            // Handle actions
        }
    }
}
```

**Screen Composable Pattern:**
```kotlin
@Composable
fun FeatureScreenRoute(
    viewModel: FeatureViewModel = koinViewModel(),
    onNavigate: (NavigationEvent) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    FeatureScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigate = onNavigate
    )
}

@Composable
private fun FeatureScreen(
    state: FeatureState,
    onAction: (FeatureAction) -> Unit,
    onNavigate: (NavigationEvent) -> Unit
) {
    // UI implementation
}
```

---

## 11. Quick Reference

### 11.1 Common Colors

| Use Case | Color | Code |
|----------|-------|------|
| Primary action | Blue | `MaterialTheme.colorScheme.primary` or `Color(0xFF1E88E5)` |
| Selected state | Gold | `Color(0xFFD4AF37)` |
| Primary text | White | `Color.White` |
| Secondary text | White 60% | `Color(0x99FFFFFF)` |
| Placeholder | White 27% | `Color(0x44FFFFFF)` |
| Card background light | White 12% | `Color.White.copy(alpha = 0.12f)` |
| Card background dark | White 5% | `Color.White.copy(alpha = 0.05f)` |
| Input background | White 8% | `Color.White.copy(alpha = 0.08f)` |
| Border highlight | White 30% | `Color.White.copy(alpha = 0.3f)` |
| Track/inactive | White 20% | `Color(0x33FFFFFF)` |

### 11.2 Common Spacing

| Use | Value |
|-----|-------|
| Minimal gap | 2dp, 4dp |
| Small spacing | 6dp, 8dp |
| Standard spacing | 10dp, 12dp |
| Large spacing | 16dp, 18dp |
| Card padding | 12dp |
| Input padding horizontal | 10dp |
| Input padding vertical | 12dp |
| Bottom nav padding | 12dp horizontal, 18dp bottom |
| Progress bar height | 6dp |

### 11.3 Common Shapes

| Component | Corner Radius |
|-----------|---------------|
| Cards | 12dp |
| Input fields | 8dp |
| Small components | 8dp |
| Bottom nav bar | 8dp |
| Bottom nav items | 16dp |

### 11.4 Import Shortcuts

```kotlin
// Theme
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme

// Common widgets
import co.za.xdcodez.wealthbuilder.common.widgets.CardComposable
import co.za.xdcodez.wealthbuilder.common.widgets.CustomTextField
import co.za.xdcodez.wealthbuilder.common.widgets.CustomAppTopBar

// Koin
import org.koin.compose.viewmodel.koinViewModel

// Resources
import org.jetbrains.compose.resources.painterResource
import wealthbuilder.composeapp.generated.resources.Res
```

---

## 12. Additional Notes

### 12.1 Current Development Status

Based on git status, recent work includes:
- Dashboard feature implementation (DashboardRepository, DashboardScreen, DashboardViewModel)
- Journal setup screens removed (likely refactored)
- Navigation updates (CustomBottomNavigationBar added)
- Budget and habits modules active development

### 12.2 Future Considerations

- No dark/light theme toggle currently (dark mode only)
- No custom font families (using system defaults)
- No explicit accessibility features documented
- No animation duration constants (using inline 300ms)
- Consider creating a Constants.kt for repeated values

### 12.3 Testing

- Preview composables exist for major components
- No explicit test files found in analyzed structure
- Consider adding unit tests for ViewModels
- Consider adding UI tests for critical flows

---

**Last Updated:** 2026-09-16
**Kotlin Version:** 2.2.20
**Compose Multiplatform:** Latest stable
**Target SDK:** Android 35, iOS 17+