package com.example.spendingmonitor.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.spendingmonitor.AppItem
import com.example.spendingmonitor.TitleBar
import com.example.spendingmonitor.appDatabase.AppEntity
import com.example.spendingmonitor.appDatabase.AppViewModel


@Composable
fun SettingScreen(
    appList: List<AppItem>?,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val selectedApp by appViewModel.apps.collectAsState()

    var selectedAppEntity by remember { mutableStateOf<AppEntity?>(null) }

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
        } else if (selectedAppEntity != null) {
            DetailScreen(
                appIcon = appList.first { it.packageName == selectedAppEntity!!.packageName }.icon.toBitmap().asImageBitmap(),
                onBack = { selectedAppEntity = null},
                selectedAppEntity = selectedAppEntity!!,
                appViewModel = appViewModel,
                modifier = modifier,

            )
        } else {
            TitleBar(
                onBack = {},
                title = "Setting",
                backEnabled = false
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = selectedApp,
                    key = { it.packageName }
                ) { app ->
                    Card(
                        onClick = { selectedAppEntity = app},
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
                                bitmap = appList.first { it.packageName == app.packageName }.icon.toBitmap().asImageBitmap(),
                                contentDescription = app.appName,
                                modifier = Modifier.weight(1f)
                                    .size(48.dp)
                            )
                            Column(
                                modifier = Modifier.weight(4f)
                            ){
                                Text(text = app.appName, fontSize = 16.sp)
                                Text(text = app.packageName, fontSize = 12.sp)
                            }
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailScreen (
    appIcon: ImageBitmap,
    onBack: () -> Unit,
    selectedAppEntity: AppEntity,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val keywordList = remember { mutableStateListOf<String>() }
    keywordList.addAll(selectedAppEntity.keyword)

    val textFieldState = rememberTextFieldState("")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
                .fillMaxSize()
        ) {
            TitleBar(
                onBack = onBack,
                title = "Detail",
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    bitmap = appIcon,
                    contentDescription = selectedAppEntity.appName,
                    modifier = Modifier.size(120.dp)
                )
                Text(
                    text = selectedAppEntity.appName,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            OutlinedTextField(
                state = textFieldState,
                label = { Text("Keyword") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    val input = textFieldState.text.toString().trim()
                    if (input.isNotEmpty()) {
                        keywordList.add(input)
                        textFieldState.clearText()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(keywordList) { keyword ->
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = keyword,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            IconButton(
                                onClick = { keywordList.remove(keyword) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            Row() {
                Box(modifier = Modifier.weight(2f))
                Button(
                    onClick = { appViewModel.addApp(
                        AppEntity(
                            packageName = selectedAppEntity.packageName,
                            appName = selectedAppEntity.appName,
                            keyword = keywordList,
                        )
                    ) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.weight(1f)
                ){
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save",
                    )
                }
            }
        }
    }
}