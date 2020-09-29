package com.place.www.ui.main.fragments

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
            return oldItem.id == newItem.id
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
        val item = getItem(position)
        holder.bind(item)
    }

    class PlaceViewHolder(
        private val binding: ItemLayoutPlaceBinding,
        private val placeItemClickListener: PlaceItemClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(placeItem: PlaceItem) {
            binding.apply {
                itemView.setOnClickListener {
                    placeItemClickListener?.onPlaceItemClicked(placeItem)
                }
                tvName.text = placeItem.name
                tvAddress.text = placeItem.address
            }
        }
    }
}