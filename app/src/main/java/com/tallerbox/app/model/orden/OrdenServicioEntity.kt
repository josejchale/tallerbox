// EntitiesOrden.kt
package com.tallerbox.app.model.orden

import androidx.room.*
import com.tallerbox.app.model.cliente.ClienteEntity
import com.tallerbox.app.model.vehiculo.VehiculoEntity
import java.util.*

// Estado simple para el chequeo de piezas
enum class EstadoCondicion { SI, NO, ROTO }

// Convertidores para enums y fechas
class OrdenTypeConverters {
    @TypeConverter fun fromEstado(e: EstadoCondicion?): String? = e?.name
    @TypeConverter fun toEstado(value: String?): EstadoCondicion? = value?.let { EstadoCondicion.valueOf(it) }
    @TypeConverter fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }
    @TypeConverter fun dateToTimestamp(date: Date?): Long? = date?.time
}

// Datos de condiciones del vehículo (embebido)
data class CondicionVehiculo(
    val espejos: EstadoCondicion = EstadoCondicion.NO,
    val asientos: EstadoCondicion = EstadoCondicion.NO,
    val faroDelantero: EstadoCondicion = EstadoCondicion.NO,
    val luzTrasera: EstadoCondicion = EstadoCondicion.NO,
    val direccionales: EstadoCondicion = EstadoCondicion.NO,
    val cubiertas: EstadoCondicion = EstadoCondicion.NO,
    val taponGasolina: EstadoCondicion = EstadoCondicion.NO,
    val pedales: EstadoCondicion = EstadoCondicion.NO,
    val parabrisas: EstadoCondicion = EstadoCondicion.NO,
    val claxon: EstadoCondicion = EstadoCondicion.NO,
    val taponAceite: EstadoCondicion = EstadoCondicion.NO,
    val taponRadiador: EstadoCondicion = EstadoCondicion.NO,
    val filtroAire: EstadoCondicion = EstadoCondicion.NO,
    val bateria: EstadoCondicion = EstadoCondicion.NO,
    val llaves: EstadoCondicion = EstadoCondicion.NO,
    val observaciones: String? = null // notas libres sobre condiciones
)

// Costo detalle
data class CostosOrden(
    val costo: Double = 0.0
)


@Entity(
    tableName = "orden_servicio",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = VehiculoEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehiculoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("clienteId"), Index("vehiculoId"), Index("numeroOrden", unique = true)]
)
@TypeConverters(OrdenTypeConverters::class)
data class OrdenServicioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // relaciones
    val clienteId: Int,
    val vehiculoId: Int?,

    // identificación y fechas
    val numeroOrden: String,
    val fechaIngreso: Date,
    val fechaEntregaEstimado: Date? = null,
    val fechaEntregaReal: Date? = null,

    // datos descriptivos
    val descripcionFalla: String? = null,
    val trabajoRealizado: String? = null,
    val notas: String? = null,

    // condiciones embebidas
    @Embedded(prefix = "cond_") val condiciones: CondicionVehiculo = CondicionVehiculo(),

    // costos embebidos (almacena campos; total se calcula en código de negocio)
    @Embedded(prefix = "costo_") val costos: CostosOrden = CostosOrden(),

    // firma y consentimiento
    val firmaClienteBase64: String? = null, // si guardas firma como imagen Base64 o path
    val aceptaEnvioPublicidad: Boolean = false,
    val aceptaCedencia: Boolean = false,

    // meta
    val estadoOrden: String? = null // e.g., "PENDIENTE", "EN_PROCESO", "COMPLETADA"
)
