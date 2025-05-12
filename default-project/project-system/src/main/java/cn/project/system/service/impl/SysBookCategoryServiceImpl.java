package cn.project.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.project.common.exception.ServiceException;
import cn.project.system.mapper.SysBookCategoryMapper;
import cn.project.system.mapper.SysBookMapper;
import cn.project.system.domain.SysBook;
import cn.project.system.domain.SysBookCategory;
import cn.project.system.service.ISysBookCategoryService;

/**
 * 书籍分类Service业务层处理
 * 
 * @author default
 * @date 2025-02-23
 */
@Service
public class SysBookCategoryServiceImpl implements ISysBookCategoryService 
{
    @Autowired
    private SysBookCategoryMapper sysBookCategoryMapper;
    
    @Autowired
    private SysBookMapper sysBookMapper;

    /**
     * 查询书籍分类
     * 
     * @param categoryId 书籍分类主键
     * @return 书籍分类
     */
    @Override
    public SysBookCategory selectSysBookCategoryByCategoryId(Long categoryId)
    {
        return sysBookCategoryMapper.selectSysBookCategoryByCategoryId(categoryId);
    }

    /**
     * 查询书籍分类列表
     * 
     * @param sysBookCategory 书籍分类
     * @return 书籍分类
     */
    @Override
    public List<SysBookCategory> selectSysBookCategoryList(SysBookCategory sysBookCategory)
    {
        return sysBookCategoryMapper.selectSysBookCategoryList(sysBookCategory);
    }

    /**
     * 新增书籍分类
     * 
     * @param sysBookCategory 书籍分类
     * @return 结果
     */
    @Override
    public int insertSysBookCategory(SysBookCategory sysBookCategory)
    {
        return sysBookCategoryMapper.insertSysBookCategory(sysBookCategory);
    }

    /**
     * 修改书籍分类
     * 
     * @param sysBookCategory 书籍分类
     * @return 结果
     */
    @Override
    public int updateSysBookCategory(SysBookCategory sysBookCategory)
    {
        return sysBookCategoryMapper.updateSysBookCategory(sysBookCategory);
    }

    /**
     * 批量删除书籍分类
     * 
     * @param categoryIds 需要删除的书籍分类主键
     * @return 结果
     */
    @Override
    public int deleteSysBookCategoryByCategoryIds(Long[] categoryIds)
    {
        // 检查分类是否被图书使用
        for (Long categoryId : categoryIds)
        {
            checkCategoryHasBook(categoryId);
        }
        return sysBookCategoryMapper.deleteSysBookCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除书籍分类信息
     * 
     * @param categoryId 书籍分类主键
     * @return 结果
     */
    @Override
    public int deleteSysBookCategoryByCategoryId(Long categoryId)
    {
        // 检查分类是否被图书使用
        checkCategoryHasBook(categoryId);
        return sysBookCategoryMapper.deleteSysBookCategoryByCategoryId(categoryId);
    }
    
    /**
     * 检查分类是否被图书使用
     * 
     * @param categoryId 分类ID
     */
    private void checkCategoryHasBook(Long categoryId)
    {
        SysBook sysBook = new SysBook();
        sysBook.setCategoryId(categoryId);
        List<SysBook> books = sysBookMapper.selectSysBookList(sysBook);
        if (books != null && !books.isEmpty())
        {
            throw new ServiceException("分类已被图书使用，不能删除");
        }
    }
    
    /**
     * 根据分类ID查询图书列表
     * 
     * @param categoryId 分类ID
     * @return 图书列表
     */
    @Override
    public List<SysBook> selectBooksByCategoryId(Long categoryId)
    {
        SysBook sysBook = new SysBook();
        sysBook.setCategoryId(categoryId);
        return sysBookMapper.selectSysBookList(sysBook);
    }
}
