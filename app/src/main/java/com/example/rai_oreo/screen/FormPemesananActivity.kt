/**
 * Penerapan praktikum: Modul 2 (komponen, Spinner, onClick), Modul 3 (Intent, Dialog),
 * Modul 11 (indikator langkah + alur pager manual tiga halaman),
 * Modul 10 (activity result untuk memilih berkas).
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.rai_oreo.R
import com.example.rai_oreo.data.OrderDraft
import com.example.rai_oreo.data.Service
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ActivityFormPemesananBinding

class FormPemesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormPemesananBinding
    private lateinit var service: Service

    private var langkah = 0
    private var jumlah = 1

    private val pilihFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                OrderDraft.desainNama = ambilNama(uri)
                OrderDraft.desainUkuran = "Siap diunggah"
                tampilkanFile()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = SimperRepository.serviceById(
            intent.getIntExtra("serviceId", OrderDraft.serviceId)
        ) ?: run {
            finish()
            return
        }
        if (OrderDraft.serviceId != service.id) OrderDraft.mulai(service)

        binding.toolbar.setNavigationOnClickListener { kembali() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (langkah > 0) {
                    tampilkanLangkah(langkah - 1)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        siapkanRingkasanLayanan()
        siapkanPilihan()
        siapkanAksi()
        tampilkanFile()
        tampilkanLangkah(0)
    }

    private fun siapkanRingkasanLayanan() {
        binding.ivLayanan.setImageResource(service.gambar)
        binding.tvLayanan.text = service.nama
        binding.tvHargaSatuan.text = "${service.hargaMulai.toRupiah()} / ${service.satuan}"

        val bagian = OrderDraft.ukuran.split(" x ")
        if (bagian.size == 2) {
            binding.etLebar.setText(bagian[0])
            binding.etTinggi.setText(bagian[1])
        }
        jumlah = OrderDraft.jumlah.coerceAtLeast(1)
        binding.tvJumlah.text = jumlah.toString()
        binding.etCatatan.setText(OrderDraft.catatan)
    }

    private fun siapkanPilihan() {
        val bahan = ArrayAdapter(this, android.R.layout.simple_spinner_item, service.bahan)
        bahan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spBahan.adapter = bahan
        binding.spBahan.setSelection(service.bahan.indexOf(OrderDraft.bahan).coerceAtLeast(0))

        val finishing = ArrayAdapter(this, android.R.layout.simple_spinner_item, service.finishing)
        finishing.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFinishing.adapter = finishing
        binding.spFinishing.setSelection(
            service.finishing.indexOf(OrderDraft.finishing).coerceAtLeast(0)
        )

        binding.etLebar.doAfterTextChanged { hitung() }
        binding.etTinggi.doAfterTextChanged { hitung() }

        val pantauPilihan = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = hitung()
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spBahan.onItemSelectedListener = pantauPilihan
        binding.spFinishing.onItemSelectedListener = pantauPilihan
    }

    private fun siapkanAksi() {
        binding.btnMinus.setOnClickListener {
            jumlah = (jumlah - 1).coerceAtLeast(1)
            binding.tvJumlah.text = jumlah.toString()
            hitung()
        }
        binding.btnPlus.setOnClickListener {
            jumlah += 1
            binding.tvJumlah.text = jumlah.toString()
            hitung()
        }

        binding.boxUpload.setOnClickListener { pilihFile.launch("*/*") }
        binding.cardFile.setOnClickListener { pilihFile.launch("*/*") }
        binding.ivHapusFile.setOnClickListener {
            OrderDraft.desainNama = ""
            OrderDraft.desainUkuran = ""
            tampilkanFile()
        }

        binding.cbSetuju.setOnCheckedChangeListener { _, _ -> perbaruiBawah() }

        binding.btnLanjut.setOnClickListener {
            when (langkah) {
                0 -> {
                    simpanDetail()
                    tampilkanLangkah(1)
                }
                1 -> {
                    simpanDesain()
                    tampilkanLangkah(2)
                    isiKonfirmasi()
                }
                else -> tanyaKonfirmasi()
            }
        }
    }

    // ---------------------------------------------------------------- langkah

    private fun tampilkanLangkah(n: Int) {
        langkah = n
        binding.scrollDetail.visibility = if (n == 0) View.VISIBLE else View.GONE
        binding.scrollUpload.visibility = if (n == 1) View.VISIBLE else View.GONE
        binding.scrollKonfirmasi.visibility = if (n == 2) View.VISIBLE else View.GONE

        bulatkan(binding.step1Circle, n, 1)
        bulatkan(binding.step2Circle, n, 2)
        bulatkan(binding.step3Circle, n, 3)
        warnaiLabel(binding.step1Label, n >= 0, n == 0)
        warnaiLabel(binding.step2Label, n >= 1, n == 1)
        warnaiLabel(binding.step3Label, n >= 2, n == 2)

        binding.garis1.setBackgroundResource(
            if (n >= 1) R.drawable.bg_line_done else R.drawable.bg_line_idle
        )
        binding.garis2.setBackgroundResource(
            if (n >= 2) R.drawable.bg_line_done else R.drawable.bg_line_idle
        )

        hitung()
        (if (n == 0) binding.scrollDetail else if (n == 1) binding.scrollUpload else binding.scrollKonfirmasi)
            .scrollTo(0, 0)
    }

    private fun bulatkan(view: android.widget.TextView, aktif: Int, nomor: Int) {
        val sudah = aktif >= nomor
        val sekarang = aktif == nomor - 1
        view.setBackgroundResource(
            if (sudah || sekarang) R.drawable.bg_step_done else R.drawable.bg_step_idle
        )
        view.text = if (aktif > nomor - 1) "✓" else nomor.toString()
        view.setTextColor(
            if (sudah || sekarang) {
                getColor(R.color.white)
            } else {
                getColor(R.color.muted)
            }
        )
    }

    private fun warnaiLabel(view: android.widget.TextView, aktif: Boolean, sekarang: Boolean) {
        view.setTextColor(getColor(if (aktif) R.color.navy800 else R.color.muted))
        view.setTypeface(null, if (sekarang) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
    }

    // -------------------------------------------------------------- perhitungan

    private fun hitung() {
        val lebar = binding.etLebar.text?.toString().orEmpty().replace(',', '.')
        val tinggi = binding.etTinggi.text?.toString().orEmpty().replace(',', '.')
        val l = lebar.toDoubleOrNull() ?: 0.0
        val t = tinggi.toDoubleOrNull() ?: 0.0

        val total = if (service.satuan.contains("m")) {
            (service.hargaMulai * l * t * jumlah).toInt()
        } else {
            service.hargaMulai * jumlah
        }
        OrderDraft.total = total

        binding.tvRincian.text = if (service.satuan.contains("m")) {
            "${service.hargaMulai.toRupiah()}/m² × ${if (lebar.isBlank()) "0" else lebar} m × " +
                "${if (tinggi.isBlank()) "0" else tinggi} m × $jumlah pcs"
        } else {
            "${service.hargaMulai.toRupiah()} × $jumlah pcs"
        }
        binding.tvBottomValue.text = total.toRupiah()
        perbaruiBawah()
    }

    private fun ukuranValid(): Boolean {
        val l = binding.etLebar.text?.toString().orEmpty().replace(',', '.').toDoubleOrNull() ?: 0.0
        val t = binding.etTinggi.text?.toString().orEmpty().replace(',', '.').toDoubleOrNull() ?: 0.0
        return l > 0.0 && t > 0.0
    }

    private fun perbaruiBawah() {
        binding.tvBottomLabel.text =
            if (langkah == 1) "Total sementara" else "Estimasi Harga"
        binding.rowHarga.visibility = if (langkah == 2) View.GONE else View.VISIBLE
        binding.tvBtnLanjut.text =
            if (langkah == 2) "Konfirmasi Pesanan" else "Lanjutkan"

        val aktif = when (langkah) {
            0 -> ukuranValid()
            2 -> binding.cbSetuju.isChecked
            else -> true
        }
        binding.btnLanjut.isEnabled = aktif
        binding.btnLanjut.setBackgroundResource(
            if (aktif) R.drawable.bg_btn_primary else R.drawable.bg_btn_disabled
        )

        val lp = binding.btnLanjut.layoutParams as LinearLayout.LayoutParams
        lp.width = if (langkah == 2) {
            LinearLayout.LayoutParams.MATCH_PARENT
        } else {
            (170 * resources.displayMetrics.density).toInt()
        }
        binding.btnLanjut.layoutParams = lp
    }

    // ------------------------------------------------------------------- data

    private fun simpanDetail() {
        OrderDraft.serviceId = service.id
        OrderDraft.ukuran = "${binding.etLebar.text} x ${binding.etTinggi.text}"
        OrderDraft.jumlah = jumlah
        OrderDraft.bahan = binding.spBahan.selectedItem?.toString().orEmpty()
        OrderDraft.finishing = binding.spFinishing.selectedItem?.toString().orEmpty()
        OrderDraft.catatan = binding.etCatatan.text?.toString().orEmpty()
    }

    private fun simpanDesain() {
        // File sudah tersimpan di OrderDraft saat dipilih.
        if (OrderDraft.desainUkuran.isBlank()) OrderDraft.desainUkuran = "-"
    }

    private fun tampilkanFile() {
        val ada = OrderDraft.desainNama.isNotBlank()
        binding.cardFile.visibility = if (ada) View.VISIBLE else View.GONE
        binding.tvNamaFile.text = OrderDraft.desainNama
        binding.tvUkuranFile.text = OrderDraft.desainUkuran
    }

    private fun isiKonfirmasi() {
        binding.ivKonfirmasiLayanan.setImageResource(service.gambar)
        binding.tvKonfirmasiLayanan.text = service.nama
        binding.tvUkuranInfo.text = "${binding.etLebar.text} x ${binding.etTinggi.text} m"
        binding.tvJumlahInfo.text = "$jumlah pcs"
        binding.tvBahanInfo.text = OrderDraft.bahan
        binding.tvFinishingInfo.text = OrderDraft.finishing
        binding.tvEstimasiInfo.text = service.estimasi
        binding.tvFileNama.text = OrderDraft.desainNama.ifBlank { "Belum ada file desain" }
        binding.tvFileInfo.text = OrderDraft.desainUkuran.ifBlank { "Kirim desain menyusul" }
        binding.tvCatatanFinal.text =
            OrderDraft.catatan.ifBlank { "Tidak ada catatan tambahan." }
        binding.tvSubtotal.text = OrderDraft.total.toRupiah()
        binding.tvTotalFinal.text = OrderDraft.total.toRupiah()
    }

    private fun tanyaKonfirmasi() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Buat pesanan sekarang?")
            .setMessage("Pesanan akan masuk antrian produksi dan statusnya bisa dipantau di tab Pesanan.")
            .setNegativeButton("Periksa lagi", null)
            .setPositiveButton("Buat Pesanan") { _, _ -> buatPesanan() }
            .show()
    }

    private fun buatPesanan() {
        val order = SimperRepository.createOrder(
            service = service,
            ukuran = OrderDraft.ukuran,
            jumlah = OrderDraft.jumlah,
            bahan = OrderDraft.bahan,
            finishing = OrderDraft.finishing,
            catatan = OrderDraft.catatan,
            desainNama = OrderDraft.desainNama.ifBlank { "belum ada file" },
            desainUkuran = OrderDraft.desainUkuran.ifBlank { "-" },
            total = OrderDraft.total,
        )
        Toast.makeText(this, "Pesanan ${order.nomor} dibuat", Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this, PesananSuksesActivity::class.java)
                .putExtra("nomor", order.nomor)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        OrderDraft.reset()
        finish()
    }

    private fun kembali() {
        if (langkah > 0) tampilkanLangkah(langkah - 1) else finish()
    }

    private fun ambilNama(uri: Uri): String {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) return cursor.getString(index)
        }
        return uri.lastPathSegment ?: "desain.pdf"
    }
}
