package cn.project.system.domain;

import cn.project.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单对象 sys_order
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_order")
public class SysOrder extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    @TableId(type = IdType.AUTO)
    private Long orderId;
    
    /** 订单编号 */
    private String orderNo;
    
    /** 用户ID */
    private Long userId;
    
    /** 收货人姓名 */
    private String userName;
    
    /** 收货人电话 */
    private String userPhone;
    
    /** 收货地址 */
    private String userAddress;
    
    /** 订单总金额 */
    private BigDecimal totalAmount;
    
    /** 订单状态（0-待付款 1-待发货 2-待收货 3-已完成 4-已取消） */
    private Integer status;
    
    /** 订单备注 */
    private String remark;
    
    /** 支付时间 */
    private Date payTime;
    
    /** 发货时间 */
    private Date deliveryTime;
    
    /** 完成时间 */
    private Date completeTime;

    /** 订单详情列表 */
    @TableField(exist = false)
    private List<SysOrderItem> orderItems;
} 