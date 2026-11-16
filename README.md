# SIMPER-NRA Mobile

Aplikasi Android untuk **New Reklame Advertising** (percetakan) — sistem informasi pemesanan
layanan cetak: katalog, pemesanan, pemantauan status produksi, sampai laporan pemilik.

Seluruh antarmuka dibangun dengan **layout XML** (Material Components), **ViewBinding**,
**Activity + Fragment**, dan **Bottom Navigation**. Tidak ada Jetpack Compose di layar aplikasi.

---

## Arsitektur

| Bagian | Teknologi | Lokasi |
|---|---|---|
| Navigasi utama | `activity_main.xml`: `FragmentContainerView` + `BottomNavigationView` | `res/layout/activity_main.xml` |
| 4 tab | Fragment: Home, Katalog, Pesanan, Profil | `java/.../fragment/` |
| 21 layar lain | Activity XML + Toolbar | `java/.../screen/` |
| Daftar data | RecyclerView (grid/kartu) + ListView custom adapter | `java/.../adapter/` |
| Data demo | Repository in-memory + `SharedPreferences` untuk sesi | `java/.../data/` |

**Alur:** `SplashActivity` (1,5 detik) → `OnboardingActivity` (3 halaman `ViewPager2`, hanya sekali)
→ `LoginActivity` (email/password atau sidik jari) → `MainActivity` (Home · Katalog · Pesanan · Profil).
Dari Profil tersedia: edit profil, alamat, ubah password, kartu QR, notifikasi, pengaturan,
bantuan, serta menu operasional (antrian & kelola layanan) dan laporan untuk karyawan/pemilik.

```
app/src/main/
├── java/com/nra/simper/
│   ├── SplashActivity, OnboardingActivity, LoginActivity, RegisterActivity, LupaPasswordActivity, MainActivity
│   ├── fragment/   HomeFragment, KatalogFragment, PesananFragment, PesananListFragment,
│   │               ProfilFragment, OnboardingPageFragment
│   ├── screen/     DetailLayanan, FormPemesanan, KonfirmasiPesanan, PesananSukses,
│   │               DetailPesanan, Notifikasi, EditProfil, Alamat, UbahPassword,
│   │               Pengaturan, Bantuan, KartuQr, ScanQr, Antrian, DetailAntrian,
│   │               KelolaLayanan, FormLayanan, DashboardPemilik, Laporan
│   ├── adapter/    Produk, Pesanan, Notifikasi, Menu, Timeline, Contoh, Layanan, Laporan, Alamat,
│   │               PesananPagerAdapter, OnboardingAdapter
│   ├── data/       Models, SimperRepository, OrderDraft, Prefs
│   └── util/       Qr (ZXing), Biometric, Badge
├── res/layout/     layout XML setiap Activity/Fragment + layout baris adapter
├── res/drawable/   shape & selector XML (latar tombol, pill, tile ikon, dll.)
├── res/drawable-nodpi/  semua gambar & ikon berformat PNG
├── res/menu/       menu_bottom, menu_toolbar, menu_notifikasi, menu_kelola
├── res/font/       Poppins regular/medium/semibold/bold
└── assets/         bantuan.html (halaman FAQ untuk WebView)
```

## Daftar layar

Splash · Onboarding (3 halaman) · Login · Daftar · Lupa Password · Beranda · Katalog · Pesanan · Profil ·
Detail Layanan · Form Pemesanan · Konfirmasi Pesanan · Pesanan Sukses · Detail Pesanan ·
Notifikasi · Edit Profil · Alamat · Ubah Password · Pengaturan · Bantuan (WebView) ·
Kartu QR · Scan QR · Antrian (karyawan) · Detail Antrian · Kelola Layanan · Form Layanan ·
Dashboard Pemilik · Laporan Penjualan.

## Akun demo

| Email | Password | Peran |
|---|---|---|
| rais@simpernra.id | 123456 | Pelanggan |
| karyawan@simpernra.id | 123456 | Karyawan |
| pemilik@simpernra.id | 123456 | Pemilik |

Login sidik jari memakai `BiometricPrompt`; jika sensor tidak ada, aplikasi menampilkan dialog
dan menyarankan login dengan password.

## Gambar

Semua foto produk, banner, logo, avatar, dan ikon adalah **file PNG** di
`res/drawable-nodpi` (bukan vector XML), dibuat/didaftar di `ASSETS.md`.
Ganti dengan foto asli cukup dengan menimpa file PNG bernama sama.

## Build

```bash
./gradlew :app:assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

- AGP 8.5.2 · Gradle 8.7 · Kotlin 2.0.21 · JDK 17
- compileSdk/targetSdk 34 · minSdk 24
- Dependensi utama: appcompat, material 1.12.0, constraintlayout, recyclerview,
  fragment-ktx, biometric, zxing-android-embedded

## Modul praktikum

Penerapan Modul 2–10 tersebar di layar aplikasi (bukan layar demo terpisah) dan ditandai
lewat komentar kode (`/** Penerapan praktikum: ... */`) di file terkait.
Pemetaan lengkap untuk penilaian ada di [MODULES.md](MODULES.md).

## Catatan

Folder `arsip-compose/` dan `arsip-modul/` berisi versi sebelumnya (UI Compose dan layar demo
modul). Keduanya **tidak** dikompilasi dan tidak dipakai aplikasi — disimpan hanya sebagai
referensi. Folder itu tidak disertakan dalam paket ZIP unduhan.
