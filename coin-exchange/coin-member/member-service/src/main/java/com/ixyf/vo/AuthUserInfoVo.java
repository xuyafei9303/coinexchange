package com.ixyf.vo;

import com.ixyf.domain.User;
import com.ixyf.domain.UserAuthAuditRecord;
import com.ixyf.domain.UserAuthInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "用户认证详情展示的信息model")
public class AuthUserInfoVo implements Serializable {
    private static final long serialVersionUID = -6679237817401642649L;

    /**
     * 用户信息
     */
    @ApiModelProperty(value = "用户信息")
    private User user;

    /**
     * 用户认证信息
     */
    @ApiModelProperty(value = "用户认证信息")
    private List<UserAuthInfo> authInfos;

    /**
     * 用户审核信息
     */
    @ApiModelProperty(value = "用户审核信息")
    private List<UserAuthAuditRecord> userAuthAuditRecords;
}
