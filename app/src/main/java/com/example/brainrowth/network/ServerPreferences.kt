package com.example.brainrowth.network

import android.content.Context
import android.content.SharedPreferences

class ServerPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "server_config",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_SERVER_IP = "server_ip"
        private const val KEY_SERVER_PORT = "server_port"
        private const val KEY_AUTO_DETECTED = "auto_detected"
        private const val DEFAULT_PORT = "3000"
    }

    fun saveServerConfig(ip: String, port: String = DEFAULT_PORT, autoDetected: Boolean = false) {
        prefs.edit().apply {
            putString(KEY_SERVER_IP, ip)
            putString(KEY_SERVER_PORT, port)
            putBoolean(KEY_AUTO_DETECTED, autoDetected)
            apply()
        }
    }

    fun getServerIP(): String? {
        return prefs.getString(KEY_SERVER_IP, null)
    }

    fun getServerPort(): String {
        return prefs.getString(KEY_SERVER_PORT, DEFAULT_PORT) ?: DEFAULT_PORT
    }

    fun getBaseUrl(): String? {
        val ip = getServerIP()
        val port = getServerPort()
        return if (ip != null) "http://$ip:$port" else null
    }

    fun isAutoDetected(): Boolean {
        return prefs.getBoolean(KEY_AUTO_DETECTED, false)
    }

    fun clearServerConfig() {
        prefs.edit().clear().apply()
    }

    fun hasServerConfig(): Boolean {
        return getServerIP() != null
    }
}
