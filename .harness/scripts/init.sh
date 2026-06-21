#!/bin/bash
# =============================================================================
# init.sh — Harness 会话启动脚本
# 每次 Agent 新会话开始时由 owner-agent.md 第零节调用。
# 幂等：多次运行结果相同。
#
# 由 Initializer Agent 生成（.harness/agents/initializer-agent.md）。
# =============================================================================

set -e

echo "🔧 Harness Session Init — $(date '+%Y-%m-%d %H:%M:%S')"

# 1. 检查项目是否可编译
if [ -f "pom.xml" ]; then
  mvn compile -q 2>/dev/null && echo "  ✅ Maven compile OK" || echo "  ❌ Maven compile failed"
elif [ -f "build.gradle" ]; then
  ./gradlew compileJava -q 2>/dev/null && echo "  ✅ Gradle compile OK" || echo "  ❌ Gradle compile failed"
else
  echo "  ⚠️  No build file found (pom.xml or build.gradle)"
fi

# 2. 检查变更目录
if [ -d ".harness/changes" ]; then
  CHANGES=$(ls -d .harness/changes/*/ 2>/dev/null | wc -l | tr -d ' ')
  echo "  📂 Changes in progress: ${CHANGES}"
else
  echo "  📂 No .harness/changes directory yet"
fi

echo "✅ Harness init complete"
