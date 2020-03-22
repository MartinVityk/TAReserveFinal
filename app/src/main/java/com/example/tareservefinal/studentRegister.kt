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
import androidx.navigation.findNavController
import com.example.tareservefinal.util.HashCode
import com.example.tareservefinal.util.ValidCallBack
import com.google.firebase.database.*

/**
 * Fragment that handles with Student Registering
 */
class studentRegister : Fragment() {

    private val MAX_NAME_LENGTH:Int = 95

    private lateinit var emailEditText: EditText
    private lateinit var nameEditText : EditText
    private lateinit var database: DatabaseReference

    // Default Lifecycle method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student_register, container, false)
    }

    // Setup Button onClick() actions
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emailEditText = view.findViewById(R.id.studentRegisterEmailEditText)
        nameEditText  = view.findViewById(R.id.studentRegisterNameEditText)
        database = FirebaseDatabase.getInstance().reference

        val registerButton: Button = view.findViewById(R.id.studentRegisterCreateButton)
        registerButton.setOnClickListener {
            createValidation(object: ValidCallBack {
                override fun onCallback(valid: Boolean) {
                    if (valid) it.findNavController().navigate(R.id.action_studentRegister_to_registerSuccess)
                }
            })

        }
    }

    // Helper function to check if input is valid
    private fun createValidation(myCallback: ValidCallBack) {
        // Check for valid email first
        val email = emailEditText.text.toString()
        if (!email.isValidEmail()) {
            Toast.makeText(context, "Invalid Email Address", Toast.LENGTH_SHORT).show()
            myCallback.onCallback(false)
            return
        }

        // Check for max length name
        val name = nameEditText.text.toString()
        if (name.length > MAX_NAME_LENGTH) {
            Toast.makeText(context, "Name is too long (Maximum 95 characters).", Toast.LENGTH_SHORT).show()
            myCallback.onCallback(false)
            return
        }

        // Check if email already exists in the database
        val hashCode = HashCode().hashCode(email)
        val studentRef = database.child("Users").child(hashCode)
        studentRef.addListenerForSingleValueEvent(object: ValueEventListener {
            var valid = false
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    valid = false
                    Toast.makeText(context, "Email Already Exists in the Database", Toast.LENGTH_SHORT).show()
                } else {
                    valid = true
                    var map:HashMap<String, String> = HashMap()
                    map["Class1"] = "ClassID1"

                    studentRef.child("ClassList").setValue(map)
                    studentRef.child("Name").setValue(name)
                    studentRef.child("Email").setValue(email)
                    studentRef.child("isTA").setValue("1")
                }

                myCallback.onCallback(valid)
            }
        })
    }

    // Helper function to check for valid Email
    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
