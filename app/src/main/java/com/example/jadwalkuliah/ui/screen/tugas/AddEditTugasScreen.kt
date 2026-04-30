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
import coil.compose.rememberAsyncImagePainter
import com.example.jadwalkuliah.data.local.entity.TugasEntity
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

    val kategoriList = listOf("Tugas", "Catatan", "Proyek", "Lainnya")
    var expanded by remember { mutableStateOf(false) }

    val localeId = remember { Locale.getAvailableLocales().find { it.language == "id" && it.country == "ID" } ?: Locale("id", "ID") }
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", localeId) }
    var deadlineText by remember { 
        mutableStateOf(deadline?.let { sdf.format(Date(it)) } ?: "") 
    }
    
    val context = LocalContext.current
    val alarmScheduler = remember { AlarmScheduler(context) }

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
                deadlineText = deadline?.let { sdf.format(Date(it)) } ?: ""
                isCompleted = tugas.isCompleted
                lampiran = tugas.lampiran
            }
        }
    }

    Scaffold(
        topBar = {
            HeaderSectionAddEdit(
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
                label = { Text("Judul Tugas") },
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
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
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
                    kategoriList.forEach { selectionOption ->
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

            OutlinedTextField(
                value = deadlineText,
                onValueChange = { 
                    deadlineText = it
                    try {
                        deadline = if (it.isBlank()) null else sdf.parse(it)?.time
                    } catch (e: Exception) { }
                },
                label = { Text("Deadline (tgl/bln/thn jam:menit)") },
                placeholder = { Text("01/01/2024 23:59") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldSoft,
                    unfocusedBorderColor = DarkOutline,
                    focusedLabelColor = GoldSoft,
                    unfocusedLabelColor = TextSoftSecondary,
                    focusedTextColor = WhiteSoft,
                    unfocusedTextColor = WhiteSoft
                ),
                trailingIcon = {
                    if (deadlineText.isNotEmpty()) {
                        IconButton(onClick = { 
                            deadlineText = ""
                            deadline = null
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSoftSecondary)
                        }
                    }
                }
            )

            // Lampiran Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    border = BorderStroke(1.dp, DarkOutline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldSoft)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tambah Lampiran", fontWeight = FontWeight.Bold)
                }
            }

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
                    viewModel.insertTugas(tugas)
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
                Text("Simpan Tugas", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
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
            .height(140.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CoffeeBrown, CoffeeDark)
                ),
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            )
            .padding(top = 16.dp, start = 8.dp, end = 24.dp, bottom = 24.dp),
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
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = WhiteSoft,
                modifier = Modifier.padding(start = 16.dp)
            )
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
