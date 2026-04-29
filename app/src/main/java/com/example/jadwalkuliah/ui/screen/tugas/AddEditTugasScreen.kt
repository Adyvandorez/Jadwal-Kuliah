package com.example.jadwalkuliah.ui.screen.tugas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.theme.DeleteRed
import com.example.jadwalkuliah.util.AlarmScheduler
import com.example.jadwalkuliah.util.DateTimeUtils
import com.example.jadwalkuliah.util.FileUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTugasScreen(
    viewModel: TugasViewModel,
    onNavigateBack: () -> Unit,
    tugasId: Int? = null,
    initialType: String = "Tugas"
) {
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf(initialType) }
    var deadline by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var isCompleted by remember { mutableStateOf(false) }
    var lampiran by remember { mutableStateOf<List<String>>(emptyList()) }

    val kategoriList = listOf("Tugas", "Catatan", "Ide")
    var expanded by remember { mutableStateOf(false) }

    val localeId = remember { Locale("id", "ID") }
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
            TopAppBar(
                title = { 
                    Text(
                        if (tugasId == null) "Tambah Tugas" else "Edit Tugas",
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
        }
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
                label = { Text("Judul Tugas") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    kategoriList.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                kategori = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (kategori == "Tugas") {
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
                        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    trailingIcon = {
                        if (deadlineText.isNotEmpty()) {
                            IconButton(onClick = { 
                                deadlineText = ""
                                deadline = null
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                )
            }

            // Lampiran Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Lampiran",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tambah Lampiran")
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
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Text("Simpan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
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
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                            .padding(4.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val icon = when {
                        mimeType?.contains("pdf") == true -> Icons.Default.Description
                        mimeType?.startsWith("video/") == true -> Icons.Default.PlayCircle
                        else -> Icons.Default.InsertDriveFile
                    }
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(32.dp).padding(4.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
            IconButton(onClick = {
                FileUtils.deleteFile(uri)
                onDelete()
            }) {
                Icon(Icons.Default.Close, contentDescription = "Hapus", tint = DeleteRed)
            }
        }
    }
}
