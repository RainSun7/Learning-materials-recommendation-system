package cn.project.system.domain;

import cn.project.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单详情对象 sys_order_item
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_order_item")
public class SysOrderItem extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    public SysOrderItem() {
    }

    public SysOrderItem(Long orderId) {
        this.orderId = orderId;
    }

    /** 订单详情ID */
    @TableId(type = IdType.AUTO)
    private Long orderItemId;
    
    /** 订单ID */
    private Long orderId;
    
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
    
    /** 小计金额 */
    private BigDecimal subtotal;
} 