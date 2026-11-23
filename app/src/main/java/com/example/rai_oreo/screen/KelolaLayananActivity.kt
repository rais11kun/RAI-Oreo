/**
 * Penerapan praktikum: Modul 10 (RecyclerView), Modul 5 (Option Menu tambah),
 * Modul 12 (CRUD layanan lewat REST API Supabase).
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.LayananAdapter
import com.example.rai_oreo.data.Service
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.remote.RemoteMapper
import com.example.rai_oreo.data.remote.RemoteRepository
import com.example.rai_oreo.databinding.ActivityKelolaLayananBinding
import kotlinx.coroutines.launch

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
        if (!RemoteRepository.siap()) {
            tampilkan(SimperRepository.services)
            return
        }
        lifecycleScope.launch {
            RemoteRepository.daftarLayanan()
                .onSuccess { daftar ->
                    val lokal = RemoteMapper.keDaftarService(daftar)
                    SimperRepository.gantiLayanan(lokal)
                    tampilkan(lokal)
                }
                .onFailure {
                    Toast.makeText(
                        this@KelolaLayananActivity,
                        "Gagal memuat dari Supabase, memakai data lokal",
                        Toast.LENGTH_SHORT,
                    ).show()
                    tampilkan(SimperRepository.services)
                }
        }
    }

    private fun tampilkan(daftar: List<Service>) {
        binding.tvRingkas.text = "${daftar.size} layanan · ${daftar.count { it.aktif }} aktif"

        binding.rvLayanan.adapter = LayananAdapter(
            daftar = daftar,
            onToggle = { service: Service, aktif: Boolean -> ubahAktif(service, aktif) },
            onClick = { service: Service -> bukaForm(service.id) },
            onLongClick = { service: Service -> tanyaHapus(service) },
        )
    }

    private fun ubahAktif(service: Service, aktif: Boolean) {
        if (service.aktif == aktif) return
        SimperRepository.toggleServiceActive(service.id)

        val idRemote = RemoteMapper.idLayananRemote(service.id)
        if (!RemoteRepository.siap() || idRemote == null) {
            tampilkan(SimperRepository.services)
            return
        }
        lifecycleScope.launch {
            RemoteRepository.ubahLayananAktif(idRemote, aktif)
                .onFailure {
                    Toast.makeText(
                        this@KelolaLayananActivity,
                        "Gagal menyimpan status ke server",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            muat()
        }
    }

    private fun tanyaHapus(service: Service) {
        if (!RemoteRepository.siap() || RemoteMapper.idLayananRemote(service.id) == null) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Hapus layanan?")
                .setMessage("${service.nama} akan dihapus dari daftar.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus") { _, _ ->
                    SimperRepository.hapusLayanan(service.id)
                    muat()
                }
                .show()
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus layanan?")
            .setMessage("${service.nama} akan dihapus dari database Supabase.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                val idRemote = RemoteMapper.idLayananRemote(service.id) ?: return@setPositiveButton
                lifecycleScope.launch {
                    RemoteRepository.hapusLayanan(idRemote)
                        .onSuccess {
                            Toast.makeText(
                                this@KelolaLayananActivity,
                                "Layanan dihapus dari server",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                        .onFailure {
                            Toast.makeText(
                                this@KelolaLayananActivity,
                                "Gagal menghapus di server",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    muat()
                }
            }
            .show()
    }

    private fun bukaForm(serviceId: Int?) {
        startActivity(
            Intent(this, FormLayananActivity::class.java).apply {
                if (serviceId != null) putExtra("serviceId", serviceId)
            }
        )
    }
}
