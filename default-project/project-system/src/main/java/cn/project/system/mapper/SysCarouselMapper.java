package cn.project.system.mapper;

import java.util.List;
import cn.project.system.domain.SysCarousel;

/**
 * 轮播图Mapper接口
 * 
 * @author default
 * @date 2025-02-23
 */
public interface SysCarouselMapper 
{
    /**
     * 查询轮播图
     * 
     * @param carouselId 轮播图主键
     * @return 轮播图
     */
    public SysCarousel selectSysCarouselByCarouselId(Long carouselId);

    /**
     * 查询轮播图列表
     * 
     * @param sysCarousel 轮播图
     * @return 轮播图集合
     */
    public List<SysCarousel> selectSysCarouselList(SysCarousel sysCarousel);

    /**
     * 新增轮播图
     * 
     * @param sysCarousel 轮播图
     * @return 结果
     */
    public int insertSysCarousel(SysCarousel sysCarousel);

    /**
     * 修改轮播图
     * 
     * @param sysCarousel 轮播图
     * @return 结果
     */
    public int updateSysCarousel(SysCarousel sysCarousel);

    /**
     * 删除轮播图
     * 
     * @param carouselId 轮播图主键
     * @return 结果
     */
    public int deleteSysCarouselByCarouselId(Long carouselId);

    /**
     * 批量删除轮播图
     * 
     * @param carouselIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysCarouselByCarouselIds(Long[] carouselIds);
}
