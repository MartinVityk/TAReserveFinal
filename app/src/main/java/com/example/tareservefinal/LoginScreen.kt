package com.example.tareservefinal

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.tareservefinal.util.HashCode
//import androidx.test.orchestrator.junit.BundleJUnitUtils.getResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


/**
 * Fragment that deals with login selection (This should be the first thing rendered to the user)
 */
class LoginScreen : Fragment() {

    private lateinit var database: DatabaseReference
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
        database = FirebaseDatabase.getInstance().reference
        studentButton.setOnClickListener { it.findNavController().navigate(R.id.action_loginScreen_to_studentLogin) }

        val taButton: Button = view.findViewById(R.id.taSignInButton)
        taButton.setOnClickListener { it.findNavController().navigate(R.id.action_loginScreen_to_taLogin) }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode("886038774832-lja4e3reta073i1iup90qmo49aeeepuc.apps.googleusercontent.com")
                .requestEmail()
                .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(activity!!, gso);

        view.findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener{
            val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
            println("HELLO")
            startActivityForResult(signInIntent, 1)

            var resultCode = 1

            onActivityResult(1, resultCode, signInIntent)
            val account = GoogleSignIn.getLastSignedInAccount(activity!!)
            println("HELLO"+ account.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) { // The Task returned from this call is always completed, no need to attach
// a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val hashCode = HashCode().hashCode(account!!.email!!)
            val model = (activity?.let { ViewModelProvider(activity as FragmentActivity)[UserViewModel::class.java]})
            model!!.setUserID(hashCode)
            model!!.setIsTA("0")

            val studentRef = database.child("Users").child(hashCode)
            studentRef.child("Name").setValue(account!!.displayName)
            studentRef.child("Email").setValue(account!!.email)
            studentRef.child("isTA").setValue("0")


            view!!.findNavController().navigate(R.id.action_loginScreen_to_classSelection)
            // Signed in successfully, show authenticated UI.

        } catch (e: ApiException) { // The ApiException status code indicates the detailed failure reason.
// Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

}
