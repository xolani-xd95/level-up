# WealthBuilder - Figma Design System Setup Guide

## Overview
This guide will walk you through setting up the complete WealthBuilder design system in Figma from scratch.

---

## Step 1: Create New Figma File

1. **Open Figma** and create a new Design file
2. **Name it:** "WealthBuilder - Design System v2.0"
3. **Set canvas to dark mode** (View → Canvas → Dark)

---

## Step 2: File Structure

Create the following pages in your Figma file:

### Page 1: 🎨 Foundations
- Color Primitives
- Color Tokens (Dark Mode)
- Color Tokens (Light Mode - Future)
- Typography Scale
- Spacing System
- Border Radius
- Elevation/Shadows
- Icons

### Page 2: 🧩 Components
- Buttons
- Cards
- Text Fields
- Navigation
- Progress Indicators
- Empty States
- Modals/Bottom Sheets

### Page 3: 📱 Screens - Finance
- Budget Dashboard
- Budget Overview
- Transactions
- Create Budget Flow
- Goals

### Page 4: 📱 Screens - Journal
- Journal Home
- Day Detail
- Trade Entry

### Page 5: 📱 Screens - Habits
- Today Check-in
- Habit Details

### Page 6: 📋 Documentation
- Usage Guidelines
- Do's and Don'ts
- Accessibility Notes

---

## Step 3: Import Design Tokens (Recommended)

### Option A: Using Tokens Studio Plugin

1. **Install "Tokens Studio for Figma"** plugin
2. **Import the design-tokens.json** file from the project
3. **Apply tokens** to your file
4. The plugin will create all color scales, spacing, typography automatically

### Option B: Manual Setup (Instructions below)

Continue with manual setup if you prefer full control.

---

## Step 4: Color Primitives (Manual Setup)

### Go to Page 1: 🎨 Foundations

Create color swatches for each primitive scale:

#### Blue Scale
Create a frame "Blue Scale" with 10 color swatches:
- Blue/50: `#E3F2FD`
- Blue/100: `#BBDEFB`
- Blue/200: `#90CAF9`
- Blue/300: `#64B5F6`
- Blue/400: `#42A5F5`
- **Blue/500: `#1E88E5`** ← Current Primary (Mark with ⭐)
- Blue/600: `#1976D2`
- Blue/700: `#1565C0`
- Blue/800: `#0D47A1`
- Blue/900: `#0A3D91`

**How to create swatches:**
1. Create a rectangle (100×100px)
2. Fill with the color
3. Add text label below (e.g., "Blue/500")
4. Add hex value as description
5. Group each swatch with its label
6. Arrange in a row

#### Gold Scale
Create a frame "Gold Scale":
- Gold/50: `#FFF9E5`
- Gold/100: `#FFF3CC`
- Gold/200: `#FFE699`
- Gold/300: `#FFD966`
- Gold/400: `#E5C44A`
- **Gold/500: `#D4AF37`** ← Current Accent (Mark with ⭐)
- Gold/600: `#B8992F`
- Gold/700: `#9C8327`
- Gold/800: `#806D1F`
- Gold/900: `#665717`

#### Green Scale (Success)
Create a frame "Green Scale":
- Green/50: `#E8F5E9`
- Green/100: `#C8E6C9`
- Green/200: `#A5D6A7`
- Green/300: `#81C784`
- Green/400: `#66BB6A`
- **Green/500: `#00C853`** ← Success (Mark with ✓)
- Green/600: `#00B248`
- Green/700: `#009C3D`
- Green/800: `#008632`
- Green/900: `#007027`

#### Red Scale (Error)
Create a frame "Red Scale":
- Red/50: `#FFEBEE`
- Red/100: `#FFCDD2`
- Red/200: `#EF9A9A`
- Red/300: `#E57373`
- Red/400: `#EF5350`
- **Red/500: `#FF2400`** ← Error (Mark with ⚠)
- Red/600: `#E62000`
- Red/700: `#CC1C00`
- Red/800: `#B31800`
- Red/900: `#991400`

#### Orange Scale (Warning)
Create a frame "Orange Scale":
- Orange/50: `#FFF3E0`
- Orange/100: `#FFE0B2`
- Orange/200: `#FFCC80`
- Orange/300: `#FFB74D`
- Orange/400: `#FFA726`
- **Orange/500: `#FFA500`** ← Warning (Mark with ⚠)
- Orange/600: `#E69500`
- Orange/700: `#CC8500`
- Orange/800: `#B37500`
- Orange/900: `#996500`

