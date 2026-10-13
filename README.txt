# Radhe Radhe AI — starter app

This is a beginner-friendly, installable web app (PWA) for Android. It is a starter, not a full hands-free system.

## Included
- Futuristic dark blue interface
- Tap-to-speak Hindi speech recognition (browser/device support required)
- Hindi text-to-speech response
- Basic commands: YouTube, Google search, WhatsApp Web, date/time
- Optional compatible chat-completions API setup
- Installable PWA files

## Important limitations
- It does NOT continuously listen for “Radhe Radhe” in the background. Android/browser restrictions prevent this simple web app from being an always-on wake-word assistant.
- AI responses need a compatible API key. API access may cost money; do not assume the provider's chat website subscription includes API credits.
- The API key is saved in local browser storage on this device. Do not use this on a shared phone; a production app should use a secure backend instead.
- Speech recognition usually needs internet and may not be available in every browser.
- It cannot perform every phone action or bypass Android permissions.

## Easiest way to use on Android
1. Extract this ZIP on your computer.
2. Upload the files to a new GitHub repository.
3. In repository Settings → Pages, enable deployment from the main branch/root.
4. Open the resulting HTTPS GitHub Pages URL in Chrome on your Android phone.
5. Chrome menu → Add to Home screen / Install app.
6. Allow microphone permission when prompted.

If you want to test on your computer first, use any static HTTPS host. Opening index.html directly from file:// will not enable every feature.

## Optional AI API setup
In the app, open “AI setup”. Enter the HTTPS chat-completions endpoint, your provider-issued API key, and the model name supported by that provider. Save settings. Check provider pricing and usage limits before using.
