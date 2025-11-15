# Development Guide

## Table of Contents

1. [Getting Started](#getting-started)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Configuration](#project-configuration)
4. [Building the Project](#building-the-project)
5. [Running the Application](#running-the-application)
6. [Testing](#testing)
7. [Debugging](#debugging)
8. [Common Development Tasks](#common-development-tasks)
9. [Troubleshooting](#troubleshooting)
10. [Code Style Guidelines](#code-style-guidelines)
11. [Git Workflow](#git-workflow)

## Getting Started

### Prerequisites Checklist

Before you begin development, ensure you have:

- [ ] Android Studio 2.3.2 or higher installed
- [ ] Java Development Kit (JDK) 7 or higher
- [ ] Android SDK with API level 25 installed
- [ ] Git installed and configured
- [ ] At least 4GB of RAM (8GB recommended)
- [ ] 10GB of free disk space

### Quick Start

```bash
# Clone the repository
git clone https://github.com/rakeshgangwar/VideoKenAssignment.git

# Navigate to project directory
cd VideoKenAssignment

# Open in Android Studio
# File → Open → Select VideoKenAssignment directory

# Wait for Gradle sync to complete

# Configure your YouTube API key (see below)

# Run the app
# Click Run button or press Shift+F10
```

## Development Environment Setup

### 1. Installing Android Studio

#### Windows
1. Download Android Studio from [developer.android.com](https://developer.android.com/studio)
2. Run the installer
3. Follow the setup wizard
4. Install Android SDK Platform 25 (API Level 25)
5. Install Build Tools 25.0.2

#### macOS
```bash
# Using Homebrew
brew install --cask android-studio

# Or download manually from developer.android.com
```

#### Linux
```bash
# Download and extract
wget https://redirector.gvt1.com/edgedl/android/studio/ide-zips/[version]/android-studio-ide-[version]-linux.tar.gz
tar -xvf android-studio-ide-[version]-linux.tar.gz

# Run Android Studio
cd android-studio/bin
./studio.sh
```

### 2. Configuring Android SDK

1. Open Android Studio
2. Navigate to: **Tools** → **SDK Manager**
3. Install the following:
   - **SDK Platforms**:
     - Android 7.1.1 (API Level 25)
     - Android 5.0 (API Level 21)
   - **SDK Tools**:
     - Android SDK Build-Tools 25.0.2
     - Android SDK Platform-Tools
     - Android SDK Tools
     - Google Play services

### 3. Setting up Android Emulator

#### Create Virtual Device

```
Tools → AVD Manager → Create Virtual Device
```

**Recommended Configuration**:
- Device: Pixel 2
- System Image: Android 7.1.1 (API 25)
- RAM: 2048 MB
- Internal Storage: 2048 MB
- Enable Hardware Keyboard

#### Alternative: Physical Device

1. Enable Developer Options on your Android device:
   - Go to **Settings** → **About Phone**
   - Tap **Build Number** 7 times
2. Enable USB Debugging:
   - **Settings** → **Developer Options** → **USB Debugging**
3. Connect via USB cable
4. Accept debugging permission on device

## Project Configuration

### YouTube API Key Setup

#### Step 1: Create Google Cloud Project

1. Visit [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Note your project name/ID

#### Step 2: Enable YouTube Data API

```
Navigation Menu → APIs & Services → Library → Search "YouTube Data API v3" → Enable
```

#### Step 3: Create API Credentials

```
APIs & Services → Credentials → Create Credentials → API Key
```

**Important**: Restrict your API key:
- Application restrictions: Android apps
- Add your app's SHA-1 fingerprint
- Add package name: `com.rakeshgangwar.videokenassignment`

#### Step 4: Configure in Project

Edit `app/src/main/java/com/rakeshgangwar/videokenassignment/DeveloperKey.java`:

```java
public class DeveloperKey {
    public static final String DEVELOPER_KEY = "YOUR_API_KEY_HERE";
}
```

**Better Approach** (for production):

Create `local.properties` (ignored by git):
```properties
youtube.api.key=YOUR_API_KEY_HERE
```

Update `app/build.gradle`:
```gradle
android {
    defaultConfig {
        // Load API key from local.properties
        Properties properties = new Properties()
        properties.load(project.rootProject.file('local.properties').newDataInputStream())
        buildConfigField "String", "YOUTUBE_API_KEY", "\"${properties.getProperty('youtube.api.key')}\""
    }
}
```

Update `DeveloperKey.java`:
```java
public class DeveloperKey {
    public static final String DEVELOPER_KEY = BuildConfig.YOUTUBE_API_KEY;
}
```

### Gradle Configuration

#### Project-level `build.gradle`

```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.2'
        classpath "io.realm:realm-gradle-plugin:3.3.1"
    }
}

allprojects {
    repositories {
        jcenter()
    }
}
```

#### Module-level `app/build.gradle`

```gradle
apply plugin: 'com.android.application'
apply plugin: 'realm-android'

android {
    compileSdkVersion 25
    buildToolsVersion "25.0.2"

    defaultConfig {
        applicationId "com.rakeshgangwar.videokenassignment"
        minSdkVersion 21
        targetSdkVersion 25
        versionCode 1
        versionName "1.0"
    }
}

dependencies {
    compile 'com.android.support:appcompat-v7:25.3.1'
    compile 'com.android.support:design:25.3.1'
    compile 'com.android.support.constraint:constraint-layout:1.0.2'
    compile 'io.realm:android-adapters:2.0.0'
    compile files('libs/YouTubeAndroidPlayerApi.jar')

    testCompile 'junit:junit:4.12'
    androidTestCompile 'com.android.support.test.espresso:espresso-core:2.2.2'
}
```

## Building the Project

### Command Line Build

```bash
# Clean the project
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Output location:
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release-unsigned.apk
```

### Android Studio Build

```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

### Build Variants

```
Build → Select Build Variant
```

- **Debug**: Includes debugging information, not optimized
- **Release**: Optimized, requires signing for distribution

## Running the Application

### From Android Studio

1. Select run configuration: `app`
2. Select target device (emulator or physical)
3. Click **Run** button or press `Shift + F10`

### From Command Line

```bash
# Install on connected device
./gradlew installDebug

# Run app
adb shell am start -n com.rakeshgangwar.videokenassignment/.MainActivity
```

### Viewing Logs

```bash
# View all logs
adb logcat

# Filter by app package
adb logcat | grep "com.rakeshgangwar.videokenassignment"

# Clear logs
adb logcat -c
```

## Testing

### Unit Tests

#### Location
`app/src/test/java/com/rakeshgangwar/videokenassignment/`

#### Running Tests

**Android Studio**:
```
Right-click on test class → Run 'ExampleUnitTest'
```

**Command Line**:
```bash
./gradlew test

# View results:
# app/build/reports/tests/testDebugUnitTest/index.html
```

#### Example Unit Test

```java
@Test
public void testYouTubeIdExtraction() {
    String url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    String videoId = MainActivity.extractYTId(url);
    assertEquals("dQw4w9WgXcQ", videoId);
}
```

### Instrumented Tests

#### Location
`app/src/androidTest/java/com/rakeshgangwar/videokenassignment/`

#### Running Tests

**Android Studio**:
```
Right-click on test class → Run 'ExampleInstrumentedTest'
```

**Command Line**:
```bash
./gradlew connectedAndroidTest
```

#### Example Instrumented Test

```java
@Test
public void testRealmDatabaseCreation() {
    Context appContext = InstrumentationRegistry.getTargetContext();
    assertEquals("com.rakeshgangwar.videokenassignment", appContext.getPackageName());

    // Test Realm is accessible
    Realm realm = Realm.getDefaultInstance();
    assertNotNull(realm);
    realm.close();
}
```

### Test Coverage

```bash
# Generate coverage report
./gradlew createDebugCoverageReport

# View report:
# app/build/reports/coverage/debug/index.html
```

## Debugging

### Debugging in Android Studio

1. Set breakpoints by clicking left margin of code editor
2. Click **Debug** button or press `Shift + F9`
3. Use debug controls:
   - **Step Over** (F8): Execute current line
   - **Step Into** (F7): Enter method call
   - **Step Out** (Shift + F8): Exit current method
   - **Resume** (F9): Continue execution

### Inspecting Realm Database

#### Using Realm Studio

1. Download [Realm Studio](https://realm.io/products/realm-studio)
2. Locate database file:
   ```bash
   adb pull /data/data/com.rakeshgangwar.videokenassignment/files/default.realm
   ```
3. Open file in Realm Studio

#### Using ADB

```bash
# Access app's database directory
adb shell
run-as com.rakeshgangwar.videokenassignment
cd /data/data/com.rakeshgangwar.videokenassignment/files/
ls -la
```

### Debugging YouTube Player

Enable YouTube Player debugging in `MainActivity.java`:

```java
@Override
public void onInitializationFailure(YouTubePlayer.Provider provider,
                                    YouTubeInitializationResult result) {
    Log.e("VideoKen", "YouTube initialization failed: " + result.toString());
    Toast.makeText(this, "Error: " + result.toString(), Toast.LENGTH_LONG).show();
}
```

### Network Debugging

```bash
# Monitor network activity
adb shell setprop log.tag.HttpClient DEBUG
adb logcat HttpClient:D *:S
```

## Common Development Tasks

### Adding a New Feature

1. Create feature branch
   ```bash
   git checkout -b feature/my-new-feature
   ```

2. Implement changes

3. Add tests

4. Test thoroughly

5. Create pull request

### Updating Dependencies

Edit `app/build.gradle`:

```gradle
dependencies {
    compile 'com.android.support:appcompat-v7:25.3.1' // Update version
}
```

Sync Gradle:
```
File → Sync Project with Gradle Files
```

### Adding Resources

#### Strings
Edit `app/src/main/res/values/strings.xml`:
```xml
<string name="my_new_string">My Text</string>
```

#### Drawables
Place files in `app/src/main/res/drawable/`

#### Layouts
Create in `app/src/main/res/layout/`

### Database Schema Changes

When modifying `AudioNotesObject.java`, update schema version:

```java
// In VideoApplication.java
RealmConfiguration config = new RealmConfiguration.Builder()
    .schemaVersion(2) // Increment version
    .migration(new MyMigration())
    .build();
Realm.setDefaultConfiguration(config);
```

### Generating Signed APK

1. **Create Keystore**:
   ```bash
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
   ```

2. **Configure signing in `app/build.gradle`**:
   ```gradle
   android {
       signingConfigs {
           release {
               storeFile file("my-release-key.jks")
               storePassword "password"
               keyAlias "my-key-alias"
               keyPassword "password"
           }
       }
       buildTypes {
           release {
               signingConfig signingConfigs.release
           }
       }
   }
   ```

3. **Build**:
   ```bash
   ./gradlew assembleRelease
   ```

## Troubleshooting

### Common Issues

#### 1. Gradle Sync Failed

**Problem**: "Failed to resolve: com.android.support:appcompat-v7:25.3.1"

**Solution**:
```bash
# Update repositories in build.gradle
repositories {
    google()
    jcenter()
}
```

#### 2. YouTube Player Not Loading

**Problem**: Video player shows error or doesn't load

**Checklist**:
- [ ] Valid API key configured
- [ ] Internet permission in manifest
- [ ] Device has Google Play Services
- [ ] Valid YouTube URL format
- [ ] Device is online

**Debug**:
```java
Log.d("VideoKen", "Video ID: " + extractYTId(videoUrl.getText().toString()));
Log.d("VideoKen", "API Key: " + DeveloperKey.DEVELOPER_KEY.substring(0, 10) + "...");
```

#### 3. Realm Database Error

**Problem**: "Realm file cannot be opened"

**Solution**:
```bash
# Clear app data
adb shell pm clear com.rakeshgangwar.videokenassignment
```

#### 4. Speech Recognition Not Working

**Problem**: "Not supported on this device"

**Checklist**:
- [ ] Device has Google app installed
- [ ] Internet connection available
- [ ] Microphone permission granted (implicit through Google app)

#### 5. Build Takes Too Long

**Solution**: Enable Gradle daemon and parallel builds

`gradle.properties`:
```properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.configureondemand=true
```

#### 6. Out of Memory During Build

**Solution**: Increase heap size

`gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m
```

### Getting Help

1. Check [Stack Overflow](https://stackoverflow.com/questions/tagged/android)
2. Review [Android Documentation](https://developer.android.com/docs)
3. Check [Realm Documentation](https://docs.mongodb.com/realm/)
4. Review YouTube API [documentation](https://developers.google.com/youtube/android/player)

## Code Style Guidelines

### Java Conventions

```java
// Class names: PascalCase
public class MyClassName { }

// Methods and variables: camelCase
public void myMethodName() {
    int myVariableName = 0;
}

// Constants: UPPER_SNAKE_CASE
public static final int MAX_COUNT = 100;

// Private fields: camelCase with descriptive names
private YouTubePlayer player;
private EditText videoUrl;
```

### Comments

```java
/**
 * Javadoc for public methods and classes
 *
 * @param videoUrl The YouTube video URL to parse
 * @return Extracted video ID or null if invalid
 */
public static String extractYTId(String videoUrl) {
    // Single-line comments for complex logic
    Pattern pattern = Pattern.compile(...);

    /* Multi-line comments for
       longer explanations */
    return videoId;
}
```

### Layout XML

```xml
<!-- Use consistent indentation (4 spaces) -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <!-- Group related attributes -->
    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/my_text"
        android:textSize="16sp"
        android:padding="8dp" />
</LinearLayout>
```

### Best Practices

1. **Always use string resources** instead of hardcoded strings
2. **Close resources** in onDestroy() or try-with-resources
3. **Handle configuration changes** (screen rotation)
4. **Use meaningful names** for variables and methods
5. **Keep methods short** (< 50 lines when possible)
6. **Add null checks** where appropriate
7. **Use @Override** annotation consistently

## Git Workflow

### Branch Strategy

```
main
  ├── develop
  │   ├── feature/add-note-editing
  │   ├── feature/export-notes
  │   └── bugfix/fix-url-parsing
  └── hotfix/critical-crash-fix
```

### Commit Messages

```bash
# Good commit messages
git commit -m "Add note editing functionality"
git commit -m "Fix YouTube URL parsing for short URLs"
git commit -m "Update Realm dependency to 3.5.0"

# Bad commit messages (avoid these)
git commit -m "fixed stuff"
git commit -m "changes"
git commit -m "asdf"
```

### Pre-commit Checklist

- [ ] Code compiles without errors
- [ ] No new warnings introduced
- [ ] Tests pass
- [ ] Code formatted consistently
- [ ] No debug logs or commented code
- [ ] API keys not hardcoded
- [ ] Meaningful commit message

### Useful Git Commands

```bash
# View current status
git status

# Create and switch to new branch
git checkout -b feature/my-feature

# Stage changes
git add .

# Commit changes
git commit -m "Descriptive message"

# Push to remote
git push origin feature/my-feature

# Pull latest changes
git pull origin develop

# View commit history
git log --oneline --graph

# Discard local changes
git checkout -- <file>
```

## Performance Tips

### Optimizing Build Times

1. **Use Gradle daemon**: Already enabled in `gradle.properties`
2. **Avoid clean builds**: Only when necessary
3. **Use instant run**: Enable in Android Studio settings
4. **Exclude unnecessary modules**: Keep project focused

### Runtime Performance

1. **Reuse ViewHolders**: Already implemented in `MyListAdapter`
2. **Avoid memory leaks**:
   - Close Realm instances
   - Release YouTube player
   - Avoid static references to contexts
3. **Use background threads** for heavy operations
4. **Optimize database queries**: Use indexed fields

## Additional Resources

### Documentation
- [Android Developer Guides](https://developer.android.com/guide)
- [Realm Java Documentation](https://docs.mongodb.com/realm/sdk/java/)
- [YouTube Android Player API](https://developers.google.com/youtube/android/player)

### Tools
- [Android Studio Tips](https://developer.android.com/studio/intro)
- [ADB Commands](https://developer.android.com/studio/command-line/adb)
- [Gradle Build Tool](https://gradle.org/guides/)

### Community
- [r/androiddev](https://www.reddit.com/r/androiddev/)
- [Android Developers Discord](https://discord.gg/android)
- [Stack Overflow Android Tag](https://stackoverflow.com/questions/tagged/android)

---

**Document Version**: 1.0
**Last Updated**: 2025-11-15
**Maintained By**: Development Team
