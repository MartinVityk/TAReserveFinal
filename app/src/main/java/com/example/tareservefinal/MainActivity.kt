package com.example.tareservefinal

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        val model = this?.let { ViewModelProvider(this as FragmentActivity)[UserViewModel::class.java]}
        MyFirebaseMessagingService(model.userId)
        val database = FirebaseDatabase.getInstance().reference
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
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                })

        val account = GoogleSignIn.getLastSignedInAccount(this)
        //findViewById<FragmentContainerView>(R.id.nav_host_fragment_container)



        if(account != null) {
            val navOption = NavOptions.Builder()
                    .setPopUpTo(R.id.classSelection, true).build()
        }

    }
}
