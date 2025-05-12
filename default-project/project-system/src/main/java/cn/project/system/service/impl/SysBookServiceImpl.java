package cn.project.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.project.system.mapper.SysBookMapper;
import cn.project.system.domain.SysBook;
import cn.project.system.service.ISysBookService;

/**
 * 书籍Service业务层处理
 * 
 * @author default
 * @date 2025-03-08
 */
@Service
public class SysBookServiceImpl implements ISysBookService 
{
    @Autowired
    private SysBookMapper sysBookMapper;

    /**
     * 查询书籍
     * 
     * @param bookId 书籍主键
     * @return 书籍
     */
    @Override
    public SysBook selectSysBookByBookId(Long bookId)
    {
        return sysBookMapper.selectSysBookByBookId(bookId);
    }

    /**
     * 查询书籍列表
     * 
     * @param sysBook 书籍
     * @return 书籍
     */
    @Override
    public List<SysBook> selectSysBookList(SysBook sysBook)
    {
        return sysBookMapper.selectSysBookList(sysBook);
    }

    /**
     * 新增书籍
     * 
     * @param sysBook 书籍
     * @return 结果
     */
    @Override
    public int insertSysBook(SysBook sysBook)
    {
        return sysBookMapper.insertSysBook(sysBook);
    }

    /**
     * 修改书籍
     * 
     * @param sysBook 书籍
     * @return 结果
     */
    @Override
    public int updateSysBook(SysBook sysBook)
    {
        return sysBookMapper.updateSysBook(sysBook);
    }

    /**
     * 批量删除书籍
     * 
     * @param bookIds 需要删除的书籍主键
     * @return 结果
     */
    @Override
    public int deleteSysBookByBookIds(Long[] bookIds)
    {
        return sysBookMapper.deleteSysBookByBookIds(bookIds);
    }

    /**
     * 删除书籍信息
     * 
     * @param bookId 书籍主键
     * @return 结果
     */
    @Override
    public int deleteSysBookByBookId(Long bookId)
    {
        return sysBookMapper.deleteSysBookByBookId(bookId);
    }
}
