package com.example.brainrowth.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URL

object ServerDiscovery {
    private const val TAG = "ServerDiscovery"
    private const val BACKEND_PORT = 3000
    private const val CONNECTION_TIMEOUT = 1000 // 1 second per IP
    private const val READ_TIMEOUT = 1000

    /**
     * Get local IP address of the device
     */
    fun getLocalIPAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                
                // Skip loopback and inactive interfaces
                if (iface.isLoopback || !iface.isUp) continue
                
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    
                    // We want IPv4 address
                    if (!addr.isLoopbackAddress && addr.hostAddress?.contains(":") == false) {
                        val ip = addr.hostAddress
                        Log.d(TAG, "Found local IP: $ip on interface ${iface.name}")
                        return ip
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting local IP", e)
        }
        return null
    }

    /**
     * Extract network prefix from IP address
     * Example: 192.168.1.50 → 192.168.1
     */
    fun getNetworkPrefix(ipAddress: String): String {
        val parts = ipAddress.split(".")
        return if (parts.size >= 3) {
            "${parts[0]}.${parts[1]}.${parts[2]}"
        } else {
            ipAddress
        }
    }

    /**
     * Test if a specific IP:port has the backend server
     */
    private suspend fun testBackendConnection(ip: String, port: Int = BACKEND_PORT): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://$ip:$port/api/solve")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = CONNECTION_TIMEOUT
                connection.readTimeout = READ_TIMEOUT
                connection.requestMethod = "GET"
                
                val responseCode = connection.responseCode
                connection.disconnect()
                
                // Backend might return 405 (Method Not Allowed) for GET on /api/solve
                // or 400 (Bad Request), but it means server is there
                val isServerFound = responseCode in 200..499
                
                if (isServerFound) {
                    Log.d(TAG, "Backend found at $ip:$port (response: $responseCode)")
                }
                
                isServerFound
            } catch (e: Exception) {
                // Connection failed - no server here
                false
            }
        }
    }

    /**
     * Scan local network subnet for backend server
     * @param onProgress Called with progress (current, total)
     * @param onFound Called when server is found
     */
    suspend fun scanForBackend(
        onProgress: ((current: Int, total: Int) -> Unit)? = null,
        onFound: ((String) -> Unit)? = null
    ): String? = withContext(Dispatchers.IO) {
        val localIP = getLocalIPAddress()
        
        if (localIP == null) {
            Log.e(TAG, "Cannot get local IP address")
            return@withContext null
        }
        
        Log.d(TAG, "Starting network scan from device IP: $localIP")
        val networkPrefix = getNetworkPrefix(localIP)
        Log.d(TAG, "Scanning subnet: $networkPrefix.x")
        
        // Prioritize common IPs first
        val priorityIPs = listOf(1, 100, 101, 102, 254) // Router, common server IPs
        val regularIPs = (2..253).filterNot { it in priorityIPs }
        val scanOrder = priorityIPs + regularIPs
        
        val total = scanOrder.size
        var current = 0
        
        // Scan in batches for better performance
        val batchSize = 20
        for (batch in scanOrder.chunked(batchSize)) {
            val results = batch.map { lastOctet ->
                async {
                    val targetIP = "$networkPrefix.$lastOctet"
                    current++
                    onProgress?.invoke(current, total)
                    
                    if (testBackendConnection(targetIP, BACKEND_PORT)) {
                        targetIP
                    } else {
                        null
                    }
                }
            }.awaitAll()
            
            // Return first found server
            val foundIP = results.filterNotNull().firstOrNull()
            if (foundIP != null) {
                Log.d(TAG, "Backend server found at: $foundIP:$BACKEND_PORT")
                onFound?.invoke(foundIP)
                return@withContext foundIP
            }
        }
        
        Log.d(TAG, "No backend server found in subnet $networkPrefix.x")
        null
    }

    /**
     * Quick test if saved IP is still valid
     */
    suspend fun testSavedServer(ip: String, port: Int = BACKEND_PORT): Boolean {
        return testBackendConnection(ip, port)
    }
}
