package com.xoso.api.controller;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.wallet.model.WalletTransactions;
import com.xoso.wallet.service.WalletTransactionReadService;
import com.xoso.wallet.service.WalletTransactionService;
import com.xoso.wallet.wsdto.WithdrawRequestWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/wallet-transactions")
public class WalletTransactionController extends BaseController {

    @Autowired
    private WalletTransactionService walletTransactionService;
    @Autowired
    private WalletTransactionReadService walletTransactionReadService;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/deposit/template")
    public ResponseEntity<?> depositTemplate() {
        return response(this.walletTransactionService.depositTemplate());
    }

    @PostMapping("/deposit/confirm")
    public ResponseEntity<?> confirmDeposit() {
        this.walletTransactionService.confirmDeposit();
        return response("success");
    }

    @PostMapping("/withdraw/template")
    public ResponseEntity<?> withdrawRequest(@Valid @RequestBody WithdrawRequestWsDTO request) {
        return response(this.walletTransactionService.withdrawRequest(request));
    }

    @GetMapping()
    public ResponseEntity<?> re(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                @RequestParam(value = "status", required = false) String status,
                                @RequestParam(value = "fromDate", required = false) String dateFromStr,
                                @RequestParam(value = "toDate", required = false) String dateToStr) {
        var currentUser = this.authenticationService.authenticatedUser();
        var format = new SimpleDateFormat("dd/MM/yyyy");
        Date fromDate = null;
        Date toDate = null;
        try {
            if (StringUtils.isNotBlank(dateFromStr)) {
                fromDate = format.parse(dateFromStr);
            }
            if (StringUtils.isNotBlank(dateToStr)) {
                toDate = format.parse(dateFromStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        var searchParams = new SearchParameters(pageNumber, pageSize, status);
        searchParams.setFromDate(fromDate);
        searchParams.setToDate(toDate);
        searchParams.setUserId(currentUser.getId());
        return response(this.walletTransactionReadService.retrieveAll(searchParams));
    }

}
