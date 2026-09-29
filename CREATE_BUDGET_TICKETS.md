# Create Budget Flow - Cleanup & Enhancement Tickets

## Overview
Tickets for refactoring and improving the 3-step budget creation wizard in the WealthBuilder app.

**Flow Location:** `composeApp/src/commonMain/kotlin/co/za/xdcodez/wealthbuilder/finance/presentation/createbudget/`

---

## 🐛 Bug Fixes

### TICKET-1: Fix Previous Month Rollover Calculation
**Priority:** High
**Type:** Bug
**Files:** `CreateBudgetViewModel.kt:158-177`

**Description:**
The rollover calculation from the previous month is incomplete. The amount is calculated but never assigned to the IncomeSourceInput, making the rollover feature non-functional.

**Current Issue:**
```kotlin
IncomeSourceInput(
    name = "Previous Month",
    // amount = String.format("%.2f", rollover) // <- Commented out
)
```

**Acceptance Criteria:**
- [ ] Uncomment and fix the rollover amount assignment
- [ ] Test with a previous month that has positive balance
- [ ] Test with a previous month that has negative balance (debt)
- [ ] Verify rollover source appears correctly in Step 1
- [ ] Ensure rollover can be edited or removed by user

**Technical Notes:**
- Amount should be formatted as string to match IncomeSourceInput.amount type
- Consider adding readonly flag to distinguish rollover from manual income sources
- Handle edge case: first month (no previous month)

---

### TICKET-2: Fix Navigation Layout Issues on CreateBudgetScreen
**Priority:** Medium
**Type:** Bug
**Files:** `CreateBudgetScreen.kt:69-142`

**Description:**
The bottom navigation (Back/Next buttons) spacing uses Spacer with weight which can cause layout issues. The buttons should be consistently positioned.

**Current Issue:**
```kotlin
if (state.currentStep > 1) {
    Row { /* Back button */ }
} else {
    Spacer(modifier = Modifier.weight(1f))  // <- Can cause issues
}
```

**Acceptance Criteria:**
- [ ] Refactor bottom navigation to use proper SpaceBetween arrangement
- [ ] Remove unnecessary Spacer elements
- [ ] Ensure buttons are always properly aligned (left/right)
- [ ] Test on different screen sizes
- [ ] Verify button tap targets are adequate (minimum 48dp)

---

### TICKET-3: Add Loading State Handling
**Priority:** Medium
**Type:** Enhancement
**Files:** `CreateBudgetScreen.kt`, `CreateBudgetViewModel.kt`

**Description:**
The `isLoading` state exists in `CreateBudgetUiState` but is never used. Add proper loading indicators when saving budget or loading previous month data.

**Current Gap:**
- `isLoading` field defined but never set to true
- No visual feedback during save operation
- No feedback when loading previous month rollover

**Acceptance Criteria:**
- [ ] Show loading indicator when fetching previous month rollover
- [ ] Show loading indicator when saving budget (Step 3)
- [ ] Disable form inputs during loading states
- [ ] Disable navigation buttons during save operation
- [ ] Add timeout handling (10 seconds max)

---

### TICKET-4: Improve Error Handling
**Priority:** High
**Type:** Enhancement
**Files:** `CreateBudgetViewModel.kt`, `CreateBudgetScreen.kt`

**Description:**
Error state exists but is not displayed to users. Users get no feedback when budget creation fails.

**Current Issue:**
```kotlin
_state.update { it.copy(error = "Failed to save budget. Please try again") }
// Error is set but never shown in UI
```

**Acceptance Criteria:**
- [ ] Display error message to user (Snackbar or dialog)
- [ ] Add specific error messages for different failure scenarios
- [ ] Add retry mechanism for failed saves
- [ ] Log errors for debugging
- [ ] Clear error state when user retries or navigates

**Error Scenarios to Handle:**
- Network failure
- Invalid data
- Repository errors
- Validation failures

---

## ✨ Enhancements

