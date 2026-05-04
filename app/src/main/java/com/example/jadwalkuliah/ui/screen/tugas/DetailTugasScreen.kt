package com.example.jadwalkuliah.ui.screen.tugas

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.component.*
import com.example.jadwalkuliah.ui.theme.*
import com.example.jadwalkuliah.util.DateTimeUtils
import java.io.File

@Composable
fun DetailTugasScreen(
    viewModel: TugasViewModel,
    tugasId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit
) {
    var tugas by remember { mutableStateOf<TugasEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    LaunchedEffect(tugasId) {
        tugas = viewModel.getTugasById(tugasId)
    }

    Scaffold(
        topBar = {
            SimpleHeader(
                title = if (tugas?.kategori == "Catatan") "Detail Catatan" else "Detail Tugas",
                onBack = onNavigateBack
            )
        },
        bottomBar = {
            val isYellowTheme = MaterialTheme.colorScheme.background == LightBackground
            tugas?.let { data ->
                Surface(
                    color = if (isYellowTheme) MaterialTheme.colorScheme.background else DarkBackground,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToEdit(data.id) },
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isYellowTheme) MaterialTheme.colorScheme.surface else DarkSurface
                            ),
                            border = BorderStroke(1.dp, (if (isYellowTheme) MaterialTheme.colorScheme.primary else GoldSoft).copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Default.Edit, 
                                contentDescription = null, 
                                tint = if (isYellowTheme) MaterialTheme.colorScheme.primary else GoldSoft, 
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Edit", 
                                fontWeight = FontWeight.Bold, 
                                color = if (isYellowTheme) MaterialTheme.colorScheme.primary else GoldSoft, 
                                fontSize = 16.sp
                            )
                        }

                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                            shape = RoundedCornerShape(24.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFB8A899), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Hapus", fontWeight = FontWeight.Bold, color = Color(0xFFB8A899), fontSize = 16.sp)
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        tugas?.let { data ->
            val isYellowTheme = MaterialTheme.colorScheme.background == LightBackground
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                val isTugas = data.kategori != "Catatan"
                // ... rest of content ...

                InfoColumn(label = "Judul", value = data.judul, isTitle = true)
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        InfoColumn(label = "Kategori", value = data.kategori.ifEmpty { "Tugas" }, isAccent = true)
                    }
                    if (isTugas) {
                        Column(modifier = Modifier.weight(1f)) {
                            val status = if (data.isCompleted) "Selesai" else "Belum Selesai"
                            val statusColor = if (data.isCompleted) SuccessGreen else DeleteRed
                            InfoColumn(label = "Status", value = status, valueColor = statusColor)
                        }
                    }
                }

                if (isTugas && data.deadline != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            InfoColumn(
                                label = "Deadline", 
                                value = DateTimeUtils.formatDeadline(data.deadline),
                                isYellowTheme = isYellowTheme
                            )
                        }
                        val countdown = DateTimeUtils.getCountdown(data.deadline)
                        if (countdown.isNotEmpty()) {
                            val isOverdue = DateTimeUtils.isOverdue(data.deadline)
                            Column(modifier = Modifier.weight(1f)) {
                                InfoColumn(
                                    label = "Countdown",
                                    value = countdown,
                                    valueColor = if (isOverdue) DeleteRed else if (isYellowTheme) MaterialTheme.colorScheme.primary else GoldSoft,
                                    isYellowTheme = isYellowTheme
                                )
                            }
                        }
                    }
                }

                if (data.deskripsi.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    InfoColumn(label = "Deskripsi", value = data.deskripsi, isYellowTheme = isYellowTheme)
                }

                // Lampiran Section
                if (data.lampiran.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Lampiran",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isYellowTheme) MaterialTheme.colorScheme.primary else GoldSoft
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    data.lampiran.forEach { path ->
                        DetailAttachmentCard(
                            path = path,
                            onOpen = { openFile(context, path) },
                            onShare = { shareFile(context, path) },
                            onDelete = {
                                val updatedLampiran = data.lampiran.filter { it != path }
                                viewModel.updateTugas(data.copy(lampiran = updatedLampiran))
                                tugas = data.copy(lampiran = updatedLampiran)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showDeleteDialog) {
        val isYellowTheme = MaterialTheme.colorScheme.background == LightBackground
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin menghapus ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        tugas?.let { viewModel.deleteTugas(it) }
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus", color = Color(0xFFB8A899), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = if (isYellowTheme) MaterialTheme.colorScheme.primary else GoldSoft)
                }
            },
            shape = RoundedCornerShape(32.dp),
            containerColor = if (isYellowTheme) MaterialTheme.colorScheme.surface else DarkSurface,
            titleContentColor = if (isYellowTheme) MaterialTheme.colorScheme.onSurface else WhiteSoft,
            textContentColor = if (isYellowTheme) MaterialTheme.colorScheme.onSurfaceVariant else TextSoftSecondary
        )
    }
}

private fun shareFile(context: Context, path: String) {
    val file = File(path)
    if (!file.exists()) return
    
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = context.contentResolver.getType(uri) ?: "*/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Bagikan via"))
}

private fun openFile(context: Context, path: String) {
    val file = File(path)
    if (!file.exists()) return
    
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, context.contentResolver.getType(uri) ?: "*/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Buka dengan"))
}

@Composable
fun InfoColumn(
    label: String,
    value: String,
    isTitle: Boolean = false,
    isAccent: Boolean = false,
    valueColor: Color? = null,
    isYellowTheme: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isYellowTheme) MaterialTheme.colorScheme.onSurfaceVariant else TextSoftSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = if (isTitle) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.titleMedium,
            color = valueColor ?: if (isAccent) (if (isYellowTheme) MaterialTheme.colorScheme.primary else GoldSoft) else (if (isYellowTheme) MaterialTheme.colorScheme.onSurface else WhiteSoft),
            lineHeight = 24.sp
        )
    }
}
