package com.tallerbox.app.screens.orden

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

fun showShareOptions(context: Context, uri: Uri) {
    // Opción 1: chooser general
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(shareIntent, "Compartir orden (PDF)")

    // Opción 2: intento directo a WhatsApp (opcional)
    val whatsappPackage = "com.whatsapp"
    val pm: PackageManager = context.packageManager
    val whatsappInstalled = try {
        pm.getPackageInfo(whatsappPackage, PackageManager.GET_ACTIVITIES)
        true
    } catch (ex: Exception) {
        false
    }

    // Si WhatsApp está instalado, mostramos diálogo simple con dos opciones:
    if (whatsappInstalled) {
        // Intent para WhatsApp
        val waIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            setPackage(whatsappPackage)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Construimos chooser que incluya WhatsApp y el chooser general
        // Primero mostramos chooser con opción directa a WhatsApp (si falla, caemos al chooser general)
        try {
            context.startActivity(waIntent)
        } catch (ae: ActivityNotFoundException) {
            // fallback a chooser
            try {
                context.startActivity(chooser)
            } catch (_: Exception) {
                Toast.makeText(context, "No hay app para compartir el PDF", Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        // Si no está WhatsApp, abrir chooser general
        try {
            context.startActivity(chooser)
        } catch (_: Exception) {
            Toast.makeText(context, "No hay app para compartir el PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
