package com.example.jadwalkuliah.ui.screen.pengingat

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
import com.example.jadwalkuliah.data.local.entity.PengingatEntity
import com.example.jadwalkuliah.ui.theme.*

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
            DetailPengingatHeader(
                title = "Detail Pengingat",
                onBack = onNavigateBack
            )
        },
        bottomBar = {
            pengingat?.let { data ->
                Surface(
                    color = DarkBackground,
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
                            border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = GoldSoft)
                            Spacer(Modifier.width(8.dp))
                            Text("Edit", fontWeight = FontWeight.Bold, color = GoldSoft)
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
        containerColor = DarkBackground
    ) { innerPadding ->
        pengingat?.let { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                InfoItem(label = "Judul Kegiatan", value = data.judul, isTitle = true)
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        InfoItem(label = "Waktu", value = data.waktu, isAccent = true)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        InfoItem(label = "Status", value = if (data.isActive) "Aktif" else "Nonaktif")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                InfoItem(
                    label = "Tipe Pengulangan", 
                    value = if (data.tipeUlang == "Daily") "Setiap Hari" else if (data.tipeUlang == "Sekali") "Sekali Saja" else "Kustom"
                )
                
                if (data.tipeUlang == "Custom" && data.hariTerpilih.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    InfoItem(label = "Hari Terpilih", value = data.hariTerpilih.joinToString(", "))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin menghapus pengingat ini?") },
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
                    Text("Batal", color = GoldSoft)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = DarkSurface,
            titleContentColor = WhiteSoft,
            textContentColor = TextSoftSecondary
        )
    }
}

@Composable
fun DetailPengingatHeader(title: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CoffeeBrown, CoffeeDark)
                ),
                shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
            )
            .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            IconButton(
                onClick = onBack,
                modifier = Modifier.offset(x = (-12).dp, y = (-40).dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = WhiteSoft,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = WhiteSoft
            )
            Text(
                text = "Detail informasi pengingat kamu!",
                style = MaterialTheme.typography.bodyMedium,
                color = WhiteSoft.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String,
    isTitle: Boolean = false,
    isAccent: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSoftSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = if (isTitle) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    else MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (isAccent) GoldSoft else WhiteSoft
        )
    }
}
