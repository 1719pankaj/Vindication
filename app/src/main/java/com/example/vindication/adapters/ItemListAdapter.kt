package com.example.vindication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.vindication.R
import com.example.vindication.dataClass.reminderItem
import com.example.vindication.fragments.FragmentMain
import com.example.vindication.fragments.FragmentMainDirections

class ItemListAdapter(
    private val reminderItemSet: ArrayList<reminderItem>,
    private val context: FragmentMain
    ): RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    val mActivity: FragmentMain =  context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.itemTextView)
        val playableIV: ImageView = view.findViewById(R.id.playableIV)
        val cardView: CardView = view.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemText = reminderItemSet[position].item.toString()
        holder.textView.text = "<generic>  |  $itemText"
        holder.textView.setOnClickListener {
            val action = FragmentMainDirections.actionFragmentMainToFragmentItem(reminderItemSet[position])
            mActivity.navigateToItemFrag(action)
        }

        if(reminderItemSet[position].ttid != "null"){
            holder.playableIV.visibility = View.VISIBLE
        } else {
            holder.playableIV.visibility = View.GONE
        }

        if(reminderItemSet[position].owner == "Pankaj Kumar Roy" && reminderItemSet[position].completion == false){
            holder.cardView.setBackgroundResource(R.drawable.blue_gradient)
            holder.textView.setTextColor(context.resources.getColor(R.color.white))
            holder.playableIV.setColorFilter(context.resources.getColor(R.color.white))

        } else if(reminderItemSet[position].owner == "Pankaj Kumar Roy" && reminderItemSet[position].completion == true){
            holder.cardView.setBackgroundResource(0)
            holder.textView.setTextColor(context.resources.getColor(R.color.apnaBlack))
            holder.textView.paintFlags = holder.textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.playableIV.setColorFilter(context.resources.getColor(R.color.apnaBlack))


        } else if(reminderItemSet[position].owner == "ktress" && reminderItemSet[position].completion == false) {
            holder.cardView.setBackgroundResource(R.drawable.pink_gradient)
            holder.textView.setTextColor(context.resources.getColor(R.color.white))
            holder.playableIV.setColorFilter(context.resources.getColor(R.color.white))

        } else if(reminderItemSet[position].owner == "ktress" && reminderItemSet[position].completion == true) {
            holder.cardView.setBackgroundResource(0)
            holder.textView.setTextColor(context.resources.getColor(R.color.apnaBlack))
            holder.textView.paintFlags = holder.textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.playableIV.setColorFilter(context.resources.getColor(R.color.apnaBlack))
        }

        holder.textView.setOnLongClickListener {
            mActivity.removePendingitem(reminderItemSet[position].item.toString())
        }
    }

    override fun getItemCount() = reminderItemSet.size

    fun clear() = reminderItemSet.clear()

}
