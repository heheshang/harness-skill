# 架构规则

> 工程结构约束与分层架构约定。这些是不随需求变化的稳定约束（Invariant Constraints）。

## 模块结构

```
price-center/
├── app/              # 接入层 — RPC Provider / Controller
│   ├── controller/   # REST 接口
│   ├── provider/     # HSF RPC Provider
│   └── validator/    # 参数校验
├── web/              # 展现层 — 管理后台
│   ├── controller/   # 后台接口
│   └── vo/           # 视图对象
├── core/             # 核心业务层
│   ├── service/      # 业务服务接口
│   ├── service/impl/ # 业务服务实现
│   ├── domain/       # 领域模型
│   ├── manager/      # 业务编排
│   └── flow/         # LiteFlow 流程组件
├── integration/      # 集成层
│   ├── rpc/          # 外部 RPC 调用
│   ├── cache/        # 缓存操作 Tair/Redis
│   └── mq/           # 消息队列
├── common/           # 公共层
│   ├── util/         # 工具类
│   ├── constant/     # 常量定义
│   └── exception/    # 异常定义
├── dal/              # 数据访问层
│   ├── mapper/       # MyBatis Mapper
│   ├── model/        # 数据模型（DO）
│   └── config/       # 数据源配置
└── bootstrap/        # 启动层
    └── config/       # 应用配置
```

## 分层依赖规则

```
app → core → integration
  ↘         ↘
   web → common → dal
```

- **上层可依赖下层，下层不可依赖上层**
- **common 可被任意层依赖，但 common 不可依赖其他模块**
- **app 和 web 层互不依赖**
- **dal 层不可依赖 core 和 integration**

## 核心架构原则

### 1. RPC 接口设计
- RPC 接口必须定义 interface + DTO，放在独立的 API 模块
- 接口必须指定 version 和 timeout
- DTO 必须实现 Serializable
- 返回值统一使用 Result<T> 包裹

### 2. 流程编排
- LiteFlow 组件必须委托 Service 层处理，组件内不写大段业务逻辑
- 组件职责单一，一个组件只做一件事
- 流程配置（XML）统一放在 `core/flow/config/` 目录

### 3. 配置中心
- Diamond 配置变更必须有对应的监听器
- 动态配置必须考虑默认值，防止配置中心不可用时系统异常
- 敏感配置（密码、密钥）禁止明文存储在配置中心

### 4. 缓存设计
- 缓存 Key 必须包含业务前缀，避免冲突
- 缓存更新必须考虑一致性问题（先更新 DB 再删除缓存）
- 批量操作必须注意缓存逐出的性能影响

### 5. 异常处理
- 业务异常继承 BaseBizException，包含错误码和错误消息
- 所有外部 RPC 调用必须有 try-catch 和降级处理
- 禁止在 catch 中吞掉异常（至少打印日志）

### 6. 国际化
- 用户侧展示信息必须走国际化资源文件
- 错误码配置对应的国际化消息
- 新增功能必须同步检查国际化的影响范围
