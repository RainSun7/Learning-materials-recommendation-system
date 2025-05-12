package cn.project.web.controller.system;

import lombok.Data;

import java.math.BigDecimal;

// OverviewResponse.java
@Data
public class OverviewResponse {
    private Integer totalVisits;  // 总访问量
    private BigDecimal totalSales; // 销售额
    private Integer totalOrders;  // 订单量

    // 构造方法、getters 和 setters
    public OverviewResponse(Integer totalVisits, BigDecimal totalSales, Integer totalOrders) {
        this.totalVisits = totalVisits;
        this.totalSales = totalSales;
        this.totalOrders = totalOrders;
    }

    // 省略 getters 和 setters...
}