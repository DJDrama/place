package com.place.www.ui.main.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.place.www.databinding.ItemLayoutReviewBinding
import com.place.www.model.PlaceItem

interface ReviewItemClickListener {
    fun onReviewItemClicked(place: PlaceItem)
}

class ReviewRecyclerViewAdapter
constructor(
    private val reviewItemClickListener: ReviewItemClickListener?
) : ListAdapter<PlaceItem, ReviewRecyclerViewAdapter.ReviewViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<PlaceItem>() {
        override fun areItemsTheSame(oldItem: PlaceItem, newItem: PlaceItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PlaceItem, newItem: PlaceItem): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding =
            ItemLayoutReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(
            binding,
            reviewItemClickListener
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ReviewViewHolder(
        private val binding: ItemLayoutReviewBinding,
        private val reviewItemClickListener: ReviewItemClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(placeItem: PlaceItem) {
            binding.apply {
                itemView.setOnClickListener {
                    reviewItemClickListener?.onReviewItemClicked(placeItem)
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