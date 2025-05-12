package cn.project.system.mapper;

import cn.project.system.domain.SysCart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车Mapper接口
 */
public interface SysCartMapper {
    
    /**
     * 查询已存在的购物车商品
     */
    public SysCart selectExistingCart(@Param("userId") Long userId, @Param("productId") Long productId, @Param("productType") String productType);
    
    /**
     * 查询用户的购物车列表
     */
    public List<SysCart> selectCartByUserId(Long userId);
    
    /**
     * 新增购物车商品
     */
    public int insertCart(SysCart cart);
    
    /**
     * 修改购物车商品
     */
    public int updateCart(SysCart cart);
    
    /**
     * 删除购物车商品
     */
    public int deleteCartById(Long cartId);
    
    /**
     * 清空用户购物车
     */
    public int deleteCartByUserId(Long userId);
} 