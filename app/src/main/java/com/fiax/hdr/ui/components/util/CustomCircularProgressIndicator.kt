package com.fiax.hdr.ui.components.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CustomCircularProgressIndicator(){
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .clickable(onClick = {})
            .fillMaxSize(),
        contentAlignment = Alignment.Center

    ){
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}