package com.lauovalle.taller_03_lauraovalle

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lauovalle.taller_03_lauraovalle.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = Firebase.auth

        setup()
    }

    private fun setup() {
        binding.menu.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.item_home -> {
                    if (requireActivity() is HomeActivity) {
                        return@setOnItemSelectedListener true
                    }
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.item_update -> {
                    true
                }
                R.id.item_available -> {
                    true
                }
                R.id.item_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    requireActivity().finish()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }
}