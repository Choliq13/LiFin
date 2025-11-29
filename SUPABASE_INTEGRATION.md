# Integrasi Supabase - Dokumentasi

## Ringkasan
Proyek ini menggunakan pola **Repository Pattern** untuk mengintegrasikan Supabase dengan aman dan menghindari internal compiler error pada Kotlin 2.0.21.

## Struktur Arsitektur

```
app/
├── SupabaseClient.kt                    # Singleton client Supabase
├── data/
│   └── WeightLogRepository.kt           # Repository layer (isolasi Supabase)
├── MainActivity.kt                       # UI Activity (tanpa import Supabase)
└── WeightLog.kt (data class di MainActivity) # Model data
```

## Prinsip Utama

### ✅ Yang BOLEH
- Import `io.github.jan.supabase.*` **HANYA** di `WeightLogRepository.kt`
- Import `io.github.jan.supabase.*` di `SupabaseClient.kt`
- Panggil repository dari `LaunchedEffect` atau ViewModel
- Gunakan `Result<T>` untuk menangani success/failure

### ❌ Yang TIDAK BOLEH
- Import `io.github.jan.supabase.*` di file Composable atau Activity
- Panggil `SupabaseClient.client.from()` langsung dari UI layer
- Menggunakan extension function Supabase di file yang berisi `@Composable`

## Pola Penggunaan

### 1. Repository Pattern (Sudah diimplementasikan)

**File: `data/WeightLogRepository.kt`**
```kotlin
class WeightLogRepository {
    suspend fun getWeightLogs(): Result<List<WeightLog>> {
        return try {
            val data = withTimeout(10_000) {
                val table = SupabaseClient.client.from("weight_logs")
                val response = table.select()
                response.decodeList<WeightLog>()
            }
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 2. Pemanggilan dari UI (Sudah diimplementasikan)

**File: `MainActivity.kt`**
```kotlin
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var logs by remember { mutableStateOf<List<WeightLog>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        val repository = WeightLogRepository()
        val result = repository.getWeightLogs()
        
        result.onSuccess { data ->
            logs = data
        }.onFailure { error ->
            errorMessage = "Gagal mengambil data: ${error.message}"
        }
    }
    
    // ... UI rendering
}
```

## Mengapa Pola Ini Menghindari Compiler Error?

### Masalah Sebelumnya
- Kotlin FIR (Frontend IR) compiler crash saat menganalisis extension function Supabase (`from`, `select`, `decodeList`) yang digunakan langsung di file Composable
- Error: `FileAnalysisException: source must not be null` menunjukkan compiler gagal melacak source element saat type checking

### Solusi
1. **Isolasi kompleksitas**: Semua operasi Supabase di-isolasi ke repository terpisah
2. **Simplified type inference**: UI layer hanya berurusan dengan `Result<List<WeightLog>>`, bukan generic types kompleks dari Supabase
3. **Clear boundaries**: Compiler menganalisis file repository dan UI secara terpisah, mengurangi beban type checking

## Testing

### Manual Test
```bash
# Di PowerShell (dari root project)
cd C:\Users\ASUS\AndroidStudioProjects\LiFin
.\gradlew.bat clean assembleDebug
```

### Expected Results
- ✅ Kompilasi berhasil tanpa `FileAnalysisException`
- ✅ App dapat mengambil data dari Supabase saat runtime
- ✅ Error handling berfungsi (timeout, network error, dll)

## Troubleshooting

### Jika masih terjadi compiler error:

1. **Pastikan import bersih**
   ```bash
   # Cari import Supabase yang tidak seharusnya
   grep -r "io.github.jan.supabase" app/src/main/java/com/example/lifin/*.kt
   ```
   
   Hasil yang valid:
   - `SupabaseClient.kt` ✅
   - `data/WeightLogRepository.kt` ✅
   - File lain ❌

2. **Clean & rebuild**
   ```bash
   .\gradlew.bat clean
   .\gradlew.bat :app:compileDebugKotlin --stacktrace
   ```

3. **Fallback: Downgrade Kotlin**
   Jika tetap gagal, edit `gradle/libs.versions.toml`:
   ```toml
   [versions]
   kotlin = "1.9.24"  # dari 2.0.21
   ```

4. **Fallback: Isolasi ke module**
   Buat module terpisah `:data` dengan Supabase dependency sendiri

## Dependencies Terkait

```kotlin
// app/build.gradle.kts
implementation(platform("io.github.jan-tennert.supabase:bom:3.2.6"))
implementation("io.github.jan-tennert.supabase:postgrest-kt")
implementation("io.github.jan-tennert.supabase:auth-kt")
implementation("io.github.jan-tennert.supabase:storage-kt")
implementation("io.ktor:ktor-client-cio:3.0.0")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
```

## Versi yang Digunakan
- Kotlin: 2.2.0 ⚠️ **PENTING: Harus 2.2.0+ untuk kompatibilitas dengan Supabase 3.2.6**
- AGP: 8.13.0
- Supabase BOM: 3.2.6
- Ktor Client: 3.3.1 (auto-resolved dari Supabase BOM)

### ⚠️ Catatan Version Compatibility
Supabase BOM 3.2.6 dan Ktor 3.3.1 dikompilasi dengan Kotlin 2.2.0. Jika Anda menggunakan Kotlin versi lebih lama (mis. 2.0.21), Anda akan mendapat error:
```
Module was compiled with an incompatible version of Kotlin. 
The binary version of its metadata is 2.2.0, expected version is 2.0.0.
```

**Solusi**: Upgrade Kotlin ke 2.2.0 di `gradle/libs.versions.toml`:
```toml
[versions]
kotlin = "2.2.0"
```

## Referensi
- [Supabase Kotlin Documentation](https://supabase.com/docs/reference/kotlin/introduction)
- [Kotlin Repository Pattern](https://developer.android.com/topic/architecture/data-layer)
- [Kotlin Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/)

---
**Last Updated**: November 24, 2025  
**Status**: ✅ Implementasi berhasil, compiler error teratasi