### TICKET-5: Add Input Validation Feedback
**Priority:** Medium
**Type:** Enhancement
**Files:** `IncomeSourceContent.kt`, `CategoryAllocationContent.kt`

**Description:**
Currently, the Next button is simply disabled when validation fails. Users need visual feedback on what's wrong.

**Acceptance Criteria:**
- [ ] Show red border on invalid text fields
- [ ] Add inline error messages below invalid fields
- [ ] Show helpful hints (e.g., "Amount must be greater than 0")
- [ ] Validate on blur, not just on submit
- [ ] Add green checkmarks for valid fields (optional polish)

**Validation Rules:**
- Income/Category name: Not blank
- Income/Category amount: Greater than 0, valid decimal format
- Step 2: Total allocation ≤ total income

---

### TICKET-6: Improve Step Navigation UX
**Priority:** Medium
**Type:** Enhancement
**Files:** `CreateBudgetScreen.kt`

**Description:**
Add step indicator UI and improve navigation feedback. Current "Step X of 3" text is basic.

**Acceptance Criteria:**
- [ ] Replace text with visual step indicator (dots or progress bar)
- [ ] Highlight current step
- [ ] Show completed steps (checkmark or different color)
- [ ] Add swipe gesture support for step navigation (optional)
- [ ] Prevent skipping steps (already implemented, verify)

**Design Reference:**
Follow glassmorphic design system in CLAUDE.md. Use primary color for active step, white 30% alpha for inactive.

---

### TICKET-7: Add Category Templates/Presets
**Priority:** Low
**Type:** Feature
**Files:** New file: `CategoryTemplates.kt`, `CreateBudgetUiState.kt`

**Description:**
Users start with 5 default categories. Enhance this with selectable templates based on budget type (student, family, single, etc.).

**Acceptance Criteria:**
- [ ] Create CategoryTemplate data class
- [ ] Define 3-4 preset templates (Student, Single Professional, Family, Custom)
- [ ] Add template selection on Step 2 entry (optional bottom sheet)
- [ ] Populate categories based on selected template
- [ ] Allow customization after template selection
- [ ] Persist user's preferred template

**Template Examples:**
- Student: Tuition, Food, Rent, Transport, Entertainment, Savings
- Family: Groceries, School Fees, Utilities, Insurance, Entertainment, Savings
- Single Professional: Rent, Groceries, Transport, Gym, Entertainment, Investments

---

### TICKET-8: Add Budget Period Customization
**Priority:** Low
**Type:** Feature
**Files:** `CreateBudgetViewModel.kt`, `CreateBudgetScreen.kt`

**Description:**
Currently budget period is determined from monthId. Allow users to customize start/end dates for irregular pay periods.

**Acceptance Criteria:**
- [ ] Add optional date pickers for budget period
- [ ] Default to calendar month
- [ ] Support custom periods (e.g., 15th to 15th for mid-month salaries)
- [ ] Validate: end date must be after start date
- [ ] Show period duration (e.g., "28 days")
- [ ] Update BudgetPeriod model if needed

---

### TICKET-9: Improve Category Allocation Keyboard Flow
**Priority:** Medium
**Type:** Enhancement
**Files:** `CategoryAllocationContent.kt`

**Description:**
Enhance keyboard navigation and input experience for category allocation. Currently basic TextField behavior.

**Acceptance Criteria:**
- [ ] Set proper IME actions (Next, Done)
- [ ] Focus moves to next field on "Next" action
- [ ] "Done" on last field closes keyboard
- [ ] Add support for Tab key navigation (desktop/tablet)
- [ ] Remember cursor position when editing
- [ ] Auto-select amount field content on focus (easier editing)

---

### TICKET-10: Add Summary Animations
**Priority:** Low
**Type:** Polish
**Files:** `ReviewBudgetContent.kt`

**Description:**
Add subtle animations to the review screen to make it more engaging and highlight important information.

