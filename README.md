# 📚 Digital Learning App (EduReach)

A comprehensive Android educational application designed for rural learning, featuring AI-powered tutoring, interactive games, and multi-language support.

⚠️ Security Notice

This repository has been updated to follow secure practices. Sensitive configuration values such as API keys are no longer stored in the codebase.

Please configure your own API keys using local configuration files (e.g., local.properties or environment variables) before running the project.

## 🚀 Features

- **AI-Powered Tutor**: Gemini AI integration for personalized learning assistance
- **Multi-Language Support**: Hindi, Punjabi, and English
- **Interactive Games**: Educational games for Classes 1-10
- **Offline Content**: Download and access educational materials offline
- **Communication Skills**: Daily speaking practice, pronunciation trainer, vocabulary builder
- **Progress Tracking**: Monitor student learning progress
- **Teacher Dashboard**: Content management and student monitoring

## 📋 Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Java 11 or higher
- Firebase account
- Google Gemini API key

## 🔧 Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/tannuk716/Digital-Learning-App.git
cd Digital-Learning-App
```

### 2. Configure API Keys

#### Create `local.properties`
```bash
cp local.properties.example local.properties
```

Edit `local.properties` and add:
```properties
sdk.dir=/path/to/your/Android/Sdk
GEMINI_API_KEY=your_gemini_api_key_here
```

#### Add Firebase Configuration
```bash
cp app/google-services.json.example app/google-services.json
```

Download your `google-services.json` from [Firebase Console](https://console.firebase.google.com/) and replace the example file.

### 3. Get Required API Keys

#### Gemini API Key
1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create a new API key
3. Add it to `local.properties`

#### Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing
3. Add an Android app with package name: `com.tannu.edureach`
4. Download `google-services.json`
5. Place it in `app/` directory

### 4. Build the Project

```bash
./gradlew clean build
```

Or in Android Studio:
- Build → Clean Project
- Build → Rebuild Project

## 📱 Running the App

### Debug Build
```bash
./gradlew installDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/tannu/edureach/
│   │   ├── games/          # Educational games
│   │   ├── learn/          # Learning modules
│   │   ├── practice/       # Quiz and practice
│   │   ├── utils/          # Utility classes
│   │   └── data/           # Data models and repositories
│   ├── res/                # Resources (layouts, drawables, etc.)
│   └── AndroidManifest.xml
├── build.gradle.kts
└── google-services.json    # (Not in repo - add manually)
```

## 🔐 Security

- **Never commit** `local.properties` or `google-services.json`
- **Always use** environment variables for sensitive data
- **Regenerate** API keys if accidentally exposed
- **Review** commits before pushing

See [SECURITY_SETUP.md](SECURITY_SETUP.md) for detailed security guidelines.

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 📦 Dependencies

- Firebase (Authentication, Firestore, Storage)
- Retrofit (API calls)
- Gemini AI SDK
- Material Design Components
- Lottie Animations
- ExoPlayer (Video playback)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For issues or questions:
- Create an issue on GitHub
- Mail Id - tannuk716@gmail.com

## 🔗 Links

- [Firebase Console](https://console.firebase.google.com/)
- [Google AI Studio](https://makersuite.google.com/app/apikey)
- [Android Developer Docs](https://developer.android.com/)

---

**Last Updated**: April 6, 2026  
**Version**: 1.0.0  
**Status**: ✅ Repository Secured
