package com.example.artistsearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.example.artistsearch.databinding.FragmentArtworksBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArtworksFragment : Fragment() {

    private var _binding: FragmentArtworksBinding? = null
    private val binding get() = _binding!!

    private lateinit var artworkAdapter: ArtworkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtworksBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initialize RecyclerView
//        artworkAdapter = ArtworkAdapter()
//        binding.artworksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        binding.artworksRecyclerView.adapter = artworkAdapter
//
//        // Hide "no artworks" initially
//        binding.noArtworksTextView.visibility = View.GONE
//
//        // Get artist_id from parent activity
//        val artistId = activity?.intent?.getStringExtra("artist_id")
//
//        artistId?.let { id ->
//            fetchArtworks(id)
//        }
//    }
//
//    private fun fetchArtworks(artistId: String) {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://android-backend-458823.wl.r.appspot.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apiService = retrofit.create(ApiService::class.java)
//
//        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                val response = apiService.getArtworks(artistId)
//                val artworks = response.embedded.artworks
//
//                withContext(Dispatchers.Main) {
//                    if (artworks.isEmpty()) {
//                        binding.noArtworksTextView.visibility = View.VISIBLE
//                        binding.artworksRecyclerView.visibility = View.GONE
//                    } else {
//                        binding.noArtworksTextView.visibility = View.GONE
//                        binding.artworksRecyclerView.visibility = View.VISIBLE
//                        artworkAdapter.updateArtworks(artworks)
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                withContext(Dispatchers.Main) {
//                    binding.noArtworksTextView.visibility = View.VISIBLE
//                    binding.artworksRecyclerView.visibility = View.GONE
//                }
//            }
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artworkAdapter = ArtworkAdapter()
        binding.artworksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.artworksRecyclerView.adapter = artworkAdapter

        binding.noArtworksTextView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.artworksRecyclerView.visibility = View.GONE

        val artistId = activity?.intent?.getStringExtra("artist_id")
        artistId?.let { fetchArtworks(it) }
    }

    private fun fetchArtworks(artistId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://android-backend-458823.wl.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getArtworks(artistId)
                val artworks = response.embedded.artworks

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE

                    if (artworks.isEmpty()) {
                        binding.noArtworksTextView.visibility = View.VISIBLE
                        binding.artworksRecyclerView.visibility = View.GONE
                    } else {
                        binding.noArtworksTextView.visibility = View.GONE
                        binding.artworksRecyclerView.visibility = View.VISIBLE
                        artworkAdapter.updateArtworks(artworks)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.noArtworksTextView.visibility = View.VISIBLE
                    binding.artworksRecyclerView.visibility = View.GONE
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
