package com.example.jadwalkuliah.ui.screen.jadwal

import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.jadwalkuliah.ui.component.*
import com.example.jadwalkuliah.ui.theme.*

@Composable
fun JadwalScreen(
    viewModel: JadwalViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val allJadwal by viewModel.allJadwal.collectAsState()
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    val scrollState = rememberLazyListState()

    // Reset scroll to top when screen is created/re-entered
    LaunchedEffect(Unit) {
        scrollState.scrollToItem(0)
    }

    var showDeleteDialog by remember { mutableStateOf<JadwalEntity?>(null) }

    Scaffold(
        topBar = {
            HeaderSection(title = "Jadwal Kuliah")
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            state = scrollState,
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
                    items(jadwalHariIni, key = { it.id }) { jadwal ->
                        JadwalCard(
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
            .height(150.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CoffeeBrown, CoffeeDark)
                ),
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            )
            .padding(horizontal = 32.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = WhiteSoft
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kelola jadwal kuliah kamu!",
                style = MaterialTheme.typography.bodyMedium,
                color = WhiteSoft.copy(alpha = 0.8f)
            )
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
