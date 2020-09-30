package com.place.www.ui.main.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.place.www.R
import com.place.www.databinding.FragmentMapDetailBinding
import com.place.www.model.PlaceItem

class MapDetailFragment : Fragment(), ReviewItemClickListener {
    private val args: MapDetailFragmentArgs by navArgs()
    private val mapDetailViewModel: MapDetailViewModel by viewModels()
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var reviewRecyclerViewAdapter: ReviewRecyclerViewAdapter

    private var _binding: FragmentMapDetailBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        firebaseFirestore = FirebaseFirestore.getInstance()
        mapDetailViewModel.setLocationItem(args.locationItem)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        reviewRecyclerViewAdapter = ReviewRecyclerViewAdapter(this)
        binding.recyclerView.adapter = reviewRecyclerViewAdapter

        fetchItemsFromFirebase()
        subscribeObservers()
    }
    private fun fetchItemsFromFirebase(){
        firebaseFirestore.collection("places")
            .whereEqualTo("id", mapDetailViewModel.getLocationItem()?.id)
            .get()
            .addOnSuccessListener {
                //Update RecyclerView
                val list = mutableListOf<PlaceItem>()
                for(document in it.documents){
                    val placeItem = document.toObject(PlaceItem::class.java)
                    placeItem?.documentId = document.id
                    placeItem?.let{pl->
                        list.add(pl)
                    }
                }
                if(list.size!=0){
                    reviewRecyclerViewAdapter.submitList(list)
                }
            }
            .addOnFailureListener {
                //FAILED
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_write, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_create -> {
                navigateToMyCreatePost()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToMyCreatePost() {
        mapDetailViewModel.getLocationItem()?.let {
            val action = MapDetailFragmentDirections
                .actionMapDetailFragmentToCreatePostFragment(
                    it
                )
            findNavController().navigate(action)
        }
    }

    private fun subscribeObservers() {
        mapDetailViewModel.locationItem.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvTitle.text = it.name
                binding.tvAddress.text = it.address
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onReviewItemClicked(place: PlaceItem) {
        val action = MapDetailFragmentDirections.actionMapDetailFragmentToItemDetailFragment(
            place
        )
        findNavController().navigate(action)
    }
}