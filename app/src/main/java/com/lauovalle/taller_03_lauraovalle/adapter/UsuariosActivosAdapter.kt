package com.lauovalle.taller_03_lauraovalle.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.UsuariosActivos
import com.lauovalle.taller_03_lauraovalle.R

class UsuariosActivosAdapter (private val usuariosActivosList:List<UsuariosActivos>) : RecyclerView.Adapter<UsuariosActivosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuariosActivosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UsuariosActivosViewHolder(layoutInflater.inflate(R.layout.item_usuariosactivos,parent,false))
    }

    override fun getItemCount(): Int {
        return usuariosActivosList.size
    }

    override fun onBindViewHolder(holder: UsuariosActivosViewHolder, position: Int) {
        val item = usuariosActivosList[position]
        holder.render(item)
    }
}