**Acceptance Criteria:**
- [ ] Animate total income/budget values when entering screen
- [ ] Add slide-in animation for review sections
- [ ] Pulse animation on "Month End Goal" if buffer is high/low
- [ ] Color-code buffer (green if good, amber if low, red if negative)
- [ ] Add confetti or success animation on save (optional)

**Animation Guidelines:**
- Use 300ms tween (existing app pattern)
- Keep animations subtle, not distracting
- Follow Material 3 motion guidelines

---

## 🧪 Testing & Quality

### TICKET-11: Add Unit Tests for CreateBudgetViewModel
**Priority:** High
**Type:** Testing
**Files:** New file: `CreateBudgetViewModelTest.kt`

**Description:**
No tests exist for the budget creation logic. Add comprehensive unit tests.

**Test Coverage Required:**
- [ ] init() - Budget period initialization
- [ ] init() - Previous month rollover calculation
- [ ] Income source CRUD operations
- [ ] Category CRUD operations
- [ ] Validation logic (isStep1Valid, isStep2Valid)
- [ ] Save budget success/failure scenarios
- [ ] State transitions between steps
- [ ] Edge cases (empty lists, invalid amounts)

**Test Framework:**
Use Kotlin test, MockK for repository mocking

---

### TICKET-12: Add Compose UI Tests
**Priority:** Medium
**Type:** Testing
**Files:** New file: `CreateBudgetScreenTest.kt`

**Description:**
Add UI tests for the budget creation flow using Compose testing APIs.

**Test Scenarios:**
- [ ] Step 1: Add/remove income sources
- [ ] Step 1: Validate required fields
- [ ] Step 2: Add/remove categories
- [ ] Step 2: Over-allocation prevention
- [ ] Step 3: Review data accuracy
- [ ] Full flow: Create budget end-to-end
- [ ] Navigation: Back button behavior
- [ ] Navigation: Next button enabled/disabled states

---

### TICKET-13: Accessibility Improvements
**Priority:** Medium
**Type:** Enhancement
**Files:** All CreateBudget components

**Description:**
Improve accessibility for users with disabilities. Add proper semantics and content descriptions.

**Acceptance Criteria:**
- [ ] Add contentDescription to all icons
- [ ] Add semantics to custom components
- [ ] Ensure minimum touch target size (48dp)
- [ ] Test with TalkBack/VoiceOver
- [ ] Add role descriptions for screen reader
- [ ] Ensure proper focus order
- [ ] Test color contrast ratios (WCAG AA)

---

## 🏗️ Architecture & Code Quality

### TICKET-14: Extract Reusable Components
**Priority:** Low
**Type:** Refactoring
**Files:** Various

**Description:**
Several UI patterns are repeated and could be extracted into reusable components.

**Components to Extract:**
- [ ] AddItemButton (used for Add Income Source & Add Category)
- [ ] StepIndicator (new component for visual step tracking)
- [ ] FormField (wrapper for CustomTextField with label)
- [ ] ValidationMessage (error/success message component)

**Acceptance Criteria:**
- [ ] Create new components in `common/widgets/`
- [ ] Follow naming convention: `[Purpose]Composable.kt`
- [ ] Add Preview composables
- [ ] Update existing screens to use new components
- [ ] Ensure design consistency

---

### TICKET-15: Optimize State Management
**Priority:** Low
**Type:** Refactoring
**Files:** `CreateBudgetViewModel.kt`, `CreateBudgetUiState.kt`

**Description:**
Review and optimize state updates to prevent unnecessary recompositions.

**Optimization Opportunities:**
- [ ] Use derivedStateOf for computed values
- [ ] Consider splitting state into smaller, focused states
- [ ] Reduce unnecessary state copies
- [ ] Add state documentation
- [ ] Review update logic for efficiency

**Performance Targets:**
- Minimal recompositions on text input
- Smooth animations (60fps)
- Fast step transitions (<100ms)

---

### TICKET-16: Add Analytics Tracking
**Priority:** Low
**Type:** Feature
**Files:** `CreateBudgetViewModel.kt`

**Description:**
Add analytics to track user behavior in budget creation flow.

