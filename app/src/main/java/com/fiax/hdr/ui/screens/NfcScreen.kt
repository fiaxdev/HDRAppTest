package com.fiax.hdr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fiax.hdr.ui.viewmodel.NfcViewModel

@Composable
fun NfcScreen(nfcViewModel: NfcViewModel) {
//    val message by nfcViewModel.nfcMessage.collectAsState()
//    val isWriting by nfcViewModel.isWriting.collectAsState()
//    var inputMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(
//            text = if (isWriting) "NFC Write Mode" else "NFC Read Mode",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold
//        )

//        TextField(
//            value = inputMessage,
//            onValueChange = { inputMessage = it },
//            label = { Text("Enter message") },
//            enabled = isWriting,
//            modifier = Modifier.fillMaxWidth()
//        )

//        Button(
//            onClick = { nfcViewModel.setMessage(inputMessage) },
//            enabled = isWriting
//        ) {
//            Text("Set NFC Message")
//        }
//
//        Button(onClick = { nfcViewModel.toggleMode() }) {
//            Text(if (isWriting) "Switch to Read Mode" else "Switch to Write Mode")
//        }

        Spacer(modifier = Modifier.height(20.dp))

//        Text("Last NFC Read: $message", fontSize = 16.sp)
    }
}