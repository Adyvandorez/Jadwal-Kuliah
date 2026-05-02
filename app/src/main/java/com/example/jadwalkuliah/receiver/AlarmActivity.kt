package com.example.jadwalkuliah.receiver

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jadwalkuliah.ui.theme.DarkBackground
import com.example.jadwalkuliah.ui.theme.GoldSoft
import com.example.jadwalkuliah.ui.theme.WhiteSoft
import java.text.SimpleDateFormat
import java.util.*

class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Konfigurasi agar muncul di atas lockscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val judul = intent.getStringExtra("JUDUL") ?: "Alarm"

        setContent {
            AlarmScreen(
                judul = judul,
                onStop = {
                    val stopIntent = Intent(this, AlarmService::class.java).apply {
                        action = AlarmService.ACTION_STOP
                    }
                    startService(stopIntent)
                    finish()
                },
                onSnooze = {
                    val snoozeIntent = Intent(this, AlarmService::class.java).apply {
                        action = AlarmService.ACTION_SNOOZE
                    }
                    startService(snoozeIntent)
                    finish()
                }
            )
        }
    }
}

@Composable
fun AlarmScreen(judul: String, onStop: () -> Unit, onSnooze: () -> Unit) {
    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(60.dp))
            Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = null,
                tint = GoldSoft,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = currentTime,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteSoft
            )
            Text(
                text = judul,
                fontSize = 20.sp,
                color = GoldSoft,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Tombol Snooze
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onSnooze,
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color(0xFF3E3A38), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Snooze,
                        contentDescription = "Snooze",
                        tint = GoldSoft,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(text = "Snooze", color = WhiteSoft, modifier = Modifier.padding(top = 8.dp))
            }

            // Tombol Stop
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onStop,
                    modifier = Modifier
                        .size(70.dp)
                        .background(GoldSoft, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Stop",
                        tint = Color(0xFF34312F),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(text = "Matikan", color = WhiteSoft, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
