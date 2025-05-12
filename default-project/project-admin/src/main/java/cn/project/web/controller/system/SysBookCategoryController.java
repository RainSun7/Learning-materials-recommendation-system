package cn.project.web.controller.system;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import cn.project.system.domain.SysBookCategory;
import cn.project.system.service.ISysBookCategoryService;
import cn.project.common.utils.poi.ExcelUtil;
import cn.project.common.core.page.TableDataInfo;

/**
 * 书籍分类Controller
 * 
 * @author default
 * @date 2025-02-23
 */
@RestController
@RequestMapping("/system/category")
public class SysBookCategoryController extends BaseController
{
    @Autowired
    private ISysBookCategoryService sysBookCategoryService;

    /**
     * 查询书籍分类列表
     */
//    @PreAuthorize("@ss.hasPermi('system:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysBookCategory sysBookCategory)
    {
        startPage();
        List<SysBookCategory> list = sysBookCategoryService.selectSysBookCategoryList(sysBookCategory);
        return getDataTable(list);
    }

    /**
     * 导出书籍分类列表
     */
    @PreAuthorize("@ss.hasPermi('system:category:export')")
    @Log(title = "书籍分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBookCategory sysBookCategory)
    {
        List<SysBookCategory> list = sysBookCategoryService.selectSysBookCategoryList(sysBookCategory);
        ExcelUtil<SysBookCategory> util = new ExcelUtil<SysBookCategory>(SysBookCategory.class);
        util.exportExcel(response, list, "书籍分类数据");
    }

    /**
     * 获取书籍分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:category:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return success(sysBookCategoryService.selectSysBookCategoryByCategoryId(categoryId));
    }

    /**
     * 新增书籍分类
     */
    @PreAuthorize("@ss.hasPermi('system:category:add')")
    @Log(title = "书籍分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysBookCategory sysBookCategory)
    {
        return toAjax(sysBookCategoryService.insertSysBookCategory(sysBookCategory));
    }

    /**
     * 修改书籍分类
     */
    @PreAuthorize("@ss.hasPermi('system:category:edit')")
    @Log(title = "书籍分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysBookCategory sysBookCategory)
    {
        return toAjax(sysBookCategoryService.updateSysBookCategory(sysBookCategory));
    }

    /**
     * 删除书籍分类
     */
    @PreAuthorize("@ss.hasPermi('system:category:remove')")
    @Log(title = "书籍分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(sysBookCategoryService.deleteSysBookCategoryByCategoryIds(categoryIds));
    }
    
    /**
     * 获取分类树形结构
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SysBookCategory sysBookCategory)
    {
        List<SysBookCategory> categories = sysBookCategoryService.selectSysBookCategoryList(sysBookCategory);
        List<Map<String, Object>> treeList = new ArrayList<>();
        for (SysBookCategory category : categories)
        {
            Map<String, Object> treeNode = new HashMap<>();
            treeNode.put("id", category.getCategoryId());
            treeNode.put("label", category.getCategoryName());
            treeNode.put("icon", category.getCategoryIcon());
            treeNode.put("orderNum", category.getOrderNum());
            treeList.add(treeNode);
        }
        return success(treeList);
    }
    
    /**
     * 根据分类ID查询图书列表
     */
    @GetMapping("/books/{categoryId}")
    public TableDataInfo listBooksByCategory(@PathVariable("categoryId") Long categoryId)
    {
        startPage();
        List<SysBook> list = sysBookCategoryService.selectBooksByCategoryId(categoryId);
        return getDataTable(list);
    }
    
    /**
     * 获取分类及其关联的图书数量
     */
    @GetMapping("/withBookCount")
    public AjaxResult listWithBookCount(SysBookCategory sysBookCategory)
    {
        List<SysBookCategory> categories = sysBookCategoryService.selectSysBookCategoryList(sysBookCategory);
        for (SysBookCategory category : categories)
        {
            List<SysBook> books = sysBookCategoryService.selectBooksByCategoryId(category.getCategoryId());
            category.setBookCount(books.size());
        }
        return success(categories);
    }
}
