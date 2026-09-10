package com.example.spendingmonitor.notificationDatabase

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val dao =  NotificationDatabase.getNotificationDatabase(application).notificationDao()

    private val _selectedUtcDateMillis = MutableStateFlow(Instant.now().toEpochMilli())
    val selectedUtcDateMillis: StateFlow<Long> = _selectedUtcDateMillis.asStateFlow()

    val notifications: StateFlow<List<NotificationEntity>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )


    @OptIn(ExperimentalCoroutinesApi::class)
    val getNotificationsByDate: StateFlow<List<NotificationEntity>> = _selectedUtcDateMillis
        .flatMapLatest { utcMillis ->
            val (startTime, endTime) = getLocalStartAndEndTimestamps(utcMillis)
            dao.getNotificationsByDate(startTime, endTime)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun onDateSelected(utcMillis: Long) {
        _selectedUtcDateMillis.value = utcMillis
    }

    private fun getLocalStartAndEndTimestamps(datePickerMillis: Long): Pair<Long, Long> {
        val localDate = Instant.ofEpochMilli(datePickerMillis)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()

        val startTimeLocal = localDate.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val endTimeLocal = localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - 1

        return Pair(startTimeLocal, endTimeLocal)
    }
}