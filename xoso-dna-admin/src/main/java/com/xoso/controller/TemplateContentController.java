package com.xoso.controller;

import com.xoso.campaign.model.CategoryEnum;
import com.xoso.campaign.service.TemplateContentService;
import com.xoso.campaign.wsdto.TemplateContentRequestWsDTO;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.lottery.model.LanguageEnum;
import com.xoso.media.repository.FileInfoRepository;
import com.xoso.media.service.FilesStorageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("template-content")
public class TemplateContentController {

    final Logger logger = LoggerFactory.getLogger(TemplateContentController.class);

    @Autowired
    private TemplateContentService templateContentService;

    @Autowired
    private FilesStorageService filesStorageService;
    @Autowired
    private FileInfoRepository fileInfoRepository;

    @GetMapping("/search")
    public String search(Model model,
                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                  @RequestParam(value = "searchValue", required = false) String searchValue,
                                  RedirectAttributes attr) {
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue);
        var templateContentPage = this.templateContentService.retrieveAll(searchParams);
        model.addAttribute("templateContentPage", templateContentPage);
        var totalItems = templateContentPage.getTotalFilteredRecords();
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
        return "editor/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("languageOptions", LanguageEnum.values());
        model.addAttribute("categoryOptions", CategoryEnum.values());
        model.addAttribute("templateContent", new TemplateContentRequestWsDTO());
        return "editor/add";
    }

    @PostMapping("/add")
    public String addNote(@Valid TemplateContentRequestWsDTO requestItem, @RequestParam("file") MultipartFile file) {
        var fileName = file.getOriginalFilename();
        if (StringUtils.isNotBlank(fileName)) {
            var existFile = this.fileInfoRepository.findByFileName(fileName);
            if (existFile == null) {
                this.filesStorageService.save(file);
            }
            requestItem.setBanner(fileName);
        }
        if (requestItem.getId() != null) {
            this.templateContentService.updateTemplateContent(requestItem.getId(), requestItem);
        } else {
            this.templateContentService.createTemplateContent(requestItem);
        }
        return "redirect:/template-content/search";
    }

    @GetMapping("/{id}")
    public String editTemp(Model model, @PathVariable("id") Long id) {
        var templateContent = this.templateContentService.retrieveOne(id);
        if (StringUtils.isNotBlank(templateContent.getBanner())) {
            var bannerUrl = MvcUriComponentsBuilder
                    .fromMethodName(FileController.class, "getFile",templateContent.getBanner()).build().toString();
            templateContent.setBannerUrl(bannerUrl);
        }

        model.addAttribute("templateContent", templateContent);
        model.addAttribute("languageOptions", LanguageEnum.values());
        model.addAttribute("categoryOptions", CategoryEnum.values());
        return "editor/edit";
    }
    @GetMapping("/delete")
    public String delete(Long id) {
        this.templateContentService.deleteContent(id);
        return "redirect:/template-content/search";
    }
}
