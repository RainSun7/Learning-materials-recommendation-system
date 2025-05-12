package cn.project.system.domain;

import cn.project.common.annotation.Excel;
import cn.project.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 书籍对象 sys_book
 * 
 * @author default
 * @date 2025-03-08
 */
@Data
public class UserAddresses extends BaseEntity
{
    private static final long serialVersionUID = 1L;
//id               bigint auto_increment comment '主键ID'
//        primary key,
//    user_id          bigint                               not null comment '用户ID',
//    receiver_name    varchar(50)                          not null comment '收货人姓名',
//    receiver_phone   varchar(20)                          not null comment '收货人电话',
//    province         varchar(50)                          not null comment '省份',
//    city             varchar(50)                          not null comment '城市',
//    district         varchar(50)                          not null comment '区县',
//    detailed_address varchar(255)                         not null comment '详细地址（街道/门牌号）',
//    postal_code      varchar(20)                          null comment '邮政编码',
//    is_default       tinyint(1) default 0                 null comment '是否默认地址：0-否，1-是',
//    is_deleted       tinyint(1) default 0                 null comment '逻辑删除：0-未删除，1-已删除',
//    created_at       datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
//    updated_at       datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
    /** 主键ID */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 收货人姓名 */
    @Excel(name = "收货人姓名")
    private String receiverName;

    /** 收货人电话 */
    @Excel(name = "收货人电话")
    private String receiverPhone;

    /** 省份 */
    @Excel(name = "省份")
    private String province;

    /** 城市 */
    @Excel(name = "城市")
    private String city;

    /** 区县 */
    @Excel(name = "区县")
    private String district;

    /** 详细地址（街道/门牌号） */
    @Excel(name = "详细地址（街道/门牌号）")
    private String detailedAddress;

    /** 邮政编码 */
    @Excel(name = "邮政编码")
    private String postalCode;

    /** 是否默认地址：0-否，1-是 */
    @Excel(name = "是否默认地址：0-否，1-是")
    private Integer isDefault;

    /** 逻辑删除：0-未删除，1-已删除 */
    @Excel(name = "逻辑删除：0-未删除，1-已删除")
    private Integer isDeleted;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
}
