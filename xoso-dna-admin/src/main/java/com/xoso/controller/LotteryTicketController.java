package com.xoso.controller;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.lottery.model.ModeCode;
import com.xoso.lottery.model.TypeCode;
import com.xoso.lottery.service.LotteryService;
import com.xoso.lottery.service.LotteryTicketService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Controller
@RequestMapping("lottery-ticket")
public class LotteryTicketController {

    @Autowired
    private LotteryTicketService lotteryTicketService;
    @Autowired
    private LotteryService lotteryService;

    @GetMapping("/search")
    public String searchLotteryTickets(Model model,
                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                  @RequestParam(value = "searchValue", required = false) String searchValue,
                                       @RequestParam(value = "lotteryCode", required = false) String lotteryCode,
                                       @RequestParam(value = "modeCode", required = false) String modeCode,
                                       @RequestParam(value = "dateFrom", required = false) String dateFromStr,
                                       @RequestParam(value = "dateTo", required = false) String dateToStr,
                                  RedirectAttributes attr) {
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
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue);
        if (StringUtils.isNotBlank(modeCode)) {
            searchParams.setModeCodes(Stream.of(modeCode).collect(Collectors.toList()));
        }
        searchParams.setFromDate(dateFrom);
        searchParams.setToDate(dateTo);
        var lotteryTicketPage = this.lotteryTicketService.retrieveAllLotteryTicket(searchParams);
        model.addAttribute("lotteryTicketPage", lotteryTicketPage);
        var totalItems = lotteryTicketPage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("typeCodes", TypeCode.values());
        model.addAttribute("modeCodes", ModeCode.values());
        model.addAttribute("lotteryCode", lotteryCode);
        model.addAttribute("modeCode", modeCode);
        model.addAttribute("dateFrom", dateFromStr);
        model.addAttribute("dateTo", dateToStr);

        var lotteries = this.lotteryService.retrieveAllLottery(new SearchParameters());
        model.addAttribute("lotteries", lotteries);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "lottery-ticket/list";
    }
}
