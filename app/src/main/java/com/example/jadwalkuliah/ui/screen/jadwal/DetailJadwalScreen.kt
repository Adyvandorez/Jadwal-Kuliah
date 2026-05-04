package com.example.jadwalkuliah.ui.screen.jadwal

import androidx.compose.foundation.BorderStroke
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
import com.example.jadwalkuliah.ui.component.SimpleHeader
import com.example.jadwalkuliah.ui.theme.*

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

    val isYellowTheme = MaterialTheme.colorScheme.background == LightBackground

    Scaffold(
        topBar = {
            SimpleHeader(
                title = "Detail Jadwal",
                onBack = onNavigateBack
            )
        },
        bottomBar = {
            jadwal?.let { data ->
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onEditNavigate(data.id) },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, if (isYellowTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isYellowTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                                containerColor = if (isYellowTheme) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
                            )
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Edit", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Hapus", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        jadwal?.let { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                InfoColumn(label = "Mata Kuliah", value = data.namaMatkul, isTitle = true)
                Spacer(modifier = Modifier.height(24.dp))
                
                InfoColumn(label = "Dosen Pengampu", value = data.dosen, isAccent = true)
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        InfoColumn(label = "Hari", value = data.hari)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        InfoColumn(label = "Waktu", value = "${data.waktuMulai} - ${data.waktuSelesai}")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                InfoColumn(label = "Ruangan", value = data.ruangan)
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin menghapus jadwal ini?") },
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
                    Text("Batal", color = if (isYellowTheme) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary)
                }
            },
            shape = RoundedCornerShape(32.dp),
            containerColor = if (isYellowTheme) LightBackground else MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
    val isYellowTheme = MaterialTheme.colorScheme.background == LightBackground
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isYellowTheme) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) 
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = if (isTitle) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    else MaterialTheme.typography.titleMedium,
            color = valueColor ?: if (isAccent) MaterialTheme.colorScheme.primary 
                    else if (isYellowTheme) MaterialTheme.colorScheme.onSurface 
                    else MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )
    }
}
