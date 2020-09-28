package com.place.www.ui.main.fragments

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.place.www.R
import com.place.www.databinding.FragmentCreatePostBinding
import com.place.www.ui.showToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_post.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreatePostFragment : Fragment(), View.OnClickListener {
    companion object {
        const val PERMISSIONS_REQUEST_READ_STORAGE = 301
        const val GALLERY_REQUEST_CODE = 201
    }
    private val args: CreatePostFragmentArgs by navArgs()

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth


    private val createPostViewModel by viewModels<CreatePostViewModel>()
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private var picker: DatePickerDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseFirestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        firebaseAuth = FirebaseAuth.getInstance()

        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = df.format(c)
        binding.tvVisitedDate.text = formattedDate

        binding.ivAddPhoto.setOnClickListener(this)
        binding.tvAddPhoto.setOnClickListener(this)
        binding.tvModify.setOnClickListener(this)
        binding.buttonCreate.setOnClickListener(this)

        subscribeObservers()

    }

    private fun subscribeObservers() {
        createPostViewModel.resultUri.observe(viewLifecycleOwner) { resultUri ->
            resultUri?.let {
                Glide.with(requireActivity())
                    .load(it)
                    .centerCrop()
                    .into(binding.ivAddPhoto)
            }
        }
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id) {
                R.id.iv_add_photo, R.id.tv_add_photo -> {
                    // Add Photo
                    if (isStoragePermissionGranted()) {
                        pickFromGallery()
                    } else {

                    }
                }
                R.id.tv_modify -> {
                    picker = DatePickerDialog(this@CreatePostFragment.requireContext())
                    picker?.setOnDateSetListener { _, year, month, day ->
                        var monthString = (month + 1).toString()
                        var dayString = day.toString()
                        if (month + 1 < 10) {
                            monthString = "0".plus(month + 1)
                        }
                        if (day < 10) {
                            dayString = "0".plus(day)
                        }
                        binding.tvVisitedDate.text = "$year-$monthString-$dayString"
                    }
                    picker?.show()
                }
                R.id.button_create->{
                    uploadPost()
                }
                else -> {

                }
            }
        }
    }
    private fun uploadPost(){
        createPostViewModel.getResultUri()?.let{resultUri->
            resultUri.path?.let{path->
                val file = File(path)
                val ref = storageReference.child("images/" + file.name)
                ref.putFile(resultUri).addOnSuccessListener {
                    //SUCCESS
                    val id = args.locationItem.id
                    val name = args.locationItem.name
                    val address = args.locationItem.address
                    val content = binding.edtText.text.toString()
                    val date = binding.tvVisitedDate.text.toString()


                    val data = hashMapOf(
                        "id" to id,
                        "name" to name,
                        "address" to address,
                        "content" to content,
                        "image" to file.name,
                        "email" to firebaseAuth.currentUser?.email,
                        "uid" to firebaseAuth.currentUser?.uid,
                        "date" to date
                    )
                    firebaseFirestore.collection("places")
                        .add(data)
                        .addOnSuccessListener {
                            //SUCCESS
                            requireContext().showToast( "정상적으로 등록되었습니다.")
                            findNavController().navigateUp()
                        }
                        .addOnFailureListener {
                            //ERROR
                            requireContext().showToast("에러 발생! 다시 시도해주세요.")
                        }
                }.addOnFailureListener {
                    //ERROR
                    requireContext().showToast("에러 발생! 다시 시도해주세요.")
                }
            }
        }

    }

    private fun isStoragePermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_READ_STORAGE
            )
            return false
        } else {
            // Permission has already been granted
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    result.uri?.let { uri ->
                        createPostViewModel.setResultUri(uri)
                    }
                }
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    //ERROR
                }
            }
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image.png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun launchImageCrop(uri: Uri?) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .start(requireContext(), this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        picker?.dismiss()
    }

}