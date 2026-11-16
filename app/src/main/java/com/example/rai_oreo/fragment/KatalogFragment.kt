/**
 * Penerapan praktikum: Modul 10 (RecyclerView grid), Modul 2 (Spinner + onClick), Modul 3 (resource string array).
 */
package com.example.rai_oreo.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.ProdukAdapter
import com.example.rai_oreo.data.Service
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.FragmentKatalogBinding
import com.example.rai_oreo.screen.DetailLayananActivity

class KatalogFragment : Fragment() {

    private var _binding: FragmentKatalogBinding? = null
    private val binding get() = _binding!!

    private var kategori = "Semua"
    private var kataKunci = ""
    private var urutan = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentKatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvSubJudul.text = "${SimperRepository.services.count { it.aktif }} layanan cetak New Reklame Advertising"
        binding.rvProduk.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.etCariKatalog.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) = Unit
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                kataKunci = s?.toString().orEmpty()
                tampilkan()
            }
        })

        binding.spUrut.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                urutan = position
                tampilkan()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spUrut.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.urutan_katalog),
        )

        siapkanChip()
        tampilkan()
    }

    private fun siapkanChip() {
        val kategoriTersedia = listOf("Semua") + SimperRepository.services.map { it.kategori }.distinct().sorted()
        val chips = mutableListOf<Chip>()

        kategoriTersedia.forEachIndexed { index, nama ->
            val chip = layoutInflater.inflate(R.layout.view_chip, binding.chipKategori, false) as Chip
            chip.id = View.generateViewId()
            chip.text = nama
            chip.isChecked = index == 0
            chip.isChipIconVisible = false
            chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_bg_selector)
            chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_text))
            chip.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    kategori = nama
                    tampilkan()
                }
            }
            chips += chip
            binding.chipKategori.addView(chip)
        }

        binding.chipKategori.setOnCheckedStateChangeListener { group: ChipGroup, _: MutableList<Int> ->
            if (group.checkedChipIds.isEmpty()) {
                chips.firstOrNull()?.isChecked = true
            }
        }
    }

    private fun tampilkan() {
        val hasil = SimperRepository.services
            .filter { it.aktif }
            .filter { kategori == "Semua" || it.kategori == kategori }
            .filter { kataKunci.isBlank() || it.nama.contains(kataKunci, true) }
            .let { daftar ->
                when (urutan) {
                    1 -> daftar.sortedBy { it.hargaMulai }
                    2 -> daftar.sortedByDescending { it.hargaMulai }
                    3 -> daftar.sortedBy { it.nama }
                    else -> daftar.sortedByDescending { it.jumlahPesanan }
                }
            }

        binding.rvProduk.adapter = ProdukAdapter(hasil) { bukaDetail(it) }
        binding.tvJumlah.text = "${hasil.size} layanan"
        binding.emptyKatalog.visibility = if (hasil.isEmpty()) View.VISIBLE else View.GONE
        binding.rvProduk.visibility = if (hasil.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun bukaDetail(service: Service) {
        startActivity(Intent(requireContext(), DetailLayananActivity::class.java).putExtra("serviceId", service.id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
