package cn.project.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.project.common.annotation.Log;
import cn.project.common.core.controller.BaseController;
import cn.project.common.core.domain.AjaxResult;
import cn.project.common.enums.BusinessType;
import cn.project.system.domain.SysBook;
import cn.project.system.service.ISysBookService;
import cn.project.common.utils.poi.ExcelUtil;
import cn.project.common.core.page.TableDataInfo;

/**
 * 书籍Controller
 * 
 * @author default
 * @date 2025-03-08
 */
@RestController
@RequestMapping("/system/book")
public class SysBookController extends BaseController
{
    @Autowired
    private ISysBookService sysBookService;

    /**
     * 查询书籍列表
     */
//    @PreAuthorize("@ss.hasPermi('system:book:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysBook sysBook)
    {
        startPage();
        List<SysBook> list = sysBookService.selectSysBookList(sysBook);
        return getDataTable(list);
    }

    /**
     * 导出书籍列表
     */
    @PreAuthorize("@ss.hasPermi('system:book:export')")
    @Log(title = "书籍", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBook sysBook)
    {
        List<SysBook> list = sysBookService.selectSysBookList(sysBook);
        ExcelUtil<SysBook> util = new ExcelUtil<SysBook>(SysBook.class);
        util.exportExcel(response, list, "书籍数据");
    }

    /**
     * 获取书籍详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:book:query')")
    @GetMapping(value = "/{bookId}")
    public AjaxResult getInfo(@PathVariable("bookId") Long bookId)
    {
        return success(sysBookService.selectSysBookByBookId(bookId));
    }

    /**
     * 新增书籍
     */
    @PreAuthorize("@ss.hasPermi('system:book:add')")
    @Log(title = "书籍", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysBook sysBook)
    {
        return toAjax(sysBookService.insertSysBook(sysBook));
    }

    /**
     * 修改书籍
     */
    @PreAuthorize("@ss.hasPermi('system:book:edit')")
    @Log(title = "书籍", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysBook sysBook)
    {
        return toAjax(sysBookService.updateSysBook(sysBook));
    }

    /**
     * 删除书籍
     */
    @PreAuthorize("@ss.hasPermi('system:book:remove')")
    @Log(title = "书籍", businessType = BusinessType.DELETE)
	@DeleteMapping("/{bookIds}")
    public AjaxResult remove(@PathVariable Long[] bookIds)
    {
        return toAjax(sysBookService.deleteSysBookByBookIds(bookIds));
    }
}
