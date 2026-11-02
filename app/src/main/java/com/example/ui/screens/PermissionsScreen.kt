package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JarvisGlassCard
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisSuccess
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.SkyCyan

data class PermissionSpec(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val reason: String,
    val isCritical: Boolean = true
)

@Composable
fun PermissionsScreen(
    onPermissionsCleared: () -> Unit
) {
    var isAuthorized by remember { mutableStateOf(false) }

    val permissionList = remember {
        listOf(
            PermissionSpec("mic", Icons.Default.Mic, "Microphone Access", "Enables voice wake-word ('Sky'/'Max') and conversational speech."),
            PermissionSpec("notif", Icons.Default.Notifications, "Notifications & Reminders", "Delivers spoken alarms, sleep alerts, and meeting notifications."),
            PermissionSpec("alarm", Icons.Default.Alarm, "Exact System Alarms", "Allows precise scheduling of daily alarms and reminder rings."),
            PermissionSpec("phone", Icons.Default.Phone, "Phone & Calling", "Enables automated voice dialing ('Call Mummy') on voice command."),
            PermissionSpec("contacts", Icons.Default.Contacts, "Contacts Registry", "Identifies named contacts for calls, SMS, and WhatsApp dispatch."),
            PermissionSpec("camera", Icons.Default.CameraAlt, "Vision & Camera", "Powers visual analysis and photo generation/editing."),
            PermissionSpec("bluetooth", Icons.Default.Bluetooth, "Bluetooth & Audio Output", "Connects to Bluetooth speakers, earbuds, and nearby devices."),
            PermissionSpec("storage", Icons.Default.SdStorage, "SD Card & Local Storage", "Encrypted backup and restore of Sky & Max memory files."),
            PermissionSpec("internet", Icons.Default.Wifi, "Internet & Cloud Updates", "Powers live cricket scores, global news, and GitHub APK updates.")
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        isAuthorized = true
        onPermissionsCleared()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("permissions_screen"),
        color = JarvisBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // JARVIS Security Header
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(SkyCyan.copy(alpha = 0.15f))
                    .border(2.dp, SkyCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "JARVIS Security Protocol",
                    tint = SkyCyan,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "JARVIS SYSTEM CLEARANCE",
                color = SkyCyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )

            Text(
                text = "Sir, to enable Sky & Max full automation, kindly authorize the following core protocols on your device:",
                color = JarvisTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                lineHeight = 18.sp
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(permissionList) { spec ->
                    JarvisGlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SkyCyan.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = spec.icon,
                                    contentDescription = spec.title,
                                    tint = SkyCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = spec.title,
                                    color = JarvisTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = spec.reason,
                                    color = JarvisTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = if (isAuthorized) JarvisSuccess else SkyCyan.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val permissions = mutableListOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.SEND_SMS
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
                        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
                    }
                    permissionLauncher.launch(permissions.toTypedArray())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("authorize_jarvis_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SkyCyan,
                    contentColor = JarvisBackground
                )
            ) {
                Text(
                    text = "AUTHORIZE ALL & INITIALIZE JARVIS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
