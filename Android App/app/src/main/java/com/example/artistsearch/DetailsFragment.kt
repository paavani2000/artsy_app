package com.example.artistsearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.artistsearch.databinding.FragmentDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private var artistId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🎯 Get artist_id passed from the parent Activity
        artistId = activity?.intent?.getStringExtra("artist_id")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🎯 Start fetching artist details
        artistId?.let { fetchArtistDetails(it) }
    }

    private fun fetchArtistDetails(artistId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://android-backend-458823.wl.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                // Show ProgressBar, Hide Content
                binding.progressBar.visibility = View.VISIBLE
                binding.detailsContent.visibility = View.GONE
            }

            try {
                val artistDetails = apiService.getArtistDetails(artistId.trim())
                withContext(Dispatchers.Main) {
                    // Hide ProgressBar, Show Content
                    binding.progressBar.visibility = View.GONE
                    binding.detailsContent.visibility = View.VISIBLE

                    binding.artistName.text = artistDetails.name
                    binding.artistLifespan.text = buildString {
                        append(artistDetails.nationality ?: "")
                        if (!artistDetails.birthday.isNullOrEmpty()) {
                            append(", ${artistDetails.birthday}")
                        }
                        if (!artistDetails.deathday.isNullOrEmpty()) {
                            append(" - ${artistDetails.deathday}")
                        }
                    }
                    binding.artistBio.text = artistDetails.biography ?: "No biography available"
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.detailsContent.visibility = View.VISIBLE
                    binding.artistName.text = "Failed to load artist details"
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
