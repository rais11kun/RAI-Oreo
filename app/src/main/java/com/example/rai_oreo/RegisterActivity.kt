/**
 * Penerapan praktikum: Modul 2 (komponen form), Modul 3 (Intent), Modul 6 (SharedPreferences),
 * Modul 12 (fitur Register meng-insert ke tabel users Supabase lewat Retrofit).
 */
package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rai_oreo.data.Prefs
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.remote.RemoteRepository
import com.example.rai_oreo.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

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
        val nama = binding.etNama.text?.toString().orEmpty().trim()
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val telepon = binding.etTelepon.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty()

        binding.tilNama.error = null
        binding.tilEmail.error = null
        binding.tilTelepon.error = null
        binding.tilPassword.error = null

        if (nama.isEmpty()) {
            binding.tilNama.error = "Nama wajib diisi"
            return
        }
        if (!email.contains("@")) {
            binding.tilEmail.error = "Format email tidak valid"
            return
        }
        if (telepon.isEmpty()) {
            binding.tilTelepon.error = "Nomor telepon wajib diisi"
            return
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password minimal 6 karakter"
            return
        }

        // Modul 12: bila Supabase sudah dikonfigurasi, akun dibuat di tabel users dulu.
        if (!RemoteRepository.siap()) {
            mulaiSesi(nama, email, telepon, password)
            return
        }

        binding.btnDaftar.isEnabled = false
        lifecycleScope.launch {
            RemoteRepository.register(nama, email, password, telepon)
                .onSuccess { mulaiSesi(nama, email, telepon, password) }
                .onFailure { error ->
                    binding.btnDaftar.isEnabled = true
                    val pesan = error.message.orEmpty()
                    if (pesan.contains("duplicate", true)) {
                        binding.tilEmail.error = "Email sudah terdaftar"
                    }
                    Toast.makeText(
                        this@RegisterActivity,
                        "Gagal mendaftar ke server: $pesan",
                        Toast.LENGTH_LONG,
                    ).show()
                }
        }
    }

    /** Setelah akun tersimpan di Supabase, sesi lokal dibuat dan masuk ke Beranda. */
    private fun mulaiSesi(nama: String, email: String, telepon: String, password: String) {
        val user = SimperRepository.register(nama, email, telepon, password).getOrNull()
            ?: SimperRepository.masukSebagai(email)

        Prefs.simpanSesi(this, email, user?.nama ?: nama, true)
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}
