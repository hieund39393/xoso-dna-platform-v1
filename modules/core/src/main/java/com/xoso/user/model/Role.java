package com.xoso.user.model;

import lombok.Getter;
import lombok.Setter;
import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.user.data.RoleData;
import com.xoso.user.wsdto.RoleCreateRequestWsDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "role", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "unq_name") })
public class Role extends AbstractAuditableCustom implements Serializable {

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "is_disabled", nullable = false)
    private Boolean disabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();


    protected Role() {
        //
    }

    public Role(final String name, final String description) {
        this.name = name.trim();
        this.description = description.trim();
        this.disabled = false;
    }

    public static Role fromJson(RoleCreateRequestWsDTO request) {
        return new Role(request.getName(), request.getDescription());
    }

    public boolean updatePermission(final Permission permission, final boolean isSelected) {
        boolean changed = false;
        if (isSelected) {
            changed = addPermission(permission);
        } else {
            changed = removePermission(permission);
        }

        return changed;
    }

    public RoleData toData() {
        return new RoleData(getId(), this.name, this.description, this.disabled);
    }

    private boolean addPermission(final Permission permission) {
        return this.permissions.add(permission);
    }

    private boolean removePermission(final Permission permission) {
        return this.permissions.remove(permission);
    }

    public Collection<Permission> getPermissions() {
        return this.permissions;
    }

    public boolean hasPermissionTo(final String permissionCode) {
        boolean match = false;
        for (final Permission permission : this.permissions) {
            if (permission.hasCode(permissionCode)) {
                match = true;
                break;
            }
        }
        return match;
    }

    public String getName() {
        return this.name;
    }

    public void disableRole() {
        this.disabled = true;
    }

    public Boolean isDisabled() {
        return this.disabled;
    }

    public void enableRole() {
        this.disabled = false;
    }

    public Boolean isEnabled() {
        return this.disabled;
    }
}
