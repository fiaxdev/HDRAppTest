package com.fiax.hdr.ui.components.util

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TitleText(
    text: String,
    fontSize: Int = 20,
    fontWeight: FontWeight = FontWeight.Bold
){
    Text(text = text, fontSize = fontSize.sp, fontWeight = fontWeight)
}
