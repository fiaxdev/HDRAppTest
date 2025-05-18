package com.fiax.hdr.viewmodel

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.domain.repository.PatientRepository
import com.fiax.hdr.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppUiEventViewModel @Inject constructor(
    patientRepository: PatientRepository,
    bluetoothCustomManager: BluetoothCustomManager
) : ViewModel() {

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    init {
        patientRepository.newPatientEvents
            .onEach { patient ->
                showReceivedPatientSnackbar(patient)
            }
            .launchIn(viewModelScope)
    }

    init {
        bluetoothCustomManager.toastMessage
            .onEach { message ->
                sendEvent(UiEvent.ShowToast(message))
            }
            .launchIn(viewModelScope)
    }

    init {
        bluetoothCustomManager.snackbarMessage
            .onEach { message ->
                sendEvent(UiEvent.ShowSnackbar(message))
            }
            .launchIn(viewModelScope)
    }

    fun showReceivedPatientSnackbar(patient: Patient) {
        viewModelScope.launch {
            sendEvent(
                UiEvent.ShowSnackbar(
                    message = "New patient received",
                    actionLabel = "View",
                    onActionClick = {
                        viewModelScope.launch {
                            emitNavigateToPatient(patient)
                        }
                    }
                )
            )
        }
    }

    private suspend fun emitNavigateToPatient(patient: Patient) {
        val route = Screen.PatientDetails.route
        sendEvent(UiEvent.NavigateWithData(route, "patient", patient))
    }

    suspend fun sendEvent(event: UiEvent) {
        _uiEvents.emit(event)
    }
}

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val onActionClick: (() -> Unit)? = null
    ) : UiEvent()
    data class NavigateBack(val eraseBackStack: Boolean = false) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data class NavigateWithData(val route: String, val key: String, val data: Parcelable) : UiEvent()
}
