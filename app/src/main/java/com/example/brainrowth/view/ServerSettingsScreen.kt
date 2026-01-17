package com.example.brainrowth.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainrowth.network.ServerDiscovery
import com.example.brainrowth.network.ServerPreferences
import kotlinx.coroutines.launch

@Composable
fun ServerSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val serverPrefs = remember { ServerPreferences(context) }
    val scope = rememberCoroutineScope()
    
    var manualIP by remember { mutableStateOf(serverPrefs.getServerIP() ?: "") }
    var manualPort by remember { mutableStateOf(serverPrefs.getServerPort()) }
    var isScanning by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableStateOf(0 to 0) } // current, total
    var scanResult by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var localIP by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        localIP = ServerDiscovery.getLocalIPAddress()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Server Configuration",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Device IP Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Your Device IP:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    localIP ?: "Detecting...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                if (localIP != null) {
                    Text(
                        "Will scan: ${ServerDiscovery.getNetworkPrefix(localIP!!)}.x",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Auto-Detect Section
        Text(
            "Auto-Detect Server",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (!isScanning) {
                    isScanning = true
                    errorMessage = null
                    scanResult = null
                    
                    scope.launch {
                        val foundIP = ServerDiscovery.scanForBackend(
                            onProgress = { current, total ->
                                scanProgress = current to total
                            },
                            onFound = { ip ->
                                scanResult = ip
                            }
                        )
                        
                        isScanning = false
                        
                        if (foundIP != null) {
                            serverPrefs.saveServerConfig(foundIP, autoDetected = true)
                            manualIP = foundIP
                        } else {
                            errorMessage = "No backend server found in local network"
                        }
                    }
                }
            },
            enabled = !isScanning && localIP != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isScanning) "Scanning..." else "Scan Network")
        }

        if (isScanning) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = {
                    if (scanProgress.second > 0) 
                        scanProgress.first.toFloat() / scanProgress.second 
                    else 0f
                },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Scanning ${scanProgress.first} / ${scanProgress.second} IPs...",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (scanResult != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✓", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "Server Found!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "$scanResult:3000",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Manual Input Section
        Text(
            "Manual Configuration",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = manualIP,
            onValueChange = { manualIP = it },
            label = { Text("Server IP Address") },
            placeholder = { Text("e.g., 192.168.1.100") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = manualPort,
            onValueChange = { manualPort = it },
            label = { Text("Port") },
            placeholder = { Text("3000") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    serverPrefs.clearServerConfig()
                    manualIP = ""
                    manualPort = "3000"
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }

            Button(
                onClick = {
                    if (manualIP.isNotBlank()) {
                        serverPrefs.saveServerConfig(manualIP, manualPort, autoDetected = false)
                        onBack()
                    }
                },
                enabled = manualIP.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Current Config Display
        if (serverPrefs.hasServerConfig()) {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Current Configuration",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Base URL: ${serverPrefs.getBaseUrl()}",
                        fontSize = 12.sp
                    )
                    Text(
                        "Method: ${if (serverPrefs.isAutoDetected()) "Auto-detected" else "Manual"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
