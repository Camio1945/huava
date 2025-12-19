# Huava (花瓦) Project

## Project Overview

This is a Java web application scaffold designed to be compiled into a native image using GraalVM, improving startup speed and reducing memory consumption. It is built with Java 21 and Spring Boot and follows RESTful API principles, utilizing `GET`, `POST`, `PUT`, `DELETE`, and `PATCH` methods.

Key technologies used include:
* Java 21
* Spring Boot
* Spring Security
* MyBatis Plus
* Redisson
* MySQL
* GraalVM

## Building and Running

### Prerequisites
* JDK 21
* Maven
* MySQL 8.0
* GraalVM (for native image compilation)

### Database Setup
1.  Install MySQL 8.0.
2.  Create a database and user for the application.
3.  Execute the latest SQL script from the `docs/old_versions/` directory to initialize the schema and data. The script is named `huava-init-<version>.sql`.

### Compiling and Running
You can compile the application into a native image, which will improve performance and reduce memory usage.

#### On Linux
1.  Install the required dependencies:
    ```shell
    yum install -y gcc zlib-devel
    ```
2.  Build the native image:
    ```shell
    chmod +x mvnw
    ./mvnw -Pnative clean native:compile -DskipTests
    ```
3.  Run the application:
    ```shell
    ./target/huava
    ```

#### On Windows
1.  Install the x64 Native Tools Command Prompt for VS 2022 (or a newer version).
2.  Build the native image:
    ```shell
    mvnw -Pnative clean native:compile -DskipTests
    ```
3.  Run the application:
    ```shell
    target/huava.exe
    ```
    
### Running in JVM Mode
You can also run the application in a traditional JVM:
```shell
./mvnw spring-boot:run
```

## Development Conventions

This project follows a strict set of development conventions to maintain code quality and consistency:
* **Code Formatting**: All code is formatted using `google-java-format`.
* **Static Analysis**: The project adheres to the rules of the Alibaba Java Coding Guidelines and SonarLint.
* **Unit Testing**: All new code should be accompanied by unit tests, with the goal of maintaining at least 90% code coverage.
* **Service Layer**: If a service has multiple public methods and any single method exceeds 10 lines of effective code, it should be extracted into a separate sub-service class.
* **RESTful APIs**: The project uses RESTful API design principles, including the use of `PUT`, `DELETE`, and `PATCH` methods in addition to `GET` and `POST`.

## Project Structure
The project follows a specific structure that is important to understand when making changes:
* **No Service Interfaces**: The service layer does not use interfaces, as each service typically has only one implementation.
* **Service Class Naming**: Each main service class is prefixed with `Ace` (e.g., `AceUserService`) to indicate that it is the primary entry point for that service.
* **Sub-services**: To avoid large classes, services are broken down into smaller, non-public sub-service classes with protected methods. This enforces a facade pattern where the `Ace` class is the only public entry point.
* **POJO Naming Conventions**:
    * **`po` (Persistent Object)**: Represents a row in a database table.
    * **`dto` (Data Transfer Object)**: Used for transferring data between layers.
    * **`qo` (Query Object)**: Used for query parameters.
    
These naming conventions are used to distinguish between different types of plain old Java objects (POJOs).
