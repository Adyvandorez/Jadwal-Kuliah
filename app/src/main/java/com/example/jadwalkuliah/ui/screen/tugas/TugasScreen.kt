package com.example.jadwalkuliah.ui.screen.tugas

import com.example.jadwalkuliah.ui.component.AppSwitch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.theme.DeleteRed
import com.example.jadwalkuliah.util.DateTimeUtils
import java.util.*

@Composable
fun TugasScreen(
    viewModel: TugasViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val allTugas by viewModel.allTugas.collectAsState()
    var selectedKategori by remember { mutableStateOf("Semua") }
    val kategoriFilter = listOf("Semua", "Tugas", "Catatan", "Ide")
    
    var showDeleteDialog by remember { mutableStateOf<TugasEntity?>(null) }

    val filteredTugas = if (selectedKategori == "Semua") {
        allTugas
    } else {
        allTugas.filter { it.kategori == selectedKategori }
    }

    Scaffold(
        topBar = {
            HeaderSection(title = "Tugas & Catatan")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ScrollableTabRow(
                selectedTabIndex = kategoriFilter.indexOf(selectedKategori),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                divider = {},
                indicator = {}
            ) {
                kategoriFilter.forEach { kategori ->
                    val selected = selectedKategori == kategori
                    Tab(
                        selected = selected,
                        onClick = { selectedKategori = kategori },
                        text = {
                            Surface(
                                color = if (selected) MaterialTheme.colorScheme.tertiary 
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = kategori,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = if (selected) MaterialTheme.colorScheme.onTertiary
                                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(filteredTugas) { tugas ->
                    TugasItem(
                        tugas = tugas,
                        onToggleCompletion = { viewModel.toggleTugasCompletion(tugas) },
                        onDelete = { showDeleteDialog = tugas },
                        onClick = { onNavigateToDetail(tugas.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
            text = { Text("Yakin ingin menghapus?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog?.let { viewModel.deleteTugas(it) }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeleteRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Batal", color = MaterialTheme.colorScheme.tertiary)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
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
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun TugasItem(
    tugas: TugasEntity,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val deadlineStr = DateTimeUtils.formatDeadline(tugas.deadline)
    val countdown = DateTimeUtils.getCountdown(tugas.deadline)
    val isUrgent = DateTimeUtils.isUrgent(tugas.deadline)
    val isOverdue = DateTimeUtils.isOverdue(tugas.deadline)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tugas.kategori,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = tugas.judul,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                AppSwitch(
                    checked = tugas.isCompleted,
                    onCheckedChange = { onToggleCompletion() }
                )
            }
            
            if (tugas.deadline != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = "Deadline: $deadlineStr",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (countdown.isNotEmpty()) {
                            Text(
                                text = countdown,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isOverdue) DeleteRed else if (isUrgent) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = if (tugas.isCompleted) Color(0xFF4CAF50).copy(alpha = 0.1f) 
                        else DeleteRed.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (tugas.isCompleted) "Selesai" else "Belum Selesai",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (tugas.isCompleted) Color(0xFF4CAF50) else DeleteRed
                )
            }
        }
    }
}

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    scale: Float = 1f
) {
    AppSwitch(
        checked = checked,
        onCheckedChange = { onCheckedChange?.invoke(it) },
        modifier = modifier
    )
}
