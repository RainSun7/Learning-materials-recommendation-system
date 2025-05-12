package cn.project.system.mapper;

import cn.project.system.domain.SysOrder;
import java.util.List;

/**
 * 订单Mapper接口
 */
public interface SysOrderMapper {
    
    /**
     * 查询订单列表
     */
    public List<SysOrder> selectOrderList(SysOrder order);
    
    /**
     * 查询订单详细信息
     */
    public SysOrder selectOrderById(Long orderId);
    
    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    public SysOrder selectOrderByOrderNo(String orderNo);
    
    /**
     * 新增订单
     */
    public int insertOrder(SysOrder order);
    
    /**
     * 修改订单
     */
    public int updateOrder(SysOrder order);
    
    /**
     * 删除订单
     */
    public int deleteOrderById(Long orderId);
    
    /**
     * 批量删除订单
     */
    public int deleteOrderByIds(Long[] orderIds);
} 