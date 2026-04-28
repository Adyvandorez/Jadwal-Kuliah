package com.example.jadwalkuliah.ui.screen.tugas

import com.example.jadwalkuliah.ui.component.AppSwitch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.theme.DeleteRed
import com.example.jadwalkuliah.util.DateTimeUtils
import com.example.jadwalkuliah.util.FileUtils
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
            HeaderSectionWithBack(
                title = "Detail Tugas",
                onBack = onNavigateBack,
                actions = {
                    tugas?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (it.isCompleted) "Selesai" else "Belum Selesai",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (it.isCompleted) Color(0xFF4CAF50) else Color(0xFFFFEBEE),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            AppSwitch(
                                checked = it.isCompleted,
                                onCheckedChange = { _ ->
                                    val updated = it.copy(isCompleted = !it.isCompleted)
                                    viewModel.updateTugas(updated)
                                    tugas = updated
                                }
                            )
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        tugas?.let { data ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                InfoColumn(label = "Judul Tugas", value = data.judul, isTitle = true)
                Spacer(modifier = Modifier.height(20.dp))
                
                InfoColumn(label = "Kategori", value = data.kategori, isAccent = true)
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (data.deadline != null) {
                        Column(modifier = Modifier.weight(1f)) {
                            InfoColumn(label = "Deadline", value = DateTimeUtils.formatDeadline(data.deadline))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            val countdown = DateTimeUtils.getCountdown(data.deadline)
                            val isUrgent = DateTimeUtils.isUrgent(data.deadline)
                            val isOverdue = DateTimeUtils.isOverdue(data.deadline)
                            InfoColumn(
                                label = "Sisa Waktu", 
                                value = countdown,
                                valueColor = if (isOverdue) DeleteRed else if (isUrgent) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                
                if (data.deskripsi.isNotEmpty()) {
                    InfoColumn(label = "Deskripsi", value = data.deskripsi)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                if (data.lampiran.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Lampiran",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        data.lampiran.forEach { path ->
                            AttachmentDetailItem(filePath = path) {
                                FileUtils.openFile(context, path)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigateToEdit(tugasId) },
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
            text = { Text("Apakah Anda yakin ingin menghapus tugas ini?") },
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
fun AttachmentDetailItem(filePath: String, onClick: () -> Unit) {
    val file = File(filePath)
    val fileName = file.name
    val mimeType = FileUtils.getMimeType(filePath)
    val isImage = mimeType?.startsWith("image/") == true

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isImage) {
                Image(
                    painter = rememberAsyncImagePainter(file),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                val icon = when {
                    mimeType?.contains("pdf") == true -> Icons.Default.Description
                    mimeType?.startsWith("video/") == true -> Icons.Default.PlayCircle
                    else -> Icons.AutoMirrored.Filled.InsertDriveFile
                }
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun HeaderSectionWithBack(title: String, onBack: () -> Unit, actions: @Composable () -> Unit = {}) {
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
            .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                actions()
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
