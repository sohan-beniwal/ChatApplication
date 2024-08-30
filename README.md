# ChatApplication

**ChatApplication** is an Android application developed in Kotlin that allows users to securely chat with each other. The app provides features for login and signup using Firebase Authentication, stores chat data in Firebase Realtime Database, and ensures data security through AES encryption.

## Features

- **User Authentication:**
  - Secure login and signup using Firebase Authentication.
  - Password recovery via email.

- **Chat Functionality:**
  - Real-time chat between users.
  - Chat messages are securely stored in Firebase Realtime Database.
  - Messages are encrypted using AES encryption to ensure data privacy.

- **Real-time Data Synchronization:**
  - Automatic synchronization of chat messages across devices.
  - Offline access to previously stored chat messages.

## Tech Stack

- **Kotlin:** Primary programming language used for developing the app.
- **Firebase Authentication:** Used for user login and signup functionalities.
- **Firebase Realtime Database:** Used to store and retrieve chat messages.
- **AES Encryption:** Used to encrypt chat messages for enhanced security.
- **XML:** Used for designing the user interface.

## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/sohan-beniwal/ChatApplication.git
   ```
2. **Open the project in Android Studio.**

3. **Set up Firebase:**
   - Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/).
   - Add your Android app to the Firebase project.
   - Download the `google-services.json` file and place it in the `app` directory.
   - Enable Firebase Authentication and Realtime Database in your Firebase console.

4. **Build the project:**
   - Sync the project with Gradle files.
   - Build and run the app on an emulator or a physical device.

## Usage

1. **Sign Up / Login:**
   - Open the app and create an account or log in with an existing account.

2. **Start Chatting:**
   - Send and receive messages in real-time.
   - Messages are securely stored and encrypted.

3. **View Messages:**
   - Access your chat messages from any device with real-time synchronization.

## Contributing

Contributions are welcome! Please fork this repository and submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
