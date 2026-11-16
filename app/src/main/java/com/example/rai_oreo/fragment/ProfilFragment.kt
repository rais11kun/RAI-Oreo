/**
 * Penerapan praktikum: Modul 7 (Fragment), Modul 3 (Intent eksplisit ke Activity lain), Modul 3 (Dialog konfirmasi keluar).
 */
package com.example.rai_oreo.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.rai_oreo.LoginActivity
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.MenuAdapter
import com.example.rai_oreo.adapter.MenuProfil
import com.example.rai_oreo.data.OrderStatus
import com.example.rai_oreo.data.Prefs
import com.example.rai_oreo.data.Role
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.FragmentProfilBinding
import com.example.rai_oreo.screen.AlamatActivity
import com.example.rai_oreo.screen.AntrianActivity
import com.example.rai_oreo.screen.BantuanActivity
import com.example.rai_oreo.screen.DashboardPemilikActivity
import com.example.rai_oreo.screen.EditProfilActivity
import com.example.rai_oreo.screen.KartuQrActivity
import com.example.rai_oreo.screen.KelolaLayananActivity
import com.example.rai_oreo.screen.LaporanActivity
import com.example.rai_oreo.screen.NotifikasiActivity
import com.example.rai_oreo.screen.PengaturanActivity
import com.example.rai_oreo.screen.UbahPasswordActivity

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())

        binding.ivEdit.setOnClickListener { buka(EditProfilActivity::class.java) }
        binding.btnKeluar.setOnClickListener { konfirmasiKeluar() }
    }

    override fun onResume() {
        super.onResume()
        val user = SimperRepository.currentUser
        binding.tvNama.text = user?.nama ?: "Pelanggan"
        binding.tvEmail.text = user?.email ?: "-"
        binding.tvRole.text = user?.role?.label ?: Role.PELANGGAN.label

        val pesanan = SimperRepository.ordersOf(user)
        binding.tvTotalPesanan.text = pesanan.size.toString()
        binding.tvAktifPesanan.text = pesanan.count { it.status.isActive }.toString()
        binding.tvSelesaiPesanan.text = pesanan.count { it.status == OrderStatus.SELESAI }.toString()

        binding.rvMenu.adapter = MenuAdapter(menu(user?.role)) { aksi -> jalankan(aksi) }
    }

    private fun menu(role: Role?): List<Any> {
        val isi = mutableListOf<Any>()
        isi += "Akun"
        isi += MenuProfil("Edit profil", "Nama, email dan nomor telepon", R.drawable.ic_edit, "edit")
        isi += MenuProfil(
            "Alamat saya", "Kelola alamat pengambilan", R.drawable.ic_pin, "alamat",
            R.color.green, R.drawable.bg_tile_green,
        )
        isi += MenuProfil(
            "Ubah password", "Jaga keamanan akun Anda", R.drawable.ic_lock, "password",
            R.color.orange_dark, R.drawable.bg_tile_orange,
        )
        isi += MenuProfil("Kartu pelanggan", "Kode QR saat mengambil pesanan", R.drawable.ic_qr, "qr")
        isi += "Lainnya"
        isi += MenuProfil("Notifikasi", "Info status dan promo", R.drawable.ic_bell, "notif")
        isi += MenuProfil(
            "Pengaturan", "Notifikasi dan login sidik jari", R.drawable.ic_settings, "pengaturan",
            R.color.orange_dark, R.drawable.bg_tile_orange,
        )
        isi += MenuProfil(
            "Bantuan & FAQ", "Panduan pemesanan dan kontak", R.drawable.ic_help, "bantuan",
            R.color.green, R.drawable.bg_tile_green,
        )

        if (role == Role.KARYAWAN || role == Role.PEMILIK) {
            isi += "Operasional"
            isi += MenuProfil("Antrian pesanan", "Pesanan yang perlu dikerjakan", R.drawable.ic_receipt, "antrian")
            isi += MenuProfil(
                "Kelola layanan", "Atur layanan yang tampil di katalog", R.drawable.ic_grid, "layanan",
                R.color.green, R.drawable.bg_tile_green,
            )
        }

        if (role == Role.PEMILIK) {
            isi += "Laporan"
            isi += MenuProfil(
                "Dashboard pemilik", "Ringkasan pendapatan dan pesanan", R.drawable.ic_chart, "dashboard",
                R.color.orange_dark, R.drawable.bg_tile_orange,
            )
            isi += MenuProfil("Laporan penjualan", "Transaksi selesai per periode", R.drawable.ic_wallet, "laporan")
        }
        return isi
    }

    private fun jalankan(aksi: String) {
        when (aksi) {
            "edit" -> buka(EditProfilActivity::class.java)
            "alamat" -> buka(AlamatActivity::class.java)
            "password" -> buka(UbahPasswordActivity::class.java)
            "qr" -> buka(KartuQrActivity::class.java)
            "notif" -> buka(NotifikasiActivity::class.java)
            "pengaturan" -> buka(PengaturanActivity::class.java)
            "bantuan" -> buka(BantuanActivity::class.java)
            "antrian" -> buka(AntrianActivity::class.java)
            "layanan" -> buka(KelolaLayananActivity::class.java)
            "dashboard" -> buka(DashboardPemilikActivity::class.java)
            "laporan" -> buka(LaporanActivity::class.java)
        }
    }

    private fun buka(kelas: Class<*>) {
        startActivity(Intent(requireContext(), kelas))
    }

    private fun konfirmasiKeluar() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Keluar dari akun?")
            .setMessage("Anda perlu masuk kembali untuk memesan layanan.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Keluar") { _, _ ->
                SimperRepository.logout()
                Prefs.hapusSesi(requireContext())
                startActivity(
                    Intent(requireContext(), LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
