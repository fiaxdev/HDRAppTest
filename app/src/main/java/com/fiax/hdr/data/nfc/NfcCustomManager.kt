//package com.fiax.hdr.data.nfc
//
//import android.app.Activity
//import android.app.PendingIntent
//import android.bluetooth.BluetoothAdapter
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.nfc.*
//import android.nfc.tech.IsoDep
//import android.nfc.tech.Ndef
//import android.nfc.tech.NdefFormatable
//import android.nfc.tech.NfcA
//import android.os.Parcelable
//import android.util.Log
//import android.widget.Toast
//import com.fiax.hdr.data.bluetooth.BluetoothManager
//import java.nio.charset.Charset
//import kotlin.io.path.isWritable
//
//
//class NfcManager(private val context: Context) {
//    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)


    package com.fiax.hdr.data.nfc

    import android.app.Activity
    import android.app.PendingIntent
    import android.content.Context
    import android.content.Intent
    import android.content.IntentFilter
    import android.nfc.NdefMessage
    import android.nfc.NdefRecord
    import android.nfc.NfcAdapter
    import android.nfc.Tag
    import android.nfc.tech.Ndef
    import android.nfc.tech.NdefFormatable
    import android.nfc.tech.NfcA
    import android.util.Log
    import android.widget.Toast
    import java.nio.charset.Charset

    class NfcCustomManager(private val context: Context) {

        private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)
        private val filters = arrayOf(
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        )
        private val techLists = arrayOf(
            arrayOf(Ndef::class.java.name),
            arrayOf(NdefFormatable::class.java.name),
            arrayOf(NfcA::class.java.name)
        )

        init {
            println("NfcManager initialized. nfcAdapter: $nfcAdapter")
            if (nfcAdapter == null) {
                println("NFC is not supported on this device.")
            } else {
                println("NFC is supported on this device.")
                if (!nfcAdapter.isEnabled) {
                    println("NFC is disabled.")
                } else {
                    println("NFC is enabled.")
                }
            }
        }

        fun writeNfcTag(tag: Tag, message: String) {
            try {
                val ndef = Ndef.get(tag) ?: return
                ndef.connect()
                val ndefMessage = NdefMessage(arrayOf(NdefRecord.createTextRecord("en", message)))

                if (ndef.isWritable) {
                    ndef.writeNdefMessage(ndefMessage)
                    showToast("Message written to NFC!")
                } else {
                    showToast("NFC tag is read-only")
                }
                ndef.close()
            } catch (e: Exception) {
                showToast("Error writing NFC")
            }
        }

        fun readNfcTag(tag: Tag): String? {
            try {
                val ndef = Ndef.get(tag) ?: return null
                ndef.connect()
                val ndefMessage = ndef.ndefMessage ?: return null
                if (ndefMessage.records.isEmpty()) return null

                val message = String(ndefMessage.records[0].payload, Charset.forName("UTF-8"))
                showToast("Read NFC: $message")

                ndef.close()
                return message
            } catch (e: Exception) {
                Log.e("NfcViewModel", "Error reading NFC", e)
                showToast("Error reading NFC")
            }
            return null
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }



//    fun readIsoDepTag(tag: Tag): String? {
//        try {
//            val isoDep = IsoDep.get(tag) ?: return null
//            isoDep.connect()
//
//            Log.d("NfcManager", "Connected to IsoDep tag")
//
//            // Example APDU Command (Select Application)
//            val command = byteArrayOf(
//                0x00.toByte(),  // CLA (Class)
//                0xA4.toByte(),  // INS (Instruction: Select)
//                0x04.toByte(),  // P1
//                0x00.toByte(),  // P2
//                0x07.toByte(),  // Length of AID
//                0xA0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x62.toByte(), 0x03.toByte(), 0x01.toByte() // Example AID
//            )
//
//            val response = isoDep.transceive(command)
//            val responseHex = response.joinToString(" ") { "%02X".format(it) }
//
//            Log.d("NfcManager", "Received APDU Response: $responseHex")
//
//            isoDep.close()
//            return responseHex
//        } catch (e: Exception) {
//            Log.e("NfcManager", "Error communicating with IsoDep tag", e)
//        }
//        return null
//    }
//
//
//    // Function to check if NFC is enabled
//    fun isNfcEnabled(): Boolean {
//        return nfcAdapter?.isEnabled == true
//    }

        fun enableForegroundDispatch(activity: Activity) {
            if (nfcAdapter == null) {
                println("NFC is not supported on this device.")
                return
            }
            val intent = Intent(context, activity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
            nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, techLists)
        }

        fun disableForegroundDispatch(activity: Activity) {
            nfcAdapter?.disableForegroundDispatch(activity)
        }
    }
//
//    fun createNdefMessage(): NdefMessage {
//        val text = "Hello from NFC!"
//        val textBytes = text.toByteArray(Charset.forName("UTF-8"))
//        val record = NdefRecord.createTextRecord("en", text)
//        return NdefMessage(arrayOf(record))
//    }
//
//    fun handleNfcIntent(intent: Intent): String? {
//        val action = intent.action
//        if (action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
//            action == NfcAdapter.ACTION_TECH_DISCOVERED ||
//            action == NfcAdapter.ACTION_TAG_DISCOVERED) {
//            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
//            if (rawMsgs != null) {
//                val messages = rawMsgs.map { it as NdefMessage }
//                val payload = messages[0].records[0].payload
//                val message = String(payload, Charset.forName("UTF-8")).substring(3) // Remove language code prefix
//                showToast("NFC Connection: $message")
//                return message
//            }
//        }
//        return null
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//    }
//}