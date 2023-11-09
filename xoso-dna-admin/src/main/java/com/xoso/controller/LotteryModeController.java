package com.xoso.controller;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.lottery.model.ModeCode;
import com.xoso.lottery.model.TypeCode;
import com.xoso.lottery.service.LotteryModeService;
import com.xoso.lottery.wsdto.LotteryModeRequestWsDTO;
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
@RequestMapping("lotterymode")
public class LotteryModeController {

    private final LotteryModeService lotteryModeService;

    @Autowired
    public LotteryModeController(LotteryModeService lotteryModeService) {
        this.lotteryModeService = lotteryModeService;
    }

    @GetMapping("/search")
    public String searchLotteries(Model model,
                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                  @RequestParam(value = "searchValue", required = false) String searchValue,
                                  RedirectAttributes attr) {
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue);
        var lotteryModePage = this.lotteryModeService.retrieveAllLotteryMode(searchParams);
        model.addAttribute("lotteryModePage", lotteryModePage);
        var totalItems = lotteryModePage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("typeCodes", TypeCode.values());
        model.addAttribute("modeCodes", ModeCode.values());
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "lotterymode/list";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public LotteryModeRequestWsDTO getLotteryMode(@PathVariable("id") Long id) {
        var lotteryMode = this.lotteryModeService.retrieveOneLotteryMode(id);
        return LotteryModeRequestWsDTO.builder()
                .id(lotteryMode.getId())
                .code(lotteryMode.getCode())
                .name(lotteryMode.getName())
                        .price(lotteryMode.getPrice())
                        .prizeMoney(lotteryMode.getPrizeMoney()).build();
    }

    @GetMapping("/detail/{id}")
    public String getWalletTransaction(Model model, @PathVariable("id") Long id) {
        var lotteryMode = this.lotteryModeService.retrieveOneLotteryMode(id);

        model.addAttribute("lotteryMode", lotteryMode);
        return "lotterymode/detail";
    }

    @PostMapping()
    public String create(@Valid LotteryModeRequestWsDTO request) {
        ResultBuilder resultBuilder = null;
        if (request.getId() != null) {
            resultBuilder = this.lotteryModeService.updateLotteryMode(request.getId(), request);
        } else {
            resultBuilder = this.lotteryModeService.createLotteryMode(request);
        }
        return  "redirect:/lotterymode/detail/" + resultBuilder.getEntityId();
    }
}
