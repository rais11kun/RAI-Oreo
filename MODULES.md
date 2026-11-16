# Pemetaan Modul Praktikum pada Aplikasi (Pertemuan 2–10)

Aplikasi **tidak** memakai layar demo berlabel "Modul 2" dan seterusnya. Semua materi
diterapkan langsung pada fitur aplikasi, dan setiap file yang relevan diberi komentar:

```kotlin
/**
 * Penerapan praktikum: Modul 3 (Intent + MaterialCardView), Modul 5 (Option Menu).
 */
```

Cara memeriksa cepat: cari komentar tersebut di Android Studio (`Ctrl+Shift+F` → "Penerapan praktikum").

---

## Modul 2 — Activity, Components, Constraint, Event Listener

| Item | Bukti di proyek |
|---|---|
| Activity | 24 Activity terdaftar di `AndroidManifest.xml` (`SplashActivity`, `LoginActivity`, `screen/*`) |
| Components | `activity_login.xml` (EditText, CheckBox, Button), `activity_pengaturan.xml` (SwitchMaterial), `activity_form_pemesanan.xml` (AutoCompleteTextView, RadioGroup/RadioButton, Spinner), `activity_katalog` (Spinner), `activity_dashboard_pemilik` (ProgressBar) |
| ConstraintLayout | `activity_pesanan_sukses.xml` — guideline horizontal + vertical chain + constraint antar elemen |
| Event `onClick` | `LoginActivity.btnMasuk`, `DetailLayananActivity.btnPesan`, `FormPemesananActivity` (stepper jumlah), item adapter (`setOnClickListener`) |
| Komentar kode | `LoginActivity.kt`, `PesananSuksesActivity.kt`, `FormPemesananActivity.kt` |

## Modul 3 — ViewBinding, Resource, Intent, Intent Data, LifeCycle, Dialog, MaterialCardView

| Item | Bukti |
|---|---|
| ViewBinding | `buildFeatures { viewBinding = true }`; 40+ binding dipakai di semua Activity/Fragment/adapter (`ActivityLoginBinding`, `FragmentHomeBinding`, `ItemPesananBinding`, …) |
| Attribute components | `inputType`, `maxLines`, `hint`, `checked`, `completionThreshold`, `entries` pada layout |
| Resource | `res/values/colors.xml`, `dimens.xml`, `strings.xml`, `styles.xml`, `arrays.xml`; font Poppins di `res/font`; gambar PNG di `res/drawable-nodpi` |
| Intent (pindah layar) | `ProfilFragment.jalankan()`, `MainActivity`, seluruh `screen/*` memakai `startActivity(Intent(...))` |
| Intent kirim data | `DetailPesananActivity.EXTRA_NOMOR`, `DetailAntrianActivity`, `DetailLayananActivity` (`serviceId`), `PesananSuksesActivity` (nomor pesanan) |
| LifeCycle | `MainActivity`/Fragment memakai `onCreate`, `onResume`, `onDestroyView`; data disegarkan di `onResume` |
| Dialog & PopUp | `AlamatActivity` (dialog tambah alamat), `ProfilFragment` (dialog keluar), `DetailPesananActivity` (dialog batalkan), `KonfirmasiPesananActivity` (konfirmasi buat pesanan), `LoginActivity` (dialog sidik jari), `FormPemesananActivity` (`DatePickerDialog`) |
| MaterialCardView | `activity_detail_layanan.xml`, `activity_detail_pesanan.xml`, `item_produk.xml`, `item_pesanan.xml`, kartu ringkasan di form & konfirmasi |

## Modul 4 — MaterialCardView (pendalaman)

`Kartu` dipakai sebagai elemen utama di hampir seluruh layar: kartu layanan (grid & baris),
kartu pesanan (ListView), kartu ringkasan biaya, kartu status, kartu statistik dashboard.
Atribut yang dipakai: `cardCornerRadius`, `cardElevation`, `strokeColor`, `strokeWidth`,
`cardBackgroundColor`, `rippleColor` — dibungkus style `Widget.App.Card`.

## Modul 5 — Toolbar, Option Menu, Scrollable, WebView, Vector Asset

| Item | Bukti |
|---|---|
| Toolbar | `MaterialToolbar` di 18 layar, style `Widget.App.Toolbar` |
| Option Menu | `res/menu/menu_toolbar.xml` (Bagikan, Bantuan), `menu_notifikasi.xml`, `menu_kelola.xml`; ditangani `setOnMenuItemClickListener` |
| Scrollable | `NestedScrollView`/`ScrollView` (Home, Profil, Detail Layanan, Form, Konfirmasi, Detail Pesanan, Pengaturan, Bantuan), `HorizontalScrollView` untuk chip kategori |
| WebView | `BantuanActivity` + `assets/bantuan.html` (halaman FAQ lokal, JavaScript & zoom) |
| Gambar PNG (bukan vector) | seluruh `res/drawable-nodpi/*.png` termasuk 40+ ikon PNG |

