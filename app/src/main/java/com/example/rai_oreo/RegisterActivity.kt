/**
 * Penerapan praktikum: Modul 2 (komponen form), Modul 3 (Intent kirim data antarlayar).
 */
package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.data.Prefs
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnDaftar.setOnClickListener { daftar() }
        binding.tvMasuk.setOnClickListener { finish() }
    }

    private fun daftar() {
        val nama = binding.etNama.text?.toString().orEmpty()
        val email = binding.etEmail.text?.toString().orEmpty()
        val telepon = binding.etTelepon.text?.toString().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()

        binding.tilNama.error = null
        binding.tilEmail.error = null
        binding.tilTelepon.error = null
        binding.tilPassword.error = null

        SimperRepository.register(nama, email, telepon, password)
            .onSuccess { user ->
                Prefs.simpanSesi(this, user.email, user.nama, true)
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .onFailure { error ->
                when {
                    nama.isBlank() -> binding.tilNama.error = "Nama wajib diisi"
                    !email.contains("@") -> binding.tilEmail.error = "Format email tidak valid"
                    telepon.isBlank() -> binding.tilTelepon.error = "Nomor telepon wajib diisi"
                    password.length < 6 -> binding.tilPassword.error = "Password minimal 6 karakter"
                    else -> Toast.makeText(this, error.message ?: "Gagal daftar", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
