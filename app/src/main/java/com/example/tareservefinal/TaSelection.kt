package com.example.tareservefinal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TaSelection.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaSelection : Fragment() {
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
        return inflater.inflate(R.layout.fragment_ta_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        database = FirebaseDatabase.getInstance().reference


        val IdArrayList = ArrayList<String>()
        val classArrayList = ArrayList<String>()

        val model = activity?.let { ViewModelProvider(activity as FragmentActivity)[UserViewModel::class.java]}

        val recyclerView = view.findViewById<RecyclerView>(R.id.taListView)

        val adapter = MovieListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val classRef = database.child("Classes").child(param1!!.substring(0, param1!!.indexOf(" "))).child(param1!!)
            .child("TAList").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    IdArrayList.clear()
                    dataSnapshot.children.forEach {
                        IdArrayList.add(it.value.toString())
                    }
                    adapter.setIdArray(IdArrayList)

                }
            })
        classRef.run {  }

        val innerClassRef = database.child("Users").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                classArrayList.clear()
                for(id in IdArrayList)
                {
                    classArrayList.add(dataSnapshot.child(id).child("Name").value.toString())
                }
                adapter.setClasses(classArrayList)
            }
        })
        innerClassRef.run {  }
    }

    inner class MovieListAdapter():
        RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>(){
        private var classes = emptyList<String>()
        private var idArray = emptyList<String>()

        override fun getItemCount(): Int {

            return classes.size
        }

        fun setClasses(classes: ArrayList<String>)
        {
            this.classes = classes
            notifyDataSetChanged()
        }

        fun setIdArray(idArray: ArrayList<String>)
        {
            this.idArray = idArray
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {


            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card_view, parent, false)
            return MovieViewHolder(v)
        }

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

            holder.view.findViewById<TextView>(R.id.itemName).text=classes[position]

            holder.itemView.setOnClickListener(){
                it.findNavController().navigate(R.id.action_taSelection_to_taInfo, bundleOf("param1" to idArray[position]))
            }

        }


        inner class MovieViewHolder(val view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
            override fun onClick(view: View?){

                if (view != null) {
                }
            }

        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TaSelection.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TaSelection().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
