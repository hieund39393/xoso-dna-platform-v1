package com.xoso.user.data;

import lombok.Data;

import java.util.Collection;
@Data
public class RolePermissionsData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String name;
    @SuppressWarnings("unused")
    private final String description;
    @SuppressWarnings("unused")
    private final Boolean disabled;

    @SuppressWarnings("unused")
    private final Collection<PermissionData> permissionUsageData;

    public RolePermissionsData(final Long id, final String name, final String description, final Boolean disabled,
            final Collection<PermissionData> permissionUsageData) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.disabled = disabled;
        this.permissionUsageData = permissionUsageData;
    }
}