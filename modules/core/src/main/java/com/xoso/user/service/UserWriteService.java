package com.xoso.user.service;

import com.xoso.agency.model.AgencyRequest;
import com.xoso.bank.model.Bank;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.user.model.AppUser;
import com.xoso.user.wsdto.*;

public interface UserWriteService {
    AppUser createUser(UserCreateRequestWsDTO request);
    void requestChangePassword(ChangePasswordRequestWsDTO request);
    void verifyOTPChangePassword(ChangePasswordVerifyOTPWsDTO request);
    void changePassword(String username, ChangePasswordWsDTO request);
    boolean increaseFailedAttempts(String username);
    void resetFailedAttempts(String username);
    boolean unlockWhenTimeExpired(AppUser user);
    void lock(AppUser user);
    void unlock(Long userId);
    void approveAgency(AgencyRequest request, Bank bank);
    void registerAgency(Long userId);
    void rejectAgency(AgencyRequest request);
    ResultBuilder updateUser(Long userId, UserUpdateRequestWsDTO request);
}
