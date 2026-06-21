# Unit Test Write Skill

> 单元测试编写技能。遵循"改动驱动测试"原则：改了哪个接口就测哪个接口。

## 输入
- 变更涉及的服务接口/方法列表
- 线上真实请求出入参（通过 MCP 工具获取，优先）

## 输出
- 新增/修改的单元测试文件

## 核心原则

### 改动驱动测试（Change-driven Testing）
- 改了哪个接口就测哪个接口，而非一刀切测最上层
- 新增方法必须新增对应的测试方法
- 修改方法需要补充新的测试 case 覆盖修改逻辑

### 数据来源优先级
1. 线上真实请求出入参（通过 MCP 工具查询）— 最高优先级
2. 线下测试环境的请求记录
3. 根据业务逻辑手动构造

## 执行步骤

### Step 1：确定测试范围
1. 分析变更影响到的接口和方法
2. 对每个受影响的接口，确定测试场景
3. 记录测试范围到 tasks.md

### Step 2：获取测试数据
1. 优先通过 MCP 工具查询线上真实请求日志
2. 获取请求入参和响应出参
3. 对敏感数据进行脱敏处理
4. 如果没有线上数据，手动构造符合业务逻辑的测试数据

### Step 3：编写测试代码
按照 Arrange-Act-Assert 三段式结构编写：

```java
@Test
public void testGetPrice_ItemExists_ReturnPrice() {
    // Arrange
    Long itemId = 12345L;
    Long expectedPrice = 9990L; // 单位：分
    when(priceDao.queryByItemId(itemId)).thenReturn(buildMockPrice(itemId, expectedPrice));

    // Act
    Long result = priceService.getPrice(itemId);

    // Assert
    assertNotNull(result);
    assertEquals(expectedPrice, result);
}

@Test
public void testGetPrice_ItemNotExists_ReturnNull() {
    // Arrange
    Long itemId = 99999L;
    when(priceDao.queryByItemId(itemId)).thenReturn(null);

    // Act
    Long result = priceService.getPrice(itemId);

    // Assert
    assertNull(result);
}
```

### Step 4：运行测试
1. 本地运行所有测试
2. 确认全部通过（passed == total）
3. 确认新增测试无 flaky

## 测试规范

### 测试框架
- 使用 JUnit 4/5 + Mockito
- 需要 Spring 容器的测试使用 `@SpringBootTest`

### 命名规范
- 类名：`{被测试类名}Test`
- 方法名：`test{MethodName}_{Scenario}_{ExpectedResult}`

### 覆盖率要求
- 新增代码行覆盖率 ≥ 80%
- 新增代码分支覆盖率 ≥ 70%
- 核心逻辑（价格计算、状态判断）需要 100% 分支覆盖

### 禁止的行为
- 禁止测试依赖外部环境（DB、缓存、RPC 需 Mock）
- 禁止测试之间有顺序依赖
- 禁止在测试中 sleep
- 禁止测试抛出未捕获异常
