package com.tallerbox.app.viewmodel.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tallerbox.app.model.usuario.UsuarioEntity
import com.tallerbox.app.repository.usuario.UsuarioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val repo: UsuarioRepository
) : ViewModel() {

    val usuario: StateFlow<UsuarioEntity?> =
        repo.getUsuario().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    // 👇 Firma en Base64
    val firma: StateFlow<String?> =
        repo.getFirma().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    fun crear(nombre: String, firma: String?) {
        viewModelScope.launch {
            repo.crear(nombre, firma)
        }
    }

    fun actualizar(usuario: UsuarioEntity) {
        viewModelScope.launch {
            repo.actualizar(usuario)
        }
    }
}

/**
 * FACTORY para crear UsuarioViewModel manualmente en Compose
 */
class UsuarioViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            return UsuarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
