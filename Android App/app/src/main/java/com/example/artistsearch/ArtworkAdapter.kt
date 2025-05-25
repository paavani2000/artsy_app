package com.example.artistsearch
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.artistsearch.databinding.ItemArtworkBinding


class ArtworkAdapter : RecyclerView.Adapter<ArtworkAdapter.ArtworkViewHolder>() {

    private val artworks = mutableListOf<Artwork>()

    fun updateArtworks(newArtworks: List<Artwork>) {
        artworks.clear()
        artworks.addAll(newArtworks)
        notifyDataSetChanged()
    }

    inner class ArtworkViewHolder(private val binding: ItemArtworkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(artwork: Artwork) {
            val title = artwork.title ?: "Untitled"
            val date = artwork.date ?: ""

            if (date.isNotEmpty()) {
                binding.artworkTitle.text = "$title, $date"
            } else {
                binding.artworkTitle.text = title
            }

            val imageUrl = artwork.links.thumbnail?.href
            if (!imageUrl.isNullOrEmpty()) {
                binding.artworkImage.load(imageUrl) {
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                    crossfade(true)
                }
            } else {
                binding.artworkImage.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // 🎯 Add View Categories button click here
            binding.viewCategoriesButton.setOnClickListener {
                val context = binding.root.context
                val artworkId = extractArtworkId(artwork)
                if (artworkId != null) {
                    val dialog = CategoryDialog(context, artworkId)
                    dialog.show()
                }
            }
        }
        private fun extractArtworkId(artwork: Artwork): String? {
            return artwork.links.self?.href?.split("/")?.lastOrNull()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtworkViewHolder {
        val binding = ItemArtworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtworkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtworkViewHolder, position: Int) {
        holder.bind(artworks[position])
    }

    override fun getItemCount(): Int = artworks.size
}
