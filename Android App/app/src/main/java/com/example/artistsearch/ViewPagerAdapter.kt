package com.example.artistsearch

import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: AppCompatActivity, private val isLoggedIn: Boolean) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return if (isLoggedIn) 3 else 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DetailsFragment()
            1 -> ArtworksFragment()
            2 -> SimilarArtistsFragment()
            else -> throw IllegalStateException("Invalid tab position")
        }
    }
}