## Modul 6 — SharedPreferences, Splash Screen

| Item | Bukti |
|---|---|
| SharedPreferences | `data/Prefs.kt` (`MODE_PRIVATE`): sesi email/nama, "ingat saya", preferensi notifikasi & sidik jari, jumlah peluncuran. Dipakai `SplashActivity`, `LoginActivity`, `PengaturanActivity` |
| Splash Screen | `SplashActivity` + `activity_splash.xml` + `Theme.Simper.Splash`; `Handler.postDelayed(1500)` lalu Intent; Activity ini adalah **launcher** |

## Modul 7 — Activity & Fragment, Bottom Navigation

| Item | Bukti |
|---|---|
| Activity & Fragment | `MainActivity` menampung `FragmentContainerView`; 4 Fragment di `fragment/` |
| Penggunaan Fragment | `supportFragmentManager.beginTransaction().replace(...)` pada tiap perpindahan tab; tiap Fragment punya layout sendiri dan `onDestroyView` membersihkan binding |
| Bottom Navigation | `BottomNavigationView` + `res/menu/menu_bottom.xml` (Home, Katalog, Pesanan, Profil), ikon PNG, `itemIconTint` selector |

## Modul 10 — ListView, ListView Custom Adapter, Widget lain

| Item | Bukti |
|---|---|
| ListView | `PesananListFragment.lvPesanan` (satu halaman ViewPager2), `NotifikasiActivity.lvNotif`, `AntrianActivity.lvAntrian`, `HomeFragment.lvSaran` (saran pencarian) |
| ListView custom adapter | `PesananAdapter`, `NotifikasiAdapter` — `BaseAdapter` + pola ViewHolder + **ViewBinding** + layout baris (`item_pesanan.xml`, `item_notifikasi.xml`) |
| Widget lain | RecyclerView grid (Katalog, 2 kolom), RecyclerView horizontal (Home), Spinner (katalog, laporan, status antrian), AutoCompleteTextView (bahan/finishing/kategori), SwitchMaterial, RadioGroup, ProgressBar, TabLayout, ChipGroup, DatePickerDialog, QR scanner |

## Modul 11 — TabLayout, RecyclerView, Onboard Screen (ViewPager2)

| Item | Bukti di proyek |
|---|---|
| Onboard Screen | `OnboardingActivity` + `activity_onboarding.xml`: ViewPager2 tiga halaman (`fragment_onboarding_page.xml`), indikator titik, tombol *Berikutnya → Mulai*, tautan *Lewati*. Tampil **sekali** setelah splash karena ditandai `Prefs.onboardingSelesai` |
| ViewPager2 | `pagerOnboarding` (onboarding) dan `pagerPesanan` (tab Pesanan), keduanya diisi `FragmentStateAdapter`: `OnboardingAdapter` dan `PesananPagerAdapter` |
| TabLayout + mediator | `fragment_pesanan.xml` + `PesananFragment`: `TabLayoutMediator(binding.tabStatus, binding.pagerPesanan)` — tab Aktif/Selesai/Dibatalkan mengendalikan halaman, dan menggeser halaman memindahkan tab |
| RecyclerView | 8 adapter di `adapter/` untuk 9 layout: grid 2 kolom (Katalog), horizontal (Home), daftar vertikal (Profil, Laporan, Kelola Layanan, Alamat, Detail Pesanan, Detail Antrian) |
| Gambar | ilustrasi onboarding `onboarding_1.png`–`onboarding_3.png`, ikon `ic_fingerprint.png`, `ic_eye_off.png` — semuanya PNG, bukan vector |

Komentar `Modul 11` ada di: `OnboardingActivity.kt`, `OnboardingAdapter.kt`, `OnboardingPageFragment.kt`,
`PesananFragment.kt`, `PesananPagerAdapter.kt`, `PesananListFragment.kt`, `SplashActivity.kt`, `Prefs.kt`,
`activity_onboarding.xml`, `fragment_pesanan.xml`.

---

## Modul 8, 9, dan 12–16

Materi tersebut belum ada di daftar yang dikirim, jadi belum dibuatkan pemetaannya.
Ketika materinya keluar, tambahkan komentar yang sama pada file terkait dan lengkapi tabel
di atas. Struktur aplikasi (Activity/Fragment/Adapter terpisah) memudahkan penambahan tanpa
mengubah layar lain.

## Catatan

- Folders `arsip-compose/` dan `arsip-modul/` hanya referensi versi lama (tidak dikompilasi).
- Semua ikon dan gambar aplikasi adalah PNG; satu-satunya file XML di `res/drawable` adalah
  *shape/selector* (latar tombol, pill, tile) yang bukan gambar.
