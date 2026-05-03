package com.example.jadwalkuliah.ui.screen.pengaturan

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jadwalkuliah.R
import com.example.jadwalkuliah.ui.component.SimpleHeader
import com.example.jadwalkuliah.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            SimpleHeader(title = "Tentang Aplikasi", onBack = onNavigateBack)
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .border(4.dp, GoldAccent, CircleShape)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder),
                            contentDescription = "Muhammad Adi",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "Muhammad Adi",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = WhiteSoft
                    )
                    Text(
                        text = "(Ady Vandorez)",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldAccent
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Designer & Programmer",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSoftSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Description Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Aplikasi ini dirancang sebagai solusi sederhana namun efektif untuk membantu pengguna dalam mengatur aktivitas harian, khususnya dalam konteks perkuliahan dan produktivitas pribadi. Dengan tampilan yang bersih dan terstruktur, aplikasi ini menggabungkan berbagai kebutuhan penting seperti pengelolaan jadwal, pencatatan tugas, pengingat, serta catatan dalam satu tempat yang terintegrasi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSoftSecondary,
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Fokus utama dari aplikasi ini adalah memberikan pengalaman penggunaan yang nyaman, cepat, dan tidak membingungkan. Setiap fitur dirancang dengan pendekatan minimalis, sehingga pengguna dapat langsung memahami cara penggunaannya tanpa perlu proses adaptasi yang rumit. Mulai dari pengelolaan jadwal kuliah, pencatatan tugas dengan deadline, hingga sistem pengingat yang terstruktur, semuanya dibuat untuk membantu pengguna tetap terorganisir dalam menjalani aktivitas sehari-hari.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSoftSecondary,
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Selain dari sisi fungsionalitas, aplikasi ini juga menekankan pada konsistensi visual dan kenyamanan antarmuka. Pemilihan warna, tata letak, serta interaksi antar elemen dirancang agar terasa halus dan tidak mengganggu fokus pengguna. Setiap detail diperhatikan untuk menciptakan pengalaman yang lebih profesional dan menyenangkan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSoftSecondary,
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Seiring dengan perkembangan kebutuhan pengguna, aplikasi ini dirancang agar fleksibel dan dapat terus dikembangkan. Aplikasi ini tidak hanya ditujukan sebagai alat bantu sementara, tetapi juga sebagai sistem yang dapat digunakan dalam jangka panjang untuk menunjang produktivitas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSoftSecondary,
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Di balik pengembangan aplikasi ini, terdapat proses belajar dan eksplorasi yang tidak singkat. Aplikasi ini dibangun sebagai bagian dari perjalanan dalam memahami bagaimana sebuah sistem tidak hanya bekerja secara fungsional, tetapi juga mampu memberikan pengalaman yang nyaman bagi penggunanya.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSoftSecondary,
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pengembangan aplikasi ini menjadi titik pertemuan antara dua hal yang berbeda namun saling melengkapi, yaitu desain visual dan pemrograman. Pendekatan ini memungkinkan aplikasi tidak hanya berfungsi dengan baik, tetapi juga memiliki tampilan yang terarah dan konsisten.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSoftSecondary,
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Aplikasi ini dikembangkan oleh Muhammad Adi, yang dikenal sebagai Ady Vandorez, seorang desainer grafis sekaligus vector portrait artist asal Bondowoso, Jawa Timur. Selain aktif di dunia desain, Ady juga mendalami bidang pemrograman sebagai mahasiswa Informatika di Universitas Nurul Jadid, Paiton.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSoftSecondary,
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Dengan menggabungkan kemampuan desain dan logika pemrograman, Ady berupaya menciptakan aplikasi yang tidak hanya berguna, tetapi juga memiliki identitas visual yang kuat. Melalui aplikasi ini, ia mencoba menghadirkan solusi yang relevan sekaligus menjadi representasi dari proses belajar, konsistensi, dan perkembangan yang ia jalani.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSoftSecondary,
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Media Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Terhubung dengan Saya",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = WhiteSoft
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SocialIcon(
                            name = "Instagram",
                            icon = Icons.Default.Language,
                            onClick = { openUrl(context, "https://instagram.com/ady.vandorez") }
                        )
                        SocialIcon(
                            name = "GitHub",
                            icon = Icons.Default.Code, 
                            onClick = { openUrl(context, "https://github.com/adyvandorez") }
                        )
                        SocialIcon(
                            name = "Linktree",
                            icon = Icons.Default.Link,
                            onClick = { openUrl(context, "https://linktr.ee/Ady_vandorez") }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Versi 1.2.0 (Stable)",
                style = MaterialTheme.typography.labelSmall,
                color = TextSoftSecondary.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SocialIcon(
    name: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = GoldAccent,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = TextSoftSecondary
        )
    }
}

fun openUrl(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}
