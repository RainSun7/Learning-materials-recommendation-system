package cn.project.web.controller.system;

import lombok.Data;

@Data
public class CategoryValue {
    private String name;
    private Integer value;

    // 构造方法、getters 和 setters
    public CategoryValue(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}
