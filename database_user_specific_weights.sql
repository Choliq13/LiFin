-- ============================================
-- OPTIONAL: Add User-Specific Weight Logs
-- Run this if you want each user to have their own data
-- ============================================

-- Step 1: Add user_id column
ALTER TABLE weight_logs
ADD COLUMN user_id UUID REFERENCES auth.users(id);

-- Step 2: Update RLS policy to filter by user
DROP POLICY IF EXISTS "Allow public read access" ON weight_logs;

-- New policy: Users can only see their own data
CREATE POLICY "Users can view own weight logs"
ON weight_logs
FOR SELECT
USING (auth.uid() = user_id);

-- Policy: Users can insert their own data
CREATE POLICY "Users can insert own weight logs"
ON weight_logs
FOR INSERT
WITH CHECK (auth.uid() = user_id);

-- Policy: Users can update their own data
CREATE POLICY "Users can update own weight logs"
ON weight_logs
FOR UPDATE
USING (auth.uid() = user_id)
WITH CHECK (auth.uid() = user_id);

-- Policy: Users can delete their own data
CREATE POLICY "Users can delete own weight logs"
ON weight_logs
FOR DELETE
USING (auth.uid() = user_id);

-- Step 3: Set user_id otomatis saat insert (optional trigger)
CREATE OR REPLACE FUNCTION set_user_id()
RETURNS TRIGGER AS $$
BEGIN
  NEW.user_id = auth.uid();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE TRIGGER set_user_id_trigger
BEFORE INSERT ON weight_logs
FOR EACH ROW
EXECUTE FUNCTION set_user_id();

-- ============================================
-- VERIFICATION
-- ============================================

-- Check schema
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'weight_logs';

-- Check policies
SELECT policyname, permissive, roles, cmd
FROM pg_policies
WHERE tablename = 'weight_logs';

-- ============================================
-- NOTES:
-- ============================================
--
-- Setelah run SQL ini:
-- 1. Data lama (4 rows) akan punya user_id = NULL
-- 2. User baru hanya lihat data mereka sendiri
-- 3. Insert baru otomatis dapat user_id
--
-- Jika Anda mau migrate data lama:
-- UPDATE weight_logs SET user_id = '<USER_UUID>' WHERE user_id IS NULL;
--
-- ============================================

