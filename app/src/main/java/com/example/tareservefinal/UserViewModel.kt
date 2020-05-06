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
    // TODO: Look to change this to private & move to 'User' Class Object
    var userId = "null"
    var isTA = "0"
    var userToken = "null"
    var studentServe = "null"
    var reservedTA = "null"
    var timeSec = 0
    var timeMin = 0
    private var running = false
    private var timer = Timer()
    private val ONE_SECOND = 1000
    private val mLap = MutableLiveData<String>()
    private var currTask: TimerTask? = null


    var currSong = 0

    init{

    }

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

    val currentTime2: MutableLiveData<Long> by lazy{
        MutableLiveData<Long>(0)
    }


    fun isRunning(): Boolean
    {
        return running
    }


    fun getTimeString(): MutableLiveData<Long>
    {

        return currentTime
    }

    fun getTimeString2(): MutableLiveData<Long>
    {

        return currentTime2
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

    lateinit var viewModelJob: Job

    //the scope is a must in order to ensure friendly behavior
    lateinit var ioScope: CoroutineScope



    fun slowLapCapture() {
        viewModelJob = Job()

        ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

        ioScope.launch {
            // launch new coroutine in background and continue
            // it will be automatically killed if viewmodel is destroyed.
            delay(1000L)
            updateLiveData(currentTime.value.toString())


        }

    }

    private fun updateLiveData(lap:String?){
        lap?.let { mLap.postValue( mLap.value+" "+it) }
    }


    //cancelling the coroutine
    fun fastCancel(){
        //viewModelJob.cancel()
        //you can also cancel the entire scope
        ioScope.cancel()


    }

    // -------------------------------


    // Option 3(outdated): AsyncTask -------------------------------------

    //an outdated way of performing short-lived async tasks
    class slowClearTask : AsyncTask<MutableLiveData<String>, Void, Void>() {


        override fun doInBackground(vararg params: MutableLiveData<String>?): Void? {
            //Delaying should happen in a java way
            Thread.sleep(1000)
            params[0]?.postValue("Laps:")
            return null
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }
    }


    // ConrolFragment calls this to reset the laps.
    fun slowClear(){
        slowClearTask().execute(mLap)
    }
}