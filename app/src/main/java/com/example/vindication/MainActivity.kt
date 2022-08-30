package com.example.vindication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_item_dialog.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var mAdapter: ItemListAdapter
    var itemList: ArrayList<reminderItem> = ArrayList()
//    val appOwner: String = "Pankaj Kumar Roy"
    val appOwner: String = "ktress"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        database = Firebase.database("https://vindication-b5dee-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        Firebase.database.setPersistenceEnabled(true)

//        addFab.setOnClickListener { addPendingitem("Tere Naam", "Pankaj Kumar Roy", false) }
        addFab.setOnClickListener { addItemDialog() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = ItemListAdapter(itemList, this)
        getTopLevelItems()
//        Log.i("TAG", "onCreate: $itemList")
        recyclerView.adapter = mAdapter

    }

    fun getTopLevelItems(): ArrayList<reminderItem> {

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                itemList.clear()
                for (i in dataSnapshot.children) {
                    val item = i.child("item").value.toString()
                    val owner = i.child("owner").value.toString()
                    val completion = i.child("completion").value.toString().toBoolean()
                    val date = i.child("date").value.toString()

                    itemList.add(reminderItem(item, owner, completion, date))
                }
                mAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
        return itemList
    }

    //Create a dialog box to add a new item
    fun addItemDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.add_item_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Add Item")
        val itemNameET = dialogView.findViewById<android.widget.EditText>(R.id.addItemNameET)
        dialogBuilder.setPositiveButton("Add") { dialog, which ->
            val item = itemNameET.text.toString()
            val owner = appOwner
            val completion = false
            addPendingitem(item, owner, completion)
            Toast.makeText(applicationContext, "Item Added", Toast.LENGTH_LONG).show()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_LONG).show()
        }
        val b = dialogBuilder.create()
        b.show()
    }

    fun addPendingitem(item:String, owner: String, completion: Boolean) {
        val scram = reminderItem(item, owner, completion, Date().toString())

        database.child(item).setValue(scram)
            .addOnSuccessListener { Toast.makeText(this, "$item added", Toast.LENGTH_SHORT).show() }
    }

    fun removePendingitem(item:String): Boolean {
        var res = true

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("DELETE $item?")
        dialogBuilder.setMessage("This action cannot be undone.")
        dialogBuilder.setPositiveButton("DELETE") { dialog, which ->
            database.child(item).removeValue()
                .addOnSuccessListener { Toast.makeText(this, "$item removed", Toast.LENGTH_SHORT).show() }
            res = true
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_LONG).show()
            res = false
        }
        val b = dialogBuilder.create()
        b.show()

        return res
    }

    fun infoDialog(itm: reminderItem) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.info_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("info")
        val itemTV = dialogView.findViewById<android.widget.TextView>(R.id.itemTV)
        val ownerTV = dialogView.findViewById<android.widget.TextView>(R.id.ownerTV)
        val completionTV = dialogView.findViewById<android.widget.TextView>(R.id.completionTV)
        val dateTV = dialogView.findViewById<android.widget.TextView>(R.id.dateTV)

        Log.i("TAGGER", itm.toString())

        itemTV.setText(itm.item)
                                          //If I remove these👇👇 Underscores, it stops showing the status and date, I hove no idea why and I can't be arsed to fix it.
        ownerTV.setText(if(itm.owner == appOwner) "Owner - You__" else "Owner - Not You")
        if(itm.completion.toString().toBoolean())
            completionTV.setText("Status - Done")
        else {
            completionTV.setText("Status - Pending")
            Log.i("TAGGER", "infoDialog: ${itm.completion.toString().toBoolean()}")
        }
        dateTV.setText(itm.date)
        Log.i("TAGGER", "infoDialog: ${itm.date}")

        dialogBuilder.setPositiveButton("DISMISS") { dialog, which -> }
        val b = dialogBuilder.create()
        b.show()
    }

    fun toggleCheck(itemName: String, isChecked: Boolean) {
        if(isChecked) {
            database.child(itemName).child("completion").setValue(true)
        }
        else {
            database.child(itemName).child("completion").setValue(false)
        }
    }

}