# Critter Calls

## Overview

Welcome to the Critter Calls Android App! This application allows you to classify animal sounds using machine learning. Follow the steps below to set up and run the app.

## Prerequisites

- Android Studio installed on your machine.
- A physical Android device with a microphone for optimal performance or an emulator with a microphone enabled.
- **Internet connection for the initial setup overall app usage**

## Getting Started

### Using the APK File with Android Emulator

1. **Extract the Zip File:**
   - Extract the contents of the provided zip file to a directory on your local machine.

2. **Install the App:**
   - Locate the APK file in the extracted directory.
   - Drag and drop the APK file into your Android emulator.

### Using the APK File with a Physical Android Device

1. **Extract the Zip File:**
   - Extract the contents of the provided zip file to a directory on your local machine.

2. **Copy APK to Android via USB:**
   - Link your Android handset to the PC via a USB cable.
   - Copy and paste the APK file from your computer to your Android folder.

3. **Install the App on your Phone:**
   - Open your "My Files" app on your Android handset.
   - Under the Categories section, you should see the APK Installation files. Select it, and it should show you the Critter-Calls.apk.
   - Download the app to your Android handset. A pop-up will appear stating that the app is unsafe and blocked as Play Protect doesn't recognize the app's developers.
   - Click on the "More details" dropdown and click "Install anyway."

### Using the Android Project

1. **Extract the Zip File:**
   - Extract the contents of the provided zip file to a directory on your local machine.

2. **Open Project in Android Studio:**
   - Launch Android Studio.
   - Open the project by selecting "Open an existing Android Studio project" and choosing the extracted directory.

3. **Build Gradle:**
   - Once the project is loaded, wait for Android Studio to sync and build the Gradle files.
   - Click on the "Sync Now" link if prompted.

## Running the App

1. **Run the App:**
   - Open the app using your selected method of installization.

2. **Account Registration:**
   - Register a new account within the app or use the test account:
     - **Username:** johnsmith@gmail.com
     - **Password:** 123456
- After registering, you will be automatically directed to the home screen of the app.

3. **Login:**
   - If not already logged in after registering, use the registered or test account credentials to log in to the application. You can log in again using the same credentials.

4. **Record and Analyze:**
   - Navigate to the "Record" section of the app.
   - Start recording an animal sound using the app's built-in recording feature.

5. **Grant Audio Permissions:**
   - When attempting to record audio in the classification module, the app will request permission to access the device's microphone.
   - Allow the app to access audio for sound classification.

6. **View Classification Results:**
   - After recording, the machine learning model will analyze the audio.

## Troubleshooting

- If you encounter any issues with the app, make sure your device or emulator has the necessary audio permissions.
- Ensure that the microphone is functional and properly connected to the device.
- Ensure a stable internet connection during usage of the app as the framework is based off of Firebase.

## Note

For optimal results, it is recommended to use a physical Android device with a built-in microphone or connect an external microphone to the computer when using an emulator. This enhances the accuracy of the animal sound classification.
