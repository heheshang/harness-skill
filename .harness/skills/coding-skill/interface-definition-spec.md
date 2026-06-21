# 接口定义 Spec

> RPC 接口定义规范与 DTO 设计原则。

## 接口定义规范

```java
/**
 * 价格查询服务接口
 *
 * @author system
 * @since 2024-01-01
 */
public interface PriceQueryService {

    /**
     * 根据商品 ID 查询价格
     *
     * @param query 查询参数
     * @return 价格信息
     * @throws BizException 当商品不存在时
     */
    Result<PriceDTO> queryPrice(PriceQueryRequest query);

    /**
     * 批量查询价格
     *
     * @param batchQuery 批量查询参数
     * @return 价格信息列表
     */
    Result<List<PriceDTO>> batchQueryPrice(PriceBatchQueryRequest batchQuery);
}
```

## 规范

### 1. 接口设计原则
- 接口必须定义在独立的 API 模块中
- 接口必须指定 version 和 timeout
- 返回值统一使用 Result<T> 包裹
- 接口方法必须有完整的 JavaDoc

### 2. DTO 设计原则
- DTO 必须实现 Serializable
- DTO 字段命名使用驼峰命名法
- DTO 必须有无参构造函数
- DTO 字段必须有明确的注释说明含义和约束
- 金额字段必须注明单位（分/元）

### 3. Request/Response 分离
- 查询请求使用 XxxRequest
- 批量查询使用 XxxBatchRequest
- 响应使用 XxxDTO 或 XxxResponse
- 禁止使用 Map 作为接口参数或返回值

### 4. 错误码定义
```java
public enum PriceErrorCode {
    ITEM_NOT_FOUND(1001, "商品不存在"),
    PRICE_CALCULATE_FAILED(1002, "价格计算失败"),
    RULE_NOT_FOUND(1003, "规则不存在"),
    INVALID_PARAM(1004, "参数不合法");

    private final int code;
    private final String message;
}
```

### 5. 版本管理
- 接口版本号在路径或注解中体现
- 新版本接口必须向后兼容
- 废弃接口标记 @Deprecated 并注明替代方案

### 6. 禁止的操作
- 禁止在接口定义中包含实现逻辑
- 禁止使用泛型通配符（如 List<?>）
- 禁止接口方法超过 4 个参数（超出用 Request 对象封装）
