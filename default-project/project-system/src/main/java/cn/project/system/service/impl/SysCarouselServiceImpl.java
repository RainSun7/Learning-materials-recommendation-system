package cn.project.system.service.impl;

import java.util.List;
import cn.project.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.project.system.mapper.SysCarouselMapper;
import cn.project.system.domain.SysCarousel;
import cn.project.system.service.ISysCarouselService;

/**
 * 轮播图Service业务层处理
 * 
 * @author default
 * @date 2025-02-23
 */
@Service
public class SysCarouselServiceImpl implements ISysCarouselService 
{
    @Autowired
    private SysCarouselMapper sysCarouselMapper;

    /**
     * 查询轮播图
     * 
     * @param carouselId 轮播图主键
     * @return 轮播图
     */
    @Override
    public SysCarousel selectSysCarouselByCarouselId(Long carouselId)
    {
        return sysCarouselMapper.selectSysCarouselByCarouselId(carouselId);
    }

    /**
     * 查询轮播图列表
     * 
     * @param sysCarousel 轮播图
     * @return 轮播图
     */
    @Override
    public List<SysCarousel> selectSysCarouselList(SysCarousel sysCarousel)
    {
        return sysCarouselMapper.selectSysCarouselList(sysCarousel);
    }

    /**
     * 新增轮播图
     * 
     * @param sysCarousel 轮播图
     * @return 结果
     */
    @Override
    public int insertSysCarousel(SysCarousel sysCarousel)
    {
        sysCarousel.setCreateTime(DateUtils.getNowDate());
        return sysCarouselMapper.insertSysCarousel(sysCarousel);
    }

    /**
     * 修改轮播图
     * 
     * @param sysCarousel 轮播图
     * @return 结果
     */
    @Override
    public int updateSysCarousel(SysCarousel sysCarousel)
    {
        sysCarousel.setUpdateTime(DateUtils.getNowDate());
        return sysCarouselMapper.updateSysCarousel(sysCarousel);
    }

    /**
     * 批量删除轮播图
     * 
     * @param carouselIds 需要删除的轮播图主键
     * @return 结果
     */
    @Override
    public int deleteSysCarouselByCarouselIds(Long[] carouselIds)
    {
        return sysCarouselMapper.deleteSysCarouselByCarouselIds(carouselIds);
    }

    /**
     * 删除轮播图信息
     * 
     * @param carouselId 轮播图主键
     * @return 结果
     */
    @Override
    public int deleteSysCarouselByCarouselId(Long carouselId)
    {
        return sysCarouselMapper.deleteSysCarouselByCarouselId(carouselId);
    }
}
