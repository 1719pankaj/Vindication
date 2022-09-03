package com.example.vindication

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    //TODO Fix the bug were is send an smpty string to Firebase for no specified TTID

    private lateinit var database: DatabaseReference
    private lateinit var mAdapter: ItemListAdapter
    var itemList: ArrayList<reminderItem> = ArrayList()
//    var itemListCpy: ArrayList<reminderItem> = ArrayList()
    val appOwner: String = "Pankaj Kumar Roy"
//    val appOwner: String = "ktress"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        database = Firebase.database("https://vindication-b5dee-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        Firebase.database.setPersistenceEnabled(true)

        addFab.setOnClickListener { addItemDialog() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = ItemListAdapter(itemList, this)
        getTopLevelItems()
//        Log.i("TAG", "onCreate: $itemList")
        recyclerView.adapter = mAdapter

    }

    fun getTopLevelItems(): ArrayList<reminderItem> {
        var oldCount = itemList.size
        database.addValueEventListener(object : ValueEventListener {
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
//                mAdapter.notifyDataSetChanged()
                if(itemList.size == oldCount){
                    Log.i("TAGGER", "CHANGED")
                    mAdapter.notifyItemChanged(itemList.size)
                } else if (itemList.size > oldCount) {
                    Log.i("TAGGER", "INSERTED")
                    mAdapter.notifyItemInserted(itemList.size)
                } else if(itemList.size < oldCount){
                    Log.i("TAGGER", "REMOVED")
                    mAdapter.notifyItemRemoved(itemList.size-1)
                }
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
        val ttIdTV = dialogView.findViewById<android.widget.EditText>(R.id.ttIdTV)
        dialogBuilder.setPositiveButton("Add") { dialog, which ->
            val item = itemNameET.text.toString()
            val owner = appOwner
            val completion = false
            val ttid = if(ttIdTV.text.toString() == "") "null" else ttIdTV.text.toString()

            val scram: reminderItem = reminderItem(item, owner, completion, Date().toString(), ttid)
            addPendingitem(scram)
            Toast.makeText(applicationContext, "Item Added $", Toast.LENGTH_LONG).show()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_LONG).show()
        }
        val b = dialogBuilder.create()
        b.show()
    }

    fun addPendingitem(scram: reminderItem) {
        scram.item?.let {
            database.child(it).setValue(scram)
                .addOnSuccessListener { Toast.makeText(this, "$it added", Toast.LENGTH_SHORT).show() }
        }
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
        val ttidTV = dialogView.findViewById<android.widget.TextView>(R.id.ttIdTV)

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

        ttidTV.setText(itm.ttid)
        Log.i("TAGGER", "infoDialog: ${itm.date}")

        dialogBuilder.setPositiveButton("DISMISS") { dialog, which -> }
        val b = dialogBuilder.create()
        b.show()
    }

    fun startStreaming(itm: reminderItem): Boolean {
        val ttId = itm.ttid
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.setDataAndType(Uri.parse("https://www.2embed.to/embed/imdb/movie?id=$ttId"), "text/html")
        Log.i("TAGGER", "startStreaming: $ttId")
        startActivity(intent)
        return true
    }

    fun toggleCheck(itemName: String, isChecked: Boolean) {
    Log.i("TAGGER", "toggleCheck: $itemName")
        if(isChecked) {
            database.child(itemName).child("completion").setValue(true)
        }
        else {
            database.child(itemName).child("completion").setValue(false)
        }
    }

}