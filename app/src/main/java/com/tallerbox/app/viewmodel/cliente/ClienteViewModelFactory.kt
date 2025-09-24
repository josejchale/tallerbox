package com.tallerbox.app.viewmodel.cliente

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.repository.cliente.ClienteRepository

class ClienteViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao = AppDatabase.getDatabase(context).clienteDao()
        val repo = ClienteRepository(dao)
        @Suppress("UNCHECKED_CAST")
        return ClienteViewModel(repo) as T
    }
}