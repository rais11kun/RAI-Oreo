/**
 * Penerapan praktikum: Modul 6 - Splash Screen: Activity pembuka dengan Handler 1500 ms lalu Intent ke layar berikutnya.
 * Modul 11 - gerbang onboard screen: ViewPager2 hanya muncul sekali setelah splash.
 */
package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.data.Prefs
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Prefs.tambahPeluncuran(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val email = Prefs.email(this)
            val punyaSesi = Prefs.ingat(this) && email.isNotEmpty()
            if (punyaSesi) SimperRepository.masukSebagai(email)

            // Modul 11: onboarding (ViewPager2) tampil sekali sebelum login.
            val tujuan = when {
                !Prefs.onboardingSelesai(this) -> OnboardingActivity::class.java
                SimperRepository.currentUser != null -> MainActivity::class.java
                else -> LoginActivity::class.java
            }
            startActivity(Intent(this, tujuan))
            finish()
        }, 1500)
    }
}
