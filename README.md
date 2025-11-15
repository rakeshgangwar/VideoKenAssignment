# VideoKen Assignment

An Android application that enables users to watch YouTube videos while taking voice-recorded notes with precise timestamps. The app converts speech to text and stores notes with their corresponding video positions, allowing users to jump back to specific moments in the video.

## Features

- **YouTube Video Playback**: Play any YouTube video by pasting its URL
- **Voice Note Recording**: Record notes using speech-to-text while watching videos
- **Timestamp Tracking**: Automatically captures the video timestamp when recording notes
- **Note Management**: View, filter, and delete notes
- **Quick Navigation**: Click on any note to jump to that exact moment in the video
- **Persistent Storage**: All notes are saved locally using Realm database

## Screenshots

The application provides an intuitive interface with:
- Video URL input field
- Embedded YouTube player
- Voice recording button
- Notes management controls
- Scrollable list of all recorded notes with timestamps

## Prerequisites

- **Android Studio**: 2.3.2 or higher
- **Android SDK**:
  - Minimum SDK: 21 (Android 5.0 Lollipop)
  - Target SDK: 25 (Android 7.1 Nougat)
  - Compile SDK: 25
- **Build Tools**: 25.0.2
- **Gradle**: 3.3 or compatible version
- **Java Development Kit (JDK)**: 7 or higher

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/rakeshgangwar/VideoKenAssignment.git
cd VideoKenAssignment
```

### 2. Configure YouTube API Key

The application requires a YouTube Data API key. You'll need to:

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the "YouTube Data API v3"
4. Create credentials (API Key)
5. Update the API key in `app/src/main/java/com/rakeshgangwar/videokenassignment/DeveloperKey.java`:

```java
public class DeveloperKey {
    public static final String DEVELOPER_KEY = "YOUR_API_KEY_HERE";
}
```

**SECURITY NOTE**: The current implementation has the API key hardcoded. For production use, consider using:
- Environment variables
- Build config fields
- Secure key management services

### 3. Open in Android Studio

1. Launch Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to the cloned repository and click "OK"
4. Wait for Gradle sync to complete

### 4. Build and Run

1. Connect an Android device via USB or start an Android emulator
2. Click the "Run" button or press `Shift + F10`
3. Select your target device
4. The app will build and install automatically

## Usage

### Playing a Video

1. Launch the application
2. Paste a YouTube video URL in the text field at the top
3. Press Enter on the keyboard
4. The video player will appear and start loading the video

**Supported URL formats:**
```
https://www.youtube.com/watch?v=VIDEO_ID
https://youtu.be/VIDEO_ID
https://www.youtube.com/embed/VIDEO_ID
https://www.youtube.com/v/VIDEO_ID
```

### Recording a Note

1. While a video is playing, click the "Record Note" button with the microphone icon
2. Speak your note when prompted
3. The speech will be converted to text and saved automatically
4. The note will include the current video timestamp

### Managing Notes

- **Show All Notes**: Displays notes from all videos you've watched
- **Show Video Notes**: Displays only notes from the currently loaded video
- **Delete All Notes**: Clears the entire notes database
- **Click on a Note**: Jumps to that specific timestamp in the video

### Example Workflow

```java
// 1. Enter YouTube URL
"https://www.youtube.com/watch?v=dQw4w9WgXcQ"

// 2. Video plays at 00:00

// 3. At 01:23, record a note
User speaks: "Important concept explained here"

// 4. Note saved as:
{
  noteText: "Important concept explained here",
  videoId: "dQw4w9WgXcQ",
  recordingTime: 83000, // milliseconds
  noteId: 1622547890123
}

