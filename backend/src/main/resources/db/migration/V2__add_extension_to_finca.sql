-- ============================================================
-- V2: Add extension column to finca table
-- ============================================================
-- This migration adds the extension (hectares) column to finca.
-- It is idempotent — safe to run even if the column already exists.
-- ============================================================

ALTER TABLE public.finca
    ADD COLUMN IF NOT EXISTS extension NUMERIC(10,2);
