package com.tallerbox.app.viewmodel.orden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tallerbox.app.model.orden.OrdenServicioEntity
import com.tallerbox.app.repository.OrdenRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrdenViewModel(private val repo: OrdenRepository) : ViewModel() {

    // 🔹 Todas las órdenes
    val ordenes: StateFlow<List<OrdenServicioEntity>> =
        repo.obtenerTodasFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 🔹 Órdenes por cliente
    fun obtenerPorCliente(clienteId: Int): StateFlow<List<OrdenServicioEntity>> =
        repo.obtenerPorClienteFlow(clienteId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 🔹 Órdenes por vehículo
    fun obtenerPorVehiculo(vehiculoId: Int): StateFlow<List<OrdenServicioEntity>> =
        repo.obtenerPorVehiculoFlow(vehiculoId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 🔹 CRUD básico
    fun insertar(orden: OrdenServicioEntity, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.insertar(orden)
            onComplete?.invoke()
        }
    }

    fun actualizar(orden: OrdenServicioEntity, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.actualizar(orden)
            onComplete?.invoke()
        }
    }

    fun eliminar(orden: OrdenServicioEntity, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.eliminar(orden)
            onComplete?.invoke()
        }
    }
}

class OrdenViewModelFactory(private val repo: OrdenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrdenViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
