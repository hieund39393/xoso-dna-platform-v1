package com.xoso.controller;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.job.LotteryJob;
import com.xoso.lottery.model.ModeCode;
import com.xoso.lottery.model.TypeCode;
import com.xoso.lottery.service.LotteryCategoryService;
import com.xoso.lottery.service.LotteryService;
import com.xoso.lottery.wsdto.LotteryCategoryRequestWsDTO;
import com.xoso.lottery.wsdto.LotteryRequestWsDTO;
import com.xoso.lottery.wsdto.LotterySetupResultWsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("lotteries")
public class LotteryController {

    final Logger logger = LoggerFactory.getLogger(LotteryController.class);

    private final LotteryService lotteryService;
    private final LotteryJob lotteryJob;
    private final LotteryCategoryService lotteryCategoryService;

    @Autowired
    public LotteryController(LotteryService lotteryService, LotteryCategoryService lotteryCategoryService, LotteryJob lotteryJob) {
        this.lotteryService = lotteryService;
        this.lotteryCategoryService = lotteryCategoryService;
        this.lotteryJob = lotteryJob;
    }

    @GetMapping("/search")
    public String searchLotteries(Model model,
                                      @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                      @RequestParam(value = "searchValue", required = false) String searchValue,
                                      RedirectAttributes attr) {
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue);
        var lotteryPage = this.lotteryService.retrieveAllLottery(searchParams);
        model.addAttribute("lotteryPage", lotteryPage);
        var totalItems = lotteryPage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("typeCodes", TypeCode.values());
        model.addAttribute("modeCodes", ModeCode.values());
        model.addAttribute("lotteryCategoryRequest", new LotteryCategoryRequestWsDTO());
        var categoryPage = this.lotteryCategoryService.retrieveAllLotteryCategory(new SearchParameters());
        model.addAttribute("categories", categoryPage.getPageItems());
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "lottery/list";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public LotteryRequestWsDTO getLottery(@PathVariable("id") Long id) {
        var lottery = this.lotteryService.retrieveOneLottery(id);
        return LotteryRequestWsDTO.builder()
                .id(lottery.getId())
                .code(lottery.getCode())
                .active(lottery.isActive())
                .vip(lottery.isVip())
                .name(lottery.getName())
                .lotteryCategoryId(lottery.getCategoryId())
                .hour(lottery.getHour())
                .min(lottery.getMin())
                .sec(lottery.getSec())
                .modes(lottery.getModes())
                .type(lottery.getType()).build();
    }

    @GetMapping("/detail/{id}")
    public String getWalletTransaction(Model model, @PathVariable("id") Long id) {
        var lottery = this.lotteryService.retrieveOneLottery(id);

        model.addAttribute("lottery", lottery);
        return "lottery/detail";
    }

    @PostMapping()
    public String create(@Valid LotteryRequestWsDTO request) {
        ResultBuilder resultBuilder = null;
        if (request.getId() != null) {
            resultBuilder = this.lotteryService.updateLottery(request.getId(), request);
        } else {
            resultBuilder = this.lotteryService.createLottery(request);
        }
        return  "redirect:/lotteries/detail/" + resultBuilder.getEntityId();
    }

    @PostMapping("/setupResult")
    public String setupResult(@Valid LotterySetupResultWsDTO request) {
        logger.info("Result = {}", request.getResult());
        lotteryService.setupResult(request.getId(), request.getResult());
        return  "redirect:/lotteries/detail/" + request.getId();
    }
}
