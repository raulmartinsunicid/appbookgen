package com.example.bookvaluescanner

/**
 * Data class representing a book.
 *
 * @property isbn The ISBN of the book (usually from barcode scanning).
 * @property title The title of the book (optional, can be added manually).
 * @property scannedDate Timestamp (milliseconds since epoch) when the book was scanned/added.
 * @property manualValue User-defined value or notes for the book (optional).
 */
data class Book(
    val isbn: String,
    val title: String? = null,
    val scannedDate: Long,
    val manualValue: String? = null
)
