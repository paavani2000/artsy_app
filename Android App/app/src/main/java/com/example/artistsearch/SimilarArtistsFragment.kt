//package com.example.artistsearch
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.*
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import coil.load
//import com.example.artistsearch.network.*
//import com.google.android.material.snackbar.Snackbar
//import kotlinx.coroutines.*
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class SimilarArtistsFragment : Fragment() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var noSimilarText: TextView
//    private lateinit var adapter: SimilarArtistsAdapter
//    private val artistList = mutableListOf<Artist>()
//    private val favoriteIds = mutableSetOf<String>()
//
//    private val apiService by lazy { ApiClient.getApiService(requireContext()) }
//    private val authService by lazy { ApiClient.getAuthService(requireContext()) }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View {
//        val view = inflater.inflate(R.layout.fragment_similar_artists, container, false)
//        recyclerView = view.findViewById(R.id.similarArtistsRecycler)
//        noSimilarText = view.findViewById(R.id.noSimilarText)
//
//        adapter = SimilarArtistsAdapter(
//            artistList,
//            favoriteIds,
//            onCardClick = { artistId ->
//                val artist = artistList.find { it.id == artistId }
//                val intent = Intent(requireContext(), ArtistDetailsActivity::class.java)
//                intent.putExtra("artist_id", artistId)
//                intent.putExtra("artist_name", artist?.name ?: "Artist")
//                startActivity(intent)
//            },
//
//            onStarClick = { artist ->
//                toggleFavorite(artist)
//            }
//        )
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//
//        val artistId = requireActivity().intent.getStringExtra("artist_id")
//        if (!artistId.isNullOrEmpty()) {
//            fetchSimilarArtists(artistId)
//        } else {
//            showError("No artist ID found")
//        }
//
//        return view
//    }
//
//    private fun fetchSimilarArtists(id: String) {
//        authService.getFavorites().enqueue(object : Callback<List<FavoriteArtistRequest>> {
//            override fun onResponse(
//                call: Call<List<FavoriteArtistRequest>>,
//                response: Response<List<FavoriteArtistRequest>>
//            ) {
//                favoriteIds.clear()
//                favoriteIds.addAll(response.body().orEmpty().map { it.artistId })
//                loadSimilar(id)
//            }
//
//            override fun onFailure(call: Call<List<FavoriteArtistRequest>>, t: Throwable) {
//                Log.e("SimilarFragment", "Failed to load favorites")
//                loadSimilar(id)
//            }
//        })
//    }
//
//    private fun loadSimilar(id: String) {
//        apiService.getSimilarArtists(id).enqueue(object : Callback<SimilarArtistsResponse> {
//            override fun onResponse(
//                call: Call<SimilarArtistsResponse>,
//                response: Response<SimilarArtistsResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val artists = response.body()?.embedded?.artists.orEmpty()
//                    if (artists.isEmpty()) {
//                        showError("No Similar Artists")
//                    } else {
//                        artistList.clear()
//                        artistList.addAll(artists)
//                        adapter.notifyDataSetChanged()
//                        noSimilarText.visibility = View.GONE
//                        recyclerView.visibility = View.VISIBLE
//                    }
//                } else {
//                    showError("Failed to fetch similar artists")
//                }
//            }
//
//            override fun onFailure(call: Call<SimilarArtistsResponse>, t: Throwable) {
//                showError("Error fetching similar artists")
//            }
//        })
//    }
//
//    private fun toggleFavorite(artist: Artist) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val isAlreadyFavorite = favoriteIds.contains(artist.id)
//
//                if (isAlreadyFavorite) {
//                    val res = authService.removeFromFavorites(artist.id).execute()
//                    if (res.isSuccessful) {
//                        favoriteIds.remove(artist.id)
//                        showSnackbar("Removed from Favorites")
//                    } else {
//                        Log.e("SimilarFragment", "❌ Remove failed: ${res.code()} ${res.errorBody()?.string()}")
//                        showSnackbar("Failed to remove favorite")
//                    }
//                } else {
//                    val reqBody = mapOf(
//                        "name" to (artist.name ?: "Unknown"),
//                        "nationality" to (artist.nationality ?: "Unknown"),
//                        "years" to (artist.birthday ?: "Unknown"),
//                        "imageUrl" to (artist.links?.thumbnail?.href ?: "https://yourcdn.com/images/default_artist.png")
//                    )
//
//                    val res = authService.addToFavorites(artist.id, reqBody).execute()
//
//                    if (res.isSuccessful) {
//                        favoriteIds.add(artist.id)
//                        showSnackbar("Added to Favorites")
//                    } else {
//                        val errorBody = res.errorBody()?.string()
//                        Log.e("SimilarFragment", "❌ Add failed: ${res.code()} - $errorBody")
//                        showSnackbar("Favorite failed")
//                    }
//                }
//
//                withContext(Dispatchers.Main) {
//                    adapter.notifyDataSetChanged()
//                }
//
//            } catch (e: Exception) {
//                Log.e("SimilarFragment", "Toggle error: ${e.message}")
//                showToast("Error: ${e.message}")
//            }
//        }
//    }
//
//    private fun showToast(message: String) {
//        requireActivity().runOnUiThread {
//            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun showSnackbar(message: String) {
//        requireActivity().runOnUiThread {
//            view?.let {
//                Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//    private fun showError(message: String) {
//        noSimilarText.text = message
//        noSimilarText.visibility = View.VISIBLE
//        recyclerView.visibility = View.GONE
//    }
//}
//

