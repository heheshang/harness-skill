# 任务拆解

> 基于 spec.md 拆解的可执行任务清单。

## 任务列表

### 任务 1：新增价格过滤规则数据模型

- **优先级**：P0
- **估计工时**：1h
- **涉及模块**：dal, common
- **涉及文件**：
  - `dal/model/PriceRuleDO.java` — 新增价格规则数据对象
  - `dal/mapper/PriceRuleMapper.java` — 新增规则查询方法
  - `dal/mapper/PriceRuleMapper.xml` — 新增规则查询 SQL
  - `common/constant/PriceRuleConstant.java` — 新增规则相关常量
- **验收标准**：
  - [ ] PriceRuleDO 包含所有必要字段（id, itemId, ruleType, channel, userLevel, region, price, priority, effectiveStart, effectiveEnd, status）
  - [ ] PriceRuleMapper 提供按多维度查询的方法
  - [ ] 单元测试通过
- **依赖任务**：无
- **状态**：待开始

---

### 任务 2：实现价格过滤规则核心逻辑

- **优先级**：P0
- **估计工时**：2h
- **涉及模块**：core/service, core/domain
- **涉及文件**：
  - `core/service/PriceRuleFilterService.java` — 新增过滤服务接口
  - `core/service/impl/PriceRuleFilterServiceImpl.java` — 过滤服务实现
  - `core/domain/PriceRule.java` — 新增价格规则领域模型
- **验收标准**：
  - [ ] 支持按渠道、用户等级、地域多维度过滤
  - [ ] 规则优先级排序正确
  - [ ] 生效时间校验正确
  - [ ] 单元测试覆盖所有场景
- **依赖任务**：任务 1
- **状态**：待开始

---

### 任务 3：集成到价格查询链路

- **优先级**：P0
- **估计工时**：1h
- **涉及模块**：core/service
- **涉及文件**：
  - `core/service/impl/PriceServiceImpl.java` — 在价格查询中集成过滤逻辑
- **验收标准**：
  - [ ] 价格查询时自动应用过滤规则
  - [ ] 现有功能不受影响（回归测试通过）
  - [ ] 缓存逻辑正确（过滤后的价格被缓存）
- **依赖任务**：任务 2
- **状态**：待开始

---

### 任务 4：编写单元测试

- **优先级**：P1
- **估计工时**：1.5h
- **涉及模块**：测试模块
- **涉及文件**：
  - `src/test/java/.../PriceRuleFilterServiceTest.java` — 过滤服务测试
  - `src/test/java/.../PriceServiceImplTest.java` — 价格服务回归测试
- **验收标准**：
  - [ ] 覆盖率 ≥ 80%
  - [ ] 所有测试用例通过
  - [ ] 边界条件覆盖完整
- **依赖任务**：任务 3
- **状态**：待开始

---

## 排期

| 任务 | 优先级 | 估计工时 | 依赖 | 计划开始 | 完成 |
|------|--------|----------|------|----------|------|
| 任务 1 | P0 | 1h | - | D1 上午 | D1 上午 |
| 任务 2 | P0 | 2h | 任务 1 | D1 下午 | D1 下午 |
| 任务 3 | P0 | 1h | 任务 2 | D2 上午 | D2 上午 |
| 任务 4 | P1 | 1.5h | 任务 3 | D2 下午 | D2 下午 |
