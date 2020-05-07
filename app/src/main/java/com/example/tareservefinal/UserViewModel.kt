package com.example.tareservefinal

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class UserViewModel(application: Application): AndroidViewModel(application){
    var userId = "null"
    var isTA = "0"
    var userToken = "null"
    var studentServe = "null"
    var reservedTA = "null"
    private var running = false
    private var timer = Timer()
    private val ONE_SECOND = 1000
    private var currTask: TimerTask? = null

    // Setter for userID
    fun setUserID(userID: String) {
        userId = userID
    }

    // Setter for isTA
    fun setIsTA(TA: String) {
        isTA = TA
    }

    val currentTime: MutableLiveData<Long> by lazy{
        MutableLiveData<Long>(0)
    }

    fun getTimeString(): MutableLiveData<Long>
    {

        return currentTime
    }

    fun startStop()
    {
        if(running)
        {
            timer.cancel()

            running = false
        }
        else
        {
            running = true

            timer = Timer()

            timer.scheduleAtFixedRate(object : TimerTask() {
                var counter: Long = currentTime.value!!

                // runs on a non-UI thread.
                override fun run() {
                    counter = currentTime.value!!

                    counter++

                    //'posting' new values every second to the MutableLiveData object
                    // notice that this is happening on a non-UI thread.
                    currentTime.postValue(counter)
                    currTask = this
                }

                //delay              //period
            }, ONE_SECOND.toLong(), ONE_SECOND.toLong())

        }
    }

    fun reset()
    {
        currentTime.value= 0
        //after calling 'cancel' on timer we need to create a new instance
        timer.cancel()
        timer.purge()
        running=false
    }
}