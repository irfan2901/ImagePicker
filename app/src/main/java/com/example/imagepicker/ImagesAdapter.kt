package com.example.imagepicker

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImagesAdapter(private val images: ArrayList<Uri>, private val onRemoveItem: (Int) -> Unit) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImage)
        val removeButton: ImageView = itemView.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_images, parent, false)
        return ImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val image = images[position]
        holder.imageView.setImageURI(image)
        holder.removeButton.setOnClickListener { onRemoveItem(position) }
    }

    fun removeItem(position: Int) {
        if (position in 0 until images.size) {
            images.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, images.size)
        }
    }
}