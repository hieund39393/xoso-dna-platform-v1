package com.xoso.controller;

import com.xoso.agency.model.AgencyStatus;
import com.xoso.agency.service.AgencyService;
import com.xoso.agency.wsdto.AgencyRequestRejectWsDTO;
import com.xoso.agency.wsdto.AgencyRequestStatusOptions;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.wallet.model.WalletStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("agencies")
public class AgencyController {

    @Autowired
    private AgencyService agencyService;

    @GetMapping("/request/search")
    public String searchUsers(Model model,
                              @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                              @RequestParam(value = "searchValue", required = false) String searchValue,
                              @RequestParam(value = "status", required = false) Integer statusInt) {

        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue);
        if (statusInt != null && statusInt != 0) {
            searchParams.setStatusInt(statusInt);
        }
        var agencyRequestPage = agencyService.retrieveAllAgencyRequest(searchParams);
        model.addAttribute("agencyRequestPage", agencyRequestPage);

        var statusOptions = new ArrayList<AgencyRequestStatusOptions>() {{
            add(new AgencyRequestStatusOptions("PROCESSING", AgencyStatus.PROCESSING));
            add(new AgencyRequestStatusOptions("APPROVED", AgencyStatus.APPROVED));
            add(new AgencyRequestStatusOptions("REJECTED", AgencyStatus.REJECTED));
        }};
        model.addAttribute("statusOptions", statusOptions);

        var totalItems = agencyRequestPage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("status", statusInt);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "agency-request/list";
    }

    @GetMapping("/search")
    public String searchUsers(Model model,
                              @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                              @RequestParam(value = "searchValue", required = false) String searchValue) {

        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue);
        searchParams.setStatusInt(2);
        var agencyRequestPage = agencyService.retrieveAllAgencyRequest(searchParams);
        model.addAttribute("agencyRequestPage", agencyRequestPage);

        var totalItems = agencyRequestPage.getTotalFilteredRecords();
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
        return "agency/list";
    }

    @GetMapping("/request/{id}")
    public String getUser(Model model, @PathVariable("id") Long id) {
        var agencyRequest = this.agencyService.retrieveOneAgencyRequest(id);

        model.addAttribute("agencyRequest", agencyRequest);
        model.addAttribute("agencyRequestReject", new AgencyRequestRejectWsDTO());
        return "agency-request/detail";
    }

    @PostMapping("/request/{id}/approve")
    public String approve(Model model, @PathVariable("id") Long id) {
        this.agencyService.approve(id);
        return "redirect:/agencies/request/" + id;
    }

    @PostMapping("/request/{id}/reject")
    public String reject(Model model, @PathVariable("id") Long id, @Valid AgencyRequestRejectWsDTO request) {
        this.agencyService.reject(id, request.getReason());
        return "redirect:/agencies/request/" + id;
    }
}
