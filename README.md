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

### 2. Why MySQL over PostgreSQL?

PostgreSQL is faster , MySQL is more popular, both are great choice. But I may want to use distributed RDBMS in the future, which is TiDB, and TiDB is compatible with MySQL protocol, so I choose MySQL.

---

### 3. Encountered "Caused by: java.lang.ClassNotFoundException: ...$$Lambda/0x..."

Cause: The code uses lambda expression, which is not supported by GraalVM.

Solution: Register your class in [LambdaRegistrationFeature.java](src%2Fmain%2Fjava%2Fcn%2Fhuava%2Fcommon%2Fconfig%2FLambdaRegistrationFeature.java) file, in the
`duringSetup` method (examples are already there).

---

### 4. Why the services has only one layer but not two (interface and implement)?

In my own experience, one service usually corresponds to a table in the database, and one service interface usually has only one implementation during the whole lifetime of a project. So my opinion is that if an interface has only one implementation for good, it's better to use just one single layer.

---

### 5. Why keep a table's services in a folder but not in a single service class?

I don't want large classes. A class that has more than 200 lines is a large class for me. I want to split large methods into small classes.

If a method has more than 15 lines of valid code in it (comment not included), it is a large method for me. Then it will be extracted into multiple methods, but that will make the single service class less clear, so it is better to move them to a separate sub-service class.

---

### 6. Why does the main service class's name end with `AceService` but not `MainService` or just `Service`?

I want the main service class to be alphabetically first, so I named it `AceService`.

---

### 7. Why are all the sub-service classes' methods protected?

In the previous version before 0.1.3, all the sub-service classes were not public, and their methods were protected. In this way, we can make the main service class a facade, the only entrance to the outside world, to achieve low coupling.

But then it appears that many service classes will use lambda expressions like
`new LambdaQueryWrapper<SysUserPo>().eq(SysUserPo::getLoginName, username)`, and if a class uses lambda expression, it has to be registered in
`LambdaRegistrationFeature.java` file in order to make the GraalVM native image work. So the sub-service classes must be public so that they can be referenced by the
`LambdaRegistrationFeature` class.

---

# How to

---

### 1. How to generate `project.rsa_public_key` and `project.rsa_public_key` in application.yml ?

```java
KeyPair keyPair = org.dromara.hutool.crypto.KeyUtil.generateKeyPair("RSA", 2048);

String publicKeyBase64 = org.dromara.hutool.core.codec.binary.Base64.encode(keyPair.getPublic().getEncoded());

String privateKeyBase64 = org.dromara.hutool.core.codec.binary.Base64.encode(keyPair.getPrivate().getEncoded());
```

---

# Kudos to

[若依管理系统](https://ruoyi.vip/)


