package com.example.spendingmonitor

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spendingmonitor.screen.ApplicationScreen
import com.example.spendingmonitor.screen.NotificationHistoryScreen
import com.example.spendingmonitor.screen.SettingScreen
import com.example.spendingmonitor.appDatabase.AppViewModel
import com.example.spendingmonitor.notificationDatabase.NotificationViewModel
import com.example.spendingmonitor.ui.theme.SpendingMonitorTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


data class AppItem(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    val isSystemApp: Boolean
)

class MainActivity : ComponentActivity() {
    private val notificationViewModel: NotificationViewModel by viewModels()
    private val appViewModel: AppViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var appList by remember { mutableStateOf<List<AppItem>?>(null) }

            LaunchedEffect(Unit) {
                val apps = withContext(Dispatchers.IO) {
                    getDeviceApp(packageManager)
                }
                appList = apps
            }

            SpendingMonitorTheme {
                var selectIndex by remember { mutableIntStateOf(0) }
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            NavigationBarItem(
                                selected = selectIndex == 0,
                                onClick = { selectIndex = 0 },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") }
                            )
                            NavigationBarItem(
                                selected = selectIndex == 1,
                                onClick = { selectIndex = 1 },
                                icon = { Icon(Icons.Default.Apps, contentDescription = "Apps") },
                                label = { Text("Apps") }
                            )
                            NavigationBarItem(
                                selected = selectIndex == 2,
                                onClick = { selectIndex = 2 },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Setting") },
                                label = { Text("Setting") }
                            )
                        }
                    }
                ) { innerPadding ->
                    when (selectIndex) {
                        0 -> NotificationHistoryScreen(
                            notificationViewModel = notificationViewModel,
                            appViewModel = appViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        1 -> ApplicationScreen(
                            appList = appList,
                            appViewModel = appViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        2 -> SettingScreen(
                            appList = appList,
                            appViewModel = appViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

fun getDeviceApp(
    packageManager: PackageManager
) :List<AppItem> {
    val apps: List<ApplicationInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0L))
    } else {
        @Suppress("DEPRECATION")
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }

    return apps.mapNotNull { appInfo ->
        val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        if (isSystem) {
            return@mapNotNull null
        }

        AppItem(
            appName = appInfo.loadLabel(packageManager).toString(),
            packageName = appInfo.packageName,
            icon = appInfo.loadIcon(packageManager),
            isSystemApp = false
        )
    }.sortedBy { it.appName }
}

@Composable
fun TitleBar(
    onBack: () -> Unit,
    title: String,
    backEnabled: Boolean = true,
    modifier: Modifier = Modifier
){
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ){
            if (backEnabled) {
                IconButton(
                    onClick =  onBack,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = title
                    )
                }
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(3f),
        ){ Text(title) }
        Box(
            modifier = Modifier.weight(1f)
        ){ }
    }
}



