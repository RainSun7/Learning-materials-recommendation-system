package cn.project.web.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import cn.project.common.annotation.Anonymous;
import cn.project.common.core.domain.entity.SysUser;
import cn.project.common.core.domain.model.LoginUser;
import cn.project.common.utils.SecurityUtils;
import com.google.common.collect.ImmutableMap;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.project.common.annotation.Log;
import cn.project.common.core.controller.BaseController;
import cn.project.common.core.domain.AjaxResult;
import cn.project.common.enums.BusinessType;
import cn.project.system.domain.TraditionalCraftGoods;
import cn.project.system.service.ITraditionalCraftGoodsService;
import cn.project.common.utils.poi.ExcelUtil;
import cn.project.common.core.page.TableDataInfo;

/**
 * 传统工艺Controller
 * 
 * @author default
 * @date 2025-03-07
 */
@RestController
@RequestMapping("/system/goods")
public class TraditionalCraftGoodsController extends BaseController
{
    @Autowired
    private ITraditionalCraftGoodsService traditionalCraftGoodsService;
@Autowired
private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    /**
     * 查询传统工艺列表
     */
    //    @PreAuthorize("@ss.hasPermi('system:goods:list')")
    @GetMapping("/list")
    public TableDataInfo list(TraditionalCraftGoods traditionalCraftGoods)
    {
        startPage();
        String goodsName = traditionalCraftGoods.getGoodsName();
        if (goodsName != null) {
            // 向search_history表中插入数据
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();
            Map<String, Object> params = new HashMap<>();
            params.put("goodsName", goodsName);
            params.put("userId", user.getUserId()); // 假设用户ID为1

            String sql = "INSERT INTO search_history (user_id, search_content, search_time) VALUES (:userId, :goodsName, NOW())";
            namedParameterJdbcTemplate.update(sql, params);
        }
        List<TraditionalCraftGoods> list = traditionalCraftGoodsService.selectTraditionalCraftGoodsList(traditionalCraftGoods);
        return getDataTable(list);
    }

