@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package dev.serein.prototype.ui

import android.animation.ValueAnimator
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Brightness6
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SignalWifi4Bar
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material.icons.rounded.StopScreenShare
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private enum class Screen {
    Welcome, Lock, Home, Drawer, Notifications, Controls, Recents, Settings, Privacy, Demo
}

private data class DemoApp(
    val name: String,
    val icon: ImageVector,
    val tint: Color,
    val description: String,
)

private data class DemoNotification(val id: Int, val title: String, val body: String, val group: String)

private data class HomeFolder(val name: String, val apps: List<DemoApp>)

private val demoApps = listOf(
    DemoApp("Phone", Icons.Rounded.Phone, Color(0xFF4A9A7B), "A call interface preview. Calls are not placed."),
    DemoApp("Messages", Icons.Rounded.Sms, Color(0xFF5C89C7), "A messaging interface preview. Messages are not sent."),
    DemoApp("Camera", Icons.Rounded.CameraAlt, Color(0xFFB67655), "Camera preview is not connected to camera hardware."),
    DemoApp("Photos", Icons.Rounded.PhotoLibrary, Color(0xFF9D78C3), "A local gallery interface preview with no photo access."),
    DemoApp("Files", Icons.Rounded.Folder, Color(0xFFC7974F), "File browsing is not connected to device storage."),
    DemoApp("Clock", Icons.Rounded.Brightness6, Color(0xFF547C87), "A clock and alarms interface preview."),
    DemoApp("Calendar", Icons.Rounded.CalendarMonth, Color(0xFFBB6470), "A calendar preview with sample events."),
    DemoApp("Browser", Icons.Rounded.Public, Color(0xFF568C8A), "A browser interface preview. No web pages are loaded."),
    DemoApp("Settings", Icons.Rounded.Settings, Color(0xFF7B8794), "Prototype appearance and accessibility preferences."),
    DemoApp("Security", Icons.Rounded.Shield, Color(0xFF538F88), "Honest device-security status and prototype disclosures."),
)

private val accents = listOf(
    Color(0xFF387E79),
    Color(0xFF536FA5),
    Color(0xFF9B648D),
    Color(0xFFB47747),
)

@Composable
private fun rememberSystemReduceMotion(): Boolean {
    val context = LocalContext.current
    val reduceMotion = remember(context) {
        mutableStateOf(!ValueAnimator.areAnimatorsEnabled())
    }

    DisposableEffect(context) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                reduceMotion.value = !ValueAnimator.areAnimatorsEnabled()
            }
        }
        val resolver = context.contentResolver
        resolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
            false,
            observer,
        )
        onDispose { resolver.unregisterContentObserver(observer) }
    }

    return reduceMotion.value
}

