package com.lauovalle.taller_03_lauraovalle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityHomeBinding
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityPruebaBinding

class Prueba : AppCompatActivity() {
    lateinit var binding: ActivityPruebaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        // --------------------  BINDING
        binding = ActivityPruebaBinding.inflate(layoutInflater)
        // llama al método getRoot() para obtener una referencia a la vista raíz
        val view: View = binding.root
        setContentView(view)
        super.onCreate(savedInstanceState)

        val stringValue = intent.getStringExtra("nombre")
        binding.textView.text = stringValue
    }
}