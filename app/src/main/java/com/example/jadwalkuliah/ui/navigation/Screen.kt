package com.example.jadwalkuliah.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Beranda : Screen("beranda", "Home", Icons.Default.Home)
    object Jadwal : Screen("jadwal", "Jadwal", Icons.Default.Schedule)
    object Tugas : Screen("tugas", "Tugas", Icons.Default.Assignment)
    object Catatan : Screen("catatan", "Catatan", Icons.Default.Description)
    object Ide : Screen("ide", "Ide", Icons.Default.EmojiObjects)
    object Pengaturan : Screen("pengaturan", "Setting", Icons.Default.Settings)
    object Pengingat : Screen("pengingat", "Pengingat", Icons.Default.Notifications)
    object DetailPengingat : Screen("detail_pengingat/{pengingatId}", "Detail Pengingat", Icons.Default.Notifications) {
        fun createRoute(pengingatId: Int) = "detail_pengingat/$pengingatId"
    }
    object AddPengingat : Screen("add_pengingat", "Tambah Pengingat", Icons.Default.Notifications)
    object EditPengingat : Screen("edit_pengingat/{pengingatId}", "Edit Pengingat", Icons.Default.Notifications) {
        fun createRoute(pengingatId: Int) = "edit_pengingat/$pengingatId"
    }
}
