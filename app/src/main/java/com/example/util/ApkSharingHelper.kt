package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object ApkSharingHelper {

    /**
     * Saves the application's own APK file directly to the device's public Downloads folder.
     */
    fun downloadApkToDevice(context: Context) {
        try {
            val srcApk = File(context.applicationInfo.sourceDir)
            if (!srcApk.exists()) {
                Toast.makeText(context, "No se encontró el archivo base del APK", Toast.LENGTH_LONG).show()
                return
            }

            var success = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "PerlaVerde_ColoniaSanLuis.apk")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.android.package-archive")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { out ->
                        srcApk.inputStream().use { inp ->
                            inp.copyTo(out)
                        }
                    }
                    success = true
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val targetFile = File(downloadsDir, "PerlaVerde_ColoniaSanLuis.apk")
                srcApk.copyTo(targetFile, overwrite = true)
                success = true
            }

            if (success) {
                Toast.makeText(
                    context,
                    "✅ APK guardado en tu carpeta de Descargas (PerlaVerde_ColoniaSanLuis.apk)",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // Fallback to cache copy
                val cacheDest = File(context.cacheDir, "PerlaVerde_ColoniaSanLuis.apk")
                srcApk.copyTo(cacheDest, overwrite = true)
                Toast.makeText(
                    context,
                    "✅ APK preparado. Usa 'Compartir por WhatsApp' para enviarlo.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al guardar APK: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares the APK file directly to WhatsApp, Telegram, Drive or any app via FileProvider.
     */
    fun shareApkViaWhatsApp(context: Context) {
        try {
            val srcApk = File(context.applicationInfo.sourceDir)
            val shareDir = File(context.cacheDir, "shared_apk")
            if (!shareDir.exists()) shareDir.mkdirs()

            val apkCopy = File(shareDir, "PerlaVerde_ColoniaSanLuis.apk")
            srcApk.copyTo(apkCopy, overwrite = true)

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkCopy
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "🟢⚪ *Colonia San Luis – Perla Verde* ⚽\nTe comparto el instalador oficial de la app del equipo. Toca el archivo para instalarla."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Enviar APK a jugadores"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al preparar APK para compartir: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
