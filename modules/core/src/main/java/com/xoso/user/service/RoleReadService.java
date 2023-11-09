package com.xoso.user.service;

import com.xoso.user.data.RoleData;

import java.util.Collection;

public interface RoleReadService {

    Collection<RoleData> retrieveAll();

    Collection<RoleData> retrieveAllActiveRoles();

    RoleData retrieveOne(Long roleId);

    Collection<RoleData> retrieveAppUserRoles(Long userId);
}
