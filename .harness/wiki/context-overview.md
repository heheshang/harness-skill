# 项目上下文总览

> 项目整体介绍、技术选型、模块职责。Agent 理解项目的第一站。

## 项目简介

**项目名称**：harness-skill（编码流程编排框架）
**项目定位**：为 AI Coding Agent 提供结构化开发流程的元框架。通过 6 阶段流程（需求→评审→编码→评审→测试→交付）和质量门禁体系，确保 AI 编码可重复、可审计、可验证。

## 核心理念

```
"框架不是约束，是 Agent 的脚手架"
```

- **结构化流程**：每个变更走完 6 阶段，不跳步
- **质量门禁**：每个阶段有可执行检查，而非口头规则
- **文档驱动**：spec → design → review → test 层层递进
- **Agent 原生**：所有模板和流程专为 AI Agent 阅读理解设计
- **平台无关**：支持 OpenCode / Claude Code / Codex CLI / Cursor 四种平台

## 系统架构

```
Agent (OC/Claude/Codex)
    │
    ├── .harness/README.md        ← 体系总览
    ├── .harness/platform.md      ← 平台适配层（工具映射 + MCP 配置）
    ├── .harness/init.md          ← 首次初始化
    ├── .harness/agents/
    │   ├── owner-agent.md        ← 编排逻辑（6 阶段流程）
    │   ├── evaluator-agent.md    ← 评审逻辑（各阶段验收）
    │   └── initializer-agent.md  ← 初始化流程
    ├── .harness/scripts/
    │   ├── init.sh               ← 会话启动（平台检测 + 构建检测）
    │   ├── detect-build.sh       ← 构建工具自动检测
    │   ├── detect-platform.sh    ← Agent 平台自动检测
    │   ├── verify-qg.sh          ← 质量门禁（bash）
    │   ├── verify-qg.py          ← 质量门禁（Python）
    │   └── gc-scan.sh            ← 熵清理扫描仪
    ├── .harness/wiki/            ← 业务知识库（本文档）
    ├── .harness/changes/template/ ← 变更模板集
    ├── .harness/rules/           ← 架构/编码/自定义规则
    ├── .harness/mcp/             ← MCP 配置中心
    └── .github/workflows/        ← CI 集成
```

## 6 阶段流程

| 阶段 | 名称 | 输入 | 输出 | 质量门禁 |
|------|------|------|------|----------|
| 1 | 需求分析 | 用户需求 | spec.md | 评审通过 |
| 2 | 需求评审 | spec.md | 批准的 spec | Evaluator 批准 |
| 3 | 编码 | 批准的 spec | 代码 + test | 编译通过 |
| 4 | 评审 | 代码 | 批准的代码 | Reviewer 批准 |
| 5 | 测试 | 代码 | 测试报告 | QG-1~8 全部通过 |
| 6 | 交付 | 测试报告 | 关闭的变更 | Summary 归档 |

## 模块职责

| 模块 | 路径 | 职责 |
|------|------|------|
| 流程编排 | `.harness/agents/owner-agent.md` | 6 阶段主流程编排，Owner Agent 执行规范 |
| 评审逻辑 | `.harness/agents/evaluator-agent.md` | 各阶段评审标准和验收条件 |
| 初始化 | `.harness/agents/initializer-agent.md` | 首次环境初始化指引 |
| 质量门禁 | `.harness/scripts/verify-qg.sh` | QG-1~8 自动化质量检查 |
| 构建检测 | `.harness/scripts/detect-build.sh` | 自动识别 maven/gradle/pip/npm/cargo/go |
| 平台检测 | `.harness/scripts/detect-platform.sh` | 自动识别 OpenCode/Claude/Codex/Cursor/CI |
| 熵清理 | `.harness/scripts/gc-scan.sh` | 注释漂移/死代码/过期引用检测 |
| 变更模板 | `.harness/changes/template/` | 10 个模板覆盖完整变更生命周期 |
| CI 集成 | `.github/workflows/harness-ci.yml` | GitHub Actions 质量门禁自动化 |

## 支持的 Agent 平台

| 平台 | 状态 | 核心差异 |
|------|------|----------|
| OpenCode | ✅ 完全支持 | 原生 `task()` 委托，`skill` 命令 |
| Claude Code | ✅ 完全支持 | bash 为主，MCP 通过 claude.json |
| Codex CLI | ✅ 完全支持 | shell + 文件系统操作 |
| Cursor | ⚠️ 部分支持 | 部分 MCP 需手动配置 |

## 配置项分类

| 配置类型 | 存储位置 | 变更频率 | 影响范围 |
|----------|----------|----------|----------|
| Agent 流程 | `.harness/agents/*.md` | 低 | 开发流程 |
| 质量门禁 | `.harness/scripts/verify-qg.*` | 低 | 质量检查 |
| 平台适配 | `.harness/platform.md` | 极低 | 平台兼容 |
| CI 配置 | `.github/workflows/` | 低 | 自动集成 |
| LSP | `.harness/mcp/`, `.opencode/` | 极低 | 代码编辑 |

## 环境信息

| 环境 | 用途 | 备注 |
|------|------|------|
| local | 本地开发 | Agent 在用户机器运行 |
| CI | GitHub Actions | 自动运行质量门禁和 Lint |
| staging | 预发布（如有） | 按需配置 |
