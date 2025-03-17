package com.fiax.hdr.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.fiax.hdr.data.nfc.NfcCustomManager

class NfcViewModel(application: Application) : AndroidViewModel(application) {

//    private val _nfcMessage = MutableStateFlow("")
//    val nfcMessage: StateFlow<String> = _nfcMessage.asStateFlow()
//
//    private val _isWriting = MutableStateFlow(true)
//    val isWriting: StateFlow<Boolean> = _isWriting.asStateFlow()

    private val nfcCustomManager = NfcCustomManager(application)
    //private val bluetoothManager = BluetoothManager(application)

//    fun handleNfcIntent(intent: Intent) {
//        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
//        if (tag == null) {
//            Log.w("NfcManager", "No NFC tag found")
//            return
//        }
//
//        val techList = tag.techList.joinToString(", ")
//        Log.d("NfcManager", "Detected NFC Technologies: $techList")
//
//        if (tag.techList.contains("android.nfc.tech.NfcA") || tag.techList.contains("android.nfc.tech.IsoDep")) {
//            Log.d("NfcManager", "NFC P2P mode detected")
//
//            // Read incoming UUID if available
//            val incomingUuid = readIsoDepTag(tag)
//            if (incomingUuid != null) {
//                Log.d("NfcManager", "Received UUID: $incomingUuid")
//                startBluetoothConnection(incomingUuid) // Start Bluetooth
//            } else {
//                Log.w("NfcManager", "No valid UUID found")
//            }
//        }
//    }

    fun writeNfcData(tag: Tag, data: String) {
        try {
            val isoDep = IsoDep.get(tag)
            if (isoDep != null) {
                isoDep.connect()

                val payload = data.toByteArray(Charsets.UTF_8)
                val command = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, payload.size.toByte()) + payload

                Log.d("NfcManager", "Sending UUID over NFC: $data")
                val response = isoDep.transceive(command)
                val responseHex = response.joinToString(" ") { "%02X".format(it) }
                Log.d("NfcManager", "Response from tag: $responseHex")

                isoDep.close()
            }
        } catch (e: Exception) {
            Log.e("NfcManager", "Error writing NFC data", e)
        }
    }

//    fun startBluetoothConnection(uuid: String) {
//        Log.d("BluetoothManager", "Received UUID from NFC: $uuid")
//
//        val bluetoothCustomManager = BluetoothCustomManager.getInstance()
//        bluetoothCustomManager.startConnection(getApplication(), uuid)
//    }

    // Function to read UUID from NFC tag
    fun readNfcTag(intent: Intent): String? {
        Log.d("NfcManager", "Attempting to read NFC tag")

        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag == null) {
            Log.w("NfcManager", "No NFC tag found in intent")
            return null
        }

        val techList = tag.techList.joinToString(", ")
        Log.d("NfcManager", "Detected NFC Technologies: $techList")

        return if (tag.techList.contains("android.nfc.tech.IsoDep")) {
            readIsoDepTag(tag)
        } else {
            Log.w("NfcManager", "Unsupported NFC technology")
            null
        }
    }


    fun readIsoDepTag(tag: Tag): String? {
        try {
            val isoDep = IsoDep.get(tag) ?: return null
            isoDep.connect()

            Log.d("NfcManager", "Connected to IsoDep tag")

            // Example APDU Command (Select Application)
            val command = byteArrayOf(
                0x00.toByte(),  // CLA (Class)
                0xA4.toByte(),  // INS (Instruction: Select)
                0x04.toByte(),  // P1
                0x00.toByte(),  // P2
                0x07.toByte(),  // Length of AID
                0xA0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x62.toByte(), 0x03.toByte(), 0x01.toByte() // Example AID
            )

            val response = isoDep.transceive(command)
            val responseHex = response.joinToString(" ") { "%02X".format(it) }

            Log.d("NfcManager", "Received APDU Response: $responseHex")

            isoDep.close()
            return responseHex
        } catch (e: Exception) {
            Log.e("NfcManager", "Error communicating with IsoDep tag", e)
        }
        return null
    }


