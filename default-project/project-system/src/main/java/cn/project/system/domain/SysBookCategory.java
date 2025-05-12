package cn.project.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.project.common.annotation.Excel;
import cn.project.common.core.domain.BaseEntity;

/**
 * 书籍分类对象 sys_book_category
 * 
 * @author default
 * @date 2025-02-23
 */
public class SysBookCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分类 ID */
    private Long categoryId;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String categoryName;

    /** 分类描述 */
    @Excel(name = "分类描述")
    private String categoryDescription;
    
    /** 分类图标 */
    @Excel(name = "分类图标")
    private String categoryIcon;
    
    /** 显示顺序 */
    @Excel(name = "显示顺序")
    private Integer orderNum;
    
    /** 关联图书数量 */
    private Integer bookCount;

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }
    public void setCategoryName(String categoryName) 
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName() 
    {
        return categoryName;
    }
    public void setCategoryDescription(String categoryDescription) 
    {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryDescription() 
    {
        return categoryDescription;
    }
    
    public void setCategoryIcon(String categoryIcon) 
    {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryIcon() 
    {
        return categoryIcon;
    }
    
    public void setOrderNum(Integer orderNum) 
    {
        this.orderNum = orderNum;
    }

    public Integer getOrderNum() 
    {
        return orderNum;
    }
    
    public void setBookCount(Integer bookCount) 
    {
        this.bookCount = bookCount;
    }

    public Integer getBookCount() 
    {
        return bookCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("categoryDescription", getCategoryDescription())
            .append("categoryIcon", getCategoryIcon())
            .append("orderNum", getOrderNum())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
