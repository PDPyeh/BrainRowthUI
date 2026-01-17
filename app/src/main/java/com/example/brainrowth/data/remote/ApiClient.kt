package com.example.brainrowth.data.remote

import android.content.Context
import com.example.brainrowth.network.ServerPreferences
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Default fallback for emulator
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:3000/"
    
    private var retrofit: Retrofit? = null
    private var currentBaseUrl: String? = null

    /**
     * Initialize ApiClient with context to load saved server config
     */
    fun init(context: Context) {
        val serverPrefs = ServerPreferences(context)
        val savedUrl = serverPrefs.getBaseUrl()
        
        // Use saved URL if available, otherwise use default
        val baseUrl = if (!savedUrl.isNullOrBlank()) {
            "$savedUrl/"
        } else {
            DEFAULT_BASE_URL
        }
        
        // Only rebuild if URL changed
        if (baseUrl != currentBaseUrl) {
            currentBaseUrl = baseUrl
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    /**
     * Update base URL dynamically (after server config changes)
     */
    fun updateBaseUrl(baseUrl: String) {
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        if (formattedUrl != currentBaseUrl) {
            currentBaseUrl = formattedUrl
            retrofit = Retrofit.Builder()
                .baseUrl(formattedUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    val api: BrainRowthApi
        get() {
            if (retrofit == null) {
                // Fallback: build with default URL if not initialized
                retrofit = Retrofit.Builder()
                    .baseUrl(currentBaseUrl ?: DEFAULT_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!.create(BrainRowthApi::class.java)
        }
}
