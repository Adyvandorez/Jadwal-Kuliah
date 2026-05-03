package com.example.jadwalkuliah.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.jadwalkuliah.data.local.AlarmPreferences
import com.example.jadwalkuliah.ui.screen.pengaturan.*
import com.example.jadwalkuliah.ui.screen.pengingat.*
import com.example.jadwalkuliah.ui.theme.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.tooling.preview.Preview
import com.example.jadwalkuliah.ui.theme.JadwalKuliahTheme
import androidx.compose.foundation.Canvas

@Composable
fun MainScreen(themePreferences: ThemePreferences) {

    val navController = rememberNavController()
    val context = LocalContext.current

    val database = remember { AppDatabase.getDatabase(context) }
    val userProfilePreferences = remember { UserProfilePreferences(context) }
    val alarmPreferences = remember { AlarmPreferences(context) }
    
    val jadwalRepository = remember { JadwalRepository(database.jadwalDao()) }
    val tugasRepository = remember { TugasRepository(database.tugasDao()) }
    val pengingatRepository = remember { PengingatRepository(database.pengingatDao()) }
    val userProfileRepository = remember { UserProfileRepository(userProfilePreferences) }

    val screens = listOf(
        Screen.Beranda,
        Screen.Jadwal,
        Screen.Tugas,
        Screen.Pengaturan
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            val isMainScreen = screens.any { it.route == currentRoute } || currentRoute == null || currentRoute == Screen.Pengingat.route
            val isFabVisible = isMainScreen && currentRoute != Screen.Pengaturan.route

            if (isMainScreen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val cutoutProgress = if (isFabVisible) 1f else 0f

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val navHeight = 80.dp.toPx()
                        val topY = 0f // Menghilangkan gap pembatas dengan merapatkan ke atas
                        val cornerRadius = 35.dp.toPx()
                        
                        val cutoutRadius = 46.dp.toPx() * cutoutProgress
                        val curveWidth = 90.dp.toPx() * cutoutProgress

                        val path = Path().apply {
                            moveTo(0f, height)
                            lineTo(0f, topY)
                            
                            if (cutoutProgress > 0.01f) {
                                val centerX = width / 2
                                lineTo(centerX - curveWidth, topY)
                                
                                cubicTo(
                                    x1 = centerX - cutoutRadius * 0.7f, y1 = topY,
                                    x2 = centerX - cutoutRadius * 0.8f, y2 = topY + cutoutRadius * 0.8f,
                                    x3 = centerX, y3 = topY + cutoutRadius * 0.8f
                                )
                                cubicTo(
                                    x1 = centerX + cutoutRadius * 0.8f, y1 = topY + cutoutRadius * 0.8f,
                                    x2 = centerX + cutoutRadius * 0.7f, y2 = topY,
                                    x3 = centerX + curveWidth, topY
                                )
                            }
                            
                            lineTo(width, topY)
                            lineTo(width, height)
                            close()
                        }

                        drawPath(path = path, color = DarkSurface)

                        // Top Outline following the curve
                        val outlinePath = Path().apply {
                            moveTo(0f, topY)
                            
                            if (cutoutProgress > 0.01f) {
                                val centerX = width / 2
                                lineTo(centerX - curveWidth, topY)
                                
                                cubicTo(
                                    x1 = centerX - cutoutRadius * 0.7f, y1 = topY,
                                    x2 = centerX - cutoutRadius * 0.8f, y2 = topY + cutoutRadius * 0.8f,
                                    x3 = centerX, y3 = topY + cutoutRadius * 0.8f
                                )
                                cubicTo(
                                    x1 = centerX + cutoutRadius * 0.8f, y1 = topY + cutoutRadius * 0.8f,
                                    x2 = centerX + cutoutRadius * 0.7f, y2 = topY,
                                    x3 = centerX + curveWidth, topY
                                )
                            }
                            
                            lineTo(width, topY)
                        }

                        drawPath(
                            path = outlinePath,
                            color = DarkOutline,
                            style = Stroke(width = 2.dp.toPx())
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
                        screens.forEachIndexed { index, screen ->
                            if (index == 2) {
                                Spacer(modifier = Modifier.weight(0.5f))
                            }
                            
                            val isSelected = currentRoute == screen.route || (currentRoute == null && index == 0)
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(26.dp)
                                    )
                                },
                                label = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = screen.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 11.sp
                                            )
                                        )
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 4.dp)
                                                    .width(14.dp)
                                                    .height(3.dp)
                                                    .background(Color(0xFFB8A899), RoundedCornerShape(1.5.dp))
                                            )
                                        }
                                    }
                                },
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = false // Reset state when switching between main tabs
                                            }
                                            launchSingleTop = true
                                            restoreState = false // Do not restore previous scroll/state
                                        }
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFB8A899),
                                    unselectedIconColor = GoldSoft,
                                    selectedTextColor = Color(0xFFB8A899),
                                    unselectedTextColor = GoldSoft,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            val isMainScreen = screens.any { it.route == currentRoute } || currentRoute == null || currentRoute == Screen.Pengingat.route
            val isFabVisible = isMainScreen && currentRoute != Screen.Pengaturan.route

            if (isFabVisible) {
                Box(
                    modifier = Modifier
                        .offset(y = 64.dp)
                        .size(64.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = CircleShape,
                            spotColor = Color.Black
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF966C4B),
                                    Color(0xFF4B3425)
                                )
                            )
                        )
                        .clickable { 
                            when {
                                currentRoute == Screen.Beranda.route || currentRoute == Screen.Jadwal.route || currentRoute == null -> 
                                    navController.navigate("add_jadwal")
                                currentRoute == Screen.Tugas.route -> 
                                    navController.navigate("add_tugas")
                                currentRoute == Screen.Pengingat.route -> 
                                    navController.navigate("add_pengingat")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Tambah",
                        tint = Color(0xFFB8A899),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Beranda.route,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                composable(Screen.Beranda.route) {
                    val viewModel: BerandaViewModel = viewModel(
                        factory = BerandaViewModelFactory(jadwalRepository, tugasRepository, userProfileRepository)
                    )
                    BerandaScreen(
                        viewModel = viewModel,
                        onNavigateToPengingat = { 
                            navController.navigate(Screen.Pengingat.route)
                        },
                        onNavigateToDetailJadwal = { id ->
                            navController.navigate("detail_jadwal/$id")
                        },
                        onNavigateToDetailTugas = { id ->
                            navController.navigate("detail_tugas/$id")
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
                composable(Screen.Pengaturan.route) {
                    PengaturanScreen(
                        themePreferences = themePreferences,
                        onNavigateToEditProfil = {
                            navController.navigate("edit_profil")
                        },
                        onNavigateToNadaDering = {
                            navController.navigate(Screen.NadaDering.route)
                        },
                        onNavigateToAbout = {
                            navController.navigate(Screen.About.route)
                        }
                    )
                }

                composable(Screen.About.route) {
                    AboutScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.NadaDering.route) {
                    NadaDeringScreen(
                        alarmPreferences = alarmPreferences,
                        onNavigateBack = { navController.popBackStack() }
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

@Preview(showBackground = true, backgroundColor = 0xFF0F0E0C)
@Composable
fun MainScreenPreview() {
    JadwalKuliahTheme(darkTheme = true) {
        val context = LocalContext.current
        MainScreen(themePreferences = ThemePreferences(context))
    }
}
