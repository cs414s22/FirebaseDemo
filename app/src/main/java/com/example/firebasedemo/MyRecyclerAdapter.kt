package com.example.firebasedemo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerAdapter(private val contacts: List<Contact>): RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // This class will represent a single row in our recyclerView list
        // This class also allows caching views and reuse them
        // Each MyViewHolder object keeps a reference to 3 view items in our row_item.xml file
        val personId = itemView.findViewById<TextView>(R.id.person_id)
        val personName = itemView.findViewById<TextView>(R.id.person_name)
        val personAge = itemView.findViewById<TextView>(R.id.person_email)
        val personImage = itemView.findViewById<ImageView>(R.id.person_image)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate a layout from our XML (row_item.XML) and return the holder

        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val currentItem = contacts[position]
        holder.personId.text = "Id: ${currentItem.id}"
        holder.personName.text = currentItem.name
        holder.personAge.text = currentItem.email
        // This is a static image but it can be dynamically assigned to a different one
        holder.personImage.setImageResource(R.drawable.ic_baseline_person_24)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return contacts.size
    }

}