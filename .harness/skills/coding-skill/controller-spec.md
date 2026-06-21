# Controller 层编码 Spec

> RPC Provider / REST Controller 实现规范。

## 代码结构

```
@RestController
@RequestMapping("/api/v1/{resource}")
public class XxxController {

    @Resource
    private XxxService xxxService;

    @PostMapping("/action")
    public Result<XxxVO> doAction(@Valid @RequestBody XxxRequest request) {
        // 1. 参数已在 DTO 通过 @Valid 校验
        // 2. 调用 Service 层
        XxxDTO result = xxxService.doAction(request.toDTO());
        // 3. 返回统一 Result
        return Result.success(XxxVO.fromDTO(result));
    }
}
```

## 规范

### 1. 接口定义
- 统一使用 `@RestController` + `@RequestMapping`
- 版本号在路径中体现：`/api/v1/`
- 使用 RESTful 风格命名
- 统一返回 `Result<T>` 类型

### 2. 参数校验
- 使用 `@Valid` 注解在请求体参数上
- 校验注解放在请求 DTO 的字段上
- 分组校验用于区分新增/修改场景
- 自定义校验器实现 ConstraintValidator

### 3. 异常处理
- 使用全局 `@ControllerAdvice` 统一处理异常
- Controller 层不捕获异常，交给全局处理器
- 业务异常由 Service 层抛出

### 4. 禁止的操作
- Controller 层不写业务逻辑
- Controller 层不直接操作 DAO
- Controller 层不调用多个 Service 做编排

### 5. 日志规范
- 请求入口打印 INFO 级别日志（含关键参数）
- 响应打印 INFO 级别日志（含耗时和结果）
- 禁止打印请求体/响应体中的敏感信息
