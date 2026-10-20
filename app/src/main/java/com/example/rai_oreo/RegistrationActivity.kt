package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import com.example.rai_oreo.databinding.ActivityRegistrationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val username = binding.etNewUsername.text.toString().trim()
            val password = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (name.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showMessage("Data belum lengkap", "Isi semua 5 input registrasi terlebih dahulu.")
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                showMessage("Password tidak cocok", "Password dan konfirmasi password harus sama.")
                return@setOnClickListener
            }
            if (phone.length < 8) {
                showMessage("Nomor handphone tidak valid", "Masukkan nomor handphone minimal 8 digit.")
                return@setOnClickListener
            }

            // Data hanya diteruskan sementara. SharedPreferences baru ditulis setelah OTP valid.
            val intent = Intent(this, OtpVerificationActivity::class.java).apply {
                putExtra(OtpVerificationActivity.EXTRA_NAME, name)
                putExtra(OtpVerificationActivity.EXTRA_PHONE, phone)
                putExtra(OtpVerificationActivity.EXTRA_USERNAME, username)
                putExtra(OtpVerificationActivity.EXTRA_PASSWORD, password)
            }
            startActivity(intent)
        }
    }

    private fun showMessage(title: String, message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
