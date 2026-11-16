package com.example.rai_oreo.screen

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.databinding.ActivityUbahPasswordBinding

class UbahPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUbahPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSimpan.setOnClickListener {
            val lama = binding.etLama.text?.toString().orEmpty()
            val baru = binding.etBaru.text?.toString().orEmpty()
            val ulangi = binding.etUlangi.text?.toString().orEmpty()

            binding.tilLama.error = null
            binding.tilBaru.error = null
            binding.tilUlangi.error = null

            when {
                lama.length < 4 -> binding.tilLama.error = "Password lama salah"
                baru.length < 6 -> binding.tilBaru.error = "Password minimal 6 karakter"
                baru != ulangi -> binding.tilUlangi.error = "Password tidak sama"
                else -> {
                    Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
