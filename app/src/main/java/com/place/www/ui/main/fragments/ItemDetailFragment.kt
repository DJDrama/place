package com.place.www.ui.main.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.place.www.R
import com.place.www.databinding.FragmentItemDetailBinding
import com.place.www.ui.showToast

class ItemDetailFragment : Fragment(R.layout.fragment_item_detail) {
    private var _binding: FragmentItemDetailBinding? = null
    private val binding: FragmentItemDetailBinding get() = _binding!!

    private val args: ItemDetailFragmentArgs by navArgs()


    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemDetailBinding.bind(view)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        with(args.placeItem) {
            val ref = FirebaseStorage.getInstance().reference
            ref.child("images/" + image).downloadUrl.addOnSuccessListener {
                Glide.with(requireContext()).load(it).fitCenter().into(binding.imageView3)
            }
            binding.tvVisitedDate.text = date
            binding.tvReview.text = content

            if(uid == firebaseAuth.currentUser?.uid){
                //Show Delete Menu only when current post's uid is equal to the current user's uid
                setHasOptionsMenu(true)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_delete, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                deleteCurrentPost()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteCurrentPost() {
        firebaseFirestore.collection("places")
            .document(args.placeItem.documentId)
            .delete()
            .addOnSuccessListener {
                requireContext().showToast("삭제 되었습니다.")
                findNavController().navigateUp()
            }
            .addOnFailureListener {
                requireContext().showToast("다시 시도해주세요.")
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}