package com.xoso.user.service;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.user.wsdto.RoleCreateRequestWsDTO;

import java.util.Map;

public interface RoleWriteService {
    ResultBuilder createRole(RoleCreateRequestWsDTO request);
    void deleteRole(Long roleId);
    void disableRole(Long roleId);
    void enableRole(Long roleId);
    ResultBuilder updateRolePermissions(Long roleId, Map<String, Boolean> permissionData);
}
