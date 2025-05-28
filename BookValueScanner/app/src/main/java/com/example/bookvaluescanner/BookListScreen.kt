package com.example.bookvaluescanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookListScreen(
    bookRepository: BookRepository, // Pass the repository instance
    onNavigateBack: () -> Unit
) {
    // Collect the list of books as a state, so the UI recomposes when it changes
    val books by bookRepository.books.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Saved Books", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (books.isEmpty()) {
            Text("No books saved yet.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f), // Takes up available space
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books, key = { book -> book.isbn }) { book ->
                    BookItem(book = book)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateBack) {
            Text("Back to Scanner")
        }
    }
}

@Composable
fun BookItem(book: Book) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ISBN: ${book.isbn}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Title: ${book.title ?: "No Title"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Value/Notes: ${book.manualValue ?: "No Value"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Scanned: ${formatDate(book.scannedDate)}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// Preview for BookListScreen (won't show real data)
// @Preview(showBackground = true, name = "Book List Screen Preview")
// @Composable
// fun DefaultBookListPreview() {
//    BookValueScannerTheme {
//        // You'd need a mock BookRepository for a meaningful preview
//        BookListScreen(bookRepository = BookRepository, onNavigateBack = {})
//    }
// }
