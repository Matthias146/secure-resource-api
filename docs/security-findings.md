# Security Findings

## CVE-2026-19880 - Logback

### Status

Accepted temporarily.

### Current dependency

- logback-classic: 1.5.38
- logback-core: 1.5.38

### Issue

The current Spring Boot dependency set includes a Logback version affected by CVE-2026-19880.

### Decision

Do not manually override the Logback version at this time.

Spring Boot 4.1.1 manages the current dependency version, and no stable Spring Boot release currently provides the patched Logback line.

### Planned Action

Upgrade Spring Boot when a stable release includes a fixed Logback version.

### Notes

The finding remains visible and will be reviewed during dependency updates.