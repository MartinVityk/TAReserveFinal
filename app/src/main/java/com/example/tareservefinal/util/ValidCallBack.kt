package com.example.tareservefinal.util

/**
 * Custom Interface to deal with asynchronous queries from the Database during Sign-In
 */
interface ValidCallBack {
    fun onCallback(value: Boolean)
}