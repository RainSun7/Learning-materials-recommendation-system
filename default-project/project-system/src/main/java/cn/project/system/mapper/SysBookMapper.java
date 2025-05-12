package cn.project.system.mapper;

import java.util.List;
import cn.project.system.domain.SysBook;

/**
 * 书籍Mapper接口
 * 
 * @author default
 * @date 2025-03-08
 */
public interface SysBookMapper 
{
    /**
     * 查询书籍
     * 
     * @param bookId 书籍主键
     * @return 书籍
     */
    public SysBook selectSysBookByBookId(Long bookId);

    /**
     * 查询书籍列表
     * 
     * @param sysBook 书籍
     * @return 书籍集合
     */
    public List<SysBook> selectSysBookList(SysBook sysBook);

    /**
     * 新增书籍
     * 
     * @param sysBook 书籍
     * @return 结果
     */
    public int insertSysBook(SysBook sysBook);

    /**
     * 修改书籍
     * 
     * @param sysBook 书籍
     * @return 结果
     */
    public int updateSysBook(SysBook sysBook);

    /**
     * 删除书籍
     * 
     * @param bookId 书籍主键
     * @return 结果
     */
    public int deleteSysBookByBookId(Long bookId);

    /**
     * 批量删除书籍
     * 
     * @param bookIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysBookByBookIds(Long[] bookIds);
}
