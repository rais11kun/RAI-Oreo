/**
 * Penerapan praktikum: Modul 12 - model data (DTO) untuk REST API Supabase/PostgREST.
 * Semua properti memakai @SerializedName karena kolom database memakai snake_case.
 * Tidak ada layar yang diubah; model ini dipakai oleh RemoteRepository.
 */
package com.example.rai_oreo.data.remote

import com.google.gson.annotations.SerializedName

/** Satu baris tabel `users`. */
data class PenggunaDto(
    val id: String? = null,
    val nama: String,
    val email: String,
    val password: String? = null,
    val telepon: String? = null,
    val role: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
)

/** Payload fitur Register (insert ke tabel users). */
data class RegisterRequest(
    val nama: String,
    val email: String,
    val password: String,
    val telepon: String? = null,
    val role: String = "pelanggan",
)

/** Satu baris tabel `layanan`. */
data class LayananDto(
    val id: Long? = null,
    val nama: String,
    val kategori: String = "Umum",
    @SerializedName("harga_mulai") val hargaMulai: Int = 0,
    val satuan: String = "pcs",
    val deskripsi: String? = null,
    val estimasi: String? = null,
    val bahan: List<String>? = null,
    val finishing: List<String>? = null,
    val aktif: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null,
)

/** Satu baris tabel `pesanan`. */
data class PesananDto(
    val id: Long? = null,
    val nomor: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("layanan_id") val layananId: Long? = null,
    val ukuran: String? = null,
    val jumlah: Int = 1,
    val bahan: String? = null,
    val finishing: String? = null,
    val catatan: String? = null,
    @SerializedName("desain_nama") val desainNama: String? = null,
    @SerializedName("desain_ukuran") val desainUkuran: String? = null,
    val total: Int = 0,
    val status: String = "BARU",
    val tanggal: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
)