#### Neutral Scale
Create a frame "Neutral Scale":
- Neutral/0: `#FFFFFF` (Pure White)
- Neutral/50: `#FAFAFA`
- Neutral/100: `#F5F5F5`
- Neutral/200: `#EEEEEE`
- Neutral/300: `#E0E0E0`
- Neutral/400: `#BDBDBD`
- Neutral/500: `#9E9E9E`
- Neutral/600: `#757575`
- Neutral/700: `#616161`
- Neutral/800: `#424242`
- **Neutral/850: `#363636`** (Background gradient)
- **Neutral/875: `#1C1C1E`** (Bottom sheet)
- **Neutral/880: `#1B1B1B`** (Background gradient)
- Neutral/900: `#121212`
- Neutral/950: `#000000` (Pure Black)

---

## Step 5: Set Up Figma Variables (Recommended)

### Create Variable Collections

1. **Go to Variables panel** (Right sidebar)
2. **Create a new collection:** "WealthBuilder Colors"
3. **Add modes:** "Dark" (default) and "Light" (future)

### Create Semantic Color Variables

#### Background Variables (Dark Mode)
- `bg/primary`: `#000000`
- `bg/secondary`: `#121212`
- `bg/gradient/start`: `#000000`
- `bg/gradient/mid-1`: `#1B1B1B`
- `bg/gradient/mid-2`: `#495057`
- `bg/gradient/mid-3`: `#363636`
- `bg/gradient/end`: `#000000`

#### Surface Variables (Dark Mode)
- `surface/primary`: White with 5% opacity
- `surface/secondary`: White with 10% opacity
- `surface/tertiary`: White with 12% opacity
- `surface/input`: White with 8% opacity
- `surface/modal`: `#1C1C1E`
- `surface/navigation`: Gray with 50% opacity
- `surface/selected`: White with 5% opacity

#### Text Variables (Dark Mode)
- `text/primary`: White 100%
- `text/secondary`: White 60%
- `text/tertiary`: White 40%
- `text/placeholder`: White 27%
- `text/disabled`: White 20%
- `text/muted`: White 50%

#### Border Variables (Dark Mode)
- `border/subtle`: White 10%
- `border/default`: White 20%
- `border/strong`: White 30%
- `border/transparent`: Transparent

#### Action Variables (Dark Mode)
- `action/primary`: `{Blue/500}`
- `action/primary-hover`: `{Blue/600}`
- `action/primary-disabled`: Blue with 5% opacity
- `action/accent`: `{Gold/500}`
- `action/success`: `{Green/500}`
- `action/error`: `{Red/500}`
- `action/warning`: `{Orange/500}`

**Tip:** Use aliases (references) to primitive colors when possible.

---

## Step 6: Typography System

Create a frame "Typography Scale" with the following styles:

### Font
**Default:** System font (SF Pro for iOS, Roboto for Android)
Or choose a custom font if desired.

### Text Styles to Create

#### Display
- **Display Large:** 40px / Medium (600) / 1.2 line height
- **Display Medium:** 36px / Medium / 1.2
- **Display Small:** 32px / Medium / 1.2

#### Headline
- **Headline Large:** 32px / Regular (400) / 1.25
- **Headline Medium:** 28px / Regular / 1.25
- **Headline Small:** 24px / Regular / 1.25 (Used: App bar titles)

#### Title
- **Title Large:** 22px / Medium / 1.3 (Used: Major headings)
- **Title Medium:** 20px / Medium / 1.3 (Used: Section headers, card titles)
- **Title Small:** 18px / Medium / 1.4

#### Body
- **Body Large:** 18px / Regular / 1.5
- **Body Medium:** 16px / Regular / 1.5 (Used: Body text)
- **Body Small:** 14px / Regular / 1.5

#### Label
- **Label Large:** 16px / Medium / 1.4 (Used: Emphasized labels)
- **Label Medium:** 14px / Medium / 1.4 (Used: Small labels, bottom nav)
- **Label Small:** 12px / Medium / 1.4

#### Input
- **Input Text:** 15px / Regular / 1.0 (Used: Text fields)

**Color for Dark Mode:** Apply white (`#FFFFFF`) to all text styles.

### Create as Figma Text Styles
1. Create a text layer with each size/weight
2. Right-click → "Create style"
3. Name using pattern: "Typography/[Category]/[Size]"
4. Example: "Typography/Headline/Small"

---

## Step 7: Spacing System

Create a frame "Spacing Scale" with visual representations:

Use rectangles to show each spacing value:

