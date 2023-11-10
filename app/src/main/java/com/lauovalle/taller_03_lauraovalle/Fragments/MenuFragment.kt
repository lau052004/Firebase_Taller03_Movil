package com.lauovalle.taller_03_lauraovalle.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.lauovalle.taller_03_lauraovalle.AuthActivity
import com.lauovalle.taller_03_lauraovalle.EditUserActivity
import com.lauovalle.taller_03_lauraovalle.HomeActivity
import com.lauovalle.taller_03_lauraovalle.R
import com.lauovalle.taller_03_lauraovalle.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = Firebase.auth
        mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios")

        setup()
    }

    override fun onResume() {
        super.onResume()
        binding.menu.selectedItemId = R.id.item_home
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
                    val intent = Intent(requireContext(), EditUserActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.item_available -> {
                    // confirmation alert
                    alertChangeState()

                    true
                }
                R.id.item_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun alertChangeState() {
        val builder = AlertDialog.Builder(requireContext())
        val user = mAuth.currentUser
        val userBD = mDatabase.child(user?.uid.toString())
        userBD.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Assuming 'disponible' is a boolean field in your Firebase Realtime Database
                val disponible = dataSnapshot.child("disponible")
                    .getValue(Boolean::class.java) ?: false

                if (disponible) {
                    builder.setMessage("Cambiar a 'No Disponible'?")
                } else {
                    builder.setMessage("Cambiar a 'Disponible'?")
                }

                builder.setCancelable(false)
                    .setTitle("Cambiar estado")
                    .setPositiveButton("Si") { dialog, _ ->
                        // Update user status

                        userBD.child("disponible")
                            .setValue(!disponible).addOnSuccessListener {
                                Toast.makeText(requireContext(), "Estado actualizado", Toast.LENGTH_SHORT).show()
                        }
                        // Dismiss the dialog
                        dialog.dismiss()
                        binding.menu.selectedItemId = R.id.item_home
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        // Dismiss the dialog
                        dialog.dismiss()
                        binding.menu.selectedItemId = R.id.item_home
                    }
                val alert = builder.create()
                alert.show()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}