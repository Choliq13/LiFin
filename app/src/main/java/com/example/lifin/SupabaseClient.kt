package com.example.lifin

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://zddbdwavmasndcymjone.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpkZGJkd2F2bWFzbmRjeW1qb25lIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM0MzQxNDUsImV4cCI6MjA3OTAxMDE0NX0.slevo3FP_H7pQ0ybv1rdOcPXktGyYKXx9xci0ex1m2s"
    ) {
        install(Postgrest)
        install(Auth) // ✅ Auth module enabled
        install(Storage)
    }
}