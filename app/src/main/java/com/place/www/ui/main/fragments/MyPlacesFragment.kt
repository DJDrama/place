package com.place.www.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.place.www.databinding.FragmentMyPlacesBinding
import com.place.www.databinding.FragmentMyProfileBinding
import com.place.www.model.PlaceItem


class MyPlacesFragment : Fragment(), PlaceItemClickListener {
    private var _binding: FragmentMyPlacesBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var placeRecyclerViewAdapter: PlaceRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseFirestore = FirebaseFirestore.getInstance()


        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        placeRecyclerViewAdapter = PlaceRecyclerViewAdapter(this)
        binding.recyclerView.adapter = placeRecyclerViewAdapter

        fetchMyPlaces()
    }

    private fun fetchMyPlaces() {
        firebaseFirestore.collection("places")
            .whereEqualTo("uid", FirebaseAuth.getInstance().currentUser?.uid)
            .get()
            .addOnSuccessListener {
                val list = mutableListOf<PlaceItem>()
                for (document in it.documents) {
                    val placeItem = document.toObject(PlaceItem::class.java)
                    placeItem?.documentId = document.id
                    placeItem?.let { pl ->
                        list.add(pl)
                    }
                }
                if (list.isNotEmpty()) {
                    placeRecyclerViewAdapter.submitList(list)
                }
            }
            .addOnFailureListener {
                //ERROR
            }
    }

    override fun onPlaceItemClicked(place: PlaceItem) {
        val action = MyPlacesFragmentDirections.actionMyPlacesFragmentToItemDetailFragment(
            place
        )
        findNavController().navigate(action)

    }
}