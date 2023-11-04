package com.lauovalle.taller_03_lauraovalle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --------------------  BINDING
        binding = ActivityAuthBinding.inflate(layoutInflater)
        // llama al método getRoot() para obtener una referencia a la vista raíz
        val view: View = binding.root
        setContentView(view)

        setup()
    }

    private fun setup() {
        title = "Atenticación"

        binding.SingUpBtn.setOnClickListener{
            if(binding.EmailAddress.text.isNotEmpty() && binding.Password.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.EmailAddress.text.toString(),binding.Password.text.toString()).addOnCompleteListener{
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


    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}