package com.place.www.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.place.www.R
import com.place.www.databinding.FragmentItemDetailBinding

class ItemDetailFragment : Fragment(R.layout.fragment_item_detail) {
    private var _binding: FragmentItemDetailBinding? = null
    private val binding: FragmentItemDetailBinding get() = _binding!!

    private val args: ItemDetailFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemDetailBinding.bind(view)
        with(args.placeItem) {
            val ref = FirebaseStorage.getInstance().reference
            ref.child("images/" + image).downloadUrl.addOnSuccessListener {
                Glide.with(requireContext()).load(it).fitCenter().into(binding.imageView3)
            }
            binding.tvVisitedDate.text = date
            binding.tvReview.text = content
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}