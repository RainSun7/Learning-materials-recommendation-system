package cn.project.system.service;

import cn.project.system.domain.SysOrder;
import java.util.List;

/**
 * 订单Service接口
 */
public interface ISysOrderService {
    
    /**
     * 查询订单列表
     */
    public List<SysOrder> selectOrderList(SysOrder order);
    
    /**
     * 查询订单详细信息
     */
    public SysOrder selectOrderById(Long orderId);
    
    /**
     * 创建订单
     */
    public String createOrder(SysOrder order);
    
    /**
     * 更新订单状态
     */
    public int updateOrderStatus(String orderNo, Integer status);
    
    /**
     * 取消订单
     */
    public int cancelOrder(String orderNo);
    public int payOrder(String orderNo);

    /**
     * 确认收货
     */
    public int confirmOrder(String orderNo);

    /**
     * 评价订单
     * 
     * @param orderNo 订单编号
     * @param remark 评价内容
     * @return 结果
     */
    public int commentOrder(String orderNo, String remark);
    
    /**
     * 查询用户订单列表
     */
    public List<SysOrder> selectOrdersByUserId(Long userId);
} 