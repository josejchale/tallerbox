package com.tallerbox.app.db.migraciones

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `orden_servicio_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `clienteId` INTEGER NOT NULL,
                `vehiculoId` INTEGER,
                `numeroOrden` TEXT NOT NULL,
                `fechaIngreso` INTEGER NOT NULL,
                `fechaEntregaEstimado` INTEGER,
                `fechaEntregaReal` INTEGER,
                `descripcionFalla` TEXT,
                `trabajoRealizado` TEXT,
                `notas` TEXT,
                `cond_espejos` TEXT,
                `cond_asientos` TEXT,
                `cond_faroDelantero` TEXT,
                `cond_luzTrasera` TEXT,
                `cond_direccionales` TEXT,
                `cond_cubiertas` TEXT,
                `cond_taponGasolina` TEXT,
                `cond_pedales` TEXT,
                `cond_parabrisas` TEXT,
                `cond_claxon` TEXT,
                `cond_taponAceite` TEXT,
                `cond_taponRadiador` TEXT,
                `cond_filtroAire` TEXT,
                `cond_bateria` TEXT,
                `cond_llaves` TEXT,
                `cond_observaciones` TEXT,
                `costo_costo` REAL NOT NULL DEFAULT 0.0,
                `firmaClienteBase64` TEXT,
                `aceptaEnvioPublicidad` INTEGER NOT NULL DEFAULT 0,
                `aceptaCedencia` INTEGER NOT NULL DEFAULT 0,
                `estadoOrden` TEXT
            )
        """.trimIndent())

        db.execSQL("""
            INSERT INTO orden_servicio_new (
                id, clienteId, vehiculoId, numeroOrden, fechaIngreso, fechaEntregaEstimado, fechaEntregaReal,
                descripcionFalla, trabajoRealizado, notas,
                cond_espejos, cond_asientos, cond_faroDelantero, cond_luzTrasera, cond_direccionales, cond_cubiertas,
                cond_taponGasolina, cond_pedales, cond_parabrisas, cond_claxon, cond_taponAceite, cond_taponRadiador,
                cond_filtroAire, cond_bateria, cond_llaves, cond_observaciones,
                costo_costo, firmaClienteBase64, aceptaEnvioPublicidad, aceptaCedencia, estadoOrden
            )
            SELECT
                id, clienteId, vehiculoId, numeroOrden, fechaIngreso, fechaEntregaEstimado, fechaEntregaReal,
                descripcionFalla, trabajoRealizado, notas,
                cond_espejos, cond_asientos, cond_faroDelantero, cond_luzTrasera, cond_direccionales, cond_cubiertas,
                cond_taponGasolina, cond_pedales, cond_parabrisas, cond_claxon, cond_taponAceite, cond_taponRadiador,
                cond_filtroAire, cond_bateria, cond_llaves, cond_observaciones,
                0.0, firmaClienteBase64, aceptaEnvioPublicidad, aceptaCedencia, estadoOrden
            FROM orden_servicio
        """.trimIndent())

        db.execSQL("DROP TABLE orden_servicio")
        db.execSQL("ALTER TABLE orden_servicio_new RENAME TO orden_servicio")
    }
}
