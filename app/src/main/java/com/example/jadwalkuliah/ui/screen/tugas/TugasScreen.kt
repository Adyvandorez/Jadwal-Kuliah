package com.example.jadwalkuliah.ui.screen.tugas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.theme.DeleteRed
import com.example.jadwalkuliah.util.DateTimeUtils
import kotlinx.coroutines.launch

@Composable
fun TugasScreen(
    viewModel: TugasViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val allItems by viewModel.allTugas.collectAsState()
    val allTugas = allItems.filter { it.kategori == "Tugas" }
    
    val tabs = listOf("Semua", "Belum Selesai", "Selesai")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()
    
    var showDeleteDialog by remember { mutableStateOf<TugasEntity?>(null) }

    Scaffold(
        topBar = {
            HeaderSection(title = "Tugas Kuliah")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Tab Row
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.tertiary 
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val filteredList = when (pageIndex) {
                    0 -> allTugas
                    1 -> allTugas.filter { !it.isCompleted }
                    2 -> allTugas.filter { it.isCompleted }
                    else -> allTugas
                }

                if (filteredList.isEmpty()) {
                    EmptyState(tabs[pageIndex])
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                    ) {
                        items(filteredList, key = { it.id }) { tugas ->
                            TugasItem(
                                tugas = tugas,
                                onToggleCompletion = { viewModel.toggleTugasCompletion(tugas) },
                                onDelete = { showDeleteDialog = tugas },
                                onClick = { onNavigateToDetail(tugas.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                showDeleteDialog?.let { viewModel.deleteTugas(it) }
                showDeleteDialog = null
            }
        )
    }
}

@Composable
fun CatatanScreen(
    viewModel: TugasViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val allItems by viewModel.allTugas.collectAsState()
    val catatanList = allItems.filter { it.kategori == "Catatan" }
    var showDeleteDialog by remember { mutableStateOf<TugasEntity?>(null) }

    Scaffold(
        topBar = { HeaderSection(title = "Catatan Kuliah") },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (catatanList.isEmpty()) {
            EmptyState("Catatan")
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                items(catatanList, key = { it.id }) { item ->
                    SimplifiedItem(
                        item = item,
                        onDelete = { showDeleteDialog = item },
                        onClick = { onNavigateToDetail(item.id) },
                        showAttachments = true
                    )
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                showDeleteDialog?.let { viewModel.deleteTugas(it) }
                showDeleteDialog = null
            }
        )
    }
}

@Composable
fun IdeScreen(
    viewModel: TugasViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val allItems by viewModel.allTugas.collectAsState()
    val ideList = allItems.filter { it.kategori == "Ide" }
    var showDeleteDialog by remember { mutableStateOf<TugasEntity?>(null) }

    Scaffold(
        topBar = { HeaderSection(title = "Ide Kreatif") },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (ideList.isEmpty()) {
            EmptyState("Ide")
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                items(ideList, key = { it.id }) { item ->
                    SimplifiedItem(
                        item = item,
                        onDelete = { showDeleteDialog = item },
                        onClick = { onNavigateToDetail(item.id) },
                        showAttachments = false,
                        maxLines = 2
                    )
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                showDeleteDialog?.let { viewModel.deleteTugas(it) }
                showDeleteDialog = null
            }
        )
    }
}

@Composable
fun SimplifiedItem(
    item: TugasEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    showAttachments: Boolean = true,
    maxLines: Int = Int.MAX_VALUE
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = item.judul,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(DeleteRed.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Delete, null, tint = DeleteRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (item.deskripsi.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (showAttachments && item.lampiran.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                item.lampiran.forEach { uri -> AttachmentChip(uri) }
            }
        }
    }
}

@Composable
fun EmptyState(type: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Tidak ada $type", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DeleteConfirmDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
        text = { Text("Yakin ingin menghapus item ini?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = DeleteRed)
            ) { Text("Hapus", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun HeaderSection(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TugasItem(
    tugas: TugasEntity,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onToggleCompletion, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (tugas.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = null,
                        tint = if (tugas.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Text(
                    text = tugas.judul,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (tugas.isCompleted) TextDecoration.LineThrough else null
                    ),
                    color = if (tugas.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) 
                            else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(DeleteRed.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Delete, null, tint = DeleteRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (tugas.deskripsi.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = tugas.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (tugas.deadline != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = DateTimeUtils.formatDeadline(tugas.deadline), style = MaterialTheme.typography.bodySmall)
                    }
                    val countdown = DateTimeUtils.getCountdown(tugas.deadline)
                    if (countdown.isNotEmpty()) {
                        Text(
                            text = countdown,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (DateTimeUtils.isOverdue(tugas.deadline)) DeleteRed 
                                    else if (DateTimeUtils.isUrgent(tugas.deadline)) MaterialTheme.colorScheme.tertiary 
                                    else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentChip(uri: String) {
    val context = LocalContext.current
    val fileName = DateTimeUtils.getFileName(context, uri)
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AttachFile, null, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = fileName, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
