package com.xoso.api.controller;

import com.xoso.api.security.dao.DaoAuthenticationProvider;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.user.service.PasswordWithdrawService;
import com.xoso.user.wsdto.*;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.user.service.UserReadService;
import com.xoso.user.service.UserWriteService;
import com.xoso.wallet.data.BalanceData;
import com.xoso.wallet.exception.WalletNotFountException;
import com.xoso.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    private final UserWriteService userWriteService;
    private final UserReadService userReadService;
    private final WalletService walletService;
    private final AuthenticationService authenticationService;
    private final DaoAuthenticationProvider customAuthenticationProvider;
    private final PasswordWithdrawService passwordWithdrawService;

    @Autowired
    public UserController(UserWriteService userWriteService, UserReadService userReadService, WalletService walletService,
                          AuthenticationService authenticationService, DaoAuthenticationProvider customAuthenticationProvider,
                          PasswordWithdrawService passwordWithdrawService) {
        this.userWriteService = userWriteService;
        this.userReadService = userReadService;
        this.walletService = walletService;
        this.authenticationService = authenticationService;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.passwordWithdrawService = passwordWithdrawService;
    }

    @PostMapping()
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreateRequestWsDTO request) {
        var user = this.userWriteService.createUser(request);
        if (user.getStaff() == null) {
            walletService.createWalletDefault(user);
        }
        return response(new ResultBuilder().withEntityId(user.getId()).build());
    }

    @PostMapping("/change-password-request")
    public ResponseEntity<?> requestChangePassword(@RequestBody @Valid ChangePasswordRequestWsDTO request) {
        this.userWriteService.requestChangePassword(request);
        return response(true);
    }

    @PostMapping("/change-password-verify-otp")
    public ResponseEntity<?> requestChangePassword(@RequestBody @Valid ChangePasswordVerifyOTPWsDTO request) {
        this.userWriteService.verifyOTPChangePassword(request);
        return response(true);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getUserDetail() {
        var currentUser = authenticationService.authenticatedUser();
        var userData = this.userReadService.findUserByUsername(currentUser.getUsername());
        return response(userData);
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getUserBalance() {
        var currentUser = authenticationService.authenticatedUser();
        var wallet = this.walletService.getWalletByUsername(currentUser.getUsername());
        if (wallet == null) {
            throw new WalletNotFountException();
        }
        return response(BalanceData.builder().balance(wallet.getBalance()).build());
    }

    @PostMapping("/create-password-withdraw")
    public ResponseEntity<?> createPasswordWithdraw(@RequestBody @Valid PasswordWithdrawCreateRequestWsDTO request) {
        this.validateUser(request.getUsername(), request.getPassword());
        this.passwordWithdrawService.create(request.getUsername(), request.getPasswordWithdraw());
        return response(true);
    }

    @PostMapping("/update-password-withdraw")
    public ResponseEntity<?> updatePasswordWithdraw(@RequestBody @Valid PasswordWithdrawCreateRequestWsDTO request) {
        this.validateUser(request.getUsername(), request.getPassword());
        this.passwordWithdrawService.updatePasswordByUser(request.getUsername(), request.getPasswordWithdraw());
        return response(true);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordWsDTO request) {
        var currentUser = this.authenticationService.authenticatedUser();
        this.validateUser(currentUser.getUsername(), request.getOldPassword());
        this.userWriteService.changePassword(currentUser.getUsername(), request);
        return response(true);
    }

    private void validateUser(String username, String password) {
        var authentication = new UsernamePasswordAuthenticationToken(username, password);
        this.customAuthenticationProvider.authenticate(authentication);
    }
}
