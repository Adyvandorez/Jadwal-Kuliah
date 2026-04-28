package com.example.jadwalkuliah.ui.screen.pengingat

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jadwalkuliah.data.local.entity.PengingatEntity
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditPengingatScreen(
    viewModel: PengingatViewModel,
    onNavigateBack: () -> Unit,
    pengingatId: Int? = null
) {
    var judul by remember { mutableStateOf("") }
    var waktu by remember { mutableStateOf("12:00") }
    var tipeUlang by remember { mutableStateOf("Daily") }
    val selectedDays = remember { mutableStateListOf<String>() }
    var isActive by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    LaunchedEffect(pengingatId) {
        if (pengingatId != null) {
            val pengingat = viewModel.getPengingatById(pengingatId)
            if (pengingat != null) {
                judul = pengingat.judul
                waktu = pengingat.waktu
                tipeUlang = pengingat.tipeUlang
                selectedDays.clear()
                selectedDays.addAll(pengingat.hariTerpilih)
                isActive = pengingat.isActive
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (pengingatId == null) "Tambah Pengingat" else "Edit Pengingat",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it },
                label = { Text("Judul Kegiatan") },
                placeholder = { Text("Contoh: Beri Makan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Button(
                onClick = {
                    val timeParts = waktu.split(":")
                    val h = timeParts[0].toInt()
                    val m = timeParts[1].toInt()
                    TimePickerDialog(
                        context,
                        { _, hour, minute -> 
                            waktu = String.format(Locale.getDefault(), "%02d:%02d", hour, minute) 
                        },
                        h, m, true
                    ).show()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Waktu: $waktu", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Medium)
                }
            }

            Text("Ulangi", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TipeUlangChipS( "Harian", tipeUlang == "Daily") { tipeUlang = "Daily" }
                TipeUlangChipS("Sekali", tipeUlang == "Sekali") { tipeUlang = "Sekali" }
                TipeUlangChipS("Custom", tipeUlang == "Custom") { tipeUlang = "Custom" }
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
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (judul.isNotBlank()) {
                        val pengingat = PengingatEntity(
                            id = pengingatId ?: 0,
                            judul = judul,
                            waktu = waktu,
                            tipeUlang = tipeUlang,
                            hariTerpilih = selectedDays.toList(),
                            isActive = isActive
                        )
                        if (pengingatId == null) {
                            viewModel.insert(pengingat)
                        } else {
                            viewModel.update(pengingat)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text(
                    if (pengingatId == null) "Simpan Pengingat" else "Simpan Perubahan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TipeUlangChipS(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.onTertiary,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = if (selected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
