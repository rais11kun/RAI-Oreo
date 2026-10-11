package com.example.rai_oreo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.databinding.ActivityDeveloperProfileBinding

class DeveloperProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeveloperProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeveloperProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.profile_title)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
