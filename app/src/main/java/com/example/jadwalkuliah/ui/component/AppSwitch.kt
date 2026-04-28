package com.example.jadwalkuliah.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Deteksi mode gelap berdasarkan luminance background agar lebih akurat dengan tema aplikasi
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val switchColors = if (isDark) {
        // --- MODE GELAP (Coffee Premium) ---
        // Tetap seperti spesifikasi sebelumnya
        SwitchDefaults.colors(
            // Saat aktif (ON)
            checkedThumbColor = Color(0xFF8B5F3C),
            checkedTrackColor = Color(0xFF5B402B),
            checkedBorderColor = Color(0xFF5B402B),
            
            // Saat nonaktif (OFF)
            uncheckedThumbColor = Color(0xFFE6E1E8),
            uncheckedTrackColor = Color(0xFF1E1A17),
            uncheckedBorderColor = Color(0xFF3A322D)
        )
    } else {
        // --- MODE TERANG (Sesuai spesifikasi terbaru) ---
        SwitchDefaults.colors(
            // Saat toggle aktif (ON):
            // Warna bulatan (thumb): #FEFEFE
            // Warna background (dalam): #B38A21
            // Warna garis (outline): #B38A21
            checkedThumbColor = Color(0xFFFEFEFE),
            checkedTrackColor = Color(0xFFB38A21),
            checkedBorderColor = Color(0xFFB38A21),

            // Saat toggle nonaktif (OFF):
            // Warna bulatan (thumb): #7A747E
            // Warna background (dalam): #E6E1E8
            // Warna garis (outline): #7A747E
            uncheckedThumbColor = Color(0xFF7A747E),
            uncheckedTrackColor = Color(0xFFE6E1E8),
            uncheckedBorderColor = Color(0xFF7A747E)
        )
    }

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = switchColors
    )
}
