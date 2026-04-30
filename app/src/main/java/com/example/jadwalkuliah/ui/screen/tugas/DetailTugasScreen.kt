package com.example.jadwalkuliah.ui.screen.tugas

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.component.*
import com.example.jadwalkuliah.ui.theme.*

@Composable
fun DetailTugasScreen(
    viewModel: TugasViewModel,
    tugasId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit
) {
    var tugas by remember { mutableStateOf<TugasEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(tugasId) {
        tugas = viewModel.getTugasById(tugasId)
    }

    Scaffold(
        topBar = {
            HeaderSectionWithBack(
                title = "Detail Tugas",
                onBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { onNavigateToEdit(tugasId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = WhiteSoft)
                    }
                }
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        tugas?.let { data ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                TugasCard(
                    tugas = data,
                    onClick = {},
                    onToggleCompletion = {
                        val updated = data.copy(isCompleted = !data.isCompleted)
                        viewModel.updateTugas(updated)
                        tugas = updated
                    },
                    onDelete = { showDeleteDialog = true }
                )
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
                    Text("Batal", color = DarkTertiary)
                }
            },
            shape = RoundedCornerShape(32.dp),
            containerColor = DarkSurface,
            titleContentColor = WhiteSoft,
            textContentColor = TextSoftSecondary
        )
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
                    colors = listOf(DarkPrimary, DarkTertiary)
                ),
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
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
                        tint = WhiteSoft
                    )
                }
                actions()
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = WhiteSoft,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
