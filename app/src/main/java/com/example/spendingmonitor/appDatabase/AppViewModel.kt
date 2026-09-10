package com.example.spendingmonitor.appDatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val dao =  AppDatabase.getAppDatabase(application).appDao()

    val apps: StateFlow<List<AppEntity>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    val allPackageNames: StateFlow<Set<String>> = dao.getAllPackageName()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptySet()
        )

    fun addApp(appEntity: AppEntity) {
        viewModelScope.launch {
            dao.upsert(appEntity)
        }
    }

    fun addByPackageName(packageName: String, appName: String) {
        viewModelScope.launch {
            dao.upsert(
                AppEntity(
                    packageName = packageName,
                    keyword = emptyList(),
                    appName = appName,
                )
            )
        }
    }
    fun deleteByPackageName(packageName: String) {
        viewModelScope.launch {
            dao.deleteByPackageName(packageName)
        }
    }

}