@Composable
fun PhonePrototypeApp(onRequestBiometric: ((() -> Unit) -> Unit)) {
    var screen by remember { mutableStateOf(Screen.Welcome) }
    var priorScreen by remember { mutableStateOf(Screen.Home) }
    var darkMode by remember { mutableStateOf(false) }
    var reduceMotion by remember { mutableStateOf(false) }
    var reduceTransparency by remember { mutableStateOf(false) }
    var largerText by remember { mutableStateOf(false) }
    var highContrast by remember { mutableStateOf(false) }
    var accentIndex by remember { mutableIntStateOf(0) }
    var homePage by remember { mutableIntStateOf(0) }
    var appSearch by remember { mutableStateOf("") }
    var settingsSearch by remember { mutableStateOf("") }
    var brightness by remember { mutableFloatStateOf(0.72f) }
    var volume by remember { mutableFloatStateOf(0.55f) }
    var openedApp by remember { mutableStateOf(demoApps.first()) }
    var editControls by remember { mutableStateOf(false) }
    var expandedNotification by remember { mutableIntStateOf(-1) }
    val notificationItems = remember {
        mutableStateListOf(
            DemoNotification(1, "A calmer start", "Take a breath and choose one thing to focus on.", "Today"),
            DemoNotification(2, "Design notes", "Your sample workspace is ready to explore.", "Today"),
            DemoNotification(3, "Quiet hours", "Do not disturb is off. This is a demo notification.", "System"),
        )
    }
    val controlTiles = remember {
        mutableStateListOf("Wi-Fi", "Bluetooth", "Airplane", "Do not disturb", "Flashlight", "Battery saver")
    }
    val controlStates = remember {
        mutableStateListOf(true, false, false, false, false, false)
    }
    val visitedApps = remember { mutableStateListOf<String>() }

    SereinTheme(darkTheme = darkMode, accent = accents[accentIndex], highContrast = highContrast) {
        val systemReduceMotion = rememberSystemReduceMotion()
        val animationsReduced = reduceMotion || systemReduceMotion
        val backgroundColors = if (darkMode) {
            listOf(Color(0xFF17272D), Color(0xFF19202B), Color(0xFF272033))
        } else {
            listOf(Color(0xFFD7E9E4), Color(0xFFF0EAE0), Color(0xFFE4DEED))
        }
        val transparency = if (reduceTransparency) 0.97f else if (darkMode) 0.78f else 0.69f
        BackHandler(enabled = screen != Screen.Home && screen != Screen.Welcome) {
            screen = when (screen) {
                Screen.Drawer, Screen.Controls, Screen.Notifications, Screen.Recents -> Screen.Home
                Screen.Privacy -> Screen.Settings
                Screen.Demo -> priorScreen
                else -> Screen.Home
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(backgroundColors))
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp),
        ) {
            AmbientOrbs()
            AnimatedContent(
                targetState = screen,
                transitionSpec = {
                    if (animationsReduced) {
                        fadeIn() togetherWith fadeOut()
                    } else {
                        fadeIn(animationSpec = spring(stiffness = 420f)) togetherWith
                            fadeOut(animationSpec = spring(stiffness = 420f))
                    }
                },
                label = "serein-screen",
            ) { target ->
                when (target) {
                    Screen.Welcome -> WelcomeScreen(
                        darkMode = darkMode,
                        onTheme = { darkMode = it },
                        onContinue = { screen = Screen.Lock },
                    )
                    Screen.Lock -> LockScreen(
                        darkMode = darkMode,
                        notifications = notificationItems,
                        onSwipeUp = { screen = Screen.Home },
                        onBiometric = { onRequestBiometric { screen = Screen.Home } },
                    )
                    Screen.Home -> HomeScreen(
                        darkMode = darkMode,
                        accent = accents[accentIndex],
                        page = homePage,
                        apps = demoApps,
                        notifications = notificationItems.size,
                        largerText = largerText,
                        transparency = transparency,
                        reduceMotion = animationsReduced,
                        onPage = { homePage = (homePage + it).coerceIn(0, 1) },
                        onLaunch = { app ->
                            openedApp = app
                            priorScreen = Screen.Home
                            if (visitedApps.none { it == app.name }) visitedApps.add(0, app.name)
                            screen = if (app.name == "Settings") Screen.Settings
                            else if (app.name == "Security") Screen.Privacy else Screen.Demo
                        },
                        onDrawer = { screen = Screen.Drawer },
                        onControls = { screen = Screen.Controls },
                        onNotifications = { screen = Screen.Notifications },
                        onRecents = { screen = Screen.Recents },
                        onSettings = { screen = Screen.Settings },
                        onLock = { screen = Screen.Lock },
                    )
                    Screen.Drawer -> DrawerScreen(
                        search = appSearch,
                        apps = demoApps,
                        largerText = largerText,
                        transparency = transparency,
                        onSearch = { appSearch = it },
                        onBack = { screen = Screen.Home },
                        onLaunch = { app ->
                            openedApp = app
                            priorScreen = Screen.Drawer
                            if (visitedApps.none { it == app.name }) visitedApps.add(0, app.name)
                            screen = if (app.name == "Settings") Screen.Settings
                            else if (app.name == "Security") Screen.Privacy else Screen.Demo
                        },
                    )
                    Screen.Notifications -> NotificationScreen(
                        items = notificationItems,
                        expanded = expandedNotification,
                        transparency = transparency,
                        onExpand = { expandedNotification = if (expandedNotification == it) -1 else it },
                        onDismiss = { id -> notificationItems.removeAll { it.id == id } },
                        onClear = { notificationItems.clear() },
                        onBack = { screen = Screen.Home },
                    )
                    Screen.Controls -> ControlScreen(
                        tiles = controlTiles,
                        states = controlStates,
                        brightness = brightness,
                        volume = volume,
                        edit = editControls,
                        transparency = transparency,
                        onToggle = { index -> controlStates[index] = !controlStates[index] },
                        onBrightness = { brightness = it },
                        onVolume = { volume = it },
                        onEdit = { editControls = !editControls },
                        onMoveTile = { index ->
                            if (index < controlTiles.lastIndex) {
                                val label = controlTiles.removeAt(index)
                                val enabled = controlStates.removeAt(index)
                                controlTiles.add(index + 1, label)
                                controlStates.add(index + 1, enabled)
                            }
                        },
                        onBack = { screen = Screen.Home },
                    )
                    Screen.Recents -> RecentsScreen(
                        apps = visitedApps.toList(),
                        transparency = transparency,
                        onClose = { name -> visitedApps.remove(name) },
                        onClear = { visitedApps.clear(); screen = Screen.Home },
                        onBack = { screen = Screen.Home },
                    )
                    Screen.Settings -> SettingsScreen(
                        search = settingsSearch,
                        darkMode = darkMode,
                        accentIndex = accentIndex,
                        accents = accents,
                        largerText = largerText,
                        highContrast = highContrast,
                        reduceMotion = reduceMotion,
                        reduceTransparency = reduceTransparency,
                        transparency = transparency,
                        onSearch = { settingsSearch = it },
                        onTheme = { darkMode = it },
                        onAccent = { accentIndex = it },
                        onLargerText = { largerText = it },
                        onHighContrast = { highContrast = it },
                        onReduceMotion = { reduceMotion = it },
                        onReduceTransparency = { reduceTransparency = it },
                        onPrivacy = { screen = Screen.Privacy },
                        onBack = { screen = Screen.Home },
                    )
                    Screen.Privacy -> PrivacyScreen(
                        transparent = transparency,
                        onBack = { screen = Screen.Settings },
                    )
                    Screen.Demo -> DemoAppScreen(
                        app = openedApp,
                        transparency = transparency,
                        onBack = { screen = priorScreen },
                    )
                }
            }
        }
    }
}

