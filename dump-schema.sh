#!/bin/bash
pg_dump \
  --host=ganaderia.cbm6w28ialq0.us-east-2.rds.amazonaws.com \
  --username=postgres \
  --dbname=ganaderia \
  --schema-only \
  --no-owner \
  --no-acl \
  2>&1 | head -400
