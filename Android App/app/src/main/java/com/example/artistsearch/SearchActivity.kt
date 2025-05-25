//package com.example.artistsearch
//
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.artistsearch.network.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import com.franmontiel.persistentcookiejar.PersistentCookieJar
//import com.franmontiel.persistentcookiejar.cache.SetCookieCache
//import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
//import okhttp3.OkHttpClient
//
//
//class SearchActivity : AppCompatActivity() {
//
//    private lateinit var searchEditText: EditText
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var apiService: ApiService
//    private lateinit var authService: AuthService
//    private lateinit var artistAdapter: ArtistAdapter
//
//    private val favoritedIds = mutableSetOf<String>()
//    private var isLoggedIn = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)
//
//        searchEditText = findViewById(R.id.toolbarTitle)
//        recyclerView = findViewById(R.id.recyclerView)
//        val closeButton = findViewById<ImageButton>(R.id.closeIcon)
//
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//
//// Step 1: Create a cookie jar that stores cookies in SharedPreferences
//        val cookieJar = PersistentCookieJar(
//            SetCookieCache(),
//            SharedPrefsCookiePersistor(applicationContext)
//        )
//
//// Step 2: Create an OkHttpClient with that cookie jar
//        val okHttpClient = OkHttpClient.Builder()
//            .cookieJar(cookieJar)
//            .build()
//
//// Step 3: Create Retrofit using that OkHttpClient
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://android-backend-458823.wl.r.appspot.com/")
//            .client(okHttpClient) // 👈 this line enables cookie support
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//// Step 4: Create your services
//        apiService = retrofit.create(ApiService::class.java)
//        authService = retrofit.create(AuthService::class.java)
//
//
//
//        lifecycleScope.launch {
//            try {
//                val userResponse = withContext(Dispatchers.IO) {
//                    authService.getUserProfile().execute()
//                }
//                isLoggedIn = userResponse.isSuccessful
//
//                Log.d("SearchActivity", "✅ Login status: $isLoggedIn")
//                Log.d("SearchActivity", "Profile response: ${userResponse.code()} - ${userResponse.message()}")
//
//                if (isLoggedIn) {
//                    val favResponse = withContext(Dispatchers.IO) {
//                        authService.getFavorites().execute()
//                    }
//                    Log.d("SearchActivity", "Favorite response: ${favResponse.code()} - ${favResponse.message()}")
//
//                    if (favResponse.isSuccessful) {
//                        val favoriteList = favResponse.body()
//                        Log.d("SearchActivity", "✅ Fetched favorites: ${favoriteList?.size ?: 0}")
//                        favoritedIds.addAll(favoriteList?.map { it.artistId } ?: emptyList())
//                    } else {
//                        Log.e("SearchActivity", "❌ Failed to fetch favorites: ${favResponse.errorBody()?.string()}")
//                    }
//                }
//
//                artistAdapter = ArtistAdapter(
//                    context = this@SearchActivity,
//                    favoritedIds = favoritedIds,
//                    isLoggedIn = isLoggedIn,
//                    onToggleFavorite = { artistId, shouldAdd ->
//                        lifecycleScope.launch {
//                            try {
//                                val artist = artistAdapter.getCurrentList().find {
//                                    it.links.self?.href?.split("/")?.lastOrNull() == artistId
//                                }
//
//                                if (shouldAdd) {
//                                    val requestBody = mapOf(
//                                        "name" to (artist?.title ?: "Unknown"),
//                                        "nationality" to "Unknown",
//                                        "years" to "Unknown",
//                                        "imageUrl" to (artist?.links?.thumbnail?.href ?: "")
//                                    )
//
//
//                                    val response = withContext(Dispatchers.IO) {
//                                        authService.addToFavorites(artistId, requestBody).execute()
//                                    }
//
//                                    if (response.isSuccessful) {
//                                        favoritedIds.add(artistId)
//                                        Toast.makeText(this@SearchActivity, "Added to Favorites", Toast.LENGTH_SHORT).show()
//                                    } else {
//                                        val errorMsg = response.errorBody()?.string()
//                                        Log.e("SearchActivity", "❌ Favorite failed: ${response.code()} - $errorMsg")
//                                        Toast.makeText(this@SearchActivity, "Favorite failed: ${response.code()}", Toast.LENGTH_LONG).show()
//                                    }
//
//                                } else {
//                                    val removeResponse = withContext(Dispatchers.IO) {
//                                        authService.removeFromFavorites(artistId).execute()
//                                    }
//                                    if (removeResponse.isSuccessful) {
//                                        favoritedIds.remove(artistId)
//                                        Toast.makeText(this@SearchActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show()
//                                    } else {
//                                        Log.e("SearchActivity", "❌ Failed to remove favorite: ${removeResponse.code()} - ${removeResponse.errorBody()?.string()}")
//                                        Toast.makeText(this@SearchActivity, "Failed to remove favorite", Toast.LENGTH_SHORT).show()
//                                    }
//
//                                    favoritedIds.remove(artistId)
//                                    Toast.makeText(this@SearchActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show()
//                                }
//
//                                artistAdapter.updateArtists(artistAdapter.getCurrentList())
//
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                                Toast.makeText(this@SearchActivity, "Failed to update favorite: ${e.message}", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//
//
//
//                )
//                recyclerView.adapter = artistAdapter
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//
//                if (e is retrofit2.HttpException) {
//                    val errorBody = e.response()?.errorBody()?.string()
//                    Log.e("SearchActivity", "❌ HTTP error: ${e.code()} - $errorBody")
//                    Toast.makeText(this@SearchActivity, "HTTP ${e.code()}: $errorBody", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(this@SearchActivity, "Failed to update favorite: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        }
//
//
//        searchEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun afterTextChanged(s: Editable?) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val query = s.toString()
//                if (query.length >= 3) {
//                    performSearch(query)
//                } else {
//                    artistAdapter.updateArtists(emptyList())
//                }
//            }
//        })
//
//        closeButton.setOnClickListener {
//            searchEditText.text.clear()
//        }
//    }
//
//    private fun performSearch(query: String) {
//        lifecycleScope.launch {
//            try {
//                val response = withContext(Dispatchers.IO) {
//                    apiService.searchArtists(query)
//                }
//                val results = response.embedded?.results ?: emptyList()
//                artistAdapter.updateArtists(results)
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(this@SearchActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}

package com.example.artistsearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artistsearch.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService
    private lateinit var authService: AuthService
    private lateinit var artistAdapter: ArtistAdapter

    private val favoritedIds = mutableSetOf<String>()
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.toolbarTitle)
        recyclerView = findViewById(R.id.recyclerView)
        val closeButton = findViewById<ImageButton>(R.id.closeIcon)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val cookieJar = PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(applicationContext)
        )

        val okHttpClient = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://android-backend-458823.wl.r.appspot.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        authService = retrofit.create(AuthService::class.java)

        lifecycleScope.launch {
            try {
                val userResponse = withContext(Dispatchers.IO) {
                    authService.getUserProfile().execute()
                }
                isLoggedIn = userResponse.isSuccessful
                Log.d("SearchActivity", "✅ Login status: $isLoggedIn")

                if (isLoggedIn) {
                    val favResponse = withContext(Dispatchers.IO) {
                        authService.getFavorites().execute()
                    }

                    if (favResponse.isSuccessful) {
                        val favoriteList = favResponse.body()
                        Log.d("SearchActivity", "✅ Fetched favorites: ${favoriteList?.size ?: 0}")
                        favoritedIds.addAll(favoriteList?.map { it.artistId } ?: emptyList())
                    } else {
                        Log.e("SearchActivity", "❌ Failed to fetch favorites: ${favResponse.errorBody()?.string()}")
                    }
                }

                artistAdapter = ArtistAdapter(
                    context = this@SearchActivity,
                    favoritedIds = favoritedIds,
                    isLoggedIn = isLoggedIn,
                    onToggleFavorite = { artistId, shouldAdd ->
                        lifecycleScope.launch {
                            try {
                                val artist = artistAdapter.getCurrentList().find {
                                    it.links.self?.href?.split("/")?.lastOrNull() == artistId
                                }

                                if (shouldAdd) {
                                    val artistIdForDetails = artist?.links?.self?.href?.split("/")?.lastOrNull()

                                    if (artistIdForDetails != null) {
                                        val artistDetails = withContext(Dispatchers.IO) {
                                            apiService.getArtistDetails(artistIdForDetails)
                                        }
                                        val requestBody = mapOf(
                                            "name" to (artist?.title ?: "Unknown"),
                                            "nationality" to (artistDetails.nationality ?: ""),
                                            "years" to (artistDetails.birthday ?: ""),
                                            "imageUrl" to (artist?.links?.thumbnail?.href ?: "")
                                        )

                                        val response = withContext(Dispatchers.IO) {
                                            authService.addToFavorites(artistIdForDetails, requestBody).execute()
                                        }

                                        if (response.isSuccessful) {
                                            favoritedIds.add(artistIdForDetails)
                                            showSnackbar("Added to Favorites")
                                        } else {
                                            Log.e("SearchActivity", "❌ Favorite failed: ${response.code()} - ${response.errorBody()?.string()}")
                                            showSnackbar("Favorite failed")
                                        }
                                    } else {
                                        showSnackbar("Invalid artist ID")
                                    }
                                } else {
                                    val artsyId = artist?.links?.self?.href?.split("/")?.lastOrNull()

                                    if (artsyId != null) {
                                        val response = withContext(Dispatchers.IO) {
                                            authService.removeFromFavorites(artsyId).execute()
                                        }

                                        if (response.isSuccessful) {
                                            favoritedIds.remove(artsyId) // make sure you remove the same ID
                                            showSnackbar("Removed from Favorites")
                                        } else {
                                            Log.e("SearchActivity", "❌ Remove failed: ${response.code()} - ${response.errorBody()?.string()}")
                                            showSnackbar("Failed to remove favorite")
                                        }
                                    } else {
                                        showSnackbar("Invalid artist ID")
                                    }
                                }


                                artistAdapter.updateArtists(artistAdapter.getCurrentList())

                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(this@SearchActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
                recyclerView.adapter = artistAdapter

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SearchActivity, "Initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.length >= 3) {
                    performSearch(query)
                } else {
                    artistAdapter.updateArtists(emptyList())
                }
            }
        })

        closeButton.setOnClickListener {
            finish()  // this will return to MainActivity
        }

    }

    private fun performSearch(query: String) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.searchArtists(query)
                }
                artistAdapter.updateArtists(response.embedded?.results ?: emptyList())
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SearchActivity, "Search failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSnackbar(message: String) {
        val rootView = findViewById<android.view.View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }

}
