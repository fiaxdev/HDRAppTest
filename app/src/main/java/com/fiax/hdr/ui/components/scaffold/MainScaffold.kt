package com.fiax.hdr.ui.components.scaffold

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fiax.hdr.ui.navigation.AppNavigation

@Composable
fun MainScaffold(
    navController: NavHostController,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = { BottomBar(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        AppNavigation(navController, Modifier.consumeWindowInsets(padding).padding(padding), snackbarHostState)
    }
}

