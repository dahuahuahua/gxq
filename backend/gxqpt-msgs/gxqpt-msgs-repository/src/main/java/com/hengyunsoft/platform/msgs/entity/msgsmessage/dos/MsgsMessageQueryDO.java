package com.hengyunsoft.platform.msgs.entity.msgsmessage.dos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(value = "MsgsMessageQuery", description = "消息分页查询参数对象")
public class MsgsMessageQueryDO {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;
    /**
     * 渠道类型
     */
    @ApiModelProperty(value = "渠道类型")
    private Long channelType;

    /**
     * 渠道名称
     */
    @ApiModelProperty(value = "渠道名称")
    private String name;
    /**
     * 消息状态
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "消息状态")
    private Long msgStatus = 0L;

    /**
     *创建开始时间
     */
    @ApiModelProperty(value = "创建开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTimeStart;

    /**
     *创建截止时间
     */
    @ApiModelProperty(value = "创建截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTimeEnd;

}
