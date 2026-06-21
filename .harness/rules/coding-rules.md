# 编码规范

> 编码风格、命名规范、类型约束。每一条规则对应一个历史失败案例。

## 类型与单位约束

| 约束 | 规范 | 反例 | 原因 |
|------|------|------|------|
| 价格字段 | `long` 类型，单位为分 | `double` / `BigDecimal` | 精度丢失导致资损 |
| 金额计算 | `long` 运算 | `double` 运算 | 浮点数精度问题 |
| 时间字段 | `Date` / `LocalDateTime` | `String` 存储时间 | 时区转换问题 |
| 百分比 | `int` 万分比（basis points） | `double` 百分比 | 精度损失 |
| ID 字段 | `long` | `String` / `Integer` | 分布式 ID 溢出 |
| 状态字段 | `int` / `enum` | `String` | 类型安全 |

## 命名规范

### Java 命名
- **类名**：UpperCamelCase，名词或名词短语（`PriceService`, `OrderQuery`）
- **方法名**：lowerCamelCase，动词或动词短语（`getPriceById`, `calculateDiscount`）
- **常量**：UPPER_SNAKE_CASE（`MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT`）
- **包名**：全小写，域名倒序（`com.company.pricecenter.core.service`）
- **布尔变量**：禁止用 `is` 开头（`isValid` → `valid`），避免序列化问题

### 数据库命名
- **表名**：小写蛇形，业务前缀（`price_rule`, `price_config`）
- **字段名**：小写蛇形（`gmt_create`, `gmt_modified`, `creator_id`）
- **主键**：`id`（bigint 自增）
- **索引名**：`idx_{表名}_{字段名}`

## 代码规范

### 方法设计
- 方法体不超过 80 行，超出考虑拆分
- 方法的圈复杂度不超过 10
- 禁止有多个 return 语句（除早期 guard clause）
- 方法参数不超过 4 个，超出用参数对象封装

### 异常处理
- 禁止 catch Exception 后不做任何处理
- 禁止使用 e.printStackTrace()
- 日志必须包含业务上下文（如 orderId, userId）
- 异常信息必须有可读性，禁止抛出无信息的异常

### 日志规范
- 统一使用 SLF4J + Logback
- 日志级别使用原则：ERROR（系统异常）、WARN（业务异常）、INFO（关键流程）、DEBUG（调试）
- 禁止在循环中打印日志
- 敏感信息（密码、手机号、身份证）脱敏后打印

### 注释规范
- 类注释包含：类功能说明、作者、创建日期
- 接口方法必须有 JavaDoc 注释
- 复杂业务逻辑必须有行内注释说明意图
- 禁止注释掉的代码（直接删除）

### 测试规范
- 单元测试覆盖率：新增代码 ≥ 80%
- 测试方法命名：`test{MethodName}_{Scenario}_{ExpectedResult}`
- 每个测试方法必须包含 Arrange-Act-Assert 三段式结构
- 禁止测试依赖外部环境（DB、缓存、RPC 需 Mock）

## 文件规范

- 单文件不超过 500 行（不含自动生成的代码）
- 一个文件只定义一个 public class/interface
- 内部类合理使用，不超过 2 个
- 禁止 import *，必须使用明确导入
