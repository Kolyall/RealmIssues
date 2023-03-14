package com.realmissues.utils

private val HEX_CHARS = "0123456789abcdef".toCharArray()

fun String.stringToHex(): String {
    return asHex(toByteArray())
}

private fun asHex(buf: ByteArray): String {
    val chars = CharArray(2 * buf.size)
    for (i in buf.indices) {
        chars[2 * i] = HEX_CHARS[buf[i].toInt() and 0xF0 ushr 4]
        chars[2 * i + 1] = HEX_CHARS[buf[i].toInt() and 0x0F]
    }
    return String(chars)
}