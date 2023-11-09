package com.xoso.controller;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.language.data.TranslatedStringData;
import com.xoso.language.service.TranslatedStringService;
import com.xoso.language.wsdto.TranslatedRequestWsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("translations")
public class TranslationController {
    private final TranslatedStringService translatedStringService;

    @Autowired
    public TranslationController(TranslatedStringService translatedStringService) {
        this.translatedStringService = translatedStringService;
    }

    @GetMapping("/search")
    public String search(Model model,
                                      @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                      @RequestParam(value = "searchValue", required = false) String searchValue,
                                      RedirectAttributes attr) {
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue);
        var translatedStringDataPage = this.translatedStringService.retrieveAll(searchParams);
        model.addAttribute("translatedStringDataPage", translatedStringDataPage);
        var totalItems = translatedStringDataPage.getTotalFilteredRecords();
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
        return "translations/list";
    }

    @GetMapping("/detail/{id}")
    public String retrieveOne(Model model, @PathVariable("id") Long id) {
        var translatedStringData = this.translatedStringService.retrieveOne(id);

        model.addAttribute("translatedStringData", translatedStringData);
        return "translations/detail";
    }

    @GetMapping("/create")
    public String createTemp(Model model) {
        model.addAttribute("translatedRequest", new TranslatedRequestWsDTO());
        return "translations/create";
    }

    @PostMapping("/create")
    public String create(TranslatedRequestWsDTO translatedRequest) {
        var translatedStringData = this.translatedStringService.create(translatedRequest);
        return "redirect:/translations/detail/" + translatedStringData.getId();
    }

    @GetMapping("/{id}")
    public String editTemp(Model model, @PathVariable("id") Long id) {
        var translatedStringData = this.translatedStringService.retrieveOne(id);
        model.addAttribute("translatedRequest", translatedStringData);
        return "translations/edit";
    }

    @PostMapping(value = "/{id}", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String update(Model model, @PathVariable("id") Long id, TranslatedStringData translatedRequest) {
        var translatedStringData = this.translatedStringService.update(id, TranslatedRequestWsDTO.builder()
                .code(translatedRequest.getCode())
                .viet(translatedRequest.getViet())
                        .thai(translatedRequest.getThai())
                        .cam(translatedRequest.getCam())
                        .lao(translatedRequest.getLao())
                .build()
        );
        return "redirect:/translations/detail/" + translatedStringData.getId();
    }
}
