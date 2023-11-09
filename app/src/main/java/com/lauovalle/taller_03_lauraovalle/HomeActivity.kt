package com.lauovalle.taller_03_lauraovalle

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.UsuariosActivos
import com.lauovalle.taller_03_lauraovalle.adapter.UsuariosActivosAdapter
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityAuthBinding
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    val usuariosActivos = listOf<UsuariosActivos>(
        UsuariosActivos("Laura","lau@gmail.com"),
        UsuariosActivos("Juan","juan@gmail.com"),
        UsuariosActivos("Juan","juan@gmail.com"),
        UsuariosActivos("Juan","juan@gmail.com"),
        UsuariosActivos("Juan","juan@gmail.com")
    )

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

        //setup(email?:"",pass?:"")
        initRecyclerView()
    }

    fun initRecyclerView() {
        // Sacar la lista de los usuarios disponibles
        binding.ListaDisponibles.layoutManager = LinearLayoutManager(this)
        binding.ListaDisponibles.adapter = UsuariosActivosAdapter(usuariosActivos)
    }
}