package com.example.rai_oreo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.rai_oreo.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.btnMyProject.setOnClickListener {
            startActivity(Intent(requireContext(), SnackActivity::class.java))
        }
        binding.btnOpenWeb.setOnClickListener {
            startActivity(Intent(requireContext(), WebActivity::class.java))
        }
        binding.btnDeveloperProfile.setOnClickListener {
            startActivity(Intent(requireContext(), DeveloperProfileActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya") { dialog, _ ->
                    requireContext().getSharedPreferences("simper_pref", Context.MODE_PRIVATE)
                        .edit().clear().apply()
                    dialog.dismiss()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
