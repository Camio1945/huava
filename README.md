---

# huava （花瓦）

一个用于构建 Java Web 应用程序的脚手架，可以使用 GraalVM 编译为本地镜像。

---

# 原则

1. 支持 GraalVM native image 本地镜像。这个是底线，为了提高启动速度、减少内存消耗，一定要支持 GraalVM native image。

2. 使用 Java 21。

3. 使用 RESTful API，不仅使用 GET 和 POST 方法，还使用 PUT、DELETE 和 PATCH 方法。

4. 尽量循环 [阿里巴巴 Java 编码规范](https://plugins.jetbrains.com/plugin/22381-alibaba-java-coding-guidelines-fix-some-bug-/versions)，但不是所有规范都支持，不支持的都有明确的理由。

5. 遵循几乎所有的 [SonarLint](https://plugins.jetbrains.com/plugin/7973-sonarlint) 规则，不支持的都有明确的理由。

6. 在服务层，如果一个服务有多个公共方法，并且某一个方法有超过 10 行有效代码，则应将其提取到一个单独的子服务类中。

7. 使用 [google-java-format](https://plugins.jetbrains.com/plugin/8527-google-java-format) 格式化代码，以保持代码风格一致。

8. 尽可能多地编写单元测试，尽量保证方法覆盖率在 90% 以上，代码行覆盖率在 90% 以上。

---

# 使用

## 初始化数据库

安装 MySQL 8.0，使用 root 用户执行如下 SQL 脚本创建数据库、用户、表和数据：

docs/<latestversion>/huava-init-<latestversion>.sql

其中 `<latestversion>` 代表最新版本号，如 `v0.2.3`，某个找不到就往前面的版本找，因为并不是每一个版本的 SQL 语句都会有变化。

## 编译成本地镜像

### Linux 系统

依赖软件: gcc、zlib-devel

以 RedHat 系列的 Linux 系统为例，执行以下命令:

```shell
yum install -y gcc zlib-devel
cd huava
chmod +x mvnw
./mvnw -Pnative clean native:compile -DskipTests
```

其中 `huava` 就是本项目的文件夹。

生成的可执行文件路径： huava/target/huava

### Windows 系统

依赖软件: x64 Native Tools Command Prompt for VS 2022 (或更新的版本)

在 `x64 Native Tools Command Prompt for VS 2022` 命令提示符下执行以下命令:

```
cd huava
mvnw -Pnative clean native:compile -DskipTests
```

其中 `huava` 就是本项目的文件夹。

生成的可执行文件路径： huava/target/huava.exe
---

# 问答

### 1. 为什么叫 huava？

后面的 `ava` 来自 `Java`，前面的 `hu` 是中文的拼音，我是湖(hu)北人，姓胡(hu)，还很喜欢 hutool 工具包，结合起来就变成了
`huava`（中文谐音`花瓦`）。一句话描述就是：一个喜欢 hutool 的姓胡的湖北人写的一个 Java 脚手架。

---

### 2. 为什么选择 MySQL 而不是 PostgreSQL？

PostgreSQL 更快，MySQL 更流行，都是很好的选择。但考虑到我以后可能会使用分布式 RDBMS TiDB，而 TiDB 兼容 MySQL 协议，所以第一阶段选择先支持 MySQL 数据库。

---

### 3. 遇到 "Caused by: java.lang.ClassNotFoundException: ...$$Lambda/0x..." 错误

原因：代码使用了 lambda 表达式，而 GraalVM 不支持。

解决方案： 如果找不到的类是你自己写的类（而不是 jar 包中依赖的类），那么执行一下 SerializationConfigGenerator.java 就行了，会自动扫描本项目中用到了 Lambda 表达式的类。

如果报错的是 jar 包中的类：TODO，我还没遇到过这种情况，遇到了再说。

---

### 4. 为什么服务只有一层而不是两层（接口和实现）？

根据我的经验，一个 Service 通常对应数据库中的一张表，并且在项目的整个生命周期中，一个 Service 接口通常只有一个实现。我的观点是，如果一个接口只有一个实现，那就没有必要使用接口。

---

### 5. 为什么将业务表对应的 Service 保存在一个文件夹中而不是一个单一的 Service 类中？

为了防止出现代码行数过多的大类。

对我来说，一个类超过 200 行代码就算是大类。一个方法有超过 15 行有效代码（不包括注释）就是大方法。我会尽量把大方法拆分到单独的 Service 类中。

---

### 6. 为什么主服务类的名称以 `Ace` 开头，而不是以 `Main` 或 `Facade` 开头？

我希望主服务类在字母顺序上排在第一位，`Ace` 可以引申出 `第一` 的意思，所以用了它。
Please translate this to Chinese for me:

---

### 7. 为什么所有子 Service 类都不是 public 的，且所有的方法都是 protected 的？

这样就能实现门面模式，将主 Service 类变成对外提供的唯一入口，从而实现低耦合。

---

### 8. 阿里巴巴强烈反对在表中使用外键，为什么 huava 项目里面还是建了外键？

[阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c/blob/master/Java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C(%E9%BB%84%E5%B1%B1%E7%89%88).pdf) 之所以反对使用外键，是因为它们在插入数据时可能会导致性能问题，因为以阿里的体量，性能是一个很重要的考量因素。

但但是大部分的普通项目并没有那么高的并发，尤其是在开发阶段，性能问题并不会很突出。但是开发阶段有很多程序员会手动编辑数据库，如果不建外键的话，经常会破坏数据的完整性。所以我还是选择要建外键。

如果后续上线前压测时出现了性能问题，再删除外键也不迟。那个时候代码已经趋于稳定，手动改数据库的情况相对就少很多了。

因此，推荐过程如下：

1. 建表时使用外键。
2. 开发项目时，利用外键保护数据的完整性。
3. 如果上线前压测出现了性能问题，就删除外键；如果没有性能问题，就保留外键。

---

### 9. 什么情况下英语单词要使用缩写？

规则：TODO

例子：

1. `Permission` 有 10 个字符，不算太长，但是有另一个基于它的类叫做 `RolePermission`，然后还有 `RolePermissionService` 和
   `RolePermissionMapper`，就有点长了，所以我把它缩写成 `perm`。

参考链接：

1. [软件行业的一些词语的缩写](https://docs.oracle.com/cd/E29376_01/hrcs90r5/eng/psbooks/atpb/chapter.htm?File=atpb/htm/atpb06.htm)

2. 查缩写的网站：[https://www.abbreviations.com/abbreviation/ABBREVIATION](https://www.abbreviations.com/abbreviation/ABBREVIATION)

---

### 10. 在 `pojo` 文件夹中，`dto`, `po`, `qo` 有什么区别？

`pojo`: 普通 Java 对象 (plain old java object).

`dto`: 数据传输对象 (data transfer object).

`po`: 持久化对象 (persistent object)，一个对象对应了数据库表中的一行记录.

`qo`: 请求对象 (quest object / request object). (注意，`q` 代表的是 `quest`，而不是 `query`)

`dto`, `po`, `qo` 都是 `pojo`。

`pojo` 对象有两种：用于请求的和用于响应的。

`qo` 和 `po` 都可以用于请求。

`dto` 和 `po` 都可以用于响应。

如果 `po` 就能满足要求，就不要使用 `dto` 或 `qo`。

注：这些单词是有意小写的，不要使用全大写的方式，原因如下：

如果使用全大写，当定义用户列表时，可能会写成： `List<UserDTO> userDTOs = new ArrayList();`

这时编译器就会有警告：variable [userDTOs] not conform to the lowerCamelCase

但如果是 `List<UserDto> userDtos = new ArrayList();` 就不会有警告。

---

### 11. 为什么不在 `BaseController` 中封装 `page` 方法？

在 `BaseController` 中封装 `page` 方法的优点是可以节省大量代码，缺点是难以扩展。

比如，假设这是 `BaseController` 中通用的代码：

```java

@GetMapping("/page")
public ResponseEntity<ResDto<PageDto<T>>> page(
    @NonNull final PageQo pageQo, @NonNull final T params) {
  // 代码
}
```

如果这个代码封装得好的话，可以解决很多场景中的分页查询问题。但是一旦需要定制的时候，在子类中重写 `page` 方法时就会有问题。

通常，我们需要重写 `page` 方法的情况有两种：

1. 将 `PageDto<T>` 改为另一种类型，例如 `PageDto<RoleDto>`。

2. 将 `T params` 改为另一种类型，例如 `RoleQo`。

在这两种情况下，直接重写都不行，必须在子类中定义一个带有 `@GetMapping` 路径的新方法，比如：

```java

@GetMapping("/customPage")
public ResponseEntity<ResDto<PageDto<RolePo>>> page(
    @NonNull final PageQo pageQo, @NonNull final RoleQo params) {
  // 代码
}
```

这不利于跟前端通信，因为前端可能已经习惯了调 `page` 方法。

---

# 缩写表格

|       原单词       |  缩写   |
|:---------------:|:-----:|
|   permission    | perm  |
| response/result |  res  |
|    parameter    | param |
|                 |       |
|                 |       |
|                 |       |

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



---

# TODO

前端密码加密后再传到后端，后端再次加密后保存到数据库。

[vue3+ts+element-plus密码强弱校验+密码自定义规则校验](https://blog.csdn.net/Johnson_7/article/details/126758162)

IP 地址发生变化，请重新登录。

踢人（一个账号在同一时间只允许在一个地方登录）。

Bug：打开用户列表，再打开角色列表，添加一个新角色，再切换回用户列表，添加新用户，发现刚添加的新角色是不可见的。
