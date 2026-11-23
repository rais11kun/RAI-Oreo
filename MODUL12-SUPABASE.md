# Modul 12 — REST API · BaaS (Supabase) · Retrofit

Dokumen ini menjelaskan database Supabase untuk SIMPER-NRA, cara memasangnya, dan
bagian kode yang ditambahkan. **UI dan alur aplikasi tidak diubah sama sekali** —
tidak ada layout, warna, teks, maupun navigasi yang disentuh.

---

## 1. Tiga tabel

| Tabel | Fungsi | Kolom penting |
|---|---|---|
| `users` | akun pelanggan/karyawan/pemilik (target fitur **Register**) | `id` (uuid, PK), `nama`, `email` (unik), `password`, `telepon`, `role`, `created_at` |
| `layanan` | katalog layanan percetakan (**CRUD 1**) | `id` (bigint, PK), `nama`, `kategori`, `harga_mulai`, `satuan`, `deskripsi`, `estimasi`, `bahan[]`, `finishing[]`, `aktif`, `created_at` |
| `pesanan` | transaksi pesanan (**CRUD 2**) | `id` (PK), `nomor` (unik, otomatis `#ORD00124`), `user_id` → `users`, `layanan_id` → `layanan`, `ukuran`, `jumlah`, `bahan`, `finishing`, `catatan`, `total`, `status`, `tanggal`, `created_at` |

Relasi: **users 1 — N pesanan**, **layanan 1 — N pesanan** (`on delete cascade` untuk user,
`on delete set null` untuk layanan). Status dibatasi `BARU / DESAIN / DIPROSES / PRODUKSI /
SELESAI / DIBATALKAN`. Nomor pesanan diisi otomatis oleh trigger `trg_set_nomor_pesanan`.

Diagram: [`supabase/erd-simper-nra.png`](supabase/erd-simper-nra.png) (siap di-import ke tldraw).

---

## 2. Membuat database di akun Supabase Anda (sekali saja)

1. Buka <https://supabase.com/dashboard> → **New project**, pilih organisasi kampus/akun Anda,
   isi nama `simper-nra`, region terdekat (Singapore), simpan **database password**.
2. Tunggu project selesai dibuat (±2 menit).
3. Buka **SQL Editor → New query**, tempel **seluruh isi** [`supabase/schema.sql`](supabase/schema.sql),
   klik **Run**. Semua tabel, policy, dan data contoh langsung dibuat.
4. Buka **Settings → API**, catat:
   - **Project URL** → `https://xxxxx.supabase.co`
   - **anon public key** → kunci panjang `eyJ...`

> Saya tidak bisa login ke akun Supabase Anda, jadi langkah 1–4 dijalankan oleh Anda.
> Cukup sekali; setelah itu database siap dipakai aplikasi.

## 3. Menghubungkan aplikasi (tanpa mengubah kode layar)

Tambahkan dua baris ini ke **`local.properties`** (file ini sudah otomatis di-`.gitignore`):

```properties
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOi...kunci-anon-anda
```

Lalu **Rebuild Project**. Nilainya masuk ke `BuildConfig.SUPABASE_URL` dan
`BuildConfig.SUPABASE_ANON_KEY` (lihat `app/build.gradle.kts`), jadi tidak ada kunci
yang ditulis di dalam kode atau ikut ke Git.

> Jangan pernah menaruh *service_role key* di aplikasi Android. Cukup **anon key**.

---

## 4. Bagian kode yang ditambahkan (semua file baru)

| Berkas | Isi |
|---|---|
| `data/remote/SupabaseModels.kt` | DTO `PenggunaDto`, `RegisterRequest`, `LayananDto`, `PesananDto` dengan `@SerializedName` |
| `data/remote/SupabaseApi.kt` | Interface Retrofit: insert `users`, CRUD `layanan`, CRUD `pesanan` |
| `data/remote/SupabaseClient.kt` | Retrofit + OkHttp, menambahkan header `apikey` & `Authorization: Bearer <anon>` |
| `data/remote/RemoteRepository.kt` | Fungsi `register`, `masuk`, `daftarLayanan`, `tambahLayanan`, `ubahLayanan`, `hapusLayanan`, `daftarPesanan`, `tambahPesanan`, `ubahPesanan`, `ubahStatusPesanan`, `hapusPesanan` (semua `suspend` + `Result`) |
| `supabase/schema.sql` | DDL tabel, trigger, index, RLS, seed |
| `supabase/erd-simper-nra.png` | ERD untuk tldraw |

Perubahan konfigurasi hanya di `app/build.gradle.kts`: dependency Retrofit/Gson/OkHttp,
`lifecycle-runtime-ktx` (untuk `lifecycleScope`), `buildConfig = true`, dan dua
`buildConfigField` untuk URL/anon key. **Tidak ada layout, warna, string tampilan,
atau alur navigasi yang diubah.**

## 4b. Sambungan ke layar (sudah terpasang)

