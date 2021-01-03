package org.jglrxavpok.todo.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch
import org.jglrxavpok.todo.MainActivity
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.SHARED_PREF_TOKEN_KEY
import org.jglrxavpok.todo.databinding.FragmentLoginBinding
import org.jglrxavpok.todo.databinding.FragmentSignupBinding
import org.jglrxavpok.todo.network.Api
import org.jglrxavpok.todo.network.UserWebService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SignupFragment: Fragment(), KoinComponent {

    private val userInfoWebService by inject<UserWebService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DataBindingUtil.bind<FragmentSignupBinding>(view)!!
        binding.signupButton.setOnClickListener {
            val isEmailFull = binding.emailInput.text.isNotBlank()
            val isPasswordFull = binding.passwordInput.text.isNotBlank()
            val isFirstNameInput = binding.firstNameInput.text.isNotBlank()
            val isLastNameInput = binding.lastNameInput.text.isNotBlank()
            val isConfirmPasswordInput = binding.passwordConfirmationInput.text.isNotBlank()

            if(!isEmailFull || !isPasswordFull || !isFirstNameInput || !isLastNameInput || !isConfirmPasswordInput) {
                Toast.makeText(context, "Merci de remplir tous les champs!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.passwordConfirmationInput.text.toString()
            if(password != confirmPassword) {
                Toast.makeText(context, "Les mots de passe ne correspondent pas!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val signupForm = SignupForm(binding.emailInput.text.toString(),
                    binding.firstNameInput.text.toString(),
                    binding.lastNameInput.text.toString(),
                    password,
                    confirmPassword)

            // TODO: view model?
            lifecycleScope.launch {
                val answer = userInfoWebService.signup(signupForm)
                val body = answer.body()
                if(body != null) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit {
                        putString(SHARED_PREF_TOKEN_KEY, body.token)
                    }
                    startActivity(Intent(activity, MainActivity::class.java))
                } else {
                    Toast.makeText(context, "Impossible de créer un compte avec ces coordonnées. ${answer.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    return@launch
                }
            }
        }
    }
}