package com.example.bookvaluescanner

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Singleton repository for managing Book data in-memory.
 */
object BookRepository {
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    /**
     * Adds a new book to the in-memory list.
     * If a book with the same ISBN already exists, it updates the existing entry.
     * Otherwise, it adds the new book.
     *
     * @param book The Book object to add or update.
     */
    fun addBook(book: Book) {
        _books.update {
            currentBooks ->
            val existingBookIndex = currentBooks.indexOfFirst { it.isbn == book.isbn }
            if (existingBookIndex != -1) {
                // Update existing book (e.g., if title or manualValue is added/changed later)
                currentBooks.toMutableList().apply { this[existingBookIndex] = book }
            } else {
                currentBooks + book
            }
        }
    }

    /**
     * Returns an immutable list of all books currently in the repository.
     *
     * @return A List<Book> containing all stored books.
     */
    fun getAllBooks(): List<Book> {
        return _books.value
    }

    /**
     * Retrieves a book by its ISBN.
     *
     * @param isbn The ISBN of the book to find.
     * @return The Book object if found, otherwise null.
     */
    fun getBookByIsbn(isbn: String): Book? {
        return _books.value.find { it.isbn == isbn }
    }

    /**
     * Clears all books from the repository.
     */
    fun clearAllBooks() {
        _books.value = emptyList()
    }

    /**
     * Adds or updates a book, primarily for use when a book is first scanned.
     * If the book exists, its scannedDate and potentially manualValue might be updated.
     * If not, a new book is created.
     *
     * @param isbn The ISBN of the book.
     * @param title Optional title.
     * @param manualValue Optional manual value.
     */
    fun addOrUpdateScannedBook(isbn: String, title: String? = null, manualValue: String? = null) {
        val existingBook = getBookByIsbn(isbn)
        val bookToAddOrUpdate = Book(
            isbn = isbn,
            title = existingBook?.title ?: title, // Keep existing title if not provided
            scannedDate = System.currentTimeMillis(), // Always update scannedDate on new scan/update
            manualValue = manualValue ?: existingBook?.manualValue // Keep existing value if not provided
        )
        addBook(bookToAddOrUpdate)
    }
}
