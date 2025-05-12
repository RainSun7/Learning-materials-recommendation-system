package cn.project.web.controller.system;

import lombok.Data;

import java.util.List;

// SalesTrendResponse.java
@Data
public class SalesTrendResponse {
    private List<String> dates;
    private List<Integer> expectedData;
    private List<Integer> actualData;

    // 构造方法、getters 和 setters
    public SalesTrendResponse(List<String> dates, List<Integer> expectedData, List<Integer> actualData) {
        this.dates = dates;
        this.expectedData = expectedData;
        this.actualData = actualData;
    }

    // 省略 getters 和 setters...
}