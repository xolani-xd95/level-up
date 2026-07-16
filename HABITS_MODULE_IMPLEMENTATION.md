# Habits & Quarterly Goals Module - Implementation Summary

## Overview
This document summarizes the implementation of the Habits & Quarterly Goals module in the WealthBuilder KMP app, following the existing layered architecture pattern (domain → data → presentation).

## Module Structure

### 1. Domain Layer (`habits/domain/`)
Contains the business logic and data models:

#### Models (`habits/domain/model/`)
- **HabitType.kt** - Enum: AVOID, BUILD
- **HabitKey.kt** - Enum: SMOKING, GYM, FOCUS, READING
- **Habit.kt** - Core habit data class with key, type, label, isCore flag, and icon
- **DailyEntry.kt** - Daily check-in record with helper methods (isFocusWin, isFocusPerfect)
- **WeeklyRollup.kt** - Weekly aggregated statistics
- **HabitStreak.kt** - Tracks current and longest streaks per habit
- **GoalType.kt** - Enum: STREAK, COUNT, METRIC_SYNCED, METRIC_MANUAL, MILESTONE
- **QuarterlyGoal.kt** - Quarterly goal with progress tracking
- **Badge.kt** - Sealed class for achievement badges (SmokeFreeMilestone, PerfectFocusWeek, GymStreak)

#### Repository Interface
- **HabitsRepository.kt** - Repository interface defining all data operations:
  - Daily entry CRUD operations
  - Streak tracking and updates
  - Weekly rollup management
  - Quarterly goals management
  - Badge persistence

### 2. Data Layer (`habits/data/`)
Implements Firestore persistence:

- **FirebaseHabitsRepositoryImpl.kt** - Firestore implementation of HabitsRepository
  - Uses nested collections under `users/{uid}/`
  - Collections: habitEntries, habitStreaks, weeklyRollups, quarterlyGoals, badges
  - Composite document keys for efficient date-range queries

### 3. Presentation Layer (`habits/presentation/`)

#### Today Check-In Screen (`habits/presentation/today/`)
- **TodayCheckInState.kt** - UI state with current entry, goals summary, streaks
- **TodayCheckInViewModel.kt** - Manages check-in logic:
  - Toggle gym/focus/reading
  - Log smoking slips with notes
  - Badge unlock detection
  - Weekly progress calculation
- **TodayCheckInScreen.kt** - Composable UI:
  - Compact quarterly goals summary (thin progress bars)
  - Smoke-free row with streak display and "Log slip" button
  - Gym row (single tap, weekly count)
  - Focus blocks row (3 tappable blocks, partial credit)
  - Reading row (deprioritized with lower opacity)
  - Slip bottom sheet with optional note field
  - Badge unlock modal

#### Quarterly Goals Screen (`habits/presentation/quarterly/`)
- **QuarterlyGoalsState.kt** - UI state with goals list, badges, current quarter info
- **QuarterlyGoalsViewModel.kt** - Manages quarterly view:
  - Load all quarterly goals
  - Calculate day of quarter
  - Toggle milestone completion
  - Navigate to check-in
- **QuarterlyGoalsScreen.kt** - Composable UI:
  - Header with quarter and day count
  - Progress goal cards (STREAK/COUNT/METRIC types)
  - Milestone goal rows (checkbox, no progress bar)
  - Badge strip with earned badges + locked preview
  - CTA button to navigate to check-in

### 4. Navigation Integration

#### Bottom Navigation (`navigation/CustomDrawer.kt`)
- **Added to bottom navigation bar**: New "Habits" tab (3rd position)
- **Icon**: `ic_habits.xml` - Checkmark in circle icon
- **Route**: `BottomNavDestination.Habits` → navigates to Today Check-in screen
- **Bottom nav order**: Budget → Trading → **Habits** → Profile

#### Routes Added (`navigation/Destination.kt`)
- `HabitsTodayDestination` - Today's check-in screen (detailed route)
- `HabitsQuarterlyGoalsDestination` - Quarterly goals overview

#### Navigation Graph (`navigation/RootNavigationGraph.kt`)
- `BottomNavDestination.Habits.route` ("habits") → Today Check-in screen
- `HabitsTodayDestination.route` ("habits_today") → Today Check-in screen
- `HabitsQuarterlyGoalsDestination.route` ("habits_quarterly_goals") → Quarterly Goals screen
- **Navigation flow**:
  - Today Check-in has "See all" button → Quarterly Goals
  - Quarterly Goals has "Go to today's check-in" button → Today Check-in
  - Bottom nav "Habits" tab → Today Check-in (default)

