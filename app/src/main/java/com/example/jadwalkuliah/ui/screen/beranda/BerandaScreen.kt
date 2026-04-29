package com.example.jadwalkuliah.ui.screen.beranda

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.theme.*
import com.example.jadwalkuliah.util.DateTimeUtils
import java.io.File

@Composable
fun BerandaScreen(
    viewModel: BerandaViewModel,
    onNavigateToPengingat: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val photoPath by viewModel.photoPath.collectAsState()
    val todayJadwal by viewModel.todayJadwal.collectAsState()
    val pendingTugas by viewModel.pendingTugas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarCustom(
                userName = userName,
                photoPath = photoPath
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
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
                items(todayJadwal) { jadwal ->
                    JadwalCard(jadwal = jadwal)
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
                items(pendingTugas) { tugas ->
                    TugasCard(tugas = tugas)
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Aplikasi Jadwal Kuliah",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isSystemInDarkTheme()) TextSoftSecondary else MaterialTheme.colorScheme.onBackground
            )
        )
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isSystemInDarkTheme()) DarkOutline else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (userName.isNotEmpty()) userName.take(1).uppercase() else "A",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSystemInDarkTheme()) WhiteSoft else MaterialTheme.colorScheme.onSurfaceVariant,
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
                    brush = Brush.linearGradient(
                        colors = listOf(CoffeeBrown, CoffeeDark)
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
                        .background(if (isSystemInDarkTheme()) DarkSurfaceVariant else Color(0xFF8A8A8A))
                        .border(2.5.dp, Color.White, CircleShape),
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
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = WhiteSoft)) {
                                append("Halo, Selamat Datang! ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = GoldSoft)) {
                                append(userName)
                            }
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoldSoft.copy(alpha = 0.8f)
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
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(DarkSurfaceVariant, DarkSurface)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IconBgCoffee),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = GoldSoft,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Alarm Pengingat",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = WhiteSoft
                        )
                    )
                }
                
                Surface(
                    onClick = onPengingatClick,
                    shape = CircleShape,
                    color = Color.Transparent,
                    modifier = Modifier.clip(CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(CoffeeBrown, DarkTertiary)
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ Tambah Alarm",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = WhiteSoft
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = if (isSystemInDarkTheme()) GoldSoft else MaterialTheme.colorScheme.tertiary
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun JadwalCard(jadwal: JadwalEntity, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(IconBgCoffee)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = GoldSoft,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = jadwal.waktuMulai,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldSoft
                    )
                    Text(
                        text = jadwal.waktuSelesai,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldSoft
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jadwal.namaMatkul,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = WhiteSoft
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = jadwal.dosen,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSoftSecondary
                )
                Text(
                    text = jadwal.ruangan,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSoftSecondary
                )
            }
        }
    }
}

@Composable
fun TugasCard(tugas: TugasEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = tugas.judul,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = WhiteSoft
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = tugas.kategori,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            if (tugas.deskripsi.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = tugas.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSoftSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (tugas.deadline != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF8FBCFF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = DateTimeUtils.formatDeadline(tugas.deadline).substringBefore(","),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSoftSecondary
                        )
                    }

                    val countdown = DateTimeUtils.getCountdown(tugas.deadline)
                    if (countdown.isNotEmpty()) {
                        Text(
                            text = countdown,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (DateTimeUtils.isOverdue(tugas.deadline)) DeleteRed 
                                    else if (DateTimeUtils.isUrgent(tugas.deadline)) GoldSoft
                                    else TextSoftSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(32.dp)
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
