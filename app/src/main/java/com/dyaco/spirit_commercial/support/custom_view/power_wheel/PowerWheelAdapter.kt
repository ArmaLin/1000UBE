package com.dyaco.spirit_commercial.support.custom_view.power_wheel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dyaco.spirit_commercial.R
import timber.log.Timber

class PowerWheelAdapter(val items: List<String>) : RecyclerView.Adapter<PowerWheelAdapter.WheelViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WheelViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.items_wheel_picker, parent, false)
        return WheelViewHolder(view)
    }

    override fun onBindViewHolder(holder: WheelViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getValueAt(position: Int): String? {
        if (position < 0 || position >= items.size) {
            Timber.e("getValueAt: 無效的 Position: %s", position)
            return null
        }
        return items[position]
    }

    class WheelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        val textView: TextView = itemView.findViewById(R.id.tv_wheel_item)

        fun bind(number: String) {
            textView.text = number
        }
    }
}