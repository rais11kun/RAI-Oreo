package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()

            /*
             * Validasi mengikuti modul Pertemuan 6:
             * username harus sama dengan password dan keduanya tidak boleh kosong.
             * Contoh kredensial valid: username "rais", password "rais".
             * Ini validasi lokal untuk praktikum, bukan autentikasi server produksi.
             */
            if (username.isNotEmpty() && password.isNotEmpty() && username == password) {
                // SharedPreferences ditulis hanya setelah validasi berhasil.
                val sharedPref = getSharedPreferences("simper_pref", MODE_PRIVATE)
                sharedPref.edit()
                    .putBoolean("isLogin", true)
                    .putString("username", username)
                    .apply()

                Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Jika salah atau kosong, tampilkan dialog dan jangan ubah status login.
                AlertDialog.Builder(this)
                    .setTitle("Login gagal")
                    .setMessage("Username dan password harus sama. Silahkan coba lagi.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
