package com.example.tareservefinal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.*

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

    fun updateText(nextStudent:TextView, currStudent:TextView, ref:Query)
    {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var x = 0
                dataSnapshot.children.forEach {
                    if(x == 0)
                    {
                        database.child("Users").child(it.key!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(databaseError: DatabaseError) {
                                }
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    currStudent.text = "Current Student is: " + dataSnapshot.child("Name").value.toString()
                                }
                            })
                    }
                    if(x == 1)
                    {
                        database.child("Users").child(it.key!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(databaseError: DatabaseError) {
                                }
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    nextStudent.text = "Next Student is: " + dataSnapshot.child("Name").value.toString()
                                }
                            })
                    }
                    x++
                }
                if(x==1)
                {
                    nextStudent.text = "No Next Student"
                }
                if(x==0)
                {
                    nextStudent.text = "No Students In Line"
                }
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currStudent = view.findViewById<TextView>(R.id.currStudent)
        val nextStudent = view.findViewById<TextView>(R.id.nextStudent)
        val takeNextStudent = view.findViewById<Button>(R.id.nextInLine)

        val model = (activity?.let { ViewModelProvider(activity as FragmentActivity)[UserViewModel::class.java]})
        database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("Users").child(model!!.userId).child("TAData").child("StudentList").orderByValue()
        updateText(currStudent, nextStudent, userRef)


        takeNextStudent.setOnClickListener {
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var x = 0
                        dataSnapshot.children.forEach {
                            if(x == 0)
                            {
                                it.ref.removeValue()
                            }
                            x++
                        }
                        updateText(currStudent, nextStudent, userRef)
                    }

                })
            database.child("Users").child(model!!.userId).child("TAData").
                addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var numStud = dataSnapshot.child("NumStudents").value.toString().toInt()
                    dataSnapshot.child("NumStudents").ref.setValue(numStud-1)
                }

            })
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
