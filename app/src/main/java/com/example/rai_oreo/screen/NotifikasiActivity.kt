/**
 * Penerapan praktikum: Modul 10 (ListView + custom adapter), Modul 5 (Option Menu).
 */
package com.example.rai_oreo.screen

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.NotifikasiAdapter
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityNotifikasiBinding

class NotifikasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotifikasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.menu_baca) {
                SimperRepository.markNotificationsRead()
                muat()
                true
            } else {
                false
            }
        }
        muat()
    }

    private fun muat() {
        val daftar = SimperRepository.notifications
        binding.lvNotif.adapter = NotifikasiAdapter(daftar)
        binding.emptyNotif.visibility = if (daftar.isEmpty()) View.VISIBLE else View.GONE
        binding.lvNotif.visibility = if (daftar.isEmpty()) View.GONE else View.VISIBLE
    }
}
