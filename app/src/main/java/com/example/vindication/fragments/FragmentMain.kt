package com.example.vindication.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vindication.R
import com.example.vindication.adapters.ItemListAdapter
import com.example.vindication.dataClass.reminderItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.util.*

class FragmentMain : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var mAdapter: ItemListAdapter
    var itemList: ArrayList<reminderItem> = ArrayList()
    //    var itemListCpy: ArrayList<reminderItem> = ArrayList()
    val appOwner: String = "Pankaj Kumar Roy"
//    val appOwner: String = "ktress"




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        database = Firebase.database("https://vindication-b5dee-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        Firebase.database.setPersistenceEnabled(true)

        view.addFab.setOnClickListener { addItemDialog() }

        view.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = ItemListAdapter(itemList, this)
        getTopLevelItems()
//        Log.i("TAG", "onCreate: $itemList")
        view.recyclerView.adapter = mAdapter

        return view
    }

    fun getTopLevelItems() {
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                itemList.clear()
                for (i in dataSnapshot.children) {
                    val item = i.child("item").value.toString()
                    val owner = i.child("owner").value.toString()
                    val completion = i.child("completion").value.toString().toBoolean()
                    val date = i.child("date").value.toString()
                    val ttid = i.child("ttid").value.toString()
                    itemList.add(reminderItem(item, owner, completion, date, ttid))
                }
                mAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    //Create a dialog box to add a new item
    fun addItemDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.add_item_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Add Item")
        val itemNameET = dialogView.findViewById<android.widget.EditText>(R.id.addItemNameET)
        val ttIdTV = dialogView.findViewById<android.widget.EditText>(R.id.ttIdTVfrag)
        dialogBuilder.setPositiveButton("Add") { dialog, which ->
            val item = itemNameET.text.toString()
            if (item  != "") {
                val owner = appOwner
                val completion = false
                val ttid = if (ttIdTV.text.toString() == "") "null" else ttIdTV.text.toString()

                val scram: reminderItem =
                    reminderItem(item, owner, completion, Date().toString(), ttid)
                addPendingitem(scram)
                Toast.makeText(context, "Item Added $", Toast.LENGTH_LONG).show()
            }
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
        }
        val b = dialogBuilder.create()
        b.show()
    }

    fun addPendingitem(scram: reminderItem) {
        scram.item?.let {
            database.child(it).setValue(scram)
                .addOnSuccessListener { Toast.makeText(context, "$it added", Toast.LENGTH_SHORT).show() }
        }
    }

    fun removePendingitem(item:String): Boolean {
        var res = true

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("DELETE $item?")
        dialogBuilder.setMessage("This action cannot be undone.")
        dialogBuilder.setPositiveButton("DELETE") { dialog, which ->
            database.child(item).removeValue()
                .addOnSuccessListener { Toast.makeText(context, "$item removed", Toast.LENGTH_SHORT).show() }
            res = true
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
            res = false
        }
        val b = dialogBuilder.create()
        b.show()

        return res
    }


    fun navigateToItemFrag(action: NavDirections) {
        view?.let { Navigation.findNavController(it).navigate(action) }
    }


}