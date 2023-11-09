package com.xoso.user.repository;

import com.xoso.user.model.AppUser;
import com.xoso.user.model.PasswordWithdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordWithdrawRepository extends JpaRepository<PasswordWithdraw, Long> {

    List<PasswordWithdraw> findByUser(AppUser user);
}
