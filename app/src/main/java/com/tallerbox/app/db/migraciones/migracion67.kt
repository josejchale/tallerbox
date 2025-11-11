package com.tallerbox.app.db.migraciones

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1️⃣ Crear tabla vehiculos con foreign key hacia clientes
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS vehiculos_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                clienteId INTEGER NOT NULL,
                marca TEXT NOT NULL,
                modelo TEXT NOT NULL,
                ano TEXT NOT NULL,
                color TEXT NOT NULL,
                vin TEXT NOT NULL,
                placa TEXT,
                FOREIGN KEY(clienteId) REFERENCES clientes(id) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_vehiculos_clienteId ON vehiculos_new(clienteId)")

        db.execSQL("""
            INSERT INTO vehiculos_new (id, clienteId, marca, modelo, ano, color, vin, placa)
            SELECT id, clienteId, marca, modelo, ano, color, vin, placa FROM vehiculos
        """)
        db.execSQL("DROP TABLE vehiculos")
        db.execSQL("ALTER TABLE vehiculos_new RENAME TO vehiculos")

        // 2️⃣ Crear tabla orden_servicio con foreign keys
        db.execSQL("""
    CREATE TABLE IF NOT EXISTS orden_servicio_new (
        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        clienteId INTEGER NOT NULL,
        vehiculoId INTEGER,
        numeroOrden TEXT NOT NULL,
        fechaIngreso INTEGER NOT NULL,
        fechaEntregaEstimado INTEGER,
        fechaEntregaReal INTEGER,
        descripcionFalla TEXT,
        trabajoRealizado TEXT,
        notas TEXT,
        cond_espejos TEXT NOT NULL,
        cond_asientos TEXT NOT NULL,
        cond_faroDelantero TEXT NOT NULL,
        cond_luzTrasera TEXT NOT NULL,
        cond_direccionales TEXT NOT NULL,
        cond_cubiertas TEXT NOT NULL,
        cond_taponGasolina TEXT NOT NULL,
        cond_pedales TEXT NOT NULL,
        cond_parabrisas TEXT NOT NULL,
        cond_claxon TEXT NOT NULL,
        cond_taponAceite TEXT NOT NULL,
        cond_taponRadiador TEXT NOT NULL,
        cond_filtroAire TEXT NOT NULL,
        cond_bateria TEXT NOT NULL,
        cond_llaves TEXT NOT NULL,
        cond_observaciones TEXT,
        costo_costo REAL NOT NULL,
        firmaClienteBase64 TEXT,
        aceptaEnvioPublicidad INTEGER NOT NULL DEFAULT 0,
        aceptaCedencia INTEGER NOT NULL DEFAULT 0,
        estadoOrden TEXT,
        FOREIGN KEY(clienteId) REFERENCES clientes(id) ON DELETE CASCADE,
        FOREIGN KEY(vehiculoId) REFERENCES vehiculos(id) ON DELETE CASCADE
    )
""")


        db.execSQL("CREATE INDEX IF NOT EXISTS index_orden_clienteId ON orden_servicio_new(clienteId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_orden_vehiculoId ON orden_servicio_new(vehiculoId)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_orden_numeroOrden ON orden_servicio_new(numeroOrden)")

        // Copiar datos antiguos asignando fechaIngreso si no existía
        db.execSQL("""
            INSERT INTO orden_servicio_new (
    id, clienteId, vehiculoId, numeroOrden, fechaIngreso,
    cond_espejos, cond_asientos, cond_faroDelantero, cond_luzTrasera,
    cond_direccionales, cond_cubiertas, cond_taponGasolina, cond_pedales,
    cond_parabrisas, cond_claxon, cond_taponAceite, cond_taponRadiador,
    cond_filtroAire, cond_bateria, cond_llaves, costo_costo,
    aceptaEnvioPublicidad, aceptaCedencia, estadoOrden
)
SELECT
    id, clienteId, vehiculoId, numeroOrden, strftime('%s','now')*1000,
    COALESCE(cond_espejos, 'NO'),
    COALESCE(cond_asientos, 'NO'),
    COALESCE(cond_faroDelantero, 'NO'),
    COALESCE(cond_luzTrasera, 'NO'),
    COALESCE(cond_direccionales, 'NO'),
    COALESCE(cond_cubiertas, 'NO'),
    COALESCE(cond_taponGasolina, 'NO'),
    COALESCE(cond_pedales, 'NO'),
    COALESCE(cond_parabrisas, 'NO'),
    COALESCE(cond_claxon, 'NO'),
    COALESCE(cond_taponAceite, 'NO'),
    COALESCE(cond_taponRadiador, 'NO'),
    COALESCE(cond_filtroAire, 'NO'),
    COALESCE(cond_bateria, 'NO'),
    COALESCE(cond_llaves, 'NO'),
    COALESCE(costo_costo, 0.0),
    COALESCE(aceptaEnvioPublicidad, 0),
    COALESCE(aceptaCedencia, 0),
    estadoOrden
FROM orden_servicio;

        """)

        db.execSQL("DROP TABLE orden_servicio")
        db.execSQL("ALTER TABLE orden_servicio_new RENAME TO orden_servicio")
    }
}
