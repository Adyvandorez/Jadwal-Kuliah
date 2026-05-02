package com.example.jadwalkuliah.ui.screen.pengaturan

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.jadwalkuliah.R
import com.example.jadwalkuliah.data.local.AlarmPreferences
import com.example.jadwalkuliah.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun NadaDeringScreen(
    alarmPreferences: AlarmPreferences,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val selectedRingtone by alarmPreferences.alarmRingtoneResId.collectAsState(initial = R.raw.alarm_1)
    
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var playingResId by remember { mutableStateOf<Int?>(null) }

    val ringtones = listOf(
        RingtoneItem("Alarm 1", R.raw.alarm_1),
        RingtoneItem("Alarm 2", R.raw.alarm_2),
        RingtoneItem("Alarm 3", R.raw.alarm_3),
        RingtoneItem("Alarm 4", R.raw.alarm_4)
    )

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    fun playAudio(resId: Int) {
        if (playingResId == resId) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            playingResId = null
        } else {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.start()
            playingResId = resId
            mediaPlayer?.setOnCompletionListener {
                playingResId = null
            }
        }
    }

    Scaffold(
        topBar = {
            NadaDeringHeader(onBack = onNavigateBack)
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ringtones) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    onClick = {
                        scope.launch { alarmPreferences.saveAlarmRingtone(item.resId) }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedRingtone == item.resId,
                                onClick = {
                                    scope.launch { alarmPreferences.saveAlarmRingtone(item.resId) }
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = GoldSoft,
                                    unselectedColor = TextSoftSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = WhiteSoft,
                                fontWeight = if (selectedRingtone == item.resId) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        IconButton(
                            onClick = { playAudio(item.resId) },
                            modifier = Modifier
                                .background(
                                    if (playingResId == item.resId) GoldSoft else DarkSurfaceVariant,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (playingResId == item.resId) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (playingResId == item.resId) CoffeeDark else GoldSoft
                            )
                        }
                    }
                }
            }
        }
    }
}

data class RingtoneItem(val name: String, val resId: Int)

@Composable
private fun NadaDeringHeader(onBack: () -> Unit) {
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
            .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            IconButton(
                onClick = onBack,
                modifier = Modifier.offset(x = (-12).dp, y = (-40).dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = WhiteSoft,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = "Nada Dering Alarm",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = WhiteSoft
            )
            Text(
                text = "Pilih nada dering favorit untuk alarm kamu!",
                style = MaterialTheme.typography.bodyMedium,
                color = WhiteSoft.copy(alpha = 0.7f)
            )
        }
    }
}
