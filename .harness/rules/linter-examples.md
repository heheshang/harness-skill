# Custom Linter Rules（自定义 Lint 规则）

> 参考：[OpenAI: Harness engineering - leveraging Codex in an agent-first world]
>
> "We enforce these rules with custom linters and structural tests,
> plus a small set of 'taste invariants.'"
>
> 可机械执行的架构约束 → 比写在文档里的规则更有效。
> Agent 可能忽略 README，但不能忽略编译失败。

## 规则类型

### 类型 A：编译期检查（最可靠）

在 `pom.xml` 或代码中直接嵌入检查，违反即编译失败。

### 类型 B：测试期检查

在 CI 中和单元测试一起运行，违反即测试失败。

### 类型 C：CI 门禁检查

在 CI pipeline 中独立步骤运行。

### 类型 D：定期扫描检查

由 Entropy GC 定期扫描，不阻塞 CI 但生成 PR。

## 示例规则

### LINT-001：禁止使用 double/float 表示金额

```java
// ❌ 错误
double price = 99.99;

// ✅ 正确
long price = 9999L; // 单位：分
// 或使用 BigDecimal
BigDecimal price = new BigDecimal("99.99");
```

**检查方式：**
```
搜索：在价格相关类中搜索 double/float 类型声明
执行：mvn checkstyle:check 或自定义 PMD 规则
```

### LINT-002：RPC 接口必须定义 version 和 timeout

```java
// ❌ 错误
public interface PriceService {
    Price getPrice(Long skuId);
}

// ✅ 正确
@HSFProvider(service = "PriceService", version = "1.0.0", timeout = 3000)
public interface PriceService {
    Price getPrice(Long skuId);
}
```

**检查方式：**
```
搜索：所有 @HSFProvider 注解
验证：每个注解必须有 version 和 timeout 属性
```

### LINT-003：禁止循环依赖

```java
// ❌ 错误：ServiceA → ServiceB → ServiceA
@Service
public class ServiceA {
    @Autowired
    private ServiceB serviceB;
}

@Service
public class ServiceB {
    @Autowired
    private ServiceA serviceA;
}
```

**检查方式：**
```
工具：jdepend / mvn dependency:analyze
规则：core → integration → dal（单向依赖）
```

### LINT-004：Controller 层不能直接注入 DAO

```java
// ❌ 错误
@RestController
public class PriceController {
    @Autowired
    private PriceDAO priceDAO;  // 跨层调用！
}

// ✅ 正确
@RestController
public class PriceController {
    @Autowired
    private PriceService priceService;
}
```

**检查方式：**
```
正则：在 *Controller.java 中搜索 @Autowired + *DAO/*Repository
```

### LINT-005：所有外部调用必须有超时和降级

```java
// ❌ 错误
HSFResponse resp = hsfClient.invoke(method, args); // 无超时

// ✅ 正确
HSFResponse resp = hsfClient.invoke(method, args, 
    new InvokeOption().timeout(3000).fallback(this::defaultPrice));
```

**检查方式：**
```
AST 扫描：检测 hsfClient.invoke / restTemplate.exchange 的方法链
验证：调用链中必须有 timeout() 或 fallback() 调用
```

## 集成方式

### 方案 1：Maven Checkstyle Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.1</version>
    <configuration>
        <configLocation>.harness/linters/checkstyle.xml</configLocation>
    </configuration>
</plugin>
```

### 方案 2：PMD 自定义规则

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.21.2</version>
    <configuration>
        <rulesets>
            <ruleset>.harness/linters/pmd-rules.xml</ruleset>
        </rulesets>
    </configuration>
</plugin>
```

### 方案 3：自定义 Shell 脚本验证

```bash
#!/bin/bash
# .harness/scripts/lint-check.sh
# 在 CI 中作为独立步骤运行

errors=0

# LINT-001: 金额字段 double 检查
grep -rn "double\s\+price" --include="*.java" . && {
  echo "❌ LINT-001: 金额字段不能使用 double"
  errors=$((errors + 1))
}

# LINT-003: 循环依赖检查
jdepend . && echo "✅ 无循环依赖" || {
  echo "❌ LINT-003: 存在循环依赖"
  errors=$((errors + 1))
}

exit $errors
```

## 规则新增流程

```
1. 发现 Agent 犯了某类重复错误
2. 评估是否可以程序化检查
3. 如果可以：添加到本文件 + 实现检查脚本/配置
4. 如果不可以：添加到 coding-rules.md 作为软约束
5. 更新 AGENTS.md 中的 Linter 索引
```
