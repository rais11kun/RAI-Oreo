package com.example.rai_oreo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class LayananFragment : Fragment(R.layout.fragment_layanan) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Katalog ditampilkan langsung pada fragment. Navigasi detail layanan
        // akan ditambahkan ketika item katalog sudah memiliki ID dan data detail.
    }
}
