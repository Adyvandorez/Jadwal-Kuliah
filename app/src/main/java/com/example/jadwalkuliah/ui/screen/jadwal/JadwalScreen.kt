package com.example.jadwalkuliah.ui.screen.jadwal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.ui.theme.*

@Composable
fun JadwalScreen(
    viewModel: JadwalViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val allJadwal by viewModel.allJadwal.collectAsState()
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    var showDeleteDialog by remember { mutableStateOf<JadwalEntity?>(null) }

    Scaffold(
        topBar = {
            HeaderSection(title = "Jadwal Kuliah")
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            hariList.forEach { hari ->
                val jadwalHariIni = allJadwal.filter { it.hari == hari }
                item {
                    DayHeader(hari = hari)
                }
                if (jadwalHariIni.isEmpty()) {
                    item {
                        EmptyJadwalItem()
                    }
                } else {
                    items(jadwalHariIni) { jadwal ->
                        JadwalItem(
                            jadwal = jadwal,
                            onClick = { onNavigateToDetail(jadwal.id) }
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkPrimary,
                        DarkTertiary
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = WhiteSoft
        )
    }
}

@Composable
fun JadwalItem(
    jadwal: JadwalEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bagian Kiri (Waktu)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = DarkTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = jadwal.waktuMulai,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = DarkTertiary
                    )
                    Text(
                        text = jadwal.waktuSelesai,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = DarkTertiary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Bagian Kanan (Info)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jadwal.namaMatkul,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = WhiteSoft
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = jadwal.dosen,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSoftSecondary
                )
                Text(
                    text = jadwal.ruangan,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSoftSecondary
                )
            }
        }
    }
}

@Composable
fun DayHeader(hari: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(0.5.dp)
                .background(TextSoftSecondary.copy(alpha = 0.2f))
        )
        Text(
            text = hari,
            style = MaterialTheme.typography.titleMedium,
            color = WhiteSoft,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(0.5.dp)
                .background(TextSoftSecondary.copy(alpha = 0.2f))
        )
    }
}

@Composable
fun EmptyJadwalItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tidak ada jadwal",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSoftSecondary
            )
        }
    }
}
