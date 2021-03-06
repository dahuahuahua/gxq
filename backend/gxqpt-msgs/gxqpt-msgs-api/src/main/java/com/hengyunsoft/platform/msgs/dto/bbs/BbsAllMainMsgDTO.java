package com.hengyunsoft.platform.msgs.dto.bbs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 消息主表DTO
 */
@Data
@ApiModel(value = "BbsAllMainMsgDTO", description = "消息主表DTO")
public class BbsAllMainMsgDTO implements Serializable {


    /**
     * 业务id
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "业务id")
    private String bizId;

    /**
     * 业务类型
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "业务类型")
    private String bizType;

    /**
     * 客户端对此消息的唯一标示,   若客户端对此消息进行了存储，这里推荐是那里的存储id
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "客户端标识")
    private String clientFlag;

    /**
     * 简要类型，粗粒度的类型
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "简要类型，粗粒度的类型")
    private String briefTpye;

    /**
     * 详细类型
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "详细类型")
    private String detailedType;

    /**
     * 详细类型描述
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "详细类型描述")
    private String detailedTypeDesc;

    /**
     * 标题
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 内容
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "内容")
    private String content;

    /**
     * 作者名称
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "作者名称")
    private String senderName;

    /**
     * 处理地址
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "处理地址")
    private String handlerUrl;

    /**
     * 处理参数
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "处理参数")
    private String handlerParams;

    /**
     * 是否单人处理
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "是否单人处理")
    private Boolean singleHandleIs;

    /**
     * 是否需要操作
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "是否需要操作")
    private Boolean handlerNeedIs;

    /**
     * 应用id
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "应用id")
    private String appId;

    /**
     * 应用名称
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "应用名称")
    private String appName;

    /**
     * 应用角色编码
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "应用角色编码：调用保存只需要查看类的信息到指定的角色，保存只需要处理类的消息到指定的角色这两个接口情况下，必填")
    private String roleCode;

    /**
     * 应用角色ID
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "应用角色ID：调用保存只需要查看类的信息到指定的角色，保存只需要处理类的消息到指定的角色这两种情况下，必填")
    private String roleAppId;


    /**
     * 消息查看用户列表
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "消息查看用户列表：调用保存消息到指定的用户集合接口情况下，选填")
    private List<Long> lookUserIds;

    /**
     * 消息处理用户列表
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "消息处理用户列表：调用保存消息到指定的用户集接情况下，选填")
    private List<Long> handlerUserIds;

    private static final long serialVersionUID = 1L;
}