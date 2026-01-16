# User Guide - BrainRowth App

## Pendahuluan

BrainRowth adalah aplikasi Android untuk menyelesaikan soal matematika dengan dua metode input:
1. **Manual Input** - ketik manual dengan bantuan keyboard matematika
2. **Camera OCR** - foto soal matematika dan otomatis detect text

---

## Fitur 1: Manual Input dengan Math Keyboard

### Langkah-langkah:

1. **Buka aplikasi** BrainRowth
2. Secara default akan terbuka di tab **"Manual Input"**
3. Anda akan melihat:
   - Text field kosong untuk input soal
   - Button "Show Math Keyboard"
   - Button "Solve"

### Menggunakan Math Keyboard:

1. Tap button **"Show Math Keyboard"**
2. Keyboard matematika akan muncul di bawah
3. Keyboard memiliki tombol:
   - **Angka**: 0, 1, 2, 3, 4, 5, 6, 7, 8, 9
   - **Operator**: +, -, ×, ÷, ^
   - **Simbol**: (, ), .
   - **Fungsi**: √ (square root), π (pi)
   - **Aksi**: AC (clear all), ⬅ (delete)

4. Tap tombol untuk menginput soal matematika
5. Contoh input: `2 + 3 * 4`

### Menghapus Input:

- **Delete satu karakter**: Tap tombol **⬅** (arrow back)
- **Hapus semua**: Tap tombol **AC** (All Clear) warna merah

### Solve Soal:

1. Setelah input selesai, tap **"Solve"**
2. Aplikasi akan mengirim ke server
3. Loading indicator muncul
4. Hasil akan ditampilkan:
   - **Card hijau**: Final Answer (Jawaban akhir)
   - **Card putih**: Solution Steps (Langkah-langkah)

### Contoh Soal yang Bisa Diselesaikan:

- Aritmatika: `2 + 2`, `10 - 5`, `3 * 4`, `20 / 4`
- Dengan kurung: `(2 + 3) * 4`
- Eksponen: `2 ^ 3` (2 pangkat 3)
- Square root: `sqrt(16)`
- Dengan pi: `2 * pi`
- Kombinasi: `sqrt(25) + (3 * 2) ^ 2`

---

## Fitur 2: Camera OCR

### Langkah-langkah:

1. **Tap tab "Camera"** di bagian atas
2. **Grant Permission**: Jika pertama kali, aplikasi akan minta permission camera
   - Tap "Allow" / "Izinkan"
3. **Camera Preview** akan muncul
4. **Arahkan kamera** ke soal matematika
   - Pastikan pencahayaan cukup
   - Text jelas dan tidak blur
   - Hindari bayangan

5. **Tap button kamera** 📷 di bawah tengah
6. Aplikasi akan:
   - Capture gambar
   - Proses dengan ML Kit OCR
   - Detect text dari gambar
   - Otomatis solve
   - Kembali ke Manual Input tab
   - Tampilkan hasil

### Tips untuk OCR yang Akurat:

✅ **DO's:**
- Gunakan background kontras (misal: tulisan hitam di kertas putih)
- Pencahayaan yang baik
- Kamera stabil (tidak goyang)
- Text yang jelas dan besar
- Fokus kamera sudah lock

❌ **DON'Ts:**
- Jangan gunakan di tempat gelap
- Hindari tulisan terlalu kecil
- Jangan foto tulisan yang blur
- Hindari refleksi atau bayangan

### Troubleshooting OCR:

**Jika text tidak terdeteksi dengan benar:**
1. Foto ulang dengan pencahayaan lebih baik
2. Pastikan text cukup besar
3. Edit manual di tab "Manual Input"

**Jika ada error:**
- Tap "Try Again" untuk foto ulang
- Atau tap "Back to Manual Input" untuk input manual

---

## Hasil dan Interpretasi

### Final Answer Card (Card Hijau):
```
Final Answer:
12
```
Ini adalah jawaban akhir dari soal.

### Solution Steps Card:
```
Solution Steps:
1. Multiply 3 and 4 to get 12
2. Add 2 and 12 to get 14
```
Ini adalah langkah-langkah detail penyelesaian.

---

## Error Handling

### "Soal tidak boleh kosong"
- Anda belum input soal
- Solusi: Input soal terlebih dahulu

### "Gagal menghubungi server"
- Koneksi internet bermasalah
- Backend server tidak running
- Solusi: Check koneksi, pastikan backend running

### "OCR Error: ..."
- Error saat proses text recognition
- Solusi: Foto ulang atau input manual

### "Camera Error: ..."
- Error saat akses camera
- Solusi: Restart app, check permission

---

## Keyboard Shortcuts

Saat Math Keyboard aktif:
- Angka langsung input
- Operator otomatis format
- `sqrt(` otomatis tambah kurung buka
- `AC` hapus semua
- `⬅` hapus satu

---

## Best Practices

1. **Untuk soal sederhana**: Gunakan manual input dengan math keyboard
2. **Untuk soal panjang dari buku/kertas**: Gunakan camera OCR
3. **Untuk akurasi**: Selalu review hasil OCR sebelum solve
4. **Save hasil**: Screenshot hasil jika perlu untuk referensi

---

## FAQ

**Q: Apakah bisa offline?**  
A: Tidak, aplikasi membutuhkan koneksi internet untuk solve di backend.

**Q: Jenis soal apa yang didukung?**  
A: Tergantung backend, umumnya: aritmatika, aljabar, kalkulus, dll.

**Q: Bisa simpan history?**  
A: Fitur ini belum tersedia di versi saat ini.

**Q: Math keyboard bisa disembunyikan?**  
A: Ya, tap "Hide Math Keyboard" untuk menyembunyikan.

**Q: Camera OCR support bahasa apa?**  
A: ML Kit mendukung berbagai bahasa termasuk Indonesia dan Inggris.

---

## Tips & Tricks

💡 **Tip 1**: Untuk square root dari ekspresi, jangan lupa tutup kurung  
Contoh: `sqrt(25)` bukan `sqrt(25`

💡 **Tip 2**: Gunakan kurung untuk prioritas operasi  
Contoh: `(2 + 3) * 4` hasilnya 20, bukan `2 + 3 * 4` yang hasilnya 14

💡 **Tip 3**: Untuk foto soal, crop area soal saja untuk hasil OCR lebih akurat

💡 **Tip 4**: Jika OCR salah detect, edit di text field sebelum solve

---

**Selamat menggunakan BrainRowth! 📚🧮**
