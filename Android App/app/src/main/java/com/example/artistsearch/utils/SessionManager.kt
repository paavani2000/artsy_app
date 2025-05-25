package com.example.artistsearch.utils

import android.content.Context
import android.util.Log
import com.example.artistsearch.network.SerializableCookie

import okhttp3.Cookie
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object SessionManager {
    private const val COOKIE_PREFS = "CookiePersistence"
    private const val COOKIE_KEY = "cookie"

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE)
        val jsonCookies = prefs.getString(COOKIE_KEY, null) ?: return false

        return try {
            val serializedCookies: List<SerializableCookie> = Json.decodeFromString(jsonCookies)
            serializedCookies
                .mapNotNull { it.toOkHttpCookie() } // convert to actual Cookie
                .any { it.name == "session_id" && !it.hasExpired() }
        } catch (e: Exception) {
            Log.e("SessionManager", "Failed to parse cookies: ${e.message}")
            false
        }
    }

    private fun Cookie.hasExpired(): Boolean {
        return this.expiresAt < System.currentTimeMillis()
    }

}
