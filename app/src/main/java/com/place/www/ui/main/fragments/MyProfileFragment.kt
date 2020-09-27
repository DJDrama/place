package com.place.www.ui.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.place.www.R
import com.place.www.databinding.FragmentMapDetailBinding
import com.place.www.databinding.FragmentMyProfileBinding
import com.place.www.ui.launcher.LauncherActivity
import kotlinx.android.synthetic.main.fragment_my_profile.*
import java.text.SimpleDateFormat
import java.util.*

class MyProfileFragment : Fragment(), View.OnClickListener {
    private lateinit var firebaseAuth: FirebaseAuth

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.tvEmailAddress.text = firebaseAuth.currentUser?.email
        binding.tvJoinedDate.text = SimpleDateFormat("yyyy/MM/dd hh:mm:ss a", Locale.KOREA)
            .format(Date(firebaseAuth.currentUser?.metadata?.creationTimestamp!!))

        binding.ivMyPlaces.setOnClickListener(this)
        binding.tvMyPlaces.setOnClickListener(this)
        binding.buttonLogout.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id) {
                R.id.iv_my_places, R.id.tv_my_places -> {
                    it.findNavController()
                        .navigate(R.id.action_myProfileFragment_to_myPlacesFragment)
                }
                R.id.button_logout -> {
                    firebaseAuth.signOut()
                    startActivity(Intent(requireActivity(), LauncherActivity::class.java))
                    requireActivity().finishAffinity()
                }

            }
        }
    }
}