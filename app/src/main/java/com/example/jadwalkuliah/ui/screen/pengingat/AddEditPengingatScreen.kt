package com.example.jadwalkuliah.ui.screen.pengingat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.example.jadwalkuliah.R
import com.example.jadwalkuliah.data.local.entity.PengingatEntity
import com.example.jadwalkuliah.ui.component.SimpleHeader
import com.example.jadwalkuliah.ui.theme.*
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
    val localeId = remember { Locale.getAvailableLocales().find { it.language == "id" && it.country == "ID" } ?: Locale("id", "ID") }
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
            SimpleHeader(
                title = if (pengingatId == null) "Tambah Pengingat" else "Edit Pengingat",
                onBack = onNavigateBack
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
            val isYellowTheme = MaterialTheme.colorScheme.background == LightBackground
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it },
                label = { Text("Judul Kegiatan") },
                placeholder = { Text("Contoh: Beri Makan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (isYellowTheme) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = if (isYellowTheme) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Button(
                onClick = {
                    val timeParts = waktu.split(":")
                    val h = timeParts[0].toInt()
                    val m = timeParts[1].toInt()
                    android.app.TimePickerDialog(
                        context,
                        R.style.CustomPickerDialogTheme,
                        { _, hour, minute -> 
                            waktu = String.format(localeId, "%02d:%02d", hour, minute) 
                        },
                        h, m, true
                    ).show()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isYellowTheme) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime, 
                        contentDescription = null, 
                        tint = if (isYellowTheme) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Waktu: $waktu", 
                        color = if (isYellowTheme) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary, 
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text("Ulangi", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TipeUlangChipS( "Harian", tipeUlang == "Daily", Modifier.weight(1f)) { tipeUlang = "Daily" }
                TipeUlangChipS("Sekali", tipeUlang == "Sekali", Modifier.weight(1f)) { tipeUlang = "Sekali" }
                TipeUlangChipS("Custom", tipeUlang == "Custom", Modifier.weight(1f)) { tipeUlang = "Custom" }
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
                            label = { Text(day, fontSize = 12.sp, color = if(isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outline,
                                selectedBorderColor = MaterialTheme.colorScheme.primary
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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    if (pengingatId == null) "Simpan Pengingat" else "Simpan Perubahan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TipeUlangChipS(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = label,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
