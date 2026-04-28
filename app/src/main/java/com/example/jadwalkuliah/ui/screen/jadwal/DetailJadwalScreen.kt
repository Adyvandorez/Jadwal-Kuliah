package com.example.jadwalkuliah.ui.screen.jadwal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.ui.theme.DeleteRed

@Composable
fun DetailJadwalScreen(
    jadwalId: Int,
    viewModel: JadwalViewModel,
    onNavigateBack: () -> Unit,
    onEditNavigate: (Int) -> Unit
) {
    var jadwal by remember { mutableStateOf<JadwalEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(jadwalId) {
        jadwal = viewModel.getJadwalById(jadwalId)
    }

    Scaffold(
        topBar = {
            HeaderSectionWithBack(
                title = "Detail Jadwal",
                onBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        jadwal?.let { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                InfoColumn(label = "Mata Kuliah", value = data.namaMatkul, isTitle = true)
                Spacer(modifier = Modifier.height(20.dp))
                
                InfoColumn(label = "Dosen Pengampu", value = data.dosen, isAccent = true)
                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        InfoColumn(label = "Hari", value = data.hari)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        InfoColumn(label = "Waktu", value = "${data.waktuMulai} - ${data.waktuSelesai}")
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                InfoColumn(label = "Ruangan", value = data.ruangan)

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { onEditNavigate(data.id) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Edit", fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Hapus", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus jadwal ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        jadwal?.let { viewModel.deleteJadwal(it) }
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun HeaderSectionWithBack(title: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(top = 16.dp, start = 8.dp, end = 24.dp, bottom = 24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun InfoColumn(
    label: String, 
    value: String, 
    isTitle: Boolean = false, 
    isAccent: Boolean = false,
    valueColor: Color? = null
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isTitle) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    else MaterialTheme.typography.titleMedium,
            color = valueColor ?: if (isAccent) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )
    }
}
