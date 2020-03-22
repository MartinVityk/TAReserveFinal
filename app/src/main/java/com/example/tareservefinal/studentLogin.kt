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
import com.google.firebase.database.*

/**
 * Fragment that deals with Student Login
 */
class studentLogin : Fragment() {

    private lateinit var editText: EditText
    private lateinit var database: DatabaseReference

    // Default Lifecycle method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student_login, container, false)
    }

    // Setup Button onClick() actions
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editText = view.findViewById(R.id.studentEmailEditText)
        database = FirebaseDatabase.getInstance().reference

        val signInButton: Button = view.findViewById(R.id.studentSignInButton)
        signInButton.setOnClickListener {
            signInValidation(object: ValidCallBack {
                override fun onCallback(valid: Boolean) {
                    if (valid)
                        it.findNavController().navigate(R.id.action_studentLogin_to_classSelection)
                    else
                        Toast.makeText(context, "Email Address was not found in our system.", Toast.LENGTH_SHORT).show()
                }
            })

        }

        val registerButton: Button = view.findViewById(R.id.studentCreateAccButton)
        registerButton.setOnClickListener { it.findNavController().navigate(R.id.action_studentLogin_to_studentRegister) }
    }

    // Check for valid entry in database
    private fun signInValidation(myCallback: ValidCallBack) {
        // Check for invalid hack
        val email = editText.text.toString().replace("\\s".toRegex(), "")
        if (email == "") {
            myCallback.onCallback(false)
            return
        }

        val hashCode = HashCode().hashCode(email)
        val model = (activity?.let { ViewModelProvider(activity as FragmentActivity)[UserViewModel::class.java]})
        val studentRef = database.child("Users").child(hashCode)

        studentRef.addListenerForSingleValueEvent(object: ValueEventListener {
            var valid = false
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    valid = true
                    model!!.setUserID(hashCode)
                    model!!.setIsTA("0")
                }
                myCallback.onCallback(valid)
            }
        })
    }
}