package org.jglrxavpok.todo.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.databinding.FragmentAuthenticationBinding
import org.jglrxavpok.todo.userinfo.UserInfoViewModel

class AuthenticationFragment : Fragment() {

    private val userViewModel: UserInfoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DataBindingUtil.bind<FragmentAuthenticationBinding>(view)!!
        if(userViewModel.isLoggedIn()) {
            findNavController().navigate(R.id.action_authenticationFragment_to_taskListFragment)
            return
        }
        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_authenticationFragment_to_loginFragment)
        }
        binding.signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_authenticationFragment_to_signupFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authentication, container, false)
    }
}