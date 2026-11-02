package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class PesanFragment : Fragment(R.layout.fragment_pesan) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnGenerateOrderQr).setOnClickListener {
            // Nomor contoh; pada integrasi API, ambil nomor dari pesanan yang sebenarnya.
            val intent = Intent(requireContext(), QROrderActivity::class.java)
            intent.putExtra(QROrderActivity.EXTRA_ORDER_NUMBER, "NR-20260721-746")
            startActivity(intent)
        }
    }
}
