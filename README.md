# COTTONS 💮

> "A cozy paper-craft planner where your tasks are notes, your focus sessions are cassette tapes, your finished days earn wax seals, and your ideas live on a sticker board."

Designed & Built for: **Rahul Shah / Editingcells**  
Offline-first Native Android application written in Kotlin and Jetpack Compose.

---

## 🎨 New Features & Enhancements

1. **Pure Circular App Icon (No Rectangle Shapes):**
   - Native launcher and adaptive icon redesigned into a pure circular wax seal emblem across all densities (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`) with transparent background corners.
2. **Smooth Multi-format Data Exports (PDF, CSV, TXT):**
   - **PDF Document:** Uses Android's native `PdfDocument` to generate a styled A4 report featuring summary statistics, task checklists with priority stars, cassette focus logs, habit streaks, and decorative stationery borders.
   - **CSV Table:** Clean UTF-8 table with sections for tasks, focus sessions, and habit counts.
   - **TXT Receipt:** Clean monospace formatted receipt with ASCII borders and progress breakdown.
   - Smooth system sharing with Android `FileProvider`.
3. **Dark Mode & Light Mode:**
   - Dedicated toggle in Settings: `☀️ Light`, `🌙 Dark`, and `⚙️ System Default`.
   - Authentic dark stationery palette with midnight check patterns, charcoal paper, and warm amber/rose accents.
4. **12 Cute Stationery Themes:**
   - *Picnic Red, Strawberry Milk, Matcha Latte, Teddy Brownie, Sakura Petal, Ocean Blvd, Blueberry Jam, Loly Pastel, Butter & Espresso, Forest Stamp, Midnight Study, Tin Case*.
5. **Built-in Typography Selector:**
   - Choose between `✍️ Cozy Handwritten (Cursive)`, `📖 Classic Editorial (Serif)`, `🖋️ Modern Clean (Sans)`, and `⌨️ Vintage Typewriter (Monospace)`.
6. **Import Pictures, Audio & Custom Stickers:**
   - **Import Pictures:** Zero-permission Android Photo Picker imports user photos directly onto the scrapbook canvas as polaroids or photo cards.
   - **Import Custom Stickers:** Users can pick any PNG image from files to add as a custom die-cut sticker into their scrapbook tray.
   - **Import Audio:** Users can import custom audio files to loop as custom cassette tape background sound.
7. **Full Notification Access & Permissions:**
   - Declared and runtime-requested `POST_NOTIFICATIONS` with status indicators and permission triggers.

---

## 🛠️ Build & Verification

- Target SDK: 36
- Min SDK: 24
- Unit & Robolectric Tests: `gradle :app:testDebugUnitTest` (ALL PASSED)
- Compilation: `compile_applet` (BUILD SUCCESSFUL)
