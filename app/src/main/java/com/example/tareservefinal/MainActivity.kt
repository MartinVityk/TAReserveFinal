package com.example.tareservefinal

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.tareservefinal.util.HashCode
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var timerCompletionReceiver: TimerBroadcastReciever? = null
    var model: UserViewModel? = null
    var timerDialog: Dialog? = null
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.flags == 100)
        {
            println(intent.flags.toString()+"OKK")
            supportFragmentManager.popBackStack()
            //finish()
            //return
        }
        createNotificationChannel()

        timerDialog = Dialog(this)
        model = this?.let { ViewModelProvider(this as FragmentActivity)[UserViewModel::class.java]}
        //MyFirebaseMessagingService(this)
        //ViewModelProvider(XXX)


        database = FirebaseDatabase.getInstance().reference
        val account = GoogleSignIn.getLastSignedInAccount(this)

        setContentView(R.layout.activity_main)
        val navHostFragment = fragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_host)
        //R.id.men
       // val toolbar = findViewById<Toolbar>(R.id.toolbar)
        //setSupportActionBar(toolbar)

        //graph.setDefaultArguments(intent.extras)
//or
//graph.startDestination = R.id.fragment2



        if(account != null) {
            println("BOB")
            graph.startDestination = R.id.classSelection
            val hashCode = HashCode().hashCode(account!!.email!!)
            model?.setUserID(hashCode)

        }
        else
        {
            println("WHAT")
        }


        navHostFragment.navController.graph = graph
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        //Log.w(TAG, "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    //database.child("Users").child(model!!.userId).child("UserToken").setValue(token)
                    model!!.userToken = token!!

                    // Log and toast
                    val msg = "test"
                    //Log.d(TAG, msg)
                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                })

        timerCompletionReceiver = TimerBroadcastReciever(this)
        registerReceiver(timerCompletionReceiver!!, IntentFilter("START_TIMER"))
    }


    override fun onDestroy() {
        unregisterReceiver(timerCompletionReceiver!!)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater;
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navHostFragment = fragment as NavHostFragment
       // val controllerFrag = navHostFragment.navController.navigate(R.id.)
        //when(item.itemId) {
           // R.id.logout -> print("show edit classes view")
           // R.id.edit_classes -> print("show edit classes view")
        //}
        return super.onOptionsItemSelected(item)
    }
    fun outputModel():UserViewModel
    {
        return model!!
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("123", "0", importance).apply {
                description = "all notifications"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showTimer()
    {
        timerDialog!!.setContentView(R.layout.timer_pop_up)
        timerDialog!!.show()
        timerDialog!!.findViewById<Button>(R.id.declineButton).setOnClickListener {
            model!!.reset()
            dequeueYourself()
            timerDialog!!.dismiss()
        }
        timerDialog!!.findViewById<Button>(R.id.acceptButton).setOnClickListener {
            model!!.reset()
            timerDialog!!.dismiss()
        }
        model!!.startStop()

        model?.getTimeString()?.observe(this, androidx.lifecycle.Observer<Long> { time ->
            var checkSecs = 60-time

            if(checkSecs == (-1).toLong())
            {
                model!!.reset()
                dequeueYourself()
                timerDialog!!.dismiss()
            }
            timerDialog!!.findViewById<TextView>(R.id.timerTime).text = checkSecs.toString()
        })

    }

    private fun dequeueYourself()
    {
        var userRef = database.child("Users").child(model!!.reservedTA).child("TAData")

        userRef.orderByValue().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var x = 0

                dataSnapshot.child("StudentList").children.forEach {
                    if(x == 0)
                    {
                        userRef.child("dequeuedStudent").setValue(1)
                        userRef.child(it.key!!).removeValue()
                    }
                    x++
                }
            }
        })
    }

    fun removeAllFragments(fragmentManager2:FragmentManager) {
        while (fragmentManager2.backStackEntryCount > 0) {
            fragmentManager2.popBackStack();
        }
    }
}