| Fitur | Layar | Aksi |
|---|---|---|
| **Register** | `RegisterActivity` | Validasi tetap sama, lalu `RemoteRepository.register()` meng-insert ke tabel `users`; setelah sukses sesi lokal dibuat dan masuk Beranda seperti biasa |
| **Layanan — Read** | `KelolaLayananActivity` | `daftarLayanan()` lalu dipetakan ke model lokal (`RemoteMapper`) sehingga RecyclerView tidak berubah |
| **Layanan — Create/Update** | `FormLayananActivity` | `tambahLayanan()` / `ubahLayanan()`; tombol Simpan dan validasinya tetap |
| **Layanan — Update aktif** | switch pada kartu layanan | `patchLayanan({aktif})` |
| **Layanan — Delete** | tekan lama kartu layanan | dialog konfirmasi → `hapusLayanan()` |
| **Pesanan — Read** | `PesananListFragment` (tab Aktif/Selesai/Dibatalkan) | `daftarPesanan()` disinkronkan ke cache lokal |
| **Pesanan — Create** | `FormPemesananActivity` (langkah 3) | `tambahPesanan()`, nomor `#ORD` dari trigger server dipakai di layar sukses |
| **Pesanan — Update status** | `DetailAntrianActivity`, tombol Batalkan di `DetailPesananActivity` | `patchPesanan({status})` |
| **Pesanan — Delete** | tekan lama kartu pesanan | dialog konfirmasi → `hapusPesanan()` |

Semua aksi punya fallback: bila `SUPABASE_URL`/`SUPABASE_ANON_KEY` kosong atau jaringan
gagal, aplikasi tetap memakai data lokal seperti sebelumnya (tidak crash, alur sama).

`RemoteMapper.kt` mengubah DTO Supabase menjadi `Service`/`Order` yang sudah dipakai UI,
sehingga tidak ada adapter atau layout yang perlu diubah. Tombol hapus memakai
**tekan lama** pada kartu, jadi tidak ada elemen baru di layar.

---

## 5. Bukti API berjalan (bisa dijalankan tanpa aplikasi)

```bash
# ganti dengan nilai Anda (jangan di-commit)
export SUPABASE_URL="https://xxxxx.supabase.co"
export SUPABASE_ANON_KEY="eyJ..."

# READ layanan
curl -s "$SUPABASE_URL/rest/v1/layanan?select=*" -H "apikey: $SUPABASE_ANON_KEY"

# CREATE layanan
curl -s -X POST "$SUPABASE_URL/rest/v1/layanan" \
  -H "apikey: $SUPABASE_ANON_KEY" -H "Content-Type: application/json" \
  -H "Prefer: return=representation" \
  -d '{"nama":"Banner Test","kategori":"Banner","harga_mulai":20000,"satuan":"m²"}'

# UPDATE layanan id=1
curl -s -X PATCH "$SUPABASE_URL/rest/v1/layanan?id=eq.1" \
  -H "apikey: $SUPABASE_ANON_KEY" -H "Content-Type: application/json" \
  -d '{"harga_mulai":22000}'

# DELETE layanan id=1
curl -s -X DELETE "$SUPABASE_URL/rest/v1/layanan?id=eq.1" -H "apikey: $SUPABASE_ANON_KEY"

# REGISTER user
curl -s -X POST "$SUPABASE_URL/rest/v1/users" \
  -H "apikey: $SUPABASE_ANON_KEY" -H "Content-Type: application/json" \
  -H "Prefer: return=representation" \
  -d '{"nama":"Uji Coba","email":"uji@simpernra.id","password":"123456","role":"pelanggan"}'
```

Simpan keluaran perintah di atas sebagai bukti CRUD (screenshot terminal).

---

## 6. tldraw & Git (bagian yang saya tidak bisa lakukan untuk Anda)

**tldraw**: buka <https://tldraw.com> → seret `supabase/erd-simper-nra.png` ke kanvas →
tambahkan catatan nama tabel/relasi bila perlu → simpan/ekspor. Atau gambar ulang
mengikuti daftar kolom di bagian 1.

**Git push** (dijalankan di komputer Anda):

```bash
git add app/build.gradle.kts \
        app/src/main/java/com/example/rai_oreo/RegisterActivity.kt \
        app/src/main/java/com/example/rai_oreo/data/SimperRepository.kt \
        app/src/main/java/com/example/rai_oreo/data/remote \
        app/src/main/java/com/example/rai_oreo/screen \
        app/src/main/java/com/example/rai_oreo/adapter \
        app/src/main/java/com/example/rai_oreo/fragment/PesananListFragment.kt \
        supabase MODUL12-SUPABASE.md BACA-DULU.md
git commit -m "feat: integrate Supabase REST API with Retrofit"
git push origin migrasi-simper-nra
```

Jangan menambahkan `local.properties` (berisi anon key) ke Git.

---

## 7. Daftar screenshot yang perlu diambil

1. Supabase **Table Editor** menampilkan 3 tabel (`users`, `layanan`, `pesanan`) setelah SQL dijalankan.
2. Isi tabel `users` (3 baris seed) dan hasil insert Register baru.
3. Isi tabel `layanan` dan `pesanan` (seed).
4. **SQL Editor** saat `schema.sql` berhasil (tab hasil "Success. No rows returned").
5. Bukti CRUD dari `curl` di bagian 5 (terminal).
6. Kanvas tldraw berisi ERD + 3 tabel.
7. Halaman repo Git setelah push (commit Modul 12).
8. (Opsional) Aplikasi tetap tampil sama seperti sebelumnya — bukti UI tidak berubah.

---

## 8. Catatan keamanan (untuk laporan)

- Kolom `password` disimpan apa adanya untuk kebutuhan demo praktikum. Di produksi
  gunakan Supabase Auth atau simpan hash (bcrypt/argon2).
- Policy RLS saat ini `using (true)` agar anon key bisa CRUD untuk praktikum.
  Untuk produksi: `using (auth.uid() = id)` pada `users`, dan filter `user_id = auth.uid()`
  pada `pesanan`.
- Anon key boleh ada di aplikasi; **service_role key tidak boleh**.
