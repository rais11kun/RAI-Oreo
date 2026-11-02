package com.example.rai_oreo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.content.Intent
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.rai_oreo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("simper_pref", MODE_PRIVATE)
        if (!prefs.getBoolean("isLogin", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Login pertama dan hasil autentikasi dari Splash mengirim flag bypass satu kali.
        val alreadyAuthenticated = intent.getBooleanExtra(EXTRA_AUTHENTICATED, false)
        intent.removeExtra(EXTRA_AUTHENTICATED)
        if (!alreadyAuthenticated) {
            showBiometricGate()
        }

        // Fragment Beranda ditampilkan saat MainActivity pertama kali dibuka.
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            binding.bottomNavView.selectedItemId = R.id.nav_beranda
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_layanan -> {
                    replaceFragment(LayananFragment())
                    true
                }
                R.id.nav_pesan -> {
                    replaceFragment(PesanFragment())
                    true
                }
                R.id.nav_riwayat -> {
                    replaceFragment(RiwayatFragment())
                    true
                }
                R.id.nav_profil -> {
                    replaceFragment(ProfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun showBiometricGate() {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK
        val manager = BiometricManager.from(this)
        if (manager.canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Fingerprint belum tersedia. Silakan login kembali.", Toast.LENGTH_LONG).show()
            getSharedPreferences("simper_pref", MODE_PRIVATE).edit().putBoolean("isLogin", false).apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(this@MainActivity, "Fingerprint berhasil", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@MainActivity, "Sidik jari tidak cocok. Coba lagi.", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    getSharedPreferences("simper_pref", MODE_PRIVATE).edit().putBoolean("isLogin", false).apply()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verifikasi SIMPER-NRA")
            .setSubtitle("Gunakan sidik jari untuk membuka aplikasi")
            .setAllowedAuthenticators(authenticators)
            .setNegativeButtonText("Kembali ke login")
            .build()
        prompt.authenticate(info)
    }

    companion object {
        const val EXTRA_AUTHENTICATED = "extra_authenticated"
    }

    // Pola mengikuti modul: mengganti isi FrameLayout dengan Fragment terpilih.
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            // Sesuai modul Pertemuan 7, back stack Bottom Navigation tidak ditambahkan.
            .commit()
    }
}