@Composable
private fun AmbientOrbs() {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = 42.dp, end = 12.dp)
                .size(160.dp)
                .background(Color.White.copy(alpha = 0.11f), CircleShape),
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 78.dp, start = 6.dp)
                .size(132.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f), CircleShape),
        )
    }
}

@Composable
private fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(top = 14.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = { content() },
    )
}

@Composable
private fun WelcomeScreen(darkMode: Boolean, onTheme: (Boolean) -> Unit, onContinue: () -> Unit) {
    ScreenColumn {
        Spacer(Modifier.weight(0.7f))
        Text("SEREIN  /  PREVIEW", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text("A little more\nroom to breathe.", fontSize = 42.sp, lineHeight = 48.sp, fontWeight = FontWeight.SemiBold)
        GlassCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Rounded.Shield, null, tint = MaterialTheme.colorScheme.primary)
                Text("A thoughtful Android interface", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "This is an app prototype, not an operating system. Your device continues to provide its real lock screen, app sandbox, and security updates.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ThemeChoice("Light", !darkMode, Modifier.weight(1f)) { onTheme(false) }
            ThemeChoice("Dark", darkMode, Modifier.weight(1f)) { onTheme(true) }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth().height(54.dp)) {
            Text("Explore the preview")
        }
        Text("No account required  ·  No permissions requested", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ThemeChoice(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(onClick = onClick, modifier = modifier) {
        if (selected) Icon(Icons.Rounded.Check, null, Modifier.size(18.dp))
        Text(label, Modifier.padding(start = 6.dp))
    }
}

@Composable
private fun LockScreen(
    darkMode: Boolean,
    notifications: List<DemoNotification>,
    onSwipeUp: () -> Unit,
    onBiometric: () -> Unit,
) {
    var dragAmount by remember { mutableFloatStateOf(0f) }
    var showEmergencyInfo by remember { mutableStateOf(false) }
    val clock = remember { java.text.SimpleDateFormat("h:mm", java.util.Locale.getDefault()).format(java.util.Date()) }
    ScreenColumn {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("9:41  ·  Serein preview", style = MaterialTheme.typography.labelMedium)
            Text("82%  ◉", style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.weight(0.3f))
        Text(clock, fontSize = 76.sp, lineHeight = 80.sp, fontWeight = FontWeight.Light)
        Text(
            java.text.SimpleDateFormat("EEEE, MMMM d", java.util.Locale.getDefault()).format(java.util.Date()),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.weight(0.55f))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("NOTIFICATIONS  ·  SAMPLE", style = MaterialTheme.typography.labelMedium)
            notifications.take(2).forEach {
                GlassCard(Modifier.fillMaxWidth(), alpha = if (darkMode) 0.67f else 0.63f) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(it.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(it.body, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
        Spacer(Modifier.weight(0.4f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            CircleAction(Icons.Rounded.Phone, "Emergency info") { showEmergencyInfo = true }
            Column(
                Modifier
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { _, amount -> dragAmount += amount },
                            onDragEnd = { if (dragAmount < -36f) onSwipeUp(); dragAmount = 0f },
                        )
                    }
                    .combinedClickable(onClick = onSwipeUp, onLongClick = onBiometric),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(Icons.Rounded.Fingerprint, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
                Text("Swipe up to preview", style = MaterialTheme.typography.labelSmall)
                TextButton(onClick = onBiometric) { Text("Try device biometrics") }
            }
            CircleAction(Icons.Rounded.Apps, "Apps preview", onClick = onSwipeUp)
        }
    }
    if (showEmergencyInfo) {
        AlertDialog(
            onDismissRequest = { showEmergencyInfo = false },
            title = { Text("Emergency access") },
            text = { Text("This app preview cannot place emergency calls. Use the emergency controls on your device's real lock screen.") },
            confirmButton = {
                TextButton(onClick = { showEmergencyInfo = false }) { Text("Understood") }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreen(
    darkMode: Boolean,
    accent: Color,
    page: Int,
    apps: List<DemoApp>,
    notifications: Int,
    largerText: Boolean,
    transparency: Float,
    reduceMotion: Boolean,
    onPage: (Int) -> Unit,
    onLaunch: (DemoApp) -> Unit,
    onDrawer: () -> Unit,
    onControls: () -> Unit,
    onNotifications: () -> Unit,
    onRecents: () -> Unit,
    onSettings: () -> Unit,
    onLock: () -> Unit,
) {
    var editMode by remember { mutableStateOf(false) }
    var showFolderEditor by remember { mutableStateOf(false) }
    var folderName by remember { mutableStateOf("") }
    var openFolder by remember { mutableStateOf<HomeFolder?>(null) }
    var dockSlotToEdit by remember { mutableIntStateOf(-1) }
    val selectedFolderApps = remember { mutableStateListOf<String>() }
    val folders = remember { mutableStateListOf<HomeFolder>() }
    val dockApps = remember {
        mutableStateListOf(demoApps[0], demoApps[1], demoApps[8])
    }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var dragIndex by remember { mutableIntStateOf(-1) }
    val arrangedApps = remember { mutableStateListOf<DemoApp>().apply { addAll(apps) } }
    val animatedLift by animateFloatAsState(
        targetValue = if (editMode) 1f else 0f,
        animationSpec = if (reduceMotion) spring(stiffness = 1000f) else spring(),
        label = "icon-lift",
    )
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 8.dp, bottom = 10.dp)
            .pointerInput(page) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, amount -> dragOffsetX += amount },
                    onDragEnd = { if (dragOffsetX < -70f) onPage(1); if (dragOffsetX > 70f) onPage(-1); dragOffsetX = 0f },
                )
            },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    "${java.text.SimpleDateFormat("h:mm", java.util.Locale.getDefault()).format(java.util.Date())}  ·  Serein",
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    java.text.SimpleDateFormat("EEEE, MMMM d", java.util.Locale.getDefault()).format(java.util.Date()),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Row {
                IconButton(onClick = onNotifications) {
                    Box {
                        Icon(Icons.Rounded.Notifications, contentDescription = "Notifications")
                        if (notifications > 0) Text("$notifications", modifier = Modifier.align(Alignment.TopEnd), fontSize = 9.sp)
                    }
                }
                IconButton(onClick = onControls) { Icon(Icons.Rounded.Tune, contentDescription = "Quick settings") }
            }
        }
        GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("A clear day, at your pace.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Your space. Make it yours.", style = MaterialTheme.typography.bodySmall)
                }
                Icon(Icons.Rounded.Widgets, null, tint = accent, modifier = Modifier.size(28.dp))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(if (editMode) "Hold and drag to rearrange" else "YOUR APPS", style = MaterialTheme.typography.labelMedium)
            Row {
                if (editMode) {
                    TextButton(onClick = { showFolderEditor = true }) { Text("New folder") }
                }
                TextButton(onClick = { editMode = !editMode }) {
                    Text(if (editMode) "Done" else "Customize")
                }
            }
        }
        if (folders.isNotEmpty()) {
            LazyRow(
                Modifier.fillMaxWidth().height(58.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(folders, key = { it.name }) { folder ->
                    GlassCard(
                        Modifier.height(54.dp),
                        alpha = transparency,
                        onClick = { openFolder = folder },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Folder, null, tint = accent)
                            Text(folder.name, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
        val visibleApps = arrangedApps.drop(page * 8).take(8)
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
            val cellWidth = maxWidth / 4
            val cellHeight = (maxHeight / 2).coerceAtLeast(92.dp)
            Column {
                visibleApps.chunked(4).forEachIndexed { rowIndex, rowApps ->
                    Row(Modifier.fillMaxWidth().height(cellHeight), horizontalArrangement = Arrangement.Start) {
                        rowApps.forEachIndexed { columnIndex, app ->
                            val index = rowIndex * 4 + columnIndex
                            val isDragging = dragIndex == index
                            AppIcon(
                                app = app,
                                textSize = if (largerText) 14.sp else 12.sp,
                                modifier = Modifier
                                    .width(cellWidth)
                                    .height(cellHeight)
                                    .alpha(if (isDragging) 0.72f else 1f)
                                    .pointerInput(index) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                dragIndex = index
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                                editMode = true
                                            },
                                            onDragEnd = {
                                                if (dragIndex >= 0) {
                                                    val dx = (dragOffsetX / cellWidth.toPx()).roundToInt()
                                                    val dy = (dragOffsetY / cellHeight.toPx()).roundToInt()
                                                    val destination = (dragIndex + dx + dy * 4)
                                                        .coerceIn(0, visibleApps.lastIndex)
                                                    if (destination != dragIndex) {
                                                        val sourceIndex = page * 8 + dragIndex
                                                        val destinationIndex = page * 8 + destination
                                                        val movedApp = arrangedApps.removeAt(sourceIndex)
                                                        arrangedApps.add(destinationIndex, movedApp)
                                                    }
                                                }
                                                dragIndex = -1
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                            },
                                            onDragCancel = {
                                                dragIndex = -1
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                            },
                                            onDrag = { change, amount ->
                                                change.consume()
                                                dragOffsetX += amount.x
                                                dragOffsetY += amount.y
                                            },
                                        )
                                    },
                                onClick = { onLaunch(app) },
                                onLongPress = { editMode = true },
                                scale = if (isDragging && !reduceMotion) 1.07f + animatedLift * 0.03f else 1f,
                                dragOffsetX = if (isDragging) dragOffsetX else 0f,
                                dragOffsetY = if (isDragging) dragOffsetY else 0f,
                            )
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(if (page == 0) "●  ○" else "○  ●", style = MaterialTheme.typography.labelMedium, color = accent)
        }
        GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                DockButton(Icons.Rounded.Apps, "All apps", onDrawer)
                dockApps.forEachIndexed { index, app ->
                    DockButton(app.icon, app.name, {
                        if (editMode) dockSlotToEdit = index else onLaunch(app)
                    })
                }
            }
        }
        if (editMode) {
            Text(
                "Tap a dock app to replace it, or drag an app to rearrange.",
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            TextButton(onClick = onRecents) { Text("Recent apps") }
            TextButton(onClick = onLock) { Text("Lock preview") }
        }
    }
    if (showFolderEditor) {
        AlertDialog(
            onDismissRequest = { showFolderEditor = false },
            title = { Text("Create a folder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = folderName,
                        onValueChange = { folderName = it },
                        label = { Text("Folder name") },
                        singleLine = true,
                    )
                    Text("Choose at least two preview apps.")
                    LazyColumn(Modifier.heightIn(max = 220.dp)) {
                        items(apps, key = { it.name }) { app ->
                            FilterChip(
                                selected = app.name in selectedFolderApps,
                                onClick = {
                                    if (app.name in selectedFolderApps) selectedFolderApps.remove(app.name)
                                    else selectedFolderApps.add(app.name)
                                },
                                label = { Text(app.name) },
                                leadingIcon = if (app.name in selectedFolderApps) {
                                    { Icon(Icons.Rounded.Check, contentDescription = null) }
                                } else null,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                        enabled = folderName.isNotBlank() &&
                            selectedFolderApps.size >= 2 &&
                            folders.none { it.name.equals(folderName.trim(), ignoreCase = true) },
                    onClick = {
                        folders.add(
                            HomeFolder(
                                folderName.trim(),
                                apps.filter { it.name in selectedFolderApps },
                            ),
                        )
                        folderName = ""
                        selectedFolderApps.clear()
                        showFolderEditor = false
                    },
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showFolderEditor = false }) { Text("Cancel") }
            },
        )
    }
    openFolder?.let { folder ->
        AlertDialog(
            onDismissRequest = { openFolder = null },
            title = { Text(folder.name) },
            text = {
                Column {
                    folder.apps.forEach { app ->
                        TextButton(
                            onClick = {
                                openFolder = null
                                onLaunch(app)
                            },
                        ) {
                            AppGlyph(app, 30.dp)
                            Text(app.name, Modifier.padding(start = 10.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { openFolder = null }) { Text("Close") }
            },
        )
    }
    if (dockSlotToEdit >= 0) {
        AlertDialog(
            onDismissRequest = { dockSlotToEdit = -1 },
            title = { Text("Choose a dock app") },
            text = {
                LazyColumn(Modifier.heightIn(max = 300.dp)) {
                    items(apps, key = { it.name }) { app ->
                        TextButton(
                            onClick = {
                                dockApps[dockSlotToEdit] = app
                                dockSlotToEdit = -1
                            },
                        ) {
                            AppGlyph(app, 30.dp)
                            Text(app.name, Modifier.padding(start = 10.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { dockSlotToEdit = -1 }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun AppIcon(
    app: DemoApp,
    textSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    scale: Float = 1f,
    dragOffsetX: Float = 0f,
    dragOffsetY: Float = 0f,
) {
    Column(
        modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = dragOffsetX
                translationY = dragOffsetY
            }
            .combinedClickable(onClick = onClick, onLongClick = onLongPress)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            Modifier
                .size(49.dp)
                .shadow(8.dp, RoundedCornerShape(17.dp))
                .clip(RoundedCornerShape(17.dp))
                .background(app.tint.copy(alpha = 0.92f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(app.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(25.dp))
        }
        Text(app.name, fontSize = textSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun DockButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        Modifier.combinedClickable(onClick = onClick, onLongClick = onClick).padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}

@Composable
private fun DrawerScreen(
    search: String,
    apps: List<DemoApp>,
    largerText: Boolean,
    transparency: Float,
    onSearch: (String) -> Unit,
    onBack: () -> Unit,
    onLaunch: (DemoApp) -> Unit,
) {
    ScreenColumn {
        PageHeader("All apps", onBack)
        SearchField(search, "Search apps", onSearch)
        Text("A — Z  ·  PREVIEW APPS", style = MaterialTheme.typography.labelMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            items(apps.filter { it.name.contains(search, ignoreCase = true) }.sortedBy { it.name }) { app ->
                GlassCard(Modifier.fillMaxWidth(), alpha = transparency, onClick = { onLaunch(app) }) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        AppGlyph(app, 42.dp)
                        Text(app.name, Modifier.weight(1f), fontSize = if (largerText) 19.sp else 16.sp)
                        Icon(Icons.Rounded.ChevronRight, null)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationScreen(
    items: List<DemoNotification>,
    expanded: Int,
    transparency: Float,
    onExpand: (Int) -> Unit,
    onDismiss: (Int) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    ScreenColumn {
        PageHeader("Notifications", onBack, action = if (items.isEmpty()) null else "Clear all", onAction = onClear)
        Text("${items.size} SAMPLE NOTIFICATIONS", style = MaterialTheme.typography.labelMedium)
        if (items.isEmpty()) {
            GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
                Text("You’re all caught up in this preview.")
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items.groupBy { it.group }.toList(), key = { it.first }) { (group, groupedItems) ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(group.uppercase(), style = MaterialTheme.typography.labelMedium)
                    groupedItems.forEach { item ->
                        GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(
                                        Modifier.weight(1f).combinedClickable(
                                            onClick = { onExpand(item.id) },
                                            onLongClick = { onDismiss(item.id) },
                                        ),
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Text("now", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text(item.body, style = MaterialTheme.typography.bodyMedium, maxLines = if (expanded == item.id) 8 else 2)
                                    }
                                    IconButton(onClick = { onDismiss(item.id) }) {
                                        Icon(Icons.Rounded.Close, contentDescription = "Dismiss ${item.title}")
                                    }
                                }
                                if (expanded == item.id) TextButton(onClick = { onDismiss(item.id) }) { Text("Dismiss") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlScreen(
    tiles: List<String>,
    states: List<Boolean>,
    brightness: Float,
    volume: Float,
    edit: Boolean,
    transparency: Float,
    onToggle: (Int) -> Unit,
    onBrightness: (Float) -> Unit,
    onVolume: (Float) -> Unit,
    onEdit: () -> Unit,
    onMoveTile: (Int) -> Unit,
    onBack: () -> Unit,
) {
    ScreenColumn {
        PageHeader("Quick settings", onBack, action = if (edit) "Done" else "Edit", onAction = onEdit)
        Text("THIS APP PREVIEW ONLY · DEVICE SETTINGS ARE UNCHANGED", style = MaterialTheme.typography.labelSmall)
        tiles.chunked(2).forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEachIndexed { columnIndex, label ->
                    val index = rowIndex * 2 + columnIndex
                    GlassCard(
                        Modifier.weight(1f).height(78.dp),
                        alpha = transparency,
                        onClick = { onToggle(index) },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Icon(controlIcon(label), null, tint = if (states[index]) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                            Column(Modifier.weight(1f)) {
                                Text(label, style = MaterialTheme.typography.labelLarge, maxLines = 1)
                                Text(if (states[index]) "On · demo" else "Off · demo", style = MaterialTheme.typography.labelSmall)
                            }
                            if (edit) IconButton(onClick = { onMoveTile(index) }) { Icon(Icons.Rounded.Tune, "Move tile later") }
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Display brightness  ·  preview", style = MaterialTheme.typography.labelLarge)
                Slider(value = brightness, onValueChange = onBrightness)
                Text("Volume  ·  preview", style = MaterialTheme.typography.labelLarge)
                Slider(value = volume, onValueChange = onVolume)
            }
        }
        Spacer(Modifier.weight(1f))
        Text("Tile switches, brightness, and volume do not change Android settings or hardware.")
    }
}

@Composable
private fun RecentsScreen(
    apps: List<String>,
    transparency: Float,
    onClose: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    ScreenColumn {
        PageHeader("Recent apps", onBack, action = if (apps.isEmpty()) null else "Clear", onAction = onClear)
        Text("RECENTLY OPENED IN THIS PREVIEW", style = MaterialTheme.typography.labelMedium)
        if (apps.isEmpty()) {
            GlassCard(Modifier.fillMaxWidth(), alpha = transparency) { Text("Open a demo app to see it here.") }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(apps, key = { it }) { name ->
                    val app = demoApps.first { it.name == name }
                    GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            AppGlyph(app, 42.dp)
                            Text(name, Modifier.weight(1f).padding(start = 12.dp), style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { onClose(name) }) { Icon(Icons.Rounded.Close, "Close $name") }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Text("These cards are not Android's system Recents screen.")
    }
}

@Composable
private fun SettingsScreen(
    search: String,
    darkMode: Boolean,
    accentIndex: Int,
    accents: List<Color>,
    largerText: Boolean,
    highContrast: Boolean,
    reduceMotion: Boolean,
    reduceTransparency: Boolean,
    transparency: Float,
    onSearch: (String) -> Unit,
    onTheme: (Boolean) -> Unit,
    onAccent: (Int) -> Unit,
    onLargerText: (Boolean) -> Unit,
    onHighContrast: (Boolean) -> Unit,
    onReduceMotion: (Boolean) -> Unit,
    onReduceTransparency: (Boolean) -> Unit,
    onPrivacy: () -> Unit,
    onBack: () -> Unit,
) {
    ScreenColumn {
        PageHeader("Settings", onBack)
        SearchField(search, "Search settings", onSearch)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
            if (matches(search, "Display theme accent light dark appearance")) item {
                GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SettingHeading(Icons.Rounded.Brightness6, "Display & appearance")
                        SettingSwitch("Dark theme", darkMode, onTheme)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            accents.forEachIndexed { index, color ->
                                Box(
                                    Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .then(if (index == accentIndex) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier)
                                        .combinedClickable(onClick = { onAccent(index) }, onLongClick = { onAccent(index) }),
                                )
                            }
                            Text("Accent color", Modifier.align(Alignment.CenterVertically), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            if (matches(search, "Accessibility text contrast motion transparency")) item {
                GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        SettingHeading(Icons.Rounded.AccessibilityNew, "Accessibility")
                        SettingSwitch("Larger text", largerText, onLargerText)
                        SettingSwitch("Higher contrast", highContrast, onHighContrast)
                        SettingSwitch("Reduce motion", reduceMotion, onReduceMotion)
                        SettingSwitch("Reduce transparency", reduceTransparency, onReduceTransparency)
                    }
                }
            }
            listOf(
                "Sound" to "Preview controls only; no device audio settings are changed.",
                "Notifications" to "Sample notification controls are available from the home screen.",
                "Storage" to "No device files are read or written.",
                "Updates" to "Android system updates are managed by your device manufacturer.",
            ).filter { matches(search, it.first + it.second) }.forEach { (title, detail) ->
                item {
                    GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(detail, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            if (matches(search, "Privacy Security Center permissions lock updates")) item {
                GlassCard(Modifier.fillMaxWidth(), alpha = transparency, onClick = onPrivacy) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SettingHeading(Icons.Rounded.Security, "Privacy & security")
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Rounded.ChevronRight, null)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyScreen(transparent: Float, onBack: () -> Unit) {
    val context = LocalContext.current
    val keyguard = remember(context) {
        context.getSystemService(android.app.KeyguardManager::class.java)
    }
    ScreenColumn {
        PageHeader("Privacy & security", onBack)
        Text("DEVICE FACTS AND PREVIEW LIMITS", style = MaterialTheme.typography.labelMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            item {
                StatusCard(Icons.Rounded.Lock, "Screen lock", if (keyguard?.isDeviceSecure == true) {
                    "A secure screen lock is configured on the host Android device."
                } else {
                    "No secure screen lock is configured on the host Android device."
                }, transparent)
            }
            item {
                StatusCard(Icons.Rounded.BatteryFull, "Android security patch", Build.VERSION.SECURITY_PATCH.ifBlank {
                    "Not reported by this Android image."
                }, transparent)
            }
            item {
                StatusCard(Icons.Rounded.Fingerprint, "Biometric prompt", "Uses Android BiometricPrompt when the device has an enrolled biometric. The app does not receive biometric data.", transparent)
            }
            item {
                StatusCard(Icons.Rounded.Shield, "App permissions", "This prototype requests no runtime permissions and does not access contacts, camera, microphone, location, or files.", transparent)
            }
            item {
                StatusCard(Icons.Rounded.Notifications, "Security activity", "Sample event: You opened the preview privacy screen. This is not a system security event log.", transparent)
            }
            item {
                GlassCard(Modifier.fillMaxWidth(), alpha = transparent) {
                    Text("Verified Boot, SELinux, encryption, key storage, permission enforcement, and system updates are supplied by the host Android build—not by this launcher-style prototype.")
                }
            }
        }
    }
}

@Composable
private fun DemoAppScreen(app: DemoApp, transparency: Float, onBack: () -> Unit) {
    ScreenColumn {
        PageHeader(app.name, onBack)
        Spacer(Modifier.weight(0.4f))
        GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
            Column(
                Modifier.fillMaxWidth().padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                AppGlyph(app, 74.dp)
                Text(app.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Text(app.description, style = MaterialTheme.typography.bodyMedium)
                Text("DEMO APP · NO DEVICE DATA", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
        if (app.name == "Files" || app.name == "Camera" || app.name == "Phone") {
            Text("This sample does not request permissions or perform real-world actions.")
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back to Serein") }
    }
}

@Composable
private fun PageHeader(
    title: String,
    onBack: () -> Unit,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back") }
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        if (action != null && onAction != null) TextButton(onClick = onAction) { Text(action) }
    }
}

@Composable
private fun SearchField(value: String, label: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(label) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        ),
    )
}

@Composable
private fun SettingHeading(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SettingSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun StatusCard(icon: ImageVector, title: String, detail: String, transparency: Float) {
    GlassCard(Modifier.fillMaxWidth(), alpha = transparency) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(detail, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    alpha: Float = 0.75f,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(25.dp)
    val cardModifier = modifier
        .shadow(10.dp, shape)
        .clip(shape)
        .background(MaterialTheme.colorScheme.surface.copy(alpha = alpha))
        .then(if (onClick != null) Modifier.combinedClickable(onClick = onClick, onLongClick = onClick) else Modifier)
        .padding(16.dp)
    Box(cardModifier) { content() }
}

@Composable
private fun AppGlyph(app: DemoApp, size: androidx.compose.ui.unit.Dp) {
    Box(
        Modifier.size(size).clip(RoundedCornerShape(15.dp)).background(app.tint),
        contentAlignment = Alignment.Center,
    ) {
        Icon(app.icon, contentDescription = app.name, tint = Color.White, modifier = Modifier.size(size * 0.52f))
    }
}

@Composable
private fun CircleAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        Modifier.width(72.dp).combinedClickable(onClick = onClick, onLongClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Surface(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), shape = CircleShape) {
            Icon(icon, contentDescription = label, modifier = Modifier.padding(12.dp).size(22.dp))
        }
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}

@Composable
private fun controlIcon(label: String): ImageVector = when (label) {
    "Wi-Fi" -> Icons.Rounded.SignalWifi4Bar
    "Bluetooth" -> Icons.Rounded.Bluetooth
    "Airplane" -> Icons.Rounded.FlashlightOn
    "Do not disturb" -> Icons.Rounded.StopScreenShare
    "Flashlight" -> Icons.Rounded.FlashlightOn
    else -> Icons.Rounded.BatteryFull
}

private fun matches(query: String, text: String): Boolean =
    query.isBlank() || text.contains(query, ignoreCase = true)
