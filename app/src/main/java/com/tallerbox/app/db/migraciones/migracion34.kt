package com.tallerbox.app.db.migraciones

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_6 = object : Migration(4, 6) {
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
                `cond_espejos` TEXT NOT NULL,
                `cond_asientos` TEXT NOT NULL,
                `cond_faroDelantero` TEXT NOT NULL,
                `cond_luzTrasera` TEXT NOT NULL,
                `cond_direccionales` TEXT NOT NULL,
                `cond_cubiertas` TEXT NOT NULL,
                `cond_taponGasolina` TEXT NOT NULL,
                `cond_pedales` TEXT NOT NULL,
                `cond_parabrisas` TEXT NOT NULL,
                `cond_claxon` TEXT NOT NULL,
                `cond_taponAceite` TEXT NOT NULL,
                `cond_taponRadiador` TEXT NOT NULL,
                `cond_filtroAire` TEXT NOT NULL,
                `cond_bateria` TEXT NOT NULL,
                `cond_llaves` TEXT NOT NULL,
                `cond_observaciones` TEXT,
                `costo_costo` REAL NOT NULL DEFAULT 0.0,
                `firmaClienteBase64` TEXT,
                `aceptaEnvioPublicidad` INTEGER NOT NULL DEFAULT 0,
                `aceptaCedencia` INTEGER NOT NULL DEFAULT 0,
                `estadoOrden` TEXT,
                FOREIGN KEY(`clienteId`) REFERENCES `clientes`(`id`) ON DELETE CASCADE,
                FOREIGN KEY(`vehiculoId`) REFERENCES `vehiculos`(`id`) ON DELETE SET NULL
            )
        """)

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
                costo_costo, firmaClienteBase64, aceptaEnvioPublicidad, aceptaCedencia, estadoOrden
            FROM orden_servicio
        """)

        db.execSQL("DROP TABLE orden_servicio")
        db.execSQL("ALTER TABLE orden_servicio_new RENAME TO orden_servicio")

        // Crear índices
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_orden_servicio_clienteId` ON `orden_servicio` (`clienteId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_orden_servicio_vehiculoId` ON `orden_servicio` (`vehiculoId`)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_orden_servicio_numeroOrden` ON `orden_servicio` (`numeroOrden`)")
    }
}
