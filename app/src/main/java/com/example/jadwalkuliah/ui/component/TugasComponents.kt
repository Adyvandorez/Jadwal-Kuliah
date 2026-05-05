package com.example.jadwalkuliah.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.ui.theme.*
import com.example.jadwalkuliah.util.DateTimeUtils
import java.io.File

@Composable
fun TugasCard(
    tugas: TugasEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleCompletion: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    isReadOnly: Boolean = false
) {
    val countdown = DateTimeUtils.getCountdown(tugas.deadline)
    val isOverdue = DateTimeUtils.isOverdue(tugas.deadline) && !tugas.isCompleted

    val isYellowTheme = MaterialTheme.colorScheme.primary == LightPrimary
    val isPurpleTheme = MaterialTheme.colorScheme.primary == PurplePrimary
    val isPinkTheme = MaterialTheme.colorScheme.primary == PinkPrimary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!isReadOnly) {
                        IconButton(
                            onClick = { onToggleCompletion?.invoke() },
                            enabled = onToggleCompletion != null,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (tugas.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Status",
                                tint = if (tugas.isCompleted) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    
                    Text(
                        text = tugas.judul,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (tugas.isCompleted && !isReadOnly) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (!isReadOnly && onDelete != null) {
                        Surface(
                            onClick = onDelete,
                            color = PdfRed.copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = PdfRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                CategoryBadge(
                    text = if (tugas.kategori.isNotEmpty()) tugas.kategori else "Tugas",
                    containerColor = when {
                        isYellowTheme -> YellowCapsuleBg
                        isPurpleTheme -> PurpleBubble
                        isPinkTheme -> PinkSurfaceVariant
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    },
                    contentColor = when {
                        isYellowTheme -> MaterialTheme.colorScheme.onPrimaryContainer
                        isPurpleTheme -> PurpleTextSecondary
                        isPinkTheme -> PinkPrimary
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (tugas.deskripsi.isNotEmpty()) {
                    Text(
                        text = tugas.deskripsi,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (tugas.lampiran.isNotEmpty()) {
                    AttachmentRow(
                        lampiran = tugas.lampiran,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DateDisplay(date = tugas.deadline)

                    if (countdown.isNotEmpty()) {
                        Text(
                            text = countdown,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isOverdue) DeleteRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!isReadOnly && tugas.isCompleted) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Selesai",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentRow(
    lampiran: List<String>,
    modifier: Modifier = Modifier,
    onAttachmentClick: ((String) -> Unit)? = null
) {
    val displayList = lampiran.take(4)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        displayList.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { path ->
                    AttachmentPill(
                        path = path,
                        modifier = Modifier.weight(1f),
                        onClick = if (onAttachmentClick != null) { { onAttachmentClick(path) } } else null
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun AttachmentPill(
    path: String, 
    modifier: Modifier = Modifier, 
    onClick: (() -> Unit)? = null
) {
    val fileName = path.substringAfterLast("/")
    val extension = fileName.substringAfterLast(".", "").lowercase()
    
    val (icon, iconColor) = when (extension) {
        "pdf" -> Icons.Outlined.Description to PdfRed
        "png", "jpg", "jpeg" -> Icons.Outlined.Image to ImageGold
        else -> Icons.AutoMirrored.Filled.InsertDriveFile to MaterialTheme.colorScheme.onSurfaceVariant
    }

    val isPurpleTheme = MaterialTheme.colorScheme.primary == PurplePrimary

    val surfaceModifier = if (onClick != null) modifier.clickable { onClick() } else modifier

    Surface(
        modifier = surfaceModifier,
        color = if (isPurpleTheme) PurpleBubble else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = fileName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}

@Composable
fun DetailAttachmentCard(
    path: String,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val fileName = path.substringAfterLast("/")
    val extension = fileName.substringAfterLast(".", "").lowercase()
    val file = File(path)
    val fileSize = if (file.exists()) {
        val size = file.length() / 1024
        if (size > 1024) String.format("%.1f MB", size / 1024.0) else "$size kB"
    } else "652 kB" // Fallback based on design if file not found locally yet

    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onOpen() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Triple dot menu icon (Top Left)
                Box(modifier = Modifier.align(Alignment.TopStart)) {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Share", color = MaterialTheme.colorScheme.onSurface) },
                            onClick = { showMenu = false; onShare() }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus", color = DeleteRed) },
                            onClick = { showMenu = false; onDelete() }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Side: Icon and Size info
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(90.dp).padding(top = 10.dp)
                    ) {
                        val color = when(extension) {
                            "pdf" -> PdfRed
                            "jpg", "jpeg", "png" -> ImageGold
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(50.dp)
                                .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (extension == "pdf") Icons.Outlined.Description else Icons.Outlined.Image,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = extension.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, fontWeight = FontWeight.Black),
                                    color = color
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "${extension.uppercase()}.$fileSize",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            fontSize = 11.sp
                        )
                    }

                    // Right Side: Preview Image
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = file,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Bottom gradient on preview to make file name strip look integrated
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                                    )
                                )
                        )
                    }
                }
            }
            
            // File Name Strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CategoryBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = contentColor
        )
    }
}

@Composable
fun DateDisplay(
    date: Long?,
    modifier: Modifier = Modifier
) {
    if (date == null) return
    
    val isYellowTheme = MaterialTheme.colorScheme.primary == LightPrimary
    val isPurpleTheme = MaterialTheme.colorScheme.primary == PurplePrimary
    val isPinkTheme = MaterialTheme.colorScheme.primary == PinkPrimary
    
    Surface(
        color = when {
            isYellowTheme -> YellowCapsuleBg
            isPurpleTheme -> PurpleBubble
            isPinkTheme -> PinkSurfaceVariant
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = when {
                    isYellowTheme -> MaterialTheme.colorScheme.onPrimaryContainer
                    isPurpleTheme -> PurpleTextSecondary
                    isPinkTheme -> PinkPrimary
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = DateTimeUtils.formatDateOnly(date),
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    isYellowTheme -> MaterialTheme.colorScheme.onPrimaryContainer
                    isPurpleTheme -> PurpleTextSecondary
                    isPinkTheme -> PinkPrimary
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                }
            )
        }
    }
}
