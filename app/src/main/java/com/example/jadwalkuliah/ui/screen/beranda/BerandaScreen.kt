package com.example.jadwalkuliah.ui.screen.beranda

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.component.*
import com.example.jadwalkuliah.ui.theme.*
import com.example.jadwalkuliah.util.DateTimeUtils
import java.io.File

@Composable
fun BerandaScreen(
    viewModel: BerandaViewModel,
    onNavigateToPengingat: () -> Unit,
    onNavigateToDetailJadwal: (Int) -> Unit,
    onNavigateToDetailTugas: (Int) -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val photoPath by viewModel.photoPath.collectAsState()
    val todayJadwal by viewModel.todayJadwal.collectAsState()
    val pendingTugas by viewModel.pendingTugas.collectAsState()
    
    val scrollState = rememberLazyListState()

    // Reset scroll to top when screen is created/re-entered
    LaunchedEffect(Unit) {
        scrollState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            TopAppBarCustom(
                userName = userName,
                photoPath = photoPath
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                WelcomeCard(
                    userName = userName,
                    date = viewModel.getFormattedDate(),
                    photoPath = photoPath
                )
            }

            item {
                AlarmPengingatSection(
                    onPengingatClick = onNavigateToPengingat
                )
            }

            item {
                SectionHeader(title = "Jadwal Hari Ini")
            }

            if (todayJadwal.isEmpty()) {
                item {
                    EmptyStateCard(message = "Tidak ada jadwal untuk hari ini.")
                }
            } else {
                items(todayJadwal, key = { it.id }) { jadwal ->
                    JadwalCard(
                        jadwal = jadwal,
                        onClick = { onNavigateToDetailJadwal(jadwal.id) }
                    )
                }
            }

            item {
                SectionHeader(title = "Tugas Belum Selesai")
            }

            if (pendingTugas.isEmpty()) {
                item {
                    EmptyStateCard(message = "Semua tugas sudah selesai! 🎉")
                }
            } else {
                items(pendingTugas, key = { it.id }) { tugas ->
                    TugasCard(
                        tugas = tugas,
                        onClick = { onNavigateToDetailTugas(tugas.id) },
                        isReadOnly = true
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun TopAppBarCustom(
    userName: String,
    photoPath: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isPurpleTheme = MaterialTheme.colorScheme.primary == PurplePrimary
        val isPinkTheme = MaterialTheme.colorScheme.primary == PinkPrimary
        Text(
            text = "Jadwal Kuliah",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = when {
                    isPurpleTheme -> PurpleTextSecondary
                    isPinkTheme -> PinkPrimary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        )
        
        val isYellowTheme = MaterialTheme.colorScheme.primary == LightPrimary
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isYellowTheme -> YellowCapsuleBg 
                        isPurpleTheme -> PurpleIconActive
                        isPinkTheme -> PinkSurfaceVariant
                        isSystemInDarkTheme() && !isPurpleTheme && !isYellowTheme && !isPinkTheme -> DarkSurfaceVariant 
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (userName.isNotEmpty()) userName.take(1).uppercase() else "A",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = when {
                    isYellowTheme -> MaterialTheme.colorScheme.onPrimaryContainer 
                    isPurpleTheme -> PurplePrimary
                    isPinkTheme -> PinkPrimary
                    isSystemInDarkTheme() -> DarkTextPrimary 
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.wrapContentSize(Alignment.Center)
            )
        }
    }
}

@Composable
fun WelcomeCard(
    userName: String,
    date: String,
    photoPath: String?
) {
    val isPurpleTheme = MaterialTheme.colorScheme.primary == PurplePrimary
    val isPinkTheme = MaterialTheme.colorScheme.primary == PinkPrimary
    val onPrimaryColor = when {
        isPurpleTheme -> PurpleIconActive
        isPinkTheme -> PinkOnPrimaryContainer
        else -> MaterialTheme.colorScheme.onPrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isPurpleTheme) {
                            listOf(PurpleHeader, PurpleHeader)
                        } else if (isPinkTheme) {
                            listOf(PinkPrimaryContainer, PinkPrimaryContainer)
                        } else {
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                )
                .padding(20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.5.dp, onPrimaryColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    photoPath?.let { path ->
                        AsyncImage(
                            model = File(path),
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: run {
                        Text(
                            text = if (userName.isNotEmpty()) userName.take(1).uppercase() else "A",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = onPrimaryColor
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = onPrimaryColor)) {
                                append("Halo, Selamat Datang! ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = onPrimaryColor.copy(alpha = 0.9f))) {
                                append(userName)
                            }
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = onPrimaryColor.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun AlarmPengingatSection(
    onPengingatClick: () -> Unit
) {
    val isYellowTheme = MaterialTheme.colorScheme.primary == LightPrimary
    val isPurpleTheme = MaterialTheme.colorScheme.primary == PurplePrimary
    val isPinkTheme = MaterialTheme.colorScheme.primary == PinkPrimary
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isYellowTheme -> YellowCapsuleBg
                                isPurpleTheme -> PurpleBubble
                                isPinkTheme -> PinkSurfaceVariant
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = when {
                            isYellowTheme -> MaterialTheme.colorScheme.onPrimaryContainer 
                            isPurpleTheme -> PurpleTextSecondary
                            isPinkTheme -> PinkPrimary
                            else -> MaterialTheme.colorScheme.tertiary
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Alarm Pengingat",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
            
            Surface(
                onClick = onPengingatClick,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "+ Tambah Alarm",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = when {
                            isPurpleTheme -> PurpleIconActive
                            isPinkTheme -> PinkOnPrimaryContainer
                            else -> MaterialTheme.colorScheme.onPrimary
                        }
                    )
                )
            }
        }
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
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (MaterialTheme.colorScheme.primary == PurplePrimary) PurpleTextPrimary else MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
        )
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
