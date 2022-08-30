package com.example.vindication

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ItemListAdapter(
    private val reminderItemSet: ArrayList<reminderItem>,
    private val context: Context
        ): RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    val mActivity: MainActivity = context as MainActivity

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.itemTextView)
        val checkBox: CheckBox = view.findViewById(R.id.itemCheckBox)
        val infoIV: ImageView = view.findViewById(R.id.infoIV)
        val cardView: CardView = view.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemText = reminderItemSet[position].item.toString()

        holder.checkBox.isChecked = reminderItemSet[position].completion == true

        if(holder.checkBox.isChecked){
            holder.textView.apply{paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            text = itemText}
        } else
            holder.textView.apply{paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            text = itemText}

        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mActivity.toggleCheck(reminderItemSet[position].item.toString(), true)
            } else {
                mActivity.toggleCheck(reminderItemSet[position].item.toString(), false)
            }
        }

        holder.infoIV.setOnClickListener {
            mActivity.infoDialog(reminderItemSet[position])
        }

        if(reminderItemSet[position].owner == "Pankaj Kumar Roy" && reminderItemSet[position].completion == false){
            holder.cardView.setBackgroundResource(R.drawable.blue_gradient)
            holder.textView.setTextColor(context.resources.getColor(R.color.white))
            holder.infoIV.drawable.setTint(context.resources.getColor(R.color.white))

        } else if(reminderItemSet[position].owner == "Pankaj Kumar Roy" && reminderItemSet[position].completion == true){
            holder.cardView.setBackgroundResource(0)
            holder.textView.setTextColor(context.resources.getColor(R.color.apnaBlack))
            holder.infoIV.drawable.setTint(context.resources.getColor(R.color.apnaBlack))

        } else if(reminderItemSet[position].owner == "ktress" && reminderItemSet[position].completion == false) {
            holder.cardView.setBackgroundResource(R.drawable.pink_gradient)
            holder.textView.setTextColor(context.resources.getColor(R.color.white))
            holder.infoIV.drawable.setTint(context.resources.getColor(R.color.white))

        } else if(reminderItemSet[position].owner == "ktress" && reminderItemSet[position].completion == true) {
            holder.cardView.setBackgroundResource(0)
            holder.textView.setTextColor(context.resources.getColor(R.color.apnaBlack))
            holder.infoIV.drawable.setTint(context.resources.getColor(R.color.apnaBlack))
        }

        holder.textView.setOnLongClickListener { mActivity.removePendingitem(reminderItemSet[position].item.toString()) }
    }

    override fun getItemCount() = reminderItemSet.size

    fun clear() = reminderItemSet.clear()

}
