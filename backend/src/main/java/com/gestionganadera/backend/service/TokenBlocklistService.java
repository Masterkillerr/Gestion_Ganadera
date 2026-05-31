package com.gestionganadera.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TokenBlocklistService {

 private final ConcurrentMap<String, Long> blocklistExpiry;
 private final long blocklistTtlMillis;

 public TokenBlocklistService(@Value("${app.jwt.blocklist-ttl:604800000}") long blocklistTtlMillis) {
  this.blocklistExpiry = new ConcurrentHashMap<>();
  this.blocklistTtlMillis = blocklistTtlMillis;
 }

 public void block(String token) {
  blocklistExpiry.put(token, Instant.now().toEpochMilli() + blocklistTtlMillis);
 }

 public void blacklistToken(String token) {
  block(token);
 }

 public boolean isBlacklisted(String token) {
  Long expiry = blocklistExpiry.get(token);
  if (expiry == null) {
   return false;
  }
  if (Instant.now().toEpochMilli() > expiry) {
   blocklistExpiry.remove(token, expiry);
   return false;
  }
  return true;
 }

 public void cleanup() {
  long now = Instant.now().toEpochMilli();
  blocklistExpiry.values().removeIf(expiry -> now > expiry);
 }
}
