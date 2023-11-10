package com.lauovalle.taller_03_lauraovalle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.User
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityEditUserBinding
import java.io.File

class EditUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditUserBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Firebase.auth.currentUser == null) {
            finish()
        }

        val idUsuario = Firebase.auth.currentUser?.uid

        storageRef = FirebaseStorage.getInstance().reference.child("Images/$idUsuario")
        dbRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(idUsuario!!)

        setup()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun setup() {

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this@EditUserActivity).load(uri.toString()).into(binding.ProfilePhoto)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                finish()
            }
        })

        binding.floatingActionButton.setOnClickListener {
            finish()
        }

        binding.updateBtn.setOnClickListener {
            if (binding.Name.text.isNotEmpty()) {
                dbRef.child("nombre").setValue(binding.Name.text.toString())
            }
            if (binding.LastName.text.isNotEmpty()) {
                dbRef.child("apellido").setValue(binding.LastName.text.toString())
            }
            if (binding.Phone.text.isNotEmpty()) {
                dbRef.child("phone").setValue(binding.Phone.text.toString())
            }
            Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()
        }
    }
}