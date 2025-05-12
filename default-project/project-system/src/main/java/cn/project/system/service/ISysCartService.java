package cn.project.system.service;

import cn.project.system.domain.SysCart;
import java.util.List;

/**
 * 购物车Service接口
 */
public interface ISysCartService {
    
    /**
     * 添加商品到购物车
     */
    public int addToCart(SysCart cart);
    
    /**
     * 获取用户购物车列表
     */
    public List<SysCart> getUserCart(Long userId);
    
    /**
     * 更新购物车商品数量
     */
    public int updateQuantity(Long cartId, Integer quantity);
    
    /**
     * 删除购物车商品
     */
    public int removeFromCart(Long cartId);
    
    /**
     * 清空用户购物车
     */
    public int clearCart(Long userId);
} 