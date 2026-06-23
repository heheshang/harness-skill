# Harness Skill — AI Agent 编码流程编排框架

> 为 AI Coding Agent 提供结构化开发流程、质量门禁体系和持续改进机制的元框架。
>
> **核心原则**：每发现一个 Agent 的错误，就工程化地消除它再次发生的可能性。

## 快速导航

| 你要做什么 | 看哪里 |
|-----------|--------|
| 理解 Harness 是什么 | [`.harness/README.md`](.harness/README.md) |
| 首次初始化环境 | [`.harness/init.md`](.harness/init.md) |
| 查看编排流程 | [`.harness/agents/owner-agent.md`](.harness/agents/owner-agent.md) |
| 查看平台适配 | [`.harness/platform.md`](.harness/platform.md) |
| **Dry Run 验证框架** | **见下方 [Dry Run 章节](#dry-run用虚拟需求验证-harness-体系)** |
| **通过框架实现一个需求** | **见下方 [6 阶段流程](#实现一个需求的完整流程)** |

---

## 前置条件

Harness 框架设计用于**已有业务代码的项目**。你需要：

1. 一个待开发的真实项目（Java / Python / TypeScript / Go / Rust 均可）
2. 将 `.harness/` 目录和 `AGENTS.md` 复制到项目根目录
3. AI Coding Agent（OpenCode / Claude Code / Codex CLI 均可）

---

## 初始化 Harness 环境

```bash
# 1. 在你的真实项目根目录下，复制 Harness 框架
cp -r /path/to/harness-skill/.harness ./
cp /path/to/harness-skill/AGENTS.md ./

# 2. 检测构建工具
source .harness/scripts/detect-build.sh
# 输出示例: 🔧 Build tool: cargo

# 3. 运行会话启动脚本
bash .harness/scripts/init.sh
```

---

## Dry Run：用虚拟需求验证 Harness 体系

> 在拿真实需求使用 Harness 之前，**必须**用一个虚拟需求完整走一遍全流程。
> 不要期望第一版 Harness 就是完美的——用低成本的方式快速验证、快速修复。

### 为什么需要 Dry Run

- 验证 Harness 框架文件是否完整，所有脚本能否正常运行
- 验证构建工具检测、平台检测是否在**你的项目**上正确工作
- 验证质量门禁（QG-1~8）能否正确拦截不符合条件的产出
- 验证 Agent 能否按 6 阶段流程正确推进
- 在无风险的环境下修复流程问题，而非在真实需求中踩坑

### Dry Run 步骤 1：环境检查

```bash
# 检查必要工具
which git 2>/dev/null || echo "WARN: git missing"

# 自动检测构建工具
source .harness/scripts/detect-build.sh

# 检查项目是否可编译
${BUILD_CHECK_CMD} 2>/dev/null && echo "✅ ${BUILD_TOOL} compile OK" || echo "❌ ${BUILD_TOOL} compile failed"

# 检查项目是否有测试
${TEST_CMD} -q 2>/dev/null && echo "✅ ${BUILD_TOOL} tests OK" || echo "ℹ️  No tests yet"

# 检查 git 仓库
git status --short 2>/dev/null && echo "✅ Git OK" || echo "⚠️  Not a git repo"
```

### Dry Run 步骤 2：验证 Harness 文件完整性

```bash
# 检查 31 个关键文件是否全部存在
required_files=(
  ".harness/README.md"
  ".harness/platform.md"
  ".harness/agents/initializer-agent.md"
  ".harness/agents/owner-agent.md"
  ".harness/agents/planner-agent.md"
  ".harness/agents/generator-agent.md"
  ".harness/agents/evaluator-agent.md"
  ".harness/rules/arch-rules-rust.md"
  ".harness/rules/coding-rules-rust.md"
  ".harness/rules/linter-examples-rust.md"
  ".harness/rules/quality-gates.md"
  ".harness/rules/workflow-rules.md"
  ".harness/rules/entropy-gc.md"
  ".harness/skills/request-analysis/skill.md"
  ".harness/skills/coding-skill/skill.md"
  ".harness/skills/expert-reviewer/skill.md"
  ".harness/skills/unit-test-write/skill.md"
  ".harness/skills/unit-test-ci/skill.md"
  ".harness/skills/deploy-verify/skill.md"
  ".harness/changes/template/spec.md"
  ".harness/changes/template/tasks.md"
  ".harness/changes/template/summary.md"
  ".harness/changes/template/progress.md"
  ".harness/changes/template/feature_list.json"
  ".harness/changes/template/contract.md"
  ".harness/changes/template/design.md"
  ".harness/changes/template/review.md"
  ".harness/changes/template/test-plan.md"
  ".harness/changes/template/deploy-log.md"
  ".harness/scripts/init.sh"
  ".harness/scripts/verify-qg.sh"
)

all_ok=true
for f in "${required_files[@]}"; do
  if [ -f "$f" ]; then
    echo "✅ $f"
  else
    echo "❌ MISSING: $f"
    all_ok=false
  fi
done

if $all_ok; then
  echo "✅ All required files present"
else
  echo "❌ Missing files detected. Fix before proceeding."
fi
```

### Dry Run 步骤 3：创建虚拟需求的变更目录

```bash
# 创建 Dry Run 变更目录
DRY_RUN_DIR=".harness/changes/dry-run-initial-setup-$(date +%Y%m%d)"
cp -r .harness/changes/template "$DRY_RUN_DIR"

# 填写虚拟 spec.md
cat > "$DRY_RUN_DIR/spec.md" << 'EOF'
# 需求规格说明书（Dry Run）

## 背景
这是一个虚拟需求，用于验证 Harness 体系的完整性。

## 需求描述
在 PriceService 中新增一个 echo 方法，返回传入的参数。

## 变更范围
- 涉及模块：crates/services
- 涉及代码层：Service
- 不涉及模块：app, handler, repository, client

## 影响分析
| 影响维度 | 分析 | 风险等级 |
|----------|------|----------|
| 上下游兼容性 | 无影响（新增方法） | 低 |
| 数据兼容性 | 无影响 | 低 |
| 配置变更 | 无 | 低 |
| 国际化 | 无影响 | 低 |
| 性能 | 无影响 | 低 |

## 验收标准
### 正常场景
1. Given 调用 echo 方法，When 传入"hello"，Then 返回"hello"
2. Given 调用 echo 方法，When 传入 42，Then 返回 42

### 异常场景
1. Given 调用 echo 方法，When 传入 null，Then 返回 null

### 边界条件
1. 传入空字符串: 返回空字符串

## 备注
这是一个 Dry Run 虚拟需求，无实际业务影响。
EOF

echo "✅ Dry Run 变更目录已创建: $DRY_RUN_DIR"
```

### Dry Run 步骤 4：按 6 阶段流程推进

```yaml
阶段 1 — 需求分析:
  操作:
    1. 读取 spec.md（已在步骤 3 中创建）
    2. 运行 QG-1 验证:
       bash .harness/scripts/verify-qg.sh 1 .harness/changes/dry-run-initial-setup-$(date +%Y%m%d)
  预期:
    - spec.md 所有必填章节完整
    - 无歧义词汇（"可能""大概"等）
    - QG-1 结果为 PASS
  确认: 检查 spec.md 内容 → 确认进入阶段 2

阶段 2 — 需求评审:
  操作:
    1. 让 Evaluator Agent 读取 spec.md
    2. Agent 检查: 需求完整性、范围明确性、影响分析、验收标准可验证性
    3. 生成 .harness/changes/dry-run-*/review-v1.md
    4. 运行 QG-2 验证:
       bash .harness/scripts/verify-qg.sh 2 .harness/changes/dry-run-initial-setup-$(date +%Y%m%d)
  预期:
    - 评审报告正确生成（文件存在）
    - 无 MUST FIX 级别问题
    - 分级评分各维度 ≥ 阈值
    - QG-2 结果为 PASS
  确认: 评审通过 → 确认进入编码

阶段 3 — 编码实现:
  操作:
    1. 让 Generator Agent 按 spec 实现 echo 方法
    2. 在 PriceService trait 中新增方法签名
    3. 在 DefaultPriceService 中实现方法体
    4. 编译检查:
       bash .harness/scripts/verify-qg.sh 3 .harness/changes/dry-run-initial-setup-$(date +%Y%m%d)
  预期:
    - 代码编译通过（零错误零警告）
    - QG-3 结果为 PASS
  自动进入: 编译通过后自动进入阶段 4

阶段 4 — 编码评审:
  操作:
    1. 让 Evaluator Agent 审查代码
    2. 检查维度: 业务正确性、功能完整性、代码质量、安全性
    3. 生成 .harness/changes/dry-run-*/review-v2.md
  预期:
    - 4 维度评分均 ≥ 阈值
    - 无 MUST FIX 级别问题
    - 没有做需求范围外的修改
  确认: 评审通过 → 确认进入测试

阶段 5 — 单元测试编写:
  操作:
    1. 让 Generator Agent 编写测试
    2. 测试正常输入、null 输入、空字符串输入
    3. 本地运行测试:
       bash .harness/scripts/verify-qg.sh 4 .harness/changes/dry-run-initial-setup-$(date +%Y%m%d)
  预期:
    - 新增测试用例数 > 0
    - 全部测试通过（passed == total）
    - QG-4 结果为 PASS
  进入: 全部通过后进入阶段 6

阶段 6 — CI 验证:
  操作:
    1. 提交代码
    2. 推送到远程触发 CI
    3. 等待 CI pipeline 完成
  预期:
    - CI status == SUCCESS
    - total_tests > 0
    - passed == total_tests
    - QG-5 所有条件满足
  记录: 结果写入 summary.md
```

### Dry Run 检查清单

完成 Dry Run 后，逐项检查：

- [ ] 所有 6 个阶段都能正常推进，无卡住或跳过
- [ ] 质量门禁正确拦截了不符合条件的产出
- [ ] 评审报告（review-v1.md, review-v2.md）正确生成
- [ ] summary.md 正确记录了每个阶段的状态（无重复行）
- [ ] CI 门禁正确检查了测试覆盖（total_tests > 0，而非仅检查状态码）
- [ ] 评审报告在简单需求下也正确生成文件（不因"太简单"而跳过）
- [ ] 编译环境与 CI 环境一致
- [ ] 没有因 Agent "追加"倾向导致 summary.md 出现重复行
- [ ] 部署参数没有被 Agent 错误推测

### Dry Run 常见问题与修复

| 问题 | 表现 | 修复方式 |
|------|------|----------|
| CI 门禁只检查状态码 | status=SUCCESS 但测试数为 0 | 在 QG-5 中增加 `total_tests > 0` 条件 |
| 评审报告不生成 | 简单需求下 Agent 跳过生成 | 在 QG-2 中增加"文件必须存在"检查 |
| summary.md 重复行 | Agent 追加时产生重复 | 在 owner-agent.md 中明确"覆盖而非追加" |
| 部署参数推测错误 | Agent 自行填写了错误的参数 | 阶段 8 增加人工确认点 |
| 构建工具未检测到 | detect-build.sh 返回 unknown | 在 detect-build.sh 中添加对应构建工具规则 |

### Dry Run 完成

Dry Run 全部通过后，Harness 体系即可投入真实需求使用。

> 以后每发现一个新错误，就更新 Rules/Skills 来防止它再次发生。

---

## 实现一个需求的完整流程

### 第零步：创建变更目录

每个需求一个独立目录，命名格式：`{type}-{name}-{date}`

```bash
cp -r .harness/changes/template .harness/changes/feat-add-cache-20260622
```

变更类型：
| 前缀 | 含义 |
|------|------|
| `feat` | 新功能 |
| `fix` | 缺陷修复 |
| `refactor` | 重构 |
| `docs` | 文档 |
| `chore` | 基础建设 |

---

### 阶段 1：需求分析

**做什么**：把需求写成结构化文档

1. 编辑 `spec.md`，填写以下章节：

```markdown
# 需求规格说明书

## 背景
{为什么要做这个需求，业务动机是什么}

## 需求描述
{清晰描述核心内容，不含"怎么实现"}

## 变更范围
### 涉及的模块
- {模块名}：{变更简述}
### 涉及的代码层
- [ ] Handler / axum 路由
- [ ] Service
- [ ] Domain
- [ ] Repository / sqlx
- [ ] Client / RPC
### 不涉及的模块
- {模块名}：{原因}

## 影响分析
| 影响维度 | 分析 | 风险等级 |
|----------|------|----------|
| 上下游兼容性 | {分析} | {高/中/低} |
| 数据兼容性 | {分析} | {高/中/低} |
| 配置变更 | {分析} | {高/中/低} |
| 国际化 | {分析} | {高/中/低} |
| 性能 | {分析} | {高/中/低} |

## 验收标准
### 正常场景
1. Given {前置条件}, When {操作}, Then {期望结果}
### 异常场景
1. Given {异常条件}, When {操作}, Then {期望错误处理}
### 边界条件
1. {边界情况}: {期望行为}

## 备注
{其他说明}
```

2. 运行质量门禁验证 spec 完整性：

```bash
bash .harness/scripts/verify-qg.sh 1 .harness/changes/feat-add-cache-20260622
```

3. **人工确认**：检查 spec.md 是否准确反映了需求，确认后进入阶段 2。

---

### 阶段 2：需求评审

**做什么**：让 Evaluator Agent 审查 spec.md 的合理性和完整性

Agent 检查维度：
- 需求完整性：是否覆盖所有业务场景
- 变更范围明确性：涉及的模块和代码层是否标记
- 影响分析全面性：兼容性/数据/配置/国际化/性能
- 验收标准可验证性：是否可测量
- 歧义检查：不含"可能""大概""也许"等不确定词汇

Agent 生成评审报告 `review-v1.md`，包含：
- MUST FIX（必须修改）：阻塞项，修复后才能通过
- LOW（建议修改）：可后续处理
- INFO（仅供参考）：观察意见
- 0-10 分级评分（每个维度有独立阈值）

**处理评审结果**：
- 无 MUST FIX → 评审通过，进入阶段 3
- 有 MUST FIX → 修改 spec.md → 重新评审（最多 3 轮）
- 超 3 轮未通过 → 升级到人工决策

**人工确认**：评审通过后，确认进入编码阶段。

---

### 阶段 3：编码实现

**做什么**：按 spec.md 编写代码

**编码前置检查**：
- [ ] 已理解 spec.md 中的全部需求
- [ ] 已阅读 tasks.md 了解任务分解
- [ ] 已读取需修改的现有代码
- [ ] 已确认不违反架构规则（`arch-rules-rust.md`）

**按分层规范编码**（以 Rust 为例）：

```
Handler → Service → Domain → Repository → Client
```

每层的具体规范参见 `.harness/skills/coding-skill/` 下的分层 Spec。

**编码规则**（自动加载 `coding-rules-rust.md`）：
- 金额字段用 `Money(i64)`（分），禁止 `f64`
- 函数体 ≤ 80 行，参数 ≤ 4 个
- 所有外部调用必须有超时和降级（通过 Client trait）
- 关键路径必须有 `#[tracing::instrument]` 和结构化日志
- 禁止 `unwrap()` / `expect()` — 使用 `?` 传播

**编译检查**：每完成一个子任务立即编译：

```bash
bash .harness/scripts/verify-qg.sh 3 .harness/changes/feat-add-cache-20260622
```

编译通过 → 自动进入阶段 4。编译失败 → 修复后重新编译。

---

### 阶段 4：编码评审

**做什么**：让 Evaluator Agent 审查代码实现

Agent 检查维度 + 评分阈值：

| 维度 | 最低分 | 检查内容 |
|------|--------|----------|
| 业务正确性 | ≥ 7 | 代码逻辑是否满足 spec.md 需求 |
| 功能完整性 | ≥ 7 | 所有变更范围是否覆盖 |
| 代码质量 | ≥ 6 | 风格、异常处理、边界条件 |
| 安全性 | ≥ 7 | SQL 注入、敏感信息、权限 |

Agent 生成 `review-v2.md`，包含 MUST FIX / LOW / INFO + 评分表。

**处理评审结果**：
- 所有维度 ≥ 阈值且 MUST FIX = 0 → 通过，进入阶段 5
- 有问题 → 回退到阶段 3 修复（最多 2 轮）
- 超 2 轮 → 升级到人工决策

**人工确认**：评审通过后，确认进入测试阶段。

---

### 阶段 5：单元测试编写

**做什么**：给改动的代码写测试

**核心原则**：**改动驱动测试** —— 改了哪个接口就测哪个接口。

**数据来源优先级**：
1. 线上真实请求出入参（通过 MCP 工具查询）
2. 线下测试环境请求记录
3. 手动构造业务数据

**测试结构**（Arrange-Act-Assert）：

```rust
#[tokio::test]
async fn test_get_price_item_exists_return_price() {
    // Arrange（准备数据）
    let item_id = ItemId(12345);
    let mut repo = MockPriceRepository::new();
    repo.expect_find_by_item_id()
        .with(eq(item_id))
        .return_once(|_| Ok(Some(build_mock_price(item_id, 9990))));

    let service = DefaultPriceService::new(Arc::new(repo));

    // Act（执行操作）
    let result = service.get_price(item_id).await.unwrap();

    // Assert（验证结果）
    assert_eq!(result, Price(Money(9990)));
}
```

**质量要求**：
- 新增代码行覆盖率 ≥ 80%
- 核心逻辑分支覆盖率 100%
- 禁止测试依赖外部环境（DB/缓存/RPC 需 Mock）
- 禁止测试之间有顺序依赖

**本地验证**：

```bash
bash .harness/scripts/verify-qg.sh 4 .harness/changes/feat-add-cache-20260622
```

全部通过 → 进入阶段 6。

---

### 阶段 6：CI 验证

**做什么**：提交代码，跑 CI Pipeline

```bash
# 提交代码
git add {变更文件}
git commit -m "feat: add cache layer for price query

- PriceService: 新增 get_price_with_cache 方法
- DefaultPriceService: 实现二级缓存逻辑
- price_service_test: 新增 12 个测试用例"

# 推送到远程触发 CI
git push
```

**CI 自动执行**（GitHub Actions）：
1. Job 1: Init — 构建工具检测
2. Job 2: Quality Gates — QG-1~4 自动化验证
3. Job 3: Lint — ShellCheck + 代码风格
4. Job 4: Docs — 关键文件完整性 + Markdown 链接

**CI 通过条件**：
```
status == SUCCESS
total_tests > 0
passed == total_tests
代码覆盖率 >= 80%（新增代码）
```

**CI 失败处理**：

| 失败类型 | 处理方式 | 回退到 |
|----------|----------|--------|
| 编译错误 | 修复编译错误 | 阶段 3 |
| 测试失败 | 修复测试或代码 | 阶段 5 |
| 测试为 0/0 | 检查测试是否正确执行 | 阶段 5 |

CI 通过后 → 创建 PR → Agent 自审 → Review → Merge。

---

### 交付后：更新 summary.md

每个阶段完成后更新 `summary.md`（整个变更的**单一真相源**）：

```markdown
# 变更摘要

## 基本信息
- **需求名称**：add cache layer for price query
- **变更类型**：feat
- **日期**：20260622

## 阶段执行状态
| 阶段 | 状态 | 轮次 | 备注 |
|------|------|------|------|
| 需求分析 | ✅ | - | spec.md v1 |
| 需求评审 | ✅ | 1 | 一轮通过，无 MUST FIX |
| 编码实现 | ✅ | - | 变更 3 个文件 |
| 编码评审 | ✅ | 1 | 4 维度均 ≥ 阈值 |
| 单元测试 | ✅ | - | 12 个用例全部通过 |
| CI 验证 | ✅ | - | build #42 SUCCESS |

## 变更文件清单
| 文件路径 | 变更类型 | 说明 |
|----------|----------|------|
| crates/services/src/price_service.rs | 修改 | 新增 get_price_with_cache 方法 |
| crates/services/src/default_price_service.rs | 修改 | 实现二级缓存逻辑 |
| crates/services/tests/price_service_test.rs | 新增 | 12 个测试用例 |

## CI 信息
- 测试用例数：12 / 通过：12 / 失败：0
- 代码覆盖率：87%
- 构建结果：SUCCESS
```

---

## 角色分工总览

| 你要做的（人工） | Agent 要做的 |
|-----------------|-------------|
| 描述需求 | 生成 spec.md |
| 确认 spec.md 内容 | 评审 spec.md（Evaluator Agent） |
| 确认进入编码 | 写代码 + 编译（Generator Agent） |
| 确认代码质量 | 评审代码（Evaluator Agent） |
| 确认进入测试 | 写测试 + 本地运行（Generator Agent） |
| 确认交付 | 提交 + CI + PR |

**你不能做的**：直接写代码。所有源码由 Agent 生成，人只审查和决策。
**Agent 不能做的**：跳过阶段、跳过人工确认点、忽略质量门禁。

---

## 5 个强制人工确认点

| 编号 | 确认点 | 位置 |
|------|--------|------|
| H1 | 需求规格确认 | 阶段 1 → 阶段 2 |
| H2 | 需求评审通过 | 阶段 2 → 阶段 3 |
| H3 | 编码评审通过 | 阶段 4 → 阶段 5 |
| H4 | 部署参数确认 | 阶段 7 → 阶段 8（扩展） |
| H5 | 交付确认 | 阶段 10（扩展） |

---

## 核心原则速查

1. **阶段不可跳过**：无论需求多简单，必须走完整流程
2. **质量门禁不可绕过**：每个阶段有可程序化验证的条件
3. **评审轮次有上限**：需求评审 ≤ 3 轮，编码/测试评审 ≤ 2 轮
4. **Agent 分离**：Planner 不写代码，Generator 不评审，Evaluator 不修改代码
5. **文档即产物**：spec.md → review.md → summary.md 层层递进，全程可追溯

---

## 目录结构

```
.harness/
├── README.md              # 体系总览 + 上下文分层策略
├── init.md                # 首次引导初始化
├── platform.md            # 跨平台适配
├── agents/                # 5 个 Agent 角色定义
│   ├── owner-agent.md     # 编排中枢（6 阶段流程）
│   ├── planner-agent.md   # 规划 Agent
│   ├── generator-agent.md # 执行 Agent
│   ├── evaluator-agent.md # 评判 Agent
│   └── initializer-agent.md # 初始化 Agent
├── rules/                 # 规则体系
│   ├── arch-rules-rust.md  # 架构规则
│   ├── coding-rules-rust.md # 编码规范
│   ├── quality-gates.md   # 8 个质量门禁
│   ├── workflow-rules.md  # 流程规则
│   └── entropy-gc.md      # 熵清理
├── skills/                # 技能体系
│   ├── request-analysis/  # 需求分析 SOP
│   ├── coding-skill/      # 编码实现（含 7 份 Rust 分层 Spec）
│   ├── expert-reviewer/   # 评审方法
│   ├── unit-test-write/   # 测试编写
│   ├── unit-test-ci/      # CI 验证
│   └── deploy-verify/     # 部署验证
├── scripts/               # 可执行脚本
│   ├── init.sh            # 会话启动
│   ├── detect-build.sh    # 构建工具检测
│   ├── detect-platform.sh # 平台检测
│   ├── verify-qg.sh       # 质量门禁（Shell）
│   ├── verify-qg.py       # 质量门禁（Python）
│   └── gc-scan.sh         # 熵清理扫描
├── wiki/                  # 知识库
├── changes/template/      # 变更模板
└── mcp/                   # MCP 工具配置
```

---

## 参考资料

- [Anthropic: Effective harnesses for long-running agents](https://www.anthropic.com/engineering/effective-harnesses-for-long-running-agents)
- [Anthropic: Harness design for long-running application development](https://www.anthropic.com/engineering/harness-design-long-running-apps)
- [OpenAI: Harness engineering — leveraging Codex in an agent-first world](https://openai.com/index/harness-engineering/)
