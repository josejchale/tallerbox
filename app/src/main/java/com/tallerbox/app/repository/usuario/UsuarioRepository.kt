package com.tallerbox.app.repository.usuario

import com.tallerbox.app.model.usuario.UsuarioDao
import com.tallerbox.app.model.usuario.UsuarioEntity

class UsuarioRepository(private val dao: UsuarioDao) {

    fun getUsuario() = dao.getUsuario()

    suspend fun crear(nombre: String, firma: String?) {
        val user = UsuarioEntity(
            id = 1,
            nombre = nombre,
            firmaBase64 = firma
        )
        dao.insert(user)
    }

    suspend fun actualizar(usuario: UsuarioEntity) {
        dao.update(usuario)
    }

    fun getFirma() = dao.getFirma()

}
