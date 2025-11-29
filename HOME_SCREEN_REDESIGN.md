# Home Screen Redesign - November 25, 2025

## Overview
HomeScreen telah diubah dari tampilan "Riwayat Berat Badan" menjadi dashboard health tracker yang modern dan interaktif sesuai dengan design yang diberikan.

---

## 🎨 Design Elements

### 1. **Background Header**
- **File:** `bglanding.jpg` dari drawable
- **Implementasi:** 
  ```kotlin
  Image(
      painter = painterResource(id = R.drawable.bglanding),
      contentScale = ContentScale.Crop,
      height = 280.dp
  )
  ```
- Background menampilkan ilustrasi langit/rumah di bagian atas

### 2. **Greeting Section**
- **Text:** "Hello, everyone!"
- **Style:** 24sp, Bold, Black color
- **Position:** 60dp dari top

### 3. **Search Bar**
- **Design:** Rounded corner (50.dp radius)
- **Background:** White dengan border transparent
- **Icon:** Search icon (Material Icons)
- **Placeholder:** "Search"

### 4. **Water Reminder Card**
- **Background Color:** `#A9BF93` (Hijau muda/olive)
- **Height:** 100dp
- **Corner Radius:** 16dp
- **Content:**
  - Text: "Udah minum hari ini?"
  - Subtext: "Jangan sampai minum air gelas pertama!"
  - Icon: `botolpink.png` (70dp size)
- **Layout:** Row dengan SpaceBetween

### 5. **Health Metrics Grid**
- **Layout:** LazyVerticalGrid dengan 2 kolom
- **Spacing:** 12dp horizontal & vertical
- **Card Size:** 120dp height

#### Health Metric Cards:
| Value | Unit | Background Color | Icon |
|-------|------|------------------|------|
| 160 | cm | `#89C2FF` (Light Blue) | jerapah.png |
| 120/80 | - | `#3F99F8` (Blue) | None |
| 45 | - | `#FF8989` (Pink) | None |
| 100 | mg/dL | `#89FFD0` (Mint) | None |
| 180 | kcal | `#C889FF` (Purple) | None |
| 70 | kg | `#FF8989` (Pink) | timbangan.png |

### 6. **Bottom Navigation Bar**
- **Background:** `#F5F5F5` (Light Gray)
- **Items:**
  1. **Home** - Selected (Circular background `#738A45`, White icon)
  2. **Calendar** - Unselected (Gray icon)
  3. **Profile** - Unselected (Gray icon)

---

## 📦 Assets Used

### From drawable folder:
- ✅ `bglanding.jpg` - Background header image
- ✅ `botolpink.png` - Water bottle illustration
- ✅ `jerapah.png` - Height/growth metric icon
- ✅ `timbangan.png` - Weight scale icon

### Not used (available for future features):
- `anaksenam.png`
- `botolputih.png`
- `brokoli.png`
- `termometer.png`
- `ukuran.png`

---

## 🎨 Color Palette

### Primary Colors:
- **Primary Green:** `#738A45` - Navigation active, main theme
- **Light Green:** `#A9BF93` - Water reminder card

### Health Metric Colors:
- **Light Blue:** `#89C2FF` - Height metric
- **Blue:** `#3F99F8` - Blood pressure
- **Pink/Red:** `#FF8989` - Heart rate, weight
- **Mint:** `#89FFD0` - Blood sugar
- **Purple:** `#C889FF` - Calories

### UI Colors:
- **White:** `#FFFFFF` - Cards, search bar
- **Light Gray:** `#F5F5F5` - Bottom nav background
- **Gray:** `Color.Gray` - Inactive icons, placeholder
- **Black:** `Color.Black` - Text

---

## 🏗️ Component Structure

```
HomeScreen (Scaffold)
├── BottomNavigationBar
└── Box (Main Content)
    ├── Image (Background Header)
    └── Column
        ├── Spacer (60dp)
        ├── Text (Greeting)
        ├── OutlinedTextField (Search Bar)
        ├── Card (Water Reminder)
        │   └── Row
        │       ├── Column (Text)
        │       └── Image (Botol)
        └── LazyVerticalGrid (Health Metrics)
            └── HealthMetricCard (x6)
                └── Box
                    ├── Column (Value + Unit)
                    └── Image (Icon - optional)
```

---

## 📊 Data Models

### HealthMetric Data Class
```kotlin
data class HealthMetric(
    val value: String,      // Nilai utama (e.g., "160")
    val unit: String,       // Unit (e.g., "cm", "kg")
    val label: String = "", // Label tambahan (future use)
    val backgroundColor: Color, // Warna background card
    val icon: Int? = null   // Resource ID untuk icon
)
```

---

## 🚀 Features

### Implemented:
- ✅ Modern dashboard layout
- ✅ Search functionality (UI ready, logic not implemented)
- ✅ Water reminder card
- ✅ 6 health metrics display
- ✅ Bottom navigation bar
- ✅ Responsive grid layout
- ✅ Custom icons for specific metrics

### Future Enhancements:
- [ ] Search functionality implementation
- [ ] Click handlers for health metric cards
- [ ] Navigation between screens via bottom nav
- [ ] Water intake tracker integration
- [ ] Real-time data from database
- [ ] Add/Edit functionality for metrics
- [ ] Animations and transitions
- [ ] Pull-to-refresh

---

## 🎯 User Flow

1. User opens app → Sees HomeScreen with greeting
2. Can search health info via search bar
3. Water reminder card prompts hydration
4. Health metrics displayed in grid:
   - Height (160 cm)
   - Blood Pressure (120/80)
   - Heart Rate (45)
   - Blood Sugar (100 mg/dL)
   - Calories (180 kcal)
   - Weight (70 kg)
5. Bottom navigation allows switching between Home, Calendar, Profile

---

## 📱 Screen Specifications

### Layout:
- **Padding horizontal:** 20dp
- **Card corner radius:** 16dp
- **Grid columns:** 2
- **Grid spacing:** 12dp
- **Search bar radius:** 50dp (fully rounded)

### Typography:
- **Greeting:** 24sp, Bold
- **Water reminder title:** 16sp, Bold
- **Water reminder subtitle:** 11sp
- **Metric value:** 32sp, Bold
- **Metric unit:** 14sp, Medium
- **Search placeholder:** Default, Gray

---

## 🔧 Technical Details

### Compose Components Used:
- `Scaffold` - Main layout structure
- `LazyVerticalGrid` - Efficient grid for metrics
- `Card` - Elevated containers
- `OutlinedTextField` - Search input
- `NavigationBar` - Bottom navigation
- `Image` - Background and icons
- `Box` - Layout positioning

### Modifiers:
- `fillMaxSize()` - Full screen
- `padding()` - Spacing
- `clip()` - Rounded corners
- `background()` - Colors
- `size()` - Dimensions
- `align()` - Positioning

---

## ✅ Testing Checklist

- [x] Background image displays correctly
- [x] Greeting text visible
- [x] Search bar functional (UI)
- [x] Water reminder card displays with icon
- [x] All 6 health metrics show correct values
- [x] Grid layout responsive
- [x] Icons display on height and weight cards
- [x] Bottom navigation shows correctly
- [x] Home icon highlighted with green circle
- [x] No compile errors
- [x] Proper spacing throughout

---

## 📸 Design Comparison

### Original Design (Riwayat Berat Badan):
- Simple list view
- Weight logs from database
- Loading states
- Error handling

### New Design (Health Dashboard):
- Modern card-based UI
- Multiple health metrics
- Interactive elements
- Visual hierarchy
- Better UX

---

## 🎨 Design Philosophy

1. **Visual Hierarchy** - Important info (greeting, water reminder) at top
2. **Color Coding** - Different colors for different metric types
3. **Consistency** - Rounded corners, consistent spacing
4. **Accessibility** - Clear text, good contrast
5. **Modularity** - Reusable components (HealthMetricCard)

---

## 💡 Notes

- Original weight log functionality has been replaced
- If weight tracking needed, create separate screen via navigation
- All assets from drawable folder are ready to use
- Color palette matches modern health app standards
- Bottom navigation prepared for multi-screen navigation

---

## 📝 Future Considerations

1. **Data Integration:** Connect health metrics to real data sources
2. **Animations:** Add subtle animations for card appearance
3. **Customization:** Allow users to rearrange/customize metrics
4. **More Metrics:** Add heart icon, water tracking, steps, etc.
5. **Charts:** Add trend visualization for each metric
6. **Notifications:** Water reminder notifications
7. **Settings:** Customize units (kg/lbs, cm/ft)

---

**Status:** ✅ Complete and Ready for Testing


