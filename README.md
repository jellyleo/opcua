# OPC UA

OpcUa协议的Java实现，项目基于Eclipse Milo库开发。服务端采用KepServer 6.X。

---

## 使用步骤：

### 1、启动服务端。
> 打开KEPServerEX 6 Administration

> 添加项目通道、设备和标记

> 项目-属性 启用匿名登录（客户端使用匿名验证时需要）

> 设置-用户管理器 新建用户

> OPCUA配置-服务器端点，TCP连接和安全策略

> 重新初始化服务器运行时

### 2、启动客户端
> 更改application.properties配置文件
>> OpcUaClientConfig.setIdentityProvider()提供四种验证方式：

>> AnonymousProvider

>> CompositeProvider

>> UsernameProvider

>> X509IdentityProvider

>> 其中匿名验证和用户名验证较为常用和简单

> 启动OpcUaApplication

### 3、接口调试
> /connect 建立连接

> /disconnect 断开连接

> /read 节点读取（较为常用信息是节点值和值数据类型）

> /write 节点值写入 需注意服务端节点类型（只读/读/写）和客户端DataValue的数据类型

> /subscribe 订阅节点 监控值有变化时回调通知 （监控参数clientHandle需自动获取，若使用统一的参数批量订阅时会造成先行节点被覆盖）

---

Eclipse Milo 地址：https://github.com/eclipse/milo