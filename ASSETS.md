# Daftar Aset Gambar (PNG) — SIMPER-NRA Mobile

Semua gambar di aplikasi ini adalah **file PNG asli** di `app/src/main/res/drawable-nodpi/`
(tidak ada `.xml` vector). Ikon navigasi & ikon kecil memakai library Material Icons
(bukan file `.xml` gambar).

Karena foto asli toko belum dikirim, semua nama file sudah final tetapi isinya masih
**placeholder bergaya brand** yang saya gambar sendiri. Cukup timpa file di bawah dengan
foto/logo asli memakai **nama file yang sama** — tidak perlu ubah kode sama sekali.

| Nama file | Ukuran sekarang | Dipakai di | File asli yang dibutuhkan |
|---|---|---|---|
| `logo_simper.png` | 512×512, transparan | Splash, menu lain | Logo SIMPER-NRA (ikon printer + kertas CMYK), latar transparan |
| `logo_simper_white.png` | 512×512, transparan | Header halaman login | Versi putih logo untuk latar biru |
| `splash_illustration.png` | 900×900, transparan | Splashscreen | Ilustrasi/logo besar untuk splash |
| `avatar_user.png` | 420×420 | Home, Profil | Foto profil pelanggan |
| `ic_google.png` | 160×160 | Tombol "Masuk dengan Google" | Logo Google berwarna |
| `banner_home_hero.png` | 1400×620 | Hero Home | Foto banner/standing banner toko |
| `banner_home_promo.png` | 1400×620 | Promo Spesial Home | Foto produk untuk banner promo |
| `produk_banner.png` | 1000×750 | Katalog, riwayat, detail | Foto produk Banner |
| `produk_spanduk.png` | 1000×750 | Katalog, detail | Foto produk Spanduk |
| `produk_stiker.png` | 1000×750 | Katalog, detail | Foto produk Stiker |
| `produk_undangan.png` | 1000×750 | Katalog, detail | Foto produk Undangan |
| `produk_idcard.png` | 1000×750 | Katalog, detail | Foto produk ID Card |
| `produk_nota.png` | 1000×750 | Katalog, detail | Foto produk Nota |
| `produk_kemasan.png` | 1000×750 | Katalog, detail | Foto produk Kemasan Custom |
| `produk_merchandise.png` | 1000×750 | Katalog, detail | Foto produk Merchandise |
| `produk_rollbanner.png` | 1000×750 | Katalog, detail | Foto produk Roll Banner |
| `produk_neonbox.png` | 1000×750 | Katalog, detail | Foto produk Neon Box |
| `produk_stempel.png` | 1000×750 | Katalog, detail | Foto produk Stempel |
| `produk_gantungan.png` | 1000×750 | Katalog, detail | Foto produk Gantungan Kunci |
| `contoh_banner_1..4.png` | 700×700 | Galeri "Contoh Hasil Cetak" | 4 foto hasil cetak nyata |
| `mipmap-*/ic_launcher.png` | 48–192 px | Ikon aplikasi | Logo aplikasi (square) |

Font: `app/src/main/res/font/poppins_{regular,medium,semibold,bold}.ttf` (Poppins resmi dari Google Fonts).

## Cara mengganti

```bash
cp foto_banner_asli.png app/src/main/res/drawable-nodpi/produk_banner.png
```

Lalu rebuild (`gradle :app:assembleDebug`). Rasio gambar yang dipakai:
katalog 4:3 (1000×750), banner home ±2.25:1 (1400×620), avatar 1:1.

## Regenerasi placeholder

```bash
python3 tools/gen_assets.py
```
