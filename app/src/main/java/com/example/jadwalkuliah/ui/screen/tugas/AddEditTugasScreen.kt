package com.example.jadwalkuliah.ui.screen.tugas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jadwalkuliah.R
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.component.SimpleHeader
import com.example.jadwalkuliah.ui.theme.*
import com.example.jadwalkuliah.util.AlarmScheduler
import com.example.jadwalkuliah.util.FileUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTugasScreen(
    viewModel: TugasViewModel,
    onNavigateBack: () -> Unit,
    tugasId: Int? = null
) {
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("Tugas") }
    var deadline by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var isCompleted by remember { mutableStateOf(false) }
    var lampiran by remember { mutableStateOf<List<String>>(emptyList()) }

    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val localeId = remember { Locale.getAvailableLocales().find { it.language == "id" && it.country == "ID" } ?: Locale("id", "ID") }
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", localeId) }
    val sdfDate = remember { SimpleDateFormat("dd/MM/yyyy", localeId) }
    val sdfTime = remember { SimpleDateFormat("HH:mm", localeId) }

    val alarmScheduler = remember { AlarmScheduler(context) }
    val calendar = remember { Calendar.getInstance() }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                val savedPath = FileUtils.saveFileToInternalStorage(context, it, "tugas_files")
                if (savedPath != null && !lampiran.contains(savedPath)) {
                    lampiran = lampiran + savedPath
                }
            }
        }
    )

    LaunchedEffect(tugasId) {
        if (tugasId != null) {
            val tugas = viewModel.getTugasById(tugasId)
            if (tugas != null) {
                judul = tugas.judul
                deskripsi = tugas.deskripsi
                kategori = tugas.kategori
                deadline = tugas.deadline
                isCompleted = tugas.isCompleted
                lampiran = tugas.lampiran
            }
        }
    }

    Scaffold(
        topBar = {
            SimpleHeader(
                title = if (tugasId == null) "Tambah Tugas" else "Edit Tugas",
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
                value = judul,
                onValueChange = { judul = it },
                label = { Text(if (kategori == "Catatan") "Judul Catatan" else "Judul Tugas") },
                placeholder = { Text(if (kategori == "Catatan") "Contoh: Materi Kuliah" else "Contoh: Laporan Akhir") },
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
                value = deskripsi,
                onValueChange = { deskripsi = it },
                label = { Text("Deskripsi") },
                placeholder = { Text("Tambahkan detail di sini...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
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
                    value = kategori,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategori") },
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
                    listOf("Tugas", "Catatan").forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption, color = WhiteSoft) },
                            onClick = {
                                kategori = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Deadline / Date Selection
            val currentDeadline = deadline ?: System.currentTimeMillis()
            calendar.timeInMillis = currentDeadline

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    if (kategori == "Catatan") "Tanggal Catatan" else "Batas Waktu (Deadline)",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSoftSecondary
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Date Picker Button
                    Button(
                        onClick = {
                            val datePickerDialog = android.app.DatePickerDialog(
                                context,
                                R.style.CustomPickerDialogTheme,
                                { _, year, month, dayOfMonth ->
                                    calendar.set(Calendar.YEAR, year)
                                    calendar.set(Calendar.MONTH, month)
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    deadline = calendar.timeInMillis
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            datePickerDialog.show()
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Today, contentDescription = null, tint = GoldSoft, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = sdfDate.format(Date(currentDeadline)),
                                color = GoldSoft,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Time Picker Button
                    Button(
                        onClick = {
                            val timePickerDialog = android.app.TimePickerDialog(
                                context,
                                R.style.CustomPickerDialogTheme,
                                { _, hourOfDay, minute ->
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    calendar.set(Calendar.MINUTE, minute)
                                    deadline = calendar.timeInMillis
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            )
                            timePickerDialog.show()
                        },
                        modifier = Modifier.weight(0.8f).height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = GoldSoft, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = sdfTime.format(Date(currentDeadline)),
                                color = GoldSoft,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            if (kategori == "Tugas") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GoldSoft,
                            uncheckedColor = DarkOutline,
                            checkmarkColor = DarkBackground
                        )
                    )
                    Text("Tandai sebagai Selesai", color = WhiteSoft, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Lampiran Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Lampiran",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldSoft
                    )
                )
                
                lampiran.forEach { uri ->
                    AttachmentItem(
                        context = context,
                        uri = uri,
                        onDelete = { lampiran = lampiran - uri }
                    )
                }

                OutlinedButton(
                    onClick = { 
                        filePickerLauncher.launch(arrayOf("*/*"))
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldSoft)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tambah Lampiran", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (judul.isBlank()) return@Button
                    val tugas = TugasEntity(
                        id = tugasId ?: 0,
                        judul = judul,
                        deskripsi = deskripsi,
                        kategori = kategori,
                        deadline = deadline,
                        isCompleted = isCompleted,
                        lampiran = lampiran
                    )
                    if (tugasId == null) {
                        viewModel.insertTugas(tugas)
                    } else {
                        viewModel.updateTugas(tugas)
                    }
                    alarmScheduler.scheduleTugasAlarm(tugas)
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldSoft,
                    contentColor = DarkBackground
                )
            ) {
                Text(
                    text = if (tugasId == null) (if (kategori == "Catatan") "Simpan Catatan" else "Simpan Tugas") else "Simpan Perubahan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AttachmentItem(context: android.content.Context, uri: String, onDelete: () -> Unit) {
    val file = File(uri)
    val fileName = file.name
    val mimeType = FileUtils.getMimeType(uri)
    val isImage = mimeType?.startsWith("image/") == true

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkOutline)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                if (isImage) {
                    Image(
                        painter = rememberAsyncImagePainter(file),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val icon = when {
                        mimeType?.contains("pdf") == true -> Icons.Default.Description
                        mimeType?.startsWith("video/") == true -> Icons.Default.PlayCircle
                        else -> Icons.AutoMirrored.Filled.InsertDriveFile
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(IconBgCoffee, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = GoldSoft,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WhiteSoft,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = {
                FileUtils.deleteFile(uri)
                onDelete()
            }) {
                Box(
                    modifier = Modifier.size(32.dp).background(DeleteRed.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Hapus", tint = DeleteRed, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
