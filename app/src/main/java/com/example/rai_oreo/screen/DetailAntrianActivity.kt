/**
 * Penerapan praktikum: Modul 2 (Spinner), Modul 3 (Intent kirim data nomor pesanan).
 */
package com.example.rai_oreo.screen

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.TimelineAdapter
import com.example.rai_oreo.data.OrderStatus
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityDetailAntrianBinding
import com.example.rai_oreo.util.Badge

class DetailAntrianActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAntrianBinding
    private var nomor = ""
    private var statusPilihan = OrderStatus.BARU

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAntrianBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nomor = intent.getStringExtra(DetailPesananActivity.EXTRA_NOMOR).orEmpty()
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.spStatus.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.status_pesanan),
        )

        binding.spStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                statusPilihan = OrderStatus.entries.getOrElse(position) { OrderStatus.BARU }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        binding.btnUpdate.setOnClickListener {
            SimperRepository.updateStatus(nomor, statusPilihan)
            Toast.makeText(this, "Status diubah menjadi ${statusPilihan.label}", Toast.LENGTH_SHORT).show()
            tampilkan()
        }
    }

    override fun onResume() {
        super.onResume()
        tampilkan()
    }

    private fun tampilkan() {
        val order = SimperRepository.orderByNumber(nomor) ?: return

        binding.tvNomor.text = order.nomor
        binding.tvPelanggan.text = order.pelanggan
        binding.tvTanggal.text = order.tanggal
        Badge.status(binding.tvStatus, order.status)

        binding.tvDetail.text = buildString {
            appendLine("Layanan: ${order.serviceNama}")
            appendLine("Ukuran: ${order.ukuran}")
            appendLine("Jumlah: ${order.jumlah} pcs")
            appendLine("Bahan: ${order.bahan}")
            append("Finishing: ${order.finishing}")
        }
        binding.tvCatatan.text = "Catatan: ${order.catatan.ifBlank { "-" }}"

        binding.rvTimeline.layoutManager = LinearLayoutManager(this)
        binding.rvTimeline.adapter = TimelineAdapter(order.timeline)

        binding.spStatus.setSelection(order.status.ordinal.coerceIn(0, OrderStatus.entries.size - 1))
    }
}
