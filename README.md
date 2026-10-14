# Radhe Radhe AI Android starter project

## Build APK using GitHub Actions
1. Upload the contents of this folder to your GitHub repository root (not the ZIP itself).
2. Keep `.github/workflows/build-apk.yml` included.
3. Open the repository's Actions tab and select `Build Radhe Radhe AI APK`.
4. Run workflow, wait for it to finish, and download the `RadheRadheAI-debug-apk` artifact.
5. Extract the artifact ZIP; it contains `app-debug.apk`.

## Notes
- This is a starter app with Hindi speech input, text-to-speech, and optional DeepSeek chat API integration.
- A DeepSeek API key is required for online AI answers. The key is entered locally in the app and is not included in this repository.
- Voice recognition availability/language depends on Android speech services and network settings.
- Android does not grant unrestricted control of all phone functions; sensitive actions require permissions and user confirmation.
