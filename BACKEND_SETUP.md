# Backend Setup Guide

## Base URL Configuration

Aplikasi ini membutuhkan backend API yang running untuk bisa solve math problems.

### Untuk Emulator Android Studio

Default BASE_URL di `ApiClient.kt` sudah dikonfigurasi:

```kotlin
private const val BASE_URL = "http://10.0.2.2:3000/"
```

**Note**: `10.0.2.2` adalah IP address khusus emulator yang mengarah ke `localhost` komputer host.

### Untuk Real Device

Jika menggunakan device fisik yang terkoneksi ke WiFi yang sama:

1. Cari IP address komputer Anda:
   - Windows: `ipconfig` di Command Prompt
   - Mac/Linux: `ifconfig` di Terminal
   
2. Update BASE_URL di `ApiClient.kt`:
   ```kotlin
   private const val BASE_URL = "http://YOUR_COMPUTER_IP:3000/"
   // Contoh: "http://192.168.1.100:3000/"
   ```

3. Pastikan firewall mengizinkan koneksi di port 3000

### API Endpoint yang Dibutuhkan

Backend harus menyediakan endpoint:

**POST** `/api/solve-text`

**Request Body:**
```json
{
  "question": "string"
}
```

**Response:**
```json
{
  "question": "string",
  "steps": ["string"],
  "final_answer": "string",
  "raw_answer": "string",
  "parse_error": "string | null"
}
```

### Testing API

Gunakan tools seperti Postman atau curl untuk test endpoint:

```bash
curl -X POST http://localhost:3000/api/solve-text \
  -H "Content-Type: application/json" \
  -d '{"question":"2 + 2"}'
```

Response yang diharapkan:
```json
{
  "question": "2 + 2",
  "steps": ["Add 2 and 2"],
  "final_answer": "4",
  "raw_answer": "4",
  "parse_error": null
}
```

## Troubleshooting

### Connection Refused
- Pastikan backend server running
- Check firewall settings
- Verify IP address dan port

### Network Security (Android 9+)
Jika ada error "Cleartext HTTP traffic not allowed", sudah ditambahkan:
```xml
android:usesCleartextTraffic="true"
```
di AndroidManifest.xml

### CORS Issues
Backend harus mengizinkan CORS jika diperlukan (biasanya tidak perlu untuk mobile app)

## Development vs Production

Untuk production, ganti dengan HTTPS URL:
```kotlin
private const val BASE_URL = "https://api.brainrowth.com/"
```

Dan remove `android:usesCleartextTraffic="true"` dari manifest.
