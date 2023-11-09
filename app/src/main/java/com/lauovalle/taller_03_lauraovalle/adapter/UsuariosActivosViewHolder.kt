package com.lauovalle.taller_03_lauraovalle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.UsuariosActivos
import com.lauovalle.taller_03_lauraovalle.R

class UsuariosActivosViewHolder (view: View): RecyclerView.ViewHolder(view){
    val nombreUsuario = view.findViewById<TextView>(R.id.NombreUsuario)
    val correoUsuario = view.findViewById<TextView>(R.id.CorreoUsuario)
    fun render(usuarioActivo: UsuariosActivos){
        nombreUsuario.text = usuarioActivo.usuarioNombre
        correoUsuario.text = usuarioActivo.usuarioCorreo
    }
}