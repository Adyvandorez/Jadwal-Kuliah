package com.example.jadwalkuliah.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.jadwalkuliah.data.local.AppDatabase
import com.example.jadwalkuliah.data.local.ThemePreferences
import com.example.jadwalkuliah.data.local.UserProfilePreferences
import com.example.jadwalkuliah.data.repository.JadwalRepository
import com.example.jadwalkuliah.data.repository.PengingatRepository
import com.example.jadwalkuliah.data.repository.TugasRepository
import com.example.jadwalkuliah.data.repository.UserProfileRepository
import com.example.jadwalkuliah.ui.screen.beranda.*
import com.example.jadwalkuliah.ui.screen.jadwal.*
import com.example.jadwalkuliah.ui.screen.tugas.*
import com.example.jadwalkuliah.ui.screen.pengaturan.*
import com.example.jadwalkuliah.ui.screen.pengingat.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

@Composable
fun MainScreen(themePreferences: ThemePreferences) {

    val navController = rememberNavController()
    val context = LocalContext.current

    val database = remember { AppDatabase.getDatabase(context) }
    val userProfilePreferences = remember { UserProfilePreferences(context) }
    
    val jadwalRepository = remember { JadwalRepository(database.jadwalDao()) }
    val tugasRepository = remember { TugasRepository(database.tugasDao()) }
    val pengingatRepository = remember { PengingatRepository(database.pengingatDao()) }
    val userProfileRepository = remember { UserProfileRepository(userProfilePreferences) }

    val screens = listOf(
        Screen.Beranda,
        Screen.Jadwal,
        Screen.Tugas,
        Screen.Catatan,
        Screen.Ide,
        Screen.Pengaturan
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            val isDark = isSystemInDarkTheme()
            val isMainScreen = screens.any { it.route == currentRoute } || currentRoute == null || currentRoute == Screen.Pengingat.route

            if (isDark) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Custom Background with Dynamic Cutout
                    val backgroundColor = MaterialTheme.colorScheme.surface
                    val borderColor = MaterialTheme.colorScheme.outline
                    
                    // No cutout for 6 items to keep it clean
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val cornerRadius = 28.dp.toPx()
                        val navHeight = 80.dp.toPx()
                        val topY = height - navHeight

                        val path = Path().apply {
                            moveTo(0f, height)
                            lineTo(0f, topY + cornerRadius)
                            arcTo(
                                rect = Rect(0f, topY, cornerRadius * 2, topY + cornerRadius * 2),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                            lineTo(width - cornerRadius, topY)
                            arcTo(
                                rect = Rect(width - cornerRadius * 2, topY, width, topY + cornerRadius * 2),
                                startAngleDegrees = 270f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                            lineTo(width, height)
                            close()
                        }

                        drawPath(path = path, color = backgroundColor)
                        drawPath(
                            path = path,
                            color = borderColor,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .height(80.dp)
                            .align(Alignment.BottomCenter),
                        windowInsets = WindowInsets(0)
                    ) {
                        screens.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                                    selectedTextColor = MaterialTheme.colorScheme.tertiary,
                                    indicatorColor = Color.Transparent,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }
            } else {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = Color.Transparent,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            val isDark = isSystemInDarkTheme()
            val isMainScreen = screens.any { it.route == currentRoute } || currentRoute == null || currentRoute == Screen.Pengingat.route
            val currentIndex = screens.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

            // FAB shown on Beranda, Jadwal, Tugas, Catatan, Ide, and Pengingat
            if (isMainScreen && (currentIndex < 5 || currentRoute == Screen.Pengingat.route)) {
                val onFabClickAction = {
                    try {
                        when (currentRoute) {
                            Screen.Beranda.route, Screen.Jadwal.route -> 
                                navController.navigate("add_jadwal")
                            Screen.Tugas.route -> 
                                navController.navigate("add_tugas?type=Tugas")
                            Screen.Catatan.route -> 
                                navController.navigate("add_tugas?type=Catatan")
                            Screen.Ide.route -> 
                                navController.navigate("add_tugas?type=Ide")
                            Screen.Pengingat.route -> 
                                navController.navigate("add_pengingat")
                        }
                    } catch (_: Exception) {}
                }

                if (isDark) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = 70f.dp.toPx() // Geser lebih bawah lagi agar tidak menutupi menu
                            }
                            .size(64.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            .clip(CircleShape) // Pastikan klik hanya di area lingkaran
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.tertiary,
                                        MaterialTheme.colorScheme.primary
                                    )
                                )
                            )
                            .clickable { onFabClickAction() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    FloatingActionButton(
                        onClick = { onFabClickAction() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape,
                        modifier = Modifier.graphicsLayer {
                            translationY = 70f.dp.toPx()
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah")
                    }
                }
            }
        }
    ) { innerPadding ->
        val isMainScreen = screens.any { it.route == currentRoute } || currentRoute == null || currentRoute == "placeholder"

        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.Beranda.route,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                composable(Screen.Beranda.route) {
                    val viewModel: BerandaViewModel = viewModel(
                        factory = BerandaViewModelFactory(jadwalRepository, tugasRepository, userProfileRepository)
                    )
                    BerandaScreen(
                        viewModel = viewModel,
                        onNavigateToPengingat = { 
                            try {
                                navController.navigate(Screen.Pengingat.route)
                            } catch (_: Exception) {
                                // Handle cases where graph is not yet set
                            }
                        }
                    )
                }
                composable(Screen.Jadwal.route) {
                    val viewModel: JadwalViewModel = viewModel(
                        factory = JadwalViewModelFactory(jadwalRepository)
                    )
                    JadwalScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { id ->
                            navController.navigate("detail_jadwal/$id")
                        }
                    )
                }
                composable(Screen.Tugas.route) {
                    val viewModel: TugasViewModel = viewModel(
                        factory = TugasViewModelFactory(tugasRepository)
                    )
                    TugasScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { tugasId ->
                            navController.navigate("detail_tugas/$tugasId")
                        }
                    )
                }
                composable(Screen.Catatan.route) {
                    val viewModel: TugasViewModel = viewModel(
                        factory = TugasViewModelFactory(tugasRepository)
                    )
                    CatatanScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { id ->
                            navController.navigate("detail_tugas/$id")
                        }
                    )
                }
                composable(Screen.Ide.route) {
                    val viewModel: TugasViewModel = viewModel(
                        factory = TugasViewModelFactory(tugasRepository)
                    )
                    IdeScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { id ->
                            navController.navigate("detail_tugas/$id")
                        }
                    )
                }
                composable(Screen.Pengaturan.route) {
                    PengaturanScreen(
                        themePreferences = themePreferences,
                        onNavigateToEditProfil = {
                            navController.navigate("edit_profil")
                        }
                    )
                }

                composable(Screen.Pengingat.route) {
                    val viewModel: PengingatViewModel = viewModel(
                        factory = PengingatViewModelFactory(context.applicationContext as android.app.Application, pengingatRepository)
                    )
                    PengingatScreen(
                        viewModel = viewModel,
                        navController = navController,
                        onBackClick = { navController.popBackStack() },
                        onNavigateToDetail = { id ->
                            navController.navigate(Screen.DetailPengingat.createRoute(id))
                        }
                    )
                }

                composable(
                    Screen.DetailPengingat.route,
                    arguments = listOf(navArgument("pengingatId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("pengingatId") ?: return@composable
                    val viewModel: PengingatViewModel = viewModel(
                        factory = PengingatViewModelFactory(context.applicationContext as android.app.Application, pengingatRepository)
                    )
                    DetailPengingatScreen(
                        pengingatId = id,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onEditNavigate = { editId ->
                            navController.navigate(Screen.EditPengingat.createRoute(editId))
                        }
                    )
                }

                composable("add_pengingat") {
                    val viewModel: PengingatViewModel = viewModel(
                        factory = PengingatViewModelFactory(context.applicationContext as android.app.Application, pengingatRepository)
                    )
                    AddEditPengingatScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    Screen.EditPengingat.route,
                    arguments = listOf(navArgument("pengingatId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("pengingatId") ?: return@composable
                    val viewModel: PengingatViewModel = viewModel(
                        factory = PengingatViewModelFactory(context.applicationContext as android.app.Application, pengingatRepository)
                    )
                    AddEditPengingatScreen(
                        viewModel = viewModel,
                        pengingatId = id,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("edit_profil") {
                    EditProfilScreen(
                        userProfileRepository = userProfileRepository,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    "detail_jadwal/{jadwalId}",
                    arguments = listOf(navArgument("jadwalId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("jadwalId") ?: return@composable
                    val viewModel: JadwalViewModel = viewModel(
                        factory = JadwalViewModelFactory(jadwalRepository)
                    )
                    DetailJadwalScreen(
                        jadwalId = id,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onEditNavigate = { editId ->
                            navController.navigate("edit_jadwal/$editId")
                        }
                    )
                }

                composable(
                    "detail_tugas/{tugasId}",
                    arguments = listOf(navArgument("tugasId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val tugasId = backStackEntry.arguments?.getInt("tugasId") ?: return@composable
                    val viewModel: TugasViewModel = viewModel(
                        factory = TugasViewModelFactory(tugasRepository)
                    )
                    DetailTugasScreen(
                        viewModel = viewModel,
                        tugasId = tugasId,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToEdit = { id ->
                            navController.navigate("edit_tugas/$id")
                        }
                    )
                }

                composable("add_jadwal") {
                    val viewModel: JadwalViewModel = viewModel(
                        factory = JadwalViewModelFactory(jadwalRepository)
                    )
                    AddEditJadwalScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    "edit_jadwal/{jadwalId}",
                    arguments = listOf(navArgument("jadwalId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("jadwalId") ?: return@composable
                    val viewModel: JadwalViewModel = viewModel(
                        factory = JadwalViewModelFactory(jadwalRepository)
                    )
                    AddEditJadwalScreen(
                        viewModel = viewModel,
                        jadwalId = id,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    "edit_tugas/{tugasId}",
                    arguments = listOf(navArgument("tugasId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val tugasId = backStackEntry.arguments?.getInt("tugasId") ?: return@composable
                    val viewModel: TugasViewModel = viewModel(
                        factory = TugasViewModelFactory(tugasRepository)
                    )
                    AddEditTugasScreen(
                        viewModel = viewModel,
                        tugasId = tugasId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    "add_tugas?type={type}",
                    arguments = listOf(navArgument("type") { 
                        type = NavType.StringType
                        defaultValue = "Tugas"
                    })
                ) { backStackEntry ->
                    val type = backStackEntry.arguments?.getString("type") ?: "Tugas"
                    val viewModel: TugasViewModel = viewModel(
                        factory = TugasViewModelFactory(tugasRepository)
                    )
                    AddEditTugasScreen(
                        viewModel = viewModel,
                        initialType = type,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("add_tugas") {
                    val viewModel: TugasViewModel = viewModel(
                        factory = TugasViewModelFactory(tugasRepository)
                    )
                    AddEditTugasScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
