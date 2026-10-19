package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class LayananFragment : Fragment(R.layout.fragment_layanan) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnOpenCatalog).setOnClickListener {
            startActivity(Intent(requireContext(), SnackActivity::class.java))
        }
    }
}
