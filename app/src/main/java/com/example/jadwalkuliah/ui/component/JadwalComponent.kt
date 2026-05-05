package com.example.jadwalkuliah.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.ui.theme.*

@Composable
fun JadwalCard(
    jadwal: JadwalEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isYellowTheme = MaterialTheme.colorScheme.primary == LightPrimary
    val isPurpleTheme = MaterialTheme.colorScheme.primary == PurplePrimary
    val isPinkTheme = MaterialTheme.colorScheme.primary == PinkPrimary
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left section (Time)
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when {
                            isYellowTheme -> YellowCapsuleBg
                            isPurpleTheme -> PurpleBubble
                            isPinkTheme -> PinkSurfaceVariant
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = when {
                            isYellowTheme -> MaterialTheme.colorScheme.onPrimaryContainer 
                            isPurpleTheme -> PurpleTextSecondary
                            isPinkTheme -> PinkPrimary
                            else -> MaterialTheme.colorScheme.tertiary
                        },
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = jadwal.waktuMulai,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when {
                            isYellowTheme -> MaterialTheme.colorScheme.onPrimaryContainer 
                            isPurpleTheme -> PurpleTextSecondary
                            isPinkTheme -> PinkPrimary
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    )
                    Text(
                        text = jadwal.waktuSelesai,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when {
                            isYellowTheme -> MaterialTheme.colorScheme.onPrimaryContainer 
                            isPurpleTheme -> PurpleTextSecondary
                            isPinkTheme -> PinkPrimary
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Right section (Info)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jadwal.namaMatkul,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = jadwal.dosen,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = jadwal.ruangan,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