//    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(getApplication())
//
//    fun toggleMode() {
//        _isWriting.value = !_isWriting.value
//    }
//
//    fun setMessage(message: String) {
//        _nfcMessage.value = message
//    }

//    fun handleNfcIntent(intent: Intent) {
//        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return
//
//        when {
//            MifareClassic.get(tag) != null -> {
//                showToast("MIFARE Classic tag detected!")
//                readMifareClassic(tag)
//            }
//            IsoDep.get(tag) != null -> {
//                showToast("ISO-DEP (MIFARE Plus SL3) detected!")
//                communicateWithIsoDepTag(tag)
//            }
//            else -> showToast("Unsupported tag type!")
//        }
//    }

//    private fun readMifareClassic(tag: Tag) {
//        try {
//            val mifare = MifareClassic.get(tag)
//            mifare.connect()
//
//            // Authenticate sector 0 with default key (this key must match the tag)
//            val auth = mifare.authenticateSectorWithKeyA(0, MifareClassic.KEY_DEFAULT)
//
//            if (auth) {
//                val blockIndex = mifare.sectorToBlock(0)
//                val data = mifare.readBlock(blockIndex)
//                showToast("Read data: ${String(data)}")
//            } else {
//                showToast("Authentication failed!")
//            }
//
//            mifare.close()
//        } catch (e: Exception) {
//            Log.e("NfcViewModel", "Error reading MIFARE Classic", e)
//            showToast("Error reading MIFARE Classic tag!")
//        }
//    }
//
//    private fun communicateWithIsoDepTag(tag: Tag) {
//        try {
//            val isoDep = IsoDep.get(tag)
//            isoDep.connect()
//
//            // Example APDU command (must be replaced with real authentication/read command)
//            val apduCommand = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, 0x00)
//            val response = isoDep.transceive(apduCommand)
//
//            showToast("Received response: ${response.joinToString(" ")}")
//
//            isoDep.close()
//        } catch (e: Exception) {
//            Log.e("NfcViewModel", "Error communicating with IsoDep tag", e)
//            showToast("Error communicating with tag!")
//        }
//    }


//    private fun writeNfcTag(tag: Tag, message: String) {
//        try {
//            val ndef = Ndef.get(tag)
//
//            if (ndef == null) {
//                val formatable = NdefFormatable.get(tag)
//                if (formatable != null) {
//                    formatable.connect()
//                    formatable.format(NdefMessage(arrayOf(NdefRecord.createTextRecord("en", message))))
//                    showToast("Tag formatted and written successfully!")
//                    formatable.close()
//                    return
//                } else {
//                    showToast("NDEF not supported and not formatable")
//                    return
//                }
//            }
//
//            val ndefMessage = NdefMessage(arrayOf(NdefRecord.createTextRecord("en", message)))
//
//            ndef.connect()
//            if (ndef.isWritable) {
//                ndef.writeNdefMessage(ndefMessage)
//                showToast("Message written to NFC!")
//            } else {
//                showToast("NFC tag is read-only")
//            }
//            ndef.close()
//        } catch (e: Exception) {
//            Log.e("NfcViewModel", "Error writing NFC", e)
//            showToast("Error writing NFC: ${e.message}")
//        }
//    }
//
//
//    private fun readNfcTag(tag: Tag) {
//        try {
//            val ndef = Ndef.get(tag) ?: run {
//                showToast("NDEF not supported on this tag")
//                return
//            }
//
//            ndef.connect()
//            val ndefMessage = ndef.ndefMessage
//
//            if (ndefMessage == null || ndefMessage.records.isEmpty()) {
//                showToast("No NDEF records found on this tag")
//                ndef.close()
//                return
//            }
//
//            val message = String(ndefMessage.records[0].payload, Charset.forName("UTF-8")).substring(3)
//            _nfcMessage.value = message
//            showToast("Read NFC: $message")
//
//            ndef.close()
//        } catch (e: Exception) {
//            Log.e("NfcViewModel", "Error reading NFC", e)
//            showToast("Error reading NFC: ${e.message}")
//        }
//    }


    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}