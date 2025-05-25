package com.example.artistsearch.network

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Response wrapping "_embedded"
data class SimilarArtistsResponse(
    @SerializedName("_embedded") val embedded: EmbeddedArtists?
)

data class EmbeddedArtists(
    @SerializedName("artists") val artists: List<Artist>
)

data class Artist(
    val id: String,
    val name: String,
    val birthday: String?,
    val nationality: String?,
    @SerializedName("_links") val links: ArtistLinks?
)

data class ArtistLinks(
    val thumbnail: ThumbnailLink?
)

data class ThumbnailLink(
    val href: String
)

// Authentication and user
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class SearchResponse(
    @SerializedName("_embedded") val embedded: EmbeddedResults?
)

data class EmbeddedResults(
    @SerializedName("results") val results: List<Artist>
)

data class FavoriteArtistRequest(
    val artistId: String,
    val name: String,
    val nationality: String?,
    val years: String?,
    val imageUrl: String?,
    val created_at: String,
)




interface AuthService {
    @POST("/api/auth/register")
    fun register(@Body body: RegisterRequest): Call<Void>

    @POST("/api/auth/login")
    fun login(@Body body: LoginRequest): Call<LoginResponse>

    @GET("/api/auth/user-profile")
    fun getUserProfile(): Call<UserProfile>

    @POST("/api/auth/logout")
    fun logout(): Call<Void>

    @DELETE("/api/auth/delete-account")
    fun deleteAccount(): Call<Void>

    @GET("similar_artists/{id}")
    fun getSimilarArtists(@Path("id") artistId: String): Call<SimilarArtistsResponse>

    @POST("/api/auth/favourites/{artistId}")
    fun addToFavorites(@Path("artistId") artistId: String, @Body body: Map<String, String> ): Call<Void>

    @DELETE("/api/auth/favourites/{id}") // ✅ CORRECT
    fun removeFromFavorites(@Path("id") id: String): Call<Void>


    @GET("/api/auth/favourites")
    fun getFavorites(): Call<List<FavoriteArtistRequest>>


}
