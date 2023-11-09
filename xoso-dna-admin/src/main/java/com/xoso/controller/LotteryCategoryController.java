package com.xoso.controller;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.lottery.service.LotteryCategoryService;
import com.xoso.lottery.wsdto.LotteryCategoryRequestWsDTO;
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
@RequestMapping("lottery-categories")
public class LotteryCategoryController {

    private final LotteryCategoryService lotteryCategoryService;

    @Autowired
    public LotteryCategoryController(LotteryCategoryService lotteryCategoryService) {
        this.lotteryCategoryService = lotteryCategoryService;
    }

    @PostMapping()
    public String create(@Valid LotteryCategoryRequestWsDTO request) {
        ResultBuilder resultBuilder = null;
        if (request.getId() != null) {
           this.lotteryCategoryService.updateLotteryCategory(request.getId(), request);
        } else {
           this.lotteryCategoryService.createLotteryCategory(request);
        }
        return  "redirect:/lottery-categories/search";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public LotteryCategoryRequestWsDTO getLotteryCategory(@PathVariable("id") Long id) {
        var lotteryCategory = this.lotteryCategoryService.retrieveOneLotteryCategory(id);
        return LotteryCategoryRequestWsDTO.builder()
                .id(lotteryCategory.getId())
                .code(lotteryCategory.getCode())
                .active(lotteryCategory.isActive())
                .campaign(lotteryCategory.isCampaign())
                .name(lotteryCategory.getName()).build();
    }

    @GetMapping("/search")
    public String searchWalletMasters(Model model,
                                      @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                      @RequestParam(value = "searchValue", required = false) String searchValue,
                                      RedirectAttributes attr) {
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue);
        var lotteryCategoryPage = this.lotteryCategoryService.retrieveAllLotteryCategory(searchParams);
        model.addAttribute("lotteryCategoryPage", lotteryCategoryPage);
        var totalItems = lotteryCategoryPage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("lotteryCategoryRequest", new LotteryCategoryRequestWsDTO());
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "lottery-categories/list";
    }
}