    /**
     * 导出传统工艺列表
     */
    @PreAuthorize("@ss.hasPermi('system:goods:export')")
    @Log(title = "传统工艺", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TraditionalCraftGoods traditionalCraftGoods)
    {
        List<TraditionalCraftGoods> list = traditionalCraftGoodsService.selectTraditionalCraftGoodsList(traditionalCraftGoods);
        ExcelUtil<TraditionalCraftGoods> util = new ExcelUtil<TraditionalCraftGoods>(TraditionalCraftGoods.class);
        util.exportExcel(response, list, "传统工艺数据");
    }
    /**
     * 获取传统工艺详细信息
     */
    //    @PreAuthorize("@ss.hasPermi('system:goods:query')")
    @GetMapping(value = "/{goodsId}")
    public AjaxResult getInfo(@PathVariable("goodsId") Long goodsId)
    {
        TraditionalCraftGoods traditionalCraftGoods = traditionalCraftGoodsService.selectTraditionalCraftGoodsByGoodsId(goodsId);

        //保存访问次数 visits
//         good_id         varchar(100)
// good_name        varchar(100)
// good_desc        varchar(100)
// classification        varchar(100)
//   cover       varchar(256)
// user_id         varchar(32)
// count      int
        //通过namedParameterJdbcTemplate 查询visits表中是否有该goodsId
        String sql = "SELECT * FROM visits WHERE good_id = :goodId and user_id = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("goodId", goodsId);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        params.put("userId", user.getUserId());
        Integer count = namedParameterJdbcTemplate.query(sql, params, rs -> {
            if (rs.next()) {
                return rs.getInt("count");
            }
            return null;
        });
        if (count == null) {
            // 如果没有记录，则插入一条新记录
            sql = "INSERT INTO visits (good_id,user_id,good_name,good_desc,classification,cover, count) VALUES " +
                    "(:goodId,:userId, :goodName, :goodDesc, :classification, :cover, 1)";
            params.put("goodName", traditionalCraftGoods.getGoodsName());
            params.put("goodDesc", traditionalCraftGoods.getDescription());
            params.put("classification", traditionalCraftGoods.getCategoryId());
            params.put("cover", traditionalCraftGoods.getPic());
            namedParameterJdbcTemplate.update(sql, params);
        } else {
            // 如果有记录，则更新访问次数
            sql = "UPDATE visits SET count = count + 1 WHERE good_id = :goodId and user_id = :userId";
            namedParameterJdbcTemplate.update(sql, params);
        }

        return success(traditionalCraftGoods);
    }
    @GetMapping("/getSearchHistory/{userId}")
    public AjaxResult getSearchHistory(@PathVariable("userId") Long userId) {
        //通过namedParameterJdbcTemplate 查询search_history表中是否有该用户访问记录
        String sql = "SELECT * FROM search_history WHERE user_id = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        ArrayList<Map<String, String>> query = namedParameterJdbcTemplate.query(sql, params, rs -> {
            ArrayList<Map<String, String>> objects = new ArrayList<>();
            while (rs.next())  {
                HashMap<String, String> stringStringHashMap = new HashMap<>();
                stringStringHashMap.put("searchContent", rs.getString("search_content"));
                stringStringHashMap.put("searchTime", rs.getString("search_time"));
                objects.add(stringStringHashMap);
            }
            return objects;
        });

        return success(query);
    }

    @GetMapping(value = "/getCountInfo/{userId}")
    public AjaxResult getCountInfo(@PathVariable("userId") Long userId){
        //通过namedParameterJdbcTemplate 查询visits表中是否有该用户访问记录
        String sql = "SELECT * FROM visits WHERE user_id = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        ArrayList<Map<String, String>> query = namedParameterJdbcTemplate.query(sql, params, rs -> {
            ArrayList<Map<String, String>> objects = new ArrayList<>();
            while (rs.next()) {
                HashMap<String, String> stringStringHashMap = new HashMap<>();
                stringStringHashMap.put("goodId", rs.getString("good_id"));
                stringStringHashMap.put("goodName", rs.getString("good_name"));
                stringStringHashMap.put("goodDesc", rs.getString("good_desc"));
                stringStringHashMap.put("classification", rs.getString("classification"));
                stringStringHashMap.put("cover", rs.getString("cover"));
                stringStringHashMap.put("count", rs.getString("count"));
                objects.add(stringStringHashMap);
            }
            return objects;
        });

        return success(query);
    }

    /**
     * 新增传统工艺
     */
    @PreAuthorize("@ss.hasPermi('system:goods:add')")
    @Log(title = "传统工艺", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TraditionalCraftGoods traditionalCraftGoods)
    {
        return toAjax(traditionalCraftGoodsService.insertTraditionalCraftGoods(traditionalCraftGoods));
    }

    /**
     * 修改传统工艺
     */
    @PreAuthorize("@ss.hasPermi('system:goods:edit')")
    @Log(title = "传统工艺", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TraditionalCraftGoods traditionalCraftGoods)
    {
        return toAjax(traditionalCraftGoodsService.updateTraditionalCraftGoods(traditionalCraftGoods));
    }

    /**
     * 删除传统工艺
     */
    @PreAuthorize("@ss.hasPermi('system:goods:remove')")
    @Log(title = "传统工艺", businessType = BusinessType.DELETE)
	@DeleteMapping("/{goodsIds}")
    public AjaxResult remove(@PathVariable Long[] goodsIds)
    {
        return toAjax(traditionalCraftGoodsService.deleteTraditionalCraftGoodsByGoodsIds(goodsIds));
    }
    
    /**
     * 获取推荐列表
     */
    @Anonymous
    @GetMapping("/recommend")
    public TableDataInfo getRecommendations(
            @RequestParam("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return getDataTable(traditionalCraftGoodsService.recommend(userId, page, size));
    }

    /**
     * 记录用户操作（点赞/点踩）
     * actionType参数应为"LIKE"或"DISLIKE"
     */
    @Anonymous
    @PostMapping("/action")
    public ResponseEntity<?> recordAction(
            @RequestParam("userId") String userId,
            @RequestBody ActionRequest request
    ) {
        traditionalCraftGoodsService.recordAction(userId, request.getMaterialId(), request.getActionType());
        return ResponseEntity.ok().build();
    }
    
    /**
     * 用户行为请求对象
     */
    public static class ActionRequest {
        private Long materialId;
        private String actionType;
        
        public Long getMaterialId() {
            return materialId;
        }
        
        public void setMaterialId(Long materialId) {
            this.materialId = materialId;
        }
        
        public String getActionType() {
            return actionType;
        }
        
        public void setActionType(String actionType) {
            this.actionType = actionType;
        }
    }
}
