package com.example.tareservefinal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.tareservefinal.util.HashCode
import com.example.tareservefinal.util.ValidCallBack
import com.example.tareservefinal.util.ValidTACallback
import com.google.firebase.database.*

/**
 * Fragment that deals with Teaching Assistant Login
 */
class taLogin : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var database: DatabaseReference

    // Default Lifecycle method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ta_login, container, false)
    }

    // Handle Sign-In Logic
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emailEditText = view.findViewById(R.id.taLoginEmailEditText)
        database = FirebaseDatabase.getInstance().reference

        val signInButton: Button = view.findViewById(R.id.taSignInButton)
        signInButton.setOnClickListener {
            signInValidation(object: ValidCallBack {
                override fun onCallback(valid: Boolean) {
                    if (valid) {
                        checkTACredentials(object: ValidTACallback {
                            override fun onTACallback(registered: Boolean) {
                                if (registered) {
                                    it.findNavController().navigate(R.id.action_taLogin_to_classSelection)
                                } else {
                                    Toast.makeText(context, "You do not have TA Credentials.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                    else
                        Toast.makeText(context, "Email Address was not found in our system.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Check for valid entry in database
    private fun signInValidation(myCallback: ValidCallBack) {
        // Check for invalid hack
        val email = emailEditText.text.toString().replace("\\s".toRegex(), "")
        if (email == "") {
            myCallback.onCallback(false)
            return
        }

        val hashCode = HashCode().hashCode(email)
        val model =
            (activity?.let { ViewModelProvider(activity as FragmentActivity)[UserViewModel::class.java] })

        val studentRef = database.child("Users").child(hashCode)

        studentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            var valid = false
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    valid = true
                    model!!.setUserID(hashCode)
                    model!!.setIsTA("1")
                }
                myCallback.onCallback(valid)
            }
        })
    }

    // Check if user has TA Credentials
    private fun checkTACredentials(myCallback: ValidTACallback) {
        val email = emailEditText.text.toString().replace("\\s".toRegex(), "")
        val hashCode = HashCode().hashCode(email)
        val studentRef = database.child("Users").child(hashCode).child("isTA")

        studentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            var valid = false
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == "1") {
                    valid = true
                }

                myCallback.onTACallback(valid)
            }
        })
    }
}
