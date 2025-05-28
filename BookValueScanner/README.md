# BookValueScanner App

BookValueScanner is an Android application designed to help users catalog their books and get an idea of their potential value. Users can scan book barcodes, enter book details manually, and (in future versions) look up book prices online.

## Phase 1 Features (Currently Implemented)

*   **Live Barcode Scanning:** Scan book ISBN barcodes using the device camera.
*   **Manual Book Entry:** Manually add or edit book details including ISBN, Title, and a custom value/note.
*   **Local Book List:** View a list of all scanned and manually entered books.
*   **In-Memory Storage:** Book data is currently stored in memory (will be persisted in a future phase).

## Prerequisites

*   [Android Studio](https://developer.android.com/studio) (latest stable version recommended).

## How to Run on Your Android Device

1.  **Install Android Studio:** If you haven't already, download and install Android Studio from the official website.
2.  **Get the Source Code:** Clone or download the source code for this project onto your computer.
3.  **Open the Project:**
    *   Launch Android Studio.
    *   Select "Open an Existing Project" (or a similar option).
    *   Navigate to and select the root directory of the `BookValueScanner` project.
    *   Allow Android Studio to import the project and download any necessary Gradle dependencies. This might take a few minutes.
4.  **Enable Developer Options and USB Debugging on Your Phone:**
    *   On your Android phone, go to **Settings > About phone**.
    *   Tap on **Build number** repeatedly (usually 7 times) until you see a message like "You are now a developer!".
    *   Go back to **Settings** and find **Developer options** (this might be under **System** or a similar category depending on your Android version and manufacturer).
    *   Inside Developer options, enable **USB debugging**.
5.  **Connect Your Phone:** Connect your Android phone to your computer using a USB cable.
6.  **Authorize Connection:**
    *   On your phone, a prompt might appear asking to "Allow USB debugging?" or "Trust this computer?". Accept this prompt.
7.  **Run the App from Android Studio:**
    *   In Android Studio's toolbar, you should see your phone listed as a target device (e.g., "Pixel 6 Pro"). If not, wait a moment for it to be recognized or check your USB connection.
    *   Select your phone as the deployment target.
    *   Click the **Run 'app'** button (it looks like a green play triangle ▶️) or choose **Run > Run 'app'** from the menu.
    *   Android Studio will build the app, install the `.apk` file onto your phone, and then automatically launch it.

## Permissions

The app will request **Camera permission** when you first try to use the barcode scanning feature. This is required for the scanner to access your device's camera.
