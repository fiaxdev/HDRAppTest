package com.fiax.hdr.ui.components.scaffold

import android.widget.Toast
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fiax.hdr.ui.components.util.CustomSnackbarHost
import com.fiax.hdr.ui.navigation.AppNavigation
import com.fiax.hdr.viewmodel.AppUiEventViewModel
import com.fiax.hdr.viewmodel.UiEvent

@Composable
fun MainScaffold(
    navController: NavHostController,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val appUIEventViewModel: AppUiEventViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        appUIEventViewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message, event.actionLabel).run {
                        when (this) {
                            SnackbarResult.Dismissed -> {}
                            SnackbarResult.ActionPerformed -> { event.onActionClick?.invoke() }
                        }
                    }
                }
                is UiEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is UiEvent.ShowToast -> {
                    Toast.makeText(navController.context, event.message, Toast.LENGTH_SHORT).show()
                }

                is UiEvent.Navigate -> {
                    navController.navigate(event.route)
                }

                is UiEvent.NavigateWithData -> {
                    // Manually set SavedStateHandle before navigating
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(event.key, event.data)

                    navController.navigate(event.route)
                }
            }
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController) },
        snackbarHost = {
            CustomSnackbarHost( snackbarHostState = snackbarHostState)
        }
    ) { innerPadding ->
        AppNavigation(navController, Modifier.consumeWindowInsets(innerPadding).padding(innerPadding))
    }
}


