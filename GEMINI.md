# Project Overview

This project is a Java web application scaffold built with Spring Boot. It's designed to be compiled into a native image using GraalVM, which can result in faster startup times and lower memory consumption.

The main features of this project are:

*   **Spring Boot:** A popular framework for building Java web applications.
*   **MyBatis:** A persistence framework for working with SQL databases.
*   **Skija:** A 2D graphics library used to generate captcha images.
*   **GraalVM Native Image:** The project is configured to be compiled into a native executable.

## Building and Running

### Prerequisites

*   Java 25
*   Maven
*   GraalVM (for native image compilation)

### Build and Run (JAR)

To build the project and run it as a JAR file, use the following command:

```bash
./mvnw spring-boot:run
```

### Build and Run (Native Image)

To build a native image of the project, use the following command:

```bash
./mvnw native:compile
```

This will create an executable file in the `target` directory. You can then run the application like this:

```bash
./target/huava
```

## Development Conventions

*   **Testing:** The project uses JUnit 5 for testing. Tests are located in the `src/test/java` directory.
*   **Code Coverage:** The JaCoCo Maven plugin is used to generate code coverage reports.
*   **Code Style:** The project uses the standard Java code style. There is an `.editorconfig` file to help enforce consistent coding styles.
*   **Dependencies:** Dependencies are managed using Maven. Add new dependencies to the `pom.xml` file.