// 5. Click the note later to jump back to 01:23
```

## Project Structure

```
VideoKenAssignment/
├── app/
│   ├── build.gradle                    # App-level build configuration
│   ├── libs/
│   │   └── YouTubeAndroidPlayerApi.jar # YouTube player library
│   ├── proguard-rules.pro              # ProGuard configuration
│   └── src/
│       ├── androidTest/                # Instrumented tests
│       ├── main/
│       │   ├── AndroidManifest.xml     # App manifest
│       │   ├── java/com/rakeshgangwar/videokenassignment/
│       │   │   ├── AudioNotesObject.java    # Realm data model
│       │   │   ├── DeveloperKey.java        # YouTube API key
│       │   │   ├── MainActivity.java        # Main activity
│       │   │   ├── MyListAdapter.java       # Notes list adapter
│       │   │   └── VideoApplication.java    # Application class
│       │   └── res/
│       │       ├── drawable/           # Icons and drawables
│       │       ├── layout/             # XML layouts
│       │       ├── mipmap/             # App icons
│       │       └── values/             # Strings, colors, styles
│       └── test/                       # Unit tests
├── gradle/                             # Gradle wrapper
├── build.gradle                        # Project-level build config
├── gradle.properties                   # Gradle properties
├── gradlew                             # Gradle wrapper script (Unix)
├── gradlew.bat                         # Gradle wrapper script (Windows)
├── settings.gradle                     # Project settings
└── README.md                           # This file
```

## Technology Stack

### Core Technologies
- **Language**: Java
- **Build System**: Gradle
- **Minimum Android Version**: 5.0 (API 21)
- **Target Android Version**: 7.1 (API 25)

### Key Libraries

| Library | Version | Purpose |
|---------|---------|---------|
| Android Support AppCompat | 25.3.1 | Backward compatibility |
| Android Support Design | 25.3.1 | Material Design components |
| Constraint Layout | 1.0.2 | Flexible layouts |
| Realm Database | 3.3.1 | Local data persistence |
| Realm Android Adapters | 2.0.0 | Realm-ListView integration |
| YouTube Android Player API | - | YouTube video playback |
| JUnit | 4.12 | Unit testing |
| Espresso | 2.2.2 | UI testing |

## Configuration

### Gradle Configuration

The app uses the following Gradle configuration:

```gradle
compileSdkVersion 25
minSdkVersion 21
targetSdkVersion 25
versionCode 1
versionName "1.0"
```

### Permissions

The app requires the following permission:
- `INTERNET`: Required for YouTube video streaming and API access

### ProGuard

ProGuard is currently disabled. To enable code obfuscation for release builds:

```gradle
buildTypes {
    release {
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
    }
}
```

## Architecture

For detailed architecture information, see [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

## Development Guide

For development setup and guidelines, see [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md)

## Known Issues and Limitations

### Security
- API key is hardcoded in source code (should use secure storage)
- No API key rotation mechanism
- Database is not encrypted

### Technical Debt
- Using deprecated `compile` dependency declarations (should use `implementation`)
- Old Android API versions (consider updating to latest)
- Limited error handling for network failures
- No offline mode or caching
- Speech recognition requires Google services

### Functionality
- No support for editing existing notes
- No export functionality for notes
- No search or filter functionality beyond video-specific filtering
- Cannot play videos in background
- No support for playlists

## Testing

### Running Unit Tests

```bash
./gradlew test
```

### Running Instrumented Tests

```bash
./gradlew connectedAndroidTest
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Keep methods focused and concise

## Future Enhancements

- [ ] Migrate to latest Android SDK and AndroidX
- [ ] Add note editing functionality
- [ ] Implement note search and filtering
- [ ] Add export functionality (JSON, CSV, PDF)
- [ ] Support for video playlists
- [ ] Cloud sync for notes
- [ ] Dark mode support
- [ ] Offline video support
- [ ] Note categories/tags
- [ ] Multi-language support

## License

This project is available for educational and portfolio purposes. Please check with the repository owner for specific licensing terms.

## Author

**Rakesh Gangwar**

## Acknowledgments

- YouTube Android Player API by Google
- Realm Database by MongoDB
- Android Support Libraries by Google
- Material Design Icons

## Support

For issues, questions, or contributions, please open an issue on the GitHub repository.

---

**Note**: This application was created as an assignment project. The API key included in the repository should be replaced with your own key before use.
