/*
package com.example.artistsearch.network

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var retrofit: Retrofit? = null
    private var cookieJar: PersistentCookieJar? = null

    fun getClient(context: Context): AuthService {
        if (cookieJar == null) {
            cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context.applicationContext))
        }

        if (retrofit == null) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .cookieJar(cookieJar!!)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl("https://android-backend-458823.wl.r.appspot.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit!!.create(AuthService::class.java)
    }
}
*/

package com.example.artistsearch.network

import android.content.Context
import com.example.artistsearch.ApiService  // ✅ This is your existing ApiService
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var retrofit: Retrofit? = null
    private var cookieJar: PersistentCookieJar? = null

    private fun getRetrofit(context: Context): Retrofit {
        if (cookieJar == null) {
            cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context.applicationContext))
        }

        if (retrofit == null) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .cookieJar(cookieJar!!)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl("https://android-backend-458823.wl.r.appspot.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit!!
    }

    fun getAuthService(context: Context): AuthService {
        return getRetrofit(context).create(AuthService::class.java)
    }

    fun getApiService(context: Context): ApiService {
        return getRetrofit(context).create(ApiService::class.java)
    }
}

