package com.xoso.controller;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.wallet.model.TransactionType;
import com.xoso.wallet.model.WalletTransactionStatus;
import com.xoso.wallet.service.WalletTransactionReadService;
import com.xoso.wallet.service.WalletTransactionService;
import com.xoso.wallet.wsdto.ApproveTransactionRequestWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("wallet-transactions")
public class WalletTransactionController {

    private final WalletTransactionReadService transactionReadService;
    private final WalletTransactionService transactionService;

    @Autowired
    public WalletTransactionController(WalletTransactionReadService transactionReadService, WalletTransactionService transactionService) {
        this.transactionReadService = transactionReadService;
        this.transactionService = transactionService;
    }


    @GetMapping
    public String search(Model model,
                         @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                         @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                         @RequestParam(value = "searchValue", required = false) String searchValue,
                         @RequestParam(value = "status", required = false) List<String> status,
                         @RequestParam(value = "dateFrom", required = false) String dateFromStr,
                         @RequestParam(value = "dateTo", required = false) String dateToStr) {

        var format = new SimpleDateFormat("dd/MM/yyyy");

        var calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        var dateFrom = calendar.getTime();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        var dateTo = calendar.getTime();
        try {
            if (StringUtils.isNotBlank(dateFromStr)) {
                dateFrom = format.parse(dateFromStr);
            } else {
                dateFromStr = format.format(dateFrom);
            }
            if (StringUtils.isNotBlank(dateToStr)) {
                dateTo = format.parse(dateToStr);
            } else {
                dateToStr = format.format(dateTo);
            }
        } catch (ParseException e) {
        }
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue, status, dateFrom, dateTo);
        var transactionStatusOptions = WalletTransactionStatus.values();
        model.addAttribute("transactionStatusOptions", transactionStatusOptions);

        var transactionPage = transactionReadService.retrieveAll(searchParams);
        model.addAttribute("transactionPage", transactionPage);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("status", status);
        model.addAttribute("dateFrom", dateFromStr);
        model.addAttribute("dateTo", dateToStr);

        var totalItems = transactionPage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("totalPages", totalPages);
        return "transactions/list";
    }

    @GetMapping("/{id}")
    public String getWalletTransaction(Model model, @PathVariable("id") Long id) {
        var transactionData = transactionReadService.retrieveOne(id);

        model.addAttribute("transaction", transactionData);
        model.addAttribute("approveTransactionRequest", new ApproveTransactionRequestWsDTO());
        return "transactions/detail";
    }

    @PostMapping("/{id}/{type}/{action}")
    public String action(Model model, @PathVariable("id") Long id, @PathVariable("action") String action,
                         @PathVariable("type") String type, ApproveTransactionRequestWsDTO request) {

        if (is(action, "APPROVE")) {
            if (type.equals(TransactionType.DEPOSIT.getCode())) {
                this.transactionService.approveDeposit(id, request);
            } else if (type.equals(TransactionType.WITHDRAW.getCode())) {
                this.transactionService.approveWithdraw(id);
            }

        } else if (is(action,  "REJECT")) {
            this.transactionService.reject(id);
        }
        var transactionData = transactionReadService.retrieveOne(id);
        model.addAttribute("transaction", transactionData);
        return "redirect:/wallet-transactions/" + id;
    }

    public boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

}