package com.example.artistsearch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artistsearch.network.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SimilarArtistsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noSimilarText: TextView
    private lateinit var progressBar: View
    private lateinit var adapter: SimilarArtistsAdapter
    private val artistList = mutableListOf<Artist>()
    private val favoriteIds = mutableSetOf<String>()

    private val apiService by lazy { ApiClient.getApiService(requireContext()) }
    private val authService by lazy { ApiClient.getAuthService(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_similar_artists, container, false)
        recyclerView = view.findViewById(R.id.similarArtistsRecycler)
        noSimilarText = view.findViewById(R.id.noSimilarText)
        progressBar = view.findViewById(R.id.progressBar)

        adapter = SimilarArtistsAdapter(
            artistList,
            favoriteIds,
            onCardClick = { artistId ->
                val artist = artistList.find { it.id == artistId }
                val intent = Intent(requireContext(), ArtistDetailsActivity::class.java)
                intent.putExtra("artist_id", artistId)
                intent.putExtra("artist_name", artist?.name ?: "Artist")
                startActivity(intent)
            },
            onStarClick = { artist ->
                toggleFavorite(artist)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val artistId = requireActivity().intent.getStringExtra("artist_id")
        if (!artistId.isNullOrEmpty()) {
            fetchSimilarArtists(artistId)
        } else {
            showError("No artist ID found")
        }

        return view
    }

    private fun fetchSimilarArtists(id: String) {
        showLoading()
        authService.getFavorites().enqueue(object : Callback<List<FavoriteArtistRequest>> {
            override fun onResponse(
                call: Call<List<FavoriteArtistRequest>>,
                response: Response<List<FavoriteArtistRequest>>
            ) {
                favoriteIds.clear()
                favoriteIds.addAll(response.body().orEmpty().map { it.artistId })
                loadSimilar(id)
            }

            override fun onFailure(call: Call<List<FavoriteArtistRequest>>, t: Throwable) {
                Log.e("SimilarFragment", "Failed to load favorites")
                loadSimilar(id)
            }
        })
    }

    private fun loadSimilar(id: String) {
        apiService.getSimilarArtists(id).enqueue(object : Callback<SimilarArtistsResponse> {
            override fun onResponse(
                call: Call<SimilarArtistsResponse>,
                response: Response<SimilarArtistsResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val artists = response.body()?.embedded?.artists.orEmpty()
                    if (artists.isEmpty()) {
                        showError("No Similar Artists")
                    } else {
                        artistList.clear()
                        artistList.addAll(artists)
                        adapter.notifyDataSetChanged()
                        noSimilarText.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                } else {
                    showError("Failed to fetch similar artists")
                }
            }

            override fun onFailure(call: Call<SimilarArtistsResponse>, t: Throwable) {
                showError("Error fetching similar artists")
            }
        })
    }

    private fun toggleFavorite(artist: Artist) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isAlreadyFavorite = favoriteIds.contains(artist.id)

                if (isAlreadyFavorite) {
                    val res = authService.removeFromFavorites(artist.id).execute()
                    if (res.isSuccessful) {
                        favoriteIds.remove(artist.id)
                        showSnackbar("Removed from Favorites")
                    } else {
                        Log.e("SimilarFragment", "❌ Remove failed: ${res.code()} ${res.errorBody()?.string()}")
                        showSnackbar("Failed to remove favorite")
                    }
                } else {
                    val reqBody = mapOf(
                        "name" to (artist.name ?: "Unknown"),
                        "nationality" to (artist.nationality ?: "Unknown"),
                        "years" to (artist.birthday ?: "Unknown"),
                        "imageUrl" to (artist.links?.thumbnail?.href ?: "https://yourcdn.com/images/default_artist.png")
                    )

                    val res = authService.addToFavorites(artist.id, reqBody).execute()

                    if (res.isSuccessful) {
                        favoriteIds.add(artist.id)
                        showSnackbar("Added to Favorites")
                    } else {
                        val errorBody = res.errorBody()?.string()
                        Log.e("SimilarFragment", "❌ Add failed: ${res.code()} - $errorBody")
                        showSnackbar("Favorite failed")
                    }
                }

                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                Log.e("SimilarFragment", "Toggle error: ${e.message}")
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        noSimilarText.visibility = View.GONE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        noSimilarText.text = message
        noSimilarText.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun showToast(message: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSnackbar(message: String) {
        requireActivity().runOnUiThread {
            view?.let {
                Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

