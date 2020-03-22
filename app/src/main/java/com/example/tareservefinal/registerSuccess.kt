package com.example.tareservefinal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

/**
 * Fragment that handles the notification to the User of a successful account creation
 */
class registerSuccess : Fragment() {

    // Default Lifecycle method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_success, container, false)
    }

    // Handle navigation back to Student Sign-In Fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val returnButton: Button = view.findViewById(R.id.returnToSignInButton)
        returnButton.setOnClickListener { it.findNavController().navigate(R.id.action_registerSuccess_to_studentLogin) }
    }
}
