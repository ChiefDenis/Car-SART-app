package com.chiefdenis.carsart.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.chiefdenis.carsart.data.database.ServiceRecord
import com.chiefdenis.carsart.data.database.ServiceType
import com.chiefdenis.carsart.data.database.Vehicle
import com.chiefdenis.carsart.data.database.MaintenanceTask
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max

// Unit converter extension functions
fun Double.format(digits: Int): String {
    return String.format("%.${digits}f", this)
}

fun kmToMiles(km: Int): Double {
    return km * 0.621371
}

data class PdfExportConfig(
    val currency: String = "NGN",
    val units: String = "METRIC",
    val includeImages: Boolean = true,
    val includeMaintenanceSection: Boolean = true,
    val locale: Locale = Locale.getDefault()
)

class PdfExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PAGE_WIDTH = 595 // A4 width in points
        private const val PAGE_HEIGHT = 842 // A4 height in points
        private const val MARGIN = 40
        private const val CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN)
        private const val HEADER_HEIGHT = 80
        private const val FOOTER_HEIGHT = 40
        private const val LINE_HEIGHT = 20
        private const val TABLE_ROW_HEIGHT = 25
        private const val TITLE_SIZE = 24f
        private const val HEADER_SIZE = 18f
        private const val BODY_SIZE = 12f
        private const val FOOTER_SIZE = 10f
    }

    fun exportServiceHistory(
        vehicle: Vehicle,
        serviceRecords: List<ServiceRecord>,
        maintenanceTasks: List<MaintenanceTask>,
        config: PdfExportConfig = PdfExportConfig()
    ): Uri? {
        return try {
            val document = PdfDocument()
            val pageCount = calculatePageCount(serviceRecords, maintenanceTasks, config)
            
            for (pageNum in 1..pageCount) {
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
                val page = document.startPage(pageInfo)
                val canvas = page.canvas
                
                drawPage(canvas, pageNum, pageCount, vehicle, serviceRecords, maintenanceTasks, config)
                document.finishPage(page)
            }
            
            val file = savePdfFile(document, vehicle)
            document.close()
            
            file?.let { FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculatePageCount(
        serviceRecords: List<ServiceRecord>,
        maintenanceTasks: List<MaintenanceTask>,
        config: PdfExportConfig
    ): Int {
        val availableHeight = PAGE_HEIGHT - HEADER_HEIGHT - FOOTER_HEIGHT - (2 * MARGIN)
        
        // Header and summary space
        var usedHeight = 150f
        
        // Service records table
        val serviceRecordsHeight = serviceRecords.size * TABLE_ROW_HEIGHT + 50f // Header + rows
        usedHeight += serviceRecordsHeight
        
        // Maintenance section
        if (config.includeMaintenanceSection && maintenanceTasks.isNotEmpty()) {
            usedHeight += 100f // Section header + tasks
        }
        
        return max(1, (usedHeight / availableHeight).toInt() + 1)
    }

    private fun drawPage(
        canvas: Canvas,
        pageNum: Int,
        totalPages: Int,
        vehicle: Vehicle,
        serviceRecords: List<ServiceRecord>,
        maintenanceTasks: List<MaintenanceTask>,
        config: PdfExportConfig
    ) {
        // Clear background
        canvas.drawColor(Color.WHITE)
        
        // Draw header
        drawHeader(canvas, vehicle, pageNum, totalPages, config)
        
        // Draw content
        val startY = HEADER_HEIGHT + MARGIN
        drawContent(canvas, startY.toFloat(), vehicle, serviceRecords, maintenanceTasks, config, pageNum)
        
        // Draw footer
        drawFooter(canvas, pageNum, totalPages)
    }

    private fun drawHeader(
        canvas: Canvas,
        vehicle: Vehicle,
        pageNum: Int,
        totalPages: Int,
        config: PdfExportConfig
    ) {
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = TITLE_SIZE
            typeface = Typeface.DEFAULT_BOLD
        }
        
        // App title
        canvas.drawText("CAR SART - Service History Report", MARGIN.toFloat(), 30f, paint)
        
        // Vehicle info
        paint.textSize = HEADER_SIZE
        paint.typeface = Typeface.DEFAULT
        
        val vehicleInfo = "${vehicle.nickname} (${vehicle.make} ${vehicle.model} ${vehicle.year})"
        canvas.drawText(vehicleInfo, MARGIN.toFloat(), 55f, paint)
        
        // VIN (masked)
        vehicle.vin?.let { vin ->
            val maskedVin = if (vin.length > 4) "****${vin.takeLast(4)}" else vin
            paint.textSize = BODY_SIZE
            canvas.drawText("VIN: $maskedVin", MARGIN.toFloat(), 75f, paint)
        }
        
        // Date and page number
        paint.textAlign = Paint.Align.RIGHT
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", config.locale)
        canvas.drawText(
            "Generated: ${dateFormat.format(Date())} - Page $pageNum/$totalPages",
            (PAGE_WIDTH - MARGIN).toFloat(),
            30f,
            paint
        )
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawContent(
        canvas: Canvas,
        startY: Float,
        vehicle: Vehicle,
        serviceRecords: List<ServiceRecord>,
        maintenanceTasks: List<MaintenanceTask>,
        config: PdfExportConfig,
        pageNum: Int
    ) {
        var currentY = startY
        
        // Service History Section
        currentY = drawServiceHistoryTable(canvas, currentY, serviceRecords, config, pageNum)
        
        // Summary Section
        currentY = drawSummarySection(canvas, currentY, serviceRecords, config)
        
        // Maintenance Section
        if (config.includeMaintenanceSection && maintenanceTasks.isNotEmpty()) {
            currentY = drawMaintenanceSection(canvas, currentY, maintenanceTasks, config)
        }
    }

    private fun drawServiceHistoryTable(
        canvas: Canvas,
        startY: Float,
        serviceRecords: List<ServiceRecord>,
        config: PdfExportConfig,
        pageNum: Int
    ): Float {
        val paint = Paint()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", config.locale)
        
        var currentY = startY
        
        // Section title
        paint.apply {
            color = Color.BLACK
            textSize = HEADER_SIZE
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText("Service History", MARGIN.toFloat(), currentY, paint)
        currentY += LINE_HEIGHT * 1.5f
        
        // Table headers
        paint.textSize = BODY_SIZE
        paint.typeface = Typeface.DEFAULT_BOLD
        
        val headers = listOf("Date", "Type", "Mileage", "Provider", "Cost")
        val columnWidths = listOf(80f, 100f, 80f, 120f, 80f)
        var currentX = MARGIN.toFloat()
        
        headers.forEachIndexed { index, header ->
            canvas.drawText(header, currentX, currentY, paint)
            currentX += columnWidths[index]
        }
        
        currentY += LINE_HEIGHT
        
        // Draw header line
        paint.color = Color.GRAY
        canvas.drawLine(
            MARGIN.toFloat(),
            currentY,
            (PAGE_WIDTH - MARGIN).toFloat(),
            currentY,
            paint
        )
        currentY += 5f
        paint.color = Color.BLACK
        
        // Table rows
        paint.textSize = BODY_SIZE
        paint.typeface = Typeface.DEFAULT
        
        serviceRecords.forEachIndexed { index, record ->
            if (index % 20 == 0 && index > 0 && pageNum == 1) {
                // Skip to next page logic would go here
                return currentY
            }
            
            // Alternate row background
            if (index % 2 == 0) {
                paint.color = Color.LTGRAY
                paint.alpha = 30
                canvas.drawRect(
                    MARGIN.toFloat(),
                    currentY - LINE_HEIGHT + 5,
                    (PAGE_WIDTH - MARGIN).toFloat(),
                    currentY + 5,
                    paint
                )
                paint.alpha = 255
                paint.color = Color.BLACK
            }
            
            currentX = MARGIN.toFloat()
            
            // Date
            canvas.drawText(
                dateFormat.format(Date(record.date)),
                currentX,
                currentY,
                paint
            )
            currentX += columnWidths[0]
            
            // Service Type
            canvas.drawText(record.serviceType.name, currentX, currentY, paint)
            currentX += columnWidths[1]
            
            // Mileage (with unit conversion)
            val mileageText = if (config.units == "IMPERIAL") {
                "${kmToMiles(record.mileage).format(1)} mi"
            } else {
                "${record.mileage} km"
            }
            canvas.drawText(mileageText, currentX, currentY, paint)
            currentX += columnWidths[2]
            
            // Provider
            canvas.drawText(record.provider ?: "—", currentX, currentY, paint)
            currentX += columnWidths[3]
            
            // Cost (with currency conversion)
            val costText = "${config.currency} ${record.cost.toDouble().format(2)}"
            canvas.drawText(costText, currentX, currentY, paint)
            
            currentY += TABLE_ROW_HEIGHT
        }
        
        return currentY + LINE_HEIGHT
    }

    private fun drawSummarySection(
        canvas: Canvas,
        startY: Float,
        serviceRecords: List<ServiceRecord>,
        config: PdfExportConfig
    ): Float {
        val paint = Paint()
        var currentY = startY + LINE_HEIGHT
        
        // Section title
        paint.apply {
            color = Color.BLACK
            textSize = HEADER_SIZE
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText("Summary", MARGIN.toFloat(), currentY, paint)
        currentY += LINE_HEIGHT * 1.5f
        
        // Calculate statistics
        val totalCost = serviceRecords.sumOf { it.cost.toDouble() }
        val avgCost = if (serviceRecords.isNotEmpty()) totalCost / serviceRecords.size else 0.0
        val serviceTypes = serviceRecords.groupBy { it.serviceType }
        
        paint.textSize = BODY_SIZE
        paint.typeface = Typeface.DEFAULT
        
        // Total spent
        canvas.drawText(
            "Total Spent: ${config.currency} ${totalCost.format(2)}",
            MARGIN.toFloat(),
            currentY,
            paint
        )
        currentY += LINE_HEIGHT
        
        // Average cost per service
        canvas.drawText(
            "Average Cost: ${config.currency} ${avgCost.format(2)}",
            MARGIN.toFloat(),
            currentY,
            paint
        )
        currentY += LINE_HEIGHT
        
        // Service count by type
        canvas.drawText(
            "Total Services: ${serviceRecords.size}",
            MARGIN.toFloat(),
            currentY,
            paint
        )
        currentY += LINE_HEIGHT
        
        // Projected annual maintenance cost
        if (serviceRecords.size >= 2) {
            val sortedRecords = serviceRecords.sortedBy { it.date }
            val timeSpan = sortedRecords.last().date - sortedRecords.first().date
            val years = timeSpan / (365.25 * 24 * 60 * 60 * 1000)
            val annualCost = if (years > 0) totalCost / years else totalCost
            
            canvas.drawText(
                "Projected Annual Cost: ${config.currency} ${annualCost.format(2)}",
                MARGIN.toFloat(),
                currentY,
                paint
            )
            currentY += LINE_HEIGHT
        }
        
        return currentY
    }

    private fun drawMaintenanceSection(
        canvas: Canvas,
        startY: Float,
        maintenanceTasks: List<MaintenanceTask>,
        config: PdfExportConfig
    ): Float {
        val paint = Paint()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", config.locale)
        var currentY = startY + LINE_HEIGHT
        
        // Section title
        paint.apply {
            color = Color.BLACK
            textSize = HEADER_SIZE
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText("Upcoming Maintenance", MARGIN.toFloat(), currentY, paint)
        currentY += LINE_HEIGHT * 1.5f
        
        paint.textSize = BODY_SIZE
        paint.typeface = Typeface.DEFAULT
        
        val activeTasks = maintenanceTasks.filter { it.isActive }
        
        if (activeTasks.isEmpty()) {
            canvas.drawText(
                "No active maintenance tasks scheduled",
                MARGIN.toFloat(),
                currentY,
                paint
            )
            return currentY + LINE_HEIGHT
        }
        
        activeTasks.forEach { task ->
            // Task name and priority
            paint.color = when (task.priority) {
                com.chiefdenis.carsart.data.database.MaintenancePriority.CRITICAL -> Color.RED
                com.chiefdenis.carsart.data.database.MaintenancePriority.HIGH -> Color.parseColor("#FF9800")
                com.chiefdenis.carsart.data.database.MaintenancePriority.MEDIUM -> Color.parseColor("#2196F3")
                com.chiefdenis.carsart.data.database.MaintenancePriority.LOW -> Color.parseColor("#4CAF50")
            }
            
            canvas.drawText(
                "• ${task.taskName} (${task.priority.name})",
                MARGIN.toFloat(),
                currentY,
                paint
            )
            currentY += LINE_HEIGHT
            
            paint.color = Color.BLACK
            
            // Due information
            val dueInfo = mutableListOf<String>()
            
            task.nextDueDate?.let { dueDate ->
                dueInfo.add("Due: ${dateFormat.format(Date(dueDate))}")
            }
            
            task.nextDueMileage?.let { dueMileage ->
                val mileageText = if (config.units == "IMPERIAL") {
                    "${kmToMiles(dueMileage).format(1)} mi"
                } else {
                    "$dueMileage km"
                }
                dueInfo.add("or $mileageText")
            }
            
            if (dueInfo.isNotEmpty()) {
                canvas.drawText(
                    "  ${dueInfo.joinToString(" ")}",
                    MARGIN.toFloat(),
                    currentY,
                    paint
                )
                currentY += LINE_HEIGHT
            }
            
            // Last checked info
            task.lastCheckedDate?.let { lastChecked ->
                canvas.drawText(
                    "  Last checked: ${dateFormat.format(Date(lastChecked))}",
                    MARGIN.toFloat(),
                    currentY,
                    paint
                )
                currentY += LINE_HEIGHT
            }
            
            currentY += LINE_HEIGHT / 2
        }
        
        return currentY
    }

    private fun drawFooter(canvas: Canvas, pageNum: Int, totalPages: Int) {
        val paint = Paint().apply {
            color = Color.GRAY
            textSize = FOOTER_SIZE
            typeface = Typeface.DEFAULT
        }
        
        val footerText = "Generated by CAR SART - Car Service And Repair Tracker"
        val pageInfo = "Page $pageNum of $totalPages"
        
        // Footer text
        canvas.drawText(
            footerText,
            MARGIN.toFloat(),
            (PAGE_HEIGHT - FOOTER_HEIGHT + 15).toFloat(),
            paint
        )
        
        // Page number
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            pageInfo,
            (PAGE_WIDTH - MARGIN).toFloat(),
            (PAGE_HEIGHT - FOOTER_HEIGHT + 15).toFloat(),
            paint
        )
        paint.textAlign = Paint.Align.LEFT
    }

    private fun savePdfFile(document: PdfDocument, vehicle: Vehicle): File? {
        val fileName = "${vehicle.nickname.replace(Regex("[^a-zA-Z0-9]"), "_")}_Service_History_${System.currentTimeMillis()}.pdf"
        
        // Try to save in Downloads first, fallback to app's external files directory
        val downloadsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "CAR_SART")
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        
        val file = File(downloadsDir, fileName)
        
        return try {
            document.writeTo(FileOutputStream(file))
            file
        } catch (e: SecurityException) {
            // Fallback to app's external files directory
            val appDir = File(context.getExternalFilesDir(null), "exports")
            if (!appDir.exists()) {
                appDir.mkdirs()
            }
            
            val fallbackFile = File(appDir, fileName)
            try {
                document.writeTo(FileOutputStream(fallbackFile))
                fallbackFile
            } catch (ex: IOException) {
                null
            }
        } catch (e: IOException) {
            null
        }
    }

    private fun Double.format(digits: Int): String {
        return "%.${digits}f".format(this)
    }
}
