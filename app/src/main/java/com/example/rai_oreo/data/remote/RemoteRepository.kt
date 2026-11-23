/**
 * Penerapan praktikum: Modul 12 - repository pemanggil REST API (Retrofit).
 * Berisi fitur Register ke tabel users dan CRUD untuk layanan & pesanan.
 *
 * Dipanggil dari layar yang sudah ada (Register, Kelola Layanan, Form Layanan,
 * Form Pemesanan, daftar/detail pesanan) tanpa mengubah layout maupun alur.
 */
package com.example.rai_oreo.data.remote

object RemoteRepository {

    /** Cek apakah URL & anon key Supabase sudah diisi. */
    fun siap(): Boolean = SupabaseClient.terkonfigurasi

    // ------------------------------------------------------------- users
    suspend fun register(
        nama: String,
        email: String,
        password: String,
        telepon: String? = null,
        role: String = "pelanggan",
    ): Result<PenggunaDto> = runCatching {
        SupabaseClient.api()
            .tambahPengguna(RegisterRequest(nama, email, password, telepon, role))
            .first()
    }

    suspend fun masuk(email: String, password: String): Result<PenggunaDto> = runCatching {
        SupabaseClient.api()
            .cariPengguna("eq.$email", "eq.$password")
            .firstOrNull()
            ?: error("Email atau password salah")
    }

    suspend fun cariPengguna(email: String): Result<PenggunaDto?> = runCatching {
        SupabaseClient.api().penggunaByEmail("eq.$email").firstOrNull()
    }

    // ----------------------------------------------------------- layanan
    suspend fun daftarLayanan(): Result<List<LayananDto>> = runCatching {
        SupabaseClient.api().daftarLayanan()
    }

    suspend fun tambahLayanan(layanan: LayananDto): Result<LayananDto> = runCatching {
        SupabaseClient.api().tambahLayanan(layanan).first()
    }

    suspend fun ubahLayanan(id: Long, layanan: LayananDto): Result<LayananDto> = runCatching {
        SupabaseClient.api().ubahLayanan("eq.$id", layanan).first()
    }

    suspend fun hapusLayanan(id: Long): Result<Unit> = runCatching {
        val respons = SupabaseClient.api().hapusLayanan("eq.$id")
        if (!respons.isSuccessful) error("Gagal menghapus layanan (${respons.code()})")
    }

    // ----------------------------------------------------------- pesanan
    suspend fun daftarPesanan(): Result<List<PesananDto>> = runCatching {
        SupabaseClient.api().daftarPesanan()
    }

    suspend fun tambahPesanan(pesanan: PesananDto): Result<PesananDto> = runCatching {
        SupabaseClient.api().tambahPesanan(pesanan).first()
    }

    suspend fun ubahPesanan(id: Long, pesanan: PesananDto): Result<PesananDto> = runCatching {
        SupabaseClient.api().ubahPesanan("eq.$id", pesanan).first()
    }

    /** Hanya mengirim kolom status, kolom lain tidak ikut tertimpa. */
    suspend fun ubahStatusPesanan(id: Long, status: String): Result<PesananDto> = runCatching {
        SupabaseClient.api().patchPesanan("eq.$id", mapOf("status" to status)).first()
    }

    /** Menyalakan/mematikan sebuah layanan tanpa mengubah kolom lain. */
    suspend fun ubahLayananAktif(id: Long, aktif: Boolean): Result<LayananDto> = runCatching {
        SupabaseClient.api().patchLayanan("eq.$id", mapOf("aktif" to aktif)).first()
    }

    suspend fun hapusPesanan(id: Long): Result<Unit> = runCatching {
        val respons = SupabaseClient.api().hapusPesanan("eq.$id")
        if (!respons.isSuccessful) error("Gagal menghapus pesanan (${respons.code()})")
    }
}
