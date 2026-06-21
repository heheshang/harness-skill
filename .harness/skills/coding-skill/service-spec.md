# Service 层编码 Spec

> 业务服务接口及实现规范。

## 接口定义规范

```java
public interface PriceService {
    /**
     * 根据商品 ID 获取价格
     *
     * @param itemId 商品 ID
     * @return 价格（单位：分），不存在时返回 null
     */
    Long getPrice(Long itemId);

    /**
     * 批量查询价格
     *
     * @param itemIds 商品 ID 列表，最大 100
     * @return Map<itemId, price>
     */
    Map<Long, Long> batchGetPrice(List<Long> itemIds);
}
```

## 规范

### 1. 接口设计
- Service 接口放在独立的 interface 中
- 方法必须有 JavaDoc：说明功能、参数、返回值、异常
- 方法签名中明确 null 的可能性（Nullable / NonNull）
- 集合操作明确大小上限

### 2. 实现规范
- 实现类命名 `XxxServiceImpl`
- 使用 `@Service` 注解标记
- 事务标注 `@Transactional` 在需要事务的方法上

### 3. 业务逻辑封装
- 核心业务逻辑封装在 Domain 层，Service 层做编排
- Service 层不做纯数据转换工作（委托给 Domain/Adapter）
- 复杂多步骤流程使用 LiteFlow 编排

### 4. 异常规范
- 业务异常抛出 `BizException` 或子类
- 异常必须包含错误码（枚举类型）
- 异常消息必须包含业务上下文 ID

### 5. 性能约束
- 批量接口必须有大小限制（默认上限 100）
- 避免在循环中调用 RPC/DB
- 热点数据考虑缓存优化
