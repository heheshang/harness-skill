# Domain 层编码 Spec

> 领域模型与业务逻辑封装规范。

## 领域模型设计

```java
public class PriceRule {
    /** 规则 ID */
    private Long id;
    /** 商品 ID */
    private Long itemId;
    /** 价格（单位：分） */
    private Long price;
    /** 生效开始时间 */
    private Date effectiveStart;
    /** 生效结束时间 */
    private Date effectiveEnd;
    /** 状态：0-禁用 1-启用 */
    private Integer status;

    // 领域方法
    public boolean isEffective(Date date) {
        return date.after(effectiveStart) && date.before(effectiveEnd);
    }

    public boolean isActive() {
        return status == 1 && isEffective(new Date());
    }
}
```

## 规范

### 1. 领域对象定位
- Domain 对象包含业务行为和业务规则
- Domain 对象是"富模型"：数据 + 行为
- 与 DO（Data Object）区分：DO 是数据载体，Domain 是业务实体

### 2. 封装原则
- 属性私有，通过 getter 访问
- 业务方法对外暴露，内部状态不对外泄露
- 集合属性返回不可变副本（`Collections.unmodifiableList()`）

### 3. 业务规则编码方法
- 业务规则编码为领域方法（如 `isEffective()`, `canApply()`）
- 规则方法命名清晰表达业务含义
- 复杂规则拆分为多个小方法

### 4. 禁止的行为
- Domain 不依赖 Spring 容器
- Domain 不依赖 DAO / RPC / 缓存等基础设施
- Domain 不做 IO 操作
