package com.example.artistsearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.artistsearch.network.Artist

//class SimilarArtistsAdapter(
//    private val artists: List<Artist>,
//    private val onClick: (String) -> Unit // Pass artist ID on click
//) : RecyclerView.Adapter<SimilarArtistsAdapter.ViewHolder>() {
//
//    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val name: TextView = view.findViewById(R.id.artistName)
//        val image: ImageView = view.findViewById(R.id.artistImage)
//        val arrow: ImageView = view.findViewById(R.id.rightArrow)
//
//        init {
//            view.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    onClick(artists[position].id)
//                }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_artist, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount() = artists.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val artist = artists[position]
//        holder.name.text = artist.name
//        holder.image.load(artist.links?.thumbnail?.href) {
//            placeholder(R.drawable.ic_launcher_foreground)
//            error(R.drawable.ic_launcher_foreground)
//            crossfade(true)
//        }
//    }
//}

class SimilarArtistsAdapter(
    private val artists: List<Artist>,
    private val favoritedArtistIds: MutableSet<String>,
    private val onCardClick: (String) -> Unit,
    private val onStarClick: (Artist) -> Unit
) : RecyclerView.Adapter<SimilarArtistsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.artistName)
        val image: ImageView = view.findViewById(R.id.artistImage)
        val star: ImageView = view.findViewById(R.id.starIcon)
        val arrow: ImageView = view.findViewById(R.id.rightArrow)

        fun bind(artist: Artist) {
            name.text = artist.name
            image.load(artist.links?.thumbnail?.href) {
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
                crossfade(true)
            }

            // Update star icon based on current favorite state
            val isFav = favoritedArtistIds.contains(artist.id)
            star.setImageResource(
                if (isFav) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24
            )

            star.setOnClickListener {
                onStarClick(artist)
            }

            itemView.setOnClickListener {
                onCardClick(artist.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = artists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(artists[position])
    }
}


