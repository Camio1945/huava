# huava

A scaffold for building Java Web applications which can be compiled to native image using GraalVM.

---

# Principles

1. Use the latest Java, even if it is not LTS. 

2. Support GraalVM native image.

3. Use the restful API, using methods not just GET and POST but also PUT, DELETE, and PATCH.

4. Follow all [Alibaba Java Coding Guidelines](https://plugins.jetbrains.com/plugin/22381-alibaba-java-coding-guidelines-fix-some-bug-/versions), with no exception.

5. Follow all [SonarLint](https://plugins.jetbrains.com/plugin/7973-sonarlint) rules, with no exception.

6. In service layer, if a method has more than 10 lines valid code, it should be extracted to a separate sub-service class.

7. Use [google-java-format](https://plugins.jetbrains.com/plugin/8527-google-java-format) to format the code, in order to keep the code style consistent.

8. Write unit tests for as many codes as possible.

9. Use English comments and documentation. (Intend to practice English for non-native English speakers.)

---

# How to use

## Init MySQL database

Install MySQL 8.0, use your root user to execute the following SQL script to create the database, user, table and data:
docs/sql/v0.0.1/huava-init-v0.0.1.sql

## Compile to native image

### Linux

Prerequisites: gcc, zlib-devel.

Example for RedHat-based Linux:
```shell
yum install -y gcc zlib-devel
cd huava
chmod +x mvnw
./mvnw -Pnative clean native:compile
```

### Windows

Prerequisites: x64 Native Tools Command Prompt for VS 2022 (or later)

```
cd huava
mvnw -Pnative clean native:compile
```

---

# Dependencies

1. GraalVM Java
2. SpringBoot
3. Mybatis-Plus
4. Hutool
5. Lombok
6. MySQL 8.0
7. Redis

---


# Q&A

### 1. Why the name huava?

I want it to be short, hu is my family name, and java is the programming language I use, so I combined them to create the name huava, just like guava.

---

# Changelog

## 0.0.1-SNAPSHOT
Initial version, make sure that the basic CRUD code with Mybatis-Plus can be compiled to native image using GraalVM.
