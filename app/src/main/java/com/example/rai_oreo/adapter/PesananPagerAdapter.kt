/**
 * Penerapan praktikum: Modul 11 - FragmentStateAdapter yang mengisi ViewPager2 tab Pesanan
 * dengan tiga halaman: Aktif, Selesai, dan Dibatalkan.
 */
package com.example.rai_oreo.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.rai_oreo.fragment.PesananListFragment

class PesananPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment =
        PesananListFragment.newInstance(position)
}
