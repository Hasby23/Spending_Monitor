package com.example.spendingmonitor.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.spendingmonitor.TitleBar
import com.example.spendingmonitor.appDatabase.AppViewModel
import com.example.spendingmonitor.isNotificationAccessGranted
import com.example.spendingmonitor.notificationDatabase.NotificationViewModel
import com.example.spendingmonitor.openNotificationAccessSettings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationHistoryScreen(
    notificationViewModel: NotificationViewModel,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isPermissionGranted by remember { mutableStateOf(isNotificationAccessGranted(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isPermissionGranted = isNotificationAccessGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var isFilterKeyword by remember { mutableStateOf(false) }
    var isFilterApp by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    val notifications by notificationViewModel.getNotificationsByDate.collectAsState()
    val selectedDateMillis by notificationViewModel.selectedUtcDateMillis.collectAsState()

    val filterApp by appViewModel.apps.collectAsState()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis
    )

    val filterAppMap: Map<String, Set<String>> = filterApp.associate { it.packageName to it.keyword.toSet() }
    val filteredNotifications = remember(notifications, filterAppMap, isFilterApp, isFilterKeyword) {
        if (isFilterApp && isFilterKeyword) {
            notifications.filter { notif ->
                val keywords = filterAppMap[notif.packageName]
                if (keywords.isNullOrEmpty()){
                    false
                } else {
                    keywords.any { keyword ->
                        notif.text.contains(keyword, ignoreCase = true)
                    }
                }
            }
        } else if (isFilterApp) {
            notifications.filter { it.packageName in filterAppMap.keys }
        } else if (isFilterKeyword) {
            notifications.filter { notif ->
                val keywords = filterAppMap[notif.packageName]
                if (keywords.isNullOrEmpty()){
                    true
                } else {
                    keywords.any { keyword ->
                        notif.text.contains(keyword, ignoreCase = true)
                    }
                }
            }
        } else {
            notifications
        }
    }

    Column(
        modifier = modifier
    ) {
        if (!isPermissionGranted) {
            Button(
                onClick = { openNotificationAccessSettings(context) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text("Grant Notification Listener Permission")
            }
        }
        TitleBar(
            onBack = {},
            title = "Notification History",
            backEnabled = false
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier =  Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = isFilterApp,
                    onCheckedChange = { isFilterApp = !isFilterApp},
                )
                Text("Filter App")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = isFilterKeyword,
                    onCheckedChange = { isFilterKeyword = !isFilterKeyword},
                )
                Text("Filter Keyword")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.weight(1f)
            ) { Text("Select Date") }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatTimestamp(selectedDateMillis, true),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { picked ->
                                notificationViewModel.onDateSelected(picked)
                            }
                            showDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false
                )
            }
        }
        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notifications captured yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredNotifications, key = { it.id }) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row() {
                                Text(
                                    text = item.packageName,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = formatTimestamp(item.timestamp),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatTimestamp(epochMillis: Long, noTime: Boolean = false): String {
    return if (noTime) {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        formatter.format(Date(epochMillis))
    } else {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
        formatter.format(Date(epochMillis))
    }

}