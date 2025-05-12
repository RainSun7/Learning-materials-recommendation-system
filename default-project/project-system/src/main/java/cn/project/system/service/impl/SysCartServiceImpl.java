package cn.project.system.service.impl;

import cn.project.system.domain.SysCart;
import cn.project.system.mapper.SysCartMapper;
import cn.project.system.service.ISysCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车Service业务层处理
 */
@Service
public class SysCartServiceImpl implements ISysCartService {
    
    @Autowired
    private SysCartMapper cartMapper;

    /**
     * 添加商品到购物车
     */
    @Override
    @Transactional
    public int addToCart(SysCart cart) {
        // 查询是否已存在该商品
        SysCart existingCart = cartMapper.selectExistingCart(cart.getUserId(), cart.getProductId(), cart.getProductType());
        
        if (existingCart != null) {
            // 更新数量
            existingCart.setQuantity(existingCart.getQuantity() + cart.getQuantity());
            return cartMapper.updateCart(existingCart);
        } else {
            // 新增购物车项
            return cartMapper.insertCart(cart);
        }
    }

    /**
     * 获取用户购物车列表
     */
    @Override
    public List<SysCart> getUserCart(Long userId) {
        return cartMapper.selectCartByUserId(userId);
    }

    /**
     * 更新购物车商品数量
     */
    @Override
    @Transactional
    public int updateQuantity(Long cartId, Integer quantity) {
        SysCart cart = new SysCart();
        cart.setCartId(cartId);
        cart.setQuantity(quantity);
        return cartMapper.updateCart(cart);
    }

    /**
     * 删除购物车商品
     */
    @Override
    @Transactional
    public int removeFromCart(Long cartId) {
        return cartMapper.deleteCartById(cartId);
    }

    /**
     * 清空用户购物车
     */
    @Override
    @Transactional
    public int clearCart(Long userId) {
        return cartMapper.deleteCartByUserId(userId);
    }
} 