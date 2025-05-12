package cn.project.system.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.project.common.core.redis.RedisCache;
import cn.project.common.utils.DateUtils;
import cn.project.common.utils.PageUtils;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import cn.project.system.mapper.TraditionalCraftGoodsMapper;
import cn.project.system.domain.TraditionalCraftGoods;
import cn.project.system.service.ITraditionalCraftGoodsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 传统工艺Service业务层处理
 * 
 * @author default
 * @date 2025-03-07
 */
@Service
public class TraditionalCraftGoodsServiceImpl implements ITraditionalCraftGoodsService 
{
    @Autowired
    private TraditionalCraftGoodsMapper traditionalCraftGoodsMapper;

    /**
     * 查询传统工艺
     * 
     * @param goodsId 传统工艺主键
     * @return 传统工艺
     */
    @Override
    public TraditionalCraftGoods selectTraditionalCraftGoodsByGoodsId(Long goodsId)
    {
        TraditionalCraftGoods traditionalCraftGoods = traditionalCraftGoodsMapper.selectTraditionalCraftGoodsByGoodsId(goodsId);
        Object goods = redisCache.getCacheMapValue("goods", goodsId.toString());
        if (goods != null) {
            // 如果redis中有数据，则将其设置到对象中
            traditionalCraftGoods.setOtherPic((String) goods);
        }
        return traditionalCraftGoods;
    }

    /**
     * 查询传统工艺列表
     * 
     * @param traditionalCraftGoods 传统工艺
     * @return 传统工艺
     */
    @Override
    public List<TraditionalCraftGoods> selectTraditionalCraftGoodsList(TraditionalCraftGoods traditionalCraftGoods)
    {
        List<TraditionalCraftGoods> traditionalCraftGoods1 = traditionalCraftGoodsMapper.selectTraditionalCraftGoodsList(traditionalCraftGoods);
        for (TraditionalCraftGoods goods : traditionalCraftGoods1) {
            Object goodsId = redisCache.getCacheMapValue("goods", goods.getGoodsId().toString());
            if (goodsId != null) {
                // 如果redis中有数据，则将其设置到对象中
                goods.setOtherPic((String) goodsId);
            }
        }


        return traditionalCraftGoods1;
    }

    @Autowired
    private RedisCache redisCache;
    /**
     * 新增传统工艺
     * 
     * @param traditionalCraftGoods 传统工艺
     * @return 结果
     */
    @Override
    public int insertTraditionalCraftGoods(TraditionalCraftGoods traditionalCraftGoods)
    {
        traditionalCraftGoods.setCreateTime(DateUtils.getNowDate());
        int i = traditionalCraftGoodsMapper.insertTraditionalCraftGoods(traditionalCraftGoods);
        Long goodsId = traditionalCraftGoods.getGoodsId();
        if (goodsId == null) {
            // 这里可以添加日志记录或其他处理
            return i;
        }else {
            //redis 里塞一个map key是goodId value是 otherPic
            redisCache.setCacheMapValue("goods", goodsId.toString(), traditionalCraftGoods.getOtherPic());
            return i;
        }
    }

    /**
     * 修改传统工艺
     * 
     * @param traditionalCraftGoods 传统工艺
     * @return 结果
     */
    @Override
    public int updateTraditionalCraftGoods(TraditionalCraftGoods traditionalCraftGoods)
    {
        traditionalCraftGoods.setUpdateTime(DateUtils.getNowDate());
        //更新redis key为goods的map
        redisCache.setCacheMapValue("goods", traditionalCraftGoods.getGoodsId().toString(), traditionalCraftGoods.getOtherPic());

        return traditionalCraftGoodsMapper.updateTraditionalCraftGoods(traditionalCraftGoods);
    }

    /**
     * 批量删除传统工艺
     * 
     * @param goodsIds 需要删除的传统工艺主键
     * @return 结果
     */
    @Override
    public int deleteTraditionalCraftGoodsByGoodsIds(Long[] goodsIds)
    {
        return traditionalCraftGoodsMapper.deleteTraditionalCraftGoodsByGoodsIds(goodsIds);
    }

