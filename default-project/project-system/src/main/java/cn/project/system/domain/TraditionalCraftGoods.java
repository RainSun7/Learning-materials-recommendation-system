package cn.project.system.domain;

import java.math.BigDecimal;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.project.common.annotation.Excel;
import cn.project.common.core.domain.BaseEntity;

/**
 * 传统工艺对象 traditional_craft_goods
 * 
 * @author default
 * @date 2025-03-07
 */
@Data
public class TraditionalCraftGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long goodsId;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String goodsName;

    /** 商品描述 */
    @Excel(name = "商品描述")
    private String description;

    /** 商品价格 */
    @Excel(name = "商品价格")
    private BigDecimal price;

    /** 库存数量 */
    @Excel(name = "库存数量")
    private Long stock;

    /** 工艺师名称 */
    @Excel(name = "工艺师名称")
    private String craftsmanName;

    /** 商品图片 */
    @Excel(name = "商品图片")
    private String pic;

    @Excel(name = "其他图片")
    private String otherPic;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    public void setGoodsId(Long goodsId) 
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId() 
    {
        return goodsId;
    }
    public void setGoodsName(String goodsName) 
    {
        this.goodsName = goodsName;
    }

    public String getGoodsName() 
    {
        return goodsName;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }
    public void setStock(Long stock) 
    {
        this.stock = stock;
    }

    public Long getStock() 
    {
        return stock;
    }
    public void setCraftsmanName(String craftsmanName) 
    {
        this.craftsmanName = craftsmanName;
    }

    public String getCraftsmanName() 
    {
        return craftsmanName;
    }
    public void setPic(String pic) 
    {
        this.pic = pic;
    }

    public String getPic() 
    {
        return pic;
    }
    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("goodsId", getGoodsId())
            .append("goodsName", getGoodsName())
            .append("description", getDescription())
            .append("price", getPrice())
            .append("stock", getStock())
            .append("craftsmanName", getCraftsmanName())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("pic", getPic())
            .append("categoryId", getCategoryId())
            .toString();
    }
}
