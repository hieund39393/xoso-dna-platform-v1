package com.xoso.user.service.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.user.exception.PermissionNotFoundException;
import com.xoso.user.exception.RoleNotFoundException;
import com.xoso.user.model.Permission;
import com.xoso.user.model.Role;
import com.xoso.user.repository.PermissionRepository;
import com.xoso.user.repository.RoleRepository;
import com.xoso.user.service.RoleWriteService;
import com.xoso.user.wsdto.RoleCreateRequestWsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class RoleWriteServiceImpl implements RoleWriteService {

    private final static Logger logger = LoggerFactory.getLogger(RoleWriteServiceImpl.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RoleWriteServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional
    @Override
    public ResultBuilder createRole(RoleCreateRequestWsDTO request) {
        try {

            final Role entity = Role.fromJson(request);
            this.roleRepository.save(entity);

            return new ResultBuilder().withEntityId(entity.getId()).build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(request.getName(), dve.getMostSpecificCause(), dve);
            return ResultBuilder.empty();
        }catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause()) ;
            handleDataIntegrityIssues(request.getName(), throwable, dve);
            return ResultBuilder.empty();
        }
    }

    @Transactional
    @Override
    public void deleteRole(Long roleId) {
        try {
            final Role role = this.roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException(roleId));

            final Integer count = this.roleRepository.getCountOfRolesAssociatedWithUsers(roleId);
            if (count > 0) { throw new AbstractPlatformException("error.msg.role.associated.with.users.deleted", "", roleId); }

            this.roleRepository.delete(role);
        } catch (final DataIntegrityViolationException e) {
            throw new AbstractPlatformException("error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource: " + e.getMostSpecificCause());
        }
    }

    @Override
    public void disableRole(Long roleId) {
        try {
            final Role role = this.roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException(roleId));

            final Integer count = this.roleRepository.getCountOfRolesAssociatedWithUsers(roleId);
            if (count > 0) { throw new AbstractPlatformException("error.msg.role.associated.with.users.disabled", "", roleId); }

            role.disableRole();
            this.roleRepository.save(role);

        } catch (final DataIntegrityViolationException e) {
            throw new AbstractPlatformException("error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource: " + e.getMostSpecificCause());
        }
    }

    @Override
    public void enableRole(Long roleId) {
        try {
            final Role role = this.roleRepository.findById(roleId)
                    .orElseThrow(() -> new RoleNotFoundException(roleId));
            role.enableRole();
            this.roleRepository.save(role);

        } catch (final DataIntegrityViolationException e) {
            throw new AbstractPlatformException("error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource: " + e.getMostSpecificCause());
        }
    }

    @Override
    @Transactional
    public ResultBuilder updateRolePermissions(Long roleId, Map<String, Boolean> permissionData) {

        if (permissionData.isEmpty()) {
            throw new AbstractPlatformException("error.msg.role.permissions.is.required",
                    "Permissions is required");
        }

        final Role role = this.roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException(roleId));

        final Collection<Permission> allPermissions = this.permissionRepository.findAll();

        final Map<String, Object> changes = new HashMap<>();
        final Map<String, Boolean> changedPermissions = new HashMap<>();
        for (final String permissionCode : permissionData.keySet()) {
            final boolean isSelected = permissionData.get(permissionCode).booleanValue();

            final Permission permission = findPermissionByCode(allPermissions, permissionCode);
            final boolean changed = role.updatePermission(permission, isSelected);
            if (changed) {
                changedPermissions.put(permissionCode, isSelected);
            }
        }

        if (!changedPermissions.isEmpty()) {
            changes.put("permissions", changedPermissions);
            this.roleRepository.save(role);
        }

        return new ResultBuilder() //
                .withEntityId(roleId) //
                .with(changes) //
                .build();
    }

    private Permission findPermissionByCode(final Collection<Permission> allPermissions, final String permissionCode) {

        if (allPermissions != null) {
            for (final Permission permission : allPermissions) {
                if (permission.hasCode(permissionCode)) { return permission; }
            }
        }
        throw new PermissionNotFoundException(permissionCode);
    }

    private void handleDataIntegrityIssues(final String name, final Throwable realCause, final Exception dve) {

        if (realCause.getMessage().contains("unq_name")) {

            throw new AbstractPlatformException("error.msg.role.duplicate.name", "Role with name `" + name + "` already exists",
                    "name", name);
        }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new AbstractPlatformException("error.msg.role.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final Exception dve) {
        logger.error("Error occured.", dve);
    }
}
