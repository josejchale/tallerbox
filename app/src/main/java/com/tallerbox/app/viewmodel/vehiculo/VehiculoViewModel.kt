package com.tallerbox.app.viewmodel.vehiculo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tallerbox.app.model.vehiculo.VehiculoEntity
import com.tallerbox.app.repository.vehiculo.VehiculoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehiculoViewModel(private val repo: VehiculoRepository) : ViewModel() {

    val vehiculo: StateFlow<List<VehiculoEntity>> =
        repo.obtenerTodosFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertarVehiculo(vehiculo: VehiculoEntity, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.insertar(vehiculo)
            onComplete?.invoke()
        }
    }
}