/**
 * Penerapan praktikum: Modul 10 (RecyclerView), Modul 5 (Option Menu tambah).
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.LayananAdapter
import com.example.rai_oreo.data.Service
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityKelolaLayananBinding

class KelolaLayananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKelolaLayananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaLayananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.menu_tambah) {
                bukaForm(null)
                true
            } else {
                false
            }
        }
        binding.btnTambah.setOnClickListener { bukaForm(null) }
        binding.rvLayanan.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        muat()
    }

    private fun muat() {
        val daftar = SimperRepository.services
        binding.tvRingkas.text = "${daftar.size} layanan · ${daftar.count { it.aktif }} aktif"

        binding.rvLayanan.adapter = LayananAdapter(
            daftar = daftar,
            onToggle = { service: Service, aktif: Boolean ->
                if (service.aktif != aktif) SimperRepository.toggleServiceActive(service.id)
                muat()
            },
            onClick = { service: Service -> bukaForm(service.id) },
        )
    }

    private fun bukaForm(serviceId: Int?) {
        startActivity(
            Intent(this, FormLayananActivity::class.java).apply {
                if (serviceId != null) putExtra("serviceId", serviceId)
            }
        )
    }
}
