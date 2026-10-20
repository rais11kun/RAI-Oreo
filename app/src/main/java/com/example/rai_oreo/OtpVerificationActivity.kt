package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.databinding.ActivityOtpVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val phone = intent.getStringExtra(EXTRA_PHONE).orEmpty()
        val username = intent.getStringExtra(EXTRA_USERNAME).orEmpty()
        val password = intent.getStringExtra(EXTRA_PASSWORD).orEmpty()

        binding.tvOtpDescription.text = "Untuk simulasi UTS, masukkan nomor handphone yang didaftarkan sebagai kode OTP: $phone"

        binding.btnVerifyOtp.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()

            // Sesuai soal UTS: nilai OTP harus sama persis dengan nomor handphone saat registrasi.
            if (otp.isEmpty() || otp != phone) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Verifikasi gagal")
                    .setMessage("Kode OTP kosong atau tidak sama dengan nomor handphone saat registrasi.")
                    .setPositiveButton("Coba lagi", null)
                    .show()
                return@setOnClickListener
            }

            // Data registrasi disimpan hanya setelah OTP dinyatakan valid.
            val sharedPref = getSharedPreferences("simper_pref", MODE_PRIVATE)
            sharedPref.edit()
                .putString("name", name)
                .putString("phone", phone)
                .putString("username", username)
                .putString("password", password)
                .putBoolean("isRegistered", true)
                .putBoolean("isLogin", false)
                .apply()

            Toast.makeText(this, "Registrasi berhasil. Silakan login.", Toast.LENGTH_LONG).show()
            val loginIntent = Intent(this, LoginActivity::class.java)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(loginIntent)
            finish()
        }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_PHONE = "extra_phone"
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_PASSWORD = "extra_password"
    }
}
