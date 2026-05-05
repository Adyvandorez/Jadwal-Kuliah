package com.example.jadwalkuliah.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jadwalkuliah.ui.theme.*

@Composable
fun SimpleHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPurpleTheme = MaterialTheme.colorScheme.primary == PurplePrimary
    val isPinkTheme = MaterialTheme.colorScheme.primary == PinkPrimary
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = when {
                    isPurpleTheme -> PurpleTextPrimary
                    isPinkTheme -> PinkPrimary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = when {
                isPurpleTheme -> PurpleTextPrimary
                isPinkTheme -> PinkPrimary
                else -> MaterialTheme.colorScheme.primary
            }
        )
    }
}
