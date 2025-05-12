package cn.project.system.domain;

import cn.project.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 购物车对象 sys_cart
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_cart")
public class SysCart extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 购物车ID */
    @TableId(type = IdType.AUTO)
    private Long cartId;
    
    /** 用户ID */
    private Long userId;
    
    /** 商品ID */
    private Long productId;
    
    /** 商品类型（BOOK-图书, CRAFT-工艺品） */
    private String productType;
    
    /** 商品名称 */
    private String productName;
    
    /** 商品图片 */
    private String productImage;
    
    /** 商品价格 */
    private BigDecimal price;
    
    /** 购买数量 */
    private Integer quantity;
} 