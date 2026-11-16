/**
 * Penerapan praktikum: Modul 11 (TabLayout dihubungkan ke ViewPager2 lewat TabLayoutMediator),
 * Modul 7 (Fragment).
 */
package com.example.rai_oreo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.example.rai_oreo.adapter.PesananPagerAdapter
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.FragmentPesananBinding

class PesananFragment : Fragment() {

    private var _binding: FragmentPesananBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?,
    ): View {
        _binding = FragmentPesananBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pagerPesanan.adapter = PesananPagerAdapter(this)
        binding.pagerPesanan.offscreenPageLimit = 2

        val judul = listOf("Aktif", "Selesai", "Dibatalkan")
        TabLayoutMediator(binding.tabStatus, binding.pagerPesanan) { tab, position ->
            tab.text = judul[position]
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        val semua = SimperRepository.ordersOf(SimperRepository.currentUser)
        val aktif = semua.count { it.status.isActive }
        binding.tvSubPesanan.text = "${semua.size} pesanan · $aktif aktif"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
