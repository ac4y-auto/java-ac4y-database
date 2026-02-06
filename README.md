# java-ac4y-database

Database adapter layer for Ac4y projects.

## Overview

This library provides database adapter and connection management for Ac4y applications.

**Components:**
- **Ac4yDBAdapter** - Database adapter for executing queries
- **DBConnection** - Database connection wrapper
- **IDBConnection** - Database connection interface

## Dependencies

- **ac4y-base** (1.0.0) - Base utilities

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-database</artifactId>
    <version>1.0.0</version>
</dependency>
```

### GitHub Packages Configuration

Add the GitHub Packages repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/ac4y-auto/*</url>
    </repository>
</repositories>
```

Configure authentication in `~/.m2/settings.xml`:

```xml
<servers>
    <server>
        <id>github</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_GITHUB_TOKEN</password>
    </server>
</servers>
```

Note: Your GitHub token must have `read:packages` scope.

## Building

```bash
mvn clean install
```

## Testing

```bash
mvn test
```
