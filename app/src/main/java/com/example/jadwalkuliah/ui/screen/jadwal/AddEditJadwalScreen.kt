package com.example.jadwalkuliah.ui.screen.jadwal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.app.TimePickerDialog
import com.example.jadwalkuliah.R
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.ui.theme.*
import com.example.jadwalkuliah.util.AlarmScheduler
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJadwalScreen(
    viewModel: JadwalViewModel,
    onNavigateBack: () -> Unit,
    jadwalId: Int? = null
) {
    var namaMatkul by remember { mutableStateOf("") }
    var dosen by remember { mutableStateOf("") }
    var hari by remember { mutableStateOf("Senin") }
    var waktuMulai by remember { mutableStateOf("08:00") }
    var waktuSelesai by remember { mutableStateOf("10:00") }
    var ruangan by remember { mutableStateOf("") }

    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    var expanded by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val alarmScheduler = remember { AlarmScheduler(context) }

    LaunchedEffect(jadwalId) {
        if (jadwalId != null) {
            val jadwal = viewModel.getJadwalById(jadwalId)
            if (jadwal != null) {
                namaMatkul = jadwal.namaMatkul
                dosen = jadwal.dosen
                hari = jadwal.hari
                waktuMulai = jadwal.waktuMulai
                waktuSelesai = jadwal.waktuSelesai
                ruangan = jadwal.ruangan
            }
        }
    }

    Scaffold(
        topBar = {
            HeaderSectionAddEdit(
                title = if (jadwalId == null) "Tambah Jadwal" else "Edit Jadwal",
                onBack = onNavigateBack
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = namaMatkul,
                onValueChange = { namaMatkul = it },
                label = { Text("Nama Mata Kuliah") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldSoft,
                    unfocusedBorderColor = DarkOutline,
                    focusedLabelColor = GoldSoft,
                    unfocusedLabelColor = TextSoftSecondary,
                    focusedTextColor = WhiteSoft,
                    unfocusedTextColor = WhiteSoft
                )
            )

            OutlinedTextField(
                value = dosen,
                onValueChange = { dosen = it },
                label = { Text("Dosen") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldSoft,
                    unfocusedBorderColor = DarkOutline,
                    focusedLabelColor = GoldSoft,
                    unfocusedLabelColor = TextSoftSecondary,
                    focusedTextColor = WhiteSoft,
                    unfocusedTextColor = WhiteSoft
                )
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = hari,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hari") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldSoft,
                        unfocusedBorderColor = DarkOutline,
                        focusedLabelColor = GoldSoft,
                        unfocusedLabelColor = TextSoftSecondary,
                        focusedTextColor = WhiteSoft,
                        unfocusedTextColor = WhiteSoft
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    hariList.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption, color = WhiteSoft) },
                            onClick = {
                                hari = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Waktu Mulai
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = waktuMulai,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mulai") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldSoft,
                            unfocusedBorderColor = DarkOutline,
                            focusedLabelColor = GoldSoft,
                            unfocusedLabelColor = TextSoftSecondary,
                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft
                        )
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                val timeParts = waktuMulai.split(":")
                                val h = if (timeParts.size == 2) timeParts[0].toIntOrNull() ?: 8 else 8
                                val m = if (timeParts.size == 2) timeParts[1].toIntOrNull() ?: 0 else 0
                                TimePickerDialog(
                                    context,
                                    R.style.CustomPickerDialogTheme,
                                    { _, hour, minute ->
                                        waktuMulai = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                                    },
                                    h, m, true
                                ).show()
                            }
                    )
                }

                // Waktu Selesai
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = waktuSelesai,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selesai") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldSoft,
                            unfocusedBorderColor = DarkOutline,
                            focusedLabelColor = GoldSoft,
                            unfocusedLabelColor = TextSoftSecondary,
                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft
                        )
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                val timeParts = waktuSelesai.split(":")
                                val h = if (timeParts.size == 2) timeParts[0].toIntOrNull() ?: 10 else 10
                                val m = if (timeParts.size == 2) timeParts[1].toIntOrNull() ?: 0 else 0
                                TimePickerDialog(
                                    context,
                                    R.style.CustomPickerDialogTheme,
                                    { _, hour, minute ->
                                        waktuSelesai = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                                    },
                                    h, m, true
                                ).show()
                            }
                    )
                }
            }

            OutlinedTextField(
                value = ruangan,
                onValueChange = { ruangan = it },
                label = { Text("Ruangan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldSoft,
                    unfocusedBorderColor = DarkOutline,
                    focusedLabelColor = GoldSoft,
                    unfocusedLabelColor = TextSoftSecondary,
                    focusedTextColor = WhiteSoft,
                    unfocusedTextColor = WhiteSoft
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val jadwal = JadwalEntity(
                        id = jadwalId ?: 0,
                        namaMatkul = namaMatkul,
                        dosen = dosen,
                        hari = hari,
                        waktuMulai = waktuMulai,
                        waktuSelesai = waktuSelesai,
                        ruangan = ruangan
                    )
                    
                    if (jadwalId == null) {
                        viewModel.insertJadwal(jadwal) { newId ->
                            alarmScheduler.scheduleJadwalAlarm(jadwal.copy(id = newId.toInt()))
                            onNavigateBack()
                        }
                    } else {
                        viewModel.updateJadwal(jadwal) {
                            alarmScheduler.scheduleJadwalAlarm(jadwal)
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldSoft,
                    contentColor = DarkBackground
                )
            ) {
                Text("Simpan Jadwal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

}

@Composable
fun HeaderSectionAddEdit(title: String, onBack: () -> Unit) {
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
            .padding(top = 16.dp, start = 16.dp, end = 32.dp, bottom = 24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = WhiteSoft
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = WhiteSoft
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Isi detail informasi dengan benar!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WhiteSoft.copy(alpha = 0.8f)
                )
            }
        }
    }
}
