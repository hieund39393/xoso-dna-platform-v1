package com.xoso.api.controller;

import com.xoso.bank.data.BankData;
import com.xoso.bank.service.BankService;
import com.xoso.bank.service.ClientBankAccountReadService;
import com.xoso.bank.service.ClientBankAccountService;
import com.xoso.bank.wsdto.ClientBankAccountCreateWsDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/banks")
public class BankController extends BaseController {

    @Autowired
    private BankService bankService;
    @Autowired
    private ClientBankAccountReadService clientBankAccountReadService;
    @Autowired
    private ClientBankAccountService clientBankAccountService;

    @GetMapping()
    @Operation(summary = "List all bank")
    public ResponseEntity<?> getBanks(@RequestParam(value = "pageNumber",required = false, defaultValue = "1") @Min(value = 1,message = "pageNumber must be greater than 0") Integer pageNumber,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Max(value = 200L,message = "pageSize must be less than 200")  Integer pageSize,
                                      @RequestParam(value = "searchValue", required = false, defaultValue = "") String searchValue){
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        Page<BankData> res = bankService.getListBank(searchValue, pageable);
        return response(res);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detail of bank by ID")
    public ResponseEntity<?> getBank(@PathVariable long id){
        return response(bankService.getBankDetail(id));
    }

    @GetMapping("/accounts")
    @Operation(summary = "Get all bank accounts")
    public ResponseEntity<?> getBankAccount(){
        return response(this.clientBankAccountReadService.retrieveByUser());
    }

    @PostMapping("/accounts")
    @Operation(summary = "Create bank accounts")
    public ResponseEntity<?> createBankAccount(@RequestBody @Valid ClientBankAccountCreateWsDTO request){
        return response(this.clientBankAccountService.create(request));
    }
}