- **0:** 0px
- **1:** 2px (Minimal)
- **2:** 4px (Tight)
- **3:** 6px (Progress bar height)
- **4:** 8px (Small spacing)
- **5:** 10px (Medium spacing)
- **6:** 12px (Card padding, standard)
- **7:** 16px (Large spacing)
- **8:** 18px (Bottom nav bottom padding)
- **9:** 24px (Extra large)
- **10:** 32px (Section spacing)
- **11:** 40px
- **12:** 48px
- **13:** 64px
- **14:** 80px (Bottom nav clearance)

**Visual:** Create small colored rectangles with width/height matching the spacing value.

---

## Step 8: Border Radius

Create a frame "Border Radius Scale":

Show rectangles with each corner radius applied:

- **None:** 0px
- **SM:** 4px
- **MD:** 8px (Small components, inputs, nav bar)
- **LG:** 12px (Cards - standard)
- **XL:** 16px (Bottom nav items)
- **2XL:** 24px
- **Full:** 999px (Pills, progress bars)

---

## Step 9: Create Component Library

### Page 2: 🧩 Components

#### Card Component

**Variant 1: Default Card**
1. Create a rectangle: 343×200px (mobile width)
2. **Fill:** Apply `surface/primary` variable OR:
   - Solid fill: White at 5% opacity (`#FFFFFF` with 5% opacity)
3. **Border:**
   - Width: 0.5px
   - Create gradient border effect (advanced):
     - Use effect/layer stroke
     - Gradient from White 30% to White 10%
   - OR simple: White at 10% opacity
4. **Corner radius:** 12px
5. **Padding:** 12px all sides
6. **Add auto-layout** with:
   - Direction: Vertical
   - Spacing: 8px
   - Padding: 12px
7. **Create component** (Cmd/Ctrl + Alt + K)
8. **Name:** "Card/Default"

**Add placeholder content:**
- Title text (Title Medium style)
- Body text (Body Medium style)

**Variant 2: Selected Card**
- Duplicate the card component
- Add subtle accent glow or border
- Name: "Card/Selected"

#### Text Field Component

**Default State:**
1. Rectangle: 343×48px
2. **Fill:** `surface/input` OR White 8% opacity
3. **Border:** 1px, Transparent
4. **Corner radius:** 8px
5. **Padding:** 10px horizontal, 12px vertical
6. **Add text layer:** "Placeholder text"
   - Style: Input Text (15px)
   - Color: `text/placeholder` (White 27%)

**Focused State:**
Create variant with:
- **Border:** 1px, `action/primary` (Blue)
- **Border glow:** Optional outer shadow

**Error State:**
Create variant with:
- **Border:** 1px, `action/error` (Red)

**Disabled State:**
Create variant with:
- **Fill:** White 5% opacity
- **Text:** `text/disabled`

**Create component set:**
1. Select all variants
2. Right-click → "Create component set"
3. Name: "TextField"
4. Add property: "State" (Default, Focused, Error, Disabled)

#### Button Component

**Primary Button:**
1. Rectangle with auto-layout
2. **Fill:** `action/primary` (Blue 80% opacity)
3. **Corner radius:** 12px
4. **Padding:** 16px horizontal, 12px vertical
5. **Text:** "Button Text" (Label Large, White)
6. **Create component**

**Variants:**
- Primary/Default
- Primary/Hover (Blue 100%)
- Primary/Pressed (Blue 700)
- Primary/Disabled (Blue 5% opacity, text 40% opacity)
- Secondary (White 10% background)
- Tertiary (Transparent)
- Destructive (Red)

#### Progress Indicator

**Linear Progress:**
1. **Track:** Rectangle 343×6px
   - Fill: White 20% opacity
   - Corner radius: Full (999px)
2. **Fill:** Rectangle inside track
   - Fill: `action/primary`
   - Corner radius: Full
   - Width: Variable (show at 50%)
3. **Group and create component**
4. Add variants for different progress values

**Circular Progress:**
- Create using circles and strokes
- Primary color stroke
- Track stroke at 20% opacity

#### Bottom Navigation Bar

1. **Container:** Rectangle 375×72px (iPhone width)
   - Fill: Gray 50% opacity OR `surface/navigation`
   - Corner radius: 8dp top corners only
   - Padding: 12px horizontal, 18px bottom, 8px top

2. **Navigation Items:** Auto-layout row
   - 3-4 items evenly spaced
   - Each item:
     - Icon (24×24px)
     - Label (Label Medium, 12px)

3. **States:**
   - **Unselected:**
     - Icon: White 50% opacity
     - No label (or very subtle)
     - Scale: 1.0
   - **Selected:**
     - Icon: Gold (`#D4AF37`)
     - Label: Gold, visible
     - Background: White 5% opacity
     - Corner radius: 16px
     - Scale: 1.1

