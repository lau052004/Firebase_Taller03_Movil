package com.lauovalle.taller_03_lauraovalle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityAuthBinding
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // --------------------  BINDING
        binding = ActivityHomeBinding.inflate(layoutInflater)
        // llama al método getRoot() para obtener una referencia a la vista raíz
        val view: View = binding.root
        setContentView(view)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val pass = bundle?.getString("password")

        setup(email?:"",pass?:"")
    }

    private fun setup(email: String, pass: String) {
        title = "Inicio"

        binding.EmailAddress.text = email
        binding.Password.text = pass

    }

}