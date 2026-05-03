package com.example.jadwalkuliah.ui.screen.tugas

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.component.*
import com.example.jadwalkuliah.ui.theme.*
import com.example.jadwalkuliah.util.DateTimeUtils
import kotlinx.coroutines.launch

@Composable
fun TugasScreen(
    viewModel: TugasViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val allItems by viewModel.allTugas.collectAsState()
    val tabs = listOf("Semua", "Tugas", "Catatan")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()
    
    val scrollState0 = rememberLazyListState()
    val scrollState1 = rememberLazyListState()
    val scrollState2 = rememberLazyListState()

    // Reset scroll and pager when screen is entered
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(0)
        scrollState0.scrollToItem(0)
        scrollState1.scrollToItem(0)
        scrollState2.scrollToItem(0)
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<TugasEntity?>(null) }

    Scaffold(
        topBar = {
            HeaderSection(
                title = "Tugas & Catatan",
                subtitle = "Kelola semua tugas kuliah kamu",
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearching = isSearching,
                onSearchToggle = { 
                    isSearching = it
                    if (!it) searchQuery = ""
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Tab Filter Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = pagerState.currentPage == index
                    Surface(
                        onClick = { scope.launch { pagerState.scrollToPage(index) } },
                        color = if (isSelected) DarkTertiary else DarkSurfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) DarkBackground else TextSoftSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                val filteredItems = remember(allItems, searchQuery, pageIndex) {
                    val baseList = when (pageIndex) {
                        1 -> allItems.filter { it.kategori == "Tugas" }
                        2 -> allItems.filter { it.kategori == "Catatan" }
                        else -> allItems
                    }
                    if (searchQuery.isEmpty()) {
                        baseList
                    } else {
                        baseList.filter { 
                            it.judul.contains(searchQuery, ignoreCase = true) || 
                            it.deskripsi.contains(searchQuery, ignoreCase = true) 
                        }
                    }
                }

                if (filteredItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isEmpty()) "Belum ada ${tabs[pageIndex].lowercase()}" else "Tidak ada hasil pencarian",
                            color = TextSoftSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        state = when(pageIndex) {
                            1 -> scrollState1
                            2 -> scrollState2
                            else -> scrollState0
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        if (pageIndex == 0) {
                            // Section Tugas
                            val tugasList = filteredItems.filter { it.kategori == "Tugas" }
                            if (tugasList.isNotEmpty()) {
                                item { SectionHeader("Tugas") }
                                items(tugasList, key = { it.id }) { tugas ->
                                    TugasCard(
                                        tugas = tugas,
                                        onToggleCompletion = { viewModel.toggleTugasCompletion(tugas) },
                                        onDelete = { showDeleteDialog = tugas },
                                        onClick = { onNavigateToDetail(tugas.id) }
                                    )
                                }
                            }

                            // Section Catatan
                            val catatanList = filteredItems.filter { it.kategori == "Catatan" }
                            if (catatanList.isNotEmpty()) {
                                item { SectionHeader("Catatan") }
                                items(catatanList, key = { it.id }) { item ->
                                    CatatanItem(
                                        item = item,
                                        onDelete = { showDeleteDialog = item },
                                        onClick = { onNavigateToDetail(item.id) }
                                    )
                                }
                            }
                        } else if (pageIndex == 1) {
                            items(filteredItems, key = { it.id }) { tugas ->
                                TugasCard(
                                    tugas = tugas,
                                    onToggleCompletion = { viewModel.toggleTugasCompletion(tugas) },
                                    onDelete = { showDeleteDialog = tugas },
                                    onClick = { onNavigateToDetail(tugas.id) }
                                )
                            }
                        } else {
                            items(filteredItems, key = { it.id }) { item ->
                                CatatanItem(
                                    item = item,
                                    onDelete = { showDeleteDialog = item },
                                    onClick = { onNavigateToDetail(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
            text = { Text("Yakin ingin menghapus item ini kamu?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog?.let { viewModel.deleteTugas(it) }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeleteRed)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Batal", color = DarkTertiary)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = DarkSurface
        )
    }
}

@Composable
fun SectionHeader(title: String) {
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
            text = title,
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
fun HeaderSection(
    title: String,
    subtitle: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    onSearchToggle: (Boolean) -> Unit
) {
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
        if (isSearching) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                placeholder = { 
                    Text(
                        "Cari...", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = WhiteSoft.copy(alpha = 0.5f) 
                    ) 
                },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = WhiteSoft) },
                trailingIcon = {
                    IconButton(onClick = { onSearchToggle(false) }) {
                        Icon(Icons.Default.Close, null, tint = WhiteSoft)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = WhiteSoft.copy(alpha = 0.1f),
                    unfocusedContainerColor = WhiteSoft.copy(alpha = 0.1f),
                    focusedTextColor = WhiteSoft,
                    unfocusedTextColor = WhiteSoft,
                    cursorColor = DarkTertiary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = WhiteSoft
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WhiteSoft.copy(alpha = 0.8f)
                    )
                }
                IconButton(
                    onClick = { onSearchToggle(true) },
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .background(WhiteSoft.copy(alpha = 0.1f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = WhiteSoft, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
fun CatatanItem(
    item: TugasEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A17))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.judul,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = WhiteSoft
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        CategoryBadge(
                            text = "Catatan",
                            containerColor = DarkPrimary.copy(alpha = 0.2f),
                            contentColor = DarkTertiary
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
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
                        color = TextSoftSecondary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (item.lampiran.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    AttachmentRow(
                        lampiran = item.lampiran,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                if (item.deadline != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    DateDisplay(date = item.deadline)
                }
            }
        }
    }
}
