package com.example.tareservefinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tareservefinal.util.HashCode
import com.google.firebase.database.*


/**
 * A simple [Fragment] subclass.
 */
class classRemoval : Fragment() {

    private lateinit var loadingTextView: TextView
    private lateinit var cancelButton:Button
    private lateinit var deleteButton:Button
    private var count:Int = 0

    val classesSelected = HashMap<String, Boolean>()

    private lateinit var database: DatabaseReference
    private var model:UserViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class_removal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        count = 0
        model = (activity?.let { ViewModelProvider(activity as FragmentActivity)[UserViewModel::class.java]})

        loadingTextView = view.findViewById(R.id.removeCourseLoadingTextView)

        cancelButton = view.findViewById(R.id.cancelCourseRemovalButton)
        cancelButton.setOnClickListener { view.findNavController().popBackStack() }

        deleteButton = view.findViewById(R.id.deleteCourseButton)
        deleteButton.isEnabled = false
        setUpDeleteButton(view)

        // Recycler View Initialization Here
        val IdArrayList = ArrayList<String>()
        val classArrayList = ArrayList<String>()
        database = FirebaseDatabase.getInstance().reference

        val recyclerView = view.findViewById<RecyclerView>(R.id.removeClassListView)

        val adapter = ClassListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val classRef = database.child("Users").child(model!!.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    model!!.isTA = dataSnapshot.child("isTA").value.toString()
                    IdArrayList.clear()
                    dataSnapshot.child("ClassList").children.forEach {
                        IdArrayList.add(it.value.toString())
                    }
                    adapter.setIdArray(IdArrayList)

                }
            })
        classRef.run {  }

        val innerClassRef = database.child("Classes").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                classArrayList.clear()
                for(id in IdArrayList)
                {
                    if(id.contains(" "))
                        classArrayList.add(dataSnapshot.child(id.substring(0, id.indexOf(" "))).child(id).key.toString())
                }
                if (classArrayList.size == 0) loadingTextView.text = "No courses to remove"
                else loadingTextView.visibility = View.GONE

                adapter.setClasses(classArrayList)
            }
        })
        innerClassRef.run {  }

    }


    private fun setUpDeleteButton(view: View) {
        deleteButton.setOnClickListener {
            val keys:Set<String> = classesSelected.keys

            for (e:String in keys) {
                if (classesSelected[e]!!) {
                    var query = database.child("Users").child(model!!.userId)
                        .child("ClassList").child(HashCode().hashCode(e))

                    query.removeValue()
                }
            }
            view.findNavController().popBackStack()
        }
    }

    private fun updateCount(increment: Boolean) {
        if (increment) count += 1
        else count -= 1

        deleteButton.isEnabled = (count > 0)
    }

    inner class ClassListAdapter:
        RecyclerView.Adapter<ClassListAdapter.ClassViewHolder>(){
        private var classes = emptyList<String>()
        private var idArray = emptyList<String>()

        override fun getItemCount(): Int {

            return classes.size
        }

        fun setClasses(classes: List<String>)
        {
            this.classes = classes
            notifyDataSetChanged()
        }

        fun setIdArray(idArray: List<String>)
        {
            this.idArray = idArray
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {


            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card_view_removal, parent, false)
            return ClassViewHolder(v)
        }

        override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
            holder.view.findViewById<TextView>(R.id.itemName).text=classes[position]

            val checkbox:CheckBox = holder.itemView.findViewById(R.id.removeCheckbox)

            checkbox.setOnClickListener {
                classesSelected[classes[position]] = checkbox.isChecked
                updateCount(checkbox.isChecked)
            }
        }


        inner class ClassViewHolder(val view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
            override fun onClick(view: View?){

                if (view != null) {
                }
            }

        }


    }

}
