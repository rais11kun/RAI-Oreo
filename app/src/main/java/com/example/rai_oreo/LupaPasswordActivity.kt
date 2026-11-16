package com.example.rai_oreo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.rai_oreo.databinding.ActivityLupaPasswordBinding

class LupaPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLupaPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLupaPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnKirim.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty().trim()
            if (!email.contains("@")) {
                binding.tilEmail.error = "Masukkan email yang valid"
                return@setOnClickListener
            }
            MaterialAlertDialogBuilder(this)
                .setTitle("Tautan terkirim")
                .setMessage("Kami mengirim tautan pengaturan ulang password ke $email. Periksa kotak masuk Anda.")
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }
    }
}
