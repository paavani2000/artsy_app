package com.example.artistsearch

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.artistsearch.databinding.ActivityArtistDetailsBinding
import com.example.artistsearch.network.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtistDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArtistDetailsBinding
    private lateinit var authService: AuthService
    private lateinit var apiService: ApiService

    private var isLoggedIn = false
    private var isFavorited = false
    private var favoriteMenuItem: MenuItem? = null
    private var artistId: String? = null
    private var artistName: String = "Artist"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        artistName = intent.getStringExtra("artist_name") ?: "Artist"
        artistId = intent.getStringExtra("artist_id")

        // Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = artistName
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Init services
        authService = ApiClient.getAuthService(this)
        apiService = ApiClient.getApiService(this)

        // Login + tab + favorite status
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userResp = authService.getUserProfile().execute()
                isLoggedIn = userResp.isSuccessful

                if (isLoggedIn && artistId != null) {
                    val favResp = authService.getFavorites().execute()
                    val favList = favResp.body()
                    isFavorited = favList?.any { it.artistId == artistId } == true
                }
            } catch (e: Exception) {
                Log.e("ArtistDetails", "Login/fav check failed: ${e.message}")
            }

            withContext(Dispatchers.Main) {
                setupTabs(isLoggedIn)
                invalidateOptionsMenu()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.artist_details_menu, menu)
        favoriteMenuItem = menu?.findItem(R.id.action_favorite)
        updateFavoriteIcon()
        favoriteMenuItem?.isVisible = isLoggedIn
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                toggleFavorite()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupTabs(isLoggedIn: Boolean) {
        val adapter = ViewPagerAdapter(this, isLoggedIn)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Details"
                    tab.setIcon(R.drawable.baseline_info_24)
                }
                1 -> {
                    tab.text = "Artworks"
                    tab.setIcon(R.drawable.baseline_account_box_24)
                }
                2 -> {
                    tab.text = "Similar"
                    tab.setIcon(R.drawable.baseline_person_search_24)
                }
            }
        }.attach()

        binding.tabLayout.post {
            val vg = binding.tabLayout.getChildAt(0) as ViewGroup
            for (i in 0 until vg.childCount) {
                val tabGroup = vg.getChildAt(i) as ViewGroup
                for (j in 0 until tabGroup.childCount) {
                    val tabChild = tabGroup.getChildAt(j)
                    if (tabChild is TextView) tabChild.isAllCaps = false
                }
            }
        }
    }
    private fun toggleFavorite() {
        val id = artistId ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isFavorited) {
                    val res = authService.removeFromFavorites(id).execute()
                    if (res.isSuccessful) {
                        isFavorited = false
                        showSnackbar("Removed from Favorites")
                    } else {
                        Log.e("ArtistDetails", "❌ Remove failed: ${res.code()} ${res.errorBody()?.string()}")
                        showSnackbar("Failed to remove favorite")
                    }
                } else {
                    val artistDetails = apiService.getArtistDetails(id)
                    val defaultImageUrl = "https://yourcdn.com/images/default_artist.png"

                    val reqBody = mapOf(
                        "name" to (artistDetails.name ?: artistName),
                        "nationality" to (artistDetails.nationality ?: "Unknown"),
                        "years" to (artistDetails.birthday ?: "Unknown"),
                        "imageUrl" to (artistDetails.imageUrl?.takeIf { it.isNotBlank() } ?: defaultImageUrl)
                    )

                    Log.d("ArtistDetails", "➡️ Sending to favorites: $reqBody")
                    val res = authService.addToFavorites(id, reqBody).execute()

                    if (res.isSuccessful) {
                        isFavorited = true
                        showSnackbar("Added to Favorites")
                    } else {
                        val errorBody = res.errorBody()?.string()
                        Log.e("ArtistDetails", "❌ Add failed: ${res.code()} - $errorBody")
                        showSnackbar("Failed to add favorite: ${res.code()}")
                    }
                }

                withContext(Dispatchers.Main) {
                    updateFavoriteIcon()
                }

            } catch (e: Exception) {
                Log.e("ArtistDetails", "Toggle error: ${e.message}")
                withContext(Dispatchers.Main) {
                    showSnackbar("Error: ${e.message}")
                }
            }
        }
    }


//    private fun toggleFavorite() {
//        val id = artistId ?: return
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                if (isFavorited) {
//                    val res = authService.removeFromFavorites(id).execute()
//                    if (res.isSuccessful) {
//                        isFavorited = false
//                        showToast("Removed from Favorites")
//                    } else {
//                        Log.e("ArtistDetails", "❌ Remove failed: ${res.code()} ${res.errorBody()?.string()}")
//                        showToast("Failed to remove favorite")
//                    }
//                } else {
//                    val artistDetails = apiService.getArtistDetails(id)
//                    val defaultImageUrl = "https://yourcdn.com/images/default_artist.png" // replace with any valid placeholder image
//
//                    val reqBody = mapOf(
//                        "name" to (artistDetails.name ?: artistName),
//                        "nationality" to (artistDetails.nationality ?: "Unknown"),
//                        "years" to (artistDetails.birthday ?: "Unknown"),
//                        "imageUrl" to (artistDetails.imageUrl?.takeIf { it.isNotBlank() } ?: defaultImageUrl)
//                    )
//
//
//                    Log.d("ArtistDetails", "➡️ Sending to favorites: $reqBody")
//
//                    val res = authService.addToFavorites(id, reqBody).execute()
//
//                    if (res.isSuccessful) {
//                        isFavorited = true
//                        showToast("Added to Favorites")
//                    } else {
//                        val errorBody = res.errorBody()?.string()
//                        Log.e("ArtistDetails", "❌ Add failed: ${res.code()} - $errorBody")
//                        showToast("Failed to add favorite: ${res.code()}")
//                    }
//
//                }
//
//                withContext(Dispatchers.Main) {
//                    updateFavoriteIcon()
//                }
//
//            } catch (e: Exception) {
//                Log.e("ArtistDetails", "Toggle error: ${e.message}")
//                withContext(Dispatchers.Main) {
//                    showToast("Error: ${e.message}")
//                }
//            }
//        }
//    }


    private fun updateFavoriteIcon() {
        favoriteMenuItem?.setIcon(
            if (isFavorited) R.drawable.baseline_star_24
            else R.drawable.baseline_star_border_24
        )
    }

    private fun showToast(msg: String) {
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }
    private fun showSnackbar(message: String) {
        runOnUiThread {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

}
