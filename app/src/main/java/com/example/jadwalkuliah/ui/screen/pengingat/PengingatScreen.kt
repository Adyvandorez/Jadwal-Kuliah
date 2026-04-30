package com.example.jadwalkuliah.ui.screen.pengingat

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.jadwalkuliah.ui.component.AppSwitch
import com.example.jadwalkuliah.data.local.entity.PengingatEntity
import com.example.jadwalkuliah.ui.screen.tugas.HeaderSection
import com.example.jadwalkuliah.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PengingatScreen(
    viewModel: PengingatViewModel,
    navController: NavController,
    onBackClick: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val pengingatList by viewModel.allPengingat.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var pengingatToEdit by remember { mutableStateOf<PengingatEntity?>(null) }
    var pengingatToDelete by remember { mutableStateOf<PengingatEntity?>(null) }
    
    // Listen to FAB clicks from NavGraph
    val fabClicked by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("fab_clicked", false)
        ?.collectAsState() ?: remember { mutableStateOf(false) }

    LaunchedEffect(fabClicked) {
        if (fabClicked == true) {
            showAddDialog = true
            navController.currentBackStackEntry?.savedStateHandle?.set("fab_clicked", false)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HeaderSection(
                title = "Pengingat Rutin",
                subtitle = "Kelola pengingat harian Anda",
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearching = isSearching,
                onSearchToggle = { 
                    isSearching = it
                    if (!it) searchQuery = ""
                }
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        val filteredList = remember(pengingatList, searchQuery) {
            if (searchQuery.isEmpty()) pengingatList
            else pengingatList.filter { it.judul.contains(searchQuery, ignoreCase = true) }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            items(filteredList, key = { it.id }) { pengingat ->
                PengingatItem(
                    pengingat = pengingat,
                    onToggle = { viewModel.togglePengingat(pengingat) },
                    onDelete = { pengingatToDelete = pengingat },
                    onClick = { onNavigateToDetail(pengingat.id) }
                )
            }
        }

        if (showAddDialog) {
            AddEditPengingatDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { judul, waktu, tipe, hari ->
                    viewModel.insert(
                        PengingatEntity(
                            judul = judul,
                            waktu = waktu,
                            tipeUlang = tipe,
                            hariTerpilih = hari
                        )
                    )
                    showAddDialog = false
                }
            )
        }

        pengingatToEdit?.let { pengingat ->
            AddEditPengingatDialog(
                pengingat = pengingat,
                onDismiss = { pengingatToEdit = null },
                onConfirm = { judul, waktu, tipe, hari ->
                    viewModel.update(
                        pengingat.copy(
                            judul = judul,
                            waktu = waktu,
                            tipeUlang = tipe,
                            hariTerpilih = hari
                        )
                    )
                    pengingatToEdit = null
                }
            )
        }

        pengingatToDelete?.let { pengingat ->
            AlertDialog(
                onDismissRequest = { pengingatToDelete = null },
                title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
                text = { Text("Yakin ingin menghapus pengingat '${pengingat.judul}'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.delete(pengingat)
                            pengingatToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hapus", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pengingatToDelete = null }) {
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
}

@Composable
fun PengingatItem(
    pengingat: PengingatEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kotak Ikon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CoffeeBrown.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (pengingat.judul.lowercase().contains("makan")) Icons.Default.Restaurant else Icons.Default.Notifications,
                    contentDescription = null,
                    tint = GoldSoft,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pengingat.judul,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = WhiteSoft
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pengingat.waktu,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = GoldSoft
                    )
                    Text(
                        text = " • ${if (pengingat.tipeUlang == "Daily") "Harian" else if (pengingat.tipeUlang == "Sekali") "Sekali" else "Kustom"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSoftSecondary
                    )
                }
            }

            AppSwitch(
                checked = pengingat.isActive,
                onCheckedChange = { onToggle() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(DeleteRed.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Delete, null, tint = DeleteRed, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditPengingatDialog(
    pengingat: PengingatEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, List<String>) -> Unit
) {
    var judul by remember { mutableStateOf(pengingat?.judul ?: "") }
    var waktu by remember { mutableStateOf(pengingat?.waktu ?: "12:00") }
    var tipeUlang by remember { mutableStateOf(pengingat?.tipeUlang ?: "Daily") }
    val selectedDays = remember { 
        mutableStateListOf<String>().apply {
            pengingat?.hariTerpilih?.let { addAll(it) }
        }
    }
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = DarkSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (pengingat == null) "Tambah Pengingat" else "Edit Pengingat",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = WhiteSoft
                )

                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Judul Kegiatan") },
                    placeholder = { Text("Contoh: Beri Makan") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldSoft,
                        unfocusedBorderColor = DarkOutline,
                        focusedLabelColor = GoldSoft,
                        unfocusedLabelColor = TextSoftSecondary,
                        focusedTextColor = WhiteSoft,
                        unfocusedTextColor = WhiteSoft
                    )
                )

                Button(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, h, m -> waktu = String.format(Locale.getDefault(), "%02d:%02d", h, m) },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown.copy(alpha = 0.1f))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = GoldSoft)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Waktu: $waktu", color = GoldSoft)
                    }
                }

                Text("Ulangi", style = MaterialTheme.typography.labelLarge, color = TextSoftSecondary)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TipeUlangChip("Harian", tipeUlang == "Daily") { tipeUlang = "Daily" }
                    TipeUlangChip("Sekali", tipeUlang == "Sekali") { tipeUlang = "Sekali" }
                    TipeUlangChip("Custom", tipeUlang == "Custom") { tipeUlang = "Custom" }
                }

                if (tipeUlang == "Custom") {
                    val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        days.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            FilterChip(
                                selected = isSelected,
                                onClick = { if (isSelected) selectedDays.remove(day) else selectedDays.add(day) },
                                label = { Text(day, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldSoft,
                                    selectedLabelColor = DarkBackground,
                                    labelColor = TextSoftSecondary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal", color = GoldSoft)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (judul.isNotBlank()) onConfirm(judul, waktu, tipeUlang, selectedDays.toList()) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldSoft),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Simpan", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TipeUlangChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = if (selected) GoldSoft else CoffeeBrown.copy(alpha = 0.1f),
        border = if (selected) null else BorderStroke(1.dp, DarkOutline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = DarkBackground,
                    unselectedColor = TextSoftSecondary
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = if (selected) DarkBackground else TextSoftSecondary,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
