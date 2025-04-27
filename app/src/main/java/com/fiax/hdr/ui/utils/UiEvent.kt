package com.fiax.hdr.ui.utils

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}