4. **Create component set** with state property

#### Empty State Component

1. **Container:** Center-aligned column
2. **Icon:** 64×64px
   - Color: White 20% opacity
3. **Title:** Title Large, White
4. **Description:** Body Medium, White 60%
5. **Optional CTA button**

---

## Step 10: Gradient Backgrounds

Create background options for screens:

### gradientOption0 (Current)
1. Create a frame: 375×812px (iPhone size)
2. **Fill:** Linear gradient
   - Angle: Top-left to bottom-right (diagonal)
   - Stops:
     - 0%: `#000000`
     - 14%: `#1B1B1B`
     - 29%: `#495057`
     - 43%: `#363636`
     - 57%: `#363636`
     - 71%: `#495057`
     - 86%: `#1B1B1B`
     - 100%: `#000000`
3. **Save as style:** "Background/Gradient/Default"

### Other Options
Create additional gradient variations (Option 1-4 from LevelUpTheme.kt) as background styles.

---

## Step 11: Screen Mockups

### Page 3: 📱 Screens - Finance

#### Budget Dashboard Screen Template

1. **Create frame:** 375×812px (iPhone 13 size)
2. **Apply background gradient** style
3. **Add status bar** (20px height, white text/icons)
4. **Add content area:**
   - Top padding: 8px
   - Side padding: 12px
   - Bottom padding: 80px (nav clearance)

5. **Add components:**
   - Title: "Budget Dashboard" (Headline Small)
   - Month summary card (use Card component)
   - Goals section
   - Goal cards
   - Bottom navigation

6. **Organize in auto-layout** for responsiveness

#### Repeat for Other Screens

Follow similar pattern for:
- Budget Overview
- Transactions
- Create Budget Flow
- Journal screens
- Habits screens

---

## Step 12: Documentation

### Page 6: 📋 Documentation

Create frames with:

#### Color Usage Guidelines
- When to use each semantic color
- Accessibility requirements
- Do's and Don'ts with examples

#### Component Usage
- When to use each component
- Spacing rules
- State management

#### Accessibility Notes
- Contrast ratios achieved
- Color-blind considerations
- Screen reader support

---

## Step 13: Organize and Share

1. **Create a cover page** with app logo and description
2. **Add thumbnails** to each page for quick navigation
3. **Organize layers** with proper naming
4. **Add comments** where needed
5. **Share with development team**
6. **Set up Figma Inspect** for developers to extract values

---

## Step 14: Export for Development

### Export Design Tokens
If using Tokens Studio:
1. Export JSON file
2. Save as `design-tokens.json` (already created in project)
3. Share with development team

### Export Assets
1. **Icons:** Export as SVG (24×24px)
2. **Images:** Export as PNG @2x, @3x
3. **Place in:** `composeApp/src/commonMain/composeResources/drawable/`

### Create Handoff Specs
1. Use Figma's Dev Mode
2. Add measurements and spacing annotations
3. Document interaction states
4. Note animation timings

---

## Tips for Success

### Use Figma Best Practices
- ✅ Use auto-layout for all components
- ✅ Create reusable components
- ✅ Use variants for component states
- ✅ Name layers clearly and consistently
- ✅ Use Figma variables for colors
- ✅ Document everything

### Maintain Consistency
- Always use color variables, not hardcoded values
- Follow spacing scale strictly
- Use text styles, never manual formatting
- Test accessibility as you design

### Collaborate Effectively
- Add comments for questions
- Use version history
- Create branches for experiments
- Share prototypes for feedback

---

## Next Steps After Figma Setup

1. **Review designs** with stakeholders
2. **Test accessibility** (contrast checker plugins)
3. **Create prototypes** for user flows
4. **Get approval** before development
5. **Begin development migration** (Ticket 14+)

---

## Useful Figma Plugins

- **Tokens Studio:** Import/export design tokens
- **Stark:** Accessibility checker
- **Color Blind:** Simulate color blindness
- **Contrast:** Check WCAG contrast ratios
- **Iconify:** Insert icons
- **Auto Flow:** Create flowcharts
- **Figma to Code:** Generate code snippets

---

## Questions or Issues?

Refer to:
- `design-tokens.json` for exact values
- `CLAUDE.md` for current codebase patterns
- `TRELLO_TICKETS.md` for task breakdown

---

**Created:** 2026-09-22
**For:** WealthBuilder Design System v2.0
**Status:** Ready for implementation
