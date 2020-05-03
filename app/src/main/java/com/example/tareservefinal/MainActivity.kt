package com.example.tareservefinal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        val model = this?.let { ViewModelProvider(this as FragmentActivity)[UserViewModel::class.java]}
        //MyFirebaseMessagingService(model.userId)
        val database = FirebaseDatabase.getInstance().reference
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
}
