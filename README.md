# huava

A scaffold for building Java Web applications which can be compiled to native image using GraalVM.

---

# Principles

1. Use Java 21 or the latest Java even if it is not LTS.

2. Support GraalVM native image.

3. Use the restful API, using methods not just GET and POST but also PUT, DELETE, and PATCH.

4. Follow almost all [Alibaba Java Coding Guidelines](https://plugins.jetbrains.com/plugin/22381-alibaba-java-coding-guidelines-fix-some-bug-/versions).

5. Follow almost all [SonarLint](https://plugins.jetbrains.com/plugin/7973-sonarlint) rules.

6. In service layer, if a service has more than 1 public method and one method has more than 10 lines valid code, it should be extracted to a separate sub-service class.

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

### 7. Why are all the sub-service classes not public and their methods protected?

In this way, we can make the main service class a facade, the only entrance to the outside world, to achieve low coupling.

Note: The sub-service classes may contain lambda expressions in them, and since the class is not public, it cannot be registered in the `LambdaRegistrationFeature.java` file; it needs to be registered in the `src/main/resources/META-INF/native-image/serialization-config.json` file, e.g.:

```json
{
  "types":[
  ],
  "lambdaCapturingTypes":[
    {"name": "cn.huava.sys.service.sysuser.LoginService"},
    {"name": "cn.huava.common.config.SecurityConfig"}
  ],
  "proxies":[
  ]
}
```


---

### 8. Why use foreign keys in tables when Alibaba is strongly against it?

The [Alibaba Java Development Guidelines](https://github.com/alibaba/p3c/blob/master/Java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C(%E9%BB%84%E5%B1%B1%E7%89%88).pdf) strongly against foreign keys, because they can cause performance issues when inserting data.

But I've seen a lot of programmers edit the database by hand and destroy the integrity of the data, and it's very hard to recover from such mistakes. So I still use foreign keys in my project.

If the performance issue occurs, you can delete the foreign keys, and by that time, the project will have probably been deployed in production mode, we will have much less opportunity to edit data by hand.

So the process is:

1. Use foreign keys in the beginning.
2. Develop the project, the foreign keys will protect the data integrity.
3. If and only if the performance issue occurs in production, delete the foreign keys.

---

### 9. What's the rules of abbreviation?

Rules: TODO

Example:

1. `Permission` has 10 characters; it's not too long, but the table has the prefix `sys`, so the persistent class becomes `SysPermission`, and there is another class based on it called `SysRolePermission`, and then `AceSysRolePermissionService` and `SysRolePermissionMapper`; it's kind of too long for me now. So I abbreviate it to `perm`.

---

### 10. Why combine java classes (like `RuntimeHintsRegistrarConfig.java`) and `src/main/resources/META-INF/native-image` folder as two means to register GraalVM native image?

Though [Collecting Metadata with the Tracing Agent](https://www.graalvm.org/latest/reference-manual/native-image/metadata/AutomaticMetadataCollection/) is convenient, it generates a large amount of metadata, for example, the `SysRoleMapper.java`'s metadata is like this (41 other methods are ignored): 

```json
{
    "name": "cn.huava.sys.mapper.SysRoleMapper",
    "queryAllDeclaredMethods": true,
    "queryAllPublicMethods": true,
    "methods": [
        {
            "name": "delete",
            "parameterTypes": [
                "com.baomidou.mybatisplus.core.conditions.Wrapper"
            ]
        },
        // Here ignored 41 other methods
    ]
}
```
But the `RuntimeHintsRegistrarConfig.java` can handle all `*Mapper.java` generically, saves a lot of code.

But the `src/main/resources/META-INF/native-image` folder is still necessary. For example, the sub-service classes are all not public; they cannot be referenced in the `NativeHintsRegistrar.java` file; they need to be registered in the `src/main/resources/META-INF/native-image/serialization-config.json` file.

To sum up, the Java classes are necessary for less code, and the `src/main/resources/META-INF/native-image` folder is necessary for more control.

For my experience, only `serialization-config.json` is needed; these are not needed: `jni-config.json`, `predefined-classes-config.json`, `proxy-config.json`, `reflect-config.json`, `resource-config.json`.

---

# Abbreviations Table

| Original word | Abbreviation |
|:-------------:|:------------:|
|  permission   |     perm     |
|               |              |
|               |              |
|               |              |
|               |              |


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


