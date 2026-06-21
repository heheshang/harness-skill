# Adapter 层编码 Spec

> 外部服务依赖调用规范：RPC 调用、缓存操作、消息发送。

## RPC 调用规范

```java
@Component
public class ExternalPriceAdapter {

    @Resource
    private ExternalPriceService externalPriceService;

    /**
     * 查询外部价格，带降级
     */
    public Long queryExternalPrice(Long itemId) {
        try {
            // 1. 设置超时
            // 2. 调用外部服务
            Result<Long> result = externalPriceService.query(itemId);
            if (result.isSuccess()) {
                return result.getData();
            }
            // 3. 业务失败降级
            log.warn("外部价格查询返回失败, itemId={}, code={}, msg={}",
                itemId, result.getCode(), result.getMessage());
            return null;
        } catch (Exception e) {
            // 4. 异常降级
            log.error("外部价格查询异常, itemId={}", itemId, e);
            return null;
        }
    }
}
```

## 规范

### 1. 统一封装
- 所有外部调用统一封装在 Adapter 类中
- Adapter 负责：参数组装 → 调用 → 结果解析 → 异常处理
- Service 层不直接调用外部 RPC，必须通过 Adapter

### 2. 超时设置
- 每个 RPC 调用必须显式设置超时时间
- 超时时间根据接口 P99 响应时间设定（1.5× ~ 2× P99）
- 长耗时接口考虑异步调用

### 3. 降级策略
- 必须提供降级实现（返回默认值 / 缓存值 / null）
- 降级逻辑必须有 WARN 级别日志
- 批量调用中单条失败不应影响其他结果

### 4. 重试策略
- 幂等接口可重试（最多 3 次，指数退避）
- 非幂等接口禁止重试
- 重试必须有上限，防止雪崩

### 5. 缓存操作规范
- 缓存 Key 格式：`{业务前缀}:{业务ID}:{字段}`
- 先更新 DB，再删除缓存（Cache-Aside Pattern）
- 批量淘汰使用 pipeline 减少网络开销
- 设置合理的过期时间（默认 1 小时）

### 6. 消息发送规范
- 消息体必须包含业务唯一 ID（用于去重）
- 消息必须有 TTL 和死信处理
- 事务消息用于重要业务操作
