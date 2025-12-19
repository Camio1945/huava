# huava Project Context

## Project Overview

huava is a Java Web application scaffolding project designed for building applications that can be compiled to native images using GraalVM. It follows modern Java development practices with Java 21 and Spring Boot 3.3.3, focusing on creating efficient, fast-starting applications with reduced memory consumption.

### Key Features:
- Support for GraalVM native image compilation
- Built with Java 21 and Spring Boot 3.3.3
- RESTful API architecture supporting GET, POST, PUT, DELETE, and PATCH methods
- MySQL 8.0 database integration with foreign key support
- Redis caching using Redisson
- JWT-based authentication
- Comprehensive testing with high coverage targets

### Architecture & Tech Stack:
- **Framework**: Spring Boot 3.3.3
- **Language**: Java 21
- **Database**: MySQL 8.0 with MyBatis Plus
- **Caching**: Redis with Redisson
- **Security**: Spring Security
- **Serialization**: JSON with Jackson
- **Build Tool**: Maven (with wrapper)
- **Format**: Google Java Format for code consistency
- **Code Quality**: SonarLint rules compliance
- **Annotation Processing**: Lombok

## Building and Running

### Standard Development Server:
```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or if you have Maven installed locally
mvn spring-boot:run
```

### Compile to Native Image:
#### On Linux:
```bash
# Install prerequisites
yum install -y gcc zlib-devel  # For RedHat-based systems

# Make mvnw executable and compile
chmod +x mvnw
./mvnw -Pnative clean native:compile -DskipTests
```

#### On Windows:
```bash
# Run in x64 Native Tools Command Prompt for VS 2022 or later
mvnw -Pnative clean native:compile -DskipTests
```

### Initialize Database:
1. Install MySQL 8.0
2. Execute the initialization SQL script from `docs/<latestversion>/huava-init-<latestversion>.sql`
3. Use the root user or a user with sufficient privileges

### Default Configuration:
- Server port: 22345
- Database URL: jdbc:mysql://localhost:3306/huava
- Database user: huava
- Redis: redis://localhost:6379
- Default access: http://localhost:22345/

## Development Conventions

### Code Organization:
- Services follow a specific pattern where large service classes (over 200 lines) or services with methods longer than 15 lines are split into sub-services
- Main service classes start with "Ace" prefix to appear first alphabetically
- Sub-service classes are non-public with protected methods to implement facade pattern

### Project Structure:
- `dto`: Data Transfer Objects for API responses
- `po`: Persistent Objects mapping to database records
- `qo`: Request Objects (Quest Objects) for API requests
- Services are organized in packages under `cn.huava.sys`
- Common utilities and configurations in `cn.huava.common`

### Testing Practices:
- Target 90%+ method coverage and 90%+ line coverage
- Extensive unit testing encouraged
- Test classes located under src/test/java

### Special Considerations for GraalVM:
Since the project supports GraalVM native compilation, special attention must be paid to:
- Lambda expressions need special handling for native compilation
- Classes using lambda expressions may need to be registered in serialization-config.json
- Reflection usage must be configured properly for native compilation

### Naming and Abbreviation Conventions:
- `perm` for Permission
- `res` for Response/Result  
- `param` for Parameter
- POJO objects are lowercase intentionally to avoid camelCase warnings

## Important Files & Configuration:

- `pom.xml` - Maven configuration with dependencies and native build plugins
- `application.yml` - Main application configuration
- `HuavaApplication.java` - Main entry point
- `serialization-config.json` - GraalVM native image serialization configuration
- `LambdaRegistrationFeature.java` - Handles lambda expressions for native compilation