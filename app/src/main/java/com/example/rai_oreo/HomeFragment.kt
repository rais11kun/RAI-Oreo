
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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        // Navigasi menuju halaman proyek
        binding.btnMyProject.setOnClickListener {

            startActivity(
                Intent(
                    requireContext(),
                    SnackActivity::class.java
                )
            )
        }

        // Navigasi menuju halaman web
        binding.btnOpenWeb.setOnClickListener {

            startActivity(
                Intent(
                    requireContext(),
                    WebActivity::class.java
                )
            )
        }

        // Navigasi menuju profil developer
        binding.btnDeveloperProfile.setOnClickListener {

            startActivity(
                Intent(
                    requireContext(),
                    DeveloperProfileActivity::class.java
                )
            )
        }

        // Proses logout
        binding.btnLogout.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya") { dialog, _ ->

                    val prefs = requireContext()
                        .getSharedPreferences(
                            "simper_pref",
                            Context.MODE_PRIVATE
                        )

                    // Hanya mengubah status login
                    // Username tetap disimpan
                    prefs.edit()
                        .putBoolean("isLogin", false)
                        .apply()

                    dialog.dismiss()

                    // Kembali ke halaman login
                    startActivity(
                        Intent(
                            requireContext(),
                            LoginActivity::class.java
                        )
                    )

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
