package com.tallerbox.app.viewmodel.vehiculo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.repository.vehiculo.VehiculoRepository

class VehiculoViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao = AppDatabase.getDatabase(context).vehiculoDao()
        val repo = VehiculoRepository(dao)
        @Suppress("UNCHECKED_CAST")
        return VehiculoViewModel(repo) as T
    }
}