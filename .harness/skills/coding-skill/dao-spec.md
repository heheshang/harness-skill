# DAO 层编码 Spec

> 数据访问层规范：建表、Mapper、数据操作。

## 表结构设计规范

```sql
CREATE TABLE `price_rule` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `item_id`       BIGINT       NOT NULL COMMENT '商品 ID',
    `price`         BIGINT       NOT NULL COMMENT '价格（单位：分）',
    `effective_start` DATETIME   NOT NULL COMMENT '生效开始时间',
    `effective_end`   DATETIME   NOT NULL COMMENT '生效结束时间',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `gmt_create`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `creator_id`    VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '创建人',
    `modifier_id`   VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '修改人',
    PRIMARY KEY (`id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_effective_time` (`effective_start`, `effective_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格规则表';
```

## 规范

### 1. 建表规范
- 表名使用业务前缀（`price_`）
- 必须包含 `id`, `gmt_create`, `gmt_modified` 字段
- 字符集统一 `utf8mb4`
- 索引命名：主键 `PRIMARY`，普通索引 `idx_{字段名}`，唯一索引 `uk_{字段名}`
- 金额字段使用 BIGINT（单位：分），禁止 DECIMAL

### 2. Mapper 规范
- Mapper 接口命名 `XxxMapper`
- XML SQL 放在 `mapper/` 目录下，命名 `XxxMapper.xml`
- 禁止在 Mapper 接口上使用 `@Select` / `@Update` 注解——全部写在 XML 中
- 复杂查询必须使用 XML 动态 SQL

### 3. 查询规范
- 单表查询使用 MyBatis Generator 生成的基础方法
- 多表关联查询谨慎使用，优先考虑应用层组装
- 批量操作必须使用 `foreach` + batch 模式
- 分页查询必须指定 `LIMIT`，禁止无限制查询

### 4. 写入规范
- 批量插入使用 `insertBatch` 方法（MyBatis foreach）
- 更新操作必须指定精确的 WHERE 条件
- 逻辑删除（is_deleted），禁止物理删除
- 乐观锁版本号用于并发控制（`version` 字段）

### 5. 禁止的操作
- 禁止在 Mapper XML 中使用 `select *`
- 禁止存储大文本/大字段（超过 1KB）在主表中
- 禁止在循环中逐条执行 SQL
- 禁止跨库 join
