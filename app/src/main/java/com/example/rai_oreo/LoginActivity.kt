/**
 * Penerapan praktikum: Modul 2 (komponen input & event listener), Modul 3 (Intent),
 * Modul 6 (SharedPreferences sesi), Modul 11 (tampilan sheet + gradien mengikuti desain Compose).
 */
package com.example.rai_oreo

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.rai_oreo.data.Prefs
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityLoginBinding
import com.example.rai_oreo.util.Biometric

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var sandiTerlihat = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etEmail.setText(Prefs.email(this))
        binding.cbIngat.isChecked = Prefs.ingat(this)

        binding.btnMasuk.setOnClickListener { masuk() }
        binding.ivTogglePassword.setOnClickListener { alihkanSandi() }
        binding.btnSidikJari.setOnClickListener { sidikJari() }
        binding.btnGoogle.setOnClickListener {
            tampilkanCatatan("Masuk dengan Google memerlukan konfigurasi OAuth. Gunakan email demo untuk sekarang.")
        }
        binding.tvDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvLupa.setOnClickListener {
            startActivity(Intent(this, LupaPasswordActivity::class.java))
        }

        siapkanSidikJari()
    }

    /** Menampilkan/menyembunyikan isi password (ikon mata). */
    private fun alihkanSandi() {
        sandiTerlihat = !sandiTerlihat
        val posisi = binding.etPassword.selectionEnd
        binding.etPassword.inputType = if (sandiTerlihat) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        binding.etPassword.setSelection(posisi.coerceAtLeast(0))
        binding.ivTogglePassword.setImageResource(
            if (sandiTerlihat) R.drawable.ic_eye_off else R.drawable.ic_eye
        )
        binding.ivTogglePassword.contentDescription =
            if (sandiTerlihat) "Sembunyikan password" else "Tampilkan password"
    }

    /** Menyesuaikan tombol sidik jari dengan ketersediaan sensor pada perangkat. */
    private fun siapkanSidikJari() {
        val tersedia = Biometric.tersedia(this)
        binding.btnSidikJari.setBackgroundResource(
            if (tersedia) R.drawable.bg_btn_fingerprint else R.drawable.bg_btn_fingerprint_off
        )
        binding.ivSidik.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, if (tersedia) R.color.blue else R.color.muted)
        )
        binding.tvSidik.setTextColor(
            ContextCompat.getColor(this, if (tersedia) R.color.primary_dark else R.color.muted)
        )
        binding.tvBioNote.text = if (tersedia) {
            "Tempelkan sidik jari pada sensor untuk masuk lebih cepat"
        } else {
            "Fingerprint belum tersedia di perangkat ini"
        }
    }

    private fun masuk() {
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty()

        tampilkanCatatan(null)
        binding.tvEmailError.visibility = View.GONE
        binding.tvPasswordError.visibility = View.GONE
        binding.boxEmail.setBackgroundResource(R.drawable.bg_input)
        binding.boxPassword.setBackgroundResource(R.drawable.bg_input)

        if (email.isEmpty()) {
            binding.tvEmailError.text = "Email wajib diisi"
            binding.tvEmailError.visibility = View.VISIBLE
            binding.boxEmail.setBackgroundResource(R.drawable.bg_input_error)
            return
        }
        if (password.length < 4) {
            binding.tvPasswordError.text = "Password minimal 4 karakter"
            binding.tvPasswordError.visibility = View.VISIBLE
            binding.boxPassword.setBackgroundResource(R.drawable.bg_input_error)
            return
        }

        SimperRepository.login(email, password)
            .onSuccess { user ->
                Prefs.simpanSesi(this, user.email, user.nama, binding.cbIngat.isChecked)
                bukaBeranda()
            }
            .onFailure { error ->
                binding.tvPasswordError.text = error.message ?: "Email atau password salah"
                binding.tvPasswordError.visibility = View.VISIBLE
                binding.boxPassword.setBackgroundResource(R.drawable.bg_input_error)
            }
    }

    private fun sidikJari() {
        if (!Biometric.tersedia(this)) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Sidik jari belum tersedia")
                .setMessage("Perangkat ini belum memiliki sensor sidik jari yang aktif. Silakan masuk dengan email dan password.")
                .setPositiveButton("Mengerti", null)
                .show()
            return
        }
        Biometric.minta(
            activity = this,
            onSukses = {
                SimperRepository.loginWithBiometric()
                Prefs.simpanSesi(this, Prefs.email(this), Prefs.nama(this), true)
                bukaBeranda()
            },
            onGagal = { pesan ->
                Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun tampilkanCatatan(teks: String?) {
        binding.tvNote.text = teks.orEmpty()
        binding.tvNote.visibility = if (teks == null) View.GONE else View.VISIBLE
    }

    private fun bukaBeranda() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}
