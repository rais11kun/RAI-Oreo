/**
 * Penerapan praktikum: Modul 12 - REST API memakai Retrofit.
 * Endpoint mengikuti PostgREST milik Supabase: /rest/v1/{tabel}.
 * Operasi CRUD untuk `layanan` dan `pesanan`, plus insert untuk `users` (Register).
 */
package com.example.rai_oreo.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    // ------------------------------------------------------------- users
    /** Register: insert satu baris ke tabel users. */
    @POST("rest/v1/users")
    suspend fun tambahPengguna(
        @Body body: RegisterRequest,
        @Header("Prefer") prefer: String = "return=representation",
    ): List<PenggunaDto>

    /** Login sederhana: cari user dengan email + password yang cocok. */
    @GET("rest/v1/users")
    suspend fun cariPengguna(
        @Query("email") emailFilter: String,
        @Query("password") passwordFilter: String,
        @Query("select") select: String = "*",
    ): List<PenggunaDto>

    @GET("rest/v1/users")
    suspend fun penggunaByEmail(
        @Query("email") emailFilter: String,
        @Query("select") select: String = "*",
    ): List<PenggunaDto>

    // ----------------------------------------------------------- layanan
    @GET("rest/v1/layanan")
    suspend fun daftarLayanan(
        @Query("select") select: String = "*",
        @Query("order") order: String = "id.asc",
    ): List<LayananDto>

    @POST("rest/v1/layanan")
    suspend fun tambahLayanan(
        @Body body: LayananDto,
        @Header("Prefer") prefer: String = "return=representation",
    ): List<LayananDto>

    @PATCH("rest/v1/layanan")
    suspend fun ubahLayanan(
        @Query("id") idFilter: String,
        @Body body: LayananDto,
        @Header("Prefer") prefer: String = "return=representation",
    ): List<LayananDto>

    /** Ubah sebagian kolom saja (mis. hanya `aktif`). */
    @PATCH("rest/v1/layanan")
    suspend fun patchLayanan(
        @Query("id") idFilter: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
        @Header("Prefer") prefer: String = "return=representation",
    ): List<LayananDto>

    @DELETE("rest/v1/layanan")
    suspend fun hapusLayanan(@Query("id") idFilter: String): Response<Unit>

    // ----------------------------------------------------------- pesanan
    @GET("rest/v1/pesanan")
    suspend fun daftarPesanan(
        @Query("select") select: String = "*",
        @Query("order") order: String = "id.desc",
    ): List<PesananDto>

    @POST("rest/v1/pesanan")
    suspend fun tambahPesanan(
        @Body body: PesananDto,
        @Header("Prefer") prefer: String = "return=representation",
    ): List<PesananDto>

    @PATCH("rest/v1/pesanan")
    suspend fun ubahPesanan(
        @Query("id") idFilter: String,
        @Body body: PesananDto,
        @Header("Prefer") prefer: String = "return=representation",
    ): List<PesananDto>

    /** Ubah sebagian kolom saja (mis. hanya `status`). */
    @PATCH("rest/v1/pesanan")
    suspend fun patchPesanan(
        @Query("id") idFilter: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
        @Header("Prefer") prefer: String = "return=representation",
    ): List<PesananDto>

    @DELETE("rest/v1/pesanan")
    suspend fun hapusPesanan(@Query("id") idFilter: String): Response<Unit>
}
