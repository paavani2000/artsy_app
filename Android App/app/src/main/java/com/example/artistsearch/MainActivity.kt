//package com.example.artistsearch
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.PopupMenu
//import coil.load
//import coil.transform.CircleCropTransformation
//import com.example.artistsearch.network.ApiClient
//import com.example.artistsearch.network.FavoriteArtistRequest
//import com.example.artistsearch.network.UserProfile
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.text.SimpleDateFormat
//import java.util.*
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var loginButton: Button
//    private lateinit var noFavoritesText: TextView
//    private lateinit var favoritesContainer: LinearLayout
//    private lateinit var profileImageView: ImageView
//    private val authService by lazy { ApiClient.getAuthService(this) }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        findViewById<TextView>(R.id.poweredBy).setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                data = android.net.Uri.parse("https://www.artsy.net/")
//            }
//            startActivity(intent)
//        }
//
//
//
//        val dateText: TextView = findViewById(R.id.dateText)
//        val today = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
//        val currentDate = today.format(Date())
//        dateText.text = currentDate
//
//        loginButton = findViewById(R.id.loginButton)
//        noFavoritesText = findViewById(R.id.noFavoritesText)
//        favoritesContainer = findViewById(R.id.favoritesContainer)
//        profileImageView = findViewById(R.id.profileImageView)
//
//        findViewById<ImageButton>(R.id.searchIcon).setOnClickListener {
//            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
//        }
//
//        loginButton.setOnClickListener {
//            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//        }
//
//        authService.getUserProfile().enqueue(object : Callback<UserProfile> {
//            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
//                if (response.isSuccessful) {
//                    val profile = response.body()
//
//                    if (!profile?.profileImageUrl.isNullOrEmpty()) {
//                        profileImageView.load(profile!!.profileImageUrl) {
//                            transformations(CircleCropTransformation())
//                            placeholder(R.drawable.baseline_person_24)
//                            error(R.drawable.baseline_person_24)
//                        }
//                    } else {
//                        profileImageView.setImageResource(R.drawable.baseline_person_24)
//                    }
//
//                    profileImageView.visibility = View.VISIBLE
//                    loginButton.visibility = View.GONE
//                    setupDropdownMenu()
//
//                    // Load favorites initially
//                    loadFavorites()
//
//                } else {
//                    profileImageView.setImageResource(R.drawable.baseline_person_24)
//                    profileImageView.visibility = View.VISIBLE
//                    loginButton.visibility = View.VISIBLE
//                }
//            }
//
//            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
//                profileImageView.setImageResource(R.drawable.baseline_person_24)
//                profileImageView.visibility = View.VISIBLE
//                loginButton.visibility = View.VISIBLE
//            }
//        })
//    }
//
//    override fun onResume() {
//        super.onResume()
//        loadFavorites()
//    }
//
//    private fun loadFavorites() {
//        favoritesContainer.removeAllViews()
//        noFavoritesText.visibility = View.GONE
//        favoritesContainer.visibility = View.GONE
//
//        authService.getFavorites().enqueue(object : Callback<List<FavoriteArtistRequest>> {
//            override fun onResponse(
//                call: Call<List<FavoriteArtistRequest>>,
//                response: Response<List<FavoriteArtistRequest>>
//            ) {
//                val favorites = response.body().orEmpty().sortedByDescending { it.created_at }
//                Log.d("RawFavoritesResponse", favorites.toString())
//
//                if (favorites.isEmpty()) {
//                    noFavoritesText.visibility = View.VISIBLE
//                } else {
//                    favoritesContainer.visibility = View.VISIBLE
//                    favorites.forEach { artist ->
//                        val itemView = layoutInflater.inflate(
//                            R.layout.favorite_item,
//                            favoritesContainer,
//                            false
//                        )
//
//                        val nameText = itemView.findViewById<TextView>(R.id.artistName)
//                        val detailsText = itemView.findViewById<TextView>(R.id.artistDetails)
//                        val timeAgoText = itemView.findViewById<TextView>(R.id.timeAgo)
//                        val chevron = itemView.findViewById<ImageView>(R.id.chevron)
//
//                        nameText.text = artist.name
//
//                        val nationality = artist.nationality?.takeIf { it.lowercase() != "unknown" } ?: ""
//                        val years = artist.years?.takeIf { it.lowercase() != "unknown" } ?: ""
//
//                        val details = listOfNotNull(nationality, years)
//                            .filter { it.isNotBlank() }
//                            .joinToString(", ")
//
//                        Log.d(
//                            "FavoritesDebug",
//                            "Name=${artist.name}, ArtistID=${artist.artistId}, Nationality=${artist.nationality}, Years=${artist.years}"
//                        )
//
//                        detailsText.text = details
//                        scheduleLiveTimeUpdate(timeAgoText, artist.created_at)
//
//                        chevron.setOnClickListener {
//                            val intent = Intent(this@MainActivity, ArtistDetailsActivity::class.java)
//                            intent.putExtra("artist_id", artist.artistId)
//                            intent.putExtra("artist_name", artist.name)
//                            startActivity(intent)
//                        }
//
//                        favoritesContainer.addView(itemView)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<FavoriteArtistRequest>>, t: Throwable) {
//                noFavoritesText.text = "Failed to load favorites"
//                noFavoritesText.visibility = View.VISIBLE
//            }
//        })
//    }
//
//    private fun setupDropdownMenu() {
//        profileImageView.setOnClickListener { view ->
//            val popup = PopupMenu(this, view)
//            popup.menuInflater.inflate(R.menu.profile_menu, popup.menu)
//            popup.setOnMenuItemClickListener {
//                when (it.itemId) {
//                    R.id.menu_logout -> {
//                        logoutUser()
//                        true
//                    }
//
//                    R.id.menu_delete -> {
//                        deleteAccount()
//                        true
//                    }
//
//                    else -> false
//                }
//            }
//            popup.show()
//        }
//    }
//
//    private fun logoutUser() {
//        authService.logout().enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                val intent = Intent(this@MainActivity, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "Logout failed", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun deleteAccount() {
//        authService.deleteAccount().enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                val intent = Intent(this@MainActivity, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "Delete failed", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    fun scheduleLiveTimeUpdate(textView: TextView, createdAt: String) {
//        val handler = android.os.Handler(mainLooper)
//
//        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).apply {
//            timeZone = TimeZone.getTimeZone("UTC")
//        }
//
//        val createdDate = try {
//            format.parse(createdAt)
//        } catch (e: Exception) {
//            null
//        } ?: return
//
//        val updateRunnable = object : Runnable {
//            override fun run() {
//                val now = Date()
//                val diff = now.time - createdDate.time
//                val seconds = diff / 1000
//                val minutes = seconds / 60
//                val hours = minutes / 60
//                val days = hours / 24
//
//                val text = when {
//                    seconds < 60 -> "$seconds second${if (seconds != 1L) "s" else ""} ago"
//                    minutes < 60 -> "$minutes minute${if (minutes != 1L) "s" else ""} ago"
//                    hours < 24 -> "$hours hour${if (hours != 1L) "s" else ""} ago"
//                    else -> "$days day${if (days != 1L) "s" else ""} ago"
//                }
//
//                textView.text = text
//
//                val delayMillis = when {
//                    seconds < 60 -> 1000L
//                    minutes < 60 -> 60_000L
//                    hours < 24 -> 60 * 60_000L
//                    else -> 24 * 60 * 60_000L
//                }
//
//                handler.postDelayed(this, delayMillis)
//            }
//        }
//
//        handler.post(updateRunnable)
//    }
//}
//
package com.example.artistsearch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import coil.load
import coil.transform.CircleCropTransformation
import com.example.artistsearch.network.ApiClient
import com.example.artistsearch.network.FavoriteArtistRequest
import com.example.artistsearch.network.UserProfile
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var noFavoritesText: TextView
    private lateinit var favoritesContainer: LinearLayout
    private lateinit var profileImageView: ImageView
    private val authService by lazy { ApiClient.getAuthService(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val action = intent.getStringExtra("action")
        action?.let {
            val message = when (it) {
                "logout" -> "Logged out successfully"
                "delete" -> "Account deleted successfully"
                else -> null
            }
            message?.let { msg ->
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show()
            }
        }


        findViewById<TextView>(R.id.poweredBy).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.artsy.net/")))
        }

        val dateText: TextView = findViewById(R.id.dateText)
        dateText.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())

        loginButton = findViewById(R.id.loginButton)
        noFavoritesText = findViewById(R.id.noFavoritesText)
        favoritesContainer = findViewById(R.id.favoritesContainer)
        profileImageView = findViewById(R.id.profileImageView)

        findViewById<ImageButton>(R.id.searchIcon).setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        checkUserState()
    }

    private fun checkUserState() {
        authService.getUserProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    loginButton.visibility = View.GONE
                    profileImageView.visibility = View.VISIBLE

                    profileImageView.load(profile?.profileImageUrl.orEmpty()) {
                        transformations(CircleCropTransformation())
                        placeholder(R.drawable.baseline_person_outline_24)
                        error(R.drawable.baseline_person_outline_24)
                    }

                    setupDropdownMenu()
                    loadFavorites() // ✅ Only here

                } else {
                    showLoggedOutState()
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                showLoggedOutState()
            }
        })
    }



    private fun showLoggedOutState() {
        loginButton.visibility = View.VISIBLE
        profileImageView.visibility = View.VISIBLE
        profileImageView.setImageResource(R.drawable.baseline_person_outline_24)
        favoritesContainer.removeAllViews()
        favoritesContainer.visibility = View.GONE
        noFavoritesText.visibility = View.GONE

        findViewById<TextView>(R.id.poweredBy).visibility = View.VISIBLE // ✅ show Artsy footer

        profileImageView.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }




    private fun loadFavorites() {
        favoritesContainer.removeAllViews()
        favoritesContainer.visibility = View.GONE
        noFavoritesText.visibility = View.GONE
        val poweredBy = findViewById<TextView>(R.id.poweredBy)
        poweredBy.visibility = View.GONE

        authService.getFavorites().enqueue(object : Callback<List<FavoriteArtistRequest>> {
            override fun onResponse(
                call: Call<List<FavoriteArtistRequest>>,
                response: Response<List<FavoriteArtistRequest>>
            ) {
                val favorites = response.body().orEmpty()
                    .distinctBy { it.artistId }
                    .sortedByDescending { it.created_at }

                if (favorites.isEmpty()) {
                    noFavoritesText.text = "No favorites"
                    noFavoritesText.visibility = View.VISIBLE
                } else {
                    favoritesContainer.visibility = View.VISIBLE
                    poweredBy.visibility = View.VISIBLE

                    favorites.forEach { artist ->
                        val itemView = layoutInflater.inflate(
                            R.layout.favorite_item,
                            favoritesContainer,
                            false
                        )

                        itemView.findViewById<TextView>(R.id.artistName).text = artist.name

                        val nationality = artist.nationality?.takeIf { it.lowercase() != "unknown" } ?: ""
                        val years = artist.years?.takeIf { it.lowercase() != "unknown" } ?: ""
                        val details = listOf(nationality, years).filter { it.isNotBlank() }.joinToString(", ")
                        itemView.findViewById<TextView>(R.id.artistDetails).text = details

                        scheduleLiveTimeUpdate(
                            itemView.findViewById(R.id.timeAgo),
                            artist.created_at
                        )

                        itemView.findViewById<ImageView>(R.id.chevron).setOnClickListener {
                            val intent = Intent(this@MainActivity, ArtistDetailsActivity::class.java)
                            intent.putExtra("artist_id", artist.artistId)
                            intent.putExtra("artist_name", artist.name)
                            startActivity(intent)
                        }

                        favoritesContainer.addView(itemView)
                    }
                }
            }

            override fun onFailure(call: Call<List<FavoriteArtistRequest>>, t: Throwable) {
                noFavoritesText.text = "Failed to load favorites"
                noFavoritesText.visibility = View.VISIBLE
                findViewById<TextView>(R.id.poweredBy).visibility = View.GONE
            }
        })
    }





    private fun setupDropdownMenu() {
        profileImageView.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.profile_menu, popup.menu)

            // Color the items
            val logoutItem = popup.menu.findItem(R.id.menu_logout)
            val deleteItem = popup.menu.findItem(R.id.menu_delete)

            logoutItem.title = getColoredText("Logout", "#163676") // Blue
            deleteItem.title = getColoredText("Delete Account", "#D32F2F") // Red

            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_logout -> {
                        logoutUser()
                        true
                    }
                    R.id.menu_delete -> {
                        deleteAccount()
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }
    }

    private fun getColoredText(text: String, colorHex: String): CharSequence {
        val spannable = android.text.SpannableString(text)
        spannable.setSpan(
            android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor(colorHex)),
            0, text.length, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }



    private fun logoutUser() {
        authService.logout().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("action", "logout")
                startActivity(intent)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Logout failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteAccount() {
        authService.deleteAccount().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("action", "delete")
                startActivity(intent)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Delete failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun scheduleLiveTimeUpdate(textView: TextView, createdAt: String) {
        val handler = android.os.Handler(mainLooper)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val createdDate = try {
            format.parse(createdAt)
        } catch (e: Exception) {
            null
        } ?: return

        val updateRunnable = object : Runnable {
            override fun run() {
                val now = Date()
                val diff = now.time - createdDate.time
                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24

                val text = when {
                    seconds < 60 -> "$seconds second${if (seconds != 1L) "s" else ""} ago"
                    minutes < 60 -> "$minutes minute${if (minutes != 1L) "s" else ""} ago"
                    hours < 24 -> "$hours hour${if (hours != 1L) "s" else ""} ago"
                    else -> "$days day${if (days != 1L) "s" else ""} ago"
                }

                textView.text = text

                val delayMillis = when {
                    seconds < 60 -> 1000L              // update every second
                    minutes < 60 -> 60_000L            // update every minute
                    hours < 24 -> 60 * 60_000L         // update every hour
                    else -> 24 * 60 * 60_000L          // update daily
                }

                handler.postDelayed(this, delayMillis)
            }
        }

        handler.post(updateRunnable)
    }

}



