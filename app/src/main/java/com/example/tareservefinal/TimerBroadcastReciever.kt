package com.example.tareservefinal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerBroadcastReciever(val mainActivity: MainActivity?=null) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        //println("GOT THE MESSAGE" + mainActivity)
        mainActivity?.showTimer()

    }
}