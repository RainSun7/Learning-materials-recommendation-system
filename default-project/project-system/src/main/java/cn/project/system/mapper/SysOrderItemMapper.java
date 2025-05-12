package cn.project.system.mapper;

import cn.project.system.domain.SysOrderItem;
import java.util.List;

/**
 * 订单详情Mapper接口
 */
public interface SysOrderItemMapper {
    
    /**
     * 查询订单详情列表
     */
    public List<SysOrderItem> selectOrderItemList(SysOrderItem orderItem);
    
    /**
     * 查询订单详情
     */
    public List<SysOrderItem> selectOrderItemsByOrderId(Long orderId);
    
    /**
     * 新增订单详情
     */
    public int insertOrderItem(SysOrderItem orderItem);
    
    /**
     * 修改订单详情
     */
    public int updateOrderItem(SysOrderItem orderItem);
    
    /**
     * 删除订单详情
     */
    public int deleteOrderItemById(Long orderItemId);
    
    /**
     * 批量删除订单详情
     */
    public int deleteOrderItemByIds(Long[] orderItemIds);
    
    /**
     * 删除订单下的所有详情
     */
    public int deleteOrderItemByOrderId(Long orderId);
} 