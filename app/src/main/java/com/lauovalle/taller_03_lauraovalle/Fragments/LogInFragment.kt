package com.lauovalle.taller_03_lauraovalle.Fragments

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lauovalle.taller_03_lauraovalle.HomeActivity
import com.lauovalle.taller_03_lauraovalle.databinding.FragmentLogInBinding


class LogInFragment : Fragment() {
    private lateinit var binding: FragmentLogInBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = Firebase.auth

        binding.LogInBtn.setOnClickListener {
            if(binding.EmailAddress.text.isNotEmpty() && binding.Password.text.isNotEmpty()) {
                mAuth.signInWithEmailAndPassword(binding.EmailAddress.text.toString(),binding.Password.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful) {
                        val homeIntent = Intent(requireContext(), HomeActivity::class.java)
                        homeIntent.putExtra("email",binding.EmailAddress.text.toString())
                        homeIntent.putExtra("password",binding.Password.text.toString())
                        startActivity(homeIntent)

                    } else {
                        showAlert()
                    }
                }
            } else {
                Snackbar.make(requireActivity().findViewById(R.id.content), "Por favor, llene todos los campos", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("ERROR")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}