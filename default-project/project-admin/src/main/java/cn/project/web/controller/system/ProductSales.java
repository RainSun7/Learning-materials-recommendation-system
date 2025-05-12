package cn.project.web.controller.system;

import lombok.Data;

// ProductSales.java
@Data
public class ProductSales {
    private String name;  // 产品名称
    private Integer sales; // 销量
    private Integer rank;  // 排名

    // 构造方法、getters 和 setters
    public ProductSales(String name, Integer sales, Integer rank) {
        this.name = name;
        this.sales = sales;
        this.rank = rank;
    }
}