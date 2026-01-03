package com.tallerbox.app.utils

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.Canvas
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.utils.pdf.ClientOrderAndServiceDataBlock
import com.tallerbox.app.utils.pdf.FailureDescriptionBlock
import com.tallerbox.app.utils.pdf.LegalAndSignaturesBlock
import com.tallerbox.app.utils.pdf.PdfHeaderDrawer
import com.tallerbox.app.utils.pdf.PdfPaints
import com.tallerbox.app.utils.pdf.ReglamentoBlock
import com.tallerbox.app.utils.pdf.VehicleConditionsBlock
import com.tallerbox.app.utils.pdf.VehicleDataTable
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    /**
     * Genera el PDF de la orden en tamaño carta (612x792).
     *
     * @param context Context necesario para recursos (logo, etc.)
     * @param data Datos de la orden
     * @param firmaBase64 Firma del prestador (Base64). Puede ser null.
     * @param outFile File de salida (donde se escribirá el PDF)
     * @return outFile después de escribir el PDF
     */
    fun generateOrdenPdf(
        context: Context,
        data: OrdenConClienteYVehiculo,
        firmaBase64: String?,
        outFile: File   // ← YA NO ES NULLABLE
    ): File {

        val doc = PdfDocument()

        val pageWidth = 612
        val pageHeight = 792
        val margin = 30f

        // --- Página 1 ---
        val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page1 = doc.startPage(pageInfo1)
        val canvas1: Canvas = page1.canvas

        var y = margin

        y = PdfHeaderDrawer.draw(context, canvas1, pageWidth, margin)

        y = ClientOrderAndServiceDataBlock.draw(canvas1, data, pageWidth, y + 10f, margin)
        y = VehicleDataTable.draw(canvas1, data, pageWidth, y + 10f, margin)
        y = FailureDescriptionBlock.draw(canvas1, data, pageWidth, y + 10f, margin)
        y = VehicleConditionsBlock.draw(canvas1, data, pageWidth, y + 10f, margin)
        y = LegalAndSignaturesBlock.draw(canvas1, data, pageWidth, y + 10f, margin, firmaBase64)

        doc.finishPage(page1)

        // --- Página 2 ---
        val pageInfo2 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
        val page2 = doc.startPage(pageInfo2)
        val canvas2: Canvas = page2.canvas

        ReglamentoBlock.draw(
            canvas2,
            pageWidth,
            margin,
            margin,
            PdfPaints.sectionTitle,
            PdfPaints.headerTitle,
            PdfPaints.body,
            PdfPaints.bodyBold
        )

        doc.finishPage(page2)

        // guardar archivo
        FileOutputStream(outFile).use { out ->
            doc.writeTo(out)
        }

        doc.close()
        return outFile
    }

}
