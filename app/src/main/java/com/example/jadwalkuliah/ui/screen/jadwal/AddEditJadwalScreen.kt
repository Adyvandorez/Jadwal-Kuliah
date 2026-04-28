package com.example.jadwalkuliah.ui.screen.jadwal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.util.AlarmScheduler

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
            TopAppBar(
                title = { 
                    Text(
                        if (jadwalId == null) "Tambah Jadwal" else "Edit Jadwal",
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
                value = namaMatkul,
                onValueChange = { namaMatkul = it },
                label = { Text("Nama Mata Kuliah") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            OutlinedTextField(
                value = dosen,
                onValueChange = { dosen = it },
                label = { Text("Dosen") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
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
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    hariList.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                hari = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = waktuMulai,
                    onValueChange = { waktuMulai = it },
                    label = { Text("Mulai (HH:mm)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
                OutlinedTextField(
                    value = waktuSelesai,
                    onValueChange = { waktuSelesai = it },
                    label = { Text("Selesai (HH:mm)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
            }

            OutlinedTextField(
                value = ruangan,
                onValueChange = { ruangan = it },
                label = { Text("Ruangan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
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
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Simpan Jadwal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

}
