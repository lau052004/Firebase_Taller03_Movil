package com.lauovalle.taller_03_lauraovalle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.User
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.UsuariosActivos
import com.lauovalle.taller_03_lauraovalle.adapter.UsuariosActivosAdapter
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    val usuariosActivos = mutableListOf<UsuariosActivos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // --------------------  BINDING
        binding = ActivityHomeBinding.inflate(layoutInflater)
        // llama al método getRoot() para obtener una referencia a la vista raíz
        val view: View = binding.root
        setContentView(view)
        mAuth = Firebase.auth
        dbRef = FirebaseDatabase.getInstance().getReference("Usuarios")

        //val bundle = intent.extras
        //val email = bundle?.getString("email")
        //val pass = bundle?.getString("password")

        //setup(email?:"",pass?:"")
    }

    override fun onResume() {
        super.onResume()
        if (mAuth.currentUser == null) {
            finish()
        }
        val usuarioActual = mAuth.currentUser?.uid

        crearLista(usuarioActual)
    }

    private fun crearLista(usuarioActual: String?) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Toast.makeText(this@HomeActivity, "datos an cambiado", Toast.LENGTH_SHORT).show()
                for (userSnapshot in dataSnapshot.children) {
                    val usuario = userSnapshot.getValue(User::class.java)
                    if (usuario != null && usuario.key != usuarioActual && usuario.disponible) {
                        val nombre = usuario.nombre
                        val correo = usuario.correo
                        // Haz algo con el nombre y el correo electrónico, como mostrarlos en tu ListView
                        usuariosActivos.add(UsuariosActivos(nombre, correo))
                    }
                }

                initRecyclerView()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Maneja errores si es necesario
                Toast.makeText(this@HomeActivity, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }


    fun initRecyclerView() {
        // Sacar la lista de los usuarios disponibles
        binding.ListaDisponibles.layoutManager = LinearLayoutManager(this)
        binding.ListaDisponibles.adapter = UsuariosActivosAdapter(usuariosActivos)
    }
}