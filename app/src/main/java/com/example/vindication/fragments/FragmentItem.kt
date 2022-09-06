package com.example.vindication.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.vindication.R
import com.example.vindication.dataClass.reminderItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_item.view.*


class FragmentItem : Fragment() {

    lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item, container, false)

        val shippment: reminderItem = FragmentItemArgs.fromBundle(requireArguments()).reminderItemParcel
        val fragmentMain = FragmentMain()
        database = Firebase.database("https://vindication-b5dee-default-rtdb.asia-southeast1.firebasedatabase.app").reference

        val owner = if (shippment.owner == fragmentMain.appOwner) "You" else "Not You"

        view.titleTVfrag.text = shippment.item
        view.ownerTVfrag.text = owner
        view.completionTVfrag.text = shippment.completion.toString()
        view.dateTVfrag.text = shippment.date
        view.ttIdTVfrag.text = shippment.ttid.toString()

        if (shippment.completion == false)
            view.doneFab.text = "Mark as done"
        else
            view.doneFab.text = "Unmark as done"


        if (shippment.ttid.toString() == "null")
            view.streamingFab.visibility = View.GONE
        else
            view.streamingFab.visibility = View.VISIBLE

        view.streamingFab.setOnClickListener { startStreaming(shippment) }

        view.doneFab.setOnClickListener {
            toggleCheck(shippment.item!!, !shippment.completion!!)
            Navigation.findNavController(view).navigate(R.id.action_fragmentItem_to_fragmentMain)
        }

        return view
    }

    fun toggleCheck(itemName: String, isChecked: Boolean) {
        database.child(itemName).child("completion").setValue(isChecked)
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

}