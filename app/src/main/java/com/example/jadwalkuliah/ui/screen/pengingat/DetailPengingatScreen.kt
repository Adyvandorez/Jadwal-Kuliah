package com.example.jadwalkuliah.ui.screen.pengingat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jadwalkuliah.data.local.entity.PengingatEntity
import com.example.jadwalkuliah.ui.screen.jadwal.HeaderSectionWithBack
import com.example.jadwalkuliah.ui.screen.jadwal.InfoColumn
import com.example.jadwalkuliah.ui.theme.DeleteRed

@Composable
fun DetailPengingatScreen(
    pengingatId: Int,
    viewModel: PengingatViewModel,
    onNavigateBack: () -> Unit,
    onEditNavigate: (Int) -> Unit
) {
    var pengingat by remember { mutableStateOf<PengingatEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pengingatId) {
        pengingat = viewModel.getPengingatById(pengingatId)
    }

    Scaffold(
        topBar = {
            HeaderSectionWithBack(
                title = "Detail Pengingat",
                onBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        pengingat?.let { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                InfoColumn(label = "Judul Kegiatan", value = data.judul, isTitle = true)
                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        InfoColumn(label = "Waktu", value = data.waktu, isAccent = true)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        InfoColumn(label = "Status", value = if (data.isActive) "Aktif" else "Nonaktif")
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                InfoColumn(
                    label = "Tipe Pengulangan", 
                    value = if (data.tipeUlang == "Daily") "Setiap Hari" else if (data.tipeUlang == "Sekali") "Sekali Saja" else "Kustom"
                )
                
                if (data.tipeUlang == "Custom" && data.hariTerpilih.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    InfoColumn(label = "Hari Terpilih", value = data.hariTerpilih.joinToString(", "))
                }

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
            text = { Text("Apakah Anda yakin ingin menghapus pengingat ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        pengingat?.let { viewModel.delete(it) }
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
