/**
 * Penerapan praktikum: Modul 11 - satu halaman onboarding di dalam ViewPager2.
 */
package com.example.rai_oreo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rai_oreo.R
import com.example.rai_oreo.databinding.FragmentOnboardingPageBinding

class OnboardingPageFragment : Fragment() {

    private var _binding: FragmentOnboardingPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?,
    ): View {
        _binding = FragmentOnboardingPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val halaman = requireArguments().getInt(ARG_HALAMAN)
        val data = halamanData[halaman]

        binding.ivIlustrasi.setImageResource(data.ilustrasi)
        binding.tvJudul.setText(data.judul)
        binding.tvDeskripsi.setText(data.deskripsi)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private data class Halaman(val ilustrasi: Int, val judul: Int, val deskripsi: Int)

    companion object {
        private const val ARG_HALAMAN = "halaman"

        private val halamanData = listOf(
            Halaman(
                R.drawable.onboarding_1,
                R.string.onboarding_1_judul,
                R.string.onboarding_1_isi,
            ),
            Halaman(
                R.drawable.onboarding_2,
                R.string.onboarding_2_judul,
                R.string.onboarding_2_isi,
            ),
            Halaman(
                R.drawable.onboarding_3,
                R.string.onboarding_3_judul,
                R.string.onboarding_3_isi,
            ),
        )

        fun newInstance(halaman: Int): OnboardingPageFragment =
            OnboardingPageFragment().apply {
                arguments = Bundle().apply { putInt(ARG_HALAMAN, halaman) }
            }
    }
}