### 5. Dependency Injection (`di/Module.kt`)
Added to `sharedModule`:
- `FirebaseHabitsRepositoryImpl` bound to `HabitsRepository`
- `TodayCheckInViewModel`
- `QuarterlyGoalsViewModel`

## Design Principles Implemented

1. **Low daily friction** - Only 3 core habits tracked (Smoking, Gym, Focus)
2. **Partial credit** - Focus blocks use 2/3 win threshold
3. **Streak preservation** - `currentStreak` resets on failure, `longestStreak` persists
4. **Quarterly-first** - All tracking ladders up to quarterly goals
5. **Badges for habits only** - Milestone goals don't earn badges

## Key Features

### Core Habits
- **Smoke-Free (AVOID)**: Default true, streak tracking, longest streak preservation
- **Gym (BUILD)**: Single tap toggle, weekly target tracking (4/week default)
- **Focus Blocks (BUILD)**: 3 blocks/day, win at 2/3, perfect at 3/3
- **Reading (optional)**: Deprioritized, no streak pressure

### Quarterly Goals
- **STREAK**: Consecutive days (e.g., 90 days smoke-free)
- **COUNT**: Accumulated count (e.g., 48 gym sessions)
- **METRIC_SYNCED**: Live data from Budget module (e.g., overall savings)
- **METRIC_MANUAL**: Manually logged separate pot (e.g., laptop fund)
- **MILESTONE**: Binary completion (e.g., "Renew passport")

### Badge System
- **SmokeFreeMilestone**: Unlocked at streak milestones (7, 14, 30, 60, 90 days)
- **GymStreak**: Unlocked for consecutive gym days
- **PerfectFocusWeek**: Unlocked for 7 consecutive perfect focus days

## Firestore Schema

```
users/{uid}/
  ├── habitEntries/{date}
  │   ├── date: String
  │   ├── smokeFree: Boolean
  │   ├── slipNote: String?
  │   ├── gymDone: Boolean
  │   ├── focusBlocksCompleted: Int
  │   └── readingDone: Boolean?
  │
  ├── habitStreaks/{habitKey}
  │   ├── habitKey: String
  │   ├── currentStreak: Int
  │   ├── longestStreak: Int
  │   └── lastUpdated: String
  │
  ├── weeklyRollups/{weekStart}
  │   ├── weekStart: String
  │   ├── smokeFreeDays: Int
  │   ├── gymDays: Int
  │   ├── focusWinDays: Int
  │   ├── focusPerfectDays: Int
  │   └── readingDays: Int
  │
  ├── quarterlyGoals/{goalId}
  │   ├── title: String
  │   ├── type: String
  │   ├── icon: String
  │   ├── targetValue: Double
  │   ├── currentValue: Double
  │   ├── unit: String?
  │   ├── linkedHabitKey: String?
  │   ├── dueDate: String?
  │   ├── isComplete: Boolean
  │   └── quarter: String
  │
  └── badges/{badgeId}
      ├── badgeType: String
      ├── label: String
      ├── icon: String
      └── [type-specific fields]
```

## Next Steps / Open Items

1. **User Authentication**: Replace hardcoded `userId` in repository with actual auth
2. **Badge Thresholds**: Define exact milestone values for each badge type
3. **Weekly Rollup Automation**: Consider Cloud Function trigger for automatic rollup calculation
4. **Custom Goals**: Enable users to create custom MANUAL/MILESTONE goals
5. **Budget Integration**: Implement METRIC_SYNCED goals reading from Budget module
6. **Notifications**: Add reminders for daily check-ins
7. **Analytics**: Track completion rates and trends
8. **Initial Data**: Create sample quarterly goals or onboarding flow for first-time users

## Testing Recommendations

1. Test daily entry persistence and retrieval
2. Test streak calculations (current vs longest)
3. Test weekly rollup aggregation
4. Test badge unlock conditions
5. Test partial credit for focus blocks (2/3 win)
6. Test slip logging and streak reset
7. Test quarterly goal progress calculations
8. Test milestone toggle functionality

## Architecture Compliance

✅ Follows existing layered architecture (domain → data → presentation)
✅ Uses Firestore for persistence (consistent with Budget and Journal modules)
✅ Uses Koin for dependency injection
✅ Uses StateFlow and SharedFlow for reactive UI updates
✅ Uses Navigation Compose for screen navigation
✅ Material 3 design system
✅ Multiplatform Compose UI
