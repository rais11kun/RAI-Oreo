package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Splash ditampilkan selama 2 detik, sesuai modul Pertemuan 6.
        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()
        }, 2000)
    }

    private fun checkLoginStatus() {
        // Status login dibaca dari SharedPreferences yang sama dengan LoginActivity.
        val sharedPref = getSharedPreferences("simper_pref", MODE_PRIVATE)
        val isLogin = sharedPref.getBoolean("isLogin", false)

        val destination = if (isLogin) {
            MainActivity::class.java
        } else {
            LoginActivity::class.java
        }

        startActivity(Intent(this, destination))
        finish() // Splash tidak muncul lagi saat pengguna menekan tombol Back.
    }
}
