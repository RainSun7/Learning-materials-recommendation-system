package cn.project.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.project.common.annotation.Excel;
import cn.project.common.core.domain.BaseEntity;

/**
 * 轮播图对象 sys_carousel
 * 
 * @author default
 * @date 2025-02-23
 */
public class SysCarousel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 轮播图 ID */
    private Long carouselId;

    /** 图片链接 */
    @Excel(name = "图片链接")
    private String imageUrl;

    /** 跳转链接 */
    @Excel(name = "跳转链接")
    private String linkUrl;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    private Long displayOrder;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 状态（0: 禁用，1: 启用） */
    @Excel(name = "状态", readConverterExp = "0=:,禁=用，1:,启=用")
    private Integer status;

    public void setCarouselId(Long carouselId) 
    {
        this.carouselId = carouselId;
    }

    public Long getCarouselId() 
    {
        return carouselId;
    }
    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }
    public void setLinkUrl(String linkUrl) 
    {
        this.linkUrl = linkUrl;
    }

    public String getLinkUrl() 
    {
        return linkUrl;
    }
    public void setDisplayOrder(Long displayOrder) 
    {
        this.displayOrder = displayOrder;
    }

    public Long getDisplayOrder() 
    {
        return displayOrder;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("carouselId", getCarouselId())
            .append("imageUrl", getImageUrl())
            .append("linkUrl", getLinkUrl())
            .append("displayOrder", getDisplayOrder())
            .append("title", getTitle())
            .append("description", getDescription())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
