/**
 * Penerapan praktikum: Modul 11 (satu halaman di dalam ViewPager2),
 * Modul 10 (ListView + custom adapter), Modul 3 (Intent kirim data nomor pesanan).
 */
package com.example.rai_oreo.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rai_oreo.adapter.PesananAdapter
import com.example.rai_oreo.data.Order
import com.example.rai_oreo.data.OrderStatus
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.FragmentPesananListBinding
import com.example.rai_oreo.screen.DetailPesananActivity

/** Satu halaman daftar pesanan sesuai tab yang dipilih (0 aktif, 1 selesai, 2 dibatalkan). */
class PesananListFragment : Fragment() {

    private var _binding: FragmentPesananListBinding? = null
    private val binding get() = _binding!!

    private val tab: Int get() = requireArguments().getInt(ARG_TAB)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?,
    ): View {
        _binding = FragmentPesananListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvEmptyJudul.setText(
            when (tab) {
                1 -> "Belum ada pesanan selesai"
                2 -> "Tidak ada pesanan dibatalkan"
                else -> "Belum ada pesanan aktif"
            }
        )
        binding.tvEmptyIsi.setText("Pesanan yang Anda buat akan muncul di sini.")

        binding.lvPesanan.setOnItemClickListener { _, _, position, _ ->
            val order = binding.lvPesanan.adapter.getItem(position) as Order
            bukaDetail(order)
        }
    }

    override fun onResume() {
        super.onResume()
        muat()
    }

    private fun muat() {
        val semua = SimperRepository.ordersOf(SimperRepository.currentUser)
        val data = when (tab) {
            1 -> semua.filter { it.status == OrderStatus.SELESAI }
            2 -> semua.filter { it.status == OrderStatus.DIBATALKAN }
            else -> semua.filter { it.status.isActive }
        }

        binding.lvPesanan.adapter = PesananAdapter(data) { order -> bukaDetail(order) }
        binding.emptyPesanan.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
        binding.lvPesanan.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun bukaDetail(order: Order) {
        startActivity(
            Intent(requireContext(), DetailPesananActivity::class.java)
                .putExtra(DetailPesananActivity.EXTRA_NOMOR, order.nomor)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TAB = "tab"

        fun newInstance(tab: Int): PesananListFragment =
            PesananListFragment().apply {
                arguments = Bundle().apply { putInt(ARG_TAB, tab) }
            }
    }
}
