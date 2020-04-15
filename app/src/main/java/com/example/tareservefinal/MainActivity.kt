package com.example.tareservefinal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        //findViewById<FragmentContainerView>(R.id.nav_host_fragment_container)



        if(account != null) {
            val navOption = NavOptions.Builder()
                    .setPopUpTo(R.id.classSelection, true).build()
        }

    }
}