    /**
     * 删除传统工艺信息
     * 
     * @param goodsId 传统工艺主键
     * @return 结果
     */
    @Override
    public int deleteTraditionalCraftGoodsByGoodsId(Long goodsId)
    {
        return traditionalCraftGoodsMapper.deleteTraditionalCraftGoodsByGoodsId(goodsId);
    }

//    @Override
//    public List<TraditionalCraftGoods> recommend(Long goodsId, int pageNum, int pageSize) {
//        return Collections.emptyList();
//    }

    public List<TraditionalCraftGoods>  recommend(Long userId, int page, int size) {
        List<String> favCategories = this.getFavoriteCategories(userId);

        // 如果没有点赞历史，返回热门资料
        if (favCategories.isEmpty()) {
            PageUtils.startPage();
            this.selectTraditionalCraftGoodsList(new TraditionalCraftGoods());
        }


        return this.getRecommendations(userId, page, size);
    }
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    // 记录用户行为（点赞/点踩）
    @Transactional
    public void recordAction(String userId, Long materialId, String actionType) {
        String sql = "replace INTO user_action (user_id, material_id, action_type) " +
                "VALUES (:userId, :materialId, :actionType) ";
        jdbcTemplate.update(sql, ImmutableMap.of(
                "userId", userId,
                "materialId", materialId,
                "actionType", actionType
        ));

        // 清除相关缓存
        // 如果有缓存管理器可以解除下面的注释
        // cacheManager.getCache("userRecommendations").evict(userId);
    }




    // 获取用户偏好分类（点赞过的分类）
    public List<String> getFavoriteCategories(Long userId) {
        String sql = "SELECT DISTINCT g.category_id " +
                "FROM user_action ua " +
                "JOIN traditional_craft_goods g ON ua.material_id = g.goods_id " +
                "WHERE ua.user_id = :userId AND ua.action_type = 'LIKE' " +
                "GROUP BY g.category_id " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT 3";
        
        return jdbcTemplate.queryForList(
                sql,
                ImmutableMap.of("userId", userId.toString()),
                String.class
        );
    }

    // 获取推荐资料（带分页）
    public List<TraditionalCraftGoods> getRecommendations(Long userId, int page, int size) {
        List<String> favoriteCategories = getFavoriteCategories(userId);
        
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId.toString());
        params.put("offset", page * size);
        params.put("limit", size);
        
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT g.*, ");
        
        if (!favoriteCategories.isEmpty()) {
            params.put("categories", favoriteCategories);
            sqlBuilder.append("CASE WHEN g.category_id IN (:categories) THEN 1 ELSE 0 END AS priority ");
        } else {
            sqlBuilder.append("0 AS priority ");
        }
        
        sqlBuilder.append("FROM traditional_craft_goods g ")
                .append("WHERE g.goods_id NOT IN ( ")
                .append("    SELECT material_id FROM user_action ")
                .append("    WHERE user_id = :userId AND action_type = 'DISLIKE' ")
                .append(") ")
                .append("ORDER BY ")
                .append("    priority DESC, ")
                .append("    (SELECT COUNT(*) FROM user_action ua2 ")
                .append("     WHERE ua2.material_id = g.goods_id AND ua2.action_type = 'LIKE') DESC, ")
                .append("    g.create_time DESC ")
                .append("LIMIT :limit OFFSET :offset");

        return jdbcTemplate.query(
                sqlBuilder.toString(),
                params,
                (rs, rowNum) -> {
                    TraditionalCraftGoods goods = new TraditionalCraftGoods();
                    goods.setGoodsId(rs.getLong("goods_id"));
                    goods.setGoodsName(rs.getString("goods_name"));
                    goods.setDescription(rs.getString("description"));
                    goods.setPrice(rs.getBigDecimal("price"));
                    goods.setStock(rs.getLong("stock"));
                    goods.setCraftsmanName(rs.getString("craftsman_name"));
                    goods.setPic(rs.getString("pic"));
                    // 从Redis获取otherPic
                    Object otherPicObj = redisCache.getCacheMapValue("goods", goods.getGoodsId().toString());
                    if (otherPicObj != null) {
                        goods.setOtherPic((String) otherPicObj);
                    }
                    return goods;
                }
        );
    }
}
