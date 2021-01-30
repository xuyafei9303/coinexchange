package com.ixyf.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@ApiModel(value = "接受角色和权限数据")
public class RolePrivilegesParam {

    @ApiModelProperty(value = "角色的id")
    private Long roleId;

    @ApiModelProperty(value = "角色包含的权限列表")
    private List<Long> privilegeIds = Collections.emptyList();
}
