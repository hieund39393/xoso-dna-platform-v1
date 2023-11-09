package com.xoso.controller;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;;
import com.xoso.lottery.service.LotteryVideoService;
import com.xoso.lottery.wsdto.LotteryVideoRequestWsDTO;
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
@RequestMapping("lotteryvideo")
public class LotteryVideoController {

    private final LotteryVideoService lotteryVideoService;

    @Autowired
    public LotteryVideoController(LotteryVideoService lotteryVideoService) {
        this.lotteryVideoService = lotteryVideoService;
    }


    @GetMapping("/search")
    public String searchLotteries(Model model,
                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                  @RequestParam(value = "searchValue", required = false) String searchValue,
                                  RedirectAttributes attr) {
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue);
        var lotteryVideoPage = this.lotteryVideoService.retrieveAllLotteryVideo(searchParams);
        model.addAttribute("lotteryVideoPage", lotteryVideoPage);
        var totalItems = lotteryVideoPage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "lotteryvideo/list";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public LotteryVideoRequestWsDTO getLotteryVideo(@PathVariable("id") Long id) {
        var lotteryVideo = this.lotteryVideoService.retrieveOne(id);
        return LotteryVideoRequestWsDTO.builder()
                        .id(lotteryVideo.getId())
                        .group(lotteryVideo.getGroup())
                        .index(lotteryVideo.getIndex())
                        .number(lotteryVideo.getNumber())
                        .duration(lotteryVideo.getDuration())
                        .url(lotteryVideo.getUrl())
                .build();
    }

    @GetMapping("/detail/{id}")
    public String getWalletTransaction(Model model, @PathVariable("id") Long id) {
        var lotteryVideo = this.lotteryVideoService.retrieveOne(id);

        model.addAttribute("lotteryVideo", lotteryVideo);
        return "lotteryvideo/detail";
    }

    @PostMapping()
    public String createOrUpdate(@Valid LotteryVideoRequestWsDTO request) {
        ResultBuilder resultBuilder = null;
        if (request.getId() != null) {
            resultBuilder = this.lotteryVideoService.updateLotteryVideo(request.getId(), request);
        } else {
            resultBuilder = this.lotteryVideoService.createLotteryVideo(request);
        }
        return  "redirect:/lotteryvideo/detail/" + resultBuilder.getEntityId();
    }

    @GetMapping("/delete")
    public String delete(Long id) {
        this.lotteryVideoService.deleteLotteryVideo(id);
        return "redirect:/lotteryvideo/search";
    }
}
