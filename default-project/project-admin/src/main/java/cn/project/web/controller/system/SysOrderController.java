package cn.project.web.controller.system;

import cn.project.common.annotation.Log;
import cn.project.common.core.controller.BaseController;
import cn.project.common.core.domain.AjaxResult;
import cn.project.common.core.domain.entity.SysUser;
import cn.project.common.core.page.TableDataInfo;
import cn.project.common.enums.BusinessType;
import cn.project.system.domain.SysBook;
import cn.project.system.domain.SysOrder;
import cn.project.system.domain.SysOrderItem;
import cn.project.system.mapper.SysOrderItemMapper;
import cn.project.system.service.ISysOrderService;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单Controller
 */
@RestController
@RequestMapping("/system/order")
public class SysOrderController extends BaseController {
    
    @Autowired
    private ISysOrderService orderService;
    @Autowired
    private SysOrderItemMapper orderItemMapper;
    /**
     * 查询订单列表
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/list")
    public TableDataInfo list(SysOrder order) {
        startPage();
        List<SysOrder> list = orderService.selectOrderList(order);
        list.forEach(e -> {
            e.setOrderItems(orderItemMapper.selectOrderItemList(new SysOrderItem(e.getOrderId())));
        });
        return getDataTable(list);
    }

    /**
     * 获取订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId) {
        return success(orderService.selectOrderById(orderId));
    }

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public AjaxResult add(@RequestBody SysOrder order) {
        //使用namedJdbcTemplate 查询用户信息
        String sql = "SELECT * FROM sys_user WHERE user_id = :userId";
        Map<String, Object> params = ImmutableMap.of("userId", order.getUserId());
        List<SysUser> list = namedJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            SysUser book = new SysUser();
            book.setUserId(rs.getLong("user_id"));
            book.setUserName(rs.getString("user_name"));
            book.setPhonenumber(rs.getString("phonenumber"));
            return book;
        });
        if (list.isEmpty()) {
            return AjaxResult.error("用户不存在");
        }
        SysUser user = list.get(0);
        //设置订单信息
        order.setUserName(user.getUserName());
        order.setUserPhone(user.getPhonenumber());
        return success(orderService.createOrder(order));
    }

    /**
     * 更新订单状态
     */
    @PreAuthorize("@ss.hasPermi('system:order:edit')")
    @Log(title = "订单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult updateStatus(@RequestParam String orderNo, @RequestParam Integer status) {
        return toAjax(orderService.updateOrderStatus(orderNo, status));
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public AjaxResult cancel(@PathVariable String orderNo) {
        return toAjax(orderService.cancelOrder(orderNo));
    }

    /**
     * 取消订单
     */
    @PostMapping("/pay/{orderNo}")
    public AjaxResult pay(@PathVariable String orderNo) {
        return toAjax(orderService.payOrder(orderNo));
    }

    /**
     * 确认收货
     */
    @PostMapping("/confirm/{orderNo}")
    public AjaxResult confirm(@PathVariable String orderNo) {
        return toAjax(orderService.confirmOrder(orderNo));
    }

    /**
     * 评价订单
     */
    @PostMapping("/comment/{orderNo}")
    public AjaxResult comment(@PathVariable String orderNo, @RequestBody Map<String, String> params) {
        String remark = params.get("remark");
        return toAjax(orderService.commentOrder(orderNo, remark));
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/user/{userId}")
    public AjaxResult getUserOrders(@PathVariable Long userId) {
        List<SysOrder> list = orderService.selectOrdersByUserId(userId);
        return success(list);
    }
} 