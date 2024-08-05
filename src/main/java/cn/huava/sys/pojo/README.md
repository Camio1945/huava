***

### 说明

文件夹的命名是根据 [阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c) 来的：

1. POJO（Plain Ordinary Java Object）：在本规约中，POJO 专指只有 setter / getter / toString 的简单类，包括 DO / DTO / BO / VO 等。

2. DO（Data Object）：阿里巴巴专指数据库表一一对应的 POJO 类。 此对象与数据库表结构一一对应，通过 DAO 层 向上传输数据源对象。（huava 项目不用）

3. PO（Persistent Object）：也指数据库表一一对应的 POJO 类。 此对象与数据库表结构一一对应，通过 DAO 层向上 传输数据源对象。

4. DTO（Data Transfer Object ）：数据传输对象，Service 或 Manager 向外传输的对象。

5. BO（Business Object）：业务对象，可以由 Service 层输出的封装业务逻辑的对象。

6. Query：数据查询对象，各层接收上层的查询请求。注意超过 2 个参数的查询封装，禁止使用 Map 类来传输。

7. VO（View Object）：显示层对象，通常是 Web 向模板渲染引擎层传输的对象。
