package com.xoso.user.service.impl;

import com.xoso.agency.model.AgencyRequest;
import com.xoso.agency.model.AgencyStatus;
import com.xoso.bank.model.Bank;
import com.xoso.bank.service.ClientBankAccountService;
import com.xoso.captcha.service.CaptchaService;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.exception.BadRequestException;
import com.xoso.infrastructure.core.exception.ExceptionCode;
import com.xoso.infrastructure.core.exception.InvalidParamException;
import com.xoso.user.model.AppUserOTP;
import com.xoso.user.repository.AppUserOTPRepository;
import com.xoso.user.wsdto.*;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.infrastructure.security.service.PlatformPasswordEncoder;
import com.xoso.staff.repository.StaffRepositoryWrapper;
import com.xoso.user.exception.*;
import com.xoso.user.model.AppUser;
import com.xoso.user.model.Role;
import com.xoso.user.repository.AppUserRepository;
import com.xoso.user.repository.RoleRepository;
import com.xoso.user.service.UserWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.regex.Pattern;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class UserWriteServiceImpl implements UserWriteService {

    private static final Logger logger = LoggerFactory.getLogger(UserWriteServiceImpl.class);

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final StaffRepositoryWrapper staffRepositoryWrapper;
    private final PlatformPasswordEncoder platformPasswordEncoder;
    private final AppUserOTPRepository appUserOTPRepository;
    private final ClientBankAccountService clientBankAccountService;

    private final CaptchaService captchaService;

    private static final long LOCK_TIME_DURATION = 24 * 60 * 60;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static boolean isValidUsername(String username) {
        String regex = "^[^!@#$%^&*?<>{}/.,+=()\"']+$";
        return Pattern.matches(regex, username);
    }
    @Override
    @Transactional
    public AppUser createUser(UserCreateRequestWsDTO request) {
        try {
//            var staffId = request.getStaffId();
//            Staff linkedStaff = null;
//            if (staffId != null) {
//                linkedStaff = this.staffRepositoryWrapper.findOneWithNotFoundDetection(staffId);
//            }
            // validate captcha
            if(!isValidUsername(request.getUsername()))
                throw new BadRequestException(ExceptionCode.USERNAME_INVALID);
            captchaService.validateCaptcha(request.getCaptchaId(),request.getCaptchaValue());

            final var username = request.getUsername();
            var checkUserName = this.appUserRepository.findAppUserByName(username);
            if (checkUserName != null) {
                throw new UserAlreadyExistsException();
            }

            var checkMobileNo = this.appUserRepository.findAppUserByMobileNo(request.getMobileNo());
            if (!CollectionUtils.isEmpty(checkMobileNo)) {
                throw new PhoneNumberAlreadyRegisteredException();
            }

            var allRoles = assembleSetOfRoles(null);
            var appUser = AppUser.fromRequest(null, allRoles, request);
            final String encodePassword = this.platformPasswordEncoder.encode(appUser);
            appUser.updatePassword(encodePassword);

            //set dai ly
            if(request.getAgency() != null && !request.getAgency().isEmpty()) {
                AppUser agency = this.appUserRepository.findAppUserByName(request.getAgency());
                if(agency != null && agency.getAgencyStatus() == AgencyStatus.APPROVED)
                    appUser.setAgencyId(agency.getId());
            }

            this.appUserRepository.saveAndFlush(appUser);
            return appUser;

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(request, dve.getMostSpecificCause(), dve);
            return null;
        } catch (final JpaSystemException | PersistenceException | AuthenticationServiceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleDataIntegrityIssues(request, throwable, dve);
            return null;
        }
    }

    @Override
    public void requestChangePassword(ChangePasswordRequestWsDTO request) {
        var userName = request.getUsername();
        var phoneNumber = request.getPhoneNumber();
        var user = this.appUserRepository.findAppUserByName(userName);
        if (user == null) {
            throw new UserNotFoundException(userName);
        }

        if (!phoneNumber.equals(user.getMobileNo())) {
            throw new InvalidParamException(phoneNumber);
        }

        // generate otp
        var userOtp = AppUserOTP.builder()
                .user(user)
                .otp("123456")
                .sentDate(LocalDateTime.now())
                .expiredDate(LocalDateTime.now().plusMinutes(1))
                .build();
        this.appUserOTPRepository.saveAndFlush(userOtp);

        // sent otp
    }

    @Override
    @Transactional
    public void verifyOTPChangePassword(ChangePasswordVerifyOTPWsDTO request) {
        var userName = request.getUsername();
        var password = request.getPassword();
        var otp = request.getOtp();
        var appUser = this.appUserRepository.findAppUserByName(userName);
        if (appUser == null) {
            throw new UserNotFoundException(userName);
        }
        // TODO: find by one
        var userOTPs = this.appUserOTPRepository.findByUserAndOtp(appUser, otp);
        if (CollectionUtils.isEmpty(userOTPs)) {
            throw new InvalidParamException(otp);
        }
//        var userOTP = userOTPs.get(0);
//        var expiredDate = userOTP.getExpiredDate();
//        if (LocalDateTime.now().isBefore(expiredDate)) {
//            throw new OtpExpiredException();
//        }
        // update password
        appUser.setPassword(password);
        final String encodePassword = this.platformPasswordEncoder.encode(appUser);
        appUser.updatePassword(encodePassword);

        this.appUserRepository.save(appUser);
    }

    @Override
    public void changePassword(String username, ChangePasswordWsDTO request) {
        var password = request.getNewPassword();
        var appUser = this.appUserRepository.findAppUserByName(username);
        if (appUser == null) {
            throw new UserNotFoundException(username);
        }
        appUser.setPassword(password);
        final String encodePassword = this.platformPasswordEncoder.encode(appUser);
        appUser.updatePassword(encodePassword);
        this.appUserRepository.save(appUser);
    }

    @Override
    public boolean increaseFailedAttempts(String username) {
        var appUser = this.appUserRepository.findAppUserByName(username);
        if (appUser == null) {
            throw new UserNotFoundException(username);
        } else {
            if (appUser.getFailedAttempt() < MAX_FAILED_ATTEMPTS - 1) {
                appUser.setFailedAttempt(appUser.getFailedAttempt() + 1);
                this.appUserRepository.save(appUser);
            } else {
                this.lock(appUser);
            }
            if (!appUser.isAccountNonLocked()) {
                return true;
                // Your account has been locked due to 5 failed attempts. It will be unlocked after 24 hours
            }
        }
        return false;
    }

    @Override
    public void resetFailedAttempts(String username) {
        var appUser = this.appUserRepository.findAppUserByName(username);
        if (appUser == null) {
            throw new UserNotFoundException(username);
        }
        appUser.setFailedAttempt(0);
        appUser.setAccountNonLocked(true);
        appUser.setLockTime(null);
        appUserRepository.save(appUser);
    }

    @Override
    public boolean unlockWhenTimeExpired(AppUser appUser) {
        var lockTime = appUser.getLockTime();
        var currentTime = LocalDateTime.now();

        if (lockTime.plusSeconds(LOCK_TIME_DURATION).isBefore(currentTime)) {
            appUser.setAccountNonLocked(true);
            appUser.setLockTime(null);
            appUser.setFailedAttempt(0);
            appUserRepository.save(appUser);
            return true;
        }
        return false;
    }

    @Override
    public void lock(AppUser appUser) {
        appUser.setAccountNonLocked(true);
        appUser.setLockTime(LocalDateTime.now());
        appUserRepository.save(appUser);
    }

    @Override
    public void unlock(Long userId) {
        var appUser = this.appUserRepository.queryFindAppUserById(userId);
        if (appUser == null) {
            throw new UserNotFoundException();
        }
        appUser.setAccountNonLocked(true);
        appUser.setLockTime(null);
        appUser.setFailedAttempt(0);
        appUserRepository.save(appUser);
    }

    private void handleDataIntegrityIssues(final UserCreateRequestWsDTO request, final Throwable realCause, final Exception dve) {
        System.out.println(realCause.getMessage());
        if (realCause.getMessage().contains("username_org")) {
            final StringBuilder defaultMessageBuilder = new StringBuilder("User with username ").append(request.getUsername())
                .append(" already exists.");
            throw new AbstractPlatformException("error.msg.user.duplicate.username", defaultMessageBuilder.toString(), "username",
                request.getUsername());
        }
        logger.error("Error occured.", dve);
        throw new AbstractPlatformException("error.msg.unknown.data.integrity.issue", "Unknown data integrity issue with resource.");
    }

    private Set<Role> assembleSetOfRoles(final List<Long> rolesArray) {

        final Set<Role> allRoles = new HashSet<>();

        if (!ObjectUtils.isEmpty(rolesArray)) {
            for (final Long roleId : rolesArray) {
                final Role role = this.roleRepository.findById(roleId)
                        .orElseThrow(() -> new RoleNotFoundException(roleId));
                allRoles.add(role);
            }
        } else {
            final Role role = this.roleRepository.getRoleByName("CLIENT");
            if (role != null) {
                allRoles.add(role);
            }
        }

        return allRoles;
    }

    @Override
    public void approveAgency(AgencyRequest request, Bank bank) {
        var appUser = this.appUserRepository.queryFindAppUserById(request.getUserId());
        if (appUser == null) {
            throw new UserNotFoundException();
        }
        appUser.setFullName(request.getFullName());
        appUser.setEmail(request.getEmail());
        appUser.setAgencyStatus(AgencyStatus.APPROVED);
        appUser.setAgencyLevel(1);
        appUserRepository.save(appUser);
        //luu thong tin bank
        clientBankAccountService.updateBankAccount(appUser, bank, request.getAccountName(), request.getAccountNumber());
    }

    @Override
    public void registerAgency(Long userId) {
        var appUser = this.appUserRepository.queryFindAppUserById(userId);
        if (appUser == null) {
            throw new UserNotFoundException();
        }
        appUser.setAgencyStatus(AgencyStatus.PROCESSING);
        appUserRepository.save(appUser);
    }

    @Override
    public void rejectAgency(AgencyRequest request) {
        var appUser = this.appUserRepository.queryFindAppUserById(request.getUserId());
        if (appUser == null) {
            throw new UserNotFoundException();
        }
        appUser.setAgencyStatus(AgencyStatus.REJECTED);
        appUserRepository.save(appUser);
    }

    @Override
    public ResultBuilder updateUser(Long userId, UserUpdateRequestWsDTO request) {
        var appUser = this.appUserRepository.queryFindAppUserById(userId);
        if (appUser == null) {
            throw new UserNotFoundException();
        }
        boolean changed = false;
        if (StringUtils.isNotBlank(request.getFullName()) && !request.getFullName().equals(appUser.getFullName())) {
            appUser.setFullName(request.getFullName());
            changed = true;
        }
        if (StringUtils.isNotBlank(request.getMobileNo()) && !request.getMobileNo().equals(appUser.getMobileNo())) {
            appUser.setMobileNo(request.getMobileNo());
            changed = true;
        }
        if (StringUtils.isNotBlank(request.getEmail()) && !request.getEmail().equals(appUser.getEmail())) {
            appUser.setEmail(request.getEmail());
            changed = true;
        }
        if (changed) {
            appUserRepository.save(appUser);
        }
        return new ResultBuilder().withEntityId(userId).build();
    }
}
