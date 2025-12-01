# Hunch QA Utility

A comprehensive Java-based test data generation utility designed to support developers and QA engineers in creating realistic test data for the Hunch platform. This utility provides automated user data generation through direct database operations and API integrations.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Dependencies](#dependencies)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Configuration](#configuration)
- [Architecture](#architecture)
- [Logging](#logging)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)

## Overview

The Hunch QA Utility is a robust tool for generating test users with realistic data for testing and development purposes. It supports:

- Direct PostgreSQL database operations for user creation
- GraphQL API integrations for user setup and authentication
- Firebase JWT token management
- Encrypted configuration management
- Realistic data generation using JavaFaker

## Features

- **User Data Generation**: Create complete user profiles with realistic data
- **Database Operations**: Direct PostgreSQL CRUD operations with HikariCP connection pooling
- **API Integration**: GraphQL API client for user setup and authentication flows
- **Firebase Integration**: JWT token generation and validation
- **Secure Configuration**: Encrypted storage of sensitive credentials
- **Data Variety**: Support for multiple user attributes including:
  - Demographics (age, gender, ethnicity)
  - Location details (city, region, country)
  - Dating preferences and relationship types
  - MBTI personality types
  - User interests and tags
- **Logging**: Comprehensive Log4j logging for debugging and audit trails

## Prerequisites

- **Java**: JDK 25 or higher
- **Maven**: 3.6+ for dependency management and build
- **PostgreSQL**: Access to Hunch PostgreSQL database
- **Firebase**: Service account credentials for Firebase authentication
- **Network**: Access to Hunch development API endpoints

## Project Structure

```
hunch-qa-utility/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/
│   │   │       └── hunch/
│   │   │           ├── apis/                  # API client implementations
│   │   │           │   ├── APIService.java
│   │   │           │   └── BaseApi.java
│   │   │           ├── constants/             # Configuration constants
│   │   │           │   └── Config.java
│   │   │           ├── core/                  # Core HTTP client functionality
│   │   │           │   ├── AbstractApiClient.java
│   │   │           │   ├── HttpClient.java
│   │   │           │   ├── HttpMethodType.java
│   │   │           │   ├── HttpResponseStatusType.java
│   │   │           │   ├── MimeType.java
│   │   │           │   └── RequestParams.java
│   │   │           ├── enums/                 # Enumeration types
│   │   │           │   ├── DesiredRelationshipType.java
│   │   │           │   ├── Ethnicity.java
│   │   │           │   ├── Gender.java
│   │   │           │   ├── Tags.java
│   │   │           │   └── core/
│   │   │           │       └── RequestBodySchemaFileEnums.java
│   │   │           ├── log/                   # Custom logging utilities
│   │   │           │   └── LoggingOutputStream.java
│   │   │           ├── models/                # Data models
│   │   │           │   ├── db/
│   │   │           │   │   └── InsertUsers.java
│   │   │           │   ├── FirebaseServiceAccount.java
│   │   │           │   ├── MBTIPolls.java
│   │   │           │   ├── RequestBody.java
│   │   │           │   ├── SetupUserV2.java
│   │   │           │   └── SmsLoginOtp.java
│   │   │           ├── operations/            # Main operations
│   │   │           │   └── GenerateUserOperations.java
│   │   │           ├── utils/                 # Utility classes
│   │   │           │   ├── database/
│   │   │           │   │   ├── DatabaseFunctions.java
│   │   │           │   │   ├── DatabaseOperations.java
│   │   │           │   │   ├── DBConfig.java
│   │   │           │   │   └── PostgresDBConnections.java
│   │   │           │   ├── Common.java
│   │   │           │   ├── CryptoUtility.java
│   │   │           │   ├── FirebaseJWTManager.java
│   │   │           │   ├── GraphQLFileUtil.java
│   │   │           │   └── ThreadUtils.java
│   │   │           └── Main.java
│   │   └── resources/
│   │       ├── graphqlSchema/                 # GraphQL query definitions
│   │       │   ├── MBTI.graphql
│   │       │   ├── MBTIPolls.graphql
│   │       │   ├── SetupUserV2.graphql
│   │       │   ├── SmsLoginOtp.graphql
│   │       │   └── VerifyOtp.graphql
│   │       └── log4j.properties              # Logging configuration
│   └── test/
│       └── java/                              # Test classes
├── pom.xml                                    # Maven configuration
└── README.md                                  # This file
```

## Dependencies

### Core Libraries
- **Jackson Databind** (3.0.0): JSON serialization/deserialization
- **org.json** (20240303): JSON parsing and manipulation
- **Lombok** (1.18.38): Reduce boilerplate code

### Database
- **PostgreSQL Driver** (42.7.8): JDBC driver for PostgreSQL
- **HikariCP** (5.1.0): High-performance JDBC connection pool

### Testing & Data Generation
- **REST Assured** (4.3.2): API testing framework
- **JavaFaker** (1.0.2): Generate realistic fake data

### Authentication & Security
- **Firebase Admin SDK** (9.7.0): Firebase authentication
- **JJWT** (0.12.6): JWT token creation and parsing

### Utilities
- **Gson** (2.11.0): Alternative JSON library
- **Log4j** (1.2.17): Logging framework

## Getting Started

### 1. Clone the Repository

```bash
git clone git@github.com:aashit-hunch/hunch-qa-utility.git
cd hunch-qa-utility
```

### 2. Configure Database Credentials

Update the encrypted credentials in `Config.java` or use the encryption utility:

```java
// Use CryptoUtility to encrypt your credentials
String encryptedHost = CryptoUtility.encrypt("your-db-host");
String encryptedUser = CryptoUtility.encrypt("your-db-user");
String encryptedPass = CryptoUtility.encrypt("your-db-password");
String encryptedName = CryptoUtility.encrypt("your-db-name");
```

### 3. Configure Firebase

Ensure Firebase service account credentials are properly encrypted and configured in `Config.java`.

### 4. Build the Project

```bash
mvn clean install
```

## Usage

### Running the Main Operation

The primary entry point for user generation is `GenerateUserOperations.java`:

```bash
mvn exec:java
```

This command executes the main class configured in `pom.xml`:
```xml
<mainClass>org.hunch.operations.GenerateUserOperations</mainClass>
```

### Current Functionality

The utility currently:

1. **Generates Random User Data**:
   - UUID for user identification
   - Random phone number (Indian format: +91XXXXXXXXXX)
   - Email based on UUID (@hunchmobile.com)
   - Random username
   - Random adjust AdID
   - Referral code

2. **Creates Database Record**:
   - Inserts user into PostgreSQL `users` table
   - Sets default values for various fields
   - Includes location details (city, region, country)
   - Initializes timestamps (created_at, updated_at, replicated_at)

3. **Validates Data**:
   - Ensures unique UUIDs, phone numbers, usernames
   - Checks database for existing records before insertion

### Example Output

```
INFO - Generating Random UUID
INFO - Generating Random Phone Number
INFO - Generating Random Username
INFO - Generating Random Adjust Ad ID
INFO - Generating Random Referral Code
INFO - Insert Status: 1
INFO - Generated User Details :
 User UID: 550e8400-e29b-41d4-a716-446655440000
 Phone Number: +919876543210
 Email: 550e8400-e29b-41d4-a716-446655440000@hunchmobile.com
```

## Configuration

### Database Configuration

Database connection uses HikariCP for connection pooling:

```java
DBConfig config = new DBConfig(
    CryptoUtility.decrypt(Config.DB_HOST),  // Host
    5432,                                    // Port
    CryptoUtility.decrypt(Config.DB_USER),  // Username
    CryptoUtility.decrypt(Config.DB_PASS),  // Password
    CryptoUtility.decrypt(Config.DB_NAME)   // Database name
);
```

### Logging Configuration

Logging is configured via `log4j.properties`:

- **Console Output**: Displays INFO level logs
- **File Output**: Writes logs to `target/logfile.log`
- **Pattern**: `%d %p [%X{reference}] [%c : %L] - %m%n`

## Architecture

### Key Components

#### 1. **GenerateUserOperations** (Main Class)
The primary orchestrator for user generation operations:
- Manages database connections
- Coordinates user data generation
- Handles unique value validation
- Executes database insertions

#### 2. **DatabaseOperations**
Provides CRUD operations for database:
- `insert()`: Insert records with prepared statements
- `findById()`: Retrieve records by ID
- `findWhere()`: Query with custom WHERE clause
- `update()`: Update existing records
- `delete()`: Remove records
- Supports JSON/JSONB PostgreSQL types
- Automatic timestamp string to `java.sql.Timestamp` conversion

#### 3. **InsertUsers** (Model)
Comprehensive user data model with 100+ fields:
- User identification (UUID, email, username)
- Demographics (age, gender, ethnicity, pronouns)
- Location data (city, region, country, coordinates)
- Dating preferences (height, relationship type)
- Social features (bio, interests, tags)
- Account settings (privacy, NSFW, verification status)
- Timestamps (created_at, updated_at, profile_setup_at)

#### 4. **CryptoUtility**
Handles encryption/decryption of sensitive configuration:
- AES encryption for database credentials
- Secure key management
- Firebase credentials encryption

#### 5. **Common Utilities**
Shared utility functions:
- Timestamp generation (`yyyy-MM-dd HH:mm:ss.SSS`)
- Random alphanumeric string generation
- Enum randomization
- Jackson ObjectMapper configuration

### Data Flow

```
GenerateUserOperations
    ↓
Generate Random Data
    ↓
Validate Uniqueness (DB Check)
    ↓
Create InsertUsers Object
    ↓
Convert to JSONObject
    ↓
DatabaseOperations.insert()
    ↓
PostgreSQL Database
```

## Future Enhancements

The utility is designed to support command-line arguments and system properties for customization:

### Planned Features

```bash
# Generate user with custom data
mvn exec:java -Duser.count=10 \
              -Duser.gender=female \
              -Duser.country=India \
              -Duser.age.min=25 \
              -Duser.age.max=35

# Generate users with specific features
mvn exec:java -Duser.profile.complete=true \
              -Duser.dating.enabled=true \
              -Duser.verification=verified

# Batch operations
mvn exec:java -Dmode=batch \
              -Dbatch.size=100 \
              -Doutput.format=csv
```

### Roadmap

- [ ] CLI argument parsing for custom inputs
- [ ] Batch user generation
- [ ] CSV/JSON export functionality
- [ ] API-based user setup flow
- [ ] Custom data templates
- [ ] Validation and testing utilities
- [ ] Performance metrics and reporting
- [ ] Integration with CI/CD pipelines

## Contributing

### Code Style

- Follow Java naming conventions
- Use Lombok annotations for boilerplate reduction
- Add appropriate logging for debugging
- Handle exceptions gracefully
- Document complex logic

### Adding New Operations

1. Create a new class in `org.hunch.operations`
2. Extend or use existing database utilities
3. Add necessary models in `org.hunch.models`
4. Update this README with new functionality

### Testing

```bash
# Run tests
mvn test

# Run with debug logging
mvn exec:java -Dlog4j.debug=true
```

## License

[Add your license information here]

## Support

For issues, questions, or contributions, please contact the development team or open an issue in the repository.

---

**Note**: This utility is intended for testing and development purposes only. Ensure proper security measures when handling production credentials and data.
