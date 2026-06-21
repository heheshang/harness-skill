# 数据模型

> 核心表结构、ER 关系、字段说明。
>
> ⚠️ **示例数据**：此 Wiki 中的表结构（price_rule 等）是示例占位数据。
> 实际项目中应替换为真实数据模型。

## ER 关系（概览）

```
price_rule (价格规则)
    ↑ item_id
    │
price_config (价格配置) ──→ price_rule_detail (规则明细)
    ↑                        ↑
    │                        │
item (商品) ──────────────→ price_history (价格变更历史)
```

## 核心表结构

### price_rule（价格规则表）

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | PK, AUTO_INCREMENT |
| item_id | BIGINT | 商品 ID | NOT NULL, INDEX |
| price | BIGINT | 价格（单位：分） | NOT NULL |
| rule_type | TINYINT | 规则类型：1-固定价 2-区间价 3-折扣 | NOT NULL |
| effective_start | DATETIME | 生效开始时间 | NOT NULL |
| effective_end | DATETIME | 生效结束时间 | NOT NULL |
| priority | INT | 优先级（数字越小优先级越高） | DEFAULT 100 |
| status | TINYINT | 状态：0-禁用 1-启用 | NOT NULL, DEFAULT 1 |
| ext_info | TEXT | 扩展信息（JSON） | NULLABLE |
| gmt_create | DATETIME | 创建时间 | NOT NULL |
| gmt_modified | DATETIME | 修改时间 | NOT NULL |
| creator_id | VARCHAR(32) | 创建人 | NOT NULL |
| modifier_id | VARCHAR(32) | 修改人 | NOT NULL |

**索引说明**：
- PRIMARY KEY (`id`)
- KEY `idx_item_id` (`item_id`)
- KEY `idx_effective_time` (`effective_start`, `effective_end`)

### price_config（价格配置表）

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | PK, AUTO_INCREMENT |
| config_key | VARCHAR(64) | 配置 Key | UNIQUE, NOT NULL |
| config_value | VARCHAR(1024) | 配置 Value | NOT NULL |
| config_desc | VARCHAR(256) | 配置说明 | NULLABLE |
| status | TINYINT | 状态：0-禁用 1-启用 | NOT NULL, DEFAULT 1 |
| gmt_create | DATETIME | 创建时间 | NOT NULL |
| gmt_modified | DATETIME | 修改时间 | NOT NULL |

### price_history（价格变更历史表）

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | PK, AUTO_INCREMENT |
| item_id | BIGINT | 商品 ID | NOT NULL, INDEX |
| old_price | BIGINT | 旧价格（分） | NOT NULL |
| new_price | BIGINT | 新价格（分） | NOT NULL |
| change_type | TINYINT | 变更类型：1-人工 2-规则 3-系统 | NOT NULL |
| operator | VARCHAR(32) | 操作人 | NOT NULL |
| change_reason | VARCHAR(256) | 变更原因 | NULLABLE |
| gmt_create | DATETIME | 创建时间 | NOT NULL |

## 字段规范

### 通用字段
| 字段 | 规范 |
|------|------|
| 状态字段 | TINYINT，使用数字枚举 |
| 金额字段 | BIGINT，单位分 |
| 时间字段 | DATETIME，精确到秒 |
| 创建/修改时间 | 必须包含 `gmt_create` 和 `gmt_modified` |
| 操作人 | 必须包含 `creator_id` 和 `modifier_id` |

### 命名规范
- 表名：小写蛇形，业务前缀 + 业务含义
- 字段名：小写蛇形，清晰表达含义
- 布尔字段：使用 `is_` / `has_` 前缀
- 外键字段：使用被引用表的主键字段名（如 `item_id`）

## 数据字典

### 状态枚举

| 字段路径 | 值 | 含义 |
|----------|-----|------|
| price_rule.status | 0 | 禁用 |
| price_rule.status | 1 | 启用 |
| price_rule.rule_type | 1 | 固定价 |
| price_rule.rule_type | 2 | 区间价 |
| price_rule.rule_type | 3 | 折扣 |

### 变更类型枚举

| 值 | 含义 | 说明 |
|-----|------|------|
| 1 | 人工变更 | 运营人工操作导致 |
| 2 | 规则变更 | 价格规则触发自动变更 |
| 3 | 系统变更 | 系统定时任务或补偿触发 |
