package com.example.artistsearch

import com.example.artistsearch.network.SimilarArtistsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
import retrofit2.Call

// 🎯 First define ArtistDetails
data class ArtistDetails(
    val name: String,
    val birthday: String?,
    val deathday: String?,
    val nationality: String?,
    val biography: String?,
    @SerializedName("image_url") val imageUrl: String?
)

interface ApiService {

    // Search artists
    @GET("search")
    suspend fun searchArtists(
        @Query("q") query: String
    ): SearchResponse

    // 🎯 New function: Get artist details by ID
    @GET("artist_details/{artist_id}")
    suspend fun getArtistDetails(
        @Path("artist_id") artistId: String
    ): ArtistDetails

    @GET("artworks")
    suspend fun getArtworks(
        @Query("artist_id") artistId: String
    ): ArtworksResponse

    @GET("categories")
    suspend fun getCategories(
        @Query("artwork_id") artworkId: String
    ): CategoriesResponse

    @GET("similar_artists/{id}")
    fun getSimilarArtists(
        @Path("id") artistId: String
    ): Call<SimilarArtistsResponse>

}

data class SearchResponse(
    @SerializedName("_embedded") val embedded: EmbeddedResults
)

data class EmbeddedResults(
    @SerializedName("results") val results: List<ArtistResult>
)

data class ArtistResult(
    val title: String,
    @SerializedName("_links") val links: ArtistLinks
)

data class ArtistLinks(
    val thumbnail: Thumbnail?,
    val self: SelfLink?
)

// --------------------------------------------

// --------------------------------------------
// 📚 Models - Artworks Response
data class ArtworksResponse(
    @SerializedName("_embedded") val embedded: EmbeddedArtworks
)

data class EmbeddedArtworks(
    @SerializedName("artworks") val artworks: List<Artwork>
)

data class Artwork(
    val title: String?,
    val date: String?,
    @SerializedName("_links") val links: ArtworkLinks
)

data class ArtworkLinks(
    val thumbnail: Thumbnail?,
    val self: SelfLink?
)

// --------------------------------------------
// 📚 Models - Categories (Genes) Response
data class CategoriesResponse(
    @SerializedName("_embedded") val embedded: EmbeddedCategories
)

data class EmbeddedCategories(
    @SerializedName("genes") val genes: List<Category>
)

data class Category(
    val name: String,
    val description: String?,
    @SerializedName("_links") val links: CategoryLinks
)

data class CategoryLinks(
    val thumbnail: Thumbnail?
)

// --------------------------------------------
// 📚 Common Reusable Models
data class Thumbnail(
    val href: String?
)

data class SelfLink(
    val href: String
)