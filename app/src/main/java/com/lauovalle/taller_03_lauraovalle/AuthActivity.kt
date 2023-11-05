package com.lauovalle.taller_03_lauraovalle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.User
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    private lateinit var dbRef: DatabaseReference
    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --------------------  BINDING
        binding = ActivityAuthBinding.inflate(layoutInflater)
        // llama al método getRoot() para obtener una referencia a la vista raíz
        val view: View = binding.root
        setContentView(view)

        // --------------------- DAATA BASE REFERENCE
        dbRef = FirebaseDatabase.getInstance().getReference("Usuarios")

        setup()
    }

    private fun setup() {
        title = "Atenticación"

        binding.SingUpBtn.setOnClickListener{
            if(binding.EmailAddress.text.isNotEmpty() && binding.Password.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.EmailAddress.text.toString(),binding.Password.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful) {
                        // Guardar la información del usuario
                        saveUserData()
                        // Intent para ver si los datos se guardan bien
                        val homeIntent = Intent(this, HomeActivity::class.java)
                        homeIntent.putExtra("email",binding.EmailAddress.text.toString())
                        homeIntent.putExtra("password",binding.Password.text.toString())
                        startActivity(homeIntent)
                    } else {
                        showAlert()
                    }
                }
            }
        }


        binding.LogInBtn.setOnClickListener{
            if(binding.EmailAddress.text.isNotEmpty() && binding.Password.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.EmailAddress.text.toString(),binding.Password.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful) {
                        val homeIntent = Intent(this, HomeActivity::class.java)
                        homeIntent.putExtra("email",binding.EmailAddress.text.toString())
                        homeIntent.putExtra("password",binding.Password.text.toString())
                        startActivity(homeIntent)

                    } else {
                        showAlert()
                    }
                }
            }
        }

    }

    private fun saveUserData() {
        if(binding.EmailAddress.text.isEmpty() || binding.Name.text.isEmpty() || binding.LastName.text.isEmpty() || binding.Password.text.isEmpty() || binding.Phone.text.isEmpty() || binding.Identification.text.isEmpty()) {
            // SnackBar pidiendo que se llenen todos los datos
            TODO()
        }
        else {

            var userId = dbRef.push().key!!
            user.key = userId
            user.nombre = binding.Name.text.toString()
            user.apellido = binding.LastName.text.toString()
            user.phone = binding.Phone.text.toString()
            user.nroId = binding.Identification.text.toString()

            dbRef.child(userId).setValue(user).addOnCompleteListener{
                Toast.makeText(this,"Datos guardados correctamente", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {err ->
                Toast.makeText(this,"Error: ${err.message}", Toast.LENGTH_LONG).show()
            }

            TODO("Incluir la latitud y la longitud")
        }
    }


    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}