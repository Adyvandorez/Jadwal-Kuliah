# Project Plan

Create a personal college assistant Android app 'Jadwal Kuliah' with a personalized experience.
Key Updates:
1. Schedule (Jadwal): Sorted by day (Senin-Minggu). Detail screen with CRUD.
2. Tasks: Detail screen with Switch (status) top-right and Delete bottom-right.
3. Profile: User profile in Beranda (Name & Photo) and Settings (Edit Profile). Data saved via DataStore/Room.
4. UI: Coffee theme (#FFF4D6 BG, #E6C58A Card). All in Bahasa Indonesia.
5. Technical: MVVM, Room, StateFlow, Navigation, Coil for images.
6. UX: Safe deletion with confirmation dialogs. Clickable list items for details.

## Project Brief

# Project Brief: Jadwal Kuliah

**Jadwal Kuliah** adalah aplikasi asisten perkuliahan pribadi berbasis Android yang dirancang dengan estetika kustom yang hangat dan modern. Aplikasi ini berfokus pada pengalaman pengguna yang aman dan personal, membantu mahasiswa mengelola jadwal akademik, tugas, serta profil mereka dalam satu platform yang terintegrasi.

### Features
1.  **Manajemen Jadwal Kuliah (CRUD)**: Fitur pengelolaan jadwal mata kuliah lengkap dengan pengurutan otomatis berdasarkan hari (Senin-Minggu) serta konfirmasi keamanan saat menghapus data.
2.  **Manajemen Tugas & Catatan**: Pelacakan tugas mandiri yang dilengkapi dengan status penyelesaian (Switch), kategori, dan tenggat waktu untuk memastikan produktivitas akademik tetap terjaga.
3.  **Dashboard Beranda Personal**: Tampilan utama yang berpusat pada profil pengguna, menampilkan foto avatar, nama kustom, tanggal hari ini, serta ringkasan jadwal dan tugas mendatang.
4.  **Profil & Pengaturan Kustom**: Kontrol penuh atas identitas pengguna (Nama & Foto) serta preferensi aplikasi, termasuk beralih antara Mode Terang/Gelap secara manual dan pengaturan notifikasi.

### High-Level Technical Stack
*   **Kotlin**: Bahasa pemrograman utama untuk pengembangan aplikasi yang modern dan aman.
*   **Jetpack Compose**: Toolkit UI deklaratif untuk membangun antarmuka kustom dengan palet warna spesifik (#FFF4D6, #E6C58A) dan sudut melengkung (20-30dp).
*   **MVVM & Repository Pattern**: Arsitektur inti untuk memisahkan logika bisnis, manajemen data, dan UI secara bersih.
*   **Room Persistence (via KSP)**: Digunakan untuk penyimpanan data jadwal dan tugas secara lokal dengan performa tinggi.
*   **DataStore**: Untuk menyimpan preferensi pengaturan dan profil pengguna secara ringan dan persisten.
*   **StateFlow & Coroutines**: Menangani pembaruan data secara reaktif dan operasi latar belakang yang efisien.
*   **Navigation Compose**: Library standar untuk menangani perpindahan antar layar aplikasi secara mulus.
*   **Coil**: Digunakan untuk memuat dan menampilkan foto profil pengguna dengan optimal.

## Implementation Steps
**Total Duration:** 4h 36m 13s

### Task_10_UI_UX_Refinement: Update the color system to the new Light Mode palette (#FFF4D6 background), implement high contrast for important elements (Gold #B38922 for time/categories), and improve UX by repositioning delete buttons and adding confirmation dialogs.
- **Status:** COMPLETED
- **Updates:** Task 10: UI/UX Refinement completed. Color system updated with the new Light Mode palette (#FFF4D6 background). High contrast applied to time/categories using Gold accent (#B38922). Deletion UX improved with repositioned buttons and confirmation dialogs. All UI text in Bahasa Indonesia and verified with a successful build.
- **Acceptance Criteria:**
  - Light Mode uses #FFF4D6 background and #E6C58A cards
  - Contrast Gold #B38922 applied to time intervals, categories, and icons
  - Delete buttons are moved away from switches and use #D32F2F color
  - Confirmation dialog appears before any deletion
  - UI text is highly readable and in Bahasa Indonesia
- **Duration:** 4m 8s

### Task_11_DataLayerAndProfilePersistence: Implement the Room database for schedules and tasks, and set up DataStore for persisting user profile information (Name and Photo path). Create the Repository layer to manage data operations.
- **Status:** COMPLETED
- **Updates:** Task 11: Data layer and profile persistence implemented. Room entities for Jadwal and Tugas defined, DataStore for user profile (Name and Photo) set up with UserProfileRepository, and Jadwal sorting by day (Senin-Minggu) handled in DAO. Verified with a successful build.
- **Acceptance Criteria:**
  - Room entities for Jadwal and Tugas defined
  - DataStore implemented for user profile data
  - Repositories for managing data flow functional
- **Duration:** 4h 16m 45s

### Task_12_CoreFeatureImplementation: Develop the Beranda (profile/summary), Schedule management (sorted by day), and Task management screens. Implement Detail pages with CRUD logic, status switches, and Profile editing in Settings.
- **Status:** COMPLETED
- **Updates:** Task 12: Core UI features implemented. Beranda displays user profile and summaries, Schedules are sorted by day with Detail views and CRUD, Task details include status switches and safe deletion, and Edit Profil functionality is active in Settings. Verified with a successful build.
- **Acceptance Criteria:**
  - Beranda displays profile and task/schedule summary
  - Schedules sorted by day (Senin-Minggu) with CRUD functional
  - Task detail features Switch (top-right) and Delete (bottom-right)
  - Settings allows editing profile name and photo
  - List items navigate to detail views
- **Duration:** 15m 20s

### Task_13_RunAndVerify: Integrate Coil for profile image loading, perform final verification of application stability and requirements, and instruct critic_agent to verify alignment and stability.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Profile photo loading with Coil functional
  - Safe deletion confirmation dialogs implemented
  - App builds and runs without crashes
  - All existing tests pass
  - UI alignment with coffee aesthetic and Bahasa Indonesia localization verified
- **StartTime:** 2026-04-24 17:19:51 WIB

