# Critter Calls

## Overview

Welcome to the Critter Calls Android App! This application allows you to classify animal sounds using machine learning. Follow the steps below to set up and run the app.

## Prerequisites

- Android Studio installed on your machine.
- A physical Android device with a microphone for optimal performance or an emulator with a microphone enabled.
- **Internet connection for the initial setup overall app usage**

## Getting Started

1. **Extract the Zip File:**
   - Extract the contents of the provided zip file to a directory on your local machine.

2. **Open Project in Android Studio:**
   - Launch Android Studio.
   - Open the project by selecting "Open an existing Android Studio project" and choosing the extracted directory.

3. **Build Gradle:**
   - Once the project is loaded, wait for Android Studio to sync and build the Gradle files.
   - Click on the "Sync Now" link if prompted.

4. **Register an Account:**
   - Register a new account within the app or use the test account:
     - **Username:** johnsmith@gmail.com
     - **Password:** 123456

5. **Login:**
   - Use the registered or test account credentials to log in to the application.

6. **Record and Analyze:**
   - Use a physical Android device with a microphone or plug in a microphone to the computer for an emulated device.
   - Navigate to the "Sound Classification" section of the app.
   - Start recording an animal sound using the app's built-in recording feature.

7. **Allow Audio Permissions:**
   - When attempting to record audio in the classification module, the app will request permission to access the device's microphone.
   - Allow the app to access audio for sound classification.

8. **View Classification Results:**
   - After recording, the machine learning model will analyze the audio and display the classification results.

## Troubleshooting

- If you encounter any issues with the app, make sure your device or emulator has the necessary audio permissions.
- Ensure that the microphone is functional and properly connected to the device.
- **Ensure a stable internet connection during usage of the app as the framework is based off of Firebase.**

## Note

For optimal results, it is recommended to use a physical Android device with a built-in microphone or connect an external microphone to the computer when using an emulator. This enhances the accuracy of the animal sound classification.