**Events to Track:**
- [ ] Budget creation started
- [ ] Step completed (1, 2, 3)
- [ ] Budget creation cancelled (which step)
- [ ] Budget saved successfully
- [ ] Budget save failed (error reason)
- [ ] Income sources added/removed (count)
- [ ] Categories added/removed (count)
- [ ] Template selected (if implemented)
- [ ] Average time spent per step

**Properties to Include:**
- Step number
- Income source count
- Category count
- Total income range (bucketed)
- Allocation percentage
- Has rollover (yes/no)

---

## 📝 Documentation

### TICKET-17: Add Component Documentation
**Priority:** Low
**Type:** Documentation
**Files:** All CreateBudget files

**Description:**
Add KDoc comments to classes and functions for better maintainability.

**Documentation Required:**
- [ ] Class-level KDoc for ViewModels
- [ ] Class-level KDoc for State classes
- [ ] Function-level KDoc for public composables
- [ ] Parameter descriptions for complex functions
- [ ] Usage examples for reusable components
- [ ] Update CLAUDE.md with budget creation patterns

**Template:**
```kotlin
/**
 * Step 1 of budget creation: Income sources input.
 *
 * Allows users to add multiple income sources with names and amounts.
 * Calculates total income and validates all fields before allowing progression.
 *
 * @param state Current budget creation state
 * @param onAction Callback for user actions
 * @param modifier Modifier for layout customization
 */
@Composable
fun IncomeSourceContent(...)
```

---

## 🎯 Priority Summary

### Must Do (P0 - High Priority)
1. TICKET-1: Fix Previous Month Rollover Calculation
2. TICKET-4: Improve Error Handling
3. TICKET-11: Add Unit Tests for CreateBudgetViewModel

### Should Do (P1 - Medium Priority)
4. TICKET-2: Fix Navigation Layout Issues
5. TICKET-3: Add Loading State Handling
6. TICKET-5: Add Input Validation Feedback
7. TICKET-6: Improve Step Navigation UX
8. TICKET-9: Improve Category Allocation Keyboard Flow
9. TICKET-12: Add Compose UI Tests
10. TICKET-13: Accessibility Improvements

### Nice to Have (P2 - Low Priority)
11. TICKET-7: Add Category Templates/Presets
12. TICKET-8: Add Budget Period Customization
13. TICKET-10: Add Summary Animations
14. TICKET-14: Extract Reusable Components
15. TICKET-15: Optimize State Management
16. TICKET-16: Add Analytics Tracking
17. TICKET-17: Add Component Documentation

---

## Estimated Effort

| Priority | Tickets | Estimated Days |
|----------|---------|----------------|
| High     | 3       | 4-5 days       |
| Medium   | 7       | 8-10 days      |
| Low      | 7       | 6-8 days       |
| **Total**| **17**  | **18-23 days** |

---

## Sprint Recommendations

### Sprint 1: Fix Critical Issues (1 week)
- TICKET-1: Fix rollover calculation
- TICKET-4: Error handling
- TICKET-2: Navigation layout
- TICKET-3: Loading states

### Sprint 2: Enhance UX (1 week)
- TICKET-5: Input validation feedback
- TICKET-6: Step navigation improvements
- TICKET-9: Keyboard flow
- TICKET-13: Accessibility

### Sprint 3: Testing & Quality (1 week)
- TICKET-11: Unit tests
- TICKET-12: UI tests
- TICKET-14: Extract components
- TICKET-15: Optimize state

### Sprint 4: Polish & Features (1 week)
- TICKET-7: Category templates
- TICKET-10: Animations
- TICKET-16: Analytics
- TICKET-17: Documentation

---

## Notes

- All tickets follow WealthBuilder design system (CLAUDE.md)
- Maintain existing glassmorphic aesthetic
- Use existing color tokens and spacing grid
- Follow Material 3 guidelines
- Keep performance in mind (target 60fps)
- Test on both Android and iOS

**Last Updated:** 2026-09-27