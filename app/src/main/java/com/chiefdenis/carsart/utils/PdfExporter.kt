package com.chiefdenis.carsart.utils

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.chiefdenis.carsart.domain.model.Vehicle
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class PdfExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun exportServiceHistory(vehicle: Vehicle): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas
        val paint = android.graphics.Paint()
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 16f

        canvas.drawText("Service History for ${vehicle.nickname}", 10f, 25f, paint)

        document.finishPage(page)

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "${vehicle.nickname}-Service-History.pdf")

        return try {
            document.writeTo(FileOutputStream(file))
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            null
        }
    }
}
