package com.xoso.user.repository;

import com.xoso.user.model.AppUser;
import com.xoso.user.model.AppUserOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserOTPRepository extends JpaRepository<AppUserOTP, Long> {
    List<AppUserOTP> findByUserAndOtp(AppUser user, String otp);
}
