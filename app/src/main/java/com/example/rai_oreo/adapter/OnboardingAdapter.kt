/**
 * Penerapan praktikum: Modul 11 - FragmentStateAdapter sebagai sumber halaman ViewPager2.
 */
package com.example.rai_oreo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.rai_oreo.fragment.OnboardingPageFragment

class OnboardingAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment =
        OnboardingPageFragment.newInstance(position)
}
