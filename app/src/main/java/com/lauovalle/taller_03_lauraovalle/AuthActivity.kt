package com.lauovalle.taller_03_lauraovalle

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.User
import com.lauovalle.taller_03_lauraovalle.Fragments.LogInFragment
import com.lauovalle.taller_03_lauraovalle.Fragments.SignUpFragment
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityAuthBinding
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.Date
import java.util.logging.Logger

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    // Database
    private lateinit var dbRef: DatabaseReference
    private lateinit var user: User

    // Storage
    private var firebaseStorage: FirebaseStorage? = null

    private lateinit var mAuth: FirebaseAuth

    private var login: Boolean = true

    companion object {
        val TAG: String = AuthActivity::class.java.name
    }

    private val logger = Logger.getLogger(TAG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --------------------  BINDING
        binding = ActivityAuthBinding.inflate(layoutInflater)
        // llama al método getRoot() para obtener una referencia a la vista raíz
        val view: View = binding.root
        setContentView(view)

        mAuth = Firebase.auth

        // --------------------- DATA BASE REFERENCE
        dbRef = FirebaseDatabase.getInstance().getReference("Usuarios")
        firebaseStorage = FirebaseStorage.getInstance()

        setup()
    }

    private fun setup() {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, LogInFragment())
            .commit()

        binding.LogInBtn.setOnClickListener {
            if (!login) {
                supportFragmentManager.beginTransaction()
                    .replace(binding.container.id, LogInFragment())
                    .commit()
                login = true

                binding.LogInBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple))
                binding.SingUpBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            }
        }
        binding.SingUpBtn.setOnClickListener {
            if (login) {
                supportFragmentManager.beginTransaction()
                    .replace(binding.container.id, SignUpFragment())
                    .commit()
                login = false

                binding.SingUpBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple))
                binding.LogInBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            }
        }
    }
}