# 接口文档生成 Spec

> 对外接口的协议文档模板与规范。

## 文档模板

```markdown
# {接口名称} 接口文档

## 接口信息
- **接口路径**：`/api/v1/{resource}/{action}`
- **请求方式**：POST / GET / PUT / DELETE
- **接口描述**：{接口功能描述}
- **认证方式**：{Token / Session / 无}
- **版本**：v1
- **超时时间**：{X}ms

## 请求参数

### Header
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| Authorization | String | 是 | 认证令牌 |
| X-Request-Id | String | 否 | 请求追踪 ID |

### Path 参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| {param} | {type} | {是/否} | {说明} |

### Query 参数
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| {param} | {type} | {是/否} | {default} | {说明} |

### Body 参数（JSON）
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| {param} | {type} | {是/否} | {default} | {说明} |
| {param}.{field} | {type} | {是/否} | - | {说明} |

### 请求示例
```json
{
  "itemId": 12345,
  "priceType": 1
}
```

## 响应参数

### 响应体结构
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "itemId": 12345,
    "price": 9990,
    "priceUnit": "CENT",
    "currency": "CNY"
  }
}
```

### 响应字段说明
| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | int | 响应码，0 表示成功 |
| message | String | 响应消息 |
| data | {Type} | 业务数据 |
| data.{field} | {type} | {说明} |

### 错误码说明
| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 0 | 成功 | - |
| 1001 | 商品不存在 | 检查商品 ID |
| 1002 | 价格计算失败 | 联系管理员 |

## 调用示例

### cURL
```bash
curl -X POST 'https://api.example.com/api/v1/price/query' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer {token}' \
  -d '{"itemId": 12345}'
```

### Java SDK
```java
PriceDTO result = priceQueryService.queryPrice(
    PriceQueryRequest.builder()
        .itemId(12345L)
        .build()
).getData();
```

## 注意事项
1. 接口调用频率限制：{X} 次/分钟
2. 数据缓存策略：{说明}
3. 灰度期间行为差异：{说明}
```

## 生成规则

### 必须生成的接口
- 所有对外暴露的 RPC 接口
- 所有对外暴露的 REST 接口
- 新增的接口必须同步生成文档

### 文档更新触发
- 接口参数变更
- 接口行为变更
- 新增/废弃接口
- 错误码变更

### 文档位置
- 接口文档统一放在 `docs/api/` 目录
- 命名格式：`{service-name}-{version}.md`
- 文档版本与接口版本保持一致
