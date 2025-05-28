package com.example.bookvaluescanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Enum to manage different screens/UI states
enum class Screen {
    SCANNER,
    MANUAL_ENTRY,
    BOOK_LIST // Added BOOK_LIST screen
}

class MainActivity : ComponentActivity() {

    private var currentScreen by mutableStateOf(Screen.SCANNER)
    private var barcodeResult by mutableStateOf("No barcode scanned yet.")
    private var lastScannedIsbn by mutableStateOf<String?>(null)

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            barcodeResult = "Camera permission granted."
        } else {
            barcodeResult = "Camera permission denied. Cannot scan barcodes."
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        setContent {
            BookValueScannerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (currentScreen) {
                        Screen.SCANNER -> {
                            ScannerScreen(
                                barcodeValue = barcodeResult,
                                onRequestPermission = { checkCameraPermission() },
                                onNavigateToManualEntry = { isbn ->
                                    lastScannedIsbn = isbn
                                    currentScreen = Screen.MANUAL_ENTRY
                                },
                                onNavigateToBookList = {
                                    currentScreen = Screen.BOOK_LIST
                                }
                            )
                        }
                        Screen.MANUAL_ENTRY -> {
                            ManualBookEntryScreen(
                                initialIsbn = lastScannedIsbn,
                                bookRepository = BookRepository,
                                onNavigateBack = {
                                    currentScreen = Screen.SCANNER
                                    lastScannedIsbn = null
                                }
                            )
                        }
                        Screen.BOOK_LIST -> {
                            BookListScreen(
                                bookRepository = BookRepository,
                                onNavigateBack = {
                                    currentScreen = Screen.SCANNER
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkCameraPermission() {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                barcodeResult = "Permission already granted. Starting camera."
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    @Composable
    fun ScannerScreen(
        barcodeValue: String,
        onRequestPermission: () -> Unit,
        onNavigateToManualEntry: (String?) -> Unit,
        onNavigateToBookList: () -> Unit // Added navigation callback for BookList
    ) {
        val context = LocalContext.current
        var hasCameraPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }

        LaunchedEffect(key1 = true) {
            if (!hasCameraPermission) {
                onRequestPermission()
            }
        }

        LaunchedEffect(key1 = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (hasCameraPermission) {
                Box(modifier = Modifier.weight(1f)) {
                    CameraPreview { imageProxy ->
                        processImageProxy(imageProxy) { resultIsbn, resultMessage ->
                            barcodeResult = resultMessage
                            lastScannedIsbn = resultIsbn
                        }
                    }
                }
            }
            Text(
                modifier = Modifier.padding(16.dp),
                text = barcodeValue
            )
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { onNavigateToManualEntry(lastScannedIsbn) }) {
                    Text("Add/Edit Manually")
                }
                Button(onClick = onNavigateToBookList) { // New Button
                    Text("View Saved Books")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    @Composable
    fun CameraPreview(onImageProxyProcessed: (ImageProxy) -> Unit) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraProvider = remember(cameraProviderFuture) { cameraProviderFuture.get() }
        val previewView = remember { PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }}

        LaunchedEffect(key1 = cameraProvider) {
            try {
                cameraProvider.unbindAll()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, BarcodeAnalyzer(onImageProxyProcessed))
                    }

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "Use case binding failed", e)
                barcodeResult = "Error starting camera: ${e.localizedMessage}"
            }
        }
        AndroidView({ previewView })
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy, onResult: (String?, String) -> Unit) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val firstBarcode = barcodes.first()
                    val scannedIsbn = firstBarcode.rawValue
                    if (scannedIsbn != null) {
                        BookRepository.addOrUpdateScannedBook(isbn = scannedIsbn)
                        onResult(scannedIsbn, "Book saved: $scannedIsbn")
                    } else {
                        onResult(null, "Scanned: No value in barcode.")
                    }
                } else {
                    // No barcode found
                }
            }
            .addOnFailureListener { e ->
                Log.e("BarcodeScanner", "Barcode scanning failed", e)
                onResult(null, "Scan failed: ${e.localizedMessage}")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private class BarcodeAnalyzer(private val onImageProxyProcessed: (ImageProxy) -> Unit) : ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            onImageProxyProcessed(imageProxy)
        }
    }
}

@Composable
fun BookValueScannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = MaterialTheme.typography,
        content = content
    )
}

@Preview(showBackground = true, name = "Scanner Screen Preview")
@Composable
fun DefaultScannerPreview() {
    BookValueScannerTheme {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Text("Camera Preview Area (placeholder)", modifier = Modifier.weight(1f))
            Text("No barcode scanned yet.")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                 Button(onClick = { }) {
                    Text("Add/Edit Manually")
                }
                Button(onClick = { }) {
                    Text("View Saved Books")
                }
            }
        }
    }
}
