package com.xoso.controller;

import com.xoso.bank.service.BankService;
import com.xoso.bank.service.ClientBankAccountReadService;
import com.xoso.bank.service.ClientBankAccountService;
import com.xoso.bank.service.MasterBankAccountService;
import com.xoso.bank.wsdto.ClientBankAccountCreateWsDTO;
import com.xoso.bank.wsdto.MasterBankAccountCreateWsDTO;
import com.xoso.bank.wsdto.WalletClientUpdateWsDTO;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.user.service.UserReadService;
import com.xoso.wallet.data.WalletData;
import com.xoso.wallet.model.Wallet;
import com.xoso.wallet.model.WalletStatus;
import com.xoso.wallet.model.WalletTransactionStatus;
import com.xoso.wallet.repository.WalletRepository;
import com.xoso.wallet.service.WalletReadService;
import com.xoso.wallet.service.WalletService;
import com.xoso.wallet.service.WalletTransactionReadService;
import com.xoso.wallet.service.WalletTransactionService;
import com.xoso.wallet.wsdto.AdminDepositRequestWsDTO;
import com.xoso.wallet.wsdto.AdminWithdrawRequestWsDTO;
import com.xoso.wallet.wsdto.WalletCreateWsDTO;
import com.xoso.wallet.wsdto.WalletUpdateWsDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("wallets")
public class WalletController {

    private final WalletReadService walletReadService;
    private final WalletService walletService;
    private final WalletTransactionReadService transactionReadService;
    private final UserReadService userReadService;
    private final BankService bankService;
    private final MasterBankAccountService bankAccountService;
    private final ClientBankAccountService clientBankAccountService;
    private final ClientBankAccountReadService clientBankAccountReadService;
    private final WalletTransactionService walletTransactionService;

    @Autowired
    public WalletController(WalletReadService walletReadService, WalletService walletService,
                            WalletTransactionReadService transactionReadService,
                            UserReadService userReadService, BankService bankService,
                            MasterBankAccountService bankAccountService,
                            ClientBankAccountService clientBankAccountService, ClientBankAccountReadService clientBankAccountReadService,
                            WalletTransactionService walletTransactionService) {
        this.walletReadService = walletReadService;
        this.walletService = walletService;
        this.transactionReadService = transactionReadService;
        this.userReadService = userReadService;
        this.bankService = bankService;
        this.bankAccountService = bankAccountService;
        this.clientBankAccountService = clientBankAccountService;
        this.clientBankAccountReadService = clientBankAccountReadService;
        this.walletTransactionService = walletTransactionService;
    }

    @GetMapping("/normal/export")
    public void exportWallet(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Wallet");

        // Tạo dòng tiêu đề
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã KH");
        headerRow.createCell(1).setCellValue("Tài khoản");
        headerRow.createCell(2).setCellValue("Ngân hàng ");
        headerRow.createCell(3).setCellValue("Số TK ngân hàng");
        headerRow.createCell(4).setCellValue("Tên khách hàng");
        headerRow.createCell(5).setCellValue("SDT");
        headerRow.createCell(6).setCellValue("Ngày tạo ví");
        headerRow.createCell(7).setCellValue("Trạng thái");
        // ... thêm các cột khác cần xuất ...

        // Thêm dữ liệu vào Sheet
        int rowNum = 1;
        int page = 0;
        while(true){
            var searchParams = new SearchParameters(page, 50, null, new ArrayList<>());
            var walletPage = walletReadService.retrieveAll(searchParams, false);
            if(walletPage.getPageItems().isEmpty())
                break;
            for(WalletData data: walletPage.getPageItems()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getUserId());
                row.createCell(1).setCellValue(data.getUsername());
                row.createCell(2).setCellValue(data.getBankCode());
                row.createCell(3).setCellValue(data.getAccountNumber());
                row.createCell(4).setCellValue(data.getFullName());
                row.createCell(5).setCellValue(data.getMobileNo());
                row.createCell(6).setCellValue(data.getCreatedDate());
                row.createCell(7).setCellValue(data.getStatus());
            }
            page++;
        }

        // Thiết lập Header cho Response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Danh sach vi.xlsx");

        // Ghi dữ liệu vào Response OutputStream
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/normal/search")
    public String searchWallets(Model model,
                                @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                @RequestParam(value = "searchValue", required = false) String searchValue,
                                @RequestParam(value = "status", required = false) List<String> status,
                                RedirectAttributes attr) {

        attr.addFlashAttribute("isloading", true);
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue, status);
        var walletPage = walletReadService.retrieveAll(searchParams, false);
        model.addAttribute("walletPage", walletPage);

