package org.jglrxavpok.todo.auth

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch
import org.jglrxavpok.todo.MainActivity
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.SHARED_PREF_TOKEN_KEY
import org.jglrxavpok.todo.databinding.FragmentLoginBinding
import org.jglrxavpok.todo.network.Api
import org.jglrxavpok.todo.network.UserWebService
import org.jglrxavpok.todo.userinfo.UserInfoViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginFragment: Fragment(), KoinComponent {

    val userViewModel: UserInfoViewModel by activityViewModels()

    private val userInfoWebService by inject<UserWebService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DataBindingUtil.bind<FragmentLoginBinding>(view)!!
        binding.loginButton.setOnClickListener {
            val isEmailFull = binding.emailInput.text.isNotBlank()
            val isPasswordFull = binding.passwordInput.text.isNotBlank()

            if(!isEmailFull || !isPasswordFull) {
                Toast.makeText(context, "Merci de remplir tous les champs!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val loginForm = LoginForm(binding.emailInput.text.toString(), binding.passwordInput.text.toString())

            // TODO: view model?
            lifecycleScope.launch {
                val answer = userInfoWebService.login(loginForm).body()
                if(answer != null) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit {
                        putString(SHARED_PREF_TOKEN_KEY, answer.token)
                    }
                    startActivity(Intent(activity, MainActivity::class.java))
                } else {
                    Toast.makeText(context, "Mauvais identifiants!", Toast.LENGTH_LONG).show()
                    return@launch
                }
            }
        }
    }
}