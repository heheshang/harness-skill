package core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 阶段 5：单元测试编写
 * 
 * 测试 PriceRuleFilterService 的价格过滤逻辑。
 * 覆盖率要求：分支覆盖 >= 90%，新增代码行覆盖 >= 85%
 */
class PriceRuleFilterServiceTest {

    private PriceRuleFilterService service;

    @BeforeEach
    void setUp() {
        service = new PriceRuleFilterService();
    }

    // ── 正常场景 ──────────────────────────────────────────────

    @Test
    @DisplayName("AC-1: 存在匹配的渠道规则，返回该渠道专属价格")
    void testFilterByChannel_shouldReturnChannelSpecificPrice() {
        PriceQueryRequest request = new PriceQueryRequest()
            .setProductId("P001")
            .setChannel("online");

        PriceResult result = service.calculate(request);

        assertNotNull(result);
        assertEquals(99.00, result.getPrice(), 0.01);
        assertEquals("online", result.getMatchedRule().getChannel());
    }

    @Test
    @DisplayName("AC-2: 存在匹配的用户等级规则，返回该等级专属价格")
    void testFilterByLevel_shouldReturnLevelSpecificPrice() {
        PriceQueryRequest request = new PriceQueryRequest()
            .setProductId("P001")
            .setUserLevel("VIP");

        PriceResult result = service.calculate(request);

        assertNotNull(result);
        assertEquals(89.00, result.getPrice(), 0.01);
    }

    @Test
    @DisplayName("AC-3: 同时存在多个规则，按优先级返回最高优先级规则的价格")
    void testMultipleRules_shouldReturnHighestPriorityPrice() {
        // P1 = 1 (highest), P2 = 5 (lower)
        PriceResult result = service.calculate(new PriceQueryRequest()
            .setProductId("P001")
            .setChannel("online")
            .setUserLevel("VIP"));

        // Both channel and level rules match — channel rule has priority 1
        assertNotNull(result);
        assertEquals("online", result.getMatchedRule().getChannel());
        assertEquals(1, result.getMatchedRule().getPriority());
    }

    @Test
    @DisplayName("AC-4: 规则未在生效时间内，不应用该规则")
    void testExpiredRule_shouldNotApply() {
        PriceQueryRequest request = new PriceQueryRequest()
            .setProductId("P001")
            .setChannel("offline");  // offline rule is expired

        PriceResult result = service.calculate(request);

        assertNotNull(result);
        // Should fall back to default price (not offline price)
        assertEquals(100.00, result.getPrice(), 0.01);
        assertNull(result.getMatchedRule());
    }

    // ── 异常场景 ──────────────────────────────────────────────

    @Test
    @DisplayName("AC-5: 无任何匹配规则，返回默认价格")
    void testNoMatchingRule_shouldReturnDefaultPrice() {
        PriceQueryRequest request = new PriceQueryRequest()
            .setProductId("P999")  // no rules for this product
            .setChannel("online");

        PriceResult result = service.calculate(request);

        assertNotNull(result);
        assertEquals(100.00, result.getPrice(), 0.01);
        assertNull(result.getMatchedRule());
    }

    @Test
    @DisplayName("AC-6: 规则数量超过上限（100条），抛出参数异常")
    void testRuleLimitExceeded_shouldThrowException() {
        PriceQueryRequest request = new PriceQueryRequest()
            .setProductId("P999")
            .setRuleCount(101);

        assertThrows(PriceRuleLimitExceededException.class,
            () -> service.calculate(request));
    }

    // ── 边界场景 ──────────────────────────────────────────────

    @Test
    @DisplayName("BC-1: 优先级相同，按创建时间倒序取最新的规则")
    void testSamePriority_shouldPickNewerRule() {
        PriceResult result = service.calculate(new PriceQueryRequest()
            .setProductId("P001")
            .setRegion("east"));

        // Two rules with priority 5 for region=east, rule2 is newer
        assertNotNull(result);
        assertEquals("rule-002", result.getMatchedRule().getRuleId());
    }

    @Test
    @DisplayName("BC-2: 规则为空，返回默认价格不报错")
    void testEmptyRules_shouldReturnDefaultWithoutError() {
        PriceResult result = service.calculate(new PriceQueryRequest()
            .setProductId("EMPTY"));

        assertNotNull(result);
        assertEquals(100.00, result.getPrice(), 0.01);
        assertNull(result.getMatchedRule());
    }

    @ParameterizedTest
    @CsvSource({
        "P001, online, VIP,   99.00",
        "P001, online, NORMAL, 99.00",
        "P001, offline, VIP, 100.00",  // offline has expired rule → default
    })
    @DisplayName("BC-3: 多维度组合查询的集成验证")
    void testMultiDimensionCombinations(String productId, String channel,
                                         String level, double expectedPrice) {
        PriceResult result = service.calculate(new PriceQueryRequest()
            .setProductId(productId)
            .setChannel(channel)
            .setUserLevel(level));
        assertEquals(expectedPrice, result.getPrice(), 0.01);
    }
}
