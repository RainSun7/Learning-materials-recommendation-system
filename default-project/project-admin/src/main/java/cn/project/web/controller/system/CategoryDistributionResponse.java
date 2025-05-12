package cn.project.web.controller.system;

import lombok.Data;

import java.util.List;

// CategoryDistributionResponse.java
@Data
public class CategoryDistributionResponse {
    private List<String> categories;
    private List<CategoryValue> values;

    // 构造方法、getters 和 setters
    public CategoryDistributionResponse(List<String> categories, List<CategoryValue> values) {
        this.categories = categories;
        this.values = values;
    }

    // 省略 getters 和 setters...
}