package com.example.tareservefinal.util

/**
 * Util class to obtain hashcode from email string
 */
class HashCode {

    // Function to take string input and output hashcode as a String
    fun hashCode(input: String):String {
        var hash = 0
        if (input.isEmpty()) return hash.toString()

        for (x in input.indices) {
            var char = input[x]

            hash = ((hash shl 5) - hash) + char.toInt()
            hash = hash and hash
        }
        return hash.toString()
    }

}