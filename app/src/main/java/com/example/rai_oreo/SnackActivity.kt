package com.example.rai_oreo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.databinding.ActivitySnackBinding

class SnackActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySnackBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}