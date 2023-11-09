package com.lauovalle.taller_03_lauraovalle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.lauovalle.taller_03_lauraovalle.Fragments.LogInFragment
import com.lauovalle.taller_03_lauraovalle.Fragments.SignUpFragment
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    private var login: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --------------------  BINDING
        binding = ActivityAuthBinding.inflate(layoutInflater)
        // llama al método getRoot() para obtener una referencia a la vista raíz
        val view: View = binding.root
        setContentView(view)

        val intent:Intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)

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