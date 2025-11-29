-- ========================================
-- SQL Script: Verifikasi & Debug Database
-- Project: LiFin (Weight Tracker)
-- Date: November 24, 2025
-- ========================================

-- ========================================
-- 1. CEK STRUKTUR TABEL
-- ========================================

-- Lihat semua tabel yang ada
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- Expected output: weight_logs

-- ========================================
-- 2. CEK STRUKTUR KOLOM weight_logs
-- ========================================

SELECT
  column_name,
  data_type,
  udt_name,
  is_nullable,
  column_default
FROM information_schema.columns
WHERE table_name = 'weight_logs'
ORDER BY ordinal_position;

-- Expected columns:
-- id          | bigint              | int8   | NO  | nextval('weight_logs_id_seq'::regclass)
-- weight      | double precision    | float8 | NO  | NULL
-- created_at  | timestamp with time zone | timestamptz | YES | now()

-- ========================================
-- 3. CEK JUMLAH DATA
-- ========================================

SELECT COUNT(*) as total_rows
FROM weight_logs;

-- Jika total_rows = 0, berarti tabel kosong (data belum diinsert)
-- Jika total_rows > 0, berarti ada data

-- ========================================
-- 4. CEK ISI DATA (TOP 10)
-- ========================================

SELECT
  id,
  weight,
  created_at,
  created_at::text as created_at_text  -- Format timestamp as text
FROM weight_logs
ORDER BY created_at DESC
LIMIT 10;

-- Expected format:
-- id | weight | created_at                      | created_at_text
-- 1  | 70.5   | 2025-11-24 10:00:00+00         | 2025-11-24 10:00:00+00

-- ========================================
-- 5. CEK ROW LEVEL SECURITY (RLS)
-- ========================================

-- Cek apakah RLS aktif
SELECT
  schemaname,
  tablename,
  rowsecurity as rls_enabled
FROM pg_tables
WHERE tablename = 'weight_logs';

-- Jika rls_enabled = true, cek policies
SELECT
  schemaname,
  tablename,
  policyname,
  permissive,
  roles,
  cmd,
  qual,
  with_check
FROM pg_policies
WHERE tablename = 'weight_logs';

-- Jika tidak ada policy atau policy tidak allow anon:
-- Data tidak akan muncul di aplikasi!

-- ========================================
-- 6. TEST QUERY SEBAGAI ANON USER
-- ========================================

-- Simulasi query yang dilakukan aplikasi
SET ROLE anon;
SELECT * FROM weight_logs;
RESET ROLE;

-- Jika error atau return 0 rows (padahal data ada):
-- RLS blocking access untuk anon role

-- ========================================
-- 7. FIX: DISABLE RLS (Development Only)
-- ========================================

-- HATI-HATI: Ini membuka akses public ke tabel
-- Hanya untuk development/testing
ALTER TABLE weight_logs DISABLE ROW LEVEL SECURITY;

-- ========================================
-- 8. FIX: ENABLE RLS + CREATE POLICY (Production)
-- ========================================

-- Enable RLS
ALTER TABLE weight_logs ENABLE ROW LEVEL SECURITY;

-- Drop existing policies (jika ada)
DROP POLICY IF EXISTS "Allow public read access" ON weight_logs;
DROP POLICY IF EXISTS "Allow public insert" ON weight_logs;
DROP POLICY IF EXISTS "Allow public update" ON weight_logs;
DROP POLICY IF EXISTS "Allow public delete" ON weight_logs;

-- Create policy: Allow anonymous users to SELECT
CREATE POLICY "Allow public read access"
ON weight_logs
FOR SELECT
TO anon
USING (true);

-- Create policy: Allow anonymous users to INSERT
CREATE POLICY "Allow public insert"
ON weight_logs
FOR INSERT
TO anon
WITH CHECK (true);

-- Create policy: Allow anonymous users to UPDATE (optional)
CREATE POLICY "Allow public update"
ON weight_logs
FOR UPDATE
TO anon
USING (true)
WITH CHECK (true);

-- Create policy: Allow anonymous users to DELETE (optional)
CREATE POLICY "Allow public delete"
ON weight_logs
FOR DELETE
TO anon
USING (true);

-- ========================================
-- 9. INSERT SAMPLE DATA (Jika Tabel Kosong)
-- ========================================

-- Hapus data lama (optional)
TRUNCATE weight_logs RESTART IDENTITY CASCADE;

-- Insert 10 sample records
INSERT INTO weight_logs (weight, created_at) VALUES
  (70.5, NOW()),
  (70.3, NOW() - INTERVAL '1 day'),
  (70.8, NOW() - INTERVAL '2 days'),
  (69.9, NOW() - INTERVAL '3 days'),
  (70.1, NOW() - INTERVAL '4 days'),
  (70.6, NOW() - INTERVAL '5 days'),
  (70.2, NOW() - INTERVAL '6 days'),
  (69.8, NOW() - INTERVAL '1 week'),
  (71.0, NOW() - INTERVAL '2 weeks'),
  (70.4, NOW() - INTERVAL '3 weeks');

-- Verify insert
SELECT COUNT(*) as total_inserted FROM weight_logs;
SELECT * FROM weight_logs ORDER BY created_at DESC;

-- ========================================
-- 10. CREATE TABLE (Jika Belum Ada)
-- ========================================

-- Buat tabel weight_logs jika belum ada
CREATE TABLE IF NOT EXISTS weight_logs (
  id BIGSERIAL PRIMARY KEY,
  weight DOUBLE PRECISION NOT NULL CHECK (weight > 0 AND weight < 500),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_weight_logs_created_at
ON weight_logs(created_at DESC);

-- Enable RLS + create policy
ALTER TABLE weight_logs ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow public read access"
ON weight_logs FOR SELECT TO anon USING (true);

CREATE POLICY "Allow public insert"
ON weight_logs FOR INSERT TO anon WITH CHECK (true);

-- Insert sample data
INSERT INTO weight_logs (weight, created_at) VALUES
  (70.5, NOW()),
  (69.8, NOW() - INTERVAL '1 day'),
  (71.2, NOW() - INTERVAL '2 days'),
  (70.0, NOW() - INTERVAL '3 days');

-- ========================================
-- 11. VERIFY FINAL STATE
-- ========================================

-- Cek semua setting
SELECT
  'Table exists' as check_name,
  CASE WHEN EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_name = 'weight_logs'
  ) THEN '✅ YES' ELSE '❌ NO' END as result
UNION ALL
SELECT
  'RLS enabled',
  CASE WHEN rowsecurity THEN '✅ YES' ELSE '❌ NO' END
FROM pg_tables WHERE tablename = 'weight_logs'
UNION ALL
SELECT
  'Has data',
  CASE WHEN COUNT(*) > 0 THEN '✅ YES (' || COUNT(*) || ' rows)' ELSE '❌ NO (0 rows)' END
FROM weight_logs
UNION ALL
SELECT
  'Anon can read',
  CASE WHEN EXISTS (
    SELECT 1 FROM pg_policies
    WHERE tablename = 'weight_logs'
    AND cmd = 'SELECT'
    AND 'anon' = ANY(roles)
  ) THEN '✅ YES' ELSE '❌ NO' END;

-- ========================================
-- 12. TEST API RESPONSE FORMAT
-- ========================================

-- Simulate API response (JSON format)
SELECT json_agg(
  json_build_object(
    'id', id,
    'weight', weight,
    'created_at', created_at
  )
) as api_response
FROM weight_logs
ORDER BY created_at DESC
LIMIT 5;

-- Expected output:
-- [
--   {"id":1,"weight":70.5,"created_at":"2025-11-24T10:00:00+00:00"},
--   {"id":2,"weight":69.8,"created_at":"2025-11-23T10:00:00+00:00"},
--   ...
-- ]

-- ========================================
-- 13. CLEANUP (Optional)
-- ========================================

-- Drop table jika mau start fresh
-- DROP TABLE IF EXISTS weight_logs CASCADE;

-- ========================================
-- NOTES:
-- ========================================
--
-- Penyebab umum "data tidak muncul":
-- 1. ❌ Tabel tidak ada → run section 10
-- 2. ❌ Tabel kosong → run section 9
-- 3. ❌ RLS blocking → run section 7 atau 8
-- 4. ❌ Nama tabel salah → cek section 1
-- 5. ❌ Struktur kolom berbeda → cek section 2
-- 6. ❌ Supabase key invalid → cek di Dashboard → Settings → API
--
-- Setelah run script ini:
-- 1. Rebuild aplikasi: .\gradlew.bat clean assembleDebug
-- 2. Install: .\gradlew.bat installDebug
-- 3. Check logcat: adb logcat -s WeightLogRepository HomeScreen
--
-- Expected log jika berhasil:
-- D/WeightLogRepository: Successfully fetched 4 weight logs
-- D/HomeScreen: ✅ Berhasil memuat 4 data
--
-- ========================================

