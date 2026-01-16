# BrainRowth - Math Problem Solver Android App

## Fitur Utama

### 1. **Math Keyboard Custom** ✨
Keyboard matematika khusus dengan tombol-tombol:
- Angka: 0-9
- Operator: +, -, ×, ÷, ^
- Fungsi matematika: √ (square root), π (pi)
- Tanda kurung: ( )
- Tombol Delete dan Clear (AC)

**Lokasi file**: `app/src/main/java/com/example/brainrowth/ui/components/MathKeyboard.kt`

### 2. **Input Manual dengan Math Keyboard**
- Tab "Manual Input" untuk mengetik soal matematika
- Toggle button untuk show/hide math keyboard
- TextField yang bisa diisi manual atau menggunakan math keyboard
- Button "Solve" untuk mengirim soal ke server

### 3. **Camera OCR Integration** 📷
- Tab "Camera" untuk mengambil foto soal matematika
- Menggunakan CameraX untuk camera preview
- ML Kit Text Recognition untuk OCR
- Otomatis solve setelah text terdeteksi

**Lokasi file**: `app/src/main/java/com/example/brainrowth/ui/components/CameraCapture.kt`

### 4. **UI/UX Modern**
- Material Design 3 dengan Jetpack Compose
- Tab navigation (Manual Input & Camera)
- Top App Bar dengan branding
- Cards untuk menampilkan hasil
- Loading indicators
- Error handling yang user-friendly

### 5. **Solve dengan Backend API**
- Integrasi dengan backend API untuk solving
- Menampilkan langkah-langkah penyelesaian
- Menampilkan jawaban akhir
- Error handling untuk network errors

## Struktur Project

```
app/src/main/java/com/example/brainrowth/
├── MainActivity.kt                          # Entry point
├── OCRIntegration.kt                        # ML Kit OCR wrapper
├── data/
│   └── remote/
│       ├── ApiClient.kt                     # Retrofit client
│       ├── BrainRowthApi.kt                 # API interface
│       ├── SolveRequest.kt                  # Request model
│       └── SolveResponse.kt                 # Response model
├── ui/
│   ├── components/
│   │   ├── MathKeyboard.kt                  # Custom math keyboard
│   │   └── CameraCapture.kt                 # Camera component
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── view/
│   └── HomeScreen.kt                        # Main screen with tabs
└── viewmodel/
    └── SolverViewModel.kt                   # ViewModel for state management
```

## Dependencies

### Core
- Jetpack Compose (Material 3)
- Kotlin Coroutines
- ViewModel & Lifecycle

### Networking
- Retrofit 2.11.0
- Gson Converter

### Camera & OCR
- CameraX 1.3.1 (Core, Camera2, Lifecycle, View)
- ML Kit Text Recognition 16.0.1

### Permissions
- Accompanist Permissions 0.32.0

## Permissions

Di `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-feature android:name="android.hardware.camera" android:required="false" />
```

## Cara Menggunakan

### Manual Input:
1. Buka tab "Manual Input"
2. Klik "Show Math Keyboard" untuk menampilkan keyboard matematika
3. Ketik atau gunakan math keyboard untuk input soal
4. Klik "Solve" untuk mendapatkan solusi
5. Lihat jawaban dan langkah-langkah penyelesaian

### Camera OCR:
1. Buka tab "Camera"
2. Berikan permission camera jika diminta
3. Arahkan kamera ke soal matematika
4. Klik tombol capture (ikon kamera)
5. Tunggu OCR processing
6. Aplikasi otomatis solve dan tampilkan hasil

## API Endpoint

Base URL: `http://10.0.2.2:3000/` (untuk emulator)

Endpoint: `POST /api/solve-text`

Request body:
```json
{
  "question": "2 + 2"
}
```

Response:
```json
{
  "question": "2 + 2",
  "steps": ["Step 1", "Step 2"],
  "final_answer": "4",
  "raw_answer": "4",
  "parse_error": null
}
```

## Build & Run

1. Pastikan backend API sudah running
2. Update `BASE_URL` di `ApiClient.kt` jika perlu
3. Build project: `./gradlew build`
4. Run di emulator atau device: `./gradlew installDebug`

## Requirements

- Android SDK 26+ (Android 8.0)
- Target SDK 36
- Kotlin
- Gradle 8.13

## Fitur Sesuai SRS

✅ Manual input dengan keyboard matematika  
✅ Camera capture untuk OCR  
✅ ML Kit text recognition  
✅ API integration untuk solving  
✅ Display langkah-langkah penyelesaian  
✅ Display jawaban akhir  
✅ Error handling  
✅ Modern UI/UX dengan Material Design 3  

## Tips Development

- Gunakan emulator dengan API 26+ untuk testing
- Pastikan camera permission granted untuk fitur camera
- Backend API harus accessible dari device/emulator
- Untuk real device, gunakan IP address komputer di local network

---

**Developed with ❤️ using Jetpack Compose & Kotlin**
