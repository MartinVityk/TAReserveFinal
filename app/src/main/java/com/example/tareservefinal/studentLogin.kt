package com.example.tareservefinal

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.tareservefinal.util.User
import com.google.firebase.database.*
import org.w3c.dom.Text

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

        val signInButton: Button = view.findViewById(R.id.studentSignInButton)
        signInButton.setOnClickListener {
            if (signInValidation())
                it.findNavController().navigate(R.id.action_studentLogin_to_classSelection)
            else
                Toast.makeText(context, "Email Address was not found in our system.", Toast.LENGTH_SHORT).show()
        }

        val registerButton: Button = view.findViewById(R.id.studentCreateAccButton)
        registerButton.setOnClickListener { it.findNavController().navigate(R.id.action_studentLogin_to_studentRegister) }
    }

    // Helper function to handle sign in validation
    private fun signInValidation():Boolean {
        var valid = false
        val model = (activity?.let { ViewModelProvider(activity as FragmentActivity)[UserViewModel::class.java]})

        // TODO: Change below to handle async operation

        // Check for valid entry in database
        database = FirebaseDatabase.getInstance().reference
        val studentRef = database.child("Users").child(editText.text.toString())

        studentRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) { // We assume all User objects have children (so that this check works)
                    valid = true
                    model!!.setUserID(editText.text.toString())
                    model!!.setIsTA(getTAValue(studentRef))
                }
            }
        })

        return valid
    }

    // Helper function to scrape the isTA value from a database entry
    private fun getTAValue(dbRef: DatabaseReference):String {
        // TODO: Fix below due to asynchronous listener() call

        var isTA = "0"
        val taRef = dbRef.child("isTA").child("0")

        taRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                isTA = snapshot.value as String
            }
        })

        return isTA
    }

}


