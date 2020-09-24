package com.place.www.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.place.www.databinding.FragmentMapBinding
import com.place.www.databinding.FragmentMapDetailBinding

class MapDetailFragment : Fragment() {
    private val args : MapDetailFragmentArgs by navArgs()
    private val mapDetailViewModel: MapDetailFragmentViewModel by viewModels()

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
        mapDetailViewModel.setLocationItem(args.locationItem)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        mapDetailViewModel.locationItem.observe(viewLifecycleOwner){
            it?.let{
                binding.tvTitle.text = it.name
                binding.tvAddress.text = it.address
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}