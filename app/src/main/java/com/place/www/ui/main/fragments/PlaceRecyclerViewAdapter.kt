package com.place.www.ui.main.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.place.www.databinding.ItemLayoutPlaceBinding
import com.place.www.model.PlaceItem

interface PlaceItemClickListener {
    fun onPlaceItemClicked(place: PlaceItem)
}

class PlaceRecyclerViewAdapter
constructor(
    private val placeItemClickListener: PlaceItemClickListener?
) : ListAdapter<PlaceItem, PlaceRecyclerViewAdapter.PlaceViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<PlaceItem>() {
        override fun areItemsTheSame(oldItem: PlaceItem, newItem: PlaceItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PlaceItem, newItem: PlaceItem): Boolean {
            return oldItem.name == newItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding =
            ItemLayoutPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(
            binding,
            placeItemClickListener
        )
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val placeItem = getItem(position)
        holder.bind(placeItem)
    }

    class PlaceViewHolder(
        private val binding: ItemLayoutPlaceBinding,
        private val placeItemClickListener: PlaceItemClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(placeItem: PlaceItem) {
            with(binding) {

                root.setOnClickListener {
                    placeItemClickListener?.onPlaceItemClicked(placeItem)
                }
                val ref = FirebaseStorage.getInstance().reference
                ref.child("images/" + placeItem.image).downloadUrl.addOnSuccessListener {
                    Glide.with(root.context).load(it).into(imageView2)
                }
                tvEmailAddress.text = placeItem.email
                tvContent.text = placeItem.content
                tvDate.text = placeItem.date
            }
        }
    }
}