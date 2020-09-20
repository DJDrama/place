package com.place.www.ui.launcher.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.place.www.R
import com.place.www.ui.showToast
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.button_register
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        val act = requireActivity() as AppCompatActivity
        //Show ActionBar
        act.supportActionBar?.show()

        button_register.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val email = editTextTextEmailAddress.text.toString()
        val password = editTextTextPassword.text.toString()
        val passwordCheck = editTextTextPassword2.text.toString()

        if (email.isBlank() || email.isEmpty()) {
            requireContext().showToast("이메일을 입력해주세요.")
            return
        }
        if (password.isBlank() || password.isEmpty()) {
            requireContext().showToast("비밀번호를 입력해주세요.")
            return
        }
        if (passwordCheck.isBlank() || passwordCheck.isEmpty()) {
            requireContext().showToast("비밀번호 확인을 입력해주세요.")
            return
        }
        if (password != passwordCheck) {
            requireContext().showToast("비밀번호가 다릅니다.")
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    requireContext().showToast("회원가입이 완료되었습니다. 가입하신 이메일로 로그인 해주세요.")
                    findNavController().navigateUp()
                } else {
                    // If sign in fails, display a message to the user.
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            requireContext().showToast("이메일 주소가 옳바르지 않습니다.")
                        }
                        is FirebaseAuthWeakPasswordException -> {
                            requireContext().showToast("비밀번호는 최소 6자리 이상이여야 합니다.")
                        }
                        is FirebaseAuthUserCollisionException -> {
                            requireContext().showToast("이미 가입되어 있는 이메일 주소입니다.")
                        }
                        else -> {
                            requireContext().showToast("알 수 없는 에러 발생!")
                        }
                    }
                }
            }
    }
}