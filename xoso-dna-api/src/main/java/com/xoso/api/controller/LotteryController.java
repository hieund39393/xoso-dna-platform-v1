package com.xoso.api.controller;

import com.xoso.bank.data.BankData;
import com.xoso.lottery.data.LotteryHistoryData;
import com.xoso.lottery.data.LotteryTicketData;
import com.xoso.lottery.service.LotteryService;
import com.xoso.lottery.service.LotteryTicketService;
import com.xoso.lottery.wsdto.LotteryTicketWsDTO;
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
@RequestMapping("/lottery")
public class LotteryController extends BaseController {

    private final LotteryService lotteryService;
    private final LotteryTicketService lotteryTicketService;

    @Autowired
    public LotteryController(LotteryService lotteryService, LotteryTicketService lotteryTicketService) {
        this.lotteryService = lotteryService;
        this.lotteryTicketService = lotteryTicketService;
    }

    @GetMapping("category")
    public ResponseEntity<?> listAll() {
        return response(lotteryService.getAllCategories());
    }

    @GetMapping("hot")
    public ResponseEntity<?> getLotteryHot() {
        return response(lotteryService.getLotteryHot());
    }

    @GetMapping("{id}/detail")
    public ResponseEntity<?> getDetail(@PathVariable("id") Long id){
        return response(lotteryService.getLotteryDetail(id,0));
    }
    @GetMapping("{id}/detail/test1")
    public ResponseEntity<?> getDetailTest1(@PathVariable("id") Long id){
        return response(lotteryService.getLotteryDetail(id,1));
    }
    @GetMapping("{id}/detail/test2")
    public ResponseEntity<?> getDetailTes2(@PathVariable("id") Long id){
        return response(lotteryService.getLotteryDetail(id,2));
    }

    @PostMapping("{id}/purchase")
    public ResponseEntity<?> purchased(@PathVariable("id") Long id, @RequestBody @Valid LotteryTicketWsDTO tickets){
        return response(lotteryTicketService.buyTicket(tickets));
    }

    @GetMapping("{id}/purchased_tickets")
    public ResponseEntity<?> purchasedTickets(@PathVariable("id") Long id,@RequestParam(value = "sessionId") Long sessionId){
        return response(lotteryTicketService.getPurchasedTicketBySessionId(sessionId));
    }

    @GetMapping("{id}/history")
    public ResponseEntity<?> getHistory(@PathVariable("id") Long id,
                                        @RequestParam(value = "pageNumber",required = false, defaultValue = "1") @Min(value = 1,message = "pageNumber must be greater than 0") Integer pageNumber,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Max(value = 200L,message = "pageSize must be less than 200")  Integer pageSize,
                                        @RequestParam(value = "searchValue", required = false, defaultValue = "") String searchValue){
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        Page<LotteryHistoryData> res = lotteryService.getHistory(id, pageable);
        return response(res);
    }

    @GetMapping("/tickets_history")
    public ResponseEntity<?> getPurchasedTickets(
                                        @RequestParam(value = "pageNumber",required = false, defaultValue = "1") @Min(value = 1,message = "pageNumber must be greater than 0") Integer pageNumber,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Max(value = 200L,message = "pageSize must be less than 200")  Integer pageSize,
                                        @RequestParam(value = "lotteryId", required = false, defaultValue = "0") Long lotteryId,
                                        @RequestParam(value = "status", required = false, defaultValue = "2") Integer status){
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        Page<LotteryTicketData> res = lotteryTicketService.queryTicket(lotteryId,status, pageable);

        return response(res);
    }
}
