package com.example.rai_oreo.data

import android.content.Context

/**
 * Penyimpanan sesi sederhana memakai SharedPreferences (MODE_PRIVATE).
 * Dipakai splash, login, dan halaman pengaturan.
 */
object Prefs {

    private const val FILE = "simper_session"
    private const val KEY_EMAIL = "email"
    private const val KEY_NAMA = "nama"
    private const val KEY_INGAT = "ingat_saya"
    private const val KEY_SIDIK = "sidik_jari"
    private const val KEY_LAUNCH = "jumlah_peluncuran"
    private const val KEY_NOTIF = "notif_pesanan"
    private const val KEY_PROMO = "notif_promo"
    private const val KEY_ONBOARDING = "onboarding_selesai"

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun simpanSesi(context: Context, email: String, nama: String, ingat: Boolean) {
        prefs(context).edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAMA, nama)
            .putBoolean(KEY_INGAT, ingat)
            .apply()
    }

    fun email(context: Context): String = prefs(context).getString(KEY_EMAIL, "") ?: ""

    fun nama(context: Context): String = prefs(context).getString(KEY_NAMA, "") ?: ""

    fun ingat(context: Context): Boolean = prefs(context).getBoolean(KEY_INGAT, false)

    var Context.sidikJari: Boolean
        get() = prefs(this).getBoolean(KEY_SIDIK, true)
        set(value) = prefs(this).edit().putBoolean(KEY_SIDIK, value).apply()

    var Context.notifPesanan: Boolean
        get() = prefs(this).getBoolean(KEY_NOTIF, true)
        set(value) = prefs(this).edit().putBoolean(KEY_NOTIF, value).apply()

    var Context.notifPromo: Boolean
        get() = prefs(this).getBoolean(KEY_PROMO, true)
        set(value) = prefs(this).edit().putBoolean(KEY_PROMO, value).apply()

    fun tambahPeluncuran(context: Context): Int {
        val jumlah = prefs(context).getInt(KEY_LAUNCH, 0) + 1
        prefs(context).edit().putInt(KEY_LAUNCH, jumlah).apply()
        return jumlah
    }

    /** Modul 11 - penanda bahwa onboard screen (ViewPager2) sudah pernah ditampilkan. */
    fun onboardingSelesai(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ONBOARDING, false)

    fun setOnboardingSelesai(context: Context) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING, true).apply()
    }

    fun hapusSesi(context: Context) {
        prefs(context).edit()
            .remove(KEY_EMAIL)
            .remove(KEY_NAMA)
            .remove(KEY_INGAT)
            .apply()
    }
}
