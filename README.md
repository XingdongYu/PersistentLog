# 🚀PersistentLog
持久化日志，用于服务器定期回捞，可定期清除。

使用
---
```kotlin
val logConfig = LogConfig(
        // 是否调试模式
        debug = false,
        // 设备数据
        deviceInfo = "Device Info",
        // 数据库检查周期
        dbCheckInterval = 4000,
        // 数据库容量
        dbThreshold = 1000,
        // 缓存大小
        cacheSize = 50,
        // 日志存储方式
        logMode = LogMode.DATABASE
)
SLog.init(this, logConfig)

// 自定义CrashHandler中
override fun uncaughtException(t: Thread?, e: Throwable?) {
    SLog.crash(TAG, e)
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
- [ ] message数据加密
- [ ] 文件模式存储优化
