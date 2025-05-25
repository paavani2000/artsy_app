package com.example.artistsearch.network

data class FavoriteArtist(
    val id: String,
    val name: String,
    val nationality: String?,
    val years: String?,
    val imageUrl: String?
)
