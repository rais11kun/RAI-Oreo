package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
            val sharedPref = getSharedPreferences("simper_pref", MODE_PRIVATE)
            val savedUsername = sharedPref.getString("username", null)
            val savedPassword = sharedPref.getString("password", null)

            // Aturan praktikum: login boleh jika username == password,
            // ATAU jika keduanya cocok dengan data registrasi di SharedPreferences.
            val matchesPracticumRule = username.isNotEmpty() && password.isNotEmpty() && username == password
            val matchesRegisteredAccount = username.isNotEmpty() && password.isNotEmpty() &&
                    username == savedUsername && password == savedPassword

            if (matchesPracticumRule || matchesRegisteredAccount) {
                sharedPref.edit()
                    .putBoolean("isLogin", true)
                    .putString("username", username)
                    .apply()

                Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Login gagal")
                    .setMessage("Periksa username dan password. Gunakan username dan password yang sama atau akun yang sudah didaftarkan.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}
