# 🚀PersistentLog
持久化日志，用于服务器定期回捞，可定期清除。

流程图
---
```flow
st=>start: SLog.i()
e=>end: 结束
op1=>operation: 存入缓存
op2=>operation: 存入数据库
op3=>operation: 清除缓存
cond1=>condition: 缓存大于阈值?

st->op1->cond1
cond1(yes)->op2->op3->e
cond1(no)->e
```

使用
---
```java
final LogConfig logConfig = new LogConfig.Builder()
        // 是否调试模式
        .debug(false)
        // 设备数据
        .deviceInfo(UUID.randomUUID() + "")
        // 数据库检查周期
        .dbCheckInterval(4000)
        // 数据库容量
        .dbThreshold(1000)
        // 缓存大小
        .cacheSize(50)
        // 日志存储方式
        .logMode(LogMode.DATABASE)
        .build();

SLog.init(this, logConfig);

// 自定义CrashHandler中
public void uncaughtException(Thread thread, Throwable t) {

    SLog.crash(TAG, t);
    ...
}
```

Demo演示
------
新建100个线程、每个线程打印4条日志的测试数据:

![image](https://github.com/XingdongYu/PersistentLog/blob/master/art/sample.gif)

数据库
---

![image](https://github.com/XingdongYu/PersistentLog/blob/master/art/database.png)

内存占用
---

![image](https://github.com/XingdongYu/PersistentLog/blob/master/art/memory.png)

TODO
---
- [ ] 文件模式存储优化
