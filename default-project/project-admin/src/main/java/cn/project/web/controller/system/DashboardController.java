package cn.project.web.controller.system;

import cn.project.common.core.controller.BaseController;
import cn.project.common.core.domain.AjaxResult;
import cn.project.common.core.domain.entity.SysUser;
import cn.project.system.mapper.SysUserMapper;
import cn.project.web.controller.common.ChatWebSocket;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import cn.project.common.core.redis.RedisCache;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.Session;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {
    private static final String[] VALID_TYPES = {"销量概览", "销售额", "订单量"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String VISITS_NUMBER_KEY = "visitsNumber";

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    @Autowired
    private RedisCache redisCache;

    @GetMapping("/sales/monthly-top5")
    public AjaxResult getMonthlyTop5Products() {
        // 1. 获取当前月份
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 2. 查询当月销量TOP5的产品
        List<ProductSales> top5Products = getMonthlyTop5ProductsFromDB(currentMonth);

        // 3. 设置排名
        for (int i = 0; i < top5Products.size(); i++) {
            top5Products.get(i).setRank(i + 1);
        }

        return AjaxResult.success(new MonthlyTop5Response(currentMonth, top5Products));
    }


    private List<ProductSales> getMonthlyTop5ProductsFromDB(String month) {
        String sql = "SELECT oi.product_name AS name, SUM(oi.quantity) AS sales " +
                "FROM sys_order_item oi " +
                "JOIN sys_order o ON oi.order_id = o.order_id " +
                "WHERE DATE_FORMAT(o.pay_time, '%Y-%m') = :month " +
                "AND o.status >= 1 " + // 已支付订单
                "AND oi.product_type = 'CRAFT' " + // 只统计工艺品
                "GROUP BY oi.product_id, oi.product_name " +
                "ORDER BY sales DESC " +
                "LIMIT 5";

        Map<String, Object> params = new HashMap<>();
        params.put("month", month);

        return namedJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            // 截取产品名称，避免过长影响显示
            String productName = rs.getString("name");
            if (productName.length() > 15) {
                productName = productName.substring(0, 15) + "...";
            }

            return new ProductSales(
                    productName,
                    rs.getInt("sales"),
                    0 // 排名将在外层设置
            );
        });
    }
    @GetMapping("/category/distribution")
    public AjaxResult getCategoryDistribution() {
        // 1. 从数据库获取各品类商品数量
        Map<Long, Integer> categoryCounts = getCategoryCountsFromDB();

        // 2. 转换为接口需要的格式
        List<String> categories = new ArrayList<>();
        List<CategoryValue> values = new ArrayList<>();

        // 按商品数量从大到小排序
        categoryCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    Long categoryId = entry.getKey();
                    Integer count = entry.getValue();
                    String categoryName = CATEGORY_NAMES.getOrDefault(categoryId, "其他");

                    categories.add(categoryName);
                    values.add(new CategoryValue(categoryName, count));
                });

        return AjaxResult.success(new CategoryDistributionResponse(categories, values));
    }
    @GetMapping("/overview")
    public AjaxResult getOverviewData() {
        // 1. 获取总访问量（从Redis）
        Integer totalVisits = getTotalVisits();

        // 2. 获取总销售额（从数据库）
        BigDecimal totalSales = getTotalSales();

        // 3. 获取总订单量（从数据库）
        Integer totalOrders = getTotalOrders();

        return AjaxResult.success(new OverviewResponse(totalVisits, totalSales, totalOrders));
    }


    private Integer getTotalVisits() {
        Map<String, Integer> visitsMap = redisCache.getCacheMap(VISITS_NUMBER_KEY);
        Integer totalVisits = 0;
        for (Integer count : visitsMap.values()) {
            totalVisits += count;
        }
        return totalVisits;
    }


    private BigDecimal getTotalSales() {
        String sql = "SELECT SUM(total_amount) FROM sys_order WHERE pay_time IS NOT NULL";
        return namedJdbcTemplate.queryForObject(sql, new HashMap<>(), BigDecimal.class);
    }

    private Integer getTotalOrders() {
        String sql = "SELECT COUNT(*) FROM sys_order";
        return namedJdbcTemplate.queryForObject(sql, new HashMap<>(), Integer.class);
    }

    @GetMapping("/sales/trend")
    public AjaxResult getSalesTrend(
            @RequestParam("type") String type) {

        // 验证类型参数
        if (!isValidType(type)) {
            throw new IllegalArgumentException("Invalid type parameter");
        }

        // 获取最近7天日期
        List<String> dates = getRecent7Days();

        if ("销量概览".equals(type)) {
            // 从Redis获取真实访问量数据
            Map<String, Integer> visitsMap = redisCache.getCacheMap(VISITS_NUMBER_KEY);
            List<Integer> actualData = new ArrayList<>();

            for (String date : dates) {
                Integer count = visitsMap.get(date);
                actualData.add(count != null ? count : 0);
            }

            // 生成预期数据
//            List<Integer> expectedData = generateExpectedData(actualData);
            return AjaxResult.success(new SalesTrendResponse(dates, null, actualData));

        } else if ("销售额".equals(type)) {
            // 查询最近7天每天的销售额
            Map<String, BigDecimal> salesData = getRecent7DaysSales();
            List<Integer> actualData = new ArrayList<>();

            for (String date : dates) {
                BigDecimal amount = salesData.get(date);
                actualData.add(amount != null ? amount.intValue() : 0);
            }

            // 生成预期数据
            List<Integer> expectedData = generateExpectedData(actualData);
            return AjaxResult.success(new SalesTrendResponse(dates, null, actualData));

        } else if ("订单量".equals(type)) {
            // 查询最近7天每天的订单量
            Map<String, Integer> orderCounts = getRecent7DaysOrderCounts();
            List<Integer> actualData = new ArrayList<>();

            for (String date : dates) {
                Integer count = orderCounts.get(date);
                actualData.add(count != null ? count : 0);
            }

            // 生成预期数据
            List<Integer> expectedData = generateExpectedData(actualData);
            return AjaxResult.success(new SalesTrendResponse(dates, null, actualData));
        }

        return AjaxResult.error("未知的类型参数");
    }


    // Mock 品类名称映射（实际项目中应从数据库获取）
    private static final Map<Long, String> CATEGORY_NAMES = ImmutableMap.of(
            1L, "后港文创",
            2L, "null",
            3L, "精品文房",
            4L, "传统工艺品",
            5L, "创意家居"
    );




    private Map<Long, Integer> getCategoryCountsFromDB() {
        String sql = "SELECT category_id, COUNT(*) AS count " +
                "FROM traditional_craft_goods " +
                "WHERE category_id IS NOT NULL " +
                "GROUP BY category_id";

        return namedJdbcTemplate.query(sql, new HashMap<>(), rs -> {
            Map<Long, Integer> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getLong("category_id"), rs.getInt("count"));
            }
            return result;
        });
    }


    // 查询最近7天的销售额
    private Map<String, BigDecimal> getRecent7DaysSales() {
        String sql = "SELECT DATE_FORMAT(pay_time, '%Y-%m-%d') AS pay_date, " +
                "SUM(total_amount) AS daily_sales " +
                "FROM sys_order " +
                "WHERE pay_time IS NOT NULL " +
                "AND pay_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                "GROUP BY DATE_FORMAT(pay_time, '%Y-%m-%d')";

        return namedJdbcTemplate.query(sql, new HashMap<>(), rs -> {
            Map<String, BigDecimal> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getString("pay_date"), rs.getBigDecimal("daily_sales"));
            }
            return result;
        });
    }

 // 查询最近7天的订单量
    private Map<String, Integer> getRecent7DaysOrderCounts() {
        String sql = "SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS order_date, " +
                "COUNT(*) AS daily_count " +
                "FROM sys_order " +
                "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')";

        return namedJdbcTemplate.query(sql, new HashMap<>(), rs -> {
            Map<String, Integer> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getString("order_date"), rs.getInt("daily_count"));
            }
            return result;
        });
    }

    // 其他辅助方法保持不变...
    private boolean isValidType(String type) {
        for (String validType : VALID_TYPES) {
            if (validType.equals(type)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getRecent7Days() {
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(DATE_FORMATTER));
        }

        return dates;
    }

    private List<Integer> generateExpectedData(List<Integer> actualData) {
        List<Integer> expectedData = new ArrayList<>();
        for (Integer actual : actualData) {
            expectedData.add((int) (actual * 1.1));
        }
        return expectedData;
    }
}