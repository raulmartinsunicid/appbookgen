package com.example.bookvaluescanner

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualBookEntryScreen(
    initialIsbn: String?,
    bookRepository: BookRepository, // Pass the repository instance
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    var isbn by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var manualValue by remember { mutableStateOf("") }

    // Pre-fill ISBN if provided (e.g., from a scan)
    // Also load existing book details if ISBN matches an existing book
    LaunchedEffect(initialIsbn) {
        if (initialIsbn != null) {
            isbn = initialIsbn
            val existingBook = bookRepository.getBookByIsbn(initialIsbn)
            if (existingBook != null) {
                title = existingBook.title ?: ""
                manualValue = existingBook.manualValue ?: ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Enter Book Details", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = isbn,
            onValueChange = { isbn = it },
            label = { Text("ISBN*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = manualValue,
            onValueChange = { manualValue = it },
            label = { Text("Notes / Manual Value") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (isbn.isBlank()) {
                Toast.makeText(context, "ISBN cannot be empty.", Toast.LENGTH_SHORT).show()
                return@Button
            }
            // Use addOrUpdateScannedBook to handle both new and existing books correctly
            bookRepository.addOrUpdateScannedBook(
                isbn = isbn.trim(),
                title = title.takeIf { it.isNotBlank() }, // Store null if blank
                manualValue = manualValue.takeIf { it.isNotBlank() } // Store null if blank
            )
            Toast.makeText(context, "Book details saved for ISBN: $isbn", Toast.LENGTH_LONG).show()
            // Optionally clear fields or navigate back
            // For now, just show toast. User can use back button.
        }) {
            Text("Save Book Details")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateBack) {
            Text("Back to Scanner")
        }
    }
}
