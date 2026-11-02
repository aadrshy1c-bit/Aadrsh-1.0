# Sky & Max – Personal JARVIS AI Assistants

An advanced, Iron Man JARVIS-inspired dual AI assistant application featuring two distinct personalities:
- **Sky**: Polite, witty, deeply caring female assistant (voice pitch 1.25x), addressing you as **"Sir"**.
- **Max**: Smart, playful, high-energy male assistant (voice pitch 0.85x), addressing you as **"Sir"**.

Sky and Max communicate in **Hindi (default)**, English, Punjabi, Tamil, Bengali, Urdu, Spanish, French, and any language you speak or write, switching dynamically mid-conversation.

---

## 🌟 Key Capabilities

1. **Wake-Word & Persona Addressing**
   - Address **"Sky"** ➔ Only Sky responds; Max remains silent.
   - Address **"Max"** ➔ Only Max responds; Sky remains silent.
   - Say **"Both of you tell me"** or **"Which of you thinks is best"** ➔ Both debate and converse in sequence.
   - Both assistants hear ongoing conversations, but maintain strictly isolated private memories.

2. **Long-Term Memory Vault & SD Card Backup**
   - Sky and Max retain independent memories (item locations like keys/phone, preferences, habits, past queries).
   - Voice trigger: *"Sky, save all your data to this SD card"* / *"Max, save all your data to this SD card"*.
   - Encrypted JSON export and automatic background detection and merge on new devices.

3. **Caring Alarms & Humorous Reminders**
   - Reminders spoken aloud with Iron Man JARVIS humor:
     > *"Sir, it is quite late, you should sleep now. If you stay up this late, you will be complaining to me tomorrow morning."*

4. **In-App GitHub Releases Auto-Updater**
   - Detects when a new release tag is pushed to the repository via the GitHub Releases API.
   - Prominently displays the **"New version is available, update"** notification.
   - Directly downloads the signed APK with real-time transfer progress and launches the Android Package Installer via `FileProvider`.
   - Built-in simulation toggle to test the update and download flow at any time.

5. **FM Radio & Live TV Hub**
   - **FM Radio**: Dial tuning (87.5 – 108.0 MHz), presets (98.3 Mirchi, 93.5 Red FM, 92.7 Big FM, etc.), simulated earphone antenna detection, Bluetooth speaker audio.
   - **Live TV**: 3 input modes (OTG TV tuner receiver, Home Free Dish Set-top Box Wi-Fi streaming, or Internet stream).

6. **AI Image & Video Generative Studio**
   - Text-to-Image generation with style filters and 4K upscaling.
   - Text-to-Video and Photo-to-Video generation (10 min, 15 min, 1 hr multi-segment stitched timelines).

7. **System Automation & Skills Engine**
   - Live on-screen telemetry executing Instagram video uploads, YouTube Shorts, dialing phone calls (*"Call Mummy"*), WhatsApp messaging, and system alarms.

---

## 📲 How to Install & Run

### A. Android (Real APK from GitHub Releases)
1. Navigate to the **Releases** tab of this GitHub repository.
2. Download the latest `sky-max-jarvis-release.apk`.
3. Open the downloaded APK on your device:
   - If prompted: **Settings ➔ Allow installation from unknown sources** for your browser/file manager.
4. Tap **Install**.
5. On first launch, the **JARVIS System Clearance** screen will present clear justifications for required permissions (Microphone, Notifications, Calling, Bluetooth, Storage, etc.). Tap **Authorize All & Initialize JARVIS**.

### B. iPhone & iPad (PWA – Progressive Web App)
1. Open the GitHub Pages link in **Safari**.
2. Tap the **Share** button (box with an upward arrow) at the bottom.
3. Scroll down and tap **"Add to Home Screen"**.
4. Tap **Add**. The Sky & Max icon appears on your home screen and launches full screen without browser bars.

### C. PC & Mac (Desktop App via Chrome / Edge)
1. Open the GitHub Pages link in Google Chrome or Microsoft Edge.
2. Click the **Install** icon in the browser address bar (top right).
3. Sky & Max runs as a standalone desktop window with offline service-worker caching.

---

## 🔐 Keystore & Secrets Security (GitHub Secrets)

**Critical Policy**: Keystore passwords, signing keys, and secrets must **never** be committed to source code.

The project utilizes the following repository secrets under **GitHub ➔ Settings ➔ Secrets and variables ➔ Actions**:

| Secret Name | Description |
|---|---|
| `KEYSTORE_BASE64` | Base64-encoded upload keystore (`base64 -w 0 my-upload-key.jks`) |
| `STORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias (e.g. `upload`) |
| `KEY_PASSWORD` | Key password |
| `GITHUB_TOKEN` | Automatic GitHub token for publishing releases |

The GitHub Actions workflow (`.github/workflows/build-and-release.yml`) automatically decodes the keystore at build time, signs the release APK, and attaches it directly to the GitHub Release.

---

## 🔄 Self-Update System (Prompt & Password)

1. Open the **Updater & Settings** tab in the app.
2. In the **Self-Update Console**, paste any code modification prompt from an external AI.
3. Enter your authorized security password (default configured in vault: `jarvis77`).
4. Tap **Authenticate & Hot-Patch Update**. The system tests the syntax, runs regression checks, and hot-swaps the runtime safely.
