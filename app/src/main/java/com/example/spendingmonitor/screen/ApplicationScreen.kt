package com.example.spendingmonitor.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.spendingmonitor.AppItem
import com.example.spendingmonitor.TitleBar
import com.example.spendingmonitor.appDatabase.AppViewModel

@Composable
fun ApplicationScreen(
    appList: List<AppItem>?,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val selectedPackageNames by appViewModel.allPackageNames.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (appList == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            TitleBar(
                onBack = {},
                title = "Application",
                backEnabled = false
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = appList,
                    key = { it.packageName }
                ) { appItem ->
                    val isChecked = appItem.packageName in selectedPackageNames

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                bitmap = appItem.icon.toBitmap().asImageBitmap(),
                                contentDescription = appItem.appName,
                                modifier = Modifier.weight(1f)
                                    .size(48.dp)
                            )
                            Column(
                                modifier = Modifier.weight(4f)
                            ){
                                Text(text = appItem.appName, fontSize = 16.sp)
                                Text(text = appItem.packageName, fontSize = 12.sp)
                            }
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        appViewModel.addByPackageName(
                                            packageName = appItem.packageName,
                                            appName = appItem.appName,
                                        )
                                    } else {
                                        appViewModel.deleteByPackageName(appItem.packageName)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}