package com.example.jadwalkuliah.ui.screen.pengaturan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.jadwalkuliah.data.repository.UserProfileRepository
import com.example.jadwalkuliah.ui.component.SimpleHeader
import com.example.jadwalkuliah.ui.theme.*
import com.example.jadwalkuliah.util.FileUtils
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun EditProfilScreen(
    userProfileRepository: UserProfileRepository,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val initialName by userProfileRepository.userName.collectAsState(initial = "Mahasiswa")
    val initialPhoto by userProfileRepository.photoPath.collectAsState(initial = null)
    
    val context = LocalContext.current
    var name by remember(initialName) { mutableStateOf(initialName) }
    var photoPath by remember(initialPhoto) { mutableStateOf(initialPhoto) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val savedPath = FileUtils.saveFileToInternalStorage(context, it, "profile_photos")
                if (savedPath != null) {
                    // Delete old photo if exists
                    photoPath?.let { oldPath -> FileUtils.deleteFile(oldPath) }
                    photoPath = savedPath
                }
            }
        }
    )

    Scaffold(
        topBar = {
            SimpleHeader(title = "Edit Profil", onBack = onNavigateBack)
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(DarkSurface)
                    .border(2.dp, GoldSoft, CircleShape)
                    .clickable {
                        photoPickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (photoPath != null) {
                    photoPath?.let { path ->
                        AsyncImage(
                            model = File(path),
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = GoldSoft.copy(alpha = 0.5f)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)))),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color(0xFFB8A899),
                        modifier = Modifier.padding(bottom = 8.dp).size(20.dp)
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Mahasiswa") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = WhiteSoft,
                    unfocusedTextColor = WhiteSoft,
                    focusedBorderColor = GoldSoft,
                    unfocusedBorderColor = DarkOutline,
                    focusedLabelColor = GoldSoft,
                    unfocusedLabelColor = TextSoftSecondary,
                    cursorColor = GoldSoft
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        userProfileRepository.saveProfile(name, photoPath)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldSoft)
            ) {
                Text("Simpan Perubahan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkBackground)
            }
        }
    }
}

