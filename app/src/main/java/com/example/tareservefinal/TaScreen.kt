package com.example.tareservefinal

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TaScreen.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaScreen : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var progressBar: ProgressBar
    var model: UserViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ta_screen, container, false)
    }

    private fun updateText( nextStudent:TextView,currStudent:TextView, ref:Query)
    {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.childrenCount.toInt() == 0)
                {
                    nextStudent.text = "No Students in Line!"

                }
                else
                {
                    nextStudent.text = ""+dataSnapshot.childrenCount+" Students in Line"
                }
                if(model!!.studentServe !="null") {
                    database.child("Users").child(model!!.studentServe).child("Name").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(databaseError: DatabaseError) {
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            currStudent.text = "Serving: " + dataSnapshot.value.toString()
                        }
                    })
                }
                else {
                    currStudent.text = ""
                }
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nextStudent = view.findViewById<TextView>(R.id.nextStudent)
        val currStudent = view.findViewById<TextView>(R.id.nextStudent2)
        val takeNextStudent = view.findViewById<Button>(R.id.nextInLine)
        val taName = view.findViewById<TextView>(R.id.taName)
        val taDesc = view.findViewById<TextView>(R.id.taDescript)
        val taTime = view.findViewById<TextView>(R.id.taTimes)
        progressBar = view.findViewById<ProgressBar>(R.id.taInfoProgressBar2)

        model = (activity?.let { ViewModelProvider(activity as FragmentActivity)[UserViewModel::class.java]})
        database = FirebaseDatabase.getInstance().reference
        var userRef = database.child("Users").child(model!!.userId).child("TAData").child("StudentList").orderByValue()
        var userRef2 = database.child("Users").child(model!!.userId).child("TAData")


        val switchViewText = view.findViewById<ImageView>(R.id.profileEdit)
        switchViewText.setOnClickListener {
            view.findNavController().navigate(R.id.action_taScreen_to_taImageSelection)
        }

        database.child("Users").child(model!!.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val taTimeString = dataSnapshot.child("TAData").child("Schedule").value.toString()
                        val taDescString = dataSnapshot.child("TAData").child("Description").value.toString()

                        taName.text = dataSnapshot.child("Name").value.toString()
                        taTime.text = "Office Hours: $taTimeString"
                        taDesc.text = "Note: $taDescString"
                    }
                })

        loadProfilePic(view)


        userRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var x = dataSnapshot.childrenCount.toInt()
                        println("BILL"+dataSnapshot.value.toString())

                        if(x == 0)
                        {
                            nextStudent.text = "No Students in Line!"
                        }
                        else
                        {
                            nextStudent.text = ""+x+" Students in Line"
                        }
                    }
                }
        )

        takeNextStudent.setOnClickListener {

            database.child("Users").child(model!!.userId).child("TAData").child("StudentList")
                    .addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onCancelled(databaseError: DatabaseError) {
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var x = 0
                            if(dataSnapshot.childrenCount.toInt() == 1)
                            {
                                database.child("Users").child(model!!.userId).child("TAData").child("NumStudents").setValue(0)
                            }
                            dataSnapshot.children.forEach {

                                if(x == 0)
                                {
                                    model!!.studentServe = it.key.toString()
                                }
                                x++
                            }
                            userRef2.child("StudentList").child(model!!.studentServe).removeValue()
                            if(x == 0)
                            {
                                model!!.studentServe = "null"
                                currStudent.text = ""
                            }
                            else
                            {
                                database.child("Users").child(model!!.studentServe).child("Name").addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(databaseError: DatabaseError) {
                                    }

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        currStudent.text = "Serving: " + dataSnapshot.value.toString()
                                    }
                                })
                            }
                        }

                    })
        }
        updateText( nextStudent,currStudent, userRef)

    }

    private fun loadProfilePic(view: View) {
        storage = Firebase.storage
        val model = (activity?.let { ViewModelProvider(activity as FragmentActivity)[UserViewModel::class.java]})

        val imageEndpoint = "gs://tareservefinal.appspot.com/" + model!!.userId + ".JPG"

        val gsReference = storage.getReferenceFromUrl(imageEndpoint)
        val ONE_MEGABYTE: Long = 1024 * 1024 * 5
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            val imageView = view.findViewById<ImageView>(R.id.taImage2)
            imageView.setImageBitmap(bmp)
            progressBar.visibility = View.GONE
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TaScreen.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TaScreen().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
