package com.xoso.user.repository;

import com.xoso.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository  extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role>  {

    @Query("SELECT COUNT(a) FROM AppUser a JOIN a.roles r WHERE r.id = :roleId AND a.deleted = false")
    Integer getCountOfRolesAssociatedWithUsers(@Param("roleId") Long roleId);

    @Query("SELECT role FROM Role role WHERE role.name = :name")
    Role getRoleByName(@Param("name") String name);
}
