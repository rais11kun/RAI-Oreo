
package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.rai_oreo.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Login menggunakan username dan password
        binding.btnLogin.setOnClickListener {

            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()

            val prefs = getSharedPreferences(
                "simper_pref",
                MODE_PRIVATE
            )

            val savedUsername = prefs.getString("username", null)
            val savedPassword = prefs.getString("password", null)

            // Login praktikum: username = password
            val practicumMatch =
                username.isNotEmpty() &&
                        password.isNotEmpty() &&
                        username == password

            // Login menggunakan akun hasil registrasi
            val registeredMatch =
                username.isNotEmpty() &&
                        password.isNotEmpty() &&
                        username == savedUsername &&
                        password == savedPassword

            if (practicumMatch || registeredMatch) {

                // Simpan status login dan username
                prefs.edit()
                    .putBoolean("isLogin", true)
                    .putString("username", username)
                    .apply()

                Toast.makeText(
                    this,
                    "Login berhasil",
                    Toast.LENGTH_SHORT
                ).show()

                openMainActivity()

            } else {

                MaterialAlertDialogBuilder(this)
                    .setTitle("Login gagal")
                    .setMessage("Periksa username dan password.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        // Login menggunakan biometrik
        binding.btnBiometricLogin.setOnClickListener {
            startBiometricLogin()
        }

        // Navigasi menuju halaman registrasi
        binding.tvRegister.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegistrationActivity::class.java
                )
            )
        }
    }

    // Memeriksa ketersediaan biometrik
    private fun startBiometricLogin() {

        val prefs = getSharedPreferences(
            "simper_pref",
            MODE_PRIVATE
        )

        // Username harus sudah pernah disimpan
        val savedUsername = prefs.getString("username", null)

        if (savedUsername.isNullOrBlank()) {

            MaterialAlertDialogBuilder(this)
                .setTitle("Login biometrik belum tersedia")
                .setMessage(
                    "Silakan login menggunakan username dan password " +
                            "terlebih dahulu."
                )
                .setPositiveButton("Mengerti", null)
                .show()

            return
        }

        val manager = BiometricManager.from(this)

        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK

        when (manager.canAuthenticate(authenticators)) {

            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt(authenticators)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                showBiometricMessage(
                    "Perangkat ini tidak memiliki sensor biometrik."
                )
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showBiometricMessage(
                    "Sensor biometrik sedang tidak tersedia."
                )
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showBiometricMessage(
                    "Belum ada sidik jari atau wajah yang didaftarkan. " +
                            "Silakan daftarkan biometrik melalui pengaturan perangkat."
                )
            }

            else -> {
                showBiometricMessage(
                    "Biometrik tidak tersedia. " +
                            "Gunakan username dan password."
                )
            }
        }
    }

    // Menampilkan dialog autentikasi biometrik
    private fun showBiometricPrompt(authenticators: Int) {

        val executor = ContextCompat.getMainExecutor(this)

        val prompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                // Autentikasi berhasil
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    val prefs = getSharedPreferences(
                        "simper_pref",
                        MODE_PRIVATE
                    )

                    prefs.edit()
                        .putBoolean("isLogin", true)
                        .apply()

                    Toast.makeText(
                        this@LoginActivity,
                        "Autentikasi berhasil",
                        Toast.LENGTH_SHORT
                    ).show()

                    openMainActivity()
                }

                // Sidik jari tidak cocok
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Toast.makeText(
                        this@LoginActivity,
                        "Biometrik tidak cocok. Coba lagi.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // Autentikasi dibatalkan atau terjadi error
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(
                        errorCode,
                        errString
                    )

                    Toast.makeText(
                        this@LoginActivity,
                        errString,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login SIMPER-NRA")
            .setSubtitle(
                "Konfirmasi identitas menggunakan sidik jari atau wajah"
            )
            .setAllowedAuthenticators(authenticators)
            .setNegativeButtonText("Gunakan password")
            .build()

        prompt.authenticate(info)
    }

    // Menampilkan pesan ketika biometrik tidak tersedia
    private fun showBiometricMessage(message: String) {

        MaterialAlertDialogBuilder(this)
            .setTitle("Biometrik tidak dapat digunakan")
            .setMessage(message)
            .setPositiveButton("Mengerti", null)
            .show()
    }

    // Navigasi menuju halaman utama
    private fun openMainActivity() {

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        intent.putExtra(
            MainActivity.EXTRA_AUTHENTICATED,
            true
        )

        startActivity(intent)
        finish()
    }
}
