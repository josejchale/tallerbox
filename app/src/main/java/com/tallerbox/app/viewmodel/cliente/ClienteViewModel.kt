package com.tallerbox.app.viewmodel.cliente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tallerbox.app.model.cliente.ClienteEntity
import com.tallerbox.app.repository.cliente.ClienteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClienteViewModel(private val repo: ClienteRepository) : ViewModel() {

    val clientes: StateFlow<List<ClienteEntity>> =
        repo.obtenerTodosFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertarCliente(cliente: ClienteEntity, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.insertar(cliente)
            onComplete?.invoke()
        }
    }

    fun eliminarCliente(cliente: ClienteEntity, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.eliminar(cliente)
            onComplete?.invoke()
        }
    }
}