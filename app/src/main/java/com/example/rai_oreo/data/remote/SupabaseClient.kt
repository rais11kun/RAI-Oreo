/**
 * Penerapan praktikum: Modul 12 - konfigurasi Retrofit + OkHttp untuk Supabase.
 * URL dan anon key dibaca dari BuildConfig yang diisi lewat local.properties,
 * jadi tidak ada kunci yang ditulis di dalam kode.
 */
package com.example.rai_oreo.data.remote

import com.example.rai_oreo.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {

    private val api: SupabaseApi? by lazy {
        val url = BuildConfig.SUPABASE_URL.trim()
        val kunci = BuildConfig.SUPABASE_ANON_KEY.trim()

        if (url.isEmpty() || kunci.isEmpty()) {
            null
        } else {

            val http = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()

                    val builder = request.newBuilder()
                        .header("apikey", kunci)
                        .header("Authorization", "Bearer $kunci")
                        .header("Accept", "application/json")

                    when (request.method.uppercase()) {
                        "GET", "HEAD" -> {
                            builder.header("Accept-Profile", "public")
                        }
                        else -> {
                            builder.header("Content-Profile", "public")
                        }
                    }

                    chain.proceed(builder.build())
                }
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()


            Retrofit.Builder()
                .baseUrl(if (url.endsWith("/")) url else "$url/")
                .client(http)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SupabaseApi::class.java)
        }
    }

    /** False bila URL/anon key belum diisi di local.properties. */
    val terkonfigurasi: Boolean get() = api != null

    fun api(): SupabaseApi = api ?: error(
        "Supabase belum dikonfigurasi. Isi SUPABASE_URL dan SUPABASE_ANON_KEY di local.properties lalu rebuild."
    )
}
