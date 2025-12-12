package com.tallerbox.app.utils.pdf

import android.graphics.Canvas
import com.tallerbox.app.utils.pdf.PdfPaints
import com.tallerbox.app.utils.pdf.TextHelpers.drawRichMultilineText

object ReglamentoBlock {

    fun draw(
        canvas: Canvas,
        pageWidth: Int,
        startY: Float,
        margin: Float,
        paintSectionTitle: android.graphics.Paint,
        paintHeaderTitle: android.graphics.Paint,
        paintBody: android.graphics.Paint,
        paintBoldBody: android.graphics.Paint
    ): Float {

        var y = startY

        val title = "REGLAMENTO DEL TALLER MECÁNICO DE MOTOS"
        val titleX = (pageWidth / 2f) - (paintSectionTitle.measureText(title) / 2f)
        canvas.drawText(title, titleX, y, paintSectionTitle)
        y += 20f

        val subtitle = "~BOX HALACHÓ~"
        val subtitleX = (pageWidth / 2f) - (paintHeaderTitle.measureText(subtitle) / 2f)
        canvas.drawText(subtitle, subtitleX, y, paintHeaderTitle)
        y += 30f

        val reglamento = """
Para garantizar un servicio eficiente y organizado, así como para evitar inconvenientes tanto para los clientes como para el taller, solicitamos que todos los clientes respeten las siguientes reglas al dejar sus motocicletas para reparación:

**1. RECEPCIÓN DE LA MOTO Y ORDEN DE SERVICIO.**
Toda moto que sea ingresada al taller para su reparación deberá estar acompañada de una Orden de Servicio debidamente llenada. Este documento es esencial para que el taller pueda ofrecer el precio acordado y garantizar que los trabajos realizados sean los correctos. Sin la Orden de Servicio, el precio acordado podría no ser respetado.

**2. PLAZO DE ENTREGA Y CARGOS POR RETRASO.**
Se establecerá un plazo de entrega para cada motocicleta, el cual será acordado entre el cliente y el taller al momento de la recepción. En caso de que el cliente no recoja la moto en la fecha acordada, se cobrará un cargo adicional de **$100 pesos MXN por cada día de retraso**. Este cargo es para cubrir el espacio ocupado en el taller y los costos adicionales generados por la demora.

**3. RESPONSABILIDAD DEL CLIENTE EN LA RETIRADA DE LA MOTO.**
Es responsabilidad del cliente retirar la moto en el plazo acordado. Si el cliente no puede cumplir con la fecha de entrega, deberá notificar al taller con al menos 24 horas de anticipación para acordar una nueva fecha, y se tomará en cuenta si existen cargos adicionales por retrasos previos.

**4. EVALUACIÓN DE DAÑOS Y PRESUPUESTO PREVIO.**
Antes de iniciar cualquier trabajo de reparación, el taller proporcionará un presupuesto detallado que debe ser aprobado por el cliente. El presupuesto podrá modificarse si durante la reparación se encuentran daños adicionales no detectados en la evaluación inicial.

**5. PIEZAS Y REPUESTOS.**
Cualquier repuesto o pieza que se requiera para la reparación de la moto será adquirido por el taller con la aprobación previa del cliente. Los costos de los repuestos serán adicionales al presupuesto inicial y deberán ser cubiertos por el cliente al momento de la entrega de la moto.

**6. GARANTÍA DE LOS TRABAJOS REALIZADOS.**
El taller ofrece una garantía sobre los trabajos realizados, que podrá variar dependiendo del tipo de reparación o servicio. Esta garantía cubre defectos de mano de obra y piezas defectuosas durante un periodo de tiempo determinado.  
**No incluye daños causados por mal uso, accidentes o negligencia del cliente. Además, El Box no se hace responsable por vehículos abandonados por más de 15 días.**

**7. POLÍTICA DE CANCELACIÓN O MODIFICACIÓN DE TRABAJOS.**
Si el cliente decide cancelar o modificar el trabajo una vez iniciado, se cobrará un cargo proporcional al avance realizado, el cual será determinado según el tipo de reparación. La cancelación deberá realizarse por escrito y con al menos 24 horas de antelación.
""".trimIndent()

        y = drawRichMultilineText(
            canvas,
            reglamento,
            margin,
            y,
            pageWidth - margin * 2,
            paintBody,
            paintBoldBody
        )

        return y
    }
}