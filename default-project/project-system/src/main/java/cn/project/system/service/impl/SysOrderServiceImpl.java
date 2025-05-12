package cn.project.system.service.impl;

import cn.project.common.utils.DateUtils;
import cn.project.common.utils.StringUtils;
import cn.project.system.domain.SysOrder;
import cn.project.system.domain.SysOrderItem;
import cn.project.system.mapper.SysOrderItemMapper;
import cn.project.system.mapper.SysOrderMapper;
import cn.project.system.service.ISysOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 订单Service业务层处理
 */
@Service
public class SysOrderServiceImpl implements ISysOrderService {
    
    @Autowired
    private SysOrderMapper orderMapper;
    
    @Autowired
    private SysOrderItemMapper orderItemMapper;

    /**
     * 查询订单列表
     */
    @Override
    public List<SysOrder> selectOrderList(SysOrder order) {
        return orderMapper.selectOrderList(order);
    }

    /**
     * 查询订单详细信息
     */
    @Override
    public SysOrder selectOrderById(Long orderId) {
        SysOrder order = orderMapper.selectOrderById(orderId);
        if (order != null) {
            // 查询订单详情
            List<SysOrderItem> items = orderItemMapper.selectOrderItemsByOrderId(orderId);
            order.setOrderItems(items);
        }
        return order;
    }

    /**
     * 创建订单
     */
    @Override
    @Transactional
    public String createOrder(SysOrder order) {
        // 生成订单编号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(0); // 待付款
        order.setCreateTime(DateUtils.getNowDate());
        
        // 保存订单
        orderMapper.insertOrder(order);
        
        // 保存订单详情
        if (StringUtils.isNotEmpty(order.getOrderItems())) {
            for (SysOrderItem item : order.getOrderItems()) {
                item.setOrderId(order.getOrderId());
                item.setCreateTime(DateUtils.getNowDate());
                orderItemMapper.insertOrderItem(item);
            }
        }
        
        return orderNo;
    }

    /**
     * 更新订单状态
     */
    @Override
    @Transactional
    public int updateOrderStatus(String orderNo, Integer status) {
        SysOrder order = new SysOrder();
        order.setOrderNo(orderNo);
        order.setStatus(status);
        
        // 根据状态更新相应时间
        if (status == 1) { // 待发货，更新支付时间
            order.setPayTime(DateUtils.getNowDate());
        } else if (status == 2) { // 待收货，更新发货时间
            order.setDeliveryTime(DateUtils.getNowDate());
        } else if (status == 3) { // 已完成，更新完成时间
            order.setCompleteTime(DateUtils.getNowDate());
        }
        
        return orderMapper.updateOrder(order);
    }

    /**
     * 取消订单
     */
    @Override
    @Transactional
    public int cancelOrder(String orderNo) {
        return updateOrderStatus(orderNo, 4);
    }

    @Override
    @Transactional
    public int payOrder(String orderNo) {
        return updateOrderStatus(orderNo, 1);
    }

    /**
     * 确认收货
     */
    @Override
    @Transactional
    public int confirmOrder(String orderNo) {
        SysOrder order = orderMapper.selectOrderByOrderNo(orderNo);
        if (order != null) {
            order.setStatus(3); // 设置为已完成状态
            order.setCompleteTime(new Date()); // 设置完成时间
            return orderMapper.updateOrder(order);
        }
        return 0;
    }

    @Override
    public int commentOrder(String orderNo, String remark) {
        System.out.println("Commenting order: " + orderNo + ", remark: " + remark); // 添加日志
        SysOrder order = orderMapper.selectOrderByOrderNo(orderNo);
        if (order != null && order.getStatus() == 3) { // 确保订单是已完成状态
            System.out.println("Found order: " + order.getOrderId()); // 添加日志
            order = new SysOrder(); // 创建新的对象，只更新必要字段
            order.setOrderNo(orderNo);
            order.setRemark(remark);
            int rows = orderMapper.updateOrder(order);
            System.out.println("Update result: " + rows); // 添加日志
            return rows;
        }
        return 0;
    }

    /**
     * 查询用户订单列表
     */
    @Override
    public List<SysOrder> selectOrdersByUserId(Long userId) {
        SysOrder order = new SysOrder();
        order.setUserId(userId);
        return orderMapper.selectOrderList(order);
    }
    
    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
} 