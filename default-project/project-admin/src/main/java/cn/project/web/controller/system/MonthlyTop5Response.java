package cn.project.web.controller.system;

import lombok.Data;

import java.util.List;

// MonthlyTop5Response.java
@Data
public class MonthlyTop5Response {
    private String month;  // 当前月份，格式 "YYYY-MM"
    private List<ProductSales> products; // TOP5产品数据

    // 构造方法、getters 和 setters
    public MonthlyTop5Response(String month, List<ProductSales> products) {
        this.month = month;
        this.products = products;
    }
}

