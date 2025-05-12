package cn.project.system.mapper;

import java.util.List;
import cn.project.system.domain.TraditionalCraftGoods;

/**
 * 传统工艺Mapper接口
 * 
 * @author default
 * @date 2025-03-07
 */
public interface TraditionalCraftGoodsMapper 
{
    /**
     * 查询传统工艺
     * 
     * @param goodsId 传统工艺主键
     * @return 传统工艺
     */
    public TraditionalCraftGoods selectTraditionalCraftGoodsByGoodsId(Long goodsId);

    /**
     * 查询传统工艺列表
     * 
     * @param traditionalCraftGoods 传统工艺
     * @return 传统工艺集合
     */
    public List<TraditionalCraftGoods> selectTraditionalCraftGoodsList(TraditionalCraftGoods traditionalCraftGoods);

    /**
     * 新增传统工艺
     * 
     * @param traditionalCraftGoods 传统工艺
     * @return 结果
     */
    public int insertTraditionalCraftGoods(TraditionalCraftGoods traditionalCraftGoods);

    /**
     * 修改传统工艺
     * 
     * @param traditionalCraftGoods 传统工艺
     * @return 结果
     */
    public int updateTraditionalCraftGoods(TraditionalCraftGoods traditionalCraftGoods);

    /**
     * 删除传统工艺
     * 
     * @param goodsId 传统工艺主键
     * @return 结果
     */
    public int deleteTraditionalCraftGoodsByGoodsId(Long goodsId);

    /**
     * 批量删除传统工艺
     * 
     * @param goodsIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTraditionalCraftGoodsByGoodsIds(Long[] goodsIds);



}
