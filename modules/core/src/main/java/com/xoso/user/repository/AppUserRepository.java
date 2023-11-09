package com.xoso.user.repository;

import com.xoso.infrastructure.security.repository.PlatformUserRepository;
import com.xoso.staff.model.Staff;
import com.xoso.user.model.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser>, PlatformUserRepository {

    @Query("select user from AppUser user where user.id = :id")
    AppUser queryFindAppUserById(@Param("id") Long id);

    @Query("select user from AppUser user where user.username = :username")
    AppUser findAppUserByName(@Param("username") String username);

    @Query("select user from AppUser user where user.staff = :staff and user.enabled = true")
    AppUser findAppUserByStaff(@Param("staff") Staff staff);

    @Query("select user from AppUser user where user.mobileNo = :mobileNo")
    List<AppUser> findAppUserByMobileNo(@Param("mobileNo") String mobileNo);

    @Query("select user from AppUser user where user.agencyId = :agencyId")
    Page<AppUser> findAllAgencyUser(@Param("agencyId" )Long agencyId,Pageable pageable);
}
