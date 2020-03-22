package com.example.tareservefinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController


/**
 * Fragment that deals with login selection (This should be the first thing rendered to the user)
 */
class LoginScreen : Fragment() {

    // Default Lifecycle method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_screen, container, false)
    }

    // Setup button onClick() to control navigation
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val studentButton: Button = view.findViewById(R.id.studentLoginButton)
        studentButton.setOnClickListener { it.findNavController().navigate(R.id.action_loginScreen_to_studentLogin) }

        val taButton: Button = view.findViewById(R.id.taSignInButton)
        taButton.setOnClickListener { it.findNavController().navigate(R.id.action_loginScreen_to_taLogin) }
    }

}
