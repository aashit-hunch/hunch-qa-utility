# Hunch QA Utility

A Java-based test data generation utility for the Hunch Technology Team. Automates the creation of test users, waves, and crushes for development and QA purposes with direct database operations and API integrations.

## Table of Contents

- [What We Solved](#what-we-solved)
- [Features](#features)
- [Current Limitations](#current-limitations)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Usage Examples](#usage-examples)
- [System Properties](#system-properties)
- [Logging Control](#logging-control)
- [Result Output & Cleanup](#result-output--cleanup)
- [Architecture Highlights](#architecture-highlights)

## What We Solved

This utility enables the Hunch team to:

1. **Generate Test Users**: Create fully functional test users with realistic data (demographics, preferences, MBTI, profile images)
2. **Generate Waves/Crushes**: Simulate user interactions by creating wave/crush requests between users
3. **Parallel Execution**: Generate multiple users or interactions simultaneously using configurable thread pools
4. **Result Visualization**: View detailed success/failed user information in formatted box output with ID, phone, and gender
5. **Automatic Failure Handling**: Failed user records are automatically rolled back from the database, maintaining data integrity
6. **Flexible Configuration**: Control all operations via system properties without code changes
7. **Resource Management**: Proper database connection pooling, thread lifecycle control, and automatic cleanup

### Solution Benefits

- **Saves Time**: Automated test data generation replaces manual user creation
- **Realistic Data**: Uses JavaFaker for genuine-looking profiles and attributes
- **Thread-Safe**: Parallel execution using ThreadLocal ensures data isolation
- **Production-Ready**: Encrypted credentials, connection pooling, and comprehensive logging
- **Configurable Logging**: Dynamic INFO log control without modifying code

## Features

- **User Generation**: Random or phone-specific user creation with complete profile setup
- **Wave/Crush Operations**: Send, receive, accept waves and crushes for testing
- **Multi-threaded Execution**: Parallel processing with configurable thread count
- **Result Visualization**: Formatted box output displaying user details (ID, phone, gender) for success/failed operations
- **Automatic Rollback**: Failed user data is automatically cleaned up from the database
- **Database Operations**: HikariCP connection pooling with PostgreSQL optimization
- **API Integration**: GraphQL API calls with Firebase JWT authentication
- **Secure Configuration**: AES-encrypted database and Firebase credentials
- **Dynamic Logging**: Runtime control of INFO logs via system properties

## Current Limitations

- **User Generation**: Maximum **2 users** can be generated at a time (resource bandwidth constraint)
- **Wave/Crush Generation**: Maximum **5 waves or crushes** per user at a time (resource bandwidth constraint)

These limits are enforced in `GenerateUserOperations.java` to prevent resource exhaustion.

## Prerequisites

- **Java**: JDK 25 or higher
- **Maven**: 3.6+ for dependency management and build
- **PostgreSQL**: Access to Hunch PostgreSQL database
- **Firebase**: Service account credentials for Firebase authentication
- **Network**: Access to Hunch development API endpoints

## Quick Start

### 1. Build the Project

```bash
mvn clean compile
```

### 2. Run User Generation (Default)

```bash
mvn exec:java
```

This executes `GenerateUserOperations.java` with default settings (1 user, male gender, female preference).

## Usage Examples

### 1. Generate Random User

```bash
mvn exec:java -Doperation=gen.user -Duser.count=2
```

Generates 2 random users with complete profile setup (MBTI, images, location).

### 2. Generate Wave Sent

```bash
mvn exec:java -Doperation=gen.wave.sent -Dphone.number=+919876543210 -Duser.count=5
```

Creates 5 wave requests **sent by** the user with phone number `+919876543210`.

### 3. Generate Crush Sent

```bash
mvn exec:java -Doperation=gen.crush.sent -Dphone.number=+919876543210 -Duser.count=3
```

Creates 3 crush requests **sent by** the user with phone number `+919876543210`.

### 4. Generate Wave Received

```bash
mvn exec:java -Doperation=gen.wave.received -Dphone.number=+919876543210 -Duser.count=5
```

Creates 5 wave requests **received by** the user with phone number `+919876543210`.

### 5. Generate Crush Received

```bash
mvn exec:java -Doperation=gen.crush.received -Dphone.number=+919876543210 -Duser.count=4
```

Creates 4 crush requests **received by** the user with phone number `+919876543210`.

### 6. Generate Accepted Crush Sent

```bash
mvn exec:java -Doperation=gen.crush.sent.accepted -Dphone.number=+919876543210 -Duser.count=2
```

Creates 2 crush requests **sent by** the user and automatically **accepted** by receivers (creates SendBird chat channel).

### 7. Generate Accepted Crush Received

```bash
mvn exec:java -Doperation=gen.crush.received.accepted -Dphone.number=+919876543210 -Duser.count=2
```

Creates 2 crush requests **received by** the user and automatically **accepted** (creates SendBird chat channel).

### 8. Custom User Generation

```bash
mvn exec:java \
  -Doperation=gen.user \
  -Duser.count=1 \
  -Duser.gender=female \
  -Duser.preference=male \
  -Duser.onboarding=mbti \
  -Duser.location=USA_NY
```

Generates 1 female user preferring males, with MBTI onboarding journey, located in New York, USA.

## System Properties

### Core Configuration

| Property | Values | Default | Description |
|----------|--------|---------|-------------|
| `operation` | `gen.user`, `gen.wave.sent`, `gen.wave.received`, `gen.crush.sent`, `gen.crush.received`, `gen.crush.sent.accepted`, `gen.crush.received.accepted` | `gen.user` | Operation type to perform |
| `user.count` | `1-2` (users), `1-5` (waves/crushes) | `1` | Number of users/interactions to generate |
| `phone.number` | `+91XXXXXXXXXX` | - | Phone number for wave/crush operations |
| `user.gender` | `male`, `female` | `male` | Gender of generated user |
| `user.preference` | `male`, `female` | `female` | Dating preference of generated user |
| `user.onboarding` | `images`, `mbti` | `images` | User onboarding journey type |
| `user.location` | `USA_NY`, `USA_CA`, etc. | `USA_NY` | User location (LatLong enum) |
| `user.custom` | `true`, `false` | `false` | Use custom user data |

### Logging Configuration

| Property | Values | Default | Description |
|----------|--------|---------|-------------|
| `enable.info.logs` | `true`, `false` | `true` | Enable/disable INFO level logs |

## Logging Control

The utility supports dynamic control of INFO-level logging without code changes.

### Enable INFO Logs 

```bash
mvn exec:java -Doperation=gen.user -Denable.info.logs=true
```

Shows all INFO, WARN, and ERROR logs.

### Disable INFO Logs(Default)

```bash
mvn exec:java -Doperation=gen.user -Denable.info.logs=false
```

Shows only WARN and ERROR logs (suppresses INFO logs).

### How It Works

1. **GlobalData.java**: Reads `enable.info.logs` system property
2. **LoggerConfig.java**: Configures Log4j root logger level at startup
3. **Automatic**: All existing `LOGGER.info()` calls respect the flag

When `enable.info.logs=false`, the root logger level is set to `WARN`, automatically suppressing all INFO logs throughout the application.

## Result Output & Cleanup

### Formatted User Details Output

After user generation completes, the utility displays a **formatted box output** showing details of all generated users:

```
┌──────────────────────────────────────────────────────────────────────────┐
│ Generated Success User IDs                                               │
├──────────────────────────────────────────────────────────────────────────┤
│ User ID                                  | Phone Number    | Gender       │
├──────────────────────────────────────────────────────────────────────────┤
│ 550e8400-e29b-41d4-a716-446655440000     | +919876543210   | MALE         │
│ 7c9e8400-a31b-42d5-b826-556655441111     | +919876543211   | FEMALE       │
└──────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────┐
│ Generated Failed User IDs                                                │
├──────────────────────────────────────────────────────────────────────────┤
│ No items                                                                 │
└──────────────────────────────────────────────────────────────────────────┘
```

**Key Information Displayed:**
- **User ID**: Unique UUID identifier
- **Phone Number**: Contact number in format +91XXXXXXXXXX
- **Gender**: User gender (MALE/FEMALE)

**Note**: This output is only displayed for **`gen.user`** operations, not for wave/crush operations.

### Automatic Data Cleanup

If any user generation **fails** during the process:

1. **Failed User Details Captured**: The utility tracks all failed user records with complete details
2. **Formatted Output**: Failed users are displayed in a separate box with their details
3. **Automatic Rollback**: `DatabaseFunctions.rollBackFailedUserCreation()` automatically removes failed user data from the database
4. **Clean State**: Ensures no partially created or orphaned user records remain in the system

**Benefits:**
- **No Manual Cleanup Required**: Failed records are automatically removed
- **Database Integrity**: Prevents incomplete or corrupted user data
- **Audit Trail**: See exactly which users failed and why (via error logs)
- **Resource Optimization**: Frees up database resources from failed operations

## Architecture Highlights

### Key Components

| Component | Purpose |
|-----------|---------|
| **GenerateUserOperations** | Main orchestrator; manages parallel execution, database connections, cleanup |
| **DatabaseOperations** | CRUD operations with HikariCP connection pooling, JSON/JSONB support |
| **DatabaseFunctions** | High-level database operations (user generation, image updates, rollback for failed users) |
| **APIService** | GraphQL API client for user setup, MBTI, image uploads, SendBird integration |
| **ThreadUtils** | ThreadLocal storage for per-thread UserDTO isolation in parallel execution |
| **PrintResultOutput** | Formatted box output for displaying user details (ID, phone, gender) |
| **LoggerConfig** | Dynamic Log4j configuration based on GlobalData flags |
| **Common** | Utility methods (timestamps, random data, Firebase token generation) |
| **CryptoUtility** | AES encryption/decryption for sensitive credentials |

### Parallel Execution Flow

```
GenerateUserOperations.main()
    ↓
LoggerConfig.configure()  [Set logging level]
    ↓
ExecutorService.newFixedThreadPool(threadCount)
    ↓
For each thread:
    ↓
    generateUserWorkflow()
        ↓
        ThreadUtils.userDto.set(new UserDetailsDTO())  [Thread-local storage]
        ↓
        DatabaseFunctions.generateRandomNewUser()      [DB insert]
        ↓
        APIService.setupV2WithSpecificData()           [GraphQL API]
        ↓
        APIService.setRandomMbti()                     [MBTI setup]
        ↓
        APIService.uploadDps()                         [Image upload]
        ↓
        DatabaseFunctions.updateUserImages()           [Update DB]
        ↓
        Store UserDetailsDTO in success/failed collections
        ↓
        ThreadUtils.userDto.remove()                   [Cleanup]
    ↓
Wait for all threads to complete
    ↓
PrintResultOutput.printBox()  [Display success users]
    ↓
PrintResultOutput.printBox()  [Display failed users]
    ↓
DatabaseFunctions.rollBackFailedUserCreation()  [Clean up failed records]
    ↓
Shutdown ExecutorService, REST Assured, DB Pool
    ↓
System.exit(0)  [Force clean termination]
```

## Technical Details

### Database Connection Pooling

- **HikariCP** with dynamic pool sizing based on thread count
- Pool size: `max(10, threadCount + 5)`
- Connection leak detection (60s threshold)
- Connection validation with `SELECT 1`
- PostgreSQL-specific optimizations

### Thread Safety

- **ThreadLocal** storage for per-thread user data
- **ExecutorService** manages thread pool lifecycle
- **AtomicInteger** for thread-safe counters
- Automatic cleanup in finally blocks

### Security

- **AES encryption** for database and Firebase credentials
- **Encrypted configuration** in Config.java
- **Firebase JWT** for API authentication
- **PreparedStatements** to prevent SQL injection

### Data Generation

- **JavaFaker** for realistic names, usernames
- **Random data** for UUIDs, phone numbers, AdIDs, referral codes
- **Uniqueness validation** via database queries
- **10,000+ pre-configured** user profiles in userData.json

---

**Note**: This utility is intended for **testing and development purposes only**. For production usage, ensure proper security measures and resource allocation.
