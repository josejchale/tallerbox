package com.tallerbox.app.viewmodel.orden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.model.orden.OrdenServicioEntity
import com.tallerbox.app.repository.OrdenRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class OrdenUiState {
    object Loading : OrdenUiState()
    data class SuccessList(val list: List<OrdenServicioEntity>) : OrdenUiState()
    data class Error(val message: String) : OrdenUiState()
}

class OrdenViewModel(private val repo: OrdenRepository) : ViewModel() {

    private val _clienteFilter = MutableStateFlow<Int?>(null)
    private val _vehiculoFilter = MutableStateFlow<Int?>(null)

    fun setClienteFilter(clienteId: Int?) { _clienteFilter.value = clienteId }
    fun setVehiculoFilter(vehiculoId: Int?) { _vehiculoFilter.value = vehiculoId }

    @OptIn(ExperimentalCoroutinesApi::class)
    val ordenesState: StateFlow<OrdenUiState> = combine(_vehiculoFilter, _clienteFilter) { vehiculoId, clienteId ->
        Pair(vehiculoId, clienteId)
    }.flatMapLatest { (vehiculoId, clienteId) ->
        when {
            vehiculoId != null -> repo.obtenerPorVehiculoFlow(vehiculoId)
            clienteId != null -> repo.obtenerPorClienteFlow(clienteId)
            else -> repo.obtenerTodasFlow()
        }
    }
        .map<List<OrdenServicioEntity>, OrdenUiState> { OrdenUiState.SuccessList(it) }
        .onStart { emit(OrdenUiState.Loading) }
        .catch { emit(OrdenUiState.Error(it.message ?: "Error al cargar órdenes")) }
        .stateIn(viewModelScope, SharingStarted.Lazily, OrdenUiState.Loading)

    private val _detalle = MutableStateFlow<Result<OrdenConClienteYVehiculo?>>(Result.success(null))
    val detalle: StateFlow<Result<OrdenConClienteYVehiculo?>> = _detalle.asStateFlow()

    fun cargarDetalle(ordenId: Int) {
        viewModelScope.launch {
            try {
                val r = repo.obtenerOrdenConRelaciones(ordenId)
                _detalle.value = Result.success(r)
            } catch (e: Exception) {
                _detalle.value = Result.failure(e)
            }
        }
    }

    fun insertar(orden: OrdenServicioEntity, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repo.insertar(orden)
            onComplete(id)
        }
    }

    fun actualizar(orden: OrdenServicioEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repo.actualizar(orden)
            onComplete()
        }
    }

    fun eliminar(orden: OrdenServicioEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repo.eliminar(orden)
            onComplete()
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
