package com.example.artistsearch.network

import kotlinx.serialization.Serializable
import okhttp3.Cookie

@Serializable
data class SerializableCookie(
    val name: String,
    val value: String,
    val domain: String,
    val path: String,
    val expiresAt: Long
) {
    fun toOkHttpCookie(): Cookie {
        return Cookie.Builder()
            .name(name)
            .value(value)
            .domain(domain)
            .path(path)
            .expiresAt(expiresAt)
            .build()
    }

    fun isExpired(): Boolean = expiresAt < System.currentTimeMillis()
}
