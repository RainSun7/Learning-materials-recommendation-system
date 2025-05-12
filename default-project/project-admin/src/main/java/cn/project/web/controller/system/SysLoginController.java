package cn.project.web.controller.system;

import java.util.List;
import java.util.Set;

import cn.project.common.core.domain.R;
import cn.project.common.core.redis.RedisCache;
import cn.project.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import cn.project.common.constant.Constants;
import cn.project.common.core.domain.AjaxResult;
import cn.project.common.core.domain.entity.SysMenu;
import cn.project.common.core.domain.entity.SysUser;
import cn.project.common.core.domain.model.LoginBody;
import cn.project.common.core.domain.model.LoginUser;
import cn.project.common.utils.SecurityUtils;
import cn.project.framework.web.service.SysLoginService;
import cn.project.framework.web.service.SysPermissionService;
import cn.project.framework.web.service.TokenService;
import cn.project.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author default
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private RedisCache redisCache;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        LoginUser loginUser = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        String token = tokenService.createToken(loginUser);
        ajax.put(Constants.TOKEN, token);
        ajax.put("userId", loginUser.getUserId());
        String remark = loginUser.getUser().getRemark();
        ajax.put("role",remark);
        //调用redis 存储当天访问总次数，key为 visitsNumber value 为map类型  key:yyyy-MM-dd value:访问次数
        String date = DateUtils.getDate();
        String key = "visitsNumber";
        Integer value = redisCache.getCacheMapValue(key, date);
        if (value == null) {
            redisCache.setCacheMapValue(key, date, 1);
        } else {
            int count = value + 1;
            redisCache.setCacheMapValue(key, date, count);
        }
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions))
        {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
