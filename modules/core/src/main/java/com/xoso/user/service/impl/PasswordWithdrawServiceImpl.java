package com.xoso.user.service.impl;

import com.xoso.user.exception.PasswordWithdrawHasNotCreatedException;
import com.xoso.user.exception.UserNotFoundException;
import com.xoso.user.model.PasswordWithdraw;
import com.xoso.user.repository.AppUserRepository;
import com.xoso.user.repository.PasswordWithdrawRepository;
import com.xoso.user.service.PasswordWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;

@Service
public class PasswordWithdrawServiceImpl implements PasswordWithdrawService {

    private final AppUserRepository appUserRepository;
    private final PasswordWithdrawRepository passwordWithdrawRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public PasswordWithdrawServiceImpl(AppUserRepository appUserRepository, PasswordWithdrawRepository passwordWithdrawRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordWithdrawRepository = passwordWithdrawRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void create(String username, String passwordValue) {
        var appUser = this.appUserRepository.findAppUserByName(username);
        if (appUser == null) {
            throw new UserNotFoundException(username);
        }
        var passwordWithdraw = PasswordWithdraw.builder()
                .password(this.bCryptPasswordEncoder.encode(passwordValue))
                .user(appUser)
                .lastTimePasswordUpdated(LocalDateTime.now()).build();
        this.passwordWithdrawRepository.saveAndFlush(passwordWithdraw);
    }

    @Override
    public void updatePasswordByUser(String username, String passwordWithdrawUpdate) {
        var appUser = this.appUserRepository.findAppUserByName(username);
        if (appUser == null) {
            throw new UserNotFoundException(username);
        }
        var passwordWithdraws = this.passwordWithdrawRepository.findByUser(appUser);
        if (CollectionUtils.isEmpty(passwordWithdraws)) {
            throw new PasswordWithdrawHasNotCreatedException();
        }
        var passwordWithdraw = passwordWithdraws.get(0);
        passwordWithdraw.setPassword(this.bCryptPasswordEncoder.encode(passwordWithdrawUpdate));
        passwordWithdraw.setLastTimePasswordUpdated(LocalDateTime.now());
        this.passwordWithdrawRepository.save(passwordWithdraw);
    }
}
