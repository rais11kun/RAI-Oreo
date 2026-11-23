# SIMPER-NRA — Patch Integrasi Supabase untuk D:\\Mobile

## Status paket
Patch ini dibuat dari:
- proyek utama: `SIMPER-NRA-Mobile(4).zip` (package `com.example.rai_oreo`)
- paket fitur Supabase: `SIMPER-NRA-Supabase-Lengkap.zip` (package awal `com.nra.simper`)

**Patch ini adalah overlay**, bukan proyek Android lengkap. Ekstrak ke akar proyek `D:\\Mobile`, bukan ke dalam folder `app` dan bukan ke folder proyek lain.

## Apa yang disesuaikan
- Semua package/import sumber Supabase dipindah dari `com.nra.simper` ke `com.example.rai_oreo`.
- Logika fitur dari paket Supabase dipertahankan.
- `app/build.gradle.kts` digabung dengan konfigurasi proyek utama: plugin Compose, namespace/applicationId lama, build features lama, dependency lama, ditambah BuildConfig, Retrofit, Gson, OkHttp, dan Lifecycle KTX.
- Tidak ada file layout XML yang diubah atau disertakan dalam patch.
- Tidak ada `AndroidManifest.xml` yang ditimpa. Proyek utama yang diperiksa sudah memiliki permission INTERNET dan deklarasi Activity yang diperlukan.
- `local.properties` tidak disertakan agar SDK path dan key lokal tidak tertimpa/terpublikasi.

## File yang diganti/ditambahkan
1. `app/build.gradle.kts` (versi hasil merge konfigurasi)
2. `RegisterActivity.kt`
3. `data/SimperRepository.kt` (penambahan metode sinkronisasi remote dan nomorOverride)
4. `data/remote/` (5 file Retrofit/Supabase)
5. `screen/` (KelolaLayanan, DetailAntrian, FormLayanan, FormPemesanan, DetailPesanan, Antrian)
6. `adapter/` (LayananAdapter, PesananAdapter)
7. `fragment/PesananListFragment.kt`
8. `supabase/schema.sql`
9. `supabase/erd-simper-nra.png`
10. `MODUL12-SUPABASE.md`

## Cara memasang
1. Buat branch kerja terlebih dahulu di proyek lokal. Contoh:
   `git switch -c integrasi-supabase`
2. Ekstrak isi ZIP ini langsung ke akar `D:\\Mobile`.
3. Saat diminta mengganti file, pilih **Replace**. Hanya file yang tertera di daftar paket yang akan diganti.
4. Jangan mengganti seluruh folder `app/` dengan folder app lain. Patch ini memang hanya berisi file overlay.
5. Periksa `app/src/main/AndroidManifest.xml`: proyek utama sudah memiliki permission INTERNET. Jika file lokal kamu berbeda dari proyek yang diperiksa, tambahkan permission itu di dalam tag `<manifest>`, di luar `<application>`.
6. Buka `local.properties` di akar proyek. Pertahankan baris `sdk.dir=...`, lalu tambahkan:
   `SUPABASE_URL=https://PROJECT_REF.supabase.co`
   `SUPABASE_ANON_KEY=YOUR_SUPABASE_PUBLISHABLE_OR_ANON_KEY`
   Jangan isi dengan `service_role` / secret key. Jangan mengirim `local.properties` ke Git.
7. Supabase Dashboard → SQL Editor → New query → tempel seluruh `supabase/schema.sql` → Run.
8. Android Studio → File → Sync Project with Gradle Files. Gunakan Gradle JDK 21 seperti saat build proyek utama.
9. Jalankan `:app:assembleDebug`, lalu Run di emulator.

## Verifikasi sebelum commit
- Pastikan `namespace` dan `applicationId` tetap `com.example.rai_oreo`.
- Pastikan `local.properties` tetap tidak masuk Git (`git status`).
- Uji Register, daftar layanan, tambah/edit/nonaktif/hapus layanan, daftar pesanan, buat pesanan, update status, dan hapus pesanan.
- Jika URL/key belum diisi, kode memiliki fallback lokal sesuai implementasi paket asal.

## Catatan keamanan penting
SQL dalam paket asal adalah konfigurasi demonstrasi praktikum:
- `users.password` disimpan sebagai teks biasa.
- Policy RLS demo memberi akses CRUD luas kepada role `anon`.
- Dengan konfigurasi tersebut, data `users` dan `pesanan` dapat dibaca/diubah melalui anon key. Gunakan hanya untuk database demo dengan data uji, jangan untuk data pelanggan nyata atau produksi.
- Jangan pernah memasukkan `service_role` key ke aplikasi Android atau Git.
