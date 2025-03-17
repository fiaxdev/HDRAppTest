package com.fiax.hdr.data.nfc

import android.nfc.Tag
import android.nfc.tech.IsoDep
import java.io.IOException


class APDUManager(tag: Tag?) {
    private val isoDep: IsoDep? = IsoDep.get(tag)

    // Constructor that takes a Tag object
    init {
        if (isoDep != null) {
            isoDep.connect()
        } else {
            throw IOException("IsoDep connection failed")
        }
    }

    // Method to send a basic APDU command and receive the response
    @Throws(IOException::class)
    fun sendCommand(apduCommand: ByteArray?): ByteArray {
        return isoDep!!.transceive(apduCommand)
    }

    // Method to close the connection after communication
    @Throws(IOException::class)
    fun close() {
        if (isoDep != null && isoDep.isConnected) {
            isoDep.close()
        }
    }

    // Helper method to format and send specific APDU commands
    @Throws(IOException::class)
    fun selectApplication(aid: ByteArray): ByteArray {
        val selectCommand = ByteArray(aid.size + 5)
        selectCommand[0] = 0x00.toByte() // CLA
        selectCommand[1] = 0xA4.toByte() // INS: Select
        selectCommand[2] = 0x04.toByte() // P1: Select by AID
        selectCommand[3] = 0x00.toByte() // P2
        selectCommand[4] = aid.size.toByte() // Lc
        System.arraycopy(aid, 0, selectCommand, 5, aid.size) // AID Data
        return sendCommand(selectCommand)
    } // Add other methods as needed for your app
}
