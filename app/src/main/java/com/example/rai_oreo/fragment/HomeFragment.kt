/**
 * Penerapan praktikum: Modul 7 (Fragment), Modul 10 (ListView saran pencarian), Modul 2 (event onClick).
 */
package com.example.rai_oreo.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rai_oreo.MainActivity
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.ProdukAdapter
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.FragmentHomeBinding
import com.example.rai_oreo.screen.DetailLayananActivity
import com.example.rai_oreo.screen.DetailPesananActivity
import com.example.rai_oreo.screen.NotifikasiActivity
import com.example.rai_oreo.screen.ScanQrActivity
import com.example.rai_oreo.util.Badge

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = SimperRepository.currentUser
        binding.tvNama.text = "${user?.nama?.substringBefore(" ") ?: "Pelanggan"}!"

        binding.ivNotif.setOnClickListener {
            startActivity(Intent(requireContext(), NotifikasiActivity::class.java))
        }
        binding.ivScan.setOnClickListener {
            startActivity(Intent(requireContext(), ScanQrActivity::class.java))
        }
        binding.btnHero.setOnClickListener { (activity as? MainActivity)?.bukaTab(R.id.tab_katalog) }
        binding.btnPromo.setOnClickListener { (activity as? MainActivity)?.bukaTab(R.id.tab_katalog) }
        binding.tvLihatKatalog.setOnClickListener { (activity as? MainActivity)?.bukaTab(R.id.tab_katalog) }
        binding.tvLihatSemua.setOnClickListener { (activity as? MainActivity)?.bukaTab(R.id.tab_pesanan) }

        siapkanPencarian()
        siapkanPopuler()
    }

    override fun onResume() {
        super.onResume()
        tampilkanPesananAktif()
        val belumDibaca = SimperRepository.unreadNotifications
        binding.tvBadge.visibility = if (belumDibaca > 0) View.VISIBLE else View.GONE
        binding.tvBadge.text = belumDibaca.toString()
    }

    private fun siapkanPencarian() {
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        binding.lvSaran.adapter = adapter

        binding.etCari.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) = Unit
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                val kata = s?.toString().orEmpty().trim()
                val hasil = if (kata.isEmpty()) emptyList()
                else SimperRepository.services.filter { it.aktif && it.nama.contains(kata, true) }

                adapter.clear()
                adapter.addAll(hasil.map { "${it.nama} · mulai ${it.hargaMulai.toRupiah()}" })
                adapter.notifyDataSetChanged()
                binding.lvSaran.visibility = if (hasil.isEmpty()) View.GONE else View.VISIBLE

                binding.lvSaran.setOnItemClickListener { _, _, position, _ ->
                    val service = hasil.getOrNull(position) ?: return@setOnItemClickListener
                    binding.etCari.setText("")
                    binding.lvSaran.visibility = View.GONE
                    bukaDetail(service.id)
                }
            }
        })
    }

    private fun siapkanPopuler() {
        val populer = SimperRepository.services.filter { it.populer && it.aktif }
        binding.rvPopuler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPopuler.adapter = ProdukAdapter(populer, horizontal = true) { bukaDetail(it.id) }
    }

    private fun tampilkanPesananAktif() {
        val aktif = SimperRepository.activeOrders.firstOrNull()

        if (aktif == null) {
            binding.cardAktif.visibility = View.GONE
            binding.cardKosong.visibility = View.VISIBLE
            return
        }

        binding.cardAktif.visibility = View.VISIBLE
        binding.cardKosong.visibility = View.GONE

        binding.ivAktifGambar.setImageResource(aktif.gambar)
        binding.tvAktifNomor.text = aktif.nomor
        binding.tvAktifLayanan.text = "${aktif.serviceNama} ${aktif.ukuran}"
        Badge.status(binding.tvAktifStatus, aktif.status)
        Badge.progres(
            steps = listOf(binding.step1, binding.step2, binding.step3, binding.step4),
            lines = listOf(binding.line1, binding.line2, binding.line3),
            langkah = aktif.status.step.coerceAtLeast(0),
        )

        binding.cardAktif.setOnClickListener {
            startActivity(
                Intent(requireContext(), DetailPesananActivity::class.java)
                    .putExtra(DetailPesananActivity.EXTRA_NOMOR, aktif.nomor)
            )
        }
    }

    private fun bukaDetail(id: Int) {
        startActivity(Intent(requireContext(), DetailLayananActivity::class.java).putExtra("serviceId", id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
