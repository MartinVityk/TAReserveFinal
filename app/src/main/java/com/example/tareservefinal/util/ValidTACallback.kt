package com.example.tareservefinal.util

/**
 * Custom Interface to deal with asynchronous queries for isTA from the Database during Sign-In
 */
interface ValidTACallback {
    fun onTACallback(registered: Boolean)
}