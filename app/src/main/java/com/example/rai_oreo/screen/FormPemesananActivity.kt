/**
 * Penerapan praktikum: Modul 2 (komponen, onClick), Modul 3 (Intent kirim data + Dialog DatePicker).
 */
package com.example.rai_oreo.screen

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.rai_oreo.R
import com.example.rai_oreo.data.OrderDraft
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ActivityFormPemesananBinding

class FormPemesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormPemesananBinding

    private val pilihFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val nama = ambilNama(uri)
            OrderDraft.desainNama = nama
            OrderDraft.desainUkuran = "Siap diunggah"
            binding.tvUploadJudul.text = nama
            binding.tvUploadInfo.text = "File desain terpilih"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val service = SimperRepository.serviceById(intent.getIntExtra("serviceId", OrderDraft.serviceId))
        if (service == null) {
            finish()
            return
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.ivLayanan.setImageResource(service.gambar)
        binding.tvLayanan.text = service.nama
        binding.tvHargaSatuan.text = "${service.hargaMulai.toRupiah()} / ${service.satuan}"

        binding.etUkuran.setText(OrderDraft.ukuran)
        binding.etBahan.setText(service.bahan.firstOrNull().orEmpty())
        binding.etFinishing.setText(service.finishing.firstOrNull().orEmpty())
        binding.etCatatan.setText(OrderDraft.catatan)
        binding.tvJumlah.text = OrderDraft.jumlah.toString()

        binding.etBahan.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, service.bahan))
        binding.etFinishing.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, service.finishing))

        binding.btnMinus.setOnClickListener {
            val baru = (OrderDraft.jumlah - 1).coerceAtLeast(1)
            OrderDraft.jumlah = baru
            binding.tvJumlah.text = baru.toString()
            hitung(service.hargaMulai)
        }
        binding.btnPlus.setOnClickListener {
            val baru = OrderDraft.jumlah + 1
            OrderDraft.jumlah = baru
            binding.tvJumlah.text = baru.toString()
            hitung(service.hargaMulai)
        }

        binding.etUkuran.doAfterTextChanged { hitung(service.hargaMulai) }
        binding.btnPilihFile.setOnClickListener { pilihFile.launch("*/*") }
        binding.cardUpload.setOnClickListener { pilihFile.launch("*/*") }

        // Dialog tanggal (DatePickerDialog) untuk rencana pengambilan pesanan.
        binding.rowTanggal.setOnClickListener {
            DatePickerDialog(
                this,
                { _, tahun, bulan, hari ->
                    val teks = "%02d/%02d/%d".format(hari, bulan + 1, tahun)
                    binding.tvTanggal.text = teks
                    OrderDraft.tanggalAmbil = teks
                },
                2026,
                8,
                12,
            ).show()
        }

        // RadioGroup: pilihan ambil di toko atau dikirim.
        binding.rgMetode.setOnCheckedChangeListener { _, id ->
            val metode = if (id == com.example.rai_oreo.R.id.rbKirim) "Dikirim" else "Ambil di toko"
            OrderDraft.pengiriman = metode
            binding.rowTanggal.isEnabled = true
        }

        binding.btnLanjut.setOnClickListener {
            val ukuran = binding.etUkuran.text?.toString().orEmpty().trim()
            if (ukuran.isEmpty()) {
                binding.tilUkuran.error = "Ukuran wajib diisi"
                return@setOnClickListener
            }
            binding.tilUkuran.error = null

            OrderDraft.serviceId = service.id
            OrderDraft.ukuran = ukuran
            OrderDraft.bahan = binding.etBahan.text?.toString().orEmpty()
            OrderDraft.finishing = binding.etFinishing.text?.toString().orEmpty()
            OrderDraft.catatan = binding.etCatatan.text?.toString().orEmpty()
            hitung(service.hargaMulai)

            startActivity(Intent(this, KonfirmasiPesananActivity::class.java))
        }

        hitung(service.hargaMulai)
    }

    private fun hitung(hargaSatuan: Int) {
        val total = hargaSatuan * OrderDraft.jumlah
        OrderDraft.total = total
        binding.tvHargaHitung.text = total.toRupiah()
        binding.tvTotal.text = total.toRupiah()
    }

    private fun ambilNama(uri: Uri): String {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) return cursor.getString(index)
        }
        return uri.lastPathSegment ?: "desain.pdf"
    }
}
