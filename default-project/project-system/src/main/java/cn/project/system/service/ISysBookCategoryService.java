package cn.project.system.service;

import java.util.List;
import cn.project.system.domain.SysBook;
import cn.project.system.domain.SysBookCategory;

/**
 * 书籍分类Service接口
 * 
 * @author default
 * @date 2025-02-23
 */
public interface ISysBookCategoryService 
{
    /**
     * 查询书籍分类
     * 
     * @param categoryId 书籍分类主键
     * @return 书籍分类
     */
    public SysBookCategory selectSysBookCategoryByCategoryId(Long categoryId);

    /**
     * 查询书籍分类列表
     * 
     * @param sysBookCategory 书籍分类
     * @return 书籍分类集合
     */
    public List<SysBookCategory> selectSysBookCategoryList(SysBookCategory sysBookCategory);

    /**
     * 新增书籍分类
     * 
     * @param sysBookCategory 书籍分类
     * @return 结果
     */
    public int insertSysBookCategory(SysBookCategory sysBookCategory);

    /**
     * 修改书籍分类
     * 
     * @param sysBookCategory 书籍分类
     * @return 结果
     */
    public int updateSysBookCategory(SysBookCategory sysBookCategory);

    /**
     * 批量删除书籍分类
     * 
     * @param categoryIds 需要删除的书籍分类主键集合
     * @return 结果
     */
    public int deleteSysBookCategoryByCategoryIds(Long[] categoryIds);

    /**
     * 删除书籍分类信息
     * 
     * @param categoryId 书籍分类主键
     * @return 结果
     */
    public int deleteSysBookCategoryByCategoryId(Long categoryId);
    
    /**
     * 根据分类ID查询图书列表
     * 
     * @param categoryId 分类ID
     * @return 图书列表
     */
    public List<SysBook> selectBooksByCategoryId(Long categoryId);
}
