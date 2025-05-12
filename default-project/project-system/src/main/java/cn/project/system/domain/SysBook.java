package cn.project.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.project.common.annotation.Excel;
import cn.project.common.core.domain.BaseEntity;

/**
 * 书籍对象 sys_book
 * 
 * @author default
 * @date 2025-03-08
 */
public class SysBook extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 书籍 ID */
    private Long bookId;

    /** 分类 ID */
    @Excel(name = "分类 ID")
    private Long categoryId;

    /** 书籍名称 */
    @Excel(name = "书籍名称")
    private String bookName;

    /** 书籍作者 */
    @Excel(name = "书籍作者")
    private String author;

    /** 书籍价格 */
    @Excel(name = "书籍价格")
    private BigDecimal price;

    /** 出版日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出版日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishDate;

    /** 书籍库存 */
    @Excel(name = "书籍库存")
    private Long stock;

    /** 图片 */
    @Excel(name = "图片")
    private String pic;

    public void setBookId(Long bookId) 
    {
        this.bookId = bookId;
    }

    public Long getBookId() 
    {
        return bookId;
    }
    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }
    public void setBookName(String bookName) 
    {
        this.bookName = bookName;
    }

    public String getBookName() 
    {
        return bookName;
    }
    public void setAuthor(String author) 
    {
        this.author = author;
    }

    public String getAuthor() 
    {
        return author;
    }
    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }
    public void setPublishDate(Date publishDate) 
    {
        this.publishDate = publishDate;
    }

    public Date getPublishDate() 
    {
        return publishDate;
    }
    public void setStock(Long stock) 
    {
        this.stock = stock;
    }

    public Long getStock() 
    {
        return stock;
    }
    public void setPic(String pic) 
    {
        this.pic = pic;
    }

    public String getPic() 
    {
        return pic;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("bookId", getBookId())
            .append("categoryId", getCategoryId())
            .append("bookName", getBookName())
            .append("author", getAuthor())
            .append("price", getPrice())
            .append("publishDate", getPublishDate())
            .append("stock", getStock())
            .append("pic", getPic())
            .toString();
    }
}
