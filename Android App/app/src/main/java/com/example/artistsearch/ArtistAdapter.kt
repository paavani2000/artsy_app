package com.example.artistsearch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.artistsearch.databinding.ItemArtistBinding

class ArtistAdapter(
    private val context: Context,
    private val favoritedIds: MutableSet<String>,
    private val isLoggedIn: Boolean,
    private val onToggleFavorite: (String, Boolean) -> Unit
) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    private val artists = mutableListOf<ArtistResult>()

    fun updateArtists(newArtists: List<ArtistResult>) {
        artists.clear()
        artists.addAll(newArtists)
        notifyDataSetChanged()
    }

    fun getCurrentList(): List<ArtistResult> = artists.toList()

    inner class ArtistViewHolder(private val binding: ItemArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(artist: ArtistResult) {
            binding.artistName.text = artist.title

            val imageUrl = artist.links.thumbnail?.href
            if (imageUrl.isNullOrEmpty()) {
                binding.artistImage.setImageResource(R.drawable.artsy_logo_playstore)
            } else {
                binding.artistImage.load(imageUrl) {
                    error(R.drawable.artsy_logo_playstore)
                }
            }

            val artistId = artist.links.self?.href?.split("/")?.lastOrNull()

            if (isLoggedIn && artistId != null) {
                binding.starIcon.visibility = View.VISIBLE

                val isFavorited = favoritedIds.contains(artistId)
                binding.starIcon.setImageResource(
                    if (isFavorited) R.drawable.baseline_star_24
                    else R.drawable.baseline_star_border_24
                )

                binding.starIcon.setOnClickListener {
                    onToggleFavorite(artistId, !isFavorited)
                }

            } else {
                binding.starIcon.visibility = View.GONE
                binding.starIcon.setOnClickListener(null)
            }

            binding.rightArrow.setOnClickListener {
                artistId?.let {
                    val intent = Intent(context, ArtistDetailsActivity::class.java)
                    intent.putExtra("artist_id", it)
                    intent.putExtra("artist_name", artist.title)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artists[position])
    }

    override fun getItemCount(): Int = artists.size
}