        var walletsStatusOptions = WalletStatus.values();
        model.addAttribute("walletsStatusOptions", walletsStatusOptions);

        var totalItems = walletPage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("status", status);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "wallets/list";
    }

    @GetMapping("/master/search")
    public String searchWalletMasters(Model model,
                                @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                @RequestParam(value = "searchValue", required = false) String searchValue,
                                @RequestParam(value = "oneServices", required = false) List<String> oneServices,
                                @RequestParam(value = "status", required = false) List<String> status,
                                RedirectAttributes attr) {

        attr.addFlashAttribute("isloading", true);
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue, status);
        var walletPage = walletReadService.retrieveAll(searchParams, true);
        model.addAttribute("walletPage", walletPage);

        var walletsStatusOptions = WalletStatus.values();
        model.addAttribute("walletsStatusOptions", walletsStatusOptions);

        var totalItems = walletPage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("status", status);
        model.addAttribute("oneServices", oneServices);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "wallets/list-master";
    }

    @GetMapping("/{walletId}")
    public String getWallet(Model model, @PathVariable("walletId") Long walletId) {

        var walletData = walletReadService.retrieveOne(walletId, true);
        model.addAttribute("walletData", walletData);

        var searchParamsSuccess = new SearchParameters(0, 10, null, List.of(WalletTransactionStatus.SUCCESS.name()));
        searchParamsSuccess.setWalletId(walletId);
        var transactionsSuccess = transactionReadService.retrieveAll(searchParamsSuccess);
        model.addAttribute("transactionsSuccess", transactionsSuccess);

        var searchParamsProcess = new SearchParameters(0, 10, null, List.of(WalletTransactionStatus.NEW.name()));
        searchParamsProcess.setWalletId(walletId);
        var transactionsProcess = transactionReadService.retrieveAll(searchParamsProcess);
        model.addAttribute("transactionsProcess", transactionsProcess);

        model.addAttribute("depositRequest", new AdminDepositRequestWsDTO());
        model.addAttribute("withdrawRequest", new AdminWithdrawRequestWsDTO());

        return "wallets/detail";
    }

    @GetMapping("/master/{walletId}")
    public String getWalletMaster(Model model, @PathVariable("walletId") Long walletId) {

        var walletData = walletReadService.retrieveOne(walletId, true);
        model.addAttribute("walletData", walletData);

        var searchParamsSuccess = new SearchParameters(0, 10, null, List.of(WalletTransactionStatus.SUCCESS.name()));
        searchParamsSuccess.setWalletId(walletId);
        var transactionsSuccess = transactionReadService.retrieveAll(searchParamsSuccess);
        model.addAttribute("transactionsSuccess", transactionsSuccess);

        var searchParamsProcess = new SearchParameters(0, 10, null, List.of(WalletTransactionStatus.NEW.name()));
        searchParamsProcess.setWalletId(walletId);
        var transactionsProcess = transactionReadService.retrieveAll(searchParamsProcess);
        model.addAttribute("transactionsProcess", transactionsProcess);
        return "wallets/detail-master";
    }

    @PostMapping("/{walletId}/lock")
    public String lock(Model model, @PathVariable("walletId") Long walletId) {
        this.walletService.lock(walletId);
        return "redirect:/wallets/" + walletId;
    }
    @PostMapping("/{walletId}/unlock")
    public String unlock(Model model, @PathVariable("walletId") Long walletId) {
        this.walletService.unlock(walletId);
        return "redirect:/wallets/" + walletId;
    }

    @GetMapping("/master/add")
    public String initAddWallet(Model model) {
        model.addAttribute("wallet", new WalletCreateWsDTO());
        model.addAttribute("users", this.userReadService.retrieveUserSystems());
        return "wallets/add-master";
    }

    @GetMapping("/master/{walletId}/edit")
    public String initEditWallet(Model model, @PathVariable("walletId") Long walletId) {

        var walletData = walletReadService.retrieveOne(walletId, true);
        var pageable = PageRequest.of(0, 1000);
        var bankDataPage = bankService.getListBank(null, pageable);
        var masterBankAccount = new MasterBankAccountCreateWsDTO(walletId);
        model.addAttribute("banks", bankDataPage.get().collect(Collectors.toList()));
        model.addAttribute("masterBankAccount", masterBankAccount);
        model.addAttribute("wallet", walletData);
        model.addAttribute("users", this.userReadService.retrieveUserSystems());
        return "wallets/edit-master";
    }

    @GetMapping("/client/{walletId}/edit")
    public String initEditWalletUser(Model model, @PathVariable("walletId") Long walletId) {
        var walletData = clientBankAccountReadService.retrieveByWallet(walletId);
        walletData.setId(walletId);
        var pageable = PageRequest.of(0, 1000);
        var bankDataPage = bankService.getListBank(null, pageable);
        var clientBankAccount = new ClientBankAccountCreateWsDTO(walletId);
        model.addAttribute("banks", bankDataPage.get().collect(Collectors.toList()));
        model.addAttribute("wallet", walletData);
        model.addAttribute("balance", walletData.getBalance());
        model.addAttribute("clientBankAccount", clientBankAccount);
        return "wallets/edit";
    }

    @PostMapping("/master/add")
    public String addWallet(Model model, @Valid WalletCreateWsDTO wallet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("wallet", new WalletCreateWsDTO());
            model.addAttribute("users", this.userReadService.retrieveUserSystems());
            model.addAttribute("errors", bindingResult.getFieldErrors("balance"));
            return "wallets/add-master";
        }
        wallet.setMaster(true);
        var result = this.walletService.create(wallet);
        // attributes.addFlashAttribute("message", "User registered successfully!");
        return "redirect:/wallets/master/" + result.getEntityId();
    }

    @PostMapping("/master/{walletId}/edit")
    public String editWallet(Model model, @PathVariable("walletId") Long walletId, @Valid WalletUpdateWsDTO wallet, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            var walletData = walletReadService.retrieveOne(walletId, true);
            var pageable = PageRequest.of(0, 1000);
            var bankDataPage = bankService.getListBank(null, pageable);
            var masterBankAccount = new MasterBankAccountCreateWsDTO(walletId);
            model.addAttribute("banks", bankDataPage.get().collect(Collectors.toList()));
            model.addAttribute("masterBankAccount", masterBankAccount);
            model.addAttribute("wallet", walletData);
            model.addAttribute("users", this.userReadService.retrieveUserSystems());
            model.addAttribute("errors", bindingResult.getFieldErrors("balance"));
            return "wallets/edit-master";
        }

        this.walletService.updateWalletMaster(walletId, wallet);
        return "redirect:/wallets/master/" + walletId;
    }

    @PostMapping("/master/{walletId}/bank-account/add")
    public String addBankAccount(Model model, @PathVariable("walletId") Long walletId, @Valid MasterBankAccountCreateWsDTO request) {
        this.bankAccountService.create(request);
        return "redirect:/wallets/master/" + walletId + "/edit";
    }

    @PostMapping("/master/{walletId}/bank-account/{bankAccountId}")
    public String deleteBankAccount(Model model, @PathVariable("walletId") Long walletId, @PathVariable("bankAccountId") Long bankAccountId) {
        this.bankAccountService.delete(bankAccountId);
        return "redirect:/wallets/master/" + walletId + "/edit";
    }

    @PostMapping("/client/{walletId}/bank-account/add")
    public String addClientBankAccount(Model model, @PathVariable("walletId") Long walletId, @Valid ClientBankAccountCreateWsDTO request) {
        this.clientBankAccountService.createForWallet(walletId, request);
        return "redirect:/wallets/client/" + walletId + "/edit";
    }

    @PostMapping("/client/{walletId}/bank-account/update")
    public String updateClientBankAccount(Model model, @PathVariable("walletId") Long walletId, @Valid WalletClientUpdateWsDTO request) {
        this.clientBankAccountService.updateForWallet(request);
        return "redirect:/wallets/client/" + walletId + "/edit";
    }

    @PostMapping("/{walletId}/transactions/deposit")
    public String deposit(Model model, @PathVariable("walletId") Long walletId, @Valid AdminDepositRequestWsDTO request) {
        this.walletTransactionService.depositFromAdmin(walletId, request);
        return "redirect:/wallets/" + walletId;
    }

    @PostMapping("/{walletId}/transactions/withdraw")
    public String withdraw(Model model, @PathVariable("walletId") Long walletId, @Valid AdminWithdrawRequestWsDTO request) {
        this.walletTransactionService.withdrawFromAdmin(walletId, request);
        return "redirect:/wallets/" + walletId;
    }
}
