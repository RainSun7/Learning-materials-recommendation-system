package cn.project.system.service;

import java.util.List;
import cn.project.system.domain.TraditionalCraftGoods;

/**
 * 传统工艺Service接口
 * 
 * @author default
 * @date 2025-03-07
 */
public interface ITraditionalCraftGoodsService 
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
     * 批量删除传统工艺
     * 
     * @param goodsIds 需要删除的传统工艺主键集合
     * @return 结果
     */
    public int deleteTraditionalCraftGoodsByGoodsIds(Long[] goodsIds);

    /**
     * 删除传统工艺信息
     * 
     * @param goodsId 传统工艺主键
     * @return 结果
     */
    public int deleteTraditionalCraftGoodsByGoodsId(Long goodsId);

    /**
     * 根据用户ID推荐传统工艺
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 传统工艺列表
     */
    public List<TraditionalCraftGoods> recommend(Long userId, int pageNum, int pageSize);
    
    /**
     * 记录用户行为（点赞/点踩）
     *
     * @param userId 用户ID
     * @param materialId 资料ID
     * @param actionType 行为类型(LIKE/DISLIKE)
     */
    public void recordAction(String userId, Long materialId, String actionType);